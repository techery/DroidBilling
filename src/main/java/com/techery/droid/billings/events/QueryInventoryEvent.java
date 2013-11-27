package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Inventory;

public class QueryInventoryEvent extends IabEvent {
    private final Inventory inventory;

    public QueryInventoryEvent(IabResult result, Inventory inventory) {
        super(result);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }
}