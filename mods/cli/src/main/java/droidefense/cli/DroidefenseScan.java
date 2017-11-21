package droidefense.cli;

import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.analysis.base.AnalysisFactory;
import droidefense.exception.InvalidScanParametersException;
import droidefense.exception.UnknownAnalyzerException;
import droidefense.sdk.helpers.APKUnpacker;
import droidefense.sdk.helpers.DroidDefenseEnvironment;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import java.io.File;

public class DroidefenseScan {

    private DroidefenseOptions options;
    private DroidefenseProject project;

    /**
     * Default constructor
     * @param args command line arguments
     * @throws InvalidScanParametersException
     */
    public DroidefenseScan(String[] args) throws InvalidScanParametersException, UnknownAnalyzerException, ParseException {
        options = new DroidefenseOptions();
        options.showAsciiBanner();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        executeUserCmd(cmd);
    }

    private void executeUserCmd(CommandLine cmd) throws UnknownAnalyzerException {
        if (cmd.hasOption("-help")) {
            options.showHelp();
        } else if (cmd.hasOption("-version")) {
            options.showVersion();
        } else {
            executeCustom(cmd);
        }
    }

    private void executeCustom(CommandLine cmd) throws UnknownAnalyzerException {
        //get user selected unpacker. default apktool
        APKUnpacker unpacker = APKUnpacker.ZIP;
        if (cmd.hasOption("unpacker")) {
            String unpackerStr = cmd.getOptionValue("unpacker");
            unpacker = APKUnpacker.getUnpackerFromStringName(unpackerStr);
        }

        this.project = new DroidefenseProject();

        if (cmd.hasOption("output")) {
            project.setSettingsReportType(cmd.getOptionValue("output"));
        }
        //set boolean values
        project.setSettingAutoOpen(cmd.hasOption("show"));
        Log.beVerbose(cmd.hasOption("verbose"));

        //read user selected .apk
        if (cmd.hasOption("input")) {
            processInput(cmd, unpacker);
        } else {
            //as default action
            options.showVersion();
            options.showHelp();
        }
    }

    private void processInput(CommandLine cmd, APKUnpacker unpacker) throws UnknownAnalyzerException {
        //initialize environment first
        if (!DroidDefenseEnvironment.isLoaded()) {
            Log.write(LoggerType.FATAL, "Droidefense initialization error");
        }
        else{
            boolean profilingEnabled = cmd.hasOption("profile");
            File inputFile = new File(cmd.getOptionValue("input"));

            //profiler wait time | start
            if (profilingEnabled) {
                profilingAlert("activate");
            }

            readSampleForScan(project, inputFile, unpacker);

            //profiler wait time | forceStop
            if (profilingEnabled) {
                profilingAlert("deactivate");
            }
        }
    }

    private void profilingAlert(String status) {
        System.out.println("Profiling mode enabled. Waiting user to " + status + " profler. Press enter key when ready.");
        System.out.println("Press enter key to continue...");
        options.readKeyBoard();
    }

    private void forceStop() {
        if(this.project != null){
            this.project.finish();
        }
        forceExit();
    }

    private void forceExit() {
        Log.write(LoggerType.TRACE, "Droidefense scan finished");
        Log.write(LoggerType.TRACE, "Exiting...");
        //force exit
        System.exit(0);
    }

    private void readSampleForScan(DroidefenseProject project, File f, APKUnpacker unpacker) throws UnknownAnalyzerException {
        if(!f.exists()){
            Log.write(LoggerType.FATAL, "target file ("+f.getAbsolutePath()+") does not exist");
        }
        else if(!f.canRead()){
            Log.write(LoggerType.FATAL, "target file ("+f.getAbsolutePath()+") can not be read. Please check your permissions");
        }
        else {
            Log.write(LoggerType.TRACE, "Building project");

            //set sample
            LocalApkFile sample = new LocalApkFile(f, project, unpacker);
            project.setSample(sample);

            //start sample scan
            startSampleScan(project);

            //forceStop scan
            this.forceStop();
        }
    }

    private void startSampleScan(DroidefenseProject project) throws UnknownAnalyzerException {
        Log.write(LoggerType.TRACE, "Project ID:\t" + project.getProjectId());
        AbstractAndroidAnalysis analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.GENERAL);
        project.analyze(analyzer);
    }

    public static void main(String[] args) throws InvalidScanParametersException {
        try {
            new DroidefenseScan(args);
        } catch (UnknownAnalyzerException e) {
            Log.write(LoggerType.FATAL, e.getLocalizedMessage());
            Log.write(LoggerType.ERROR, "Analyzer not found: " + e.getLocalizedMessage());
        } catch (ParseException e) {
            Log.write(LoggerType.FATAL, e.getLocalizedMessage());
            Log.write(LoggerType.ERROR, "Parsing exception detected: " + e.getLocalizedMessage());
        }
    }
}