package com.sullbrothers.crypto.mancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DecisionSuite
 */
public class DecisionSuite {

    // Order is potentially very important here.  See shouldPerform
    private List<DecisionInterface> interfaces;

    public DecisionSuite () {
        this.interfaces = new ArrayList<DecisionInterface>();
    }

    public DecisionSuite (List<DecisionInterface> decisionInterfaces) {
        this.interfaces = new ArrayList<DecisionInterface>(decisionInterfaces);
    }

    public void addDecisionInterface(DecisionInterface i){
        this.interfaces.add(i);
    }

    public MancerAction shouldPerform(MancerState state, int rhPos){
        MancerAction[] actions = new MancerAction[this.interfaces.size()];
        MancerAction previousAction = null;
        for(DecisionInterface i : this.interfaces){
            previousAction = i.shouldPerform(state, rhPos, previousAction);
            actions[this.interfaces.indexOf(i)] = previousAction;
        }

        // Return the action that's seen most frequently?  In theory I want this to be somewhat more intelligent
        // Or maybe return the last action and have actions depend on the previous one?  let's try this in the two step approach,
        // first algo to determine potential action, second to confirm
        return actions[actions.length-1];
    }
}