package com.zerjioang.apkr.v2.plugins.collection.sttc;

import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrStaticPlugin;

/**
 * Created by sergio on 7/3/16.
 */
public class GooglePlayCheckerPlugin extends AbstractApkrStaticPlugin {

    private static final String BASE_URL = "https://play.google.com/store/apps/details?id=";
    private static final String NOT_FOUND_MSG = "We're sorry, the requested URL was not found on this server.";
    private String url;
    private String packageName;

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void onPreExecute() {
        packageName = currentProject.getManifestInfo().getPackageName();
        if (packageName != null) {
            url = BASE_URL + packageName;
        }
    }

    private String getGooglePlayData(String url) {
        if (url != null && url.length() > 0)
            return FileIOHandler.getWebsiteContent(url);
        return null;
    }

    @Override
    public void onExecute() {
        //TODO parse retrieved web data and get stuff
        String webdata = getGooglePlayData(url);
        positiveMatch = webdata != null && !webdata.contains(NOT_FOUND_MSG);
    }

    @Override
    protected void postExecute() {
        if (positiveMatch) {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-green\"><i id=\"plugin-icon\"class=\"fa fa-shopping-cart\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">This application is available at <b>Google Play Store</b> at <a target=\"_blank\" href=\"" + this.url + "\">" + this.url + "</a></span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        } else {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-red\"><i id=\"plugin-icon\"class=\"fa fa-shopping-cart\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">This applications is no longer available at <b>Google Play Store</b></span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        }
    }

    @Override
    protected String getPluginName() {
        return "Google Play information checker";
    }

    @Override
    protected String getResultAsJson() {
        return null;
    }

    @Override
    protected String getResultAsHTML() {
        return html;
    }
}
