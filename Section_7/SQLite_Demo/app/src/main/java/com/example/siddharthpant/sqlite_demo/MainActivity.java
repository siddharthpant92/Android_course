package com.example.siddharthpant.sqlite_demo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        // In some devices, if this isn't mentioned, content is displayed using devices default web browser
        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("https://www.google.com");
        webView.loadData("<html>" +
                                    "<body>"+
                                        "<h1>"+
                                            "Hi there!"+
                                        "</h1>"+
                                        "<p>"+
                                            "Typed out some HTML in android!!"+
                                        "</p>"+
                                    "</body>"+
                                "</html>", "text/html", "UTF-8");

        try
        {
            SQLiteDatabase database = this.openOrCreateDatabase("users", Context.MODE_PRIVATE, null);
            database.execSQL("drop table if exists userTable");
            database.execSQL("create table if not exists userTable(id integer primary key, name varchar, age integer)");
            database.execSQL("insert into userTable(name, age) values ('sid', 25)");
            database.execSQL("insert into userTable(name, age) values ('saish', 26)");

            Cursor cursor = database.rawQuery("select * from userTable", null);
            int nameIndex = cursor.getColumnIndex("name");
            int ageIndex = cursor.getColumnIndex("age");
            int idIndex = cursor.getColumnIndex("id");
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Log.d(tag, "id: "+cursor.getInt(idIndex)+", name: "+cursor.getString(nameIndex)+", age: "+cursor.getInt(ageIndex));
                cursor.moveToNext();
            }

            // Deleting user
            database.execSQL("delete from userTable where name='saish'");
            cursor = database.rawQuery("select * from userTable", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Log.d(tag, "After deleting | id: "+cursor.getInt(idIndex)+", name: "+cursor.getString(nameIndex)+", age: "+cursor.getInt(ageIndex));
                cursor.moveToNext();
            }
        }
        catch(Exception e)
        {
            Log.d(tag, "Exception: "+e);
        }

    }
}
