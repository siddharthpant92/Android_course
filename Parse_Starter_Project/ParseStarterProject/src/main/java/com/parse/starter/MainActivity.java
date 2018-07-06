/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Parse dasboard: http://54.153.48.88/apps/

        //Storing object in parse server
//        ParseObject score = new ParseObject("Score"); //Class name
//        score.put("username", "Sid");
//        score.put("score", 85);
//        score.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null)
//                {
//                    Log.d(tag, "Saved");
//                }
//                else
//                {
//                    Log.d(tag, "Error: "+e);
//                }
//            }
//        });

         //Getting data from parse server
//        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Score");
//        parseQuery.getInBackground("XAfhxSikka", new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if(e == null && object != null)
//                {
//                    Log.d(tag, object.getString("username"));
//                    Log.d(tag, String.valueOf(object.getInt("score")));
//
//                    //Updating score
//                    object.put("score", 91);
//                    object.saveInBackground();
//                    Log.d(tag, "Updated score: "+object.getInt("score"));
//                }
//                else
//                {
//                    Log.d(tag, "Exception: "+e);
//                }
//            }
//        });

//        ParseObject parseObject = new ParseObject("Tweet");
//        parseObject.put("username", "Sid");
//        parseObject.put("tweet", "this is a tweet too");
//        parseObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null)
//                {
//                    Log.d(tag, "Tweet saved!");
//                }
//                else
//                {
//                    Log.d(tag, "error:"+e);
//                }
//            }
//        });

//        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Tweet");
//        parseQuery.whereEqualTo("username", "Sid 1");
//        parseQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if(e == null )
//                {
//                    if(objects.size() > 0)
//                    {
//                        for(ParseObject ob: objects)
//                        {
//                            ob.put("tweet", "changed the tweet for a specific user");
//                            ob.saveInBackground();
//                        }
//                    }
//                }
//            }
//        });
//
//
//
//        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}