package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

public class OnIabPurchaseEvent extends IabEvent {
    private final Purchase info;

    public OnIabPurchaseEvent(IabResult result, Purchase info) {
        super(result);
        this.info = info;
    }

    public Purchase getInfo() {
        return info;
    }
}