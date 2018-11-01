package com.parse.starter;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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
import java.util.HashMap;
import java.util.List;

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
    
    String TAG = "DriverMapActivity";
//    String riderUsername, driver_user_name;
//    Double riderLatitude,riderLongitude,driverLatitude, driverLongitude;
    Boolean isRequestAccepted;
    Handler handler;
    private GoogleMap mMap;
    UserClass userClass;
    
    private static final long TIME_INTERVAL = 2000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userClass = new UserClass(this);
    
        acceptRequestButton = (Button) findViewById(R.id.acceptRequestButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        
        isRequestAccepted = false; // Set to true when a driver accepts the request
        handler = new Handler();
        
        // Checking periodically if the rider cancels the request.
        checkRiderCancelsRequest();
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
        ParseUser.logOut();
        Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    
    public void acceptRequestTapped(View view)
    {
        if(!isRequestAccepted)
        {
            acceptUberRequest();
        }
        else
        {
            cancelUberRequest();
        }
    }
    //endregion
    
    //region MAP
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
                
                HashMap<String, Object> bookingDetails  = getBookingDetails();
                
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
    /**
     * The driver accepts the uber requests.
     * The location for both the rider and the driver are sent to google maps.
     */
    public void acceptUberRequest()
    {
//        acceptRequestButton.setText("Cancel Current Request");
//        isRequestAccepted = true;
//        logoutButton.setVisibility(View.INVISIBLE);
//
//        // Finding the uber request so that the driver can be added.
//        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
//        query.whereEqualTo("Rider_Name", riderUsername);
//        query.findInBackground(new FindCallback<ParseObject>()
//        {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e)
//            {
//                if (e == null)
//                {
//                    if (objects.size() > 0)
//                    {
//                        for (ParseObject object : objects)
//                        {
//                            // Adding the driver to that uber requests
//                            object.put("Driver_Name", ParseUser.getCurrentUser().getUsername());
//                            object.saveInBackground(new SaveCallback()
//                            {
//                                @Override
//                                public void done(ParseException e)
//                                {
//                                    if (e == null)
//                                    {
//                                        Toast.makeText(DriverMapActivity.this, "Uber booked", Toast.LENGTH_SHORT).show();
//
//                                        //Launching google maps intent
//                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                                                Uri.parse("http://maps.google.com/maps?saddr=" + driverLatitude + "," + driverLongitude + "&daddr=" + riderLatitude + "," + riderLongitude));
//                                        startActivity(intent);
//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(DriverMapActivity.this, "Check exception 2: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                    else
//                    {
//                        Toast.makeText(DriverMapActivity.this, "Could not find the selected request.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    acceptRequestButton.setText("Accept Request");
//                    isRequestAccepted = false;
//                    logoutButton.setVisibility(View.VISIBLE);
//
//                    Toast.makeText(DriverMapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "HERE: acceptUberRequest");
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    
    /**
     * The driver cancels the request and the rider should be notified.
     * The driver's name is removed from Uber_Request and the rider goes back to waiting for a driver to accept the request.
     */
    public void cancelUberRequest()
    {
//        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
//        query.whereEqualTo("Rider_Name", riderUsername);
//        query.whereEqualTo("Driver_Name", driver_user_name);
//        query.findInBackground(new FindCallback<ParseObject>()
//        {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e)
//            {
//                if(e == null)
//                {
//                    if(objects.size() > 0)
//                    {
//                        for(ParseObject object: objects)
//                        {
//                            object.remove("Driver_Name");
//                            object.saveInBackground(new SaveCallback()
//                            {
//                                @Override
//                                public void done(ParseException e)
//                                {
//                                    if(e == null)
//                                    {
//                                        isRequestAccepted = false;
//                                        logoutButton.setVisibility(View.VISIBLE);
//                                        acceptRequestButton.setText("Accept Request");
//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(DriverMapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, "HERE: cancelUberRequest");
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                    else
//                    {
//                        Toast.makeText(DriverMapActivity.this, "Request not found", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(DriverMapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "HERE: cancelUberRequest");
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    //endregion
    
    //region HELPERS
    /**
     * If the rider cancels the request then the driver should be notified.
     * If the rider cancels the request, then the request is deleted from Uber_Request, the query doesn't return anything and isRiderRequestActive gets set to false.
     * If the driver cancels the request, the driver cancels the
     */
    public void checkRiderCancelsRequest()
    {
//        final Boolean[] isRiderRequestActive = {true};
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                if(isRiderRequestActive[0])
//                {
//                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
//                    query.whereEqualTo("Rider_Name", riderUsername);
//                    query.findInBackground(new FindCallback<ParseObject>()
//                    {
//                        @Override
//                        public void done(List<ParseObject> objects, ParseException e)
//                        {
//                            if(e == null)
//                            {
//                                // The request was not found which means the rider cancelled it.
//                                if(objects.size() == 0)
//                                {
//                                    Toast.makeText(DriverMapActivity.this, "Request has been cancelled", Toast.LENGTH_SHORT).show();
//                                    isRequestAccepted = false;
//                                    isRiderRequestActive[0] = false;
//                                    logoutButton.setVisibility(View.VISIBLE);
//                                    acceptRequestButton.setText("Accept Request");
//                                    mMap.clear();
//                                    finish();
//                                }
//                            }
//                            else
//                            {
//                                Toast.makeText(DriverMapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, "HERE: checkRiderCancelSRequest");
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//                handler.postDelayed(this, TIME_INTERVAL);
//            }
//        }, TIME_INTERVAL);
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
            bookingDetails.put("driverLatitude", bundle.getDouble("driverLatitude"));
            bookingDetails.put("driverLongitude", bundle.getDouble("driverLongitude"));
        }
        else
        {
            Toast.makeText(DriverMapActivity.this, "There was some problem in getting the booking details. please try acceptig again", Toast.LENGTH_SHORT);
        }
        return bookingDetails;
    }
    //endregion
}
