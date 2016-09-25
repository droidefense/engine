package com.zerjioang.apkr.v1.httpserver;

import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by sanguita on 01/02/2016.
 */

public final class ApkrServer {

    public static final int PORT = 80;

    public static void main(String[] args) {

        Log.write(LoggerType.INFO, "Starting apkr http server...");

        ThreadPooledServer server = new ThreadPooledServer(PORT);
        (new Thread(server)).start();

        while (server.keepServerRunning()) {
            try {
                Thread.sleep(server.getDefaultLifeTime());
            } catch (InterruptedException exc) {
                StringWriter sw = new StringWriter();
                exc.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Log.write(LoggerType.FATAL, "Fatal error: ", exc.getLocalizedMessage(), exceptionAsString);
            }
        }
        server.stop();
        Log.write(LoggerType.INFO, "apkr server stop");
    }
}
