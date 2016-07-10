package com.senz.tracer.Tracer;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.senz.tracer.Recognizable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxiaoping on 4/24/16.
 */
public class LocationTracer extends Tracer implements AMapLocationListener {

    public AMapLocationClient mLocationClient;
    public AMapLocationListener mLocationListener;
    public AMapLocationClientOption mLocationOption;

    public LocationTracer(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onInit() {
        mLocationListener = this;
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        //初始化定位
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(30 * 1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onStart() {
        mLocationClient.startLocation();
    }

    @Override
    public void onStop() {
        mLocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        mLocationClient.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (tracerCallback != null) {
            List<Recognizable> arround = new ArrayList<>();
            arround.add(new Recognizable(aMapLocation));
            tracerCallback.onUpdate(arround);
        }
    }
}
