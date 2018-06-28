package com.example.siddharthpant.shared_pref_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.shared_pref_demo", Context.MODE_PRIVATE);

//        sharedPreferences.edit().putString("username", "Sid").apply();

        String username = sharedPreferences.getString("username", "default Value");
        Log.d(tag, username);
    }
}
