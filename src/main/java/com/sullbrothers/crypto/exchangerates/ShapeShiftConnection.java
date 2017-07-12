package com.sullbrothers.crypto.exchangerates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.json.JSONObject;

public class ShapeShiftConnection{
    private static String host = "https://shapeshift.io/";

    public ShapeShiftConnection(){

    }

    public List<ExchangeRate> getExchangeRates(String fromCurrency, String toCurrencies) {
		fromCurrency = fromCurrency.toLowerCase();
		List<String> currencies = Arrays.asList(toCurrencies.toLowerCase().split("\\s*,\\s*"));
		List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();
		currencies.remove(fromCurrency);

        try {
			for (String c : currencies) {
				URL url = new URL(host + "rate/" + fromCurrency + "_" + c);
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

				try {
					ExchangeRate exchangeRate = new ExchangeRate(c.toUpperCase());
					exchangeRate.setFromCurrency(fromCurrency.toUpperCase());
					exchangeRate.setPrice(exchangeRatesJson.getDouble("rate"));

					exchangeRates.add(exchangeRate);
					System.out.println(c + ": " + exchangeRate);
				} catch(Exception e) {
					e.printStackTrace();
					
				}

				conn.disconnect();
			}
			
			ExchangeRate exchangeRate = new ExchangeRate(fromCurrency.toUpperCase());
			exchangeRate.setFromCurrency(fromCurrency.toUpperCase());
			exchangeRate.setPrice(1.0);

			exchangeRates.add(exchangeRate);
			System.out.println(fromCurrency.toUpperCase() + ": " + exchangeRate);

	  	} catch (MalformedURLException e) {

			e.printStackTrace();

	  	} catch (IOException e) {

			e.printStackTrace();

	  	}

        return exchangeRates;
    }
}