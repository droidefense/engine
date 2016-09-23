package com.zerjioang.apkr.v1.core.analysis.sttc.handlers;

import apkr.external.module.datamodel.manifest.UsesPermission;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.core.ml.MachineLearningResult;
import com.zerjioang.apkr.v2.batch.task.OutPutResult;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 6/3/16.
 */
public class WekaResultsHandler extends AbstractHandler {

    private File featuresFile;

    //useful methods
    public static Evaluation classify(Classifier model, Instances trainingSet, Instances testingSet) throws Exception {

        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {

        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {

        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    @Override
    public boolean doTheJob() {
        try {
            featuresFile = new File(FileIOHandler.getProjectFolderPath(project) + File.separator + ApkrConstants.WEKA_FEATURES_FILE);
            //generate features as weka file format
            generateFeatures();
            //evaluate them with models
            evaluate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void generateFeatures() {
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
        models = readModels(modelFiles);

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
        result.setRatio(positive / (double) total);
        project.setMachineLearningResult(result);
        Log.write(LoggerType.TRACE, "WEKA classification done!");
    }

    private Classifier[] readModels(File[] modelFiles) throws Exception {
        if (modelFiles == null || modelFiles.length == 0) {
            return new Classifier[0];
        }
        Classifier[] classList = new Classifier[modelFiles.length];
        for (int i = 0; i < classList.length; i++) {
            classList[i] = (Classifier) SerializationHelper.read(modelFiles[i].getAbsolutePath());
        }
        return classList;
    }
}
