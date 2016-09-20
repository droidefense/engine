package com.zerjioang.apkr._main;

import com.zerjioang.apkr.v2.batch.exception.EmptyDataSetException;
import com.zerjioang.apkr.v2.batch.exception.NoFilesFoundException;
import com.zerjioang.apkr.v2.batch.helper.DirScanner;
import com.zerjioang.apkr.v2.batch.helper.DirectoryFilter;

import java.io.IOException;

/**
 * Created by sergio on 4/9/16.
 */
public class FileScan {

    public static void main(String[] args) throws IOException, EmptyDataSetException, NoFilesFoundException {
        String sources = "/Volumes/Warehouse/android-sdk/sources/android-23";
        String support = "/Volumes/Warehouse/android-sdk/extras/android/support";
        String java = "/Users/sergio/Documents/Atom-git/engine/temp/jdk8";

        new DirScanner(sources, DirectoryFilter.ANDROID_SDK_FILTER).scan();
        new DirScanner(support, DirectoryFilter.ANDROID_SUPPORT_FILTER).scan();
        new DirScanner(java, DirectoryFilter.JAVA_SDK_FILTER).scan();
    }
}
