package com.zerjioang.apkr.analysis.base;

import com.zerjioang.apkr.analysis.*;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AnalysisFactory {

    public static final byte STATIC_ANALYSIS = 0x00;
    public static final byte STATIC_ANALYSIS_PLUGIN = 0x01;
    public static final byte UNPACK = 0x02;
    public static final byte DYNAMIC_ANALYSIS = 0x03;
    public static final byte DYNAMIC_PLUGIN_ANALYSIS = 0x04;
    public static final byte RULE_ENGINE_ANALYSIS = 0x05;
    public static final byte MACHINE_LEARNING_ANALYSIS = 0x6;
    public static final byte GENERAL = 0x07;

    public static AbstractAndroidAnalysis getAnalyzer(byte id) {
        switch (id) {
            case GENERAL:
                return new GeneralAnalysis();
            case STATIC_ANALYSIS:
                return new AndroidStaticAnalysis();
            case STATIC_ANALYSIS_PLUGIN:
                return new AndroidStaticPluginAnalysis();
            case UNPACK:
                return new UnpackAnalysis();
            case DYNAMIC_ANALYSIS:
                return new AndroidDynamicAnalysis();
            case DYNAMIC_PLUGIN_ANALYSIS:
                return new AndroidDynamicPluginAnalysis();
            case MACHINE_LEARNING_ANALYSIS:
                return new MachineLearningAnalysis();
            case RULE_ENGINE_ANALYSIS:
                return new RuleAnalysis();
        }
        return null;
    }
}
