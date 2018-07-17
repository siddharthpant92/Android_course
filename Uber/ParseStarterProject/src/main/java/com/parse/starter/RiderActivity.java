package com.parse.starter;

import android.Manifest;
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
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback
{
    
    Button callUberButton;
    
    String tag = "RiderActivity", user_name;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    LatLng user_location;
    Boolean isUberBooked = false;
    
    private GoogleMap mMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    
        user_name = ParseUser.getCurrentUser().getUsername();
        callUberButton = (Button) findViewById(R.id.callUberButton);
        
        //Checking if the user has already booked an uber
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Rider_Name", user_name);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        Toast.makeText(RiderActivity.this, "Uber has already been booked", Toast.LENGTH_SHORT).show();
                        isUberBooked = true;
                        callUberButton.setText("Cancel Uber");
                    }
                }
                else
                {
                    Toast.makeText(RiderActivity.this, "Check exception 2: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on the logout button to go back", Toast.LENGTH_SHORT).show();
    }
    

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                updateMap(location);
            }
        
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            
            }
        
            @Override
            public void onProviderEnabled(String s)
            {
            
            }
        
            @Override
            public void onProviderDisabled(String s)
            {
            
            }
        };
        
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        
    }
    
    public void logoutTapped(View view)
    {
        //Stopping location updates. It'll restart when the user logs in again
        locationManager.removeUpdates(locationListener);
        ParseUser.logOut();
        finish();
    }
    
    public  void callUberTapped(View view)
    {
        if(!isUberBooked)
        {
            // Booking an uber and saving the request
            ParseGeoPoint geoPoint = new ParseGeoPoint(user_location.latitude, user_location.longitude);
    
            ParseObject parseObject = new ParseObject("Uber_Request");
            parseObject.put("Rider_Name", user_name);
            parseObject.put("Rider_Location", geoPoint);
            parseObject.saveInBackground(new SaveCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    if(e == null)
                    {
                        Toast.makeText(RiderActivity.this, "Uber has been booked", Toast.LENGTH_SHORT).show();
                        callUberButton.setText("Cancel Uber");
                        isUberBooked = true;
                    }
                    else
                    {
                        Toast.makeText(RiderActivity.this, "Check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            // Cancelling the uber and deleting the request
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
            query.whereEqualTo("Rider_Name", user_name);
            query.findInBackground(new FindCallback<ParseObject>()
            {
                @Override
                public void done(List<ParseObject> objects, ParseException e)
                {
                    if(e == null && objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            object.deleteInBackground(new DeleteCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if(e == null)
                                    {
                                        Toast.makeText(RiderActivity.this, "Uber has been cancelled", Toast.LENGTH_SHORT).show();
                                        callUberButton.setText("Call Uber");
                                        isUberBooked = false;
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    
                    //Simply setting map to last known location
                    updateMap(lastKnownLocation);
                }
            }
        }
    }
    
    public void updateMap(Location location)
    {
        mMap.clear();
        user_location = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(user_location).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 15));
    
        ParseGeoPoint geoPoint = new ParseGeoPoint(user_location.latitude, user_location.longitude);
        
        ParseUser.getCurrentUser().put("User_Location",geoPoint);
        ParseUser.getCurrentUser().saveInBackground();
    }
}
