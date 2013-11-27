package com.techery.droid.billings;

import android.content.Context;


import com.techery.droid.billings.annotations.Billing;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

public abstract class AbstractController {
    @Inject
    @Billing
    EventBus bus;

    @Inject
    @Billing
    Context context;

    protected final ObjectGraph objectGraph;

    public AbstractController(ObjectGraph objectGraph) {
        this.objectGraph = objectGraph;
        this.objectGraph.inject(this);
    }

    protected EventBus getBus() {
        return bus;
    }

    protected Context getContext() {
        return context;
    }

    protected ObjectGraph getObjectGraph() {
        return objectGraph;
    }
}
