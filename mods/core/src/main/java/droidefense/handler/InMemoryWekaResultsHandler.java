package droidefense.handler;

import droidefense.batch.helper.OutPutResult;
import droidefense.handler.base.AbstractHandler;
import droidefense.ml.MLResultHolder;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.ml.WekaClassifier;
import droidefense.sdk.helpers.InternalConstant;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;

/**
 * Created by sergio on 6/3/16.
 */
public class InMemoryWekaResultsHandler extends AbstractHandler {

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

    private void generateFeatures() throws Exception {
        // Declare two numeric attributes
        Attribute Attribute1 = new Attribute("firstNumeric");
        Attribute Attribute2 = new Attribute("secondNumeric");

        // Declare a nominal attribute along with its values
        FastVector fvNominalVal = new FastVector(3);
        fvNominalVal.addElement("blue");
        fvNominalVal.addElement("gray");
        fvNominalVal.addElement("black");
        Attribute Attribute3 = new Attribute("aNominal", fvNominalVal);

        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(2);
        fvClassVal.addElement("positive");
        fvClassVal.addElement("negative");
        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

        // Declare the feature vector
        FastVector fvWekaAttributes = new FastVector(4);
        fvWekaAttributes.addElement(Attribute1);
        fvWekaAttributes.addElement(Attribute2);
        fvWekaAttributes.addElement(Attribute3);
        fvWekaAttributes.addElement(ClassAttribute);

        //create a training set

        // Create an empty training set
        Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
        // Set class index
        isTrainingSet.setClassIndex(3);


        System.out.println(isTrainingSet);

        //fill the training set with one instance

        // Create the instance
        Instance iExample = new DenseInstance(4);
        iExample.setValue((Attribute) fvWekaAttributes.elementAt(0), 1.0);
        iExample.setValue((Attribute) fvWekaAttributes.elementAt(1), 0.5);
        iExample.setValue((Attribute) fvWekaAttributes.elementAt(2), "gray");
        iExample.setValue((Attribute) fvWekaAttributes.elementAt(3), "positive");

        // add the instance
        isTrainingSet.add(iExample);

        System.out.println(iExample);

        //choose a clasifier and classify

        // Create a naïve bayes classifier
        Classifier cModel = new NaiveBayes();
        cModel.buildClassifier(isTrainingSet);
        System.out.println(cModel);

        // Test the model
        Evaluation eTest = new Evaluation(isTrainingSet);
        eTest.evaluateModel(cModel, isTrainingSet);
        System.out.println(eTest);

        // Print the result à la Weka explorer:
        String strSummary = eTest.toSummaryString();
        System.out.println(strSummary);

        // Get the confusion matrix
        double[][] cmMatrix = eTest.confusionMatrix();

        //use the dataset

        // Specify that the instance belong to the training set
        // in order to inherit from the set description
        //instance.setDataset(isTrainingSet);

        // Get the likelihood of each classes
        // fDistribution[0] is the probability of being “positive”
        // fDistribution[1] is the probability of being “negative”
        //double[] fDistribution = cModel.distributionForInstance(instance);
    }

    private void evaluate() throws Exception {

        Log.write(LoggerType.TRACE, "Starting WEKA classifier...");

        //classifier names
        File[] modelFiles = FileIOHandler.getModelsDir().listFiles();

        if(modelFiles==null || modelFiles.length==0){
            throw new IOException("Could not find any valid model files on specified folder");
        }

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

        MLResultHolder result = new MLResultHolder();

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
            result.setMalwareRatio(positive / (double) total);
        } else {
            result.setMalwareRatio(0);
        }
        project.setMachineLearningResult(result);
        Log.write(LoggerType.TRACE, "WEKA classification done!");
    }
}
