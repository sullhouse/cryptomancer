package com.sullbrothers.exchangerates;

import com.sullbrothers.crypto.exchangerates.*;
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
        exchangeRates.getExchangeRatesFromCoinbase();
        assertTrue("Exchange rates map should include BTC greater than zero", exchangeRates.getExchangeRateByCurrency("BTC").getPrice() > 0);
        assertTrue("Exchange rates map should include ETH greater than zero", exchangeRates.getExchangeRateByCurrency("ETH").getPrice() > 0);
        assertTrue("Exchange rates map should include LTC greater than zero", exchangeRates.getExchangeRateByCurrency("LTC").getPrice() > 0);
        assertTrue("Exchange rates map should include USD greater than zero", exchangeRates.getExchangeRateByCurrency("USD").getPrice() > 0);

        assertNotNull("Exchange rates timestamp should exist", exchangeRates.getTimestamp());

        ExchangeRates exchangeRates2 = new ExchangeRates("BTC");
        exchangeRates2.getExchangeRatesFromCryptoCompare("BTC,ETH,LTC,USD");
        assertTrue("Exchange rates map should include BTC greater than zero", exchangeRates2.getExchangeRateByCurrency("BTC").getPrice() > 0);
        assertTrue("Exchange rates map should include ETH greater than zero", exchangeRates2.getExchangeRateByCurrency("ETH").getPrice() > 0);
        assertTrue("Exchange rates map should include LTC greater than zero", exchangeRates2.getExchangeRateByCurrency("LTC").getPrice() > 0);
        assertTrue("Exchange rates map should include USD greater than zero", exchangeRates2.getExchangeRateByCurrency("USD").getPrice() > 0);

        assertNotNull("Exchange rates timestamp should exist", exchangeRates2.getTimestamp());
    }
}