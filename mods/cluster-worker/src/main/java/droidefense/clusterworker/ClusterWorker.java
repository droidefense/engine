package droidefense.clusterworker;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import io.nats.client.*;

import java.io.IOException;

public class ClusterWorker {

    private static final String URL = "localhost:4222";

    private Connection c;
    private String server;

    public ClusterWorker(String url) throws IOException {
        if (url == null) {
            this.server = URL;
        } else {
            this.server = url;
        }
        this.c = this.initConnection();
    }

    private Connection initConnection() throws IOException {
        Options options = new Options.Builder()
                .errorCb(this::connectionError)
                .disconnectedCb(this::disconnected)
                .reconnectedCb(this::reconnected)
                .build();

        return Nats.connect(this.server, options);
    }

    private void reconnected(ConnectionEvent event) {
        Log.write(LoggerType.ERROR, "Reconnected to server: {}", event.getConnection());
    }

    private void disconnected(ConnectionEvent event) {
        Log.write(LoggerType.ERROR, "Channel disconnected: {}", event.getConnection());
    }

    private void connectionError(NATSException ex) {
        Log.write(LoggerType.ERROR, "Connection Exception: ", ex);
    }
}