package com.example.siddharthpant.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    EditText username, password;
    ImageView image;

    String usernameText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(R.drawable.image1);
        image.setTag(1);
    }

    public void loginTapped(View view)
    {
        usernameText = String.valueOf(username.getText());
        passwordText = String.valueOf(password.getText());
        Log.i(TAG, "username: "+usernameText+"\npassword: "+passwordText);

        Toast.makeText(MainActivity.this, usernameText+" , "+passwordText, Toast.LENGTH_SHORT).show();
    }

    public void changeImageTapped(View view)
    {
        if((Integer)image.getTag() == 1)
        {
            image.setImageResource(R.drawable.image2);
            image.setTag(2);
        }
        else
        {
            image.setImageResource(R.drawable.image1);
            image.setTag(1);
        }
    }
}
