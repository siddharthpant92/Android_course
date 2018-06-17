package com.example.siddharthpant.brain_train;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView timerText, scoreText, resultText, questionText;
    Button guess1,guess2,guess3,guess4, startGame;

    String TAG = "MainActivity";

    Integer numQuestions = 20; //The number of questions that will be asked
    Integer timeLimit = 45;//The tmelimit the user has to answer all the questions IN SECONDS
    Integer attemptedQuestions = 0;//The number of questions the user has attempted so far
    Integer score = 0;//The score i.e., the number of questions the user has got right
    Integer correctAnswer = 0;//The correct answer to each questions
    Integer[] options = new Integer[4]; //Array to hold all 4 answer options

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = (TextView) findViewById(R.id.timeText);
        scoreText = (TextView) findViewById(R.id.scoreText);
        resultText = (TextView) findViewById(R.id.resultText);
        questionText = (TextView) findViewById(R.id.questionText);

        guess1 = (Button) findViewById(R.id.guess1);
        guess2 = (Button) findViewById(R.id.guess2);
        guess3 = (Button) findViewById(R.id.guess3);
        guess4 = (Button) findViewById(R.id.guess4);
        startGame = (Button) findViewById(R.id.startGame);

        newGame();
    }


    public void startNewGameTapped(View view)
    {
        startGame.setVisibility(View.INVISIBLE);
        startGame.setEnabled(false);

        guess1.setVisibility(View.VISIBLE);
        guess2.setVisibility(View.VISIBLE);
        guess3.setVisibility(View.VISIBLE);
        guess4.setVisibility(View.VISIBLE);

        startCountDown();

        setAnswerOptions();
    }


    public void answerTapped(View view)
    {
        Integer selectedOption = Integer.parseInt((String) view.getTag()); //Getting the selected button

        //Checking if that had thr right answer
        if(options[selectedOption] == correctAnswer)
        {
            score++;
            resultText.setText("Correct!");
        }
        else
        {
            resultText.setText("Wrong!");
        }
        attemptedQuestions++;

        scoreText.setText(score+" / "+attemptedQuestions);

        if(attemptedQuestions == numQuestions)
        {
            gameOver();
        }

        setAnswerOptions();
    }



    public void newGame()
    {
        startGame.setVisibility(View.VISIBLE);
        startGame.setEnabled(true);

        timerText.setText("");
        scoreText.setText("");
        resultText.setText("");
        questionText.setText("");
        guess1.setVisibility(View.INVISIBLE);
        guess2.setVisibility(View.INVISIBLE);
        guess3.setVisibility(View.INVISIBLE);
        guess4.setVisibility(View.INVISIBLE);
    }


    public void startCountDown()
    {
        new CountDownTimer(timeLimit*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("00:"+String.format("%02d", millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                gameOver();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newGame();
                    }
                }, 5000);
            }
        }.start();
    }

    public void setAnswerOptions()
    {
        Random rand = new Random();
        Integer num1 = rand.nextInt(100)+1;
        Integer num2 = rand.nextInt(100)+1;
        correctAnswer = num1 + num2;

        Integer selectedButton = rand.nextInt(3)+1; //The randomly selected button which will have the correct answer
        for(int i=0; i<4; i++)
        {
            //For the selected button, setting the right answer
            if(selectedButton == i)
            {
                options[selectedButton] = correctAnswer;
            }
            else //Selecting a random number close to the correct answer
            {
                Integer temp = rand.nextInt(correctAnswer+15)+correctAnswer - 15;
                if(temp == correctAnswer)
                {
                    temp += 5;
                }
                options[i] = temp;
            }
        }

        questionText.setText(num1+" + "+num2+" = ?");

        guess1.setText(String.valueOf(options[0]));
        guess2.setText(String.valueOf(options[1]));
        guess3.setText(String.valueOf(options[2]));
        guess4.setText(String.valueOf(options[3]));
    }

    public void gameOver()
    {
        resultText.setText("Time up! Your final score is: "+score+"/"+numQuestions);
    }
}
