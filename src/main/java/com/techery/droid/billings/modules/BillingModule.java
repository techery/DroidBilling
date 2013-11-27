package com.techery.droid.billings.modules;

import android.content.Context;

import com.techery.droid.billings.BillingInitializationController;
import com.techery.droid.billings.BillingManager;
import com.techery.droid.billings.annotations.Billing;
import com.techery.droid.billings.utils.BillingSecurity;
import com.techery.droid.billings.utils.BillingSupportingChecker;
import com.techery.droid.billings.utils.ResponseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(library = true, complete = false, injects = {
        BillingInitializationController.class
})
public class BillingModule {

    @Provides
    @Singleton
    ResponseHelper provideResponseHelper() {
        return new ResponseHelper();
    }

    @Provides
    @Singleton
    BillingSecurity provideBillingSecurity() {
        return new BillingSecurity();
    }

    @Provides
    BillingInitializationController provideBillingService(@Billing ObjectGraph objectGraph) {
        return new BillingInitializationController(objectGraph);
    }

    @Provides
    @Singleton
    BillingSupportingChecker provideBillingSupportingChecker(Context context) {
        return new BillingSupportingChecker(context);
    }

    @Provides
    BillingManager provideBillingManager(@Billing EventBus eventBus) {
        return new BillingManager(eventBus);
    }
}
