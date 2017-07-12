package com.sullbrothers.crypto.app;

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
        for(int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            switch (arg) {
            	case "-h": case "--h": case "-help": case "--help":
					System.out.print(help());
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
    
    private static String help() {
    	String help = 
"CryptoMancer application by sullbrothers. © 2017\n" +
"\n" +
"Usage:\n" +
"	java -jar [CryptoMancer].jar [command] [option]\n" +
"\n" +
"Commands:\n" +
"	-gr	[sources]	: 	Get current exchange rates from specified source and persist to \n" +
"						database. List of supported sources below. Separate multiple sources \n" +
"						with a comma and no spaces.\n" +
"	-h,--h,-help,\n" +
"		--help		:	View this help guide.\n" +
"	-uw				: 	Update all known wallets with their known balance in Coinbase.\n" +
"\n" +
"Examples:\n" +
"	java -jar [CryptoMancer].jar -gr CryptoCompare\n" +
"					:	Get current exchange rates from CryptoCompare and persist to database.\n" +
"	java -jar [CryptoMancer].jar -gr CryptoCompare,ShapeShift\n" +
"					:	Get current exchange rates from both CryptoCompare and ShapeShift and\n" + 
"						persist to database.\n" +
"	java -jar [CryptoMancer].jar -uw\n" +
"					:	Update all wallets with their known balance in Coinbase.\n" +
"\n" +
"Supported Exchange Rate Sources:\n" +
"	CryptoCompare\n" +
"	ShapeShift\n" +
"	Coinbase";
    	return help;
    }
}
