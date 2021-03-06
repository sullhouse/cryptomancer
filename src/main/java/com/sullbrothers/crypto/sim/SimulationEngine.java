package com.sullbrothers.crypto.sim;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.sullbrothers.crypto.database.CurrencyValuesDAO;
import com.sullbrothers.crypto.database.RateHistoryDAO;
import com.sullbrothers.crypto.mancer.DecisionSuite;
import com.sullbrothers.crypto.mancer.MancerAction;
import com.sullbrothers.crypto.mancer.MancerState;

/**
 * SimulationEngine
 */
public class SimulationEngine {

    public static final double HISTORICAL_RATIO = .9;
    private static final int ITERATION_DELAY = 5;

    private MancerState state;
    private int simPosition;

    private static SimulationEngine instance;

    public SimulationEngine (Date simStart, Date simEnd) {
        try{
            List<RateHistoryDAO.RateHistory> rh = new RateHistoryDAO(simStart, simEnd).getAllHistoricalRates();
            this.simPosition = new Double(rh.size()*HISTORICAL_RATIO).intValue();
            this.state = new MancerState(new CurrencyValuesDAO(), rh.get(this.simPosition), rh);
        }
        catch(SQLException e){
            // TODO: handle exception in gathering currency values
        }
    }

    public static String runSimulation(DecisionSuite ds){
        StringBuilder toReturn = new StringBuilder();

        // Starting with some seed money for test purposes
        getInstance().state.currencyValues.setValueForCurrency("USD", 0);
        getInstance().state.currencyValues.setValueForCurrency("ETH", 110);
        getInstance().state.currencyValues.setValueForCurrency("BTC", 51.5);
        getInstance().state.currencyValues.setValueForCurrency("LTC", 105);
        getInstance().state.setCurrentRatePosition(getInstance().simPosition);
        double initialValue = getInstance().state.getTotalValue();
        toReturn.append("Initial state: " + getInstance().state + "\n");

        while(getInstance().simPosition < getInstance().state.historicalRates.size()){
            // What happens if we only simulate every N minutes?
            if(getInstance().simPosition%ITERATION_DELAY != 0){
                getInstance().simPosition++;
                continue;
            }            
            getInstance().state.setCurrentRatePosition(getInstance().simPosition);
            MancerAction toPerform = ds.shouldPerform(getInstance().state, getInstance().simPosition++);
            toReturn.append("Performing action " + toPerform == null ? "NO ACTION" : toPerform);
            toReturn.append(", SUCCESS?: " + purchase(toPerform) + "\n");
        }
        toReturn.append("Final state: " + getInstance().state + "\n");
        double finalValue = getInstance().state.getTotalValue();
        toReturn.append("Final difference: " + (finalValue - initialValue) + "\n");
        getInstance().state.currencyValues.setValueForCurrency("USD", 0);
        getInstance().state.currencyValues.setValueForCurrency("ETH", 110);
        getInstance().state.currencyValues.setValueForCurrency("BTC", 51.5);
        getInstance().state.currencyValues.setValueForCurrency("LTC", 105);
        double unmodifiedFinalValue = getInstance().state.getTotalValue();
        toReturn.append("Difference between traded final and naive final: " + (finalValue - unmodifiedFinalValue) + "\n");
        toReturn.append("ratio of traded final to naive final: " + (finalValue/unmodifiedFinalValue));

        return toReturn.toString();
    }

    public static boolean purchase(MancerAction action){
        if(action == null){
            return true;
        }
        CurrencyValuesDAO cvs = getInstance().state.currencyValues;
        double buyRate = 1/(getInstance().state.currentRates.getRates().getExchangeRateByCurrency(action.currencyToBuy).getPrice());
        double payRate = 1/(getInstance().state.currentRates.getRates().getExchangeRateByCurrency(action.currencyToPay).getPrice());

        double buyValue = cvs.getValueForCurrency(action.currencyToBuy);
        double payValue = cvs.getValueForCurrency(action.currencyToPay);

        double adjPurchaseValue = action.amountToBuy*buyRate;
        double adjPaymentAmount = adjPurchaseValue/payRate;

        if(adjPaymentAmount > payValue){
            return false;
        }
        
        cvs.setValueForCurrency(action.currencyToBuy, buyValue + action.amountToBuy);
        cvs.setValueForCurrency(action.currencyToPay, payValue - adjPaymentAmount);
        
        return true;
    }

    private static SimulationEngine getInstance(){
        if(instance == null){
            instance = new SimulationEngine(Date.from(Instant.EPOCH), new Date());
        }
        return instance;
    }


}