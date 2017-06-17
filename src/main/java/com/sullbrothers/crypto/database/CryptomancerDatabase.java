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
            this.internalConnection = DriverManager.getConnection(url.toString(),
                                                                System.getenv(DB_USERNAME_VAR),
                                                                System.getenv(DB_PASSWORD_VAR));
        }catch(SQLException e){
            // TODO handle broken connection here
        }
    }

    public static void open(){

    }

    public static CryptomancerDatabase initialize(){
        instance = new CryptomancerDatabase();
    }

    public static CryptomancerDatabase getConnection(){
        if(instance == null){
            return initialize();
        }
        return instance;
    }


}