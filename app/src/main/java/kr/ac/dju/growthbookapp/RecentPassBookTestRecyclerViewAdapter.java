package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dju.book.HttpConn;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestRecyclerViewAdapter extends RecyclerView.Adapter<RecentPassBookTestRecyclerViewAdapter.ViewHolder> implements HttpConn.CallbackListener {
    private List<RecentPassBookTestItem> _container;
    private Handler _mainHandler;
    private Map<Integer, Float> _rateCache;
    private Map<HttpConn, Integer> _httpTasks;
    private Map<Integer, ViewHolder> _holderTasks;
    private DoRateButtonClickListener _listener;

    public interface DoRateButtonClickListener {
        public void doRateButtonClicked(int position);
    }

    public RecentPassBookTestRecyclerViewAdapter(Context context) {
        _container = new ArrayList<RecentPassBookTestItem>();
        _mainHandler = new Handler(context.getMainLooper());
        _rateCache = new HashMap<>();
        _httpTasks = new HashMap<>();
        _holderTasks = new HashMap<>();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_pass_book_test_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setRateStatus(0);
        RecentPassBookTestItem item = _container.get(position);
        Integer currentId = Integer.valueOf(item.getId());
        holder.setCurrentHoldItem(currentId.intValue());

        if (_rateCache.containsKey(Integer.valueOf(item.getId())) == false ){
            HttpConn conn = new HttpConn();
            _httpTasks.put(conn, currentId);
            _holderTasks.put(currentId, holder);
            conn.setUserAgent("GBApp");
            conn.setCallBackListener(this);
            String tmpbookName = item.getDescription();
            int lastIndex = tmpbookName.indexOf("도서");
            String bookName = "- " + tmpbookName.substring(0, lastIndex);
            JSONObject json = new JSONObject();
            try {
                json.put("book_id", MD5(bookName));

                if ( StudentIDHolder.getInstance().isHold() == false ){

                }

                json.put("device_id", StudentIDHolder.getInstance().getID());

                Map<String,String> header = new HashMap<String, String>();
                String timecookie = TimeCookieGenarator.OneTimeInstance().gen(String.valueOf(json.toString().length()));
                header.put("Content-type", "application/json");
                header.put("Cookie", timecookie);
                header.put("Content-Length", String.valueOf(json.toString().length()));
                conn.setPrefixHeaderFields(header);
                conn.sendPOSTRequest(new URL("https://growthbookapp-api.net/personalrate"), json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
           Float rate = _rateCache.get(Integer.valueOf(item.getId()));

            holder.setRateStatus(rate);
        }

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
        float rate = 0;
        try{
            JSONObject result = new JSONObject(s);
            if ( result.get("result").toString().equals("0") == true ) {
               rate = Float.valueOf(result.get("rate").toString());
            } else {

            }
        } catch (Exception e) {

        }
        float finalRate = rate;
        _mainHandler.post(()->{

            Integer id = _httpTasks.get(httpConn);
            _rateCache.put(id, finalRate);
            ViewHolder viewHolder = _holderTasks.get(id);
            if (viewHolder == null ){
                return;
            }
             _httpTasks.remove(httpConn);
             _holderTasks.remove(id);

            if ( viewHolder.getCurrentHoldItem() == id.intValue() ){
                viewHolder.setRateStatus(finalRate);
            }
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

    public String MD5(String str){

        String MD5 = "";

        try{

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(str.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();

            for(int i = 0 ; i < byteData.length ; i++){

                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));

            }

            MD5 = sb.toString();



        }catch(NoSuchAlgorithmException e){

            e.printStackTrace();

            MD5 = null;

        }

        return MD5;

    }

    public void clear() {
        _container.clear();
        _holderTasks.clear();
        _httpTasks.clear();
        _rateCache.clear();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private int _currentHoldItem;

        private TextView _descriptionTextview;
        private TextView _dateTextview;
        private TextView _pointTextview;
        private Button _doRateButton;

        private ImageView _ratingIcon;
        private TextView _ratingText;
        private RatingBar _ratingBar;
        private TextView  _ratingNum;

        public ViewHolder(View itemView) {
            super(itemView);
            _descriptionTextview = (TextView)itemView.findViewById(R.id.book_pass_description_textview);
            _dateTextview = (TextView)itemView.findViewById(R.id.book_pass_date_textview);
            _pointTextview = (TextView)itemView.findViewById(R.id.book_pass_point_textview);
            _ratingBar = (RatingBar)itemView.findViewById(R.id.book_pass_rating_bar);
            _doRateButton = (Button)itemView.findViewById(R.id.do_rate_button);

            _ratingIcon = (ImageView)itemView.findViewById(R.id.difficulty_icon_imageview);
            _ratingText = (TextView)itemView.findViewById(R.id.difficulty_string_text);
            _ratingNum = (TextView)itemView.findViewById(R.id.rating_num);

        }
        public void setCurrentHoldItem(int id) {
            _currentHoldItem = id;
        }

        public int getCurrentHoldItem() {
            return _currentHoldItem;
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

        public void setRateStatus(float rate){
            int intRate = (int)Math.ceil(rate);
            System.out.println("Intrate :" + intRate);
            _ratingBar.setRating(rate);
            _ratingNum.setText(String.valueOf(rate));
            switch( intRate ){
                case 0:
                    _ratingIcon.setVisibility(View.INVISIBLE);
                    _ratingText.setText("아직 평가되지 않았습니다");
                    _doRateButton.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    _ratingIcon.setVisibility(View.VISIBLE);
                    _ratingIcon.setImageResource(R.drawable.star1);
                    _ratingText.setText("너무 쉬워요");
                    _doRateButton.setVisibility(View.INVISIBLE);

                    break;
                case 2:
                    _ratingIcon.setVisibility(View.VISIBLE);
                    _ratingIcon.setImageResource(R.drawable.star2);
                    _ratingText.setText("쉬워요");
                    _doRateButton.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    _ratingIcon.setVisibility(View.VISIBLE);
                    _ratingIcon.setImageResource(R.drawable.star3);
                    _ratingText.setText("보통이에요");
                    _doRateButton.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    _ratingIcon.setVisibility(View.VISIBLE);
                    _ratingIcon.setImageResource(R.drawable.star4);
                    _ratingText.setText("어려워요");
                    _doRateButton.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    _ratingIcon.setVisibility(View.VISIBLE);
                    _ratingIcon.setImageResource(R.drawable.star5);
                    _ratingText.setText("너무 어려워요");
                    _doRateButton.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}
