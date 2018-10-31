package com.parse.starter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.DriverClass;
import Model.LocationClass;
import Model.UserClass;

public class RiderRequestsActivity extends Activity
{
    ListView riderRequestsListView;
    
    static RiderRequestsActivity riderRequestsActivity; // instance of this activity
    String TAG = "RiderRequestsActivity", user_name;
    ArrayList<String> nearbyRiderDistance = new ArrayList<>();
    ArrayList<String> nearbyRiderUsername = new ArrayList<>();
    ArrayList<Double> nearbyRiderLatitude = new ArrayList<>();
    ArrayList<Double> nearbyRiderLongitude = new ArrayList<>();
    Double driverLatitude, driverLongitude;
    ArrayAdapter adapter;
    LocationManager locationManager;
    LocationListener locationListener;
    ProgressBar progressBar3;
    TextView riderListTitle;
    
    DriverClass driverClass;
    UserClass userClass;
    LocationClass locationClass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_requests);
  
//        riderRequestsActivity = this;// Initializing this instance
        driverClass = new DriverClass(this); // So that DriverClass can call functions in this activity
        userClass = new UserClass(this); // So that UserClass can call functions in this activity
        locationClass = new LocationClass(this); // So that LocationClass can call functions in this activity
        
        
        riderRequestsListView = (ListView) findViewById(R.id.riderRequestsListView);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        riderListTitle = (TextView) findViewById(R.id.riderListTitle);

        progressBar3.setVisibility(View.VISIBLE);
        user_name = ParseUser.getCurrentUser().getUsername();

        nearbyRiderDistance.clear();
        nearbyRiderDistance.add("Getting nearby riders");

        UserClass currentUser = userClass.getCurrentUser();

        // Checking if the driver had already accepted a request previously
        driverClass.checkExistingRequest(currentUser.username, RiderRequestsActivity.this);
    
        // Constantly updating driver location
    
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        locationListener = LocationClass.getLocationListener(locationManager, RiderRequestsActivity.this);
        
//        locationListener = new LocationListener()
//        {
//            @Override
//            public void onLocationChanged(Location location)
//            {
//                // If user has logged out, then can't save the updated location
//                if (ParseUser.getCurrentUser() != null)
//                {
//                    ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
//                    driverLatitude = driverGeoPoint.getLatitude();
//                    driverLongitude = driverGeoPoint.getLongitude();
//                    Log.d(TAG, driverLatitude.toString());
//                    Log.d(TAG, driverLongitude.toString());
//                    ParseUser.getCurrentUser().put("User_Location", driverGeoPoint);
//                    ParseUser.getCurrentUser().saveInBackground();
//                }
//
//                // Continuously finding nearby riders.
//                findNearbyRiderDistance(location);
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle)
//            {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s)
//            {
//                if (ActivityCompat.checkSelfPermission(RiderRequestsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//                {
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//                }
//            }
//
//            @Override
//            public void onProviderDisabled(String s)
//            {
//                turnOnLocation();
//            }
//        };

    
    
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            Log.d(TAG, "here");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearbyRiderDistance);
        riderRequestsListView.setAdapter(adapter);

        riderRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (nearbyRiderUsername.size() > 0)
                {
                    final String nearbyRiderName = nearbyRiderUsername.get(i);
                    final Double nearbyRiderLat = nearbyRiderLatitude.get(i);
                    final Double nearbyRiderLong = nearbyRiderLongitude.get(i);

                    goToDriverMapActivity(nearbyRiderName, nearbyRiderLat, nearbyRiderLong, driverLatitude, driverLongitude);

                }
            }
        });
    }
    
    //region USER ACTIONS
    // NOTE: riderRequestsListView.setOnItemClickListener is in onCreate
    public void logoutTapped(View view)
    {
//        locationManager.removeUpdates(locationListener);
        ParseUser.logOut();
        finish();
    }
    
    //Disabling back button
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on the logout button to go back", Toast.LENGTH_SHORT).show();
    }
    //endregion
    
    
    //region PERMISSIONS
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
                    turnOnLocation();
                }
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
    //endregion
    
    
    
    //region HELPERS
    
    /**
     * A rider's location is saved in Uber_Requests only when they call an uber.
     * If they call an uber and then change their location, the location from which they booked is the one which is locked.
     * This method finds only those riders which have booked an uber
     */
    
    public void findNearbyRiderDistance(final Location location)
    {
        nearbyRiderDistance.clear();
        nearbyRiderDistance.add("Getting nearby riders");
        
        final ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        //Getting list of nearby riders
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereDoesNotExist("Driver_Name");
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if(e == null)
                {
                    nearbyRiderDistance.clear();
                    if(objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            Double distanceInMiles = (double) Math.round(driverGeoPoint.distanceInMilesTo(object.getParseGeoPoint("Rider_Location"))*10)/10;
                            nearbyRiderDistance.add(distanceInMiles+" mi.");
                
                            // Adding the rider's username and location to array lists which is passed on to the next activity
                            nearbyRiderUsername.add(object.getString("Rider_Name"));
                            nearbyRiderLatitude.add(object.getParseGeoPoint("Rider_Location").getLatitude());
                            nearbyRiderLongitude.add(object.getParseGeoPoint("Rider_Location").getLongitude());
                            
                            
                        }
                    }
                    else
                    {
                        nearbyRiderDistance.add("No nearby riders found");
                    }
                    // Coming back to main thread to update the listView
                    RiderRequestsActivity.this.runOnUiThread(new Runnable() {
                        public void run()
                        {
                            adapter.notifyDataSetChanged();
                            progressBar3.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else
                {
                    Toast.makeText(RiderRequestsActivity.this, "Check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void goToDriverMapActivity(String nearbyRiderName, Double nearbyRiderLat, Double nearbyRiderLong, Double driverLatitude, Double driverLongitude)
    {
        Intent intent = new Intent(RiderRequestsActivity.this, DriverMapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("riderUsername", nearbyRiderName);
        bundle.putDouble("riderLatitude", nearbyRiderLat);
        bundle.putDouble("riderLongitude", nearbyRiderLong);
        bundle.putDouble("driverLatitude", driverLatitude);
        bundle.putDouble("driverLongitude", driverLongitude);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    
    public void goToDriverMapActivity(HashMap<String, Object> bookingDetails)
    {
        progressBar3.setVisibility(View.INVISIBLE);
        
        if((Boolean) bookingDetails.get("isBookingEmpty"))
        {
            riderListTitle.setText("There are currently no bookings available");
        }
        else
        {
            Intent intent = new Intent(RiderRequestsActivity.this, DriverMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("riderUsername", bookingDetails.get("nearbyRiderName").toString());
            bundle.putDouble("riderLatitude", Double.valueOf(bookingDetails.get("nearbyRiderLat").toString()));
            bundle.putDouble("riderLongitude", Double.valueOf(bookingDetails.get("nearbyRiderLong").toString()));
            bundle.putDouble("driverLatitude", Double.valueOf(bookingDetails.get("driverLatitude").toString()));
            bundle.putDouble("driverLongitude", Double.valueOf(bookingDetails.get("driverLongitude").toString()));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    //endregion
}