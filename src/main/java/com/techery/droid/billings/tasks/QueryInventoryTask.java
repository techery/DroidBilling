package com.techery.droid.billings.tasks;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.techery.droid.billings.BillingConfig;
import com.techery.droid.billings.Constants;
import com.techery.droid.billings.events.QueryInventoryErrorEvent;
import com.techery.droid.billings.events.QueryInventoryFinishedEvent;
import com.techery.droid.billings.models.BillingFeatureSupportingResult;
import com.techery.droid.billings.models.IabException;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.models.Inventory;
import com.techery.droid.billings.models.Purchase;
import com.techery.droid.billings.models.SkuDetails;
import com.techery.droid.billings.tasks.BillingTask;
import com.techery.droid.billings.utils.BillingSecurity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class QueryInventoryTask extends BillingTask {
    private final List<String> moreSkus;
    private final boolean querySkuDetails;

    @Inject
    BillingSecurity billingSecurity;

    @Inject
    BillingConfig billingConfig;

    @Inject
    BillingFeatureSupportingResult supportingResult;

    public QueryInventoryTask(List<String> moreSkus, boolean querySkuDetails) {
        this.moreSkus = moreSkus;
        this.querySkuDetails = querySkuDetails;
    }

    protected Inventory queryInventory(boolean querySkuDetails, List<String> moreSkus) throws IabException {
        return queryInventory(querySkuDetails, moreSkus, null);
    }

    protected Inventory queryInventory(boolean querySkuDetails, List<String> moreItemSkus,
                                    List<String> moreSubsSkus) throws IabException {
        try {
            Inventory inv = new Inventory();
            int r = queryPurchases(inv, Constants.ITEM_TYPE_INAPP);
            if (r != Constants.BILLING_RESPONSE_RESULT.OK) {
                throw new IabException(r, "Error refreshing inventory (querying owned items).");
            }

            if (querySkuDetails) {
                r = querySkuDetails(Constants.ITEM_TYPE_INAPP, inv, moreItemSkus);
                if (r != Constants.BILLING_RESPONSE_RESULT.OK) {
                    throw new IabException(r, "Error refreshing inventory (querying prices of items).");
                }
            }

            if (this.supportingResult.isSubscriptionSupported()) {
                r = queryPurchases(inv, Constants.ITEM_TYPE_SUBS);
                if (r != Constants.BILLING_RESPONSE_RESULT.OK) {
                    throw new IabException(r, "Error refreshing inventory (querying owned subscriptions).");
                }

                if (querySkuDetails) {
                    r = querySkuDetails(Constants.ITEM_TYPE_SUBS, inv, moreItemSkus);
                    if (r != Constants.BILLING_RESPONSE_RESULT.OK) {
                        throw new IabException(r, "Error refreshing inventory (querying prices of subscriptions).");
                    }
                }
            }

            return inv;
        } catch (RemoteException e) {
            throw new IabException(Constants.IABHELPER_REMOTE_EXCEPTION, "Remote exception while refreshing inventory.", e);
        } catch (JSONException e) {
            throw new IabException(Constants.IABHELPER_BAD_RESPONSE, "Error parsing JSON response while refreshing inventory.", e);
        }
    }

    int querySkuDetails(String itemType, Inventory inv, List<String> moreSkus)
            throws RemoteException, JSONException {
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.addAll(inv.getAllOwnedSkus(itemType));
        if (moreSkus != null) {
            for (String sku : moreSkus) {
                if (!skuList.contains(sku)) {
                    skuList.add(sku);
                }
            }
        }

        if (skuList.size() == 0) {
            return Constants.BILLING_RESPONSE_RESULT.OK;
        }

        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList(Constants.GET_SKU_DETAILS_ITEM_LIST, skuList);
        Bundle skuDetails = getService().getSkuDetails(3, context.getPackageName(),
                itemType, querySkus);

        if (!skuDetails.containsKey(Constants.RESPONSE_GET_SKU_DETAILS_LIST)) {
            int response = this.responseHelper.getResponseCodeFromBundle(skuDetails);
            if (response != Constants.BILLING_RESPONSE_RESULT.OK) {
                return response;
            } else {
                return Constants.IABHELPER_BAD_RESPONSE;
            }
        }

        ArrayList<String> responseList = skuDetails.getStringArrayList(
                Constants.RESPONSE_GET_SKU_DETAILS_LIST);

        for (String thisResponse : responseList) {
            SkuDetails d = new SkuDetails(itemType, thisResponse);
            inv.addSkuDetails(d);
        }
        return Constants.BILLING_RESPONSE_RESULT.OK;
    }

    int queryPurchases(Inventory inv, String itemType) throws JSONException, RemoteException {
        boolean verificationFailed = false;
        String continueToken = null;

        do {
            Bundle ownedItems = getService().getPurchases(3, context.getPackageName(),
                    itemType, continueToken);

            int response = this.responseHelper.getResponseCodeFromBundle(ownedItems);
            if (response != Constants.BILLING_RESPONSE_RESULT.OK) {
                return response;
            }
            if (!ownedItems.containsKey(Constants.RESPONSE_INAPP_ITEM_LIST)
                    || !ownedItems.containsKey(Constants.RESPONSE_INAPP_PURCHASE_DATA_LIST)
                    || !ownedItems.containsKey(Constants.RESPONSE_INAPP_SIGNATURE_LIST)) {
                return Constants.IABHELPER_BAD_RESPONSE;
            }

            ArrayList<String> ownedSkus = ownedItems.getStringArrayList(
                    Constants.RESPONSE_INAPP_ITEM_LIST);
            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(
                    Constants.RESPONSE_INAPP_PURCHASE_DATA_LIST);
            ArrayList<String> signatureList = ownedItems.getStringArrayList(
                    Constants.RESPONSE_INAPP_SIGNATURE_LIST);

            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku = ownedSkus.get(i);
                if (this.billingSecurity.verifyPurchase(this.billingConfig.getSignatureBase64(), purchaseData, signature)) {
                    Purchase purchase = new Purchase(itemType, purchaseData, signature);
                    inv.addPurchase(purchase);
                } else {
                    verificationFailed = true;
                }
            }

            continueToken = ownedItems.getString(Constants.INAPP_CONTINUATION_TOKEN);
        } while (!TextUtils.isEmpty(continueToken));

        return verificationFailed ? Constants.IABHELPER_VERIFICATION_FAILED : Constants.BILLING_RESPONSE_RESULT.OK;
    }

    @Override
    public void run() {
        IabResult result = new IabResult(Constants.BILLING_RESPONSE_RESULT.OK, "Inventory refresh successful.");
        Inventory inv = null;
        try {
            inv = queryInventory(querySkuDetails, moreSkus);
        } catch (IabException ex) {
            result = ex.getResult();
        }

        if (result.isSuccess()) {
            bus.post(new QueryInventoryFinishedEvent(result, inv));
        } else {
            bus.post(new QueryInventoryErrorEvent(result, inv));
        }
    }
}
