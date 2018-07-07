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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

    TextView username, password;
    Button signUp, login;

    String usernameText, passwordText, tag="MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      username = (TextView) findViewById(R.id.username);
      password = (TextView) findViewById(R.id.password);

      ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }


  public void signUpTapped(View view)
  {
      if(getCredentials())
      {
          ParseUser user = new ParseUser();
          user.setUsername(usernameText);
          user.setPassword(passwordText);

          user.signUpInBackground(new SignUpCallback() {
              @Override
              public void done(ParseException e) {
                  if(e == null)
                  {
                      Log.d(tag, "signed up");
                  }
                  else
                  {
                      Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              }
          });
      }
  }



  public void loginTapped(View view)
  {
      if(getCredentials())
      {
          ParseUser user = new ParseUser();
          user.logInInBackground(usernameText, passwordText, new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {
                  if(user != null && e == null)
                  {
                      Log.d(tag, "logged in");
                  }
                  else
                  {
                      Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              }
          });
      }

  }


  public boolean getCredentials()
  {
      usernameText = String.valueOf(username.getText());
      passwordText = String.valueOf(password.getText());

      if(usernameText.length() == 0 || passwordText.length() == 0)
      {
          Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
          return false;
      }
      return true;
  }

}