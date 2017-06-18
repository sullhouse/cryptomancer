package com.sullbrothers.crypto.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import com.sullbrothers.crypto.coinbase.ExchangeRate;
import com.sullbrothers.crypto.coinbase.ExchangeRates;

import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * RateHistoryDAO
 */
public class RateHistoryDAO {

    private static String GET_BASE_STMT = "SELECT * FROM rate_history WHERE DATE >= '%s' AND DATE <= '%s'";
    private static String PUT_BASE_STMT = "INSERT INTO rate_history (date, BTC, ETH, LTC, USD) VALUES('%s', %s,%s,%s,%s) ON DUPLICATE KEY UPDATE date='%s', BTC=%s, ETH=%s, LTC=%s, USD=%s;";

    private List<RateHistory> rateHistory;

    public RateHistoryDAO (Date startDate, Date endDate) throws SQLException{
        String startDateStr = CryptomancerDatabase.getSqlDateString(startDate);
        String endDateStr = CryptomancerDatabase.getSqlDateString(endDate);
        System.out.println("Running query: " + String.format(GET_BASE_STMT, startDateStr, endDateStr));
        ResultSet rs = CryptomancerDatabase.runQuery(String.format(GET_BASE_STMT, startDateStr, endDateStr));
        rateHistory = new ArrayList<RateHistory>();
        while(rs.next()){
            Date rhDate = rs.getDate(2);
            List<ExchangeRate> rhRates = new ArrayList<ExchangeRate>();
            for(int i = 3; i <= rs.getMetaData().getColumnCount(); i++){
                ExchangeRate rhRate = new ExchangeRate(rs.getMetaData().getColumnName(i));
                rhRate.setPrice(rs.getDouble(i));
                rhRates.add(rhRate);
            }
            rateHistory.add(new RateHistory(rhDate, rhRates));
        }
    }

    public RateHistoryDAO (Date timestamp, ExchangeRates rates) throws SQLException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String timestampStr = df.format(timestamp);
        Double BTC = rates.getExchangeRateByCurrency("BTC").getPrice();
        Double ETH = rates.getExchangeRateByCurrency("ETH").getPrice();
        Double LTC = rates.getExchangeRateByCurrency("LTC").getPrice();
        Double USD = rates.getExchangeRateByCurrency("USD").getPrice();

        System.out.println("Running query: " + String.format(PUT_BASE_STMT, timestampStr, BTC, ETH, LTC, USD, timestampStr, BTC, ETH, LTC, USD));
        int rateHistoryId = CryptomancerDatabase.runUpdate(String.format(PUT_BASE_STMT, timestampStr, BTC, ETH, LTC, USD, timestampStr, BTC, ETH, LTC, USD));

        CryptomancerDatabase.runUpdate(getPutDetailStatement(rateHistoryId, rates.getExchangeRateByCurrency("BTC")));
        CryptomancerDatabase.runUpdate(getPutDetailStatement(rateHistoryId, rates.getExchangeRateByCurrency("ETH")));
        CryptomancerDatabase.runUpdate(getPutDetailStatement(rateHistoryId, rates.getExchangeRateByCurrency("LTC")));
        CryptomancerDatabase.runUpdate(getPutDetailStatement(rateHistoryId, rates.getExchangeRateByCurrency("USD")));

        RateHistory rh = new RateHistory(timestamp, rates.getExchangeRates());

