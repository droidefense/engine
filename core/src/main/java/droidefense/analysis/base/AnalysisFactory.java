package droidefense.analysis.base;

import droidefense.analysis.*;
import droidefense.exception.UnknownAnalyzerException;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AnalysisFactory {

    public static final byte STATIC_ANALYSIS = 0x00;
    public static final byte STATIC_ANALYSIS_PLUGIN = 0x01;
    public static final byte UNPACK_AND_DECODE = 0x02;
    public static final byte DYNAMIC_ANALYSIS = 0x03;
    public static final byte DYNAMIC_PLUGIN_ANALYSIS = 0x04;
    public static final byte RULE_ENGINE_ANALYSIS = 0x05;
    public static final byte MACHINE_LEARNING_ANALYSIS = 0x6;
    public static final byte GENERAL = 0x07;
    public static final byte PRIVACY_ANALYSIS = 0x08;

    public static AbstractAndroidAnalysis getAnalyzer(byte id) throws UnknownAnalyzerException {
        switch (id) {
            case GENERAL:
                return new GeneralAnalysis();
            case STATIC_ANALYSIS:
                return new AndroidStaticAnalysis();
            case STATIC_ANALYSIS_PLUGIN:
                return new AndroidStaticPluginAnalysis();
            case UNPACK_AND_DECODE:
                return new UnpackAnalysis();
            case DYNAMIC_ANALYSIS:
                return new AndroidDynamicAnalysis();
            case DYNAMIC_PLUGIN_ANALYSIS:
                return new AndroidDynamicPluginAnalysis();
            case MACHINE_LEARNING_ANALYSIS:
                return new MachineLearningAnalysis();
            case RULE_ENGINE_ANALYSIS:
                return new RuleAnalysis();
            case PRIVACY_ANALYSIS:
                return new PrivacyAnalysis();
        }
        throw new UnknownAnalyzerException("An analyzer with id " + Integer.toHexString(id) + " was requested but it does not exists");
    }
}
