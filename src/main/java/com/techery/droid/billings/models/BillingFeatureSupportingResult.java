package com.techery.droid.billings.models;

import com.techery.droid.billings.Constants;

public class BillingFeatureSupportingResult {
    private final int billingSupportingResult;
    private final int subscriptionSupportingResult;

    public BillingFeatureSupportingResult(int billingSupportingResult, int subscriptionSupportingResult) {
        this.billingSupportingResult = billingSupportingResult;
        this.subscriptionSupportingResult = subscriptionSupportingResult;
    }

    public boolean isBillingSupportingResult() {
        return billingSupportingResult  == Constants.BILLING_RESPONSE_RESULT.OK;
    }

    public boolean isSubscriptionSupported() {
        return subscriptionSupportingResult  == Constants.BILLING_RESPONSE_RESULT.OK;
    }

    public int getBillingSupportingResult() {
        return billingSupportingResult;
    }

    public int getSubscriptionSupportingResult() {
        return subscriptionSupportingResult;
    }
}