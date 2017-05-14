package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;

public class DrawerArrayAdapter extends ArrayAdapter<Integer>
{

    private final Activity context;
    private final Integer[] web;
    private final Integer[] imageId;

    public DrawerArrayAdapter(Activity context, Integer[] web, Integer[] imageId)
    {
        super(context, R.layout.activity_main_custom_drawer_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        View rowView = context.getLayoutInflater()
                .inflate(R.layout.activity_main_custom_drawer_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(web[position]);
        imageView.setImageResource(imageId[position]);

        return rowView;
    }

}