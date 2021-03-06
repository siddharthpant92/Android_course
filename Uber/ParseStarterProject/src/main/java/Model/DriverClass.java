package Model;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.RiderRequestsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DriverClass
{
    private String TAG = "DriverClass";
    private ArrayList<String> nearbyRiderDistance = new ArrayList<>();
    private ArrayList<String> nearbyRiderUsername = new ArrayList<>();
    private ArrayList<Double> nearbyRiderLatitude = new ArrayList<>();
    private ArrayList<Double> nearbyRiderLongitude = new ArrayList<>();
    
    private RiderRequestsActivity riderRequestsActivity;
    
    public DriverClass(RiderRequestsActivity riderRequestsActivity)
    {
        this.riderRequestsActivity = riderRequestsActivity;
    }
    
    /**
     * When a driver logs in, checking if driver has already accepted a request.
     * If the user has already accepted a request, getting the details and going to DriverMapActivity
     * @param username  Userame of currently logged in user
     * @param context   Activity that called this function
     */
    public void checkExistingRequest(String username, final Context context)
    {
        final HashMap<String, Object> bookingDetails = new HashMap<>();
    
        UserClass userClass = new UserClass();
        final UserClass currentUser = userClass.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Driver_Name", username);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        bookingDetails.put("nearbyRiderName", objects.get(0).getString("Rider_Name"));
                        bookingDetails.put("nearbyRiderLat",objects.get(0).getParseGeoPoint("Rider_Location").getLatitude());
                        bookingDetails.put("nearbyRiderLong", objects.get(0).getParseGeoPoint("Rider_Location").getLongitude());
                        bookingDetails.put("driverUsername", currentUser.username);
                        bookingDetails.put("driverLat", currentUser.user_latitude);
                        bookingDetails.put("driverLong",currentUser.user_longitude);
                        bookingDetails.put("isExistingBooking", false);
                    }
                    else
                    {
                        bookingDetails.put("isExistingBooking", true);
                    }
                    riderRequestsActivity.goToDriverMapActivity(bookingDetails);
                }
                else
                {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HERE: checkExistingRequest");
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    /**
     * A rider's location is saved in Uber_Requests only when they call an uber.
     * If they call an uber and then change their location, the location from which they booked is the one which is locked.
     * This method finds only those riders which have booked an uber
     */
    
    public void findNearbyRiderDistance(final Location location, final Context context)
    {
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
                    nearbyRiderDistance.clear();
                    nearbyRiderUsername.clear();
                    nearbyRiderLatitude.clear();
                    nearbyRiderLongitude.clear();
                    
                    if(objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            Double distanceInMiles = (double) Math.round(driverGeoPoint.distanceInMilesTo(object.getParseGeoPoint("Rider_Location"))*10)/10;
                            nearbyRiderDistance.add(distanceInMiles+" mi.");
                            
                            // Adding the rider's username and location to array lists which is passed on to the next activity
                            nearbyRiderUsername.add(object.getString("Rider_Name"));
                            nearbyRiderLatitude.add(object.getParseGeoPoint("Rider_Location").getLatitude());
                            nearbyRiderLongitude.add(object.getParseGeoPoint("Rider_Location").getLongitude());
                        }
                    }
                    else
                    {
                        nearbyRiderDistance.add("No nearby riders found");
                    }
                    
                    riderRequestsActivity.updateAvailableRiderList(nearbyRiderDistance, nearbyRiderUsername, nearbyRiderLatitude, nearbyRiderLongitude);
                }
                else
                {
                    Toast.makeText(context, "Check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
