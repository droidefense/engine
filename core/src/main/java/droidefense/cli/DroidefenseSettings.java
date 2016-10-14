package droidefense.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;

/**
 * Created by .local on 10/10/2016.
 */
public class DroidefenseSettings {

    @Parameter(names = {"-v", "--verbose"}, description = "Be a verbose output")
    public boolean verbose = false;
    private JCommander cmm;
    @Parameter(names = {"-d", "--debug"}, description = "Enable debug mode")
    private boolean debug = false;

    @Parameter(names = {"-version"}, description = "Show current version of the engine")
    private boolean version;

    @Parameter(names = {"-i", "--input"}, description = "Input Android application to scan", required = true)
    private File input;

    @Parameter(names = {"-h", "--help"}, description = "show this help", help = true)
    private boolean help;

    @Parameter(names = {"-p", "--profiling"}, description = "Enable JVM profiling")
    private boolean profiling;

    @Parameter(names = {"-u", "--unpack"}, description = "Select unpacker: ZIP, APKTOOL")
    private String unpacker;

    public DroidefenseSettings(String[] args) {
        cmm = new JCommander(this);
        if (args != null && args.length > 0) {
            cmm.parse(args);
        }
    }

    public boolean getVersion() {
        return version;
    }

    public boolean isDebug() {
        return debug;
    }

    public File getInput() {
        return input;
    }

    public boolean isHelpRequested() {
        return help;
    }

    public void showUsage() {
        cmm.usage();
    }

    public boolean hasFile() {
        return getInput() != null;
    }

    public boolean profilingEnabled() {
        return profiling;
    }

    public String getUnpacker() {
        return unpacker;
    }

    public void setUnpacker(String unpacker) {
        this.unpacker = unpacker;
    }
}
