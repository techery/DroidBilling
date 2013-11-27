package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

public class OnIabPurchaseErrorEvent extends OnIabPurchaseEvent {

    public OnIabPurchaseErrorEvent(IabResult result, Purchase info) {
        super(result, info);
    }
}