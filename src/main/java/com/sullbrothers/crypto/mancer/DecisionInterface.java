package com.sullbrothers.crypto.mancer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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

    public DecisionInterface () {
        
    }

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

            currencies.add(er.getCurrency());
        }

        // Add the classification attribute
        ArrayList<String> classifications = new ArrayList<String>();
        classifications.add("NONE");
        int i = 0;
        while(i < currencies.size()){
            int j = i+1;
            while(j < currencies.size()){
                classifications.add(currencies.get(i) + ":" + currencies.get(j));
                j++;
            }
            i++;
        }

        dataVector.add(new Attribute("CLASSIFICATION", classifications));

        Instances data = new Instances(relationshipName, dataVector, 10);
        data.setClassIndex(classifications.size());


        // Now populate the dataset
        for(RateHistory rh : state.historicalRates){
            Instance toAdd = new SparseInstance(classifications.size());
            i = 0;
            for(String curr : currencies){
                // i is currency rate
                toAdd.setValue(dataVector.get(i), rh.getRates().getExchangeRateByCurrency(curr).getPrice());

                // i + 1 is currency amount                
                // we're not actually storing this at the moment so I'll have to spoof it for now
                toAdd.setValue(dataVector.get(i+1), getAmountForCurrency(curr));
            }
            // Using sparse instance to ignore classification for now
            // toAdd.setValue(dataVector.get(classifications.size()), "?");
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