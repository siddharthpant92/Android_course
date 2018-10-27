package Model;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.starter.RiderRequestsActivity;

import static android.content.Context.LOCATION_SERVICE;

public class LocationClass
{
    String TAG = "LocationClass";
    
    RiderRequestsActivity riderRequestsActivity;
    
    public LocationClass(RiderRequestsActivity riderRequestsActivity)
    {
        this.riderRequestsActivity = riderRequestsActivity;
    }
    
    public void updateLocation(LocationManager locationManager, LocationListener locationListener, final Context context)
    {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        final LocationManager finalLocationManager = locationManager;
        final LocationListener finalLocationListener = locationListener;
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                // If user has logged out, then can't save the updated location
                if (ParseUser.getCurrentUser() != null)
                {
                    ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, String.valueOf(driverGeoPoint.getLatitude()));
//                    driverLatitude = driverGeoPoint.getLatitude();
//                    driverLongitude = driverGeoPoint.getLongitude();
                    ParseUser.getCurrentUser().put("User_Location", driverGeoPoint);
                    ParseUser.getCurrentUser().saveInBackground();
                }
            
                // Continuously finding nearby riders.
//                findNearbyRiderDistance(location);
            }
        
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            
            }
        
            @Override
            public void onProviderEnabled(String s)
            {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    finalLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, finalLocationListener);
                }
            }
        
            @Override
            public void onProviderDisabled(String s)
            {
                turnOnLocation(context);
            }
        };
    }
    
    /**
     * Prompts the user to switch on their location to high accuracy mode.
     */
    public void turnOnLocation(final Context context)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it? Please enable it to your high accuracy mode.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
