package com.example.siddharthpant.memorable_places;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;

    String tag = "MapsActivty", newLoc;
    List<Address> addresses; //The address based on the latitude and longitude
    ArrayList<String> places = new ArrayList<String>(); //List of all places
    Integer index; //The index of the place the user selected
    Bundle bundle;
    Double latitude, longitude;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.memorable_places", Context.MODE_PRIVATE);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                newLoc = "";
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                geocoder = new Geocoder(MapsActivity.this);

                try
                {
                    //Getting the location of where the user clicked
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                }
                catch (IOException e)
                {
                    Toast.makeText(MapsActivity.this, "Location could not be found", Toast.LENGTH_SHORT).show();
                    addresses.clear();
                }

                //If something is returned after the user clicked on the map
                if(addresses.size() > 0)
                {
                    if(addresses.get(0).getLocality() != null)
                    {
                        newLoc = addresses.get(0).getLocality()+", ";
                    }
                    if(addresses.get(0).getAdminArea() != null)
                    {
                        newLoc += addresses.get(0).getAdminArea()+"\n";
                    }
                    if(addresses.get(0).getCountryName() != null)
                    {
                        newLoc += addresses.get(0).getCountryName();
                    }

                    //Asking the user to confirm the location
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Your new location")
                            .setMessage(newLoc);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //When the user confirm to add the location
                            MainActivity.places.add(newLoc);
                            MainActivity.latitudes.add(latitude);
                            MainActivity.longitudes.add(longitude);

                            try
                            {
                                sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
                                sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(MainActivity.latitudes)).apply();
                                sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(MainActivity.longitudes)).apply();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            try
                            {
                                places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            MainActivity.adapter.notifyDataSetChanged();

                            //Adding the new location to the map
                            displayLocation(latitude, longitude, newLoc);;
                        }
                    });
                    builder.show();
                }
                else
                {
                    Toast.makeText(MapsActivity.this, "Location could not be found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            Log.d(tag, "permission already granted");
        }

        bundle = getIntent().getExtras();
        index = bundle.getInt("index");
        if(index != -99)
        {
            mMap.clear();
            //Adding the selected location to the map
            displayLocation(MainActivity.latitudes.get(index), MainActivity.longitudes.get(index), MainActivity.places.get(index));
        }
        else
        {
            mMap.clear();
            for (int index=0; index<MainActivity.places.size(); index++)
            {
                //Adding all previous locations
                displayLocation(MainActivity.latitudes.get(index), MainActivity.longitudes.get(index), MainActivity.places.get(index));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void displayLocation(Double lats, Double longs, String customTitle)
    {
        LatLng addLocation = new LatLng(lats, longs);
        mMap.addMarker(new MarkerOptions().position(addLocation).title(customTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(addLocation));
    }
}