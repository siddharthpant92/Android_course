package com.example.siddharthpant.tic_tac_toe;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    Integer player;
    ImageView icon;

    android.support.v7.widget.GridLayout grid;

    //The postions that have already been played
    Integer[] playedPosition;
    Integer[][] winningPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newGame();
    }

    public void playTurnTapped(View view) {
        Boolean gameOver = false;

        icon = (ImageView) view;

        Integer attemptedPos = Integer.parseInt(String.valueOf(icon.getTag()));

        gameOver = checkTurn(attemptedPos);
        if (gameOver) {
            Toast.makeText(MainActivity.this, "Player " + player + " won the game!", Toast.LENGTH_SHORT).show();

            //Wait for 2 seconds before restarting the game
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    newGame();
                }
            }, 3000);
        }
    }

    public void newGameTapped(View view) {
        newGame();
    }

    public void newGame() {
        playedPosition = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        player = 1; //Player 1 or 2

        grid = (android.support.v7.widget.GridLayout) findViewById(R.id.grid);
        for (int i = 0; i < grid.getChildCount(); i++) {
            ((ImageView) grid.getChildAt(i)).setImageResource(R.drawable.unknown);
        }
    }

    private boolean checkTurn(Integer attemptedPos) {
        if (playedPosition[attemptedPos] == 0) //The position has not been played yet
        {
            //Icons will appear with animation.
            icon.setScaleX(0f);
            icon.setScaleY(0f);
            icon.setRotation(0);

            if (player == 1) {
                icon.setImageResource(R.drawable.circle);
                icon.animate().scaleY(1f).scaleX(1f).rotation(360).setDuration(1000);
                playedPosition[attemptedPos] = 1;

                if (checkWinner()) {
                    return true;
                }

                player = 2;
            } else {
                icon.setImageResource(R.drawable.cross);
                icon.animate().scaleY(1f).scaleX(1f).rotation(360).setDuration(1000);
                playedPosition[attemptedPos] = 2;

                if (checkWinner()) {
                    return true;
                }

                player = 1;
            }
        } else {
            Toast.makeText(MainActivity.this, "You can't play there. Try again", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean checkWinner() {
        for (Integer[] winPos : winningPositions) {
            if ((playedPosition[winPos[0]] != 0) && (playedPosition[winPos[0]] == playedPosition[winPos[1]]) && (playedPosition[winPos[1]] == playedPosition[winPos[2]])) {
                return true;
            }
        }
        return false;
    }
}

