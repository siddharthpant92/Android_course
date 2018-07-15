package com.parse.starter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends Activity {

    ListView listView;

    ArrayList<String> requests = new ArrayList<String>();
    ArrayList<Double> requestLatitudes = new ArrayList<Double>();
    ArrayList<Double> requestLongitudes = new ArrayList<Double>();
    ArrayList<String> usernames = new ArrayList<String>();
    ArrayAdapter adapter;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;

    String tag = "ViewRequestsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
        requests.clear();
        requestLatitudes.clear();
        requestLongitudes.clear();
        requests.add("Getting nearby requests....");
        listView.setAdapter(adapter);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateListView(location);
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateListView(lastKnownLocation);
            }
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ActivityCompat.checkSelfPermission(ViewRequestsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    // The last location that was used when "call uber" was clicked is the drivers location
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d(tag, requestLatitudes.size()+" , "+requestLongitudes.size()+" , "+i+" , "+lastKnownLocation);
                    if(requestLatitudes.size() >= i+1 && requestLatitudes.size() >= i+1 && lastKnownLocation != null && usernames.size() >= i+1)
                    {
                        Intent intent = new Intent(ViewRequestsActivity.this, DriverLocationActivity.class);
                        intent.putExtra("requestLatitude", requestLatitudes.get(i));
                        intent.putExtra("requestLongitude", requestLongitudes.get(i));
                        intent.putExtra("driverLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnownLocation.getLongitude());
                        intent.putExtra("username", usernames.get(i));
                        startActivity(intent);
                    }
                }
            }
        });
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

    public void updateListView(final Location location)
    {
        if(location != null)
        {
            requests.clear();
            requestLatitudes.clear();
            requestLongitudes.clear();

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
            final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            query.whereNear("user_location", geoPointLocation);
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null)
                    {
                        if(objects.size() > 0)
                        {
                            for(ParseObject object: objects)
                            {
                                ParseGeoPoint requestLocation = (ParseGeoPoint) object.get("user_location");
                                if(requestLocation != null)
                                {
                                    //Gives the distance between the driver's current location and locations of other requests.
                                    Double distanceInMiles = Double.valueOf(Math.round(geoPointLocation.distanceInMilesTo((ParseGeoPoint) requestLocation) * 10) / 10);
                                    requests.add(distanceInMiles + " miles");
                                    requestLatitudes.add(requestLocation.getLatitude());
                                    requestLongitudes.add(requestLocation.getLongitude());
                                    usernames.add(object.getString("username"));
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(ViewRequestsActivity.this, "No nearby drivers found", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
