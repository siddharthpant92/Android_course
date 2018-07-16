package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

public class RiderRequestsActivity extends Activity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_requests);
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
