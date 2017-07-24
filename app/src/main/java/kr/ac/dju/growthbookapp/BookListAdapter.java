package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by dodrn on 2017-07-23.
 */

public class BookListAdapter extends BaseAdapter {
    private ArrayList<BookGenreItem> _container;
    private Context _context;

    public BookListAdapter() {
        _container = new ArrayList<>();
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
            convertView = inflater.inflate(R.layout.book_genre_item, parent, false);

        }


        TextView genreTextView = (TextView)convertView.findViewById(R.id.genre_string);
        genreTextView.setText(_container.get(pos).getBookGenre());


        return convertView;
    }

    public void addItem(BookGenreItem newItem){
        _container.add(newItem);
    }

    static public class BookGenreItem {
        private int _id;
        private String _genre;

        public BookGenreItem(int id,String genre) {
            _id = id;
            _genre = genre;
        }

        public int getId() {
            return _id;
        }

        public String getBookGenre() {
            return _genre;
        }
    }
}
