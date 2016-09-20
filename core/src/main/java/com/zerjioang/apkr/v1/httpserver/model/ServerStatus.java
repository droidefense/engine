package com.zerjioang.apkr.v1.httpserver.model;

import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

/**
 * Created by .local on 11/05/2016.
 */
public class ServerStatus {

    private final String servername;
    private final String version;

    public ServerStatus() {
        this.servername = "Atom development server";
        this.version = ApkrConstants.ENGINE_VERSION;
    }

    public String getServername() {
        return servername;
    }
}
