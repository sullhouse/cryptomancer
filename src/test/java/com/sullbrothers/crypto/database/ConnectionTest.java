package com.sullbrothers.crypto.database;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class ConnectionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ConnectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ConnectionTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testConnection()
    {
        try{
            ResultSet rs = CryptomancerDatabase.runQuery("SELECT 'I Worked'");
            rs.next();
            assertEquals("Message was different from expected.  Instead was: " + rs.getString(1), "I Worked", rs.getString(1));
        }catch(SQLException e){
            System.out.println("Caught sql exception when testing connection");
            System.out.println("Exception: " + e.getMessage());
            fail("Encountered exception when running simple connection test");
        }

        try{
            RateHistoryDAO rateHistory = new RateHistoryDAO(Date.from(Instant.EPOCH), new Date());
            System.out.println(rateHistory.toString());
        }catch(SQLException e){
            System.out.println("Caught sql exception when testing rate history query");
            System.out.println("Exception: " + e.getMessage());
            fail("Encountered exception when running simple connection test");
        }
    }
}
