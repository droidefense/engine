package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.analysis.base.AnalysisFactory;
import droidefense.exception.UnknownAnalyzerException;

/**
 * Created by sergio on 4/9/16.
 */
public final class GeneralAnalysis extends AbstractAndroidAnalysis {

    @Override
    protected boolean analyze() {

        executionSuccessful = false;
        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense general scan ---\n\n");

        //UNPACK_AND_DECODE
        AbstractAndroidAnalysis analyzer;
        try {
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.UNPACK);
            currentProject.analyze(analyzer);
            if (currentProject.isCorrectUnpacked()) {
                analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.DECODE);
                currentProject.analyze(analyzer);
                if (currentProject.isCorrectDecoded()) {
                    //STATIC ANALYSIS
                    analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.STATIC_ANALYSIS);
                    currentProject.analyze(analyzer);

                    if (currentProject.isStaticAnalysisDone()) {
                        //RUN STATIC ANALYSIS PLUGINS
                        analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.STATIC_ANALYSIS_PLUGIN);
                        currentProject.analyze(analyzer);
                    } else {
                        //static analysis error
                        Log.write(LoggerType.ERROR, "Error executing static analysis");
                    }
                    if (currentProject.isStaticAnalysisDone()) {
                        //RUN DYNAMIC ANALYSIS
                        analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.DYNAMIC_ANALYSIS);
                        currentProject.analyze(analyzer);

                        if (currentProject.isDynamicAnalysisDone()) {
                            //RUN DYNAMIC ANALYSIS PLUGINS
                            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.DYNAMIC_PLUGIN_ANALYSIS);
                            currentProject.analyze(analyzer);
                        } else {
                            //dynamic plugin failed
                            Log.write(LoggerType.ERROR, "Error executing dynamic analysis");
                        }

                        if (currentProject.isDynamicAnalysisDone()) {
                            //RUN RULE ENGINE
                            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.RULE_ENGINE_ANALYSIS);
                            currentProject.analyze(analyzer);
                        } else {
                            //rule engine failed
                            Log.write(LoggerType.ERROR, "Error executing rule engine analysis");
                        }

                        if (currentProject.isDynamicAnalysisDone()) {
                            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.MACHINE_LEARNING_ANALYSIS);
                            currentProject.analyze(analyzer);
                        } else {
                            //weka failed
                            Log.write(LoggerType.ERROR, "Error executing weka analysis");
                        }
                    } else {
                        //dynamic failed
                        Log.write(LoggerType.ERROR, "Error executing dynamic analysis");
                    }

                    if (currentProject.isStaticAnalysisDone()) {
                        //RUN PRIVACY SCAN
                        analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.PRIVACY_ANALYSIS);
                        currentProject.analyze(analyzer);
                    } else {
                        //privacy scan failed
                        Log.write(LoggerType.ERROR, "Error executing privacy analysis");
                    }

                    if (currentProject.isStaticAnalysisDone()) {
                        //RUN SOCIAL ANALYSIS
                        analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.SOCIAL_ANALYSIS);
                        currentProject.analyze(analyzer);
                    } else {
                        //social analysis error
                        Log.write(LoggerType.ERROR, "Error executing social analysis");
                    }

                } else {
                    //decoded error
                    Log.write(LoggerType.ERROR, "Error decoding sample");
                }
            } else {
                //unpack error
                Log.write(LoggerType.ERROR, "Error unpacking sample");
            }

            this.timeStamp.stop();
            return true;

        } catch (UnknownAnalyzerException e) {
            Log.write(LoggerType.FATAL, "An error ocurred while running general scan", e.getLocalizedMessage());
            this.timeStamp.stop();
            this.errorList.add(e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "Droidefense scanner";
    }
}
