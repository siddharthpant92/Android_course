/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends AppCompatActivity
{
    Switch userRoleSwitch;
    Button loginButton;
    
    String user_role, tag="MainActivity", username;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        loginButton = (Button) findViewById(R.id.loginButton);
    
        // Checking if user is already logged in
        try
        {
            username = ParseUser.getCurrentUser().getUsername();
            Log.d(tag, username);
            user_role = ParseUser.getCurrentUser().getString("User_Role");
            redirectUser();
        }
        catch(Exception e)
        {
            Log.d(tag, "no current user");
        }
        
        
//        if (ParseUser.getCurrentUser().getUsername() != null)
//        {
//            Log.d(tag, ParseUser.getCurrentUser().getUsername());
//            user_role = ParseUser.getCurrentUser().getString("User_Role");
//            redirectUser();
//        }
//        else
//        {
//            Log.d(tag, "No user");
//        }
        
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
    
    public void loginTapped(View view)
    {
        if(userRoleSwitch.isChecked())
        {
            user_role = "driver";
        }
        else
        {
            user_role = "rider";
        }
        
        anonymousLogin();
    }
    
    public void anonymousLogin()
    {
        ParseAnonymousUtils.logIn(new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if (e == null)
                {
                    user.put("User_Role", user_role);
                    user.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if(e == null)
                            {
                                redirectUser();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Check exception 2", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Check exception 1", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void redirectUser()
    {
        if(user_role.equals("rider"))
        {
            Log.d(tag, "rider");
//            intent = new Intent(MainActivity.this, RiderActivity.class);
        }
        else
        {
            Log.d(tag, "driver");
            Intent intent = new Intent(MainActivity.this, RiderRequestsActivity.class);
            startActivity(intent);
        }
        
    }

}