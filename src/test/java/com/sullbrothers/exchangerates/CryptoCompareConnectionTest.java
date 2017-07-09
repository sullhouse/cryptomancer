package com.sullbrothers.exchangerates;

import com.sullbrothers.crypto.exchangerates.CryptoCompareConnection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class CryptoCompareConnectionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CryptoCompareConnectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CryptoCompareConnectionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        CryptoCompareConnection cryptoCompareConnection = new CryptoCompareConnection();
        assertNotNull("CryptoCompare Connection should return exchange rates", cryptoCompareConnection.getExchangeRates("BTC", "ETH,LTC,USD"));
    }

}