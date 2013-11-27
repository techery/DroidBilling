package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Inventory;

public class QueryInventoryErrorEvent extends QueryInventoryEvent {
    public QueryInventoryErrorEvent(IabResult result, Inventory inventory) {
        super(result, inventory);
    }
}