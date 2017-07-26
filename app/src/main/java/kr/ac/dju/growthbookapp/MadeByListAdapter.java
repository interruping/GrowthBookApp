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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by geonyounglim on 2017. 5. 29..
 */

public class MadeByListAdapter extends BaseAdapter {
    private ArrayList<Worker>  _container;
    private Context _context;


    public MadeByListAdapter() {
        _container = new ArrayList<Worker>();
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

        Worker worker = _container.get(pos);
        if ( convertView == null ){
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.made_by_item, parent, false);

        }

        ImageView userIcon = (ImageView)convertView.findViewById(R.id.user_icon);
        userIcon.setImageDrawable(worker.getUserIcon());
        TextView name = (TextView) convertView.findViewById(R.id.user_name);
        name.setText(worker.getName());
        TextView description = (TextView) convertView.findViewById(R.id.description_textview);
        description.setText(worker.getDescription());
        TextView kakaoId = (TextView)convertView.findViewById(R.id.kakotalk_id_textview);
        kakaoId.setText(worker.getKakaoId());


        return convertView;
    }

    public void addItem(Worker newWorker ) {
        _container.add(newWorker);
    }



    static class Worker {
        private int _id;
        static private int _autoInc = 0;
        private String _name;
        private String _description;
        private String _kakaoId;
        private Drawable _userIcon;

        public Worker(String name, String description, String kakaoId, Drawable userIcon) {
            _id = _autoInc;
            _autoInc++;
            _name = name;
            _description = description;
            _kakaoId = kakaoId;
            _userIcon = userIcon;
        }

        public int getId () {
            return _id;
        }

        public String getName() {
            return _name;
        }

        public String getDescription() {
            return _description;
        }

        public String getKakaoId() {
            return _kakaoId;
        }

        public Drawable getUserIcon() {
            return _userIcon;
        }

    }

}
