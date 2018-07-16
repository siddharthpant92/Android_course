package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

public class RiderRequestsActivity extends Activity
{
    
    ListView riderRequestsListView;
    
    ArrayList<String> nearbyRiders = new ArrayList<>();
    ArrayAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_requests);
        
        riderRequestsListView = (ListView) findViewById(R.id.riderRequestsListView);
        
        Bundle bundle = getIntent().getExtras();
        nearbyRiders = bundle.getStringArrayList("nearbyRiders");
        
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearbyRiders);
        riderRequestsListView.setAdapter(adapter);
    }
    
    public  void logoutTapped(View view)
    {
        ParseUser.logOut();
        finish();
    }
    
    //Disabling back button
    
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on the logout button to go back", Toast.LENGTH_SHORT).show();
    }
}
