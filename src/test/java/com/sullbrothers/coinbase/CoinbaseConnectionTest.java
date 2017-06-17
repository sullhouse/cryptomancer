package com.sullbrothers.coinbase;

import com.sullbrothers.crypto.coinbase.CoinbaseConnection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CoinbaseConnectionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CoinbaseConnectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CoinbaseConnectionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        CoinbaseConnection coinbaseConnection = new CoinbaseConnection();
        assertNotNull("Coinbase Connection should return exchange rates", coinbaseConnection.getExchangeRates("BTC"));
    }

}