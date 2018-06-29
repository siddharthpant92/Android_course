package com.example.siddharthpant.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView noteTitle;
    SharedPreferences sharedPreferences;
    static ArrayAdapter adapter;

    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> details = new ArrayList<>();
    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.siddharthpant.notes", Context.MODE_PRIVATE);

        noteTitle = (ListView) findViewById(R.id.noteTitle);

        titles.clear();
        details.clear();
        try
        {
            titles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notesTitles", ObjectSerializer.serialize(new ArrayList<String>())));
            details = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("notesDetails", ObjectSerializer.serialize(new ArrayList<String>())));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Setting the titles because only the titles are displayed in list view
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, titles);


        noteTitle.setAdapter(adapter);
        noteTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailActivity(position);
            }
        });
    }

    private void goToDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.addNote:
                goToDetailActivity(-99);
                break;
            default:
                Log.d(tag, "default");
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
