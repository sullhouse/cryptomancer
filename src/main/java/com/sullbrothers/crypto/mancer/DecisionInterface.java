package com.sullbrothers.crypto.mancer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sullbrothers.crypto.coinbase.ExchangeRate;
import com.sullbrothers.crypto.database.RateHistoryDAO.RateHistory;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;

/**
 * DecisionInterface
 */
public class DecisionInterface {

    /** This is the number of rate histories in between a data point and it's future "success" point */
    public static final int POSITIONS_TO_FUTURE = 24*60;

    public DecisionInterface () {
        
    }

    /**
     * Given a set of historical points, can I tell if performing an action on those points would have been beneficial,
     * and can I also tell to what extent?
     * 
     * First pass at this method will return the action that is predicted
     */
    public static String predictResult(MancerState state, int rateHistoryPosition){
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
        Map<String, Double> actionValues = new HashMap<String, Double>();
        String[] currencies = state.currencyValues.getCurrencies().toArray(new String[0]);
        for(int i = 0; i < currencies.length; i++){
            for(int j = 0; j < currencies.length; j++){
                if(i == j){
                    continue;
                }
                String buyCurrency = currencies[i];
                String sellCurrency = currencies[j];
                if(buyCurrency.equalsIgnoreCase("usd") || sellCurrency.equalsIgnoreCase("usd")){
                    continue;
                }
                double currBuyRate = curr.getRates().getExchangeRateByCurrency(buyCurrency).getPrice();
                double currSellRate = curr.getRates().getExchangeRateByCurrency(sellCurrency).getPrice();
                double futureBuyRate = future.getRates().getExchangeRateByCurrency(buyCurrency).getPrice();
                double futureSellRate = future.getRates().getExchangeRateByCurrency(sellCurrency).getPrice();
                double dValue = (currSellRate*futureBuyRate/currBuyRate) - futureSellRate;
                String action = buyCurrency + ":" + sellCurrency;
                actionValues.put(action, dValue);
            }
        }

        // TODO: potentially increase  this to something to reflect the desire
        // to only trade when we make more than a certain amount
        double maxDelta = 0;
        String action = "NONE";
        for(String k : actionValues.keySet()){
            if(actionValues.get(k) > maxDelta){
                maxDelta=actionValues.get(k);
                action = k;
            }
        }

        return action;
    }

    /**
     * Given a set of historical points, can I bring in data from even further in history to enrich my data for classification?
     * First thought for this is to take data from 24 hours prior (roughly 1480 positions in the past) and add that to the data row
     * and let the classifier determine it's value.  Maybe get points from more than just the last day?  Can try several methods and 
     * plug them into weka and see what happens
     */
    //TODO: come up with method sig here

    public static MancerAction shouldPerform(MancerState state){
        // TODO: do something brilliant here
        return MancerAction.getRandomAction();
    }

    public static Instances generateDataSet(MancerState state, String relationshipName){
        ArrayList<Attribute> dataVector = new ArrayList<Attribute>();

        ArrayList<String> currencies = new ArrayList<String>();

        for (ExchangeRate er : state.currentRates.getRates().getExchangeRates()) {
            // Add a currency attribute for current rate
            Attribute r = new Attribute(er.getCurrency() + "_RATE");
            dataVector.add(r);

            // Add a currency value for currently held amount
            Attribute a = new Attribute(er.getCurrency() + "_AMOUNT");
            dataVector.add(a);

            System.out.printf("currency:%s, fromCurrency:%s\n", er.getCurrency(), er.getFromCurrency());
            currencies.add(er.getCurrency());
        }

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

        dataVector.add(new Attribute("CLASSIFICATION", classifications));

        Instances data = new Instances(relationshipName, dataVector, 10);
        data.setClassIndex(dataVector.size()-1);


        // Now populate the dataset
        for(RateHistory rh : state.historicalRates){
            Instance toAdd = new SparseInstance(dataVector.size());
            i = 0;
            for(String curr : currencies){
                // i is currency rate
                toAdd.setValue(dataVector.get(i), rh.getRates().getExchangeRateByCurrency(curr).getPrice());

                // i + 1 is currency amount                
                // we're not actually storing this at the moment so I'll have to spoof it for now
                toAdd.setValue(dataVector.get(i+1), getAmountForCurrency(curr));
                i = i + 2;
            }
            // Using sparse instance to ignore classification for now
            String action = predictResult(state, state.historicalRates.indexOf(rh));
            if(action != null){
                System.out.println("Setting class attribute to " + action + " for " + rh.toString());
                toAdd.setValue(dataVector.get(dataVector.size()-1), action);
            }
            data.add(toAdd);
        }

        return data;
    }

    public static String saveInstancesToFile(Instances data, String filename){
        try{
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            File file = new File("C:/temp/cryptomancer/weka/" + filename);
            saver.setFile(file);
            saver.writeBatch();
            return file.getAbsolutePath();
        }
        catch(Exception e){
            return "FAILURE";            
        }
    }

    public static double getAmountForCurrency(String currency){
        if(TEST_CURRENCY_AMOUNTS.containsKey(currency)){
            return TEST_CURRENCY_AMOUNTS.get(currency);
        }
        return 0;
    }

    public static final HashMap<String, Double> TEST_CURRENCY_AMOUNTS;
    static{
    	TEST_CURRENCY_AMOUNTS = new HashMap<String, Double>();
    	TEST_CURRENCY_AMOUNTS.put("USD", 10.0);
    	TEST_CURRENCY_AMOUNTS.put("BTC", 4.0);
    	TEST_CURRENCY_AMOUNTS.put("LTC", 20.0);
    	TEST_CURRENCY_AMOUNTS.put("ETH", 2.0);
    }
    
    /**
     * Expectation for <code>classification</code> is format <CurrencyToBuy>:<CurrencyToSell>
     * Example: BTC:LTC means buy BTC using LTC
     * 
     * If Classification is empty or NONE, null will be returned indicating no action should 
     * be taken
     */
    public static MancerAction parseClassification(String classification) {
        if(classification == null || classification.equals("") || classification.equalsIgnoreCase("NONE")){
            return null;
        }
        String[] currencies = classification.split(":");
        return new MancerAction(currencies[0], currencies[1], 0);
    }
}