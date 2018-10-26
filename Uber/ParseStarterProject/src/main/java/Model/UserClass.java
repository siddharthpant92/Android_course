package Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.MainActivity;

public class UserClass
{
    String TAG = "UserClass";
    ParseUser parseUser;
    MainActivity mainActivity = MainActivity.getInstance(); // Gets an object instance of MainActivity
    
    public String username, role;
    
    // Default constructor
    public UserClass()
    {
        username = null;
        role = null;
    }
    
    // Constructor to initialise object
    public UserClass(String user_name, String user_role)
    {
        username = user_name;
        role = user_role;
    }
    
    /**
     * Gets the currently logged in user
     * @return null or the currently logged in user
     */
    public UserClass getCurrentUser()
    {
        try
        {
            username = ParseUser.getCurrentUser().getUsername();
            role = ParseUser.getCurrentUser().getString("User_Role");
            return new UserClass(username, role);
        }
        catch(Exception e)
        {
            // User has not logged in
            return null;
        }
        
    }
    
    /**
     * Saving the user role in the server and then redirecting the user
     * @param user          Object instance of ParseUser
     * @param role          Role the user selected
     * @param isRedirecUser If the user should be redirected or not
     * @param context       The activity that called this function
     */
    public void saveUserRole(ParseUser user, final String role, final Boolean isRedirecUser, final Context context)
    {
        Log.d(TAG, role);
        user.put("User_Role", role);
    
        user.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)
                {
                    if(isRedirecUser)
                    {
                        mainActivity.redirectUser(role);
                    }
                }
                else
                {
                    Toast.makeText(context, "Check exception 2: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
}
