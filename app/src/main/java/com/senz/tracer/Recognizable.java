package com.senz.tracer;

import com.amap.api.location.AMapLocation;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengxiaoping on 4/24/16.
 */
public class Recognizable {
    public Map<String, Serializable> attributes = new HashMap<>();

    public static Map<String, Serializable> MAPPING = new HashMap<>();

    public static String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

    static {
        MAPPING.put("b9407f30-f5f8-466e-aff9-25556b57fe6d,9989,11940", "13488892615");
        MAPPING.put("b9407f30-f5f8-466e-aff9-25556b57fe6d,14865,38516", "13701309320");
    }

    public Recognizable(String key, String value) {
        attributes.put(key, value);
    }

    public Recognizable(Beacon beacon) {
        String id = beacon.getProximityUUID() + "," + beacon.getMajor() + "," + beacon.getMinor();
        attributes.put("id", id);
        attributes.put("distance", Utils.computeAccuracy(beacon));
        if (MAPPING.containsKey(id)) {
            attributes.put("user_name", MAPPING.get(id));
        }
    }

    public Recognizable(AMapLocation aMapLocation) {
        attributes.put("lat", aMapLocation.getLatitude());
        attributes.put("long", aMapLocation.getLongitude());
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
