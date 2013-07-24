package org.greatsunflower.android;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


public class LocationDemand extends Service implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient customClient;
    private LocationRequest customRequest;
    private final IBinder mBinder = new MyBinder();

    private static final int INTERVAL_DURATION_IN_MILLISECONDS = 60000;
    private static final int FAST_INTERVAL_DURATION_IN_MILLISECONDS = 30000;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LocationDemand.class.getSimpleName(), "Starting service");

        super.onStartCommand(intent, flags, startId);
        
        if (customClient != null && !customClient.isConnected()) {    
        customClient.connect();
        }


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        customClient = new LocationClient(this, this, this);

        customRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        customRequest.setInterval(INTERVAL_DURATION_IN_MILLISECONDS);

        // Use high accuracy
        customRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to ten seconds
        customRequest.setFastestInterval(FAST_INTERVAL_DURATION_IN_MILLISECONDS);


    }


    @Override
    public void onDestroy() {

        if (customClient != null && customClient.isConnected()) {

            customClient.removeLocationUpdates(this);

            customClient.disconnect();
        }

        super.onDestroy();

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(LocationDemand.class.getSimpleName(), "Location changed");


    }

    @Override
    public void onConnected(Bundle bundle) {
        customClient.requestLocationUpdates(customRequest, this);

    }

    @Override
    public void onDisconnected() {
        customClient.removeLocationUpdates(this);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected String returnLocation() {
        Location getLocation = customClient.getLastLocation();
        if (getLocation != null) {
            String latitude = String.valueOf(getLocation.getLatitude());
            String longitude = String.valueOf(getLocation.getLongitude());

            Log.d(LocationDemand.class.getSimpleName(), "Latitude:" + latitude + " Longitude:" + longitude);

            return latitude + "," + longitude;

        } else return "";


    }

    public class MyBinder extends Binder {
        LocationDemand getService() {
            return LocationDemand.this;
        }
    }

}
