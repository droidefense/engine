package com.zerjioang.apkr.http;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.temp.ApkrScanner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadPooledServer implements Runnable {

    private static final int MAX_PORT = 65535;
    private static final int POOL_SIZE = 20;
    private static final int DEFAULT_PORT = 8080;
    private static final int MINUTES = 1;
    private final long DEFAULT_LIFETIME = (long) (MINUTES * 60 * 1000);

    private int serverPort;

    private volatile ServerSocket serverSocket;
    private volatile boolean isStopped;
    private volatile Thread runningThread;
    private volatile ExecutorService threadPool;
    private volatile long lastRequestUnixTime;

    public ThreadPooledServer(int port) {
        this.serverSocket = null;
        this.isStopped = false;
        this.runningThread = null;
        this.threadPool = Executors.newFixedThreadPool(POOL_SIZE);
        this.lastRequestUnixTime = System.currentTimeMillis();
        if (port >= 0 && port <= MAX_PORT) {
            this.serverPort = port;
            Log.write(LoggerType.INFO, "Server running at custom port: ", port);
        } else {
            this.serverPort = DEFAULT_PORT;
            Log.write(LoggerType.DEBUG, "Server running at default port: ", DEFAULT_PORT);
        }

    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        Log.write(LoggerType.INFO, "Loading Atom environment variables...");
        new ApkrScanner(ApkrScanner.LOAD_VARIABLES);

        Log.write(LoggerType.INFO, "OPEN apkr http server socket...");
        this.openServerSocket();
        Log.write(LoggerType.INFO, "Server socket OPEN and LISTENING");

        Socket clientSocket;

        try {
            for (; !this.isStopped(); this.threadPool.execute(new ThreadWorker(clientSocket))) {
                try {
                    clientSocket = this.serverSocket.accept();
                    if (clientSocket != null) {
                        this.lastRequestUnixTime = System.currentTimeMillis();
                    } else {
                        Log.write(LoggerType.ERROR, "Client socket become NULL");
                    }
                } catch (IOException var4) {
                    Log.write(LoggerType.FATAL, var4);
                    if (this.isStopped()) {
                        System.out.println("Server Stopped.");
                        break;
                    }
                    Log.write(LoggerType.FATAL, "Error accepting client connection", var4);
                    throw new RuntimeException("Error accepting client connection", var4);
                }
            }
        } catch (SocketException e) {
            Log.write(LoggerType.FATAL, "Error reading client socket ", e.getLocalizedMessage());
            e.printStackTrace();
        }

        this.threadPool.shutdown();
        Log.write(LoggerType.INFO, "Server Stopped.");
    }

    private boolean isStopped() {
        return this.isStopped;
    }

    public void stop() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
                this.isStopped = true;
                Log.write(LoggerType.INFO, "Serversocket closed");
            }
            Log.write(LoggerType.ERROR, "Serversocket was NULL and could not be closed");
        } catch (IOException var2) {
            this.isStopped = false;
            RuntimeException error = new RuntimeException("Error closing apkr http server", var2);
            Log.write(LoggerType.FATAL, "Fatal exception on Atom http server", error);
            throw error;
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException var2) {
            RuntimeException error = new RuntimeException("Cannot open port " + this.serverPort, var2);
            Log.write(LoggerType.FATAL, "Fatal exception on Atom http server", error, "caused by:", var2);
            throw error;
        }
    }

    public boolean keepServerRunning() {
        return System.currentTimeMillis() - this.lastRequestUnixTime < this.DEFAULT_LIFETIME;
    }

    public long getDefaultLifeTime() {
        return this.DEFAULT_LIFETIME;
    }
}
