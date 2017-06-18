package com.sullbrothers.crypto.mancer;

import java.util.List;

import com.sullbrothers.crypto.database.CurrencyValuesDAO;
import com.sullbrothers.crypto.database.RateHistoryDAO;

/**
 * MancerState
 */
public class MancerState {

    public CurrencyValuesDAO currencyValues;
    public RateHistoryDAO.RateHistory currentRates;
    public List<RateHistoryDAO.RateHistory> historicalRates;

    public MancerState (CurrencyValuesDAO cv, RateHistoryDAO.RateHistory cr, List<RateHistoryDAO.RateHistory> hr) {
        this.currencyValues = cv;
        this.currentRates = cr;
        this.historicalRates = hr;
    }

    public double checkAgainst(MancerState other){
        return this.getTotalValue() - other.getTotalValue();
    }

    public double getTotalValue(){
        double toReturn = 0;
        for(String s : this.currencyValues.getCurrencies()){
            toReturn += this.currencyValues.getValueForCurrency(s)*this.currentRates.getRates().getExchangeRateByCurrency(s).getPrice();
        }
        return toReturn;
    }

    public String toString(){
        return "\"STATE\": {" + currencyValues + ", \"CURRENT_RATES\": " + currentRates + "}";
    }
}