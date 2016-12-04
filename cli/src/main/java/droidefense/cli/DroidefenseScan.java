package droidefense.cli;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.analysis.base.AnalysisFactory;
import droidefense.exception.ConfigFileNotFoundException;
import droidefense.exception.InvalidScanParametersException;
import droidefense.exception.UnknownAnalyzerException;
import droidefense.sdk.helpers.APKUnpacker;
import droidefense.sdk.helpers.DroidDefenseParams;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.temp.DroidefenseIntel;

import java.io.File;
import java.security.cert.CertificateException;

/**
 * Created by sergio on 3/9/16.
 */
public class DroidefenseScan {

    private static boolean init = false;
    private DroidefenseProject project;

    public DroidefenseScan(String[] args) throws InvalidScanParametersException {

        DroidefenseSettings settings = new DroidefenseSettings(args);

        //help info if requested
        if (settings.isHelpRequested()) {
            settings.showUsage();
            return;
        }

        //version requested
        if (settings.getVersion()) {
            System.out.println("Current version of droidefense: " + InternalConstant.ENGINE_VERSION);
            System.out.println("Check out on Github: https://github.com/droidefense");
            System.out.println("Lead developer: @zerjioang");
            return;
        }

        //profiler wait time | start
        if (settings.profilingEnabled()) {
            profilingAlert("activate");
        }

        //get user selected unpacker. default apktool
        String unpackerStr = settings.getUnpacker();
        APKUnpacker unpacker = APKUnpacker.APKTOOL_UNPACKER;
        if (unpackerStr != null) {
            if (unpackerStr.equalsIgnoreCase("apktool")) {
                unpacker = APKUnpacker.APKTOOL_UNPACKER;
            } else if (unpackerStr.equalsIgnoreCase("zip")) {
                unpacker = APKUnpacker.ZIP_UNPACKER;
            }
        }

        if (settings.hasFile()) {
            //security check
            File inputFile = settings.getInput();
            if (inputFile != null) {
                LocalApkFile file = new LocalApkFile(inputFile, unpacker);
                initScan(file);
                //profiler wait time | stop
                if (settings.profilingEnabled()) {
                    profilingAlert("deactivate");
                }
            } else {
                throw new InvalidScanParametersException("Received parameters are not valid to launch the scan", args);
            }
        }
    }

    public static void main(String[] args) throws CertificateException, InvalidScanParametersException {
        new DroidefenseScan(args);
    }

    private void profilingAlert(String status) {
        System.out.println("Profiling mode enabled. Waiting user to " + status + " profler. Press enter key when ready.");
        System.out.println("Press enter key to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    public void stop() {
        //save report .json to file
        Log.write(LoggerType.TRACE, "Saving report file...");
        project.finish();
        Log.write(LoggerType.TRACE, "Droidefense scan finished");
    }

    private void initScan(LocalApkFile apk) {
        //execute only once
        try {
            loadVariables();

            Log.write(LoggerType.TRACE, "Building project");
            project = new DroidefenseProject(apk);

            Log.write(LoggerType.TRACE, "Running Droidefense");

            Log.write(LoggerType.TRACE, "Project ID:\t" + project.getProjectId());

            AbstractAndroidAnalysis analyzer;
            try {
                analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.GENERAL);
                //Start analysis
                project.analyze(analyzer);
            } catch (UnknownAnalyzerException e) {
                Log.write(LoggerType.FATAL, e.getLocalizedMessage());
                System.err.println("Analyzer not found" + e.getLocalizedMessage());
            }

            //stop scan
            this.stop();
        } catch (ConfigFileNotFoundException e) {
            Log.write(LoggerType.FATAL, e.getLocalizedMessage());
            System.err.println("Fatal error occurred: " + e.getLocalizedMessage());
        }
    }

    private void loadVariables() throws ConfigFileNotFoundException {
        if (!init) {
            //init data structs
            DroidDefenseParams.init();
            Log.write(LoggerType.TRACE, "Loading Droidefense data structs...");
            //create singleton instance of AtomIntelligence
            DroidefenseIntel.getInstance();
            Log.write(LoggerType.TRACE, "Data loaded!!");
            init = true;
        }
    }
}