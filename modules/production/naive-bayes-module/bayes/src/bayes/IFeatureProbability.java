package bayes;

/**
 * Simple interface defining the method to calculate the feature probability.
 *
 * @param <T> The feature class.
 * @param <K> The category class.
 * @author Philipp Nolte
 */
public interface IFeatureProbability<T, K> {

    public float featureProbability(T feature, K category);

}