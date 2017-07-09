package com.sullbrothers.crypto.exchangerates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.json.JSONObject;

public class CoinbaseConnection{
    private static String host = "https://api.coinbase.com/v2/";

    public CoinbaseConnection(){

    }

    public JSONObject getExchangeRatesJson(String currency) {
        JSONObject exchangeRates = null;

        try {

		URL url = new URL(host + "exchange-rates?currency=" + currency);
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

	public List<ExchangeRate> getExchangeRates(String currency) {
		List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
        try {

		URL url = new URL(host + "exchange-rates?currency=" + currency);
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

        JSONObject exchangeRatesJsonData = exchangeRatesJson.getJSONObject("data");
        JSONObject exchangeRatesJsonDataRates = exchangeRatesJsonData.getJSONObject("rates");

        Iterator<String> keys = exchangeRatesJsonDataRates.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                ExchangeRate exchangeRate = new ExchangeRate(key);
                exchangeRate.setFromCurrency(currency);
                exchangeRate.setPrice(exchangeRatesJsonDataRates.getDouble(key));
                exchangeRates.add(exchangeRate);
                System.out.println(key + " = " + exchangeRatesJsonDataRates.getDouble(key));
            } catch(Exception e) {
                
            }
        }

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

        return exchangeRates;
    }
}