package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ImageView imageView;

    String selectedUsername, tag="UserFeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        linearLayout = (LinearLayout) findViewById(R.id.user_feed);

        Intent intent = getIntent();
        selectedUsername = intent.getStringExtra("username");
        setTitle(selectedUsername+"'s Feed");

        ParseQuery query = new ParseQuery<ParseObject>("Image");
        query.whereEqualTo("username", selectedUsername);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size() > 0)
                {
                    Toast.makeText(UserFeedActivity.this, "It takes a while", Toast.LENGTH_SHORT).show();
                    for(ParseObject object: objects)
                    {
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(data != null && e == null)
                                {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    //Have to create image views dynamically
                                    imageView = new ImageView(getApplicationContext());
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setImageBitmap(bitmap);

                                    linearLayout.addView(imageView);
                                }
                                else
                                {
                                    Toast.makeText(UserFeedActivity.this, "There was an error displaying the image", Toast.LENGTH_SHORT).show();
                                    Log.d(tag, "exception 2: "+e);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(UserFeedActivity.this, "There doesn't seem to be any images", Toast.LENGTH_SHORT).show();
                    if(e!=null)
                    {
                        Log.d(tag, "exception 1: "+e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
