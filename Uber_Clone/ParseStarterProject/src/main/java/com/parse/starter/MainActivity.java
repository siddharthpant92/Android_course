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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends Activity {

    String tag = "MainActivity", userType="rider", username, role;

    Switch userTypeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);

    if(ParseUser.getCurrentUser() == null)
    {
        Log.d(tag, "Not logged in initially");
        anonymousLogin();
    }
    else
    {
        Log.d(tag, "Logged in initially");
        username = ParseUser.getCurrentUser().getUsername();
        Toast.makeText(this, "Logged in as "+username, Toast.LENGTH_SHORT).show();
        if(ParseUser.getCurrentUser().get("User_Role") != null)
        {
            redirectUser();
        }
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void getStartedTapped(View view)
    {
        Log.d(tag, "get started tapped");
        if(userTypeSwitch.isChecked())
        {
            userType = "driver";
        }
        else
        {
            userType = "rider";
        }

        if(ParseUser.getCurrentUser() == null)
        {
            anonymousLogin();
        }
        
        if(ParseUser.getCurrentUser() != null) 
        {
            ParseUser.getCurrentUser().put("User_Role", userType);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        redirectUser();
                    } else {
                        Toast.makeText(MainActivity.this, "Oops. Check logs", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "No user to assign role", Toast.LENGTH_SHORT).show();
        }
    }

    public void  anonymousLogin()
    {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if(e == null)
                {
                    //After logging in, role is assigned on tapping get started
                    username = ParseUser.getCurrentUser().getUsername();
                }
                else
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void redirectUser()
    {
        if(userType.equals("rider"))
        {
            Intent intent = new Intent(MainActivity.this, RiderActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, ViewRequestsActivity.class);
            startActivity(intent);
        }
    }
}