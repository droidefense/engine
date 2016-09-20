package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.ObjdumpHandler;
import com.zerjioang.apkr.v1.core.analysis.sttc.parser.AbstractFileParser;
import com.zerjioang.apkr.v1.core.analysis.sttc.parser.ParserFactory;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidStaticAnalysis extends AbstractAndroidAnalysis {

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
