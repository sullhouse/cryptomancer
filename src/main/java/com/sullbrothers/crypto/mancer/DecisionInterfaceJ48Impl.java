package com.sullbrothers.crypto.mancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import com.sullbrothers.crypto.exchangerates.ExchangeRate;
import com.sullbrothers.crypto.database.RateHistoryDAO.RateHistory;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * DecisionInterface
 */
public class DecisionInterfaceJ48Impl implements DecisionInterface {

    // Singleton decision tree
    private Classifier CLASSIFIER;
    private Instances CLASSIFIER_DATASET;
    private List<String> CURRENCIES;
    private List<Attribute> DATA_VECTOR;
    private String[] CLASSIFICATIONS;

    /** This is the number of rate histories in between a data point and it's future "success" point */
    public static final int POSITIONS_TO_FUTURE = 1*60;
    // attempt to check future value for making predictions every 4 hours
    public static final int POSITIONS_TO_PREDICTION = 24*60;
    public static final int[] HISTORICAL_INDICES = new int[]{
        -12*60,
        -24*60,
        -36*60,
        -48*60,
        -72*60,
        -120*60
    };

    public static final HashMap<String, Double> TEST_CURRENCY_AMOUNTS;
    static{
    	TEST_CURRENCY_AMOUNTS = new HashMap<String, Double>();
    	TEST_CURRENCY_AMOUNTS.put("USD", 10.0);
    	TEST_CURRENCY_AMOUNTS.put("BTC", 4.0);
    	TEST_CURRENCY_AMOUNTS.put("LTC", 20.0);
    	TEST_CURRENCY_AMOUNTS.put("ETH", 2.0);
    }

    public DecisionInterfaceJ48Impl () {
        
    }

    /**
     * Given a set of historical points, can I tell if performing an action on those points would have been beneficial,
     * and can I also tell to what extent?
     * 
     * First pass at this method will return the action that is predicted
     */
    public String predictResult(MancerState state, int rateHistoryPosition){
        if(rateHistoryPosition + POSITIONS_TO_FUTURE >= state.historicalRates.size()){
            return null;
        }
        RateHistory future = state.historicalRates.get(rateHistoryPosition + POSITIONS_TO_FUTURE);
        RateHistory curr = state.historicalRates.get(rateHistoryPosition);

        // Can check my math here, but the change in value for buying one currency with another should be like this:
        // (currA)*Arate + (currB)*Brate = current
        // (currA + (Brate/Arate) * delta)*Arate' + (currB - delta)*Brate' = new
        // taking the first derivative with respect to delta:
        // ((Brate*Arate')/Arate) - Brate' = dPrice/dt
        //
        // Changing my math approach.  new take is:
        // relative number of new shares = N' = Arate/Brate
        // relative value of new shares = P' = Brate'/Arate'
        // number of current shares * current value normalized = 1
        // factor of change = N'P' - 1
        Map<String, Double> actionValues = new HashMap<String, Double>();
        String[] currencies = state.currencyValues.getCurrencies().toArray(new String[0]);
        for(int i = 0; i < currencies.length; i++){
            for(int j = 0; j < currencies.length; j++){
                if(i == j){
                    continue;
                }
                String buyCurrency = currencies[i];
                String sellCurrency = currencies[j];
                String action = buyCurrency + ":" + sellCurrency;
                if(buyCurrency.equalsIgnoreCase("usd") || sellCurrency.equalsIgnoreCase("usd")){
                    continue;
                }
                int futurePos = rateHistoryPosition + POSITIONS_TO_FUTURE;
                ArrayList<Double> currencyFutureAverage = new ArrayList<Double>();
                while(futurePos < state.historicalRates.size()){
                    future = state.historicalRates.get(futurePos);
                    double currBuyRate = 1/(curr.getRates().getExchangeRateByCurrency(buyCurrency).getPrice());
                    double currSellRate = 1/(curr.getRates().getExchangeRateByCurrency(sellCurrency).getPrice());
                    double futureBuyRate = 1/(future.getRates().getExchangeRateByCurrency(buyCurrency).getPrice());
                    double futureSellRate = 1/(future.getRates().getExchangeRateByCurrency(sellCurrency).getPrice());
                    double dValue = (currSellRate*futureBuyRate)/(currBuyRate*futureSellRate) - 1;
                    currencyFutureAverage.add(dValue);
                    futurePos += POSITIONS_TO_FUTURE;
                }
                OptionalDouble dValue = currencyFutureAverage.stream().mapToDouble(a -> a).average();
                actionValues.put(action, dValue.isPresent() ? dValue.getAsDouble() : -1);
            }
        }

        // TODO: potentially increase this to something to reflect the desire
        // to only trade when we make more than a certain amount (now set to 10% increase)
        double maxDelta = 0.05;
        String action = "NONE";
        System.out.println("Rate " + rateHistoryPosition + " future values list: " + actionValues.toString());
        for(String k : actionValues.keySet()){
            if(actionValues.get(k) > maxDelta){
                maxDelta=actionValues.get(k);
                action = k;
            }
        }

        return action;
    }

