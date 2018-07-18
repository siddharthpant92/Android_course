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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RiderRequestsActivity extends Activity
{
    ListView riderRequestsListView;
    
    String tag = "RiderRequestsActivity", user_name;
    ArrayList<String> nearbyRiderDistance = new ArrayList<>();
    ArrayList<String> nearbyRiderUsername = new ArrayList<>();
    ArrayList<Double> nearbyRiderLatitude = new ArrayList<>();
    ArrayList<Double> nearbyRiderLongitude = new ArrayList<>();
    Double driverLatitude, driverLongitude;
    ArrayAdapter adapter;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_requests);
        
        riderRequestsListView = (ListView) findViewById(R.id.riderRequestsListView);
        user_name = ParseUser.getCurrentUser().getUsername();
    
        nearbyRiderDistance.clear();
        nearbyRiderDistance.add("Getting nearby riders");
    
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                driverLatitude = driverGeoPoint.getLatitude();
                driverLongitude = driverGeoPoint.getLongitude();
                ParseUser.getCurrentUser().put("User_Location", driverGeoPoint);
                ParseUser.getCurrentUser().saveInBackground();
                findNearbyRiderDistance(location);
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
        
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearbyRiderDistance);
        riderRequestsListView.setAdapter(adapter);
        
        riderRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                final String nearbyRiderName = nearbyRiderUsername.get(i);
                final Double nearbyRiderLat = nearbyRiderLatitude.get(i);
                final Double nearbyRiderLong = nearbyRiderLongitude.get(i);
    
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
        });
    }
    
    public  void logoutTapped(View view)
    {
        locationManager.removeUpdates(locationListener);
        ParseUser.logOut();
        finish();
    }
    
    //Disabling back button
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on the logout button to go back", Toast.LENGTH_SHORT).show();
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
                    findNearbyRiderDistance(lastKnownLocation);
                }
            }
        }
    }
    
    public void findNearbyRiderDistance(Location location)
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
                    if(objects.size() > 0)
                    {
                        nearbyRiderDistance.clear();
                        for(ParseObject object: objects)
                        {
                            Double distanceInMiles = (double) Math.round(driverGeoPoint.distanceInMilesTo(object.getParseGeoPoint("Rider_Location"))*10)/10;
                            nearbyRiderDistance.add(distanceInMiles+" mi.");
                            
                            // Adding the rider's usernames and location to array lists which is passed on to the next activity
                            nearbyRiderUsername.add(object.getString("Rider_Name"));
                            nearbyRiderLatitude.add(object.getParseGeoPoint("Rider_Location").getLatitude());
                            nearbyRiderLongitude.add(object.getParseGeoPoint("Rider_Location").getLongitude());
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(RiderRequestsActivity.this, "No uber riders found", Toast.LENGTH_SHORT).show();
                    }
                }
                else 
                {
                    Toast.makeText(RiderRequestsActivity.this, "Check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
