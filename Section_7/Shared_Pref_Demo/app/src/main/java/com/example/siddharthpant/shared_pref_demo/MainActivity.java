package com.example.siddharthpant.shared_pref_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.shared_pref_demo", Context.MODE_PRIVATE);

//        sharedPreferences.edit().putString("username", "Sid").apply();

        String username = sharedPreferences.getString("username", "default Value");
        Log.d(tag, username);

        ArrayList<String> test = new ArrayList<>();
        test.add("a");
        test.add("b");
        test.add("c");
        test.add("d");

//        try
//        {
//            sharedPreferences.edit().putString("friends", ObjectSerializer.serialize(test)).apply();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        ArrayList<String> test2 = new ArrayList<>();
        try
        {
            //Getting the deserialized array list from preferences. Default value is empty array list.
            test2 = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d(tag, String.valueOf(test2));
    }
}
