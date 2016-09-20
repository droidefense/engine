package com.zerjioang.apkr.v2.batch.helper;

import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerFilter;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerHandler;
import com.zerjioang.apkr.v2.batch.exception.EmptyDataSetException;
import com.zerjioang.apkr.v2.batch.exception.NoFilesFoundException;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by sergio on 4/9/16.
 */
public class DirScanner {

    private final String baseDir;
    private final DirectoryFilter filter;

    public DirScanner(String baseDir, DirectoryFilter filter) {
        this.baseDir = baseDir;
        this.filter = filter;
        if (baseDir == null)
            throw new NullPointerException("Base path can not be null.");
        if (filter == null)
            throw new NullPointerException("Base path can not be null.");
    }

    public void scan() throws EmptyDataSetException, NoFilesFoundException, IOException {

        Log.write(LoggerType.TRACE, "Reading dir: " + baseDir);

        DirScannerHandler handler = new DirScannerHandler(new File(baseDir), false, new DirScannerFilter() {
            @Override
            public boolean addFile(File f) {
                return filter.filterCondition(f);
            }
        });
        handler.doTheJob();
        ArrayList<ResourceFile> files = handler.getFiles();

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

        File outputDir = new File("/Users/sergio/Documents/Atom-git/engine/temp/");
        FileIOHandler.saveAsRAW(set, filter.getResultName(), outputDir);
    }
}
