package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.parse.ParseUser;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;

import Model.BookingClass;
import Model.UserClass;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback
{
    /**
     * Not needed to update the driver's location here because once they accept the request, google maps is launched which handles that
     */
    Button acceptRequestButton;
    Button logoutButton;
    ProgressBar progressBar2;
    
    private String TAG = "DriverMapActivity";
    Boolean isRequestAccepted;
    private GoogleMap mMap;
    
    UserClass userClass;
    BookingClass bookingClass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        userClass = new UserClass();
        bookingClass = new BookingClass(this);
    
        acceptRequestButton = (Button) findViewById(R.id.acceptRequestButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        
        isRequestAccepted = false; // Set to true when a driver accepts the request
    }
    
    //region USER ACTIONS
    @Override
    public void onBackPressed()
    {
        if(isRequestAccepted)
        {
            Toast.makeText(this, "You cannot go back to accept another request. Please cancel this request first before you go back", Toast.LENGTH_SHORT).show();
        }
        else
        {
            super.onBackPressed();
        }
    }
    
    public void logoutTapped(View view)
    {
        RiderRequestsActivity.stopLocationUpdates();
        ParseUser.logOut();
        Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    
    public void acceptRequestTapped(View view)
    {
        final HashMap<String, Object> bookingDetails  = getBookingDetails();
        
        if(!isRequestAccepted)
        {
            bookingClass.driverAcceptUberRequest(DriverMapActivity.this, bookingDetails.get("riderUsername").toString(), bookingDetails.get("driverUsername").toString());
        }
        else
        {
           bookingClass.driverCancelUberRequest(DriverMapActivity.this, bookingDetails.get("riderUsername").toString(), bookingDetails.get("driverUsername").toString());
        }
    }
    //endregion
    
    //region MAP
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    
        final HashMap<String, Object> bookingDetails  = getBookingDetails();
    
        // Checking periodically if the rider cancels the request.
        bookingClass.checkRiderCancelsRequest(DriverMapActivity.this, bookingDetails.get("riderUsername").toString());
    
        ConstraintLayout mapLayout = (ConstraintLayout)findViewById(R.id.activityLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                progressBar2.setVisibility(View.INVISIBLE);
                
                LatLng driverLocation = new LatLng((Double) bookingDetails.get("driverLatitude"), (Double) bookingDetails.get("driverLongitude"));
                LatLng riderLocation = new LatLng((Double) bookingDetails.get("riderLatitude"), (Double) bookingDetails.get("riderLongitude"));
            
                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver: You")));
                markers.add(mMap.addMarker(new MarkerOptions().position(riderLocation).title("Rider: "+bookingDetails.get("riderUsername")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            
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
    //endregion
    
    //region UBER REQUEST HANDLERS
    

    //endregion
    
    //region HELPERS
    public void riderCancelsRequestResult()
    {
        Toast.makeText(DriverMapActivity.this, "Request has been cancelled", Toast.LENGTH_SHORT).show();
        isRequestAccepted = false;
        isRequestAccepted = false;
        logoutButton.setVisibility(View.VISIBLE);
        acceptRequestButton.setText("Accept Request");
        mMap.clear();
        finish();
    }
    
    public HashMap<String, Object> getBookingDetails()
    {
        HashMap<String, Object> bookingDetails = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
    
        if (bundle != null)
        {
            bookingDetails.put("riderUsername", bundle.getString("riderUsername"));
            bookingDetails.put("riderLatitude", bundle.getDouble("riderLatitude"));
            bookingDetails.put("riderLongitude", bundle.getDouble("riderLongitude"));
            bookingDetails.put("driverUsername", bundle.getString("driverUsername"));
            bookingDetails.put("driverLatitude", bundle.getDouble("driverLatitude"));
            bookingDetails.put("driverLongitude", bundle.getDouble("driverLongitude"));
        }
        else
        {
            Toast.makeText(DriverMapActivity.this, "There was some problem in getting the booking details. please try acceptig again", Toast.LENGTH_SHORT).show();
        }
        return bookingDetails;
    }
    
    public void driverIsAssigned(boolean isDriverAssigned)
    {
        HashMap<String, Object> bookingDetails  = getBookingDetails();
        
        if(isDriverAssigned)
        {
            isRequestAccepted = true;
            logoutButton.setVisibility(View.INVISIBLE);
            acceptRequestButton.setText("Cancel Current Request");
    
            Toast.makeText(DriverMapActivity.this, "Uber booked", Toast.LENGTH_SHORT).show();
    
            Double driverLatitude = (Double) bookingDetails.get("driverLatitude");
            Double driverLongitude = (Double) bookingDetails.get("driverLongitude");
            Double riderLatitude = (Double) bookingDetails.get("riderLatitude");
            Double riderLongitude = (Double) bookingDetails.get("riderLongitude");
            //Launching google maps intent
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + driverLatitude + "," + driverLongitude + "&daddr=" + riderLatitude + "," + riderLongitude));
            startActivity(intent);
        }
        else
        {
            isRequestAccepted = false;
            logoutButton.setVisibility(View.VISIBLE);
            acceptRequestButton.setText("Accept Request");
    
            Toast.makeText(DriverMapActivity.this, "Ride has been cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}
