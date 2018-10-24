/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

import Model.UserClass;


public class MainActivity extends Activity
{
    Switch userRoleSwitch;
    Button loginButton, signupButton;
    TextView usernameTextView, passwordTextView;
    
    String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        loginButton = (Button) findViewById(R.id.loginButton);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        UserClass userClass = new UserClass();
    
        UserClass currentUser = userClass.getCurrentUser();
        
        // Checking if user is already logged in
        if(currentUser != null)
        {
            Toast.makeText(this, "Logged in as:  "+currentUser.username, Toast.LENGTH_SHORT).show();
            redirectUser(currentUser);
        }
        
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
    
    //region USER ACTIONS
    
    public void loginTapped(View view)
    {
        final String selectedRole = getSelectedUserRole();
        
        Map<String, Object> userCredentials;
        userCredentials = getCredentials();
    
        if((Boolean) userCredentials.get("isValid"))
        {
            final String username = (String) userCredentials.get("username");
            String password = (String) userCredentials.get("password");
            
            ParseUser user = new ParseUser();
            user.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(final ParseUser user, ParseException e) {
                    if(user != null && e == null)
                    {
                        // Checking role of user trying to login
                        String userRole = String.valueOf(user.get("User_Role"));
                        if(userRole.equals(selectedRole))
                        {
                            saveUserRole(user, selectedRole, username);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "In correct role. Cannot log in", Toast.LENGTH_SHORT).show();
                            ParseUser.logOut();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "check exception 1: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void signupTapped(View view)
    {
        Map<String, Object> userCredentials;
        userCredentials = getCredentials();
    
        if((Boolean) userCredentials.get("isValid"))
        {
            final String username = (String) userCredentials.get("username");
            String password = (String) userCredentials.get("password");
            
            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        String selectedRole = getSelectedUserRole();
    
                        saveUserRole(user, selectedRole, username);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Check exception 3: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    //endregion
    
    /**
     * Gets the credentials the user entered
     * @return dictionary of credentials and valid state
     */
    public Map<String, Object> getCredentials()
    {
        Map<String, Object> userCredentials = new HashMap<>();
        String username = String.valueOf(usernameTextView.getText());
        String password = String.valueOf(passwordTextView.getText());
        
        if(username.length() != 0 || password.length() != 0)
        {
            userCredentials.put("username", username);
            userCredentials.put("password", password);
            userCredentials.put("isValid", true);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            userCredentials.put("username", null);
            userCredentials.put("password", null);
            userCredentials.put("isValid", false);
        }
       
        return userCredentials;
    }
    
    /**
     * Saving the user role in the server and then redirecting the user
     * @param user          Object instance of ParseUser
     * @param role          Role the user selected
     * @param username      Username the user entered
     */
    public void saveUserRole(ParseUser user, final String role, final String username)
    {
        final Boolean[] result = new Boolean[1];
        result[0] = false; //Deafult value
        
        user.put("User_Role", role);
        
        user.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)
                {
                    UserClass currentUser = new UserClass(username, role);
                    
                    redirectUser(currentUser);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Check exception 2: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Redirects the user based on their role
     * @param currentUser   The currently logged in user
     */
    public void redirectUser(UserClass currentUser)
    {
        Intent intent;
        if(currentUser.role.equals("rider"))
        {
            intent = new Intent(MainActivity.this, RiderActivity.class);
        }
        else
        {
            intent = new Intent(MainActivity.this, RiderRequestsActivity.class);
        }
        startActivity(intent);
    }
    
    /**
     * Gets the role the user selected on the login screen
     * @return The role selected by the user
     */
    public String getSelectedUserRole()
    {
        if(userRoleSwitch.isChecked())
        {
            return "driver";
        }
        else
        {
            return"rider";
        }
    }

}