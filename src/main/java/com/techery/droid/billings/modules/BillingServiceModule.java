package com.techery.droid.billings.modules;

import com.android.vending.billing.IInAppBillingService;
import com.techery.droid.billings.BillingProcessController;
import com.techery.droid.billings.models.BillingFeatureSupportingResult;
import com.techery.droid.billings.tasks.ConsumeTask;
import com.techery.droid.billings.tasks.QueryInventoryTask;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false, injects = {
        BillingProcessController.class,
        ConsumeTask.class,
        QueryInventoryTask.class

}, addsTo = BillingModule.class
)
public class BillingServiceModule {

    private final BillingFeatureSupportingResult supportingResult;
    private final IInAppBillingService service;

    public BillingServiceModule(BillingFeatureSupportingResult supportingResult, IInAppBillingService service) {
        this.supportingResult = supportingResult;
        this.service = service;
    }

    @Provides
    BillingFeatureSupportingResult provideBillingFeatureSupportingResult() {
        return this.supportingResult;
    }

    @Provides
    IInAppBillingService provideIInAppBillingService() {
        return this.service;
    }
}
