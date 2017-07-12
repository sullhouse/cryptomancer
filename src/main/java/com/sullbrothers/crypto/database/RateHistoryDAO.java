package com.sullbrothers.crypto.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.sullbrothers.crypto.exchangerates.ExchangeRate;
import com.sullbrothers.crypto.exchangerates.ExchangeRates;

import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * RateHistoryDAO
 */
public class RateHistoryDAO {

    private static String GET_BASE_STMT = "SELECT rate_history_id, date, BTC, ETH, LTC, USD FROM rate_history WHERE DATE >= '%s' AND DATE <= '%s'";
    private static String GET_BASE_STMT_SOURCE = "SELECT rate_history_id, date, BTC, ETH, LTC, USD FROM rate_history WHERE DATE >= '%s' AND DATE <= '%s' and source='%s'";
    private static String PUT_BASE_STMT = "INSERT INTO rate_history (date, BTC, ETH, LTC, USD) VALUES('%s', %s,%s,%s,%s) ON DUPLICATE KEY UPDATE date='%s', BTC=%s, ETH=%s, LTC=%s, USD=%s;";
    private static String PUT_BASE_STMT_SOURCE = "INSERT INTO rate_history (date, BTC, ETH, LTC, USD, source) VALUES('%s', %s,%s,%s,%s,'%s') ON DUPLICATE KEY UPDATE date='%s', BTC=%s, ETH=%s, LTC=%s, USD=%s, source='%s';";

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
            rateHistory.add(new RateHistory(rhDate, new ExchangeRates("", endDate, rhRates)));
        }
    }

    public RateHistoryDAO (Date startDate, Date endDate, String source) throws SQLException{
        String startDateStr = CryptomancerDatabase.getSqlDateString(startDate);
        String endDateStr = CryptomancerDatabase.getSqlDateString(endDate);
        System.out.println("Running query: " + String.format(GET_BASE_STMT_SOURCE, startDateStr, endDateStr, source));
        ResultSet rs = CryptomancerDatabase.runQuery(String.format(GET_BASE_STMT_SOURCE, startDateStr, endDateStr, source));
        rateHistory = new ArrayList<RateHistory>();
        while(rs.next()){
            Date rhDate = rs.getDate(2);
            List<ExchangeRate> rhRates = new ArrayList<ExchangeRate>();
            for(int i = 3; i <= rs.getMetaData().getColumnCount(); i++){
                ExchangeRate rhRate = new ExchangeRate(rs.getMetaData().getColumnName(i));
                rhRate.setPrice(rs.getDouble(i));
                rhRates.add(rhRate);
            }
            rateHistory.add(new RateHistory(rhDate, new ExchangeRates("", endDate, rhRates), source));
        }
    }

    public RateHistoryDAO (Date timestamp, ExchangeRates rates) throws SQLException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        ExchangeRate er;
        String timestampStr = df.format(timestamp);
        Double BTC = ((er = rates.getExchangeRateByCurrency("BTC")) != null) ? er.getPrice() : 0.0;
        Double ETH = ((er = rates.getExchangeRateByCurrency("ETH")) != null) ? er.getPrice() : 0.0;
        Double LTC = ((er = rates.getExchangeRateByCurrency("LTC")) != null) ? er.getPrice() : 0.0;
        Double USD = ((er = rates.getExchangeRateByCurrency("USD")) != null) ? er.getPrice() : 0.0;

        System.out.println("Running query: " + String.format(PUT_BASE_STMT, timestampStr, BTC, ETH, LTC, USD, timestampStr, BTC, ETH, LTC, USD));
        int rateHistoryId = CryptomancerDatabase.runUpdate(String.format(PUT_BASE_STMT, timestampStr, BTC, ETH, LTC, USD, timestampStr, BTC, ETH, LTC, USD));

        for (ExchangeRate r : rates.getExchangeRates()) {
            CryptomancerDatabase.runUpdate(getPutDetailStatement(rateHistoryId, r));
        }

        RateHistory rh = new RateHistory(timestamp, rates);

        rateHistory = new ArrayList<RateHistory>();
        rateHistory.add(rh);
    }

    public RateHistoryDAO (Date timestamp, ExchangeRates rates, String source) throws SQLException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        ExchangeRate er;
        String timestampStr = df.format(timestamp);
        Double BTC = ((er = rates.getExchangeRateByCurrency("BTC")) != null) ? er.getPrice() : 0.0;
        Double ETH = ((er = rates.getExchangeRateByCurrency("ETH")) != null) ? er.getPrice() : 0.0;
        Double LTC = ((er = rates.getExchangeRateByCurrency("LTC")) != null) ? er.getPrice() : 0.0;
        Double USD = ((er = rates.getExchangeRateByCurrency("USD")) != null) ? er.getPrice() : 0.0;

        System.out.println("Running query: " + String.format(PUT_BASE_STMT_SOURCE, timestampStr, BTC, ETH, LTC, USD, source, timestampStr, BTC, ETH, LTC, USD, source));
        int rateHistoryId = CryptomancerDatabase.runUpdate(String.format(PUT_BASE_STMT_SOURCE, timestampStr, BTC, ETH, LTC, USD, source, timestampStr, BTC, ETH, LTC, USD, source));

        for (ExchangeRate r : rates.getExchangeRates()) {
        	if (!r.getCurrency().equalsIgnoreCase(r.getFromCurrency())) {
        		CryptomancerDatabase.runUpdate(getPutDetailStatementWithSource(rateHistoryId, r, source));
        	}
        }

        RateHistory rh = new RateHistory(timestamp, rates);

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
        if (exchangeRate.getMarket()!=null) s +=  ",'" + exchangeRate.getMarket() + "'";
        if (exchangeRate.getFlags()>0) s +=  "," + exchangeRate.getFlags();
        if (exchangeRate.getLastUpdate()!=null) s +=  ",'" + df.format(exchangeRate.getLastUpdate()) + "'";
        if (exchangeRate.getLastVolume()!=null) s +=  "," + exchangeRate.getLastVolume();
        if (exchangeRate.getLastVolumeTo()!=null) s +=  "," + exchangeRate.getLastVolumeTo();
        if (exchangeRate.getLastTradeId()>0) s +=  "," + exchangeRate.getLastTradeId();
        if (exchangeRate.getVolume24hour()!=null) s +=  "," + exchangeRate.getVolume24hour();
        if (exchangeRate.getVolume24hourTo()!=null) s +=  "," + exchangeRate.getVolume24hourTo();
        if (exchangeRate.getOpen24hour()!=null) s +=  "," + exchangeRate.getOpen24hour();
        if (exchangeRate.getHigh24hour()!=null) s +=  "," + exchangeRate.getHigh24hour();
        if (exchangeRate.getLow24hour()!=null) s +=  "," + exchangeRate.getLow24hour();
        if (exchangeRate.getLastMarket()!=null) s +=  ",'" + exchangeRate.getLastMarket() + "'";
        if (exchangeRate.getChange24hour()!=null) s +=  "," + exchangeRate.getChange24hour();
        if (exchangeRate.getChangePct24hour()!=null) s +=  "," + exchangeRate.getChangePct24hour();
        if (exchangeRate.getSupply()!=null) s +=  "," + exchangeRate.getSupply();
        if (exchangeRate.getMarketCap()!=null) s +=  "," + exchangeRate.getMarketCap();
        s += ");";
        System.out.println("SQL to update details: " + s);
        return s;
    }

    private String getPutDetailStatementWithSource(int rateHistoryId, ExchangeRate exchangeRate, String source) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String s = "";
        s += "INSERT INTO rate_history_detail ("
            + "rate_history_id,"
            + "currency,"
            + "from_currency,"
            + "price,"
            + "source";
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
        s += exchangeRate.getPrice() + ",";
        s += "'" + source + "'";
        if (exchangeRate.getType()>0) s +=  "," + exchangeRate.getType();
        if (exchangeRate.getMarket()!=null) s +=  ",'" + exchangeRate.getMarket() + "'";
        if (exchangeRate.getFlags()>0) s +=  "," + exchangeRate.getFlags();
        if (exchangeRate.getLastUpdate()!=null) s +=  ",'" + df.format(exchangeRate.getLastUpdate()) + "'";
        if (exchangeRate.getLastVolume()!=null) s +=  "," + exchangeRate.getLastVolume();
        if (exchangeRate.getLastVolumeTo()!=null) s +=  "," + exchangeRate.getLastVolumeTo();
        if (exchangeRate.getLastTradeId()>0) s +=  "," + exchangeRate.getLastTradeId();
        if (exchangeRate.getVolume24hour()!=null) s +=  "," + exchangeRate.getVolume24hour();
        if (exchangeRate.getVolume24hourTo()!=null) s +=  "," + exchangeRate.getVolume24hourTo();
        if (exchangeRate.getOpen24hour()!=null) s +=  "," + exchangeRate.getOpen24hour();
        if (exchangeRate.getHigh24hour()!=null) s +=  "," + exchangeRate.getHigh24hour();
        if (exchangeRate.getLow24hour()!=null) s +=  "," + exchangeRate.getLow24hour();
        if (exchangeRate.getLastMarket()!=null) s +=  ",'" + exchangeRate.getLastMarket() + "'";
        if (exchangeRate.getChange24hour()!=null) s +=  "," + exchangeRate.getChange24hour();
        if (exchangeRate.getChangePct24hour()!=null) s +=  "," + exchangeRate.getChangePct24hour();
        if (exchangeRate.getSupply()!=null) s +=  "," + exchangeRate.getSupply();
        if (exchangeRate.getMarketCap()!=null) s +=  "," + exchangeRate.getMarketCap();
        s += ");";
        System.out.println("SQL to update details: " + s);
        return s;
    }

    public List<RateHistory> getAllHistoricalRates(){
        return this.rateHistory;
    }
    public static RateHistory getLatest(){
        return null;
    }

    /**
     * RateHistory
     */
    public class RateHistory {
    
        private Date date;
        private ExchangeRates rates;
        private String source;

        public RateHistory (Date date, ExchangeRates rates) {
            this.date = date;
            this.rates = rates;
            this.source = "";
        }

        public RateHistory (Date date, ExchangeRates rates, String source) {
            this.date = date;
            this.rates = rates;
            this.source = source;
            rates.setSource(source);
        }

        public Date getDate() {
            return this.date;
        }

        public ExchangeRates getRates(){
            return this.rates;
        }

        public String toString(){
            StringBuilder sb = new StringBuilder("{DATE: " + this.date.toString());
            for(ExchangeRate e : this.rates.getExchangeRates()) {
                sb.append(", " + e.getCurrency() + ": " + 1/e.getPrice());
            } 
            sb.append("}");
            return sb.toString();
        }

        public String getSource() {
            return this.source;
        }
    }
}