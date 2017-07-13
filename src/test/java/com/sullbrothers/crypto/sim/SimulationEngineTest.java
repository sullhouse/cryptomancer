package com.sullbrothers.crypto.sim;

import com.sullbrothers.crypto.mancer.DecisionInterface;
import com.sullbrothers.crypto.mancer.DecisionSuite;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class SimulationEngineTest 
    extends TestCase
{
    private static List<DecisionInterface> interfaces = new ArrayList<DecisionInterface>();

    public static void addDecisionInterface(DecisionInterface i){
        interfaces.add(i);
    }

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
            DecisionSuite ds = new DecisionSuite(interfaces);
            System.out.println(SimulationEngine.runSimulation(ds));
        }
        catch(Exception e){
            e.printStackTrace();
            fail("Failure during simulation test with message " + e.getMessage());
        }
    }
}
