package droidefense.cli;

import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
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

public class DroidefenseScan {

    private static boolean init;
    private DroidefenseProject project;

    public DroidefenseScan(String[] args) throws InvalidScanParametersException {

        showAsciiBanner();

        DroidefenseSettings settings = new DroidefenseSettings(args);

        //help info if requested
        if (settings.isHelpRequested()) {
            settings.showUsage();
            return;
        }
        //version requested
        else if (settings.getVersion()) {
            System.out.println("################################################################################");
            System.out.println("Current version of droidefense: \t" + InternalConstant.ENGINE_VERSION);
            System.out.println("Check out on Github: \t\t\t\t" + InternalConstant.REPO_URL);
            System.out.println("Report your issue: \t\t\t\t\t" + InternalConstant.ISSUES_URL);
            System.out.println("Lead developer: \t\t\t\t\t" + InternalConstant.LEAD_DEVELOPER);
            System.out.println("################################################################################");
            return;
        }

        //get user selected unpacker. default apktool
        String unpackerStr = settings.getUnpacker();
        APKUnpacker unpacker = APKUnpacker.ZIP;
        if (unpackerStr != null) {
            if (unpackerStr.equalsIgnoreCase(APKUnpacker.APKTOOL.name())) {
                //todo fix inmemory apktool procedure
                unpacker = APKUnpacker.APKTOOL;
            } else if (unpackerStr.equalsIgnoreCase(APKUnpacker.ZIP.name())) {
                unpacker = APKUnpacker.ZIP;
            }
        }

        //read user selected .apk
        if (settings.hasFile()) {
            //initialize environment first
            init = loadVariables();
            if (!init) {
                Log.write(LoggerType.FATAL, "Droidefense initialization error");
                return;
            }

            //security check
            File inputFile = settings.getInput();
            if (inputFile != null) {
                //profiler wait time | start
                if (settings.profilingEnabled()) {
                    profilingAlert("activate");
                }
                initScan(inputFile, unpacker);
                //profiler wait time | stop
                if (settings.profilingEnabled()) {
                    profilingAlert("deactivate");
                }
            } else {
                throw new InvalidScanParametersException("Received parameters are not valid to launch the scan", args);
            }
        } else {
            //show help in case of invalid input
            settings.showUsage();
        }
    }

    public static void main(String[] args) throws InvalidScanParametersException {
        new DroidefenseScan(args);
    }

    private static boolean loadVariables() {
        //init data structs
        try {
            DroidDefenseParams.init();
            Log.write(LoggerType.TRACE, "Loading Droidefense data structs...");
            //create singleton instance of AtomIntelligence
            DroidefenseIntel.getInstance();
            Log.write(LoggerType.TRACE, "Data loaded!!");
        } catch (ConfigFileNotFoundException e) {
            Log.write(LoggerType.FATAL, e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private void profilingAlert(String status) {
        System.out.println("Profiling mode enabled. Waiting user to " + status + " profler. Press enter key when ready.");
        System.out.println("Press enter key to continue...");
        try {System.in.read();} catch (Exception e) {}
    }

    public void stop() {
        //save report .json to file
        Log.write(LoggerType.TRACE, "Saving report file...");
        project.finish();
        Log.write(LoggerType.TRACE, "Droidefense scan finished");
    }

    private void initScan(File f, APKUnpacker unpacker) {
        Log.write(LoggerType.TRACE, "Building project");
        project = new DroidefenseProject();
        //set sample
        LocalApkFile sample = new LocalApkFile(f, project, unpacker);
        project.setSample(sample);

        Log.write(LoggerType.TRACE, "Running Droidefense [ANALYSIS]");

        Log.write(LoggerType.TRACE, "Project ID:\t" + project.getProjectId());

        AbstractAndroidAnalysis analyzer;
        try {
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.GENERAL);
            //Start analysis
            project.analyze(analyzer);
        } catch (UnknownAnalyzerException e) {
            Log.write(LoggerType.FATAL, e.getLocalizedMessage());
            Log.write(LoggerType.ERROR, "Analyzer not found" + e.getLocalizedMessage());
        }

        //stop scan
        this.stop();
    }


    private void showAsciiBanner() {
        System.out.println();
        System.out.println();
        System.out.println("________               .__    .___      _____                            ");
        System.out.println("\\______ \\_______  ____ |__| __| _/_____/ ____\\____   ____   ______ ____  ");
        System.out.println(" |    |  \\_  __ \\/  _ \\|  |/ __ |/ __ \\   __\\/ __ \\ /    \\ /  ___// __ \\ ");
        System.out.println(" |    `   \\  | \\(  <_> )  / /_/ \\  ___/|  | \\  ___/|   |  \\\\___ \\\\  ___/ ");
        System.out.println("/_______  /__|   \\____/|__\\____ |\\___  >__|  \\___  >___|  /____  >\\___  >");
        System.out.println("        \\/                     \\/    \\/          \\/     \\/     \\/     \\/ ");
        System.out.println();
        System.out.println();
    }
}