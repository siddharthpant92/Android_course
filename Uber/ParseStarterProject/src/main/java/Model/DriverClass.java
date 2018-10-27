package Model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.RiderRequestsActivity;

import java.util.HashMap;
import java.util.List;

public class DriverClass
{
    String TAG = "DriverClass";
    
    RiderRequestsActivity riderRequestsActivity;
    
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
        
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Driver_Name", username);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                Log.d(TAG, String.valueOf(objects.size()));
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        bookingDetails.put("nearbyRiderName", objects.get(0).getString("Rider_Name"));
                        bookingDetails.put("nearbyRiderLat",objects.get(0).getParseGeoPoint("Rider_Location").getLatitude());
                        bookingDetails.put("nearbyRiderLong", objects.get(0).getParseGeoPoint("Rider_Location").getLongitude());
                        bookingDetails.put("driverLat", ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLatitude());
                        bookingDetails.put("driverLong",ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLongitude());
                        
                        riderRequestsActivity.goToDriverMapActivity(bookingDetails);
                    }
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
}
