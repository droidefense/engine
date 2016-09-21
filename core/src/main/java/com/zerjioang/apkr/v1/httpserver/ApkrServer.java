package com.zerjioang.apkr.v1.httpserver;

import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by sanguita on 01/02/2016.
 */

public class ApkrServer {

    private static final int DEV_PORT = 1234;
    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;
    private static final int HTTP_PROXY_PORT = 8080;
    private static final int VNC_PORT = 5900;

    public static final int PORT = DEV_PORT;

    public static void main(String[] args) {

        Log.write(LoggerType.INFO, "Starting apkr http server...");

        ThreadPooledServer server = new ThreadPooledServer(PORT);
        (new Thread(server)).start();

        while (server.keepServerRunning()) {
            try {
                Thread.sleep(server.getDefaultLifeTime());
            } catch (InterruptedException exc){
                StringWriter sw = new StringWriter();
                exc.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Log.write(LoggerType.FATAL, "Fatal error: ", exc.getLocalizedMessage(), exceptionAsString);
            }
        }
        server.stop();
        Log.write(LoggerType.INFO, "apkr server stop!!");
    }
}
