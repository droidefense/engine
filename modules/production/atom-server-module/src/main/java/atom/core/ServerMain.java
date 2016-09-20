package atom.core;

import atom.core.handlers.HelloHandler;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class ServerMain {

    public static final int SERVER_PORT = 8080;
    private static final String FAKE_BANNER = "Apache/2.0.55 (Debian)";

    public static void main(String[] args) {
        try {
            runServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runServer() throws Exception {
        Server server = new Server(SERVER_PORT);

        //set handlers

        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new HelloHandler("Root Hello"));

        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjoir"));

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new HelloHandler("Bongiorno"));


        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextFR, contextIT});

        server.setHandler(contexts);

        //run the server
        server.start();

        //config
        HttpGenerator.setJettyVersion(FAKE_BANNER);

        server.dumpStdErr();
        server.join();
    }
}
