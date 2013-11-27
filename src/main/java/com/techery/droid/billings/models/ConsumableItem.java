package com.techery.droid.billings.models;

/**
 * Created by ad on 11/27/13.
 */
public class ConsumableItem extends BillableItem {
    public ConsumableItem(String sku) {
        super(sku);
    }

    public void markAsConsumed() {
        this.setAvailable(false);
    }
}
