package com.parse.starter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback
{
    
    
    Button callUberButton;
    
    String tag = "RiderActivity", user_name;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    LatLng user_location, driver_location;
    Double driver_latitude, driver_longitude;
    Boolean isUberBooked = false;
    Handler handler;
    private static final long LOCATION_INTERVAL = 5000;
    
    static GoogleMap mMap;
    
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
    
        handler =  new Handler();
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
                updateMapRiderOnly(location);
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
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            
            // Getting the user's previously known location
            checkUserPreviousLocation();
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
    
                        // Checking every 5 seconds if a driver has accepted the request
                        checkDriverAcceptRequest();
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
                    updateMapRiderOnly(lastKnownLocation);
                }
            }
        }
    }
    
  
    public void updateMapRiderOnly(Location location)
    {
        mMap.clear();
        user_location = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(user_location).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 15));
    
        ParseGeoPoint geoPoint = new ParseGeoPoint(user_location.latitude, user_location.longitude);
        
        ParseUser.getCurrentUser().put("User_Location",geoPoint);
        ParseUser.getCurrentUser().saveInBackground();
        
    }
    
//    public void updateMapRiderDriver(Location driverLoc, String driverName)
//    {
//        ArrayList<Marker> markers = new ArrayList<>();
//        markers.add(mMap.addMarker(new MarkerOptions().position(driverLoc).title("Driver: "+driverName)));
//        markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Rider: You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : markers)
//        {
//            builder.include(marker.getPosition());
//        }
//
//        LatLngBounds bounds = builder.build();
//        int padding = 100; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        mMap.animateCamera(cu);
//    }
    
    
    
    public void checkUserPreviousLocation()
    {
         //Checking if the user has already booked an uber
        final ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Rider_Name", user_name);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                        
                        Toast.makeText(RiderActivity.this, "Uber has already been booked", Toast.LENGTH_SHORT).show();
                        for (ParseObject object : objects)
                        {
                            
                            // Getting the location from where the user previously booked an uber
                            ParseGeoPoint geoPoint = object.getParseGeoPoint("Rider_Location");
                            if (ActivityCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            {
                                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                lastKnownLocation.setLatitude(geoPoint.getLatitude());
                                lastKnownLocation.setLongitude(geoPoint.getLongitude());
    
                                updateMapRiderOnly(lastKnownLocation);
                            }
                        }
                        isUberBooked = true;
                        callUberButton.setText("Cancel Uber");
    
                        // Checking every 5 seconds if a driver has accepted the request
                        checkDriverAcceptRequest();
                    }
                    else
                    {
                        // Getting the user's previously known location if they haven't called an uber
                        ParseGeoPoint geoPoint = ParseUser.getCurrentUser().getParseGeoPoint("User_Location");
                        if (ActivityCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            lastKnownLocation.setLatitude(geoPoint.getLatitude());
                            lastKnownLocation.setLongitude(geoPoint.getLongitude());
                            updateMapRiderOnly(lastKnownLocation);
                        }
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
    
    
    public void checkDriverAcceptRequest()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Checking if driver has been added to request
                ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
                query.whereEqualTo("Rider_Name", user_name);
                query.whereExists("Driver_Name");
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e)
                    {
                        if(e == null)
                        {
                            if(objects.size() > 0)
                            {
                                // Getting the driver's location
                                getDriverLocation(objects.get(0).getString("Driver_Name"));
                            }
                        }
                        else
                        {
                            Toast.makeText(RiderActivity.this, "Check exception 3: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
                
                handler.postDelayed(this, LOCATION_INTERVAL);
            }
        }, LOCATION_INTERVAL);
    }
    
    private void getDriverLocation(final String driverName)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", driverName);
        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> users, ParseException e)
            {
                if(e == null)
                {
                    if(users.size() > 0)
                    {
                        
                        driver_latitude = users.get(0).getParseGeoPoint("User_Location").getLatitude();
                        driver_longitude = users.get(0).getParseGeoPoint("User_Location").getLongitude();
                        driver_location = new LatLng(driver_latitude, driver_longitude);
                        Log.d(tag, String.valueOf(driver_location));
                        
                        Location driverLoc = new Location(LocationManager.GPS_PROVIDER);
                        driverLoc.setLatitude(driver_latitude);
                        driverLoc.setLongitude(driver_latitude);
//                        updateMapRiderDriver(driverLoc, driverName);
                    }
                    else
                    {
                        Log.d(tag, "nothing");
                    }
                }
                else
                {
                   Toast.makeText(RiderActivity.this, "Check exception 4: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                   e.printStackTrace();
                }
            }
       });
    }
}
