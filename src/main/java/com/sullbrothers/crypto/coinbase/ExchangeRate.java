package com.sullbrothers.crypto.coinbase;

import java.util.Date;

public class ExchangeRate {
    private String currency;
    private String fromCurrency;
    private int type;
    private String market;
    private int flags;
    private Double price;
    private Date lastUpdate;
    private Double lastVolume;
    private Double lastVolumeTo;
    private int lastTradeId;
    private Double volume24hour;
    private Double volume24hourTo;
    private Double open24hour;
    private Double high24hour;
    private Double low24hour;
    private String lastMarket;
    private Double change24hour;
    private Double changePct24hour;
    private Double supply;
    private Double marketCap;


    public ExchangeRate(String currency) {
        this.currency = currency;
    }

    public void setChange24hour(Double change24hour) {
        this.change24hour = change24hour;
    }

    public void setChangePct24hour(Double changePct24hour) {
        this.changePct24hour = changePct24hour;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setHigh24hour(Double high24hour) {
        this.high24hour = high24hour;
    }

    public void setLastMarket(String lastMarket) {
        this.lastMarket = lastMarket;
    }

    public void setLastTradeId(int lastTradeId) {
        this.lastTradeId = lastTradeId;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastVolume(Double lastVolume) {
        this.lastVolume = lastVolume;
    }

    public void setLastVolumeTo(Double lastVolumeTo) {
        this.lastVolumeTo = lastVolumeTo;
    }

    public void setLow24hour(Double low24hour) {
        this.low24hour = low24hour;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public void setOpen24hour(Double open24hour) {
        this.open24hour = open24hour;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSupply(Double supply) {
        this.supply = supply;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setVolume24hour(Double volume24hour) {
        this.volume24hour = volume24hour;
    }

    public void setVolume24hourTo(Double volume24hourTo) {
        this.volume24hourTo = volume24hourTo;
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public Double getChange24hour() {
        return this.change24hour;
    }

    public Double getChangePct24hour() {
        return this.changePct24hour;
    }

    public Double getHigh24hour() {
        return this.high24hour;
    }

    public Double getLastVolume() {
        return this.lastVolume;
    }

    public Double getLastVolumeTo() {
        return this.lastVolumeTo;
    }

    public Double getLow24hour() {
        return this.low24hour;
    }

    public Double getMarketCap() {
        return this.marketCap;
    }

    public Double getOpen24hour() {
        return this.open24hour;
    }

    public Double getPrice() {
        return this.price;
    }

    public Double getSupply() {
        return this.supply;
    }

    public Double getVolume24hour() {
        return this.volume24hour;
    }

    public Double getVolume24hourTo() {
        return this.volume24hourTo;
    }

    public int getFlags() {
        return this.flags;
    }

    public int getLastTradeId() {
        return this.lastTradeId;
    }

    public int getType() {
        return this.type;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getFromCurrency() {
        return this.fromCurrency;
    }

    public String getLastMarket() {
        return this.lastMarket;
    }

    public String getMarket() {
        return this.market;
    }
}