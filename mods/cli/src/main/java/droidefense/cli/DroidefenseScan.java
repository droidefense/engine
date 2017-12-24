package droidefense.cli;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.analysis.base.AnalysisFactory;
import droidefense.exception.ConfigFileNotFoundException;
import droidefense.exception.EnvironmentNotReadyException;
import droidefense.exception.InvalidScanParametersException;
import droidefense.exception.UnknownAnalyzerException;
import droidefense.handler.FileIOHandler;
import droidefense.sdk.helpers.APKUnpacker;
import droidefense.sdk.helpers.DroidDefenseEnvironment;
import droidefense.sdk.helpers.DroidDefenseEnvironmentConfig;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import java.io.File;

public class DroidefenseScan {

    private final String[] scanArguments;
    private DroidefenseOptions options;
    private DroidefenseProject project;

    /**
     * Default constructor
     *
     * @param args command line arguments
     */
    public DroidefenseScan(String[] args) {
        this.scanArguments = args;
    }

    public static void main(String[] args) {
        DroidefenseScan scan = new DroidefenseScan(args);
        try {
            scan.loadUserPreferences();
        } catch (UnknownAnalyzerException e) {
            Log.write(LoggerType.ERROR, "Analyzer not found", e.getLocalizedMessage());
        } catch (ParseException e) {
            Log.write(LoggerType.ERROR, "Parsing exception detected", e.getLocalizedMessage());
        } catch (InvalidScanParametersException e) {
            Log.write(LoggerType.ERROR, "Invalid scan parameter provided", e.getLocalizedMessage());
        } catch (EnvironmentNotReadyException e) {
            Log.write(LoggerType.ERROR, "Environment not ready", e.getBaseMessage());
            Log.write(LoggerType.ERROR, "Please, check a valid .json file exists before executing the scan");
            scan.createDefaultConfigurationFile();
        }
    }

    /**
     * Reads user specified command line arguments and executes according to specified user preferences
     *
     * @throws UnknownAnalyzerException
     * @throws EnvironmentNotReadyException
     * @throws ParseException
     */
    private void loadUserPreferences() throws UnknownAnalyzerException, InvalidScanParametersException, EnvironmentNotReadyException, ParseException {
        String errorMessage = isEnvironmentReady();
        if (errorMessage == null) {
            options = new DroidefenseOptions();
            options.showAsciiBanner();

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, this.scanArguments);
            executeUserCmd(cmd);
        } else {
            throw new EnvironmentNotReadyException(errorMessage);
        }
    }

    private String isEnvironmentReady() {
        File config = FileIOHandler.getConfigurationFile();
        if (!config.exists()) {
            return "Configuration file (" + config.getAbsolutePath() + ") not found under expected folder";
        }
        return null;
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

        this.project = new DroidefenseProject();

        //get user selected unpacker. default zip
        APKUnpacker unpacker = APKUnpacker.ZIP;
        if (cmd.hasOption("unpacker")) {
            String unpackerStr = cmd.getOptionValue("unpacker");
            unpacker = APKUnpacker.getUnpackerFromStringName(unpackerStr);
        }
        this.project.setUsedUnpacker(unpacker);

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
            options.showHelp();
        }
    }

    private void processInput(CommandLine cmd, APKUnpacker unpacker) throws UnknownAnalyzerException {
        //initialize environment first
        if (DroidDefenseEnvironment.isLoaded()) {
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
        } else {
            Log.write(LoggerType.FATAL, "Droidefense initialization error");
        }
    }

    private void profilingAlert(String status) {
        System.out.println("Profiling mode enabled. Waiting user to " + status + " profler. Press enter key when ready.");
        System.out.println("Press enter key to continue...");
        options.readKeyBoard();
    }

    private void forceStop() {
        if (this.project != null) {
            this.project.finish();
        }
        forceExit();
    }

    private void forceExit() {
        Log.write(LoggerType.TRACE, "Droidefense scan finished");
        Log.write(LoggerType.TRACE, "Aborting further execution");
        Log.write(LoggerType.TRACE, "Exiting...");
        //force exit
        System.exit(-1);
    }

    private void readSampleForScan(DroidefenseProject project, File f, APKUnpacker unpacker) throws UnknownAnalyzerException {
        if (!f.exists()) {
            Log.write(LoggerType.FATAL, "target file (" + f.getAbsolutePath() + ") does not exist");
        } else if (!f.canRead()) {
            Log.write(LoggerType.FATAL, "target file (" + f.getAbsolutePath() + ") can not be read. Please check your permissions");
        } else {
            Log.write(LoggerType.TRACE, "Building project");

            //set sample
            Log.write(LoggerType.TRACE, "Loading sample into system memory");
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

    private void createDefaultConfigurationFile() {
        Log.write(LoggerType.DEBUG, "Creating default config.json file...");
        try {
            boolean success = DroidDefenseEnvironmentConfig.getInstance().createDefaultConfigJsonFile();
            if (success) {
                Log.write(LoggerType.INFO, "Configuration file succesfully created", "Please, configure it and lauch droidefense again");
            }
        } catch (ConfigFileNotFoundException e) {
            Log.write(LoggerType.FATAL, "Could not create default .json file. Please check your filesystem permissions and try again");
        }
    }
}