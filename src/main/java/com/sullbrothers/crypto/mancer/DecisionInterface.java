package com.sullbrothers.crypto.mancer;

/**
 * DecisionInterface
 */
public class DecisionInterface {

    public DecisionInterface () {
        
    }

    public static MancerAction shouldPerform(MancerState state){
        // TODO: do something brilliant here
        return MancerAction.getRandomAction();
    }
}