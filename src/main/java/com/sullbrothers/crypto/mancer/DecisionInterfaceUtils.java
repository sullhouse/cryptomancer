package com.sullbrothers.crypto.mancer;

import java.io.File;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * DecisionInterfaceUtils
 */
public class DecisionInterfaceUtils {
        

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
        return new MancerAction(currencies[0], currencies[1], 2);
    }
}