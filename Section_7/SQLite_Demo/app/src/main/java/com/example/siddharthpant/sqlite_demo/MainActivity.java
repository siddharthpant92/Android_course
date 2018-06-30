package com.example.siddharthpant.sqlite_demo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            SQLiteDatabase database = this.openOrCreateDatabase("Test", MODE_PRIVATE, null);
            database.execSQL("create table if not exists test(title varchar, detail varchar)");
            database.execSQL("insert into test(title, detail) values ('title 1', 'detail 1')");
            database.execSQL("insert into test(title, detail) values ('title 2', 'detail 2')");

            Cursor cursor = database.rawQuery("select * from test", null);
            int titleIndex = cursor.getColumnIndex("title");
            int detailIndex = cursor.getColumnIndex("detail");

            cursor.moveToFirst();
            while(cursor != null)
            {
                Log.d(tag, "title: "+cursor.getString(titleIndex)+"\n detail: "+cursor.getString(detailIndex));
                cursor.moveToNext();
            }

        }
        catch(Exception e)
        {

        }
    }
}
