package droidefense.social;

import droidefense.handler.FileIOHandler;
import droidefense.sdk.model.base.DroidefenseProject;

/**
 * Created by sergio on 7/3/16.
 */
public class GooglePlayChecker {

    private static final String BASE_URL = "https://play.google.com/store/apps/details?id=";
    private static final String NOT_FOUND_ERROR = "404";
    private final DroidefenseProject currentProject;
    private String url;
    private String packageName;
    private boolean executionSuccessful;
    private String html;

    public GooglePlayChecker(DroidefenseProject project) {
        this.currentProject = project;
    }

    public void onPreExecute() {
        packageName = currentProject.getManifestInfo().getPackageName();
        if (packageName != null) {
            url = BASE_URL + packageName;
        }
    }

    public boolean existsOnGooglePlay(String url) {
        return !FileIOHandler.getWebsiteContent(url).equals(NOT_FOUND_ERROR);
    }

    public String getGooglePlayData(String url) {
        if (url != null && url.length() > 0)
            return FileIOHandler.getWebsiteContent(url);
        return "";
    }

    public void onExecute() {
        //TODO parse retrieved web data and get stuff
        boolean exists = existsOnGooglePlay(url);
        if (exists) {
            String webdata = getGooglePlayData(url);
            executionSuccessful = webdata != null && webdata.trim().length() > 0;
        } else {
            executionSuccessful = false;
        }
    }

    public void getStarts() {
        //Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
    }

    public void postExecute() {
        if (executionSuccessful) {
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

    public String getPluginName() {
        return "Google Play checker";
    }

    public boolean isExecutionSuccessful() {
        return executionSuccessful;
    }
}
