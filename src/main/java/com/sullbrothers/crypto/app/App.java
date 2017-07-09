package com.sullbrothers.crypto.app;

import java.sql.SQLException;
import java.util.Date;

import com.sullbrothers.crypto.exchangerates.*;
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
                        getLatestExchangeRates("CRYPTOCOMPARE");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Ran into an issue getting latest exchange rates!");
                        //TODO: handle exception
                    }
                    break;
                case "-gr":
                    i++;
                    String source = "CRYPTOCOMPARE";
                    if (i < args.length) {
                        arg = args[i].toLowerCase();
                        switch (arg) {
                            case "cryptocompare":
                                break;
                            case "coinbase":
                                source = "COINBASE";
                                break;
                            case "shapeshift":
                                source = "SHAPESHIFT";
                                break;
                            default:
                                System.out.println( "Unrecognized source: '" + arg + "'. Valid sources are CryptoCompare, Coinbase and ShapeShift");
                                return;
                        }
                    }
                    try {
                        getLatestExchangeRates(source);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Ran into an issue getting latest exchange rates!");
                        //TODO: handle exception
                    }
                default:
                    System.out.println( "Unrecognized argument: " + arg );
                    break;
            }
        }
    }

    private static void getLatestExchangeRates(String source) throws SQLException {
        ExchangeRates exchangeRates;
        switch (source) {
            case "CRYPTOCOMPARE":
                exchangeRates = new ExchangeRates("USD");
                exchangeRates.getExchangeRatesFromCryptoCompare("BTC,ETH,LTC,USD");
                break;
            case "COINBASE":
                exchangeRates = new ExchangeRates("USD");
                exchangeRates.getExchangeRatesFromCoinbase();
                break;
            case "SHAPESHIFT":
                exchangeRates = new ExchangeRates("BTC");
                exchangeRates.getExchangeRatesFromShapeshift("ETH,LTC,POT");
                break;
            default:
                exchangeRates = new ExchangeRates("USD");
                exchangeRates.getExchangeRatesFromCryptoCompare("BTC,ETH,LTC,USD");
                break;
        }
        exchangeRates.getExchangeRatesFromCryptoCompare("BTC,ETH,LTC,USD");
        new RateHistoryDAO(new Date(), exchangeRates);
    }
}
