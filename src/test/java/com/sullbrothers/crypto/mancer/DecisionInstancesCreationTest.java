package com.sullbrothers.crypto.mancer;

import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.sullbrothers.crypto.database.CurrencyValuesDAO;
import com.sullbrothers.crypto.database.RateHistoryDAO;
import com.sullbrothers.crypto.sim.SimulationEngine;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Unit test for simple App.
 */
public class DecisionInstancesCreationTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DecisionInstancesCreationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DecisionInstancesCreationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testDecisionInstancesCreation()
    {
        try{
            List<RateHistoryDAO.RateHistory> rh = new RateHistoryDAO(Date.from(Instant.EPOCH), new Date()).getAllHistoricalRates();
            MancerState state = new MancerState(new CurrencyValuesDAO(), rh.get(0), rh);
            Instances dataSet = DecisionInterface.generateDataSet(state, "mancerTest", SimulationEngine.HISTORICAL_RATIO);
            String path = DecisionInterface.saveInstancesToFile(dataSet, "mancerTest.arff");
            DecisionInterface.setClassifier(DecisionInterface.buildAndTrainClassifier(dataSet));
            assertTrue(path != null && !path.equalsIgnoreCase("failure") && new File(path).isFile());
        }
        catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

}
