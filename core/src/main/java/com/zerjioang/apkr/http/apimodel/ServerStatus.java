package com.zerjioang.apkr.http.apimodel;


import com.zerjioang.apkr.sdk.helpers.ApkrConstants;

/**
 * Created by .local on 11/05/2016.
 */
public class ServerStatus {

    private final String servername;
    private final String version;

    public ServerStatus() {
        this.servername = "apkr server";
        this.version = ApkrConstants.ENGINE_VERSION;
    }

    public String getServername() {
        return servername;
    }
}
