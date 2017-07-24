package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dju.book.HttpConn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestRecyclerViewAdapter extends RecyclerView.Adapter<RecentPassBookTestRecyclerViewAdapter.ViewHolder> implements HttpConn.CallbackListener {
    private List<RecentPassBookTestItem> _container;
    private Handler _mainHandler;
    private Map<Integer, Double> _rateCache;
    private DoRateButtonClickListener _listener;

    public interface DoRateButtonClickListener {
        public void doRateButtonClicked(int position);
    }

    public RecentPassBookTestRecyclerViewAdapter(Context context) {
        _container = new ArrayList<RecentPassBookTestItem>();
        _mainHandler = new Handler(context.getMainLooper());

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
        holder.getDoRateButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_listener != null ) {
                    _listener.doRateButtonClicked(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return _container.size();
    }

    public void addItem(RecentPassBookTestItem item){
        _container.add(item);
    }

    public void getItem(int position){
        _container.get(position);
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
         _mainHandler.post(()->{


         });
    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {
        _mainHandler.post(()->{


        });
    }

    @Override
    public void requestTimeout(HttpConn httpConn) {
        _mainHandler.post(()->{


        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView _descriptionTextview;
        private TextView _dateTextview;
        private TextView _pointTextview;
        private Button _doRateButton;

        private RatingBar _ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            _descriptionTextview = (TextView)itemView.findViewById(R.id.book_pass_description_textview);
            _dateTextview = (TextView)itemView.findViewById(R.id.book_pass_date_textview);
            _pointTextview = (TextView)itemView.findViewById(R.id.book_pass_point_textview);
            _ratingBar = (RatingBar)itemView.findViewById(R.id.book_pass_rating_bar);
            _doRateButton = (Button)itemView.findViewById(R.id.do_rate_button);

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

        public RatingBar getRatingBar() { return _ratingBar; }

        public Button getDoRateButton() { return _doRateButton; }
    }
}
