package com.example.siddharthpant.shared_pref_demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";
    String langauge1 = "English";
    String langauge2 = "Hindi";

    SharedPreferences sharedPreferences;

    TextView languagePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        languagePref = (TextView) findViewById(R.id.languagePref);

        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.shared_pref_demo", Context.MODE_PRIVATE);
        Log.d(tag, String.valueOf(sharedPreferences.getAll()));

        languagePref.setText(sharedPreferences.getString("selectedLanguage", langauge1));

        sharedPreferences.edit().putString("username", "Sid").apply();

        String username = sharedPreferences.getString("username", "default Value");
        Log.d(tag, username);

        ArrayList<String> test = new ArrayList<>();
        test.add("a");
        test.add("b");
        test.add("c");
        test.add("d");

        try
        {
            sharedPreferences.edit().putString("friends", ObjectSerializer.serialize(test)).apply();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ArrayList<String> test2 = new ArrayList<>();
        try
        {
            //Getting the deserialized array list from preferences. Default value is empty array list.
            test2 = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d(tag, String.valueOf(test2));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.settings:
                Log.d(tag, "settings selected");
                break;

            case R.id.help:
                Log.d(tag, "help selected");
                break;
            case R.id.change:
                changeLangauge();
                break;
            default:
                Log.d(tag, "default");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeLangauge()
    {
        new AlertDialog.Builder(this)
                        .setTitle("Change your language preference")
                        .setMessage("Select your language")
                        .setPositiveButton(langauge1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setNewLangauge(langauge1);
                            }
                        })
                        .setNegativeButton(langauge2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setNewLangauge(langauge2);
                            }
                        })
                        .show();
    }

    public void setNewLangauge(String newLangauge)
    {
        sharedPreferences.edit().putString("selectedLanguage", newLangauge).apply();
        languagePref.setText(newLangauge);
    }
}
