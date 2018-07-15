package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Intent intent;

    String tag = "DriverLocationActivity", username;

    Button acceptRequestButton;

    LatLng driverLocation, requestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        acceptRequestButton = (Button) findViewById(R.id.acceptRequestButton);

        intent = getIntent();
        username = intent.getStringExtra("username");


        // NOTE: SOMETIMES THIS CRASHES, OTHER TIMES, GLOBAL LISTENER CRASHES. NOT SURE WHAT THE PROBLEM IS
//        ConstraintLayout mapLayout = (ConstraintLayout) findViewById(R.id.mapLayout);
//        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Add a marker in Sydney and move the camera
//                driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0), intent.getDoubleExtra("driverLongitude", 0));
//                requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude", 0), intent.getDoubleExtra("requestLongitude", 0));
////        mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location as Driver"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLocation));
//
//                // Setting zoom level so that we can see both locations on the map properly
//                ArrayList<Marker> markers = new ArrayList<>();
//                markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location as Driver")));
//                markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Customer request Location")));
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                for(Marker marker: markers)
//                {
//                    builder.include(marker.getPosition());
//                }
//                LatLngBounds bounds = builder.build();
//
//                int padding = 30;
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mMap.animateCamera(cameraUpdate);
//            }
//        });
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
        mMap.clear();

        // NOTE: SOMETIMES THIS CRASHES, OTHER TIMES, GLOBAL LISTENER CRASHES. NOT SURE WHAT THE PROBLEM IS

        // Add a marker in Sydney and move the camera
        driverLocation = new LatLng(intent.getDoubleExtra("driverLatitude", 0), intent.getDoubleExtra("driverLongitude", 0));
        requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude", 0), intent.getDoubleExtra("requestLongitude", 0));
//        mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location as Driver"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLocation));

        // Setting zoom level so that we can see both locations on the map properly
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your location as Driver")));
        markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Customer request Location")));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker: markers)
        {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 30;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cameraUpdate);

    }


    public void acceptRequestTapped(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Uber_Request");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null)
                                    {
                                        // Getting directions between locations
                                        String mapUri = "http://maps.google.com/maps?saddr="+driverLocation.latitude+","+driverLocation.longitude+"&daddr="+requestLocation.latitude+","+requestLocation.longitude;
                                        Log.d(tag, mapUri);
                                        Intent directionsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
                                        startActivity(directionsIntent);
                                    }
                                    else
                                    {
                                        Toast.makeText(DriverLocationActivity.this, "Issue in adding driver username. See Logs", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(DriverLocationActivity.this, "No uber request found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
