package com.techery.droid.billings.tasks;

import android.os.RemoteException;

import com.techery.droid.billings.Constants;
import com.techery.droid.billings.events.OnConsumeFinishedEvent;
import com.techery.droid.billings.events.OnConsumeMultiFinishedEvent;
import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabException;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;

import java.util.ArrayList;
import java.util.List;

public class ConsumeTask extends BillingTask {
    private final List<ConsumableItem> items;
    private final boolean isSingleQuery;

    public ConsumeTask(List<ConsumableItem> items) {
        this.items = items;
        this.isSingleQuery = false;
    }

    public ConsumeTask(ConsumableItem purchase) {
        List<ConsumableItem> purchases = new ArrayList<ConsumableItem>();
        purchases.add(purchase);
        this.items = purchases;
        this.isSingleQuery = true;
    }

    private void consume(Purchase itemInfo) throws IabException {
        if (!itemInfo.getItemType().equals(Constants.ITEM_TYPE_INAPP)) {
            throw new IabException(Constants.IABHELPER_INVALID_CONSUMPTION,
                    "Items of type '" + itemInfo.getItemType() + "' can't be consumed.");
        }

        try {
            String token = itemInfo.getToken();
            String sku = itemInfo.getSku();
            if (token == null || token.equals("")) {
                throw new IabException(Constants.IABHELPER_MISSING_TOKEN, "PurchaseInfo is missing token for sku: "
                        + sku + " " + itemInfo);
            }

            int response = getService().consumePurchase(3, context.getPackageName(), token);
            if (response != Constants.BILLING_RESPONSE_RESULT.OK) {
                throw new IabException(response, "Error consuming sku " + sku);
            }

        } catch (RemoteException e) {
            throw new IabException(Constants.IABHELPER_REMOTE_EXCEPTION, "Remote exception while consuming. PurchaseInfo: " + itemInfo, e);
        }
    }

    @Override
    public void run() {
        final List<IabResult> results = new ArrayList<IabResult>();
        for (ConsumableItem item : this.items) {
            try {
                consume(item.getPurchase());
                item.markAsConsumed();
                results.add(new IabResult(Constants.BILLING_RESPONSE_RESULT.OK, "Successful consume of sku " + item.getSku()));
            } catch (IabException ex) {
                results.add(ex.getResult());
            }
        }

        if (this.isSingleQuery) {
            this.bus.post(new OnConsumeFinishedEvent(this.items.get(0), results.get(0)));
        } else {
            this.bus.post(new OnConsumeMultiFinishedEvent(this.items, results));
        }
    }
}
