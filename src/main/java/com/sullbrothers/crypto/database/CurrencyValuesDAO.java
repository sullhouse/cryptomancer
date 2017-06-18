package com.sullbrothers.crypto.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
/**
 * CurrencyValuesDAO
 */
public class CurrencyValuesDAO {

    private static String TABLE_NAME = "currency_values";
    private static String GET_BASE_STMT = "SELECT * FROM " + TABLE_NAME;
    private static String PUT_BASE_STMT = "INSERT INTO " + TABLE_NAME + " (date, BTC, ETH, LTC, USD) VALUES('%s', %s,%s,%s,%s) ON DUPLICATE KEY UPDATE date='%s', BTC=%s, ETH=%s, LTC=%s, USD=%s;";

    private Map<String, Double> currencyValues;

    public CurrencyValuesDAO () throws SQLException {
        System.out.println("Running query: " + String.format(GET_BASE_STMT));
        ResultSet rs = CryptomancerDatabase.runQuery(String.format(GET_BASE_STMT));
        this.currencyValues = new HashMap<String, Double>();
        while(rs.next()){
            for(int i = 3; i <= rs.getMetaData().getColumnCount(); i++){
                this.currencyValues.put(rs.getMetaData().getColumnName(i), rs.getDouble(i));
            }
        }
    }

    public double getValueForCurrency(String currency){
        return this.currencyValues.get(currency);
    }

    public void setValueForCurrency(String currency, double value){
        this.currencyValues.put(currency, value);
    }

    public Set<String> getCurrencies(){
        return this.currencyValues.keySet();
    }

    public String toString(){
        StringBuilder toReturn = new StringBuilder("\"CURRENCY_VALUES\": {");
        
        for(String currency : this.getCurrencies()){
            toReturn.append("\"" + currency + "\": \"" + this.getValueForCurrency(currency) + "\", ");
        }
        toReturn.append("}");
        return toReturn.toString();
    }
}