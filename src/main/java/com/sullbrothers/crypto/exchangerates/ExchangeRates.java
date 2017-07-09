package com.sullbrothers.crypto.exchangerates;

import java.util.*;

public class ExchangeRates{
    private List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
    private Date timestamp;
    private String currency;
    private String source;

    public ExchangeRates(String currency) {
        this.currency = currency;
    }

    public ExchangeRates(String currency, Date timestamp, List<ExchangeRate> exchangeRates){
        this.exchangeRates = exchangeRates;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    public void getExchangeRatesFromCoinbase() {
        this.source = "COINBASE";
        CoinbaseConnection coinbaseConnection = new CoinbaseConnection();
        timestamp = new Date();
        System.out.println(timestamp);

        this.exchangeRates = coinbaseConnection.getExchangeRates(this.currency);
    }

    public void getExchangeRatesFromCryptoCompare(String toCurrencies) {
        this.source = "CRYPTOCOMPARE";
        CryptoCompareConnection cryptoCompareConnection = new CryptoCompareConnection();
        timestamp = new Date();
        System.out.println(timestamp);
        
        this.exchangeRates = cryptoCompareConnection.getExchangeRates(this.currency, toCurrencies);
    }

    public void getExchangeRatesFromShapeshift(String toCurrencies) {
        this.source = "SHAPESHIFT";
        ShapeShiftConnection shapeShiftConnection = new ShapeShiftConnection();
        timestamp = new Date();
        System.out.println(timestamp);

        this.exchangeRates = shapeShiftConnection.getExchangeRates(this.currency, toCurrencies);
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

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}