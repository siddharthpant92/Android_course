package com.parse.starter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

import Model.DriverClass;
import Model.LocationClass;
import Model.UserClass;

public class RiderRequestsActivity extends Activity
{
    ListView riderRequestsListView;
    
    String TAG = "RiderRequestsActivity";
    ArrayAdapter<String> adapter;
    static LocationManager locationManager;
    static LocationListener locationListener;
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
  
        driverClass = new DriverClass(this); // So that DriverClass can call functions in this activity
        userClass = new UserClass(this);
        locationClass = new LocationClass(); // So that LocationClass can call functions in this activity
        
        riderRequestsListView = (ListView) findViewById(R.id.riderRequestsListView);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        riderListTitle = (TextView) findViewById(R.id.riderListTitle);

        progressBar3.setVisibility(View.VISIBLE);

        final UserClass currentUser = userClass.getCurrentUser();
        if(currentUser != null) // In this case location might not be saved either, which means ride could not have been booked
        {
            // Checking if the driver had already accepted a request previously
            driverClass.checkExistingRequest(currentUser.username, RiderRequestsActivity.this);
        }
        
        // Constantly updating driver location
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                userClass.saveUserLocation(driverGeoPoint); // Can't use currentUser as it might be null if location wasn't saved earlier
                
                // Continuously finding nearby riders.
                driverClass.findNearbyRiderDistance(location, RiderRequestsActivity.this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {
                if (ActivityCompat.checkSelfPermission(RiderRequestsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }

            @Override
            public void onProviderDisabled(String s)
            {
                locationClass.turnOnLocation(RiderRequestsActivity.this);
            }
        };

    
    
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    
    //region USER ACTIONS
    public void logoutTapped(View view)
    {
        ParseUser.logOut();
        stopLocationUpdates();
        
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
                    locationClass.turnOnLocation(RiderRequestsActivity.this);
                }
            }
        }
    }
    //endregion
    
    //region HELPERS
    public static void stopLocationUpdates()
    {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
    public void goToDriverMapActivity(HashMap<String, Object> bookingDetails)
    {
        progressBar3.setVisibility(View.INVISIBLE);
        
        if((Boolean) bookingDetails.get("isExistingBooking"))
        {
            // See if there are nearby riders
            riderListTitle.setText("There is no existing booking");
            
            // drierClass.findNearbyRiderDistance is called automatically from onLocationChanged
        }
        else
        {
            Intent intent = new Intent(RiderRequestsActivity.this, DriverMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("riderUsername", bookingDetails.get("nearbyRiderName").toString());
            bundle.putDouble("riderLatitude", Double.valueOf(bookingDetails.get("nearbyRiderLat").toString()));
            bundle.putDouble("riderLongitude", Double.valueOf(bookingDetails.get("nearbyRiderLong").toString()));
            bundle.putString("driverUsername", bookingDetails.get("driverUsername").toString());
            bundle.putDouble("driverLatitude", Double.valueOf(bookingDetails.get("driverLat").toString()));
            bundle.putDouble("driverLongitude", Double.valueOf(bookingDetails.get("driverLong").toString()));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    
    public void updateAvailableRiderList(ArrayList<String> nearbyRiderDistance, final ArrayList<String> nearbyRiderUsername, final ArrayList<Double> nearbyRiderLatitude, final ArrayList<Double> nearbyRiderLongitude)
    {
        final HashMap<String, Object> bookingDetails = new HashMap<>();
        final UserClass currentUser = userClass.getCurrentUser();
    
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nearbyRiderDistance);
        riderRequestsListView.setAdapter(adapter);
    
        riderRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (nearbyRiderUsername.size() > 0)
                {
                    bookingDetails.put("nearbyRiderName", nearbyRiderUsername.get(i));
                    bookingDetails.put("nearbyRiderLat",nearbyRiderLatitude.get(i));
                    bookingDetails.put("nearbyRiderLong", nearbyRiderLongitude.get(i));
                    bookingDetails.put("driverUsername", currentUser.username);
                    bookingDetails.put("driverLat", currentUser.user_latitude);
                    bookingDetails.put("driverLong", currentUser.user_longitude);
                    bookingDetails.put("isExistingBooking", false);
                    
                    goToDriverMapActivity(bookingDetails);
                }
            }
        });
    }
    //endregion
}