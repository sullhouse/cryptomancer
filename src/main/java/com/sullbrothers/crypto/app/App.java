package com.sullbrothers.crypto.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sullbrothers.crypto.exchangerates.*;
import com.sullbrothers.crypto.wallet.Wallet;
import com.sullbrothers.crypto.database.*;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Running main application." );
        for(int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            switch (arg) {
            	case "-h": case "--h": case "-help": case "--help":
					try {
						BufferedReader in = new BufferedReader(new FileReader("help.txt"));
						String line;
						while((line = in.readLine()) != null) {
						    System.out.println(line);
						}
						in.close();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
                case "-gr":
                    i++;
                    if (i == args.length) {
                    	System.out.println("Source is required when requesting rates. --help for more info.");
                    } else {
                        arg = args[i].toUpperCase();
                        List<String> sources = Arrays.asList(arg.split(","));
                        for (String source : sources) {
	                        switch (source) {
	                            case "CRYPTOCOMPARE": 
	                            case "COINBASE": 
	                            case "SHAPESHIFT":
	                                break;
	                            default:
	                                System.out.println( "Unrecognized source: '" + source + "'. Valid sources are CryptoCompare, Coinbase and ShapeShift");
	                                return;
	                        }
                        }
                        try {
                            getLatestExchangeRates(sources);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Ran into an issue getting latest exchange rates!");
                            //TODO: handle exception
                        }
                    }
                    break;
                case "-uw":
					List<Wallet> wallets = new WalletDAO().getAllWallets();
					for (Wallet wallet : wallets) {
						wallet.updateBalance();
						WalletDAO.persistBalance(wallet);
					}
                    break;
                default:
                    System.out.println( "Unrecognized argument: " + arg );
                    break;
            }
        }
    }

    private static void getLatestExchangeRates(List<String> sources) throws SQLException {
        ExchangeRates exchangeRates;
        for (String source: sources) {
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
	        new RateHistoryDAO(new Date(), exchangeRates, source);
        }
    }
}
