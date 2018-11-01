package Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.starter.MainActivity;

public class UserClass
{
    private String TAG = "UserClass";
    private MainActivity mainActivity; // Gets an object instance of MainActivity
    
    public String username, role;
    public Double user_latitude, user_longitude;
    
    // Default constructor
    public UserClass()
    {
        username = null;
        role = null;
        user_latitude = null;
        user_longitude = null;
    }
    
    // Constructor to initialise object
    public UserClass(String user_name, String user_role, Double userLatitude, Double userLongitude)
    {
        username = user_name;
        role = user_role;
        user_latitude = userLatitude;
        user_longitude = userLongitude;
    }
    
    public UserClass(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
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
            user_latitude = ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLatitude();
            user_longitude = ParseUser.getCurrentUser().getParseGeoPoint("User_Location").getLongitude();
            return new UserClass(username, role, user_latitude, user_longitude);
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
     * @param isRedirectUser If the user should be redirected or not
     * @param context       The activity that called this function
     */
    private void saveUserRole(ParseUser user, final String role, final Boolean isRedirectUser, final Context context)
    {
        user.put("User_Role", role);
    
        user.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)
                {
                    if(isRedirectUser)
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
    
    /**
     *
     * @param username      Username of user logging in
     * @param password      Password of user logging in
     * @param selectedRole  Role selected by the user
     * @param context       Activity calling this function
     */
    public void userLogin(String username, String password, final String selectedRole, final Context context)
    {
        ParseUser pu = new ParseUser();
        pu.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(final ParseUser parseUser, ParseException e) {
                if(parseUser != null && e == null)
                {
                    // Checking role of user trying to login
                    String userRole = String.valueOf(parseUser.get("User_Role"));
                    if(userRole.equals(selectedRole))
                    {
                        saveUserRole(parseUser, selectedRole, true, context);
                    }
                    else
                    {
                        Toast.makeText(context, "In correct role. Cannot log in", Toast.LENGTH_SHORT).show();
                        ParseUser.logOut();
                    }
                }
                else
                {
                    Toast.makeText(context, "check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * @param username      Username of user signing up
     * @param password      Password of user signing up
     * @param selectedRole  Role selected by the user
     * @param context       Activity calling this function
     */
    public void userSignup(String username, String password, final String selectedRole, final Context context)
    {
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
    
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    saveUserRole(user, selectedRole, true, context);
                }
                else
                {
                    Toast.makeText(context, "Check exception 3: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void saveUserLocation(ParseGeoPoint driverGeoPoint)
    {
        // If user has logged out, cant save location
        if(ParseUser.getCurrentUser() != null)
        {
            ParseUser.getCurrentUser().put("User_Location", driverGeoPoint);
            ParseUser.getCurrentUser().saveInBackground();
        }
        
    }
}
