package com.parse.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    Button callUberButton, logoutButton;
    ProgressBar progressBar;
    
    String tag = "RiderActivity", user_name;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng user_location, driver_location, driverPrevLocation;
    Double driver_latitude, driver_longitude;
    Boolean isUberBooked, isDriverAssigned;
    Handler handler;
    private static final long LOCATION_INTERVAL = 2000;
    CountDownTimer countDownTimer;
    
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
        logoutButton = (Button) findViewById(R.id.logoutButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    
        handler = new Handler();
        
        // Simply setting the driver's previous location only so that it isn't null
        driverPrevLocation = new LatLng(0,0);
    
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        isUberBooked = false; // Set to true once the rider books the uber request
        isDriverAssigned = false; // Set to true when a driver accepts the uber request
        callUberButton.setVisibility(View.INVISIBLE);
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
        
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                user_location = new LatLng(location.getLatitude(), location.getLongitude());
                
                // If user has logged out, can't save location
                if(ParseUser.getCurrentUser() != null)
                {
                    ParseGeoPoint geoPoint = new ParseGeoPoint(user_location.latitude, user_location.longitude);
    
                    ParseUser.getCurrentUser().put("User_Location", geoPoint);
                    ParseUser.getCurrentUser().saveInBackground();
    
                    // Checking if a uber has been booked only before a driver has been assigned.
                    // Once a driver has been assigned, the map updates to show both locations
                    if(!isDriverAssigned)
                    {
                        checkUberBooked();
                    }
                }
            }
    
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }
    
            @Override
            public void onProviderEnabled(String s)
            {
                progressBar.setVisibility(View.VISIBLE);
            }
    
            @Override
            public void onProviderDisabled(String s)
            {
                turnOnLocation();
            }
        };
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            // Checking if uber has been booked, setting location accordingly
            checkUberBooked();
        }
    }
    
    public void logoutTapped(View view)
    {
        //Stopping location updates. It'll restart when the user logs in again
        locationManager.removeUpdates(locationListener);
        ParseUser.logOut();

        finish();
    }
    
    /**
     * Either cancels or makes the uber request.
     * @param view
     */
    public  void callUberTapped(View view)
    {
        // Booking the uber
        if(!isUberBooked)
        {
            isUberBooked = true;
            callUberButton.setText("Cancel Uber");
            logoutButton.setVisibility(View.INVISIBLE);
            // Setting a countdown timer for 1 minute to find a nearby request
            startCountDownATimer();
    
            // Booking an uber and saving the request
            ParseGeoPoint geoPoint = new ParseGeoPoint(user_location.latitude, user_location.longitude);
    
            // Saving the rider's  name and location
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
                        // Letting user know of udpate
                        progressBar.setVisibility(View.VISIBLE);
                        
                        // checkUberBooked continues to be executed which checks if a driver has accepted an uber request
                    }
                    else
                    {
                        isUberBooked = false;
                        callUberButton.setText("Call Uber");
                        logoutButton.setVisibility(View.VISIBLE);
                        
                        Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(tag, "HERE: callUberTapped");
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            cancelUberRequest();
        }
        
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                // Sending user to settings page to turn settings on if they accept
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    turnOnLocation();
                }
            }
            else
            {
                turnOnLocation();
            }
        }
    }
    
    /**
     * Prompts the user to switch on their location to high accuracy mode.
     */
    private void turnOnLocation()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it? Please enable it to your high accuracy mode.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
    
    /**
     * Gets the rider's current location and updates the map to show only the rider's location.
     */
    public void showCurrentLocationOnMap()
    {
        // Updating the map
        if (ActivityCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Location prevRequestLocation = new Location(LocationManager.GPS_PROVIDER);
            
            if(ParseUser.getCurrentUser().getParseGeoPoint("User_Location") != null)
            {
                prevRequestLocation.setLatitude(ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLatitude());
                prevRequestLocation.setLongitude(ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLongitude());
            }
        
            updateMapRiderOnly(prevRequestLocation);
        }
    }
    
    /**
     * Rider has booked an uber but it hasn't been accepted by a driver yet.
     * @param location
     */
    public void updateMapRiderOnly(Location location)
    {
       mMap.clear();
        
        // If an uber is booked, letting the progress bar show. It'll become invisible when a driver is assigned
        if(!isUberBooked)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
        callUberButton.setVisibility(View.VISIBLE);
        
        user_location = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(user_location).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 15));
        
    }
    
    /**
     * Driver has accepted rider's uber request.
     * @param driverName
     */
    public void updateMapRiderDriver(String driverName)
    {
        mMap.clear();
        progressBar.setVisibility(View.INVISIBLE);
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(driver_location).title("Driver: "+driverName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
        markers.add(mMap.addMarker(new MarkerOptions().position(user_location).title("Rider: You")));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers)
        {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }
    
    
    /**
     * Checking if the rider has booked an uber. The request is saved when the user taps the request button.
     * If s request has been made, the map updates to show the rider's location and triggers the periodic check to see if the driver accepts the request.
     */
    public void checkUberBooked()
    {
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
                        for (ParseObject object : objects)
                        {
                            
                            // Getting the location from where the user previously booked an uber
                            ParseGeoPoint geoPoint = object.getParseGeoPoint("Rider_Location");
                            if (ActivityCompat.checkSelfPermission(RiderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            {
                                Location uberRequestLocation = new Location(LocationManager.GPS_PROVIDER);
                                uberRequestLocation.setLatitude(geoPoint.getLatitude());
                                uberRequestLocation.setLongitude(geoPoint.getLongitude());
    
                                updateMapRiderOnly(uberRequestLocation);
                            }
                        }
                        isUberBooked = true;
                        callUberButton.setText("Cancel Uber");
                        logoutButton.setVisibility(View.INVISIBLE);
    
                        // Triggering to check every 5 seconds if a driver has accepted the request
                        checkDriverAcceptRequest();
                    }
                    else
                    {
                        // Getting the user's current if they haven't called an uber
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        
                        showCurrentLocationOnMap();
                    }
                }
                else
                {
                    Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(tag, "HERE: checkUberBooked");
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    /**
     * Running a countdown timer for 60 seconds to find a driver to accept the request.
     */
    public void startCountDownATimer()
    {
        countDownTimer = new CountDownTimer(60000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                // Do nothing here. If a toast is displayed, it remains displayed if a driver accapts or if the rider cancels.
            }
        
            public void onFinish()
            {
                // Cancelling the request if driver wasn't assigned
                if(!isDriverAssigned)
                {
                    cancelUberRequest();
                }
            }
        }.start();
    }
    
    /**
     * Cancelling the uber request
     */
    public void cancelUberRequest()
    {
        isUberBooked = false;
        isDriverAssigned = false;
        callUberButton.setText("Call Uber");
        logoutButton.setVisibility(View.VISIBLE);
        mMap.clear();
    
        // Cancelling the uber and deleting the request
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Rider_Name", user_name);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if(e == null && objects.size() > 0)
                {
                    for(final ParseObject object: objects)
                    {
                        object.deleteInBackground(new DeleteCallback()
                        {
                            @Override
                            public void done(ParseException e)
                            {
                                if(e == null)
                                {
                                    Toast.makeText(RiderActivity.this, "Could not find a driver. Please try again later", Toast.LENGTH_SHORT).show();
                                
                                    // Get's the rider's current location and updates the map
                                    showCurrentLocationOnMap();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    
    /**
     * Checking periodically if a driver has accepted the uber request.
     * If a driver hasn't been assigned or if a driver cancels the request, only the rider's location will show on tbe map.
     * Once a driver accepts the request, the map updates to show both locations.
     */
    public void checkDriverAcceptRequest()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Checking every 'LOCATION_INTERVAL' if driver has been added to request
                ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
                query.whereEqualTo("Rider_Name", user_name);
                query.whereExists("Driver_Name");
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e)
                    {
                        if (e == null)
                        {
                            if (objects.size() > 0)
                            {
                                /**
                                 * isDriverAssigned has to be set to true in getDriverLocation. If it's set here, updating map to rider and driver
                                 * could be called from location listener before driver location is actually set.
                                 */
                                getDriverLocation(objects.get(0).getString("Driver_Name"));
                            }
                            else
                            {
                                // Driver accepted the request and then cancelled it
                                isDriverAssigned = false;
                                progressBar.setVisibility(View.VISIBLE);
                                
                                // Updating map to show rider location
                                showCurrentLocationOnMap();
                            }
                        }
                        else
                        {
                           Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                           Log.d(tag, "HERE: checkDriverAcceptRequest");
                            e.printStackTrace();
                        }
                    }
                });
                handler.postDelayed(this, LOCATION_INTERVAL);
            }
        }, LOCATION_INTERVAL);
    }
    
    /**
     *  Getting the driver's location and then updating the map.
     *  This method is called periodically as long as a driver is assigned to the request.
     * @param driverName
     */
    private void getDriverLocation(final String driverName)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", driverName);
        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> users, ParseException e)
            {
                if (e == null)
                {
                    if (users.size() > 0)
                    {
                        driver_latitude = users.get(0).getParseGeoPoint("User_Location").getLatitude();
                        driver_longitude = users.get(0).getParseGeoPoint("User_Location").getLongitude();
                        driver_location = new LatLng(driver_latitude, driver_longitude);
    
                        isDriverAssigned = true;
                        updateMapRiderDriver(driverName);
                        
                        //Updating the map only if the driver's location has changed
//                        if(!driverPrevLocation.equals(driver_location))
//                        {
//                            Log.d(tag, "changed");
//                            updateMapRiderDriver(driverName);
//                            driverPrevLocation = driver_location;
//                        }
                    }
                }
                else
                {
                    Toast.makeText(RiderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(tag, "HERE: getDriverLocation");
                    e.printStackTrace();
                }
            }
        });
    }
}