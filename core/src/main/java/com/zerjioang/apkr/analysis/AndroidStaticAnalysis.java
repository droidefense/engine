package com.zerjioang.apkr.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.handler.ObjdumpHandler;
import com.zerjioang.apkr.parser.base.AbstractFileParser;
import com.zerjioang.apkr.parser.base.ParserFactory;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

/**
 * Created by r00t on 24/10/15.
 */
public final class AndroidStaticAnalysis extends AbstractAndroidAnalysis {

    public AndroidStaticAnalysis() {
        timeStamp = new AtomTimeStamp();
    }

    @Override
    public boolean analyze() {
        positiveMatch = false;
        Log.write(LoggerType.TRACE, "\n\n --- Running Android static analysis ---\n\n");

        //0 parse meta
        AbstractFileParser parser = ParserFactory.getParser(ParserFactory.STATIC_META);
        parser.setApk(apkFile);
        parser.parse();

        //1 decompile if enabled
        if (ApkrConstants.DECOMPILE.equals("true")) {
            //2 parse certificates
            parser = ParserFactory.getParser(ParserFactory.CODE_DECOMPILER);
            parser.setApk(apkFile);
            parser.parse();
        }

        //2 parse manifest
        parser = ParserFactory.getParser(ParserFactory.MANIFEST_PARSER);
        parser.setApk(apkFile);
        parser.parse();

        //3 parse .so files
        ObjdumpHandler handler = new ObjdumpHandler();
        handler.setApk(apkFile);
        handler.setList(currentProject.getStaticInfo().getLibFiles());
        handler.doTheJob();

        //4 parse certificates
        parser = ParserFactory.getParser(ParserFactory.CERTIFICATE_PARSER);
        parser.setApk(apkFile);
        parser.parse();

        //5 parse resources
        parser = ParserFactory.getParser(ParserFactory.RESOURCE_PARSER);
        parser.setApk(apkFile);
        parser.parse();

        return true;
    }

    @Override
    public String getName() {
        return "Android static analysis";
    }
}
