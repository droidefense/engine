package com.zerjioang.apkr.batch.helper;

import apkr.external.module.batch.exception.EmptyDataSetException;
import apkr.external.module.batch.exception.NoFilesFoundException;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.DirScannerHandler;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.handler.base.DirScannerFilter;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by sergio on 4/9/16.
 */
public class DirScanner {

    public static final String OUT_DIR = "apkr/dirscan/";
    private final String baseDir;
    private final DirectoryFilter filter;

    public DirScanner(String baseDir, DirectoryFilter filter) {
        this.baseDir = baseDir;
        this.filter = filter;
        if (baseDir == null)
            throw new NullPointerException("Base path can not be null.");
        if (filter == null)
            throw new NullPointerException("Filter directive can not be null.");
    }

    public HashSet<String> scan() throws EmptyDataSetException, NoFilesFoundException, IOException {

        Log.write(LoggerType.TRACE, "Reading dir: " + baseDir);

        DirScannerHandler handler = new DirScannerHandler(new File(baseDir), false, new DirScannerFilter() {
            @Override
            public boolean addFile(File f) {
                return filter.filterCondition(f);
            }
        });
        handler.doTheJob();
        ArrayList<ApkrFile> files = handler.getFiles();

        if (files == null || files.isEmpty()) {
            throw new NoFilesFoundException("Directory scanner could not find any files under directory " + baseDir);
        }

        Log.write(LoggerType.TRACE, "Files detected: " + files.size());

        //convert files to string hashset using packagename+classname as key
        HashSet<String> set = new HashSet<>();

        set = filter.saveCondition(set, files);

        if (set.isEmpty()) {
            throw new EmptyDataSetException("Android SDK support folder scanned files set is empty.");
        }

        File outputDir = new File(OUT_DIR);
        FileIOHandler.saveAsRAW(set, filter.getResultName(), outputDir);
        return set;
    }
}
