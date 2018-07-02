package com.example.siddharthpant.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    EditText selectedDetail, selectedTitle;

    SharedPreferences sharedPreferences;

    Integer position;
    String tag = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.notes", Context.MODE_PRIVATE);

        selectedDetail = (EditText) findViewById(R.id.selectedDetail);
        selectedTitle = (EditText) findViewById(R.id.selectedTitle);

        Bundle bundle  = getIntent().getExtras();
        position = bundle.getInt("position");

        //If existing note was tapped
        if(position != -99)
        {
            selectedDetail.setText(MainActivity.details.get(position));
            selectedTitle.setText(MainActivity.titles.get(position));
        }
        else
        {
            selectedDetail.setText("");
            selectedTitle.setText("");
        }
    }


    public  void saveNotesTapped(View view)
    {
        if((selectedTitle.getText().length() > 0) && (selectedDetail.getText().length() > 0))
        {
            if (position != -99)
            {
                //Changing the values of selected note
                MainActivity.titles.set(position, String.valueOf(selectedTitle.getText()));
                MainActivity.details.set(position, String.valueOf(selectedDetail.getText()));
            }
            else
            {
                //Adding the new note
                MainActivity.titles.add(String.valueOf(selectedTitle.getText()));
                MainActivity.details.add(String.valueOf(selectedDetail.getText()));
            }

            try
            {
                sharedPreferences.edit().putString("notesTitles", ObjectSerializer.serialize(MainActivity.titles)).apply();
                sharedPreferences.edit().putString("notesDetails", ObjectSerializer.serialize(MainActivity.details)).apply();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            MainActivity.adapter.notifyDataSetChanged();
            Toast.makeText(DetailsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(DetailsActivity.this, "Title and Details cannot be left blank", Toast.LENGTH_SHORT).show();
        }
    }
}
