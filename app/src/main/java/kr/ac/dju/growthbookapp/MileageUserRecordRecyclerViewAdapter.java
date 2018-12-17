package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dju.book.HttpConn;

import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class MileageUserRecordRecyclerViewAdapter extends RecyclerView.Adapter<MileageUserRecordRecyclerViewAdapter.ViewHolder>  {
    private List<RecentPassBookTestItem> _container;

    public MileageUserRecordRecyclerViewAdapter() {
        _container = new ArrayList<RecentPassBookTestItem>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mileage_user_record_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecentPassBookTestItem item = _container.get(position);

        holder.getDescTextView().setText(item.getDescription());
        holder.getDateTextView().setText(item.getDate());
        holder.getPointTextView().setText(item.getPoint());

    }

    @Override
    public int getItemCount() {
        return _container.size();
    }

    public void addItem(RecentPassBookTestItem item){
        _container.add(item);
    }

    public RecentPassBookTestItem getItem(int position){
        return _container.get(position);
    }


    public void clear() {
        _container.clear();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private int _currentHoldItem;

        private TextView _descriptionTextview;
        private TextView _dateTextview;
        private TextView _pointTextview;

        public ViewHolder(View itemView) {
            super(itemView);
            _descriptionTextview = (TextView)itemView.findViewById(R.id.book_pass_description_textview);
            _dateTextview = (TextView)itemView.findViewById(R.id.book_pass_date_textview);
            _pointTextview = (TextView)itemView.findViewById(R.id.book_pass_point_textview);

        }


        public TextView getDescTextView() {
            return _descriptionTextview;
        }

        public TextView getDateTextView() {
            return _dateTextview;
        }

        public TextView getPointTextView() {
            return _pointTextview;
        }



    }
}
