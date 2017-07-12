package com.sullbrothers.crypto.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.sullbrothers.crypto.wallet.Wallet;

public class WalletDAO {

    private static String WALLET_TABLE_NAME = "wallet";
    private static String WALLET_VALUES_TABLE_NAME = "wallet_values";
    private static String GET_WALLET_BASE_STMT = "SELECT * FROM " + WALLET_TABLE_NAME + " WHERE currency='%s'";
    private static String GET_ALL_WALLETS_BASE_STMT = "SELECT * FROM " + WALLET_TABLE_NAME + ";";
    private static String PUT_BALANCE_BASE_STMT = "INSERT INTO " + WALLET_VALUES_TABLE_NAME + " (wallet_id, date, balance, USD_balance) VALUES(%s,'%s',%s,%s) ON DUPLICATE KEY UPDATE wallet_id=%s, date='%s', balance=%s, USD_balance=%s;";

    public WalletDAO() {
    	
    }
    
	public Wallet getWallet(String currency) {
    	Wallet wallet = null;
		String query = String.format(GET_WALLET_BASE_STMT, currency);
        System.out.println("Running query: " + query);
        ResultSet rs;
		try {
			rs = CryptomancerDatabase.runQuery(query);
			while(rs.next()){
	            wallet = new Wallet(
	            		rs.getInt("wallet_id"), 
	            		rs.getString("external_id"),
	            		rs.getString("name"),
	            		rs.getString("type"), 
	            		rs.getString("currency"), 
	            		rs.getString("resource_path"), 
	            		rs.getString("address")
	            		);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return wallet;
    }
    
    public static void persistBalance(Wallet wallet) {
    	if (wallet.getTimestamp()!=null) {
            try {
            	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestampStr = df.format(wallet.getTimestamp());
        		String query = String.format(PUT_BALANCE_BASE_STMT, wallet.getDatabaseId(), timestampStr, wallet.getBalance(), wallet.getUSDbalance(), wallet.getDatabaseId(), timestampStr, wallet.getBalance(), wallet.getUSDbalance());
        		System.out.println("Running query: " + query);
				CryptomancerDatabase.runUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public List<Wallet> getAllWallets() {
    	List<Wallet> wallets = new ArrayList<Wallet>();
		String query = String.format(GET_ALL_WALLETS_BASE_STMT);
        System.out.println("Running query: " + query);
        ResultSet rs;
		try {
			rs = CryptomancerDatabase.runQuery(query);
			while(rs.next()){
	            Wallet wallet = new Wallet(
	            		rs.getInt("wallet_id"), 
	            		rs.getString("external_id"),
	            		rs.getString("name"),
	            		rs.getString("type"), 
	            		rs.getString("currency"), 
	            		rs.getString("resource_path"), 
	            		rs.getString("address")
	            		);
	            wallets.add(wallet);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return wallets;
    }
}