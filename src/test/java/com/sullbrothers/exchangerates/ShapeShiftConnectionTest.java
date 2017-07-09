package com.sullbrothers.exchangerates;

import com.sullbrothers.crypto.exchangerates.ShapeShiftConnection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ShapeShiftConnectionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ShapeShiftConnectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ShapeShiftConnectionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        ShapeShiftConnection shapeShiftConnection = new ShapeShiftConnection();
        assertNotNull("ShapeShift Connection should return exchange rates", shapeShiftConnection.getExchangeRates("BTC", "ETH,LTC"));
    }

}