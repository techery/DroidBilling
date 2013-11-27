package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;

public class IabSetupErrorEvent extends IabEvent {
    public IabSetupErrorEvent(IabResult iabResult) {
        super(iabResult);
    }
}