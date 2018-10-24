package Model;

import android.util.Log;

import com.parse.ParseUser;

public class UserClass
{
    String TAG = "UserClass";
    ParseUser parseUser;
    
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
}
