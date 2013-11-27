package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Inventory;

public class QueryInventoryFinishedEvent extends QueryInventoryEvent {
    public QueryInventoryFinishedEvent(IabResult result, Inventory inventory) {
        super(result, inventory);
    }
}