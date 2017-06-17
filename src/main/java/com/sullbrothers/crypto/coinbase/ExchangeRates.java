package com.sullbrothers.crypto.coinbase;

import org.json.JSONObject;
import java.util.*;

public class ExchangeRates{
    private Map<String, Double> exchangeRates = new HashMap<String, Double>();
    private Date timestamp;

    public ExchangeRates(String currency) {
        CoinbaseConnection coinbaseConnection = new CoinbaseConnection();
        timestamp = new Date();
        System.out.println(timestamp);
        JSONObject exchangeRatesJson = coinbaseConnection.getExchangeRates(currency);

        JSONObject exchangeRatesJsonData = exchangeRatesJson.getJSONObject("data");
        JSONObject exchangeRatesJsonDataRates = exchangeRatesJsonData.getJSONObject("rates");

        Iterator<String> keys = exchangeRatesJsonDataRates.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                this.exchangeRates.put(key, exchangeRatesJsonDataRates.getDouble(key));
                System.out.println(key + " = " + exchangeRatesJsonDataRates.getDouble(key));
            } catch(Exception e) {
                
            }
        }
    }

    public Map<String, Double> getExchageRates() {
        return exchangeRates;
    }  

    public Date getTimestamp() {
        return timestamp;
    }
}