package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    ListView usernamesList;

    ArrayList<String> usernames = new ArrayList<>();
    String tag = "UserListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        usernamesList = (ListView) findViewById(R.id.usernamesList);

        getIntentData();

        ArrayAdapter adapter = new ArrayAdapter(UserListActivity.this, android.R.layout.simple_list_item_1, usernames);
        usernamesList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.share)
        {
            checkImagePermission();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkImagePermission()
    {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
        {
            getPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getPhoto();
        }
    }

    private void getIntentData()
    {
        Bundle bundle = getIntent().getExtras();
        usernames = bundle.getStringArrayList("usernames");
    }


    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==  1 && resultCode == RESULT_OK && data != null)
        {
            // Upload data to parse server
            Uri selectedImage = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                Log.d(tag, String.valueOf(bitmap.getByteCount()));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                Log.d(tag, String.valueOf(stream.size()));
                byte[] bytes = stream.toByteArray();
                ParseFile file = new ParseFile("image.png", bytes);
                Log.d(tag, String.valueOf(file.isDataAvailable()));
                ParseObject parseObject = new ParseObject("Image");
                parseObject.put("image", file);
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if( e == null)
                        {
                            Toast.makeText(UserListActivity.this, "Image shared", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.d(tag, "exception 1: "+e);
                            e.printStackTrace();
                            Toast.makeText(UserListActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            catch(Exception e)
            {
                Log.d(tag, "exception 2: "+e);
                e.printStackTrace();
            }
        }
    }
}
