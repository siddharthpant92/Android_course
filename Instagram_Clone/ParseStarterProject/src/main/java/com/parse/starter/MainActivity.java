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

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;


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

      if(ParseUser.getCurrentUser().getUsername() != null)
      {
          Toast.makeText(this, "Logged in as "+ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
          getAllUsers();
      }
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
                     getAllUsers();
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
                      getAllUsers();
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

  public void getAllUsers()
  {
      ParseQuery<ParseUser> query = ParseUser.getQuery();
      query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
      query.addAscendingOrder("username");
      query.findInBackground(new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> objects, ParseException e) {
              if(objects.size() > 0 && e == null)
              {
                  ArrayList<String> usernames = new ArrayList<>();
                  for(ParseUser user: objects)
                  {
                      usernames.add(user.getUsername());
                  }
                  gotoUserListActivity(usernames);
              }
          }
      });
  }

  private void gotoUserListActivity(ArrayList<String> usernames)
  {
      Intent intent = new Intent(MainActivity.this, UserListActivity.class);
      Bundle bundle = new Bundle();
      bundle.putStringArrayList("usernames", usernames);
      intent.putExtras(bundle);
      startActivity(intent);
  }


}