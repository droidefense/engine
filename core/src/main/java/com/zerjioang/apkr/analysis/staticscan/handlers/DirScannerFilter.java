package com.zerjioang.apkr.analysis.staticscan.handlers;

import java.io.File;

/**
 * Created by .local on 21/04/2016.
 */
public abstract class DirScannerFilter {

    public abstract boolean addFile(File f);
}
