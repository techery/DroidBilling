package com.techery.droid.billings.utils;

import android.content.Context;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.techery.droid.billings.Constants;
import com.techery.droid.billings.models.BillingFeatureSupportingResult;

public class BillingSupportingChecker {
    private final Context context;

    public BillingSupportingChecker(Context context) {
        this.context = context;
    }

    public BillingFeatureSupportingResult getFeaturesSupporting(IInAppBillingService iInAppBillingService) throws RemoteException {
        return new BillingFeatureSupportingResult(
                getFeatureSupporting(Constants.ITEM_TYPE_INAPP, iInAppBillingService),
                getFeatureSupporting(Constants.ITEM_TYPE_SUBS, iInAppBillingService)
        );
    }

    public int getFeatureSupporting(String featureType, IInAppBillingService iInAppBillingService) throws RemoteException {
        String packageName = context.getPackageName();
        return iInAppBillingService.isBillingSupported(3, packageName, featureType);
    }
}
