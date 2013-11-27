package com.techery.droid.billings.events;

import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

public class OnConsumeErrorEvent extends OnConsumeEvent {
    public OnConsumeErrorEvent(ConsumableItem tem, IabResult result) {
        super(tem, result);
    }
}