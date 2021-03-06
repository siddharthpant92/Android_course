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
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

import Model.UserClass;


public class MainActivity extends Activity
{
    static MainActivity mainActivity; // instance of this activity
    Switch userRoleSwitch;
    Button loginButton, signupButton;
    TextView usernameTextView, passwordTextView;
    
    String TAG = "MainActivity";
    
    UserClass userClass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
//        mainActivity = this; // Initializing this instance
        userClass = new UserClass(this); // So that UserClass can call functions in this activity
        
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        loginButton = (Button) findViewById(R.id.loginButton);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        
        UserClass currentUser = userClass.getCurrentUser();
        
        // Checking if user is already logged in
        if(currentUser != null)
        {
            Toast.makeText(this, "Logged in as:  "+currentUser.username, Toast.LENGTH_SHORT).show();
            redirectUser(currentUser.role);
        }
        
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
    
    // Other classes and activities can call this method to create an object of MainActivity.
//    public static MainActivity getInstance()
//    {
//        return mainActivity;
//    }

    //region USER ACTIONS
    
    public void loginTapped(View view)
    {
        Map<String, Object> userCredentials;
        userCredentials = getCredentials();
    
        if((Boolean) userCredentials.get("isValid"))
        {
            String selectedRole = getSelectedUserRole();
            
            String username = (String) userCredentials.get("username");
            String password = (String) userCredentials.get("password");
            
            userClass.userLogin(username, password, selectedRole, MainActivity.this);
        }
    }

    public void signupTapped(View view)
    {
        Map<String, Object> userCredentials;
        userCredentials = getCredentials();

        if((Boolean) userCredentials.get("isValid"))
        {
            String selectedRole = getSelectedUserRole();
            
            String username = (String) userCredentials.get("username");
            String password = (String) userCredentials.get("password");
    
            userClass.userSignup(username, password, selectedRole, MainActivity.this);
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
     * Redirects the user based on their role. Called from UserClass once the role has been saved
     * @param role   The role of currently logged in user
     */
    public void redirectUser(String role)
    {
        Intent intent;
        if(role.equals("rider"))
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