package com.sullbrothers.crypto.database;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.SQLException;

import com.sullbrothers.crypto.coinbase.ExchangeRates;

/**
 * Unit test for simple App.
 */
public class PutRateTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PutRateTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PutRateTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testConnection()
    {
        try{
            ExchangeRates exchangeRates = new ExchangeRates("USD");
            RateHistoryDAO rateHistoryPut = new RateHistoryDAO(exchangeRates.getTimestamp(), exchangeRates.getExchageRates());
            System.out.println("Put in DB: " + rateHistoryPut.toString());

            RateHistoryDAO rateHistoryGet = new RateHistoryDAO(exchangeRates.getTimestamp(), exchangeRates.getExchageRates());
            System.out.println("Retrieved from DB: " + rateHistoryGet.toString());

            assertEquals("Rate put in should equal rates retrieved", rateHistoryPut.toString(), rateHistoryGet.toString());

        }catch(SQLException e){
            System.out.println("Caught sql exception when testing put rate query");
            System.out.println("Exception: " + e.getMessage());
            fail("Encountered exception when running simple connection test");
        }
    }
}
