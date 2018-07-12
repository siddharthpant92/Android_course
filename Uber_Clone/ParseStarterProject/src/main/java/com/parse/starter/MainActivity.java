/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;


public class MainActivity extends Activity {

  String tag = "MainActivity", userType;

  Switch userTypeSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);

      if(ParseUser.getCurrentUser() == null)
      {
          ParseAnonymousUtils.logIn(new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {
                  if(e == null)
                  {
                      Log.d(tag, "Success");
                  }
                  else
                  {
                      Log.d(tag, "failed");
                  }
              }
          });
      }
      else
      {
          if(ParseUser.getCurrentUser().get("User Type") != null)
          {
              Log.d(tag, String.valueOf(ParseUser.getCurrentUser().get("User Type")));
          }
      }

      ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

  public void getStartedTapped(View view)
  {
      userType = "rider";
      if(userTypeSwitch.isChecked())
      {
          userType = "driver";
      }

      ParseUser.getCurrentUser().put("User Role", userType);

      Log.d(tag, userType);
  }

}