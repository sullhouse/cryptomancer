package com.sullbrothers.crypto.exchangerates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;

public class CryptoCompareConnection{
    private static String host = "https://min-api.cryptocompare.com/";

    public CryptoCompareConnection(){

    }

    public JSONObject getExchangeRatesJson(String fromCurrency, String toCurrencies) {
        JSONObject exchangeRates = null;

        try {

		URL url = new URL(host + "data/pricemultifull?fsyms=" + fromCurrency + "&tsyms=" + toCurrencies);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String exchangeRatesString = "";
        String output;

		while ((output = br.readLine()) != null) {
			System.out.println(output);
            exchangeRatesString += output;
		}

        exchangeRates = new JSONObject(exchangeRatesString);

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

        return exchangeRates;
    }

	public List<ExchangeRate> getExchangeRates(String fromCurrency, String toCurrencies) {
        List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();

        try {

			URL url = new URL(host + "data/pricemultifull?fsyms=" + fromCurrency + "&tsyms=" + toCurrencies);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String exchangeRatesString = "";
			String output;

			while ((output = br.readLine()) != null) {
				System.out.println(output);
				exchangeRatesString += output;
			}

			JSONObject exchangeRatesJson = new JSONObject(exchangeRatesString);

			JSONObject exchangeRatesJsonData = exchangeRatesJson.getJSONObject("RAW");
			JSONObject exchangeRatesJsonDataRates = exchangeRatesJsonData.getJSONObject(fromCurrency);

			Iterator<String> keys = exchangeRatesJsonDataRates.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				try {
					ExchangeRate exchangeRate = new ExchangeRate(key);
					JSONObject jRate = exchangeRatesJsonDataRates.getJSONObject(key);
					exchangeRate.setChange24hour(jRate.getDouble("CHANGE24HOUR"));
					exchangeRate.setChangePct24hour(jRate.getDouble("CHANGEPCT24HOUR"));
					exchangeRate.setFlags(jRate.getInt("FLAGS"));
					exchangeRate.setFromCurrency(fromCurrency);
					exchangeRate.setHigh24hour(jRate.getDouble("HIGH24HOUR"));
					exchangeRate.setLastMarket(jRate.getString("LASTMARKET"));
					exchangeRate.setLastTradeId(jRate.getInt("LASTTRADEID"));
					exchangeRate.setLastUpdate(getDateFromString(jRate.getLong("LASTUPDATE")));
					exchangeRate.setLastVolume(jRate.getDouble("LASTVOLUME"));
					exchangeRate.setLastVolumeTo(jRate.getDouble("LASTVOLUMETO"));
					exchangeRate.setLow24hour(jRate.getDouble("LOW24HOUR"));
					exchangeRate.setMarket(jRate.getString("MARKET"));
					exchangeRate.setMarketCap(jRate.getDouble("MKTCAP"));
					exchangeRate.setOpen24hour(jRate.getDouble("OPEN24HOUR"));
					exchangeRate.setPrice(jRate.getDouble("PRICE"));
					exchangeRate.setSupply(jRate.getDouble("SUPPLY"));
					exchangeRate.setType(jRate.getInt("TYPE"));
					exchangeRate.setVolume24hour(jRate.getDouble("VOLUME24HOUR"));
					exchangeRate.setVolume24hourTo(jRate.getDouble("VOLUME24HOURTO"));

					exchangeRates.add(exchangeRate);
					System.out.println(key + ": " + exchangeRate);
				} catch(Exception e) {
					e.printStackTrace();
					
				}
			}
			ExchangeRate exchangeRate = new ExchangeRate(fromCurrency);
			exchangeRate.setFromCurrency(fromCurrency);
			exchangeRate.setPrice(1.0);

			exchangeRates.add(exchangeRate);
			System.out.println(fromCurrency + ": " + exchangeRate);

			conn.disconnect();

	  	} catch (MalformedURLException e) {

			e.printStackTrace();

	  	} catch (IOException e) {

			e.printStackTrace();

	  	}

    	return exchangeRates;
    }

	private Date getDateFromString(Long s) {
        Date d = new Date(s * 1000);
        return d;
    }
}