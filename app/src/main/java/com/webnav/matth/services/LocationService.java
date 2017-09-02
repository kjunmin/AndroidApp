package com.webnav.matth.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by matth on 31/8/2017.
 */

public class LocationService extends Service {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 122;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private long MEAN_TIME_IN_MILLIS = 2000;
    private long MEAN_DISTANCE_IN_METERS = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("location_latitude", location.getLatitude());
                i.putExtra("location_longitude", location.getLongitude());
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MEAN_TIME_IN_MILLIS, MEAN_DISTANCE_IN_METERS, mLocationListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
