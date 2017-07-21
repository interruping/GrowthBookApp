package kr.ac.dju.growthbookapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestRecyclerViewAdapter extends RecyclerView.Adapter<RecentPassBookTestRecyclerViewAdapter.ViewHolder> {
    private List<RecentPassBookTestItem> _container;

    public RecentPassBookTestRecyclerViewAdapter() {
        _container = new ArrayList<RecentPassBookTestItem>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_pass_book_test_item,parent,false);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
