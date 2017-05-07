package droidefense.handler;

import droidefense.batch.helper.OutPutResult;
import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.ml.MachineLearningResult;
import droidefense.ml.WekaClassifier;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.manifest.UsesPermission;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 6/3/16.
 */
public class WekaResultsHandler extends AbstractHandler {

    private static final int ATRRIBUTE_NUMBER = 206;
    private static final String[] attributesList = OutPutResult.getGlobalPermissionList();
    private File featuresFile;
    private WekaClassifier classifier;

    @Override
    public boolean doTheJob() {
        classifier = new WekaClassifier();
        try {
            featuresFile = new File(FileIOHandler.getProjectFolderPath(project) + File.separator + InternalConstant.WEKA_FEATURES_FILE);
            //generate features as weka file format
            generateFeatures();
            //evaluate them with models
            evaluate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void generateFeatures() {
        //old way, but functional

        //get data
        ArrayList<UsesPermission> permList = project.getManifestInfo().getUsesPermissionList();
        OutPutResult result = new OutPutResult(permList);
        //get result
        String data = result.toWekaData();
        //save
        //convert data to weka format
        String[] names = OutPutResult.getGlobalPermissionList();
        String header = "@relation relation\r\n";
        header += "\r\n";
        for (String attr : names) {
            header += "@attribute " + attr + " {false, true}\r\n";
        }
        header += "\r\n";
        header += "@attribute class {MALWARE, GOODWARE} \r\n";
        header += "\r\n";
        header += "@data\r\n";
        header += "\r\n";
        String body = data + ",?\r\n";

        String out = header + body;
        FileIOHandler.saveFile(featuresFile, out);
    }

    private void evaluate() throws Exception {

        Log.write(LoggerType.TRACE, "Starting WEKA classifier...");

        //classifier names
        File[] modelFiles = FileIOHandler.getModelsDir().listFiles();

        /*
         * First we load the training data from our ARFF file
		 */
        ArffLoader trainLoader = new ArffLoader();

        trainLoader.setSource(featuresFile);
        Instances unlabeled = trainLoader.getDataSet();

		/*
         * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
        unlabeled.setClassIndex(unlabeled.size() - 1);

        // Use a set of classifiers
        Classifier[] models = {};

        /*
         * Now we read in the serialized apimodel from disk
		 */
        models = classifier.readModels(modelFiles);

        MachineLearningResult result = new MachineLearningResult();

        // Run for each apimodel
        int total = models.length;
        int positive = 0;
        for (int j = 0; j < models.length; j++) {
            /*
            Evaluation eval = new Evaluation(unlabeled);
            eval.evaluateModel(models[j], unlabeled);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            result.add(modelFiles[j].getName().replace(".apimodel", ""), eval.correct());
            */
            double clsLabel = models[j].classifyInstance(unlabeled.instance(0));
            positive += (int) clsLabel;
            result.add(modelFiles[j].getName().replace(".apimodel", ""), clsLabel);
        }
        //generate json output for report
        //class 1: goodware
        //class 0: malware
        positive = total - positive;
        result.setPositives(positive);
        result.setTotal(total);
        if (total != 0) {
            result.setRatio(positive / (double) total);
        } else {
            result.setRatio(0);
        }
        project.setMachineLearningResult(result);
        Log.write(LoggerType.TRACE, "WEKA classification done!");
    }
}
