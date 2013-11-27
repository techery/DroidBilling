package com.techery.droid.billings.events;

import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

public class OnConsumeFinishedEvent extends OnConsumeEvent {
    public OnConsumeFinishedEvent(ConsumableItem item, IabResult result) {
        super(item, result);
    }
}