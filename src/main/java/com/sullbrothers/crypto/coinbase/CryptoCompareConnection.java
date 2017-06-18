package com.sullbrothers.crypto.coinbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

public class CryptoCompareConnection{
    private static String host = "https://min-api.cryptocompare.com/";

    public CryptoCompareConnection(){

    }

    public JSONObject getExchangeRates(String fromCurrency, String toCurrencies) {
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
}