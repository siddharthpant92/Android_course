package Model;

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
    
    public UserClass getCurrentUser()
    {
        username = ParseUser.getCurrentUser().getUsername();
        role = ParseUser.getCurrentUser().getString("User_Role");
        
        return new UserClass(username, role);
    }
}
