package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.exception.UnknownParserException;
import droidefense.handler.ObjdumpHandler;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.worker.base.AbstractFileParser;
import droidefense.worker.base.ParserFactory;

import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public final class AndroidStaticAnalysis extends AbstractAndroidAnalysis {

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running Android static analysis ---\n\n");

        AbstractFileParser parser;

        //0 parse meta
        try {
            parser = ParserFactory.getParser(ParserFactory.STATIC_META, currentProject, apkFile);
            parser.parse();
        } catch (UnknownParserException e) {
            Log.write(LoggerType.FATAL, "Could not recover metadata information parser", e.getLocalizedMessage());
            this.errorList.add(e);
        }

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
        try {
            parser = ParserFactory.getParser(ParserFactory.MANIFEST_PARSER, currentProject, apkFile);
            parser.parse();
        } catch (UnknownParserException e) {
            Log.write(LoggerType.FATAL, "Could not recover manifest parser", e.getLocalizedMessage());
            this.errorList.add(e);
        }

        //parse found .so files
        ArrayList<AbstractHashedFile> filelist = currentProject.getStaticInfo().getLibFiles();
        if (!filelist.isEmpty()) {
            //3 parse .so files
            ObjdumpHandler handler = new ObjdumpHandler();
            handler.setApk(apkFile);
            handler.setList(filelist);
            handler.doTheJob();
        } else {
            Log.write(LoggerType.INFO, "[OK] Skipping native .so files analysis cause no native code found inside");
        }

        //4 parse certificates
        try {
            parser = ParserFactory.getParser(ParserFactory.CERTIFICATE_PARSER, currentProject, apkFile);
            parser.parse();
        } catch (UnknownParserException e) {
            Log.write(LoggerType.FATAL, "Could not recover certificate parser", e.getLocalizedMessage());
            this.errorList.add(e);
        }

        //5 parse resources

        ArrayList<AbstractHashedFile> resourceList = currentProject.getStaticInfo().getResourceFiles();
        if (!resourceList.isEmpty()) {
            try {
                parser = ParserFactory.getParser(ParserFactory.RESOURCE_PARSER, currentProject, apkFile);
                parser.parse();
            } catch (UnknownParserException e) {
                Log.write(LoggerType.FATAL, "Could not recover resource parser", e.getLocalizedMessage());
                this.errorList.add(e);
            }
        } else {
            Log.write(LoggerType.INFO, "[OK] Skipping resource analysis");
        }

        this.currentProject.setStaticAnalysisDone(true);
        return true;
    }

    @Override
    public String getName() {
        return "Android static analysis";
    }
}
