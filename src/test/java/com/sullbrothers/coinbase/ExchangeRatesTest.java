package com.sullbrothers.coinbase;

import com.sullbrothers.crypto.coinbase.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ExchangeRatesTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExchangeRatesTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ExchangeRatesTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        ExchangeRates exchangeRates = new ExchangeRates("BTC");
        assertTrue("Exchange rates map should include BTC greater than zero", exchangeRates.getExchageRates().get("BTC") > 0);
        assertTrue("Exchange rates map should include ETH greater than zero", exchangeRates.getExchageRates().get("ETH") > 0);
        assertTrue("Exchange rates map should include LTC greater than zero", exchangeRates.getExchageRates().get("LTC") > 0);
        assertTrue("Exchange rates map should include USD greater than zero", exchangeRates.getExchageRates().get("USD") > 0);

        assertNotNull("Exchange rates timestamp should exist", exchangeRates.getTimestamp());
    }
}