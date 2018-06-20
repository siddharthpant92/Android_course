package com.example.siddharthpant.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomListViewActivity extends AppCompatActivity {

    String TAG = "CustomListViewActivity";

    ListView customListView;

    int[] IMAGES = {R.drawable.goku, R.drawable.fullmetal, R.drawable.naruto, R.drawable.natsu};

    String[] NAMES = {"Dragon Ball Super", "Fullmetal Alchemist", "Naruto", "Fairy Tail"};

    String[] CHARACTERS = {"Goku", "Ed and Elric", "Naruto", "Natsu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view);

        customListView = findViewById(R.id.customListView);

        CustomLayout customAdapter = new CustomLayout(this,  IMAGES, NAMES, CHARACTERS);
        customListView.setAdapter(customAdapter);

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, NAMES[position]);
            }
        });
    }
}
