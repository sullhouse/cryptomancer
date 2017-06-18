package com.sullbrothers.crypto.app;

import java.sql.SQLException;
import java.util.Date;

import com.sullbrothers.crypto.coinbase.*;
import com.sullbrothers.crypto.database.*;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Running main application." );
        for(int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            switch (arg) {
                case "getrates":
                    try {
                        getLatestExchangeRates("BTC");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Ran into an issue getting latest exchange rates!");
                        //TODO: handle exception
                    }
                    break;
                default:
                    System.out.println( "Unrecognized argument: " + arg );
                    break;
            }
        }
    }

    private static void getLatestExchangeRates(String currency) throws SQLException {
        ExchangeRates exchangeRates = new ExchangeRates(currency);
        exchangeRates.getExchangeRatesFromCoinbase();
        new RateHistoryDAO(new Date(), exchangeRates);
    }
}
