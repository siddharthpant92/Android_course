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

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
{
    Switch userRoleSwitch;
    Button loginButton, signupButton;
    TextView usernameTextView, passwordTextView;
    
    String user_role, tag="MainActivity", username, password;
    ArrayList<String> nearbyRiders = new ArrayList<>();
    ArrayList<Double> nearbyRidersLatitudes = new ArrayList<>();
    ArrayList<String> nearbyRidersLongitudes = new ArrayList<>();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        userRoleSwitch = (Switch) findViewById(R.id.userRoleSwitch);
        loginButton = (Button) findViewById(R.id.loginButton);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
    
        // Checking if user is already logged in
        try
        {
            username = ParseUser.getCurrentUser().getUsername();
            Toast.makeText(this, "Logged in as:  "+username, Toast.LENGTH_SHORT).show();
            user_role = ParseUser.getCurrentUser().getString("User_Role");
            redirectUser();
        }
        catch(Exception e)
        {
            Log.d(tag, "no current user");
        }
        
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
    
    public void loginTapped(View view)
    {
        setUserRole();
    
        if(getCredentials())
        {
            ParseUser user = new ParseUser();
            user.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(final ParseUser user, ParseException e) {
                    if(user != null && e == null)
                    {
                        // Checking role of user trying to login
                        String check_role = String.valueOf(user.get("User_Role"));
                        if(check_role.equals(user_role))
                        {
                            addUserRole(user);
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
        setUserRole();
        
        if(getCredentials())
        {
            final ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        addUserRole(user);
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
    
    public boolean getCredentials()
    {
        username = String.valueOf(usernameTextView.getText());
        password = String.valueOf(passwordTextView.getText());
        
        if(username.length() == 0 || password.length() == 0)
        {
            Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    
    public void addUserRole(final ParseUser user)
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
                    Toast.makeText(MainActivity.this, "Check exception 2: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void redirectUser()
    {
        if(user_role.equals("rider"))
        {
            Intent intent = new Intent(MainActivity.this, RiderActivity.class);
            startActivity(intent);
        }
        else
        {
            // As a driver, they can see all nearby requests of riders
            
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("User_Role", "rider");
            query.findInBackground(new FindCallback<ParseUser>()
            {
                @Override
                public void done(List<ParseUser> users, ParseException e)
                {
                    if(e == null)
                    {
                        if(users.size() > 0)
                        {
                            for(ParseUser user: users)
                            {
                                nearbyRiders.add(user.getUsername());
                            }
                            
                            Intent intent = new Intent(MainActivity.this, RiderRequestsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("nearbyRiders", nearbyRiders);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "No nearby riders", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Check exception 4: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        
    }
    
    public void setUserRole()
    {
        if(userRoleSwitch.isChecked())
        {
            user_role = "driver";
        }
        else
        {
            user_role = "rider";
        }
    }

}