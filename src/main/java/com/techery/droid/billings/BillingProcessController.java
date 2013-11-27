package com.techery.droid.billings;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.techery.droid.billings.AbstractController;
import com.techery.droid.billings.events.OnIabPurchaseErrorEvent;
import com.techery.droid.billings.events.OnIabPurchaseFinishedEvent;
import com.techery.droid.billings.models.BillingFeatureSupportingResult;
import com.techery.droid.billings.models.ConsumableItem;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Purchase;
import com.techery.droid.billings.models.PurchaseResult;
import com.techery.droid.billings.tasks.BillingTask;
import com.techery.droid.billings.tasks.ConsumeTask;
import com.techery.droid.billings.tasks.QueryInventoryTask;
import com.techery.droid.billings.utils.BillingSecurity;
import com.techery.droid.billings.utils.ResponseHelper;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class BillingProcessController extends AbstractController {

    @Inject
    ExecutorService executorService;

    @Inject
    BillingSecurity billingSecurity;

    @Inject
    ResponseHelper responseHelper;

    @Inject
    BillingFeatureSupportingResult supportingResult;

    @Inject
    IInAppBillingService service;

    @Inject
    BillingConfig billingConfig;

    int requestCode;

    String purchasingItemType;

    public BillingProcessController(ObjectGraph objectGraph) {
        super(objectGraph);
    }

    public boolean isSubscriptionsSupported() {
        return this.supportingResult.isSubscriptionSupported();
    }

    private final void submitTask(BillingTask task) {
        getObjectGraph().inject(task);
        this.executorService.submit(task);
    }

    public void queryInventoryAsync(final boolean querySkuDetails, final List<String> moreSkus) {
        submitTask(new QueryInventoryTask(moreSkus, querySkuDetails));
    }

    public void queryInventoryAsync() {
        queryInventoryAsync(true, null);
    }

    public void queryInventoryAsync(boolean querySkuDetails) {
        queryInventoryAsync(querySkuDetails, null);
    }

    public void consumeAsync(ConsumableItem item) {
        submitTask(new ConsumeTask(item));
    }

    public void consumeAsync(List<ConsumableItem> items) {
        submitTask(new ConsumeTask(items));
    }

    public void launchPurchaseFlow(Activity act, String sku, int requestCode) {
        launchPurchaseFlow(act, sku, requestCode, "");
    }

    public void launchPurchaseFlow(Activity act, String sku, int requestCode, String extraData) {
        launchPurchaseFlow(act, sku, Constants.ITEM_TYPE_INAPP, requestCode, extraData);
    }

    public void launchSubscriptionPurchaseFlow(Activity act, String sku, int requestCode) {
        launchSubscriptionPurchaseFlow(act, sku, requestCode, "");
    }

    public void launchSubscriptionPurchaseFlow(Activity act, String sku, int requestCode, String extraData) {
        launchPurchaseFlow(act, sku, Constants.ITEM_TYPE_SUBS, requestCode, extraData);
    }

    public void launchPurchaseFlow(Activity act, String sku, String itemType, int requestCode, String extraData) {
        IabResult result = null;

        if (itemType.equals(Constants.ITEM_TYPE_SUBS) && !isSubscriptionsSupported()) {
            result = new IabResult(Constants.IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE, "Subscriptions are not available.");
        } else {
            try {
                Bundle buyIntentBundle = this.service.getBuyIntent(3, context.getPackageName(), sku, itemType, extraData);

                int response = this.responseHelper.getResponseCodeFromBundle(buyIntentBundle);

                if (response == Constants.BILLING_RESPONSE_RESULT.OK) {
                    startIntentSender(act, itemType, requestCode, buyIntentBundle);
                } else {
                    result = new IabResult(response, "Unable to buy item");
                }
            } catch (SendIntentException e) {
                result = new IabResult(Constants.IABHELPER_SEND_INTENT_FAILED, "Failed to send intent.");
            } catch (RemoteException e) {
                result = new IabResult(Constants.IABHELPER_REMOTE_EXCEPTION, "Remote exception while starting purchase flow");
            }
        }

        if (result != null) {
            bus.postSticky(new OnIabPurchaseErrorEvent(result, null));
        }
    }

    private void startIntentSender(Activity act, String itemType, int requestCode, Bundle buyIntentBundle) throws SendIntentException {
        PendingIntent pendingIntent = buyIntentBundle.getParcelable(Constants.RESPONSE_BUY_INTENT);
        this.requestCode = requestCode;
        purchasingItemType = itemType;
        assert pendingIntent != null;
        act.startIntentSenderForResult(
                pendingIntent.getIntentSender(),
                requestCode,
                new Intent(),
                0,
                0,
                0
        );
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != this.requestCode) {
            return false;
        }

        IabResult result;

        if (data != null) {
            PurchaseResult purchaseResult = new PurchaseResult(this.responseHelper.getResponseCodeFromIntent(data), data);

            if (resultCode == Activity.RESULT_OK) {
                result = processPurchase(purchaseResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                result = new IabResult(Constants.IABHELPER_USER_CANCELLED, "User canceled.");
            } else {
                result = new IabResult(Constants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE, "Unknown purchase response.");
            }

        } else {
            result = new IabResult(Constants.IABHELPER_BAD_RESPONSE, "Null data in IAB result");
        }

        if (!result.isSuccess()) {
            bus.postSticky(new OnIabPurchaseErrorEvent(result, null));
        }

        return true;
    }

    private IabResult processPurchase(PurchaseResult purchaseResult) {
        IabResult result;

        if (purchaseResult.isOk()) {
            if (purchaseResult.isDataValid()) {
                try {
                    Purchase purchase = new Purchase(purchasingItemType, purchaseResult);

                    if (this.billingSecurity.verifyPurchase(this.billingConfig.getSignatureBase64(), purchaseResult)) {
                        result = new IabResult(Constants.BILLING_RESPONSE_RESULT.OK, "Success");
                        this.bus.postSticky(new OnIabPurchaseFinishedEvent(result, purchase));
                    } else {
                        result = new IabResult(Constants.IABHELPER_VERIFICATION_FAILED, "Signature verification failed for sku " + purchase.getSku());
                    }

                } catch (JSONException e) {
                    result = new IabResult(Constants.IABHELPER_BAD_RESPONSE, "Failed to parse purchase data.");
                }
            } else {
                result = new IabResult(Constants.IABHELPER_UNKNOWN_ERROR, "IAB returned null purchaseData or dataSignature");
            }
        } else {
            result = new IabResult(purchaseResult.getResponseCode(), "Problem purchashing item.");
        }

        return result;
    }
}
