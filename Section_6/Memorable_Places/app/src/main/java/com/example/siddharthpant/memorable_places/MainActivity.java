package com.example.siddharthpant.memorable_places;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView placesList;

    SharedPreferences sharedPreferences;

    String tag = "MainActivity";
    static ArrayList<String> places  = new ArrayList<String>();
    static ArrayList<Double> latitudes  = new ArrayList<Double>();
    static ArrayList<Double> longitudes  = new ArrayList<Double>();
    static ArrayAdapter adapter;
    Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesList = (ListView) findViewById(R.id.placesList);
        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.memorable_places", Context.MODE_PRIVATE);

        intent = new Intent(MainActivity.this, MapsActivity.class);
        bundle = new Bundle();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        try
        {
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<Double>())));
            longitudes = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<Double>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        placesList.setAdapter(adapter);

        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bundle.putInt("index", position); //Only this location will be marked
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void addPlaceButtonTapped(View view)
    {

        //Passing a list of all places previously added by the user
        bundle.putInt("index", -99); //All Locations will be marked
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
