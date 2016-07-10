package com.senz.tracer.Tracer;

import android.content.Context;

/**
 * Created by fengxiaoping on 4/24/16.
 */
public abstract class Tracer {
    public static final String TAG = "Tracer";

    Context context;
    TracerCallback tracerCallback;

    public Tracer(Context context) {
        this.context = context;
        onInit();
    }

    public abstract void onInit();

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onDestroy();

    public void startTracing(TracerCallback tracerCallback) {
        this.tracerCallback = tracerCallback;
        onStart();
    }

    public void destroyTracing() {
        onDestroy();
    }

}
