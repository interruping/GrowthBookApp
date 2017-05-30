package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by geonyounglim on 2017. 5. 29..
 */

public class MenuListAdapter extends BaseAdapter {
    private ArrayList<MenuItem>  _container;

    public MenuListAdapter() {
        _container = new ArrayList<MenuItem>();
    }
    @Override
    public int getCount() {
        return _container.size();
    }

    @Override
    public Object getItem(int position) {
        return _container.get(position);
    }

    @Override
    public long getItemId(int position) {
        return _container.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        if ( convertView == null ){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menu_item, parent, false);

        }

        ImageView menuIcon = (ImageView)convertView.findViewById(R.id.menu_icon);
        TextView menuString = (TextView)convertView.findViewById(R.id.menu_string);


        MenuItem item = _container.get(position);

        menuIcon.setImageDrawable(item.getMenuIcon());
        menuIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        menuIcon.setPadding(0, 10,10,10);

        menuString.setText(item.getMenuString());
        menuString.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        menuString.setTextColor(0xFFFFFFFF);
        menuString.setGravity(Gravity.CENTER_VERTICAL| Gravity.LEFT);

        return convertView;
    }

    public void addItem(int id,Drawable icon, String title ) {
        MenuItem menuItem = new MenuItem(id, icon, title);

        _container.add(menuItem);
    }



}
