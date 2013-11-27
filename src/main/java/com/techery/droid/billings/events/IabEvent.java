package com.techery.droid.billings.events;

import com.techery.droid.billings.models.IabResult;

public class IabEvent {
    private final IabResult result;

    public IabEvent(IabResult iabResult) {

        result = iabResult;
    }

    public IabResult getResult() {
        return result;
    }
}