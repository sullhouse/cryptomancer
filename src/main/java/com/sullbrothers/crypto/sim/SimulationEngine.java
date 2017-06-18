package com.sullbrothers.crypto.sim;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.sullbrothers.crypto.database.CurrencyValuesDAO;
import com.sullbrothers.crypto.database.RateHistoryDAO;
import com.sullbrothers.crypto.mancer.DecisionInterface;
import com.sullbrothers.crypto.mancer.MancerAction;
import com.sullbrothers.crypto.mancer.MancerState;

/**
 * SimulationEngine
 */
public class SimulationEngine {

    private final double HISTORICAL_RATIO = .75;

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

    public static String runSimulation(){
        StringBuilder toReturn = new StringBuilder();
        double initialValue = getInstance().state.getTotalValue();
        toReturn.append("Initial state: " + getInstance().state + "\n");

        while(getInstance().simPosition++ < getInstance().state.historicalRates.size()){
            MancerAction toPerform = DecisionInterface.shouldPerform(getInstance().state);
            toReturn.append("Performing action " + toPerform);
            toReturn.append(", SUCCESS?: " + purchase(toPerform) + "\n");
        }
        toReturn.append("Final state: " + getInstance().state + "\n");
        toReturn.append("Final difference: " + (getInstance().state.getTotalValue() - initialValue));
        return toReturn.toString();
    }

    public static boolean purchase(MancerAction action){
        CurrencyValuesDAO cvs = getInstance().state.currencyValues;
        double buyRate = getInstance().state.currentRates.getRates().get(action.currencyToBuy);
        double payRate = getInstance().state.currentRates.getRates().get(action.currencyToPay);

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