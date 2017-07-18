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
import java.util.logging.Level;

/**
 * Created by geonyounglim on 2017. 7. 17..
 */

public class LevelDescriptionListAdapter extends BaseAdapter {
    private ArrayList<LevelDesc> _container;

    public LevelDescriptionListAdapter() {
        _container = new ArrayList<LevelDesc>();
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
        return _container.get(position).getId() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;



        if ( convertView == null ){
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.menu_item, parent, false);

        }

        ImageView menuIcon = (ImageView)convertView.findViewById(R.id.menu_icon);
        TextView menuString = (TextView)convertView.findViewById(R.id.menu_string);


        LevelDesc item = _container.get(position);

        menuIcon.setImageDrawable(item.getLevelImageDrawable());

        menuString.setText(item.getLevelDescriptionString());
        menuString.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        menuString.setGravity(Gravity.CENTER_VERTICAL| Gravity.LEFT);

        return convertView;
    }

    public void addItem(Drawable levelImg, String descString){
        _container.add(new LevelDesc(levelImg, descString));
    }

    public class LevelDesc {
        private int autoInc = 0;
        private int _id;
        private Drawable _levelImageDrawable;
        private String _levelDescriptionString;

        public LevelDesc( Drawable levelImage, String levelDescString ){

            _id = autoInc;
            autoInc++;
            _levelImageDrawable = levelImage;
            _levelDescriptionString = levelDescString;
        }

        public int getId () {
            return _id;
        }

        public Drawable getLevelImageDrawable () {
            return _levelImageDrawable;
        }

        public String getLevelDescriptionString () {
            return _levelDescriptionString;
        }
    }
}
