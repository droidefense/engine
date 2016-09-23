package com.zerjioang.apkr.v1.httpserver.apimodel;

import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by sergio on 22/3/16.
 */
public class LatestUploads {

    private static final int DEFAULT_SAMPLES = 10;
    private final int max;
    private int added;
    private String[] names, hash, lastMod;

    public LatestUploads(int max) {
        if (max <= 0)
            this.max = DEFAULT_SAMPLES;
        else
            this.max = max;

        this.names = new String[max];
        this.hash = new String[max];
        this.lastMod = new String[max];
        loadLatest();
    }

    private void loadLatest() {
        //get folder
        File uploadFolder = FileIOHandler.getUploadsFolder();
        //get files
        File[] files = uploadFolder.listFiles((dir, name) -> {
            return name.endsWith(ApkrConstants.APK_EXTENSION);
        });
        //sort files
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });
        added = 0;
        for (File f : files) {
            if (added < max) {
                //add file
                names[added] = f.getName();
                hash[added] = String.valueOf(f.hashCode());
                BasicFileAttributes attr = null;
                try {
                    attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                    lastMod[added] = Util.calculateDateTime(System.currentTimeMillis() - attr.creationTime().toMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                    lastMod[added] = ApkrConstants.EMPTY_STRING;
                }
                added++;
            }
        }
    }
}
