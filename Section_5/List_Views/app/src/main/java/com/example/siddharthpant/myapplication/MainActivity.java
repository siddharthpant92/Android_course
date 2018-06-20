package com.example.siddharthpant.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    ListView listView;
    SeekBar seekBarValue;
    ArrayList<String> arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        seekBarValue = (SeekBar) findViewById(R.id.seekBarValue);
        seekBarValue.setMax(20);
        seekBarValue.setProgress(1);


        //The initial values that are displayed
        generateValues(1);

        seekBarValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                arrayList.clear();
                //Can't set minimum value of seekbar
                if(progress == 0)
                {
                    seekBarValue.setProgress(1);
                }

                generateValues(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void customisedListViewTapped(View view)
    {
        Intent intent = new Intent(MainActivity.this, CustomListViewActivity.class);
        startActivity(intent);
    }

    public void generateValues(Integer value)
    {
        for(int i = 1; i <= 10; i++)
        {
            arrayList.add(String.valueOf(value * i));
        }

        //Resetting the adapter to display the new values each time seekbar is changed
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: "+position);
                Log.d(TAG, "id: "+id);
            }
        });
    }
}
