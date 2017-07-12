package com.sullbrothers.crypto.wallet;

import java.util.Date;

public class Wallet {
	private int databaseId;
	private String externalId;
	private String name;
	private String type;
	private String currency;
	private double balance;
	private double USDbalance;
	private String resourcePath;
	private String address;
	private Date timestamp;
	
	public Wallet(String externalId, String name, String type, String currency, double balance,
			double USDbalance, String resourcePath) {
		this.externalId = externalId;
		this.name = name;
		this.type = type;
		this.currency = currency;
		this.balance = balance;
		this.USDbalance = USDbalance;
		this.resourcePath = resourcePath;
		this.timestamp = new Date();
	}
	
	public Wallet(int databaseId, String externalId, String name, String type, String currency, 
			String resourcePath, String address) {
		this.databaseId = databaseId;
		this.externalId = externalId;
		this.name = name;
		this.type = type;
		this.currency = currency;
		this.resourcePath = resourcePath;
		this.address = address;
	}
	
	public void updateBalance() {
		CoinbaseAPI coinbaseAPI = new CoinbaseAPI();
		coinbaseAPI.updateWallet(this);
	}

	public String getExternalId() {
		return externalId;
	}

	public void setId(String externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabase_id(int databaseId) {
		this.databaseId = databaseId;
	}
	
	public double getUSDbalance() {
		return USDbalance;
	}

	public void setUSDbalance(double USDbalance) {
		this.USDbalance = USDbalance;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Wallet [databaseId=" + databaseId + ", "
				+ (externalId != null ? "externalId=" + externalId + ", " : "")
				+ (name != null ? "name=" + name + ", " : "") + (type != null ? "type=" + type + ", " : "")
				+ (currency != null ? "currency=" + currency + ", " : "") + "balance=" + balance + ", USDbalance="
				+ USDbalance + ", " + (resourcePath != null ? "resourcePath=" + resourcePath + ", " : "")
				+ (address != null ? "address=" + address + ", " : "")
				+ (timestamp != null ? "timestamp=" + timestamp : "") + "]";
	}
}
