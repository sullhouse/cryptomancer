package com.sullbrothers.crypto.mancer;

/**
 * MancerAction
 */
public class MancerAction {

    private static final String[] AVAILABLE_CURRENCIES = {
        "BTC",
        "ETH",
        "LTC"
    };

    public String currencyToBuy;
    public String currencyToPay;
    public double amountToBuy;

    public MancerAction (String currencyToBuy, String currencyToPay, double amountToBuy) {
        this.currencyToBuy = currencyToBuy;
        this.currencyToPay = currencyToPay;
        this.amountToBuy = amountToBuy;
    }

    public static MancerAction getRandomAction(){
        int currencyPos = new Double(Math.random()*AVAILABLE_CURRENCIES.length).intValue() % AVAILABLE_CURRENCIES.length;
        return new MancerAction(AVAILABLE_CURRENCIES[currencyPos], AVAILABLE_CURRENCIES[(currencyPos+1)%AVAILABLE_CURRENCIES.length], Math.random());
    }

    public String toString(){
        return "\"ACTION\": {\"CURRENCY_TO_BUY\": \"" + this.currencyToBuy + "\", \"CURRENCY_TO_PAY\": \"" + this.currencyToPay + "\", \"AMOUNT_TO_BUY\": \"" + this.amountToBuy + "\"}";
    }
}