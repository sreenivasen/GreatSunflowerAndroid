package org.greatsunflower.android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ListViewAdapter extends BaseAdapter {
 
    // Declare Variables
    Context context;

    String[] country;

    LayoutInflater inflater;
 
    public ListViewAdapter(Context context, String[] country) {
    	this.context = context;
        this.country = country;
    }
 
    public int getCount() {
        return country.length;
    }
 
    public Object getItem(int position) {
        return null;
    }
 
    public long getItemId(int position) {
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
 
        // Declare Variables

        TextView txtcountry;
 
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
 
        // Locate the TextViews in listview_item.xml
        txtcountry = (TextView) itemView.findViewById(R.id.country);
  
        txtcountry.setText(country[position]);
 
        return itemView;
    }
}
