package com.techery.droid.billings.tasks;

import android.content.Context;

import com.android.vending.billing.IInAppBillingService;
import com.techery.droid.billings.annotations.Billing;
import com.techery.droid.billings.utils.ResponseHelper;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public abstract class BillingTask implements Runnable {

    @Inject
    @Billing
    Context context;

    @Inject
    @Billing
    EventBus bus;

    @Inject
    ResponseHelper responseHelper;

    @Inject
    IInAppBillingService service;

    protected IInAppBillingService getService() {
        return this.service;
    }
}
