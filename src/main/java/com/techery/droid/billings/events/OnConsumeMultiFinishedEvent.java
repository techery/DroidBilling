package com.techery.droid.billings.events;

import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

import java.util.List;

public class OnConsumeMultiFinishedEvent {
    private final List<ConsumableItem> items;
    private final List<IabResult> results;

    public OnConsumeMultiFinishedEvent(List<ConsumableItem> items, List<IabResult> results) {
        this.items = items;
        this.results = results;
    }

    public List<ConsumableItem> getItems() {
        return items;
    }

    public List<IabResult> getResults() {
        return results;
    }
}