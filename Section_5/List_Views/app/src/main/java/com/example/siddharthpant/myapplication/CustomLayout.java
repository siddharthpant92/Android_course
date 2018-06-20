package com.example.siddharthpant.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomLayout extends BaseAdapter {
    LayoutInflater mInflater;

    int[] images;
    String[] names;
    String[] characters;
    String TAG = "CustomLayout";

    public CustomLayout(CustomListViewActivity customListViewActivity, int[] images, String[] names, String[] characters) {
        this.images = images;
        this.names = names;
        this.characters = characters;
        mInflater = (LayoutInflater) customListViewActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.images.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.custom_layout, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView textView1 = (TextView) convertView.findViewById(R.id.textView);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);

        imageView.setImageResource(this.images[position]);
        textView1.setText(this.characters[position]);
        textView2.setText(this.names[position]);
        return convertView;
    }
}
