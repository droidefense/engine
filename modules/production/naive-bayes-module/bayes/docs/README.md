Java Naive Bayes bayes.Classifier
==================

Nothing special. It works and is well documented, so you should get it running without wasting too much time searching for other alternatives on the net.

Overview
------------------

I like talking about *features* and *categories*. Objects have features and may belong to a category. The classifier will try matching objects to their categories by looking at the objects' features. It does so by consulting its memory filled with knowledge gathered from training examples.

Classifying a feature-set results in the highest product of 1) the probability of that category to occur and 2) the product of all the features' probabilities to occure in that category:

```classify(feature1, ..., featureN) = argmax(P(category) * PROD(P(feature|category)))```

This is a so-called maximum a posteriori estimation. Wikipedia actually does a good job explaining it: http://en.wikipedia.org/wiki/Naive_Bayes_classifier#Probabilistic_model

Learning from Examples
------------------

Add knowledge by telling the classifier, that these features belong to a specific category:

```java
String[] positiveText = "I love sunny days".split("\\s");
bayes.learn("positive", Arrays.asList(positiveText));
```

Classify unknown objects
------------------

Use the gathered knowledge to classify unknown objects with their features. The classifier will return the category that the object most likely belongs to.

```java
String[] unknownText1 = "today is a sunny day".split("\\s");
bayes.classify(Arrays.asList(unknownText1)).getCategory());
```

Example
------------------

Here is an excerpt from the example. The classifier will classify sentences (arrays of features) as sentences with either positive or negative sentiment. Please refer to the full example for a more detailed documentation.

```java
// Create a new bayes classifier with string categories and string features.
bayes.Classifier<String, String> bayes = new bayes.BayesClassifier<String, String>();

// Two examples to learn from.
String[] positiveText = "I love sunny days".split("\\s");
String[] negativeText = "I hate rain".split("\\s");

// Learn by classifying examples.
// New categories can be added on the fly, when they are first used.
// A classification consists of a category and a list of features
// that resulted in the classification in that category.
bayes.learn("positive", Arrays.asList(positiveText));
bayes.learn("negative", Arrays.asList(negativeText));

// Here are two unknown sentences to classify.
String[] unknownText1 = "today is a sunny day".split("\\s");
String[] unknownText2 = "there will be rain".split("\\s");

System.out.println( // will output "positive"
    bayes.classify(Arrays.asList(unknownText1)).getCategory());
System.out.println( // will output "negative"
    bayes.classify(Arrays.asList(unknownText2)).getCategory());

// Get more detailed classification result.
((bayes.BayesClassifier<String, String>) bayes).classifyDetailed(
    Arrays.asList(unknownText1));

// Change the memory capacity. New learned classifications (using
// the learn method) are stored in a queue with the size given
// here and used to classify unknown sentences.
bayes.setMemoryCapacity(500);
```

Forgetful learning
------------------

This classifier is forgetful. This means, that the classifier will forget recent classifications it uses for future classifications after - defaulting to 1.000 - classifications learned. This will ensure, that the classifier can react to ongoing changes in the user's habbits.


Interface
------------------
The abstract ```bayes.Classifier<T, K>``` serves as a base for the concrete ```bayes.BayesClassifier<T, K>```. Here are its methods. Please also refer to the Javadoc.

* ```void reset()``` Resets the learned feature and category counts.
* ```Set<T> getFeatures()``` Returns a ```Set``` of features the classifier knows about.
* ```Set<K> getCategories()``` Returns a ```Set``` of categories the classifier knows about.
* ```int getCategoriesTotal()``` Retrieves the total number of categories the classifier knows about.
* ```int getMemoryCapacity()``` Retrieves the memory's capacity.
* ```void setMemoryCapacity(int memoryCapacity)``` Sets the memory's capacity.  If the new value is less than the old value, the memory will be truncated accordingly.
* ```void incrementFeature(T feature, K category)``` Increments the count of a given feature in the given category.  This is equal to telling the classifier, that this feature has occurred in this category.
* ```void incrementCategory(K category)``` Increments the count of a given category.  This is equal to telling the classifier, that this category has occurred once more.
* ```void decrementFeature(T feature, K category)``` Decrements the count of a given feature in the given category.  This is equal to telling the classifier that this feature was classified once in the category.
* ```void decrementCategory(K category)``` Decrements the count of a given category.  This is equal to telling the classifier, that this category has occurred once less.
* ```int featureCount(T feature, K category)``` Retrieves the number of occurrences of the given feature in the given category.
* ```int categoryCount(K category)``` Retrieves the number of occurrences of the given category.
* ```float featureProbability(T feature, K category)``` (*implements* ```bayes.IFeatureProbability<T, K>.featureProbability```) Returns the probability that the given feature occurs in the given category.
* ```float featureWeighedAverage(T feature, K category)``` Retrieves the weighed average ```P(feature|category)``` with overall weight of ```1.0``` and an assumed probability of ```0.5```. The probability defaults to the overall feature probability.
* ```float featureWeighedAverage(T feature, K category, bayes.IFeatureProbability<T, K> calculator)``` Retrieves the weighed average ```P(feature|category)``` with overall weight of ```1.0```, an assumed probability of ```0.5``` and the given object to use for probability calculation.
* ```float featureWeighedAverage(T feature, K category, bayes.IFeatureProbability<T, K> calculator, float weight)```Retrieves the weighed average ```P(feature|category)``` with the given weight and an assumed probability of ```0.5``` and the given object to use for probability calculation.
* ```float featureWeighedAverage(T feature, K category, bayes.IFeatureProbability<T, K> calculator, float weight,  float assumedProbability)``` Retrieves the weighed average ```P(feature|category)``` with the given weight, the given assumed probability and the given object to use for probability calculation.
* ```void learn(K category, Collection<T> features)``` Train the classifier by telling it that the given features resulted in the given category.
* ```void learn(bayes.Classification<T, K> classification)``` Train the classifier by telling it that the given features resulted in the given category.

The ```bayes.BayesClassifier<T, K>``` class implements the following abstract method:

* ```bayes.Classification<T, K> classify(Collection<T> features)``` It will retrieve the most likely category for the features given and depends on the concrete classifier implementation.

Possible Performance issues
------------------

Performance improvements, I am currently thinking of:

- Store the natural logarithms of the feature probabilities and add them together instead of multiplying the probability numbers

The MIT License (MIT)
------------------

Copyright (c) 2012-2014 Philipp Nolte

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
