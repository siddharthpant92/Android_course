package com.parse.starter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Button callUberButton;

    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    LatLng userLocation;
    String tag = "RiderActivity";
    Boolean isUberBooked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        callUberButton = (Button) findViewById(R.id.callUber);

        //Checking if uber was previously booked
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0)
                {
                    isUberBooked = true;
                    callUberButton.setText("Cancel Uber");
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateMap(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }
        }
    }

    public void updateMap(Location location)
    {
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

    }


    public void callUberTapped(View view)
    {
        if(isUberBooked)
        {
            cancelUber();
        }
        else
        {
            bookUber();
        }
    }

    public void logoutButtonTapped(View view)
    {
        ParseUser.logOut();
        if(ParseUser.getCurrentUser() == null)
        {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.d(tag, ParseUser.getCurrentUser().getUsername());
        }
        finish();
    }

    public void bookUber()
    {
        ParseObject request = new ParseObject("Uber_Request");
        request.put("username", ParseUser.getCurrentUser().getUsername());
        if(userLocation != null)
        {
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(userLocation.latitude, userLocation.longitude);
            request.put("user_location", parseGeoPoint);
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        Toast.makeText(RiderActivity.this, "Uber has been requested", Toast.LENGTH_SHORT).show();
                        callUberButton.setText("Cancel Uber");
                        isUberBooked = true;
                    }
                    else
                    {
                        Toast.makeText(RiderActivity.this, "Could not save location. See logs", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "No location.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelUber()
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0)
                {
                    //Deleting object
                    for(ParseObject object: objects)
                    {
                        object.deleteInBackground();
                    }

                    isUberBooked = false;
                    callUberButton.setText("Call Uber");
                    Toast.makeText(RiderActivity.this, "Uber has been cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
