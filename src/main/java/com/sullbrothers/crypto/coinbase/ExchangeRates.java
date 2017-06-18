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

     public void getExchangeRatesFromCryptoCompare(String toCurrencies) {
        CryptoCompareConnection cryptoCompareConnection = new CryptoCompareConnection();
        timestamp = new Date();
        System.out.println(timestamp);
        JSONObject exchangeRatesJson = cryptoCompareConnection.getExchangeRates(this.currency, toCurrencies);

        JSONObject exchangeRatesJsonData = exchangeRatesJson.getJSONObject("RAW");
        JSONObject exchangeRatesJsonDataRates = exchangeRatesJsonData.getJSONObject(this.currency);

        Iterator<String> keys = exchangeRatesJsonDataRates.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                ExchangeRate exchangeRate = new ExchangeRate(key);
                JSONObject jRate = exchangeRatesJsonDataRates.getJSONObject(key);
                exchangeRate.setChange24hour(jRate.getDouble("CHANGE24HOUR"));
                exchangeRate.setChangePct24hour(jRate.getDouble("CHANGEPCT24HOUR"));
                exchangeRate.setFlags(jRate.getInt("FLAGS"));
                exchangeRate.setFromCurrency(this.currency);
                exchangeRate.setHigh24hour(jRate.getDouble("HIGH24HOUR"));
                exchangeRate.setLastMarket(jRate.getString("LASTMARKET"));
                exchangeRate.setLastTradeId(jRate.getInt("LASTTRADEID"));
                exchangeRate.setLastUpdate(getDateFromString(jRate.getLong("LASTUPDATE")));
                exchangeRate.setLastVolume(jRate.getDouble("LASTVOLUME"));
                exchangeRate.setLastVolumeTo(jRate.getDouble("LASTVOLUMETO"));
                exchangeRate.setLow24hour(jRate.getDouble("LOW24HOUR"));
                exchangeRate.setMarket(jRate.getString("MARKET"));
                exchangeRate.setMarketCap(jRate.getDouble("MKTCAP"));
                exchangeRate.setOpen24hour(jRate.getDouble("OPEN24HOUR"));
                exchangeRate.setPrice(jRate.getDouble("PRICE"));
                exchangeRate.setSupply(jRate.getDouble("SUPPLY"));
                exchangeRate.setType(jRate.getInt("TYPE"));
                exchangeRate.setVolume24hour(jRate.getDouble("VOLUME24HOUR"));
                exchangeRate.setVolume24hourTo(jRate.getDouble("VOLUME24HOURTO"));

                this.exchangeRates.add(exchangeRate);
                System.out.println(key + ": " + exchangeRate);
            } catch(Exception e) {
                e.printStackTrace();
                
            }
        }
        ExchangeRate exchangeRate = new ExchangeRate(this.currency);
        exchangeRate.setFromCurrency(this.currency);
        exchangeRate.setPrice(1.0);

        this.exchangeRates.add(exchangeRate);
        System.out.println(this.currency + ": " + exchangeRate);
    }

    private Date getDateFromString(Long s) {
        Date d = new Date(s * 1000);
        return d;
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