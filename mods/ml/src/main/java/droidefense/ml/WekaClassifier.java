package droidefense.ml;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.File;
import java.util.ArrayList;

public class WekaClassifier {

    //useful methods
    public Evaluation classify(Classifier model, Instances trainingSet, Instances testingSet) throws Exception {

        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public double calculateAccuracy(ArrayList predictions) {
        double correct = 0;
        if ( predictions!=null && predictions.size() > 0){
            for (Object prediction : predictions) {
                NominalPrediction np = (NominalPrediction) prediction;
                if (np.predicted() == np.actual()) {
                    correct++;
                }
            }
            return 100 * correct / predictions.size();
        }
        return 0.0;
    }

    public Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {

        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    public Classifier[] readModels(File[] modelFiles) throws Exception {
        if (modelFiles == null || modelFiles.length == 0) {
            return new Classifier[0];
        }
        Classifier[] classList = new Classifier[modelFiles.length];
        for (int i = 0; i < classList.length; i++) {
            classList[i] = (Classifier) SerializationHelper.read( modelFiles[i].getAbsolutePath() );
        }
        return classList;
    }
}
