package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;

public class IabSetupFinishedEvent extends IabEvent {
    public IabSetupFinishedEvent(IabResult iabResult) {
        super(iabResult);
    }
}
