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

        positiveMatch = false;
        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense general scan ---\n\n");

        //UNPACK_AND_DECODE
        AbstractAndroidAnalysis analyzer;
        try {
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.UNPACK_AND_DECODE);
            currentProject.analyze(analyzer);

            //STATIC ANALYSIS
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.STATIC_ANALYSIS);
            currentProject.analyze(analyzer);

            //RUN STATIC ANALYSIS PLUGINS
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.STATIC_ANALYSIS_PLUGIN);
            currentProject.analyze(analyzer);

            /*

            //RUN DYNAMIC ANALYSIS
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.DYNAMIC_ANALYSIS);
            currentProject.analyze(analyzer);

            //RUN DYNAMIC ANALYSIS PLUGINS
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.DYNAMIC_PLUGIN_ANALYSIS);
            currentProject.analyze(analyzer);



            //RUN RULE ENGINE
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.RULE_ENGINE_ANALYSIS);
            currentProject.analyze(analyzer);

            */

            //RUN WEKA
            /*
            analyzer = AnalysisFactory.getAnalyzer(AnalysisFactory.MACHINE_LEARNING_ANALYSIS);
            currentProject.analyze(analyzer);
            return true;
            */

        } catch (UnknownAnalyzerException e) {
            e.printStackTrace();
        }
        this.timeStamp.stop();
        return true;
    }

    @Override
    public String getName() {
        return "Droidefense scanner";
    }
}
