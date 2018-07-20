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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback
{
    Button acceptRequestButton;
    ProgressBar progressBar2;
    
    String riderUsername, tag = "DriverMapActivity";
    Double riderLatitude,riderLongitude,driverLatitude, driverLongitude;
    private GoogleMap mMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    
        acceptRequestButton = (Button) findViewById(R.id.acceptRequestButton);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
    }
    
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    
        ConstraintLayout mapLayout = (ConstraintLayout)findViewById(R.id.activityLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                progressBar2.setVisibility(View.INVISIBLE);
                
                Bundle bundle = getIntent().getExtras();
                riderUsername = bundle.getString("riderUsername");
                riderLatitude = bundle.getDouble("riderLatitude", 0);
                riderLongitude = bundle.getDouble("riderLongitude", 0);
                driverLatitude = bundle.getDouble("driverLatitude", 0);
                driverLongitude = bundle.getDouble("driverLongitude", 0);
            
                LatLng driverLocation = new LatLng(driverLatitude, driverLongitude);
                LatLng riderLocation = new LatLng(riderLatitude, riderLongitude);
            
                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver: You")));
                markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Rider: "+riderUsername).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            
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
        });
    }
    
    public void acceptRequestTapped(View view)
    {
        // Finding the uber request so that the driver can be added.
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Rider_Name", riderUsername);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            // Adding the driver to that uber requests
                            object.put("Driver_Name", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if(e == null)
                                    {
                                        Toast.makeText(DriverMapActivity.this, "Uber booked", Toast.LENGTH_SHORT).show();
                                        //Launching google maps intent
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                                    Uri.parse("http://maps.google.com/maps?saddr="+driverLatitude+","+driverLongitude+"&daddr="+riderLatitude+","+riderLongitude));
                                        startActivity(intent);
                                        acceptRequestButton.setText("Already accepted an Uber");
                                        acceptRequestButton.setEnabled(false);
                                        // Call method on rider side to show that driver has been booked? Or handle that some way?
    
                                    }
                                    else
                                    {
                                        Toast.makeText(DriverMapActivity.this, "Check exception 2: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(DriverMapActivity.this, "Could not find the selected request. ", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(DriverMapActivity.this, "Check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
