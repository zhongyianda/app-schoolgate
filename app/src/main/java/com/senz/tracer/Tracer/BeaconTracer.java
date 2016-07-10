package com.senz.tracer.Tracer;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.senz.tracer.Recognizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxiaoping on 4/24/16.
 */
public class BeaconTracer extends Tracer {

    //    private static final Region ALL_BEACONS_REGION = new Region("estimote", null,
//            null, null);
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("all Estimote beacons", null, null, null);


//    Region allBeaconsRegion = new Region("Beacons with default Estimote UUID",
//            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;

    private boolean isConnected = false;

    public BeaconTracer(Context context) {
        super(context);
    }

    @Override
    public void onInit() {
        Log.i(TAG, "beacon tracer started");

        beaconManager = new BeaconManager(context.getApplicationContext());
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (beacons != null && beacons.size() > 0) {
                    List<Recognizable> arround = new ArrayList<>();
                    for (Beacon beacon : beacons) {
                        Log.i(TAG, beacon.getProximityUUID() + "," + beacon.getMajor() + "," + beacon.getMinor() + "," + Utils.computeAccuracy(beacon));
                        arround.add(new Recognizable(beacon));
                    }
                    if (tracerCallback != null) {
                        tracerCallback.onUpdate(arround);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                isConnected = true;
                Log.i(TAG, "connected");
//                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
            }
        });
    }

    @Override
    public void onStop() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
    }

    @Override
    public void onDestroy() {
        beaconManager.disconnect();
    }

}
