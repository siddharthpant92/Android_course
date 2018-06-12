package com.example.siddharthpant.tic_tac_toe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    Integer player = 1; //Player 1 or 2
    ImageView icon;

    //The postions that have already been played
    Integer[] playedPosition = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void playTurnTapped(View view)
    {

        icon = (ImageView) view;

        Integer attemptedPos = Integer.parseInt(String.valueOf(icon.getTag()));

        if(playedPosition[attemptedPos] == 2) //The position has not been played yet
        {
            //Icons will appear with animation.
            icon.setScaleX(0f);
            icon.setScaleY(0f);

            if(player == 1)
            {
                icon.setImageResource(R.drawable.circle);
                icon.animate().scaleY(1f).scaleX(1f).rotation(360).setDuration(1500);
                playedPosition[attemptedPos] = 1;

                player = 2;//Next players turn
            }
            else
            {
                icon.setImageResource(R.drawable.cross);
                icon.animate().scaleY(1f).scaleX(1f).rotation(360).setDuration(1500);
                playedPosition[attemptedPos] = 1;

                player = 1; //Next players turn
            }
        }
        else
        {
            Toast.makeText(MainActivity.this,"You can't play there. Try again", Toast.LENGTH_SHORT).show();
        }
    }
}
