package com.sullbrothers.crypto.mancer;

import com.sullbrothers.crypto.database.RateHistoryDAO.RateHistory;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * DecisionInterface
 */
public interface DecisionInterface {

    /**
     * Given a set of historical points, can I tell if performing an action on those points would have been beneficial,
     * and can I also tell to what extent?
     * 
     * First pass at this method will return the action that is predicted
     */
    String predictResult(MancerState state, int rateHistoryPosition);     

    Instance getInstanceFromRateHistory(List<RateHistory> historicalRates, List<Attribute> dataVector, List<String> currencies, int rhPos);

    /**
     * Given a set of historical points, can I bring in data from even further in history to enrich my data for classification?
     * First thought for this is to take data from 24 hours prior (roughly 1480 positions in the past) and add that to the data row
     * and let the classifier determine it's value.  Maybe get points from more than just the last day?  Can try several methods and 
     * plug them into weka and see what happens
     */
    //TODO: come up with method sig here

    /**
     * Determine what action should be performed given current state and position in state
     * 
     * @param state Representation of the current state of the application
     * @param rhPos Current position in the state's rate history list (for production this will always be histories.last)
     * @param previousAction Action decided on by previous classifier, null if first algo
     */
    MancerAction shouldPerform(MancerState state, int rhPos, MancerAction previousAction);

    // This implementation creates a J48 decision tree
    // TODO: make this a real interface that allows for different classifiers
    Classifier buildAndTrainClassifier(Instances trainingSet);

    Instances generateDataSet(MancerState state, String relationshipName, double percentToKeep);

    void setClassifier(Classifier c);
}