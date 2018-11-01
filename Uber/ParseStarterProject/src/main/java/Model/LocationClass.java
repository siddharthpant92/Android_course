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
    
}
