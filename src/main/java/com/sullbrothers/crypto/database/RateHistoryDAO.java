package com.sullbrothers.crypto.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * RateHistoryDAO
 */
public class RateHistoryDAO {

    private static String BASE_STMT = "SELECT * FROM rate_history WHERE DATE >= '%s' AND DATE <= '%s'";

    private List<RateHistory> rateHistory;

    public RateHistoryDAO (Date startDate, Date endDate) throws SQLException{
        String startDateStr = CryptomancerDatabase.getSqlDateString(startDate);
        String endDateStr = CryptomancerDatabase.getSqlDateString(endDate);
        System.out.println("Running query: " + String.format(BASE_STMT, startDateStr, endDateStr));
        ResultSet rs = CryptomancerDatabase.runQuery(String.format(BASE_STMT, startDateStr, endDateStr));
        rateHistory = new ArrayList<RateHistory>();
        while(rs.next()){
            Date rhDate = rs.getDate(2);
            Map<String, Double> rhRates = new HashMap<String, Double>();
            for(int i = 3; i <= rs.getMetaData().getColumnCount(); i++){
                rhRates.put(rs.getMetaData().getColumnName(i), rs.getDouble(i));
            }
            rateHistory.add(new RateHistory(rhDate, rhRates));
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("RATE_HISTORIES[");
        for(RateHistory rh : this.rateHistory){
            sb.append(rh.toString() + ",");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * RateHistory
     */
    private class RateHistory {
    
        private Date date;
        private Map<String, Double> rates;

        public RateHistory (Date date, Map<String, Double> rates) {
            this.date = date;
            this.rates = rates;
        }

        public Date getDate() {
            return this.date;
        }

        public Map<String, Double> getRates(){
            return this.rates;
        }

        public void setRate(String currency, double rate){
            this.rates.put(currency, rate);
        }

        public String toString(){
            StringBuilder sb = new StringBuilder("{DATE: " + this.date.toString());
            for(String k : this.rates.keySet()){
                sb.append(", " + k.toUpperCase() + ": " + this.rates.get(k));
            } 
            sb.append("}");
            return sb.toString();
        }
    }
}