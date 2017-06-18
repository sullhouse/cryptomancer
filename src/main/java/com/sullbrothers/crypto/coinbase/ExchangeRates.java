package com.sullbrothers.crypto.coinbase;

import org.json.JSONObject;
import java.util.*;

public class ExchangeRates{
    private List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
    private Date timestamp;
    private String currency;

    public ExchangeRates(String currency) {
        this.currency = currency;
    }
    public void getExchangeRatesFromCoinbase() {
        CoinbaseConnection coinbaseConnection = new CoinbaseConnection();
        timestamp = new Date();
        System.out.println(timestamp);
        JSONObject exchangeRatesJson = coinbaseConnection.getExchangeRates(this.currency);

        JSONObject exchangeRatesJsonData = exchangeRatesJson.getJSONObject("data");
        JSONObject exchangeRatesJsonDataRates = exchangeRatesJsonData.getJSONObject("rates");

        Iterator<String> keys = exchangeRatesJsonDataRates.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                ExchangeRate exchangeRate = new ExchangeRate(key);
                exchangeRate.setFromCurrency(this.currency);
                exchangeRate.setPrice(exchangeRatesJsonDataRates.getDouble(key));
                this.exchangeRates.add(exchangeRate);
                System.out.println(key + " = " + exchangeRatesJsonDataRates.getDouble(key));
            } catch(Exception e) {
                
            }
        }
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }  

    public Date getTimestamp() {
        return timestamp;
    }

    public ExchangeRate getExchangeRateByCurrency(String currency) {
        for (ExchangeRate e : exchangeRates) {
            if (e.getCurrency().equalsIgnoreCase(currency)) return e;
        }
        return null;
    }
}