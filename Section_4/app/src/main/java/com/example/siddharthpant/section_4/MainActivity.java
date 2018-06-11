package com.example.siddharthpant.section_4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    ImageView gokuImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gokuImage = (ImageView) findViewById(R.id.gokuImage);
        gokuImage.setImageResource(R.drawable.goku_1);
        gokuImage.setTag(1);
    }

    public void changeImageTapped(View view)
    {
        if((Integer) gokuImage.getTag() == 1)
        {
            gokuImage.animate().alpha(0.3f).setDuration(2000);

            gokuImage.setImageResource(R.drawable.goku_2);
            gokuImage.setAlpha(0.3f);//To start animation
            gokuImage.animate().alpha(1f).setDuration(2000);
            gokuImage.setTag(2);
        }
        else
        {
            gokuImage.animate().alpha(0.3f).setDuration(2000);

            gokuImage.setImageResource(R.drawable.goku_1);
            gokuImage.setAlpha(0.3f);//To start animation
            gokuImage.animate().alpha(1f).setDuration(2000);
            gokuImage.setTag(1);
        }
    }
}
