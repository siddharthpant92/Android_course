package Model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.DriverMapActivity;

import java.util.List;

public class BookingClass
{
    private String TAG = "BookingClass";
    private static final long TIME_INTERVAL = 2000;
    public  Runnable runnable;
    
    private DriverMapActivity driverMapActivity;
    
    public BookingClass(DriverMapActivity driverMapActivity)
    {
        this.driverMapActivity = driverMapActivity;
    }
    
    /**
     * If the rider cancels the request then the driver should be notified.
     * If the rider cancels the request, then the request is deleted from Uber_Request, the query doesn't return anything and isRiderRequestActive gets set to false.
     * If the driver cancels the request, the driver cancels the
     */
    public void checkRiderCancelsRequest(final Context context, final String riderUsername)
    {
        final Handler handler = new Handler();
        runnable =  new Runnable()
        {
            @Override
            public void run()
            {
                ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
                query.whereEqualTo("Rider_Name", riderUsername);
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e)
                    {
                        if(e == null)
                        {
                            // The request was not found which means the rider cancelled it.
                            if(objects.size() == 0)
                            {
                                handler.removeCallbacks(runnable); // No need to keep checking if the rider has been cancelled
                                runnable = null;
                                driverMapActivity.riderCancelsRequestResult();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                });
                handler.postDelayed(runnable, TIME_INTERVAL); // This is to execute the runnable periodically
            }
        };
    
        handler.postDelayed(runnable, TIME_INTERVAL); // This is to start the runnable
    }
    
    /**
     * The driver accepts the uber requests.
     * The location for both the rider and the driver are sent to google maps.
     * @param context
     * @param riderUsername
     * @param driverUsername
     */
    public void driverAcceptUberRequest(final Context context, String riderUsername, final String driverUsername)
    {
        // Finding the uber request so that the driver can be added.
        ParseQuery<ParseObject> query = new ParseQuery<>("Uber_Request");
        query.whereEqualTo("Rider_Name", riderUsername);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> objects, ParseException e)
            {
                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                        for (ParseObject object : objects)
                        {
                            // Adding the driver to that uber requests
                            object.put("Driver_Name", driverUsername);
                            object.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if (e == null)
                                    {
                                        driverMapActivity.driverIsAssigned(true);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Check exception 2: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        driverMapActivity.driverIsAssigned(false);
                    }
                }
                else
                {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HERE: acceptUberRequest");
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * The driver cancels the request and the rider should be notified.
     * The driver's name is removed from Uber_Request and the rider goes back to waiting for a driver to accept the request.
     * @param context
     * @param riderUsername
     * @param driverUsername
     */
    public void driverCancelUberRequest(final Context context, String riderUsername, String driverUsername)
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Uber_Request");
        query.whereEqualTo("Rider_Name", riderUsername);
        query.whereEqualTo("Driver_Name", driverUsername);
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
                            object.remove("Driver_Name");
                            object.saveInBackground(new SaveCallback()
                            {
                                @Override
                                public void done(ParseException e)
                                {
                                    if(e == null)
                                    {
                                        driverMapActivity.driverIsAssigned(false);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "HERE: cancelUberRequest");
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Request not found. Please go back and try again", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "HERE: cancelUberRequest");
                    e.printStackTrace();
                }
            }
        });
    }
}
