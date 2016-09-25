package com.zerjioang.apkr.v1.httpserver.apimodel;

import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sergio on 20/3/16.
 */
public class HashChecking implements Serializable {

    private static final String scannerVersion = ApkrConstants.ENGINE_VERSION;
    private final String hash;
    private final boolean analyzed;
    private String result;
    private String resultUrl;
    private String days;
    private Date date;

    public HashChecking(String hash) {
        if (hash != null) {
            this.hash = hash.toUpperCase();

            //calculate if it was analyzed or not
            String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
            File saveFolder = new File(path + File.separator + hash);
            this.analyzed = saveFolder.exists();

            //set default values
            this.days = "just now";
            this.date = new Date(System.currentTimeMillis());
            this.result = "Unknown";
            this.resultUrl = "/report/" + hash;

            //update values if exists
            if (this.analyzed) {
                //read HashChecking file if exists
                try {
                    HashChecking check = (HashChecking) FileIOHandler.readAsRAW(new File(saveFolder.getAbsolutePath() + File.separator + ApkrConstants.ANALYSIS_METADATA_FILE));
                    if (check != null) {
                        this.date = check.getDate();
                        this.days = Util.calculateDateTime(System.currentTimeMillis() - this.date.getTime());
                        this.result = check.getResult();
                        this.resultUrl = check.getResultUrl();
                    }
                } catch (IOException e) {
                    Log.write(LoggerType.ERROR, "HashChecking error", e);
                } catch (ClassNotFoundException e) {
                    Log.write(LoggerType.ERROR, "HashChecking class not found", e);
                }
            }
        } else {
            this.analyzed = false;
            this.hash = null;
        }
    }

    public String getHash() {
        return hash;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
