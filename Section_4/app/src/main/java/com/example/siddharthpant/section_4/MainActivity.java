package com.example.siddharthpant.section_4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    ImageView gokuImage1, gokuImage2;

    Boolean goku1Displayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gokuImage1 = (ImageView) findViewById(R.id.gokuImage1);
        goku1Displayed = true;
        gokuImage2 = (ImageView) findViewById(R.id.gokuImage2);
    }

    public void changeImageTapped(View view)
    {
        if(goku1Displayed)
        {
            gokuImage1.animate().alpha(0f).setDuration(1000);
            goku1Displayed = false;

            gokuImage2.animate().alpha(1f).setDuration(1000);
        }
        else
        {
            gokuImage2.animate().alpha(0f).setDuration(1000);

            goku1Displayed = true;
            gokuImage1.animate().alpha(1f).setDuration(1000);
        }
    }
}
