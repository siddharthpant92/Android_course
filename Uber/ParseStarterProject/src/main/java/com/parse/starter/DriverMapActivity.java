package com.parse.starter;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
    
        Bundle bundle = getIntent().getExtras();
        riderUsername = bundle.getString("riderUsername");
        riderLatitude = bundle.getDouble("riderLatitude");
        riderLongitude = bundle.getDouble("riderLongitude");
        driverLatitude = bundle.getDouble("driverLatitude");
        driverLongitude = bundle.getDouble("driverLongitude");
    
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