    public Instance getInstanceFromRateHistory(List<RateHistory> historicalRates, List<Attribute> dataVector, List<String> currencies, int rhPos){
        if(currencies == null){
            if(CURRENCIES != null){
                currencies = CURRENCIES;
            }else{
                currencies = new ArrayList<String>();
                for(ExchangeRate er : historicalRates.get(0).getRates().getExchangeRates()){
                    currencies.add(er.getCurrency());
                }
            }
        }
        if(dataVector == null){
            if(DATA_VECTOR != null){
                dataVector = DATA_VECTOR;
            }
            // else panic?
        }
        RateHistory rh = historicalRates.get(rhPos);
        Instance toReturn = new SparseInstance(dataVector.size());
        int i = 0;
        for(String curr : currencies){
            // i is currency rate
            toReturn.setValue(dataVector.get(i++), 1/(rh.getRates().getExchangeRateByCurrency(curr).getPrice()));

            // i + 1 is currency amount                
            // we're not actually storing this at the moment so I'll have to spoof it for now
            toReturn.setValue(dataVector.get(i++), getAmountForCurrency(curr));

            // add historical rates
            for(int n = 0; n < HISTORICAL_INDICES.length; n++){
                if(rhPos + HISTORICAL_INDICES[n] >= 0){
                    // toReturn.setValue(dataVector.get(i+n), 1/(historicalRates.get(rhPos+HISTORICAL_INDICES[n]).getRates().getExchangeRateByCurrency(curr).getPrice()));
                    // what if instead of the rate at that time we did a diff with current rate?
                    double tempCurrRate = 1/(rh.getRates().getExchangeRateByCurrency(curr).getPrice());
                    double tempHistRate = 1/(historicalRates.get(rhPos+HISTORICAL_INDICES[n]).getRates().getExchangeRateByCurrency(curr).getPrice());
                    toReturn.setValue(dataVector.get(i+n), (tempHistRate - tempCurrRate)/tempCurrRate);
                }
            }
            i = i + HISTORICAL_INDICES.length;
        }
        return toReturn;
    }

    /**
     * Given a set of historical points, can I bring in data from even further in history to enrich my data for classification?
     * First thought for this is to take data from 24 hours prior (roughly 1480 positions in the past) and add that to the data row
     * and let the classifier determine it's value.  Maybe get points from more than just the last day?  Can try several methods and 
     * plug them into weka and see what happens
     */
    //TODO: come up with method sig here

    public MancerAction shouldPerform(MancerState state, int rhPos, MancerAction previousAction){
        // NOTE: previousAction is unused here as this will be our first algo
        if(CLASSIFIER == null){
            // TODO: decide how to initialize our classifiers outside of test cases
            return MancerAction.getRandomAction();
        }
        if(CLASSIFIER_DATASET == null){
            CLASSIFIER_DATASET = new Instances("mancerTest", (ArrayList<Attribute>)DATA_VECTOR, 10);
            CLASSIFIER_DATASET.setClassIndex(DATA_VECTOR.size()-1);
        }
        try{
            Instance instance = getInstanceFromRateHistory(state.historicalRates, null, null, rhPos);
            instance.setDataset(CLASSIFIER_DATASET);
            int pos = new Double(((J48)CLASSIFIER).classifyInstance(instance)).intValue();
            /** The below block of code gets the full probability distribution for prediction
            double[] classDistribution = ((J48)CLASSIFIER).distributionForInstance(instance);
            double maxProb = .05;
            int pos = -1;
            for(int i = 0; i < classDistribution.length; i++){
                if(classDistribution[i] > maxProb){
                    maxProb = classDistribution[i];
                    pos = i;
                }
            }
            */
            if(pos >= 0){
                System.out.println("Returning Action " + DecisionInterfaceUtils.parseClassification(CLASSIFICATIONS[pos]) + " for the following input:");
                System.out.println("Input: " + state.historicalRates.get(rhPos));
                System.out.println("Instance: " + instance);
                System.out.println("Predicted Class: " + CLASSIFICATIONS[pos]);
                /**
                StringBuilder sb = new StringBuilder("Classifications: {");
                for(int i = 0; i < classDistribution.length; i++){
                    sb.append("\""+CLASSIFICATIONS[i]+"\":" + classDistribution[i] + ", ");
                }
                sb.replace(sb.length()-2, sb.length(), "");
                sb.append("}");
                System.out.println(sb);
                */
                return DecisionInterfaceUtils.parseClassification(CLASSIFICATIONS[pos]);
            }
            System.out.println("Determined that no actions were within threshold.  Returning null");
            return null;
        }catch(Exception e){
            System.out.println("Caught exception attempting to classify an action");
            e.printStackTrace();
            return null;
        }
    }

