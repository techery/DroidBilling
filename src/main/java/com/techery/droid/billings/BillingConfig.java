package com.techery.droid.billings;

public class BillingConfig {
    private final String signatureBase64;

    public BillingConfig(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
    }

    public String getSignatureBase64() {
        return signatureBase64;
    }
}
