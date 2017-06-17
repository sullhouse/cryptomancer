package com.sullbrothers.crypto.database;

import java.sql.*;

public class CryptomancerDatabase{
    protected static String DB_HOSTNAME_VAR = "CRYPTOMANCER_DB_HOST";
    protected static String DB_USERNAME_VAR = "CRYPTOMANCER_DB_USER";
    protected static String DB_PASSWORD_VAR = "CRYPTOMANCER_DB_PASSWORD";

    private static CryptomancerDatabase instance;

    private Connection internalConnection;

    private CryptomancerDatabase(){
        try{
            StringBuilder url =  new StringBuilder("jdbc:mysql://");
            url.append(System.getenv(DB_HOSTNAME_VAR));
            url.append(":3306/cryptomancer");
            System.out.println("Using connection string: " + url.toString() + " with username " + System.getenv(DB_USERNAME_VAR) + " and password " + System.getenv(DB_PASSWORD_VAR));
            this.internalConnection = DriverManager.getConnection(url.toString(),
                                                                System.getenv(DB_USERNAME_VAR),
                                                                System.getenv(DB_PASSWORD_VAR));
        }catch(SQLException e){
            // TODO handle broken connection here
            System.out.println("Could not connect to database");
            System.out.println("Message: " + e.getMessage());
        }
    }

    protected static ResultSet runQuery(String query) throws SQLException{
        Statement stmt = getConnection().internalConnection.createStatement();
        return stmt.executeQuery(query);
    }

    private static void initialize(){
        instance = new CryptomancerDatabase();
        return null;
    }

    public static CryptomancerDatabase getConnection(){
        if(instance == null){
            initialize();
        }
        return instance;
    }

    public static String getSqlDateString(java.util.Date inputDate){
        Timestamp timestamp = new Timestamp(inputDate.getTime());
        return timestamp.toString(); 
    }
}