package com.techery.droid.billings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.techery.droid.billings.AbstractController;
import com.techery.droid.billings.events.IabSetupErrorEvent;
import com.techery.droid.billings.events.IabSetupFinishedEvent;
import com.techery.droid.billings.models.BillingFeatureSupportingResult;
import com.techery.droid.billings.models.IabResult;
import com.techery.droid.billings.modules.BillingServiceModule;
import com.techery.droid.billings.utils.BillingSupportingChecker;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class BillingInitializationController extends AbstractController {
    private ServiceConnection serviceConnection;
    private BillingProcessController billingProcessController;

    @Inject
    BillingSupportingChecker billingSupportingChecker;

    public BillingInitializationController(ObjectGraph objectGraph) {
        super(objectGraph);
    }

    public void start() {
        buildServiceConnection();
        bindServiceConnection();
    }

    public void stop() {
        if (serviceConnection != null) {
            if (context != null) {
                context.unbindService(serviceConnection);
            }
        }

        serviceConnection = null;
        billingProcessController = null;
    }

    private void bindServiceConnection() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");

        if (!context.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty()) {
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            bus.post(new IabSetupErrorEvent(new IabResult(Constants.BILLING_RESPONSE_RESULT.BILLING_UNAVAILABLE, "Billing service unavailable on device.")));
        }
    }

    private void buildServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                billingProcessController = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                setupService(IInAppBillingService.Stub.asInterface(service));
            }
        };
    }

    private void setupService(IInAppBillingService iInAppBillingService) {
        try {
            BillingFeatureSupportingResult supportingResult = this.billingSupportingChecker.getFeaturesSupporting(iInAppBillingService);

            if (supportingResult.isBillingSupportingResult()) {
                setupHelper(iInAppBillingService, supportingResult);
            } else {
                bus.post(new IabSetupErrorEvent(new IabResult(supportingResult.getBillingSupportingResult(), "Error checking for billing v3 support.")));
            }

        } catch (RemoteException e) {
            bus.post(new IabSetupErrorEvent(new IabResult(Constants.IABHELPER_REMOTE_EXCEPTION, "RemoteException while setting up in-app billing.")));
            return;
        }
    }

    private void setupHelper(IInAppBillingService iInAppBillingService, BillingFeatureSupportingResult supportingResult) {
        IabResult result = new IabResult(Constants.BILLING_RESPONSE_RESULT.OK, "Setup successful.");

        ObjectGraph billingServiceGraph = this.objectGraph.plus(new BillingServiceModule(supportingResult, iInAppBillingService));

        this.billingProcessController = new BillingProcessController(billingServiceGraph);

        bus.post(new IabSetupFinishedEvent(result));
    }

    public BillingProcessController getBillingProcessController() {
        return billingProcessController;
    }
}
