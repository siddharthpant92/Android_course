package com.parse.starter;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

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

import java.util.ArrayList;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback
{
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
}
