package Model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.DriverMapActivity;

import java.util.List;

public class BookingClass
{
    private String TAG = "BookingClass";
    private static final long TIME_INTERVAL = 2000;
    
    private DriverMapActivity driverMapActivity;
    Handler handler;
    
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
        handler = new Handler();

        handler.postDelayed(new Runnable()
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
                                driverMapActivity.riderCancelsRequestResult(true);
                                Log.d(TAG, "cancelled");
                            }
                        }
                        else
                        {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "HERE: checkRiderCancelSRequest");
                            e.printStackTrace();
                        }
                    }
                });
                handler.postDelayed(this, TIME_INTERVAL);
            }
        }, TIME_INTERVAL);
    }
}
