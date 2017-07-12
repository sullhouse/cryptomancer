package com.sullbrothers.crypto.wallet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

public class CoinbaseAPI {
	protected static String CB_API_KEY = "CRYPTOMANCER_CB_API_KEY";
	protected static String CB_API_SECRET = "CRYPTOMANCER_CB_API_SECRET";
	private static String host = "https://api.coinbase.com";
	private String apiKey;
	private String apiSecret;
	
	public CoinbaseAPI() {
		apiKey = System.getenv(CB_API_KEY);
		apiSecret = System.getenv(CB_API_SECRET);
	}
	
	public Wallet getWallet(String currency){
		JSONObject accountsJson = this.getAccountsJson();
		JSONArray dataJsonArray = accountsJson.getJSONArray("data");
		
		for (int i = 0; i < dataJsonArray.length(); i++) {
			JSONObject walletJson = dataJsonArray.getJSONObject(i);
			if (walletJson.getJSONObject("currency").getString("code").equals(currency)) {
				Wallet wallet = new Wallet(
						walletJson.getString("id"), 
						walletJson.getString("name"), 
						walletJson.getString("type"), 
						currency, 
						walletJson.getJSONObject("balance").getDouble("amount"),
						walletJson.getJSONObject("native_balance").getDouble("amount"),
						walletJson.getString("resource_path")
						);
				return wallet;
			}
		}
		
		return null;
	}
	
	public void updateWallet(Wallet wallet){
		JSONObject accountsJson = this.getAccountsJson();
		JSONArray dataJsonArray = accountsJson.getJSONArray("data");
		
		for (int i = 0; i < dataJsonArray.length(); i++) {
			JSONObject walletJson = dataJsonArray.getJSONObject(i);
			if (walletJson.getString("id").equals(wallet.getExternalId())) {
				wallet.setBalance(walletJson.getJSONObject("balance").getDouble("amount"));
				wallet.setUSDbalance(walletJson.getJSONObject("native_balance").getDouble("amount"));
				wallet.setTimestamp(new Date());
			}
		}
	}
	
	public JSONObject getAccountsJson() {
		try {
			String pathURL = "/v2/accounts";
			int timestamp = this.getTimestamp();
			String signature = encode(this.apiSecret, timestamp + "GET" + pathURL);
			
			URL url = new URL(host + pathURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("CB-ACCESS-KEY", this.apiKey);
			conn.setRequestProperty("CB-ACCESS-SIGN", signature);
			conn.setRequestProperty("CB-ACCESS-TIMESTAMP", Integer.toString(timestamp));
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	
			String accountsJsonString = "";
	        String output;
	
			while ((output = br.readLine()) != null) {
				System.out.println(output);
	            accountsJsonString += output;
			}
	        
			conn.disconnect();
			
			JSONObject accountsJson = new JSONObject(accountsJsonString);
			return accountsJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int getTimestamp() {
		try {
			URL url = new URL(host + "/v2/time");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
	
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	
			String timeJsonString = "";
	        String output;
	
			while ((output = br.readLine()) != null) {
	            timeJsonString += output;
			}
	
	        JSONObject timeJson = new JSONObject(timeJsonString);
	        JSONObject dataJson = timeJson.getJSONObject("data");
	        int time = dataJson.getInt("epoch");
	        
			conn.disconnect();
			
			return time;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return 0;
	}
	
	private static String encode(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		String signature = Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
		System.out.println(signature);
		return signature;
	}
}
