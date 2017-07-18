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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by geonyounglim on 2017. 5. 29..
 */

public class MenuListAdapter extends BaseAdapter {
    private ArrayList<MenuItem>  _container;
    private Context _context;

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
        _context = parent.getContext();


        if ( convertView == null ){
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menu_item, parent, false);

        }

        ImageView menuIcon = (ImageView)convertView.findViewById(R.id.menu_icon);
        TextView menuString = (TextView)convertView.findViewById(R.id.menu_string);


        MenuItem item = _container.get(position);

        menuIcon.setImageDrawable(item.getMenuIcon());

        menuString.setText(item.getMenuString());
        menuString.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        menuString.setGravity(Gravity.CENTER_VERTICAL| Gravity.LEFT);

        return convertView;
    }

    public void addItem(int id,Drawable icon, String title ) {
        MenuItem menuItem = new MenuItem(id, icon, title);

        _container.add(menuItem);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    public void removeAllHightLight(ListView listView) {
        int len = _container.size();
        TextView dummy = new TextView(_context);

        for (int i = 0; i < len; i++){
            View item = getViewByPosition(i,listView);
            View highLightLine = item.findViewById(R.id.highlight_line);
            TextView textView = (TextView)item.findViewById(R.id.menu_string);
            highLightLine.setVisibility(View.INVISIBLE);
            textView.setTextColor(dummy.getTextColors());
        }
    }



}
