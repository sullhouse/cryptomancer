package com.sullbrothers.crypto.sim;

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
public class SimulationEngineTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SimulationEngineTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SimulationEngineTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testSimulationEngine()
    {
        try{
            System.out.println(SimulationEngine.runSimulation());
        }
        catch(Exception e){
            e.printStackTrace();
            fail("Failure during simulation test with message " + e.getMessage());
        }
    }
}
