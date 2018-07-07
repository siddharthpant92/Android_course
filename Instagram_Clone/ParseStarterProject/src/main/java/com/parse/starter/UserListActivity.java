package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    ListView usernamesList;

    ArrayList<String> usernames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        usernamesList = (ListView) findViewById(R.id.usernamesList);

        getIntentData();

        ArrayAdapter adapter = new ArrayAdapter(UserListActivity.this, android.R.layout.simple_list_item_1, usernames);
        usernamesList.setAdapter(adapter);
    }

    private void getIntentData()
    {
        Bundle bundle = getIntent().getExtras();
        usernames = bundle.getStringArrayList("usernames");
    }
}
