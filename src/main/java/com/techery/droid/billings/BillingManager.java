package com.techery.droid.billings;

import com.techery.droid.billings.annotations.Billing;
import com.techery.droid.billings.events.OnConsumeFinishedEvent;
import com.techery.droid.billings.events.OnIabPurchaseFinishedEvent;
import com.techery.droid.billings.events.QueryInventoryFinishedEvent;
import com.techery.droid.billings.models.BillableItem;
import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.Inventory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by ad on 11/27/13.
 */
public class BillingManager {
    public interface Events {
        public class UpdatePurchasesStatusEvent {

        }
    }

    private final EventBus eventBus;

    List<BillableItem> items = new ArrayList<BillableItem>();

    public BillingManager(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.registerSticky(this);
    }

    public void startManagingForItem(BillableItem item) {
        this.items.add(item);
    }

    public void onEventMainThread(QueryInventoryFinishedEvent event) {
        Inventory inventory = event.getInventory();

        for(BillableItem item : items) {
            item.update(inventory);
        }

        this.eventBus.post(new Events.UpdatePurchasesStatusEvent());
    }

    public void onEventMainThread(OnIabPurchaseFinishedEvent event) {
        this.eventBus.removeStickyEvent(event);

        for(BillableItem item : items) {
            if (event.getInfo().getSku().equals(item.getSku())) {
                item.updateFromPurchase(event.getInfo());
            }
        }

        this.eventBus.post(new Events.UpdatePurchasesStatusEvent());
    }

    public void onEventMainThread(OnConsumeFinishedEvent event) {
        this.eventBus.post(new Events.UpdatePurchasesStatusEvent());
    }
}
