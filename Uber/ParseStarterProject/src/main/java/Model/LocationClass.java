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
    
    UserClass userClass = new UserClass();
    
    RiderRequestsActivity riderRequestsActivity;
    LocationManager locationManager;
    static LocationListener locationListener;
    
    
    public LocationClass(RiderRequestsActivity riderRequestsActivity)
    {
        this.riderRequestsActivity = riderRequestsActivity;
    }
    
    
    public static LocationListener getLocationListener(final LocationManager locationManager, final Context context)
    {
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                // If user has logged out, then can't save the updated location
                if (ParseUser.getCurrentUser() != null)
                {
                    ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d("test", String.valueOf(driverGeoPoint.getLatitude()));
//                    driverLatitude = driverGeoPoint.getLatitude();
//                    driverLongitude = driverGeoPoint.getLongitude();
//                    Log.d(TAG, driverLatitude.toString());
//                    Log.d(TAG, driverLongitude.toString());
//                    ParseUser.getCurrentUser().put("User_Location", driverGeoPoint);
//                    ParseUser.getCurrentUser().saveInBackground();
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);;
                }
            }
        
            @Override
            public void onProviderDisabled(String s)
            {
//                turnOnLocation();
            }
        };
        
        return locationListener;
    }
}
