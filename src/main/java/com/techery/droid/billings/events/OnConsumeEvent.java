package com.techery.droid.billings.events;

import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

public class OnConsumeEvent extends IabEvent {
    private final ConsumableItem item;

    public OnConsumeEvent(ConsumableItem item, IabResult result) {
        super(result);
        this.item = item;
    }

    public Purchase getPurchase() {
        return item.getPurchase();
    }

    public ConsumableItem getItem() {
        return item;
    }
}