package droidefense.cli;

import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.system.OSDetection;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;

public class DroidefenseOptions extends Options {

    public DroidefenseOptions() {
        this.addOption("d", "debug", false, "print debugging information");
        this.addOption("p", "profile", false, "Wait for JVM profiler");
        this.addOption("v", "verbose", false, "be verbose");
        this.addOption("V", "version", false, "show current version information");
        this.addOption("h", "help", false, "print this message");
        this.addOption("s", "show", false, "show generated report after scan");

        this.addOption(
                Option.builder("u")
                        .longOpt("unpacker")
                        .desc("select prefered unpacker: [zip, apktool, memapktool]")
                        .hasArg()
                        .argName("unpacker")
                        .build()
        );

        this.addOption(
                Option.builder("i")
                        .longOpt("input")
                        .desc("input .apk to be analyzed")
                        .hasArg()
                        .argName("apk")
                        .build()
        );

        this.addOption(
                Option.builder("o")
                        .longOpt("output")
                        .desc("select prefered output: [json, json.min]")
                        .hasArg()
                        .argName("format")
                        .build()
        );
    }

    public void showHelp() {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("droidefense", this);
        System.out.println();
        System.out.println("For additional info, please visit: https://github.com/droidefense/engine");
        System.out.println();
    }

    public void showVersion() {
        String offset = OSDetection.isUnix() ? "" : "\t";
        System.out.println("\t* Current version: \t\t\t" + offset + InternalConstant.ENGINE_VERSION);
        System.out.println("\t* Current build: \t\t\t" + offset + readVersion());
        System.out.println("\t* Check out on Github: \t\t\t" + offset + InternalConstant.REPO_URL);
        System.out.println("\t* Report your issue: \t\t\t" + offset + offset + InternalConstant.ISSUES_URL);
        System.out.println("\t* Lead developer: \t\t\t" + offset + offset + InternalConstant.LEAD_DEVELOPER);
        System.out.println();
    }

    private String readVersion() {
        try {
            return Util.readFileFromInternalResourcesAsString(
                    DroidefenseOptions.class.getClassLoader(),
                    "/lastbuild");
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not get version information");
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            return "unknown";
        }
    }

    public void showAsciiBanner() {
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
        System.out.println("droidefense v"+InternalConstant.ENGINE_VERSION+" - a tool for automated reverse engineering of Android apk files");
        System.out.println("Copyright 2017 Droidefense <droidefense@protonmail.com>");
        System.out.println();
    }

    public void readKeyBoard() {
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }
}