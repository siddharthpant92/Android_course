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

    String tag = "MainActivity", username, role;

    Switch userTypeSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void getStartedTapped(View view)
    {
        String userType;
        if(userTypeSwitch.isChecked())
        {
            userType = "driver";
        }
        else
        {
            userType = "rider";
        }
        anonymousLogin(userType);
    }

    public void  anonymousLogin(final String userType)
    {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e)
            {
                if(e == null)
                {
                    //After logging in, role is assigned on tapping get started
                    username = user.getUsername();
                    user.put("User_Role", userType);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                redirectUser(userType);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Oops. Check logs", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void redirectUser(String userType)
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