        rateHistory = new ArrayList<RateHistory>();
        rateHistory.add(rh);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("RATE_HISTORIES[");
        for(RateHistory rh : this.rateHistory){
            sb.append(rh.toString() + ",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String getPutDetailStatement(int rateHistoryId, ExchangeRate exchangeRate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String s = "";
        s += "INSERT INTO rate_history_detail ("
            + "rate_history_id,"
            + "currency,"
            + "from_currency,"
            + "price";
        if (exchangeRate.getType()>0) s += ",type"; 
        if (exchangeRate.getMarket()!=null) s += ",market";
        if (exchangeRate.getFlags()>0) s += ",flags";
        if (exchangeRate.getLastUpdate()!=null) s += ",last_update";
        if (exchangeRate.getLastVolume()!=null) s += ",last_volume";
        if (exchangeRate.getLastVolumeTo()!=null) s += ",last_volume_to";
        if (exchangeRate.getLastTradeId()>0) s += ",last_trade_id";
        if (exchangeRate.getVolume24hour()!=null) s += ",volume_24_hour";
        if (exchangeRate.getVolume24hourTo()!=null) s += ",volume_24_hour_to";
        if (exchangeRate.getOpen24hour()!=null) s += ",open_24_hour";
        if (exchangeRate.getHigh24hour()!=null) s += ",high_24_hour";
        if (exchangeRate.getLow24hour()!=null) s += ",low_24_hour";
        if (exchangeRate.getLastMarket()!=null) s += ",last_market";
        if (exchangeRate.getChange24hour()!=null) s += ",change_24_hour";
        if (exchangeRate.getChangePct24hour()!=null) s += ",change_percent_24_hour";
        if (exchangeRate.getSupply()!=null) s += ",supply";
        if (exchangeRate.getMarketCap()!=null) s += ",market_cap";
        s += ") VALUES ("
            + rateHistoryId + ",";
        s += "'" + exchangeRate.getCurrency() + "',";
        s += "'" + exchangeRate.getFromCurrency() + "',";
        s += exchangeRate.getPrice();
        if (exchangeRate.getType()>0) s +=  "," + exchangeRate.getType();
        if (exchangeRate.getMarket()!=null) s +=  ",''" + exchangeRate.getMarket() + "'";
        if (exchangeRate.getFlags()>0) s +=  "," + exchangeRate.getFlags();
        if (exchangeRate.getLastUpdate()!=null) s +=  ",''" + exchangeRate.getLastUpdate() + "'";
        if (exchangeRate.getLastVolume()!=null) s +=  "," + exchangeRate.getLastVolume();
        if (exchangeRate.getLastVolumeTo()!=null) s +=  "," + exchangeRate.getLastVolumeTo();
        if (exchangeRate.getLastTradeId()>0) s +=  "," + exchangeRate.getLastTradeId();
        if (exchangeRate.getVolume24hour()!=null) s +=  "," + exchangeRate.getVolume24hour();
        if (exchangeRate.getVolume24hourTo()!=null) s +=  "," + exchangeRate.getVolume24hourTo();
        if (exchangeRate.getOpen24hour()!=null) s +=  "," + exchangeRate.getOpen24hour();
        if (exchangeRate.getHigh24hour()!=null) s +=  "," + exchangeRate.getHigh24hour();
        if (exchangeRate.getLow24hour()!=null) s +=  "," + exchangeRate.getLow24hour();
        if (exchangeRate.getLastMarket()!=null) s +=  ",''" + exchangeRate.getLastMarket() + "'";
        if (exchangeRate.getChange24hour()!=null) s +=  "," + exchangeRate.getChange24hour();
        if (exchangeRate.getChangePct24hour()!=null) s +=  "," + exchangeRate.getChangePct24hour();
        if (exchangeRate.getSupply()!=null) s +=  "," + exchangeRate.getSupply();
        if (exchangeRate.getMarketCap()!=null) s +=  "," + exchangeRate.getMarketCap();
        s += ");";
        System.out.println("SQL to update details: " + s);
        return s;
    }
    public static RateHistory getLatest(){
        return null;
    }

    /**
     * RateHistory
     */
    private class RateHistory {
    
        private Date date;
        private List<ExchangeRate> rates;

        public RateHistory (Date date, List<ExchangeRate> rates) {
            this.date = date;
            this.rates = rates;
        }

        public Date getDate() {
            return this.date;
        }

        public List<ExchangeRate> getRates(){
            return this.rates;
        }

        public String toString(){
            StringBuilder sb = new StringBuilder("{DATE: " + this.date.toString());
            for(ExchangeRate e : this.rates) {
                sb.append(", " + e.getCurrency() + ": " + e.getPrice());
            } 
            sb.append("}");
            return sb.toString();
        }
    }
}