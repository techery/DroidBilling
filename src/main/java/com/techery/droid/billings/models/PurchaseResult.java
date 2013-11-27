package com.techery.droid.billings.models;

import android.content.Intent;

import com.techery.droid.billings.Constants;

public class PurchaseResult {
    private final int responseCode;
    private final String purchaseData;
    private final String dataSignature;

    public PurchaseResult(int responseCode, Intent data) {
        this.responseCode = responseCode;
        this.purchaseData = data.getStringExtra(Constants.RESPONSE_INAPP_PURCHASE_DATA);
        this.dataSignature = data.getStringExtra(Constants.RESPONSE_INAPP_SIGNATURE);
    }

    public String getPurchaseData() {
        return purchaseData;
    }

    public String getDataSignature() {
        return dataSignature;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isOk() {
        return this.responseCode == Constants.BILLING_RESPONSE_RESULT.OK;
    }

    public boolean isDataValid() {
        return getPurchaseData() != null && getDataSignature() != null;
    }
}
