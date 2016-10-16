package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.ObjdumpHandler;
import droidefense.parser.base.AbstractFileParser;
import droidefense.parser.base.ParserFactory;
import droidefense.sdk.model.base.ExecutionTimer;

/**
 * Created by r00t on 24/10/15.
 */
public final class AndroidStaticAnalysis extends AbstractAndroidAnalysis {

    public AndroidStaticAnalysis() {
        timeStamp = new ExecutionTimer();
    }

    @Override
    public boolean analyze() {
        positiveMatch = false;
        Log.write(LoggerType.TRACE, "\n\n --- Running Android static analysis ---\n\n");

        //0 parse meta
        AbstractFileParser parser = ParserFactory.getParser(ParserFactory.STATIC_META, currentProject, apkFile);
        parser.parse();

        //1 decompile if enabled
        //TODO make in-memory decompilation
        /*
        if (DroidDefenseParams.getInstance().DECOMPILE) {
            //2 parse certificates
            parser = ParserFactory.getParser(ParserFactory.CODE_DECOMPILER, currentProject, apkFile);
            parser.parse();
        }
        */

        //2 parse manifest
        parser = ParserFactory.getParser(ParserFactory.MANIFEST_PARSER, currentProject, apkFile);
        parser.parse();

        //3 parse .so files
        ObjdumpHandler handler = new ObjdumpHandler();
        handler.setApk(apkFile);
        handler.setList(currentProject.getStaticInfo().getLibFiles());
        handler.doTheJob();

        //4 parse certificates
        parser = ParserFactory.getParser(ParserFactory.CERTIFICATE_PARSER, currentProject, apkFile);
        parser.parse();

        //5 parse resources
        parser = ParserFactory.getParser(ParserFactory.RESOURCE_PARSER, currentProject, apkFile);
        parser.parse();

        return true;
    }

    @Override
    public String getName() {
        return "Android static analysis";
    }
}