    // This implementation creates a J48 decision tree
    // TODO: make this a real interface that allows for different classifiers
    public Classifier buildAndTrainClassifier(Instances trainingSet){
        Classifier toReturn = (Classifier)new J48();

        try{
            toReturn.buildClassifier(trainingSet);
        }
        catch(Exception e){
            System.out.println("ERROR building and training classifier");
            e.printStackTrace();
            return null;
        }

        return toReturn;
    }

    public Instances generateDataSet(MancerState state, String relationshipName, double percentToKeep){
        ArrayList<Attribute> dataVector = new ArrayList<Attribute>();

        ArrayList<String> currencies = new ArrayList<String>();

        for (ExchangeRate er : state.currentRates.getRates().getExchangeRates()) {
            // Add a currency attribute for current rate
            Attribute r = new Attribute(er.getCurrency() + "_RATE");
            dataVector.add(r);

            // Add a currency value for currently held amount
            Attribute a = new Attribute(er.getCurrency() + "_AMOUNT");
            dataVector.add(a);

            // Add attributes for historical data
            // For each of the historical periods we will add as many attributes as were added above
            for(int i = 0; i < HISTORICAL_INDICES.length; i++){
                dataVector.add(new Attribute(er.getCurrency() + "_HIST_RATE_" + i));
            }

            System.out.printf("currency:%s, fromCurrency:%s\n", er.getCurrency(), er.getFromCurrency());
            currencies.add(er.getCurrency());
        }
        CURRENCIES = currencies;
        // Add the classification attribute
        ArrayList<String> classifications = new ArrayList<String>();
        System.out.println("Adding possible classification \"NONE\"");
        classifications.add("NONE");
        int i = 0;
        while(i < currencies.size()){
            int j = 0;
            while(j < currencies.size()){
                if(i == j){
                    j++;
                    continue;
                }
                System.out.println("Adding possible classification \"" + currencies.get(i) + ":" + currencies.get(j) + "\"");                
                classifications.add(currencies.get(i) + ":" + currencies.get(j));
                j++;
            }
            i++;
        }

        CLASSIFICATIONS = classifications.toArray(new String[0]);

        dataVector.add(new Attribute("CLASSIFICATION", classifications));
        // TODO: determine weights of individual attributes.  For now all are 1
        dataVector.forEach(a -> a.setWeight(1.0));
        DATA_VECTOR = dataVector;

        Instances data = new Instances(relationshipName, dataVector, 10);
        data.setClassIndex(dataVector.size()-1);


        // Now populate the dataset
        for(int rhPos = 0; rhPos < state.historicalRates.size()*percentToKeep; rhPos++){
            Instance toAdd = getInstanceFromRateHistory(state.historicalRates, dataVector, currencies, rhPos);

            // Using sparse instance to ignore classification for now
            RateHistory rh = state.historicalRates.get(rhPos);
            String action = predictResult(state, rhPos);
            if(action != null){
                System.out.println("Setting class attribute to " + action + " for " + rh.toString());
                toAdd.setValue(dataVector.get(dataVector.size()-1), action);
            }
            data.add(toAdd);
        }

        return data;
    }

    public double getAmountForCurrency(String currency){
        if(TEST_CURRENCY_AMOUNTS.containsKey(currency)){
            return TEST_CURRENCY_AMOUNTS.get(currency);
        }
        return 0;
    }

    public void setClassifier(Classifier c){
        CLASSIFIER = c;
    }
}