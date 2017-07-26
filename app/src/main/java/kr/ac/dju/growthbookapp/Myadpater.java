package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dju.book.HttpConn;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.rating;


/**
 * Created by dodrn on 2017-06-28.
 */

public class Myadpater extends RecyclerView.Adapter<Myadpater.ViewHolder> implements HttpConn.CallbackListener {
    private ArrayList<BookListData> bookListDatas = new ArrayList<BookListData>();
    private Handler _mainHandler;
    private Map<Integer, Float> _rateCache;
    private Map<Integer, String> _numofpersonCache;
    private Map<HttpConn, Integer> _httpTasks;
    private Map<Integer, ViewHolder> _holderTasks;
    private Context mContext;
    private ApplyButtonClickListner _applyButtonClickListener;


    private boolean mUnapproved;


    public Myadpater(ArrayList<BookListData> bookdata, Context mcontext) {
        bookListDatas = bookdata;
        mContext = mcontext;
        mUnapproved = false;
        _mainHandler = new Handler(mcontext.getMainLooper());
        _rateCache = new HashMap<>();
        _numofpersonCache = new HashMap<>();
        _httpTasks = new HashMap<>();
        _holderTasks = new HashMap<>();
    }


    public void settingForUnapproved() {
        mUnapproved = true;
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<BookListData> getBookLIstDatas() {
        return bookListDatas;
    }


    public String MD5(String name) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(name.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String hashname = Integer.toHexString(0xFF & aMessageDigest);
                while (hashname.length() < 2)
                    hashname = 0 + hashname;
                hexString.append(hashname);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unprovedbook_item, parent, false);
        View container = v.findViewById(R.id.unprovedbook_item_container);

        ViewHolder holder = new ViewHolder(container);

        return holder;
    }

    // ViewHolder Setting
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookListData item = bookListDatas.get(position);
        Integer currentId = Integer.valueOf(item.getId());
        holder.setCurrentHoldItem(currentId.intValue());

        holder.author2.setText(bookListDatas.get(position).GetBookAuthor());
        holder.authpass2.setText(bookListDatas.get(position).GetAuthPoint());
        holder.booklist2.setText(bookListDatas.get(position).GetBookList());
        holder.company2.setText(bookListDatas.get(position).GetBookCompany());
        holder.passpoint2.setText(bookListDatas.get(position).GetPassPoint());
        holder.bookname.setText(bookListDatas.get(position).GetBookSubject());
        holder.bookdays.setText(bookListDatas.get(position).GetBookDday());
        // 사진 출력 함수 Picasso
        Picasso.with(mContext)
                .load(bookListDatas.get(position).GetBookSrc())
                .into(holder.bookImg);


        if (mUnapproved == true) {
            holder.getButton().setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _applyButtonClickListener.applyButtonOnClicked(position);
                        }

                    });
        }

        if (_rateCache.containsKey(Integer.valueOf(item.getId())) == false) {
            String bookname = bookListDatas.get(position).GetBookSubject();
            String hash_name = MD5(bookname);
            HttpConn conn = new HttpConn();
            _httpTasks.put(conn, currentId);
            _holderTasks.put(currentId, holder);
            conn.setCallBackListener(new HttpConn.CallbackListener() {
                @Override
                public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                    float rate = 0;
                    String numOfper = null;
                    try {
                        JSONObject result = new JSONObject(s);
                        if (result.get("result").toString().equals("0") == true) {
                            rate = result.get("AVG(rate)").toString().equals("null") ? 0 : Float.valueOf(result.get("AVG(rate)").toString());

                            numOfper = result.get("COUNT(rate)").toString();


                        } else {

                        }
                    } catch (Exception e) {

                    }
                    float finalfloats = rate;
                    String numOfPerson = numOfper;
                    _mainHandler.post(() -> {

                        Integer id = _httpTasks.get(httpConn);
                        _rateCache.put(id, finalfloats);
                        _numofpersonCache.put(id, numOfPerson);
                        ViewHolder viewHolder = _holderTasks.get(id);
                        if (viewHolder == null) {
                            return;

                        }
                        _httpTasks.remove(httpConn);
                        _holderTasks.remove(id);

                        if (viewHolder.getCurrentHoldItem() == id.intValue()) {

                            viewHolder.setRateStatus(finalfloats, numOfPerson);
                            notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

                }

                @Override
                public void requestTimeout(HttpConn httpConn) {

                }
            });


            try {

                JSONObject json = new JSONObject();
                json.put("book_id", hash_name);
                int contentLength = json.toString().length();
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-type", "application/json");
                header.put("Content-Length", String.valueOf(contentLength));
                header.put("Cookie", TimeCookieGenarator.OneTimeInstance().gen(String.valueOf(contentLength)));

                conn.setPrefixHeaderFields(header);

                try {
                    conn.sendPOSTRequest(new URL("https://growthbookapp-api.net/loadrate"), json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            Float rate = _rateCache.get(Integer.valueOf(item.getId()));
            String num = _numofpersonCache.get(Integer.valueOf(item.getId()));

            holder.setRateStatus(rate, num);
        }
    }


    @Override
    public int getItemCount() {
        return bookListDatas.size();
    }


    // 서버에서 전송하기 위한 HTTPCallbackListener 함수
    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        System.out.println(s);

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {
        System.out.print(s);
    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }


    // 인터페이스
    public interface ApplyButtonClickListner {
        public void applyButtonOnClicked(int position);
    }

    public void clear() {
        _holderTasks.clear();
        _httpTasks.clear();
        _rateCache.clear();
        _numofpersonCache.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int _currentHoldItem;

        View _root;
        ImageView bookImg;
        TextView bookname, author2, company2, booklist2, passpoint2, authpass2, bookdays;
        Button applyBtn;
        ImageView rating_icon;
        TextView rating_avg, rating_context, textview11, mNumOfperson;
        RatingBar rating_bar;

        public ViewHolder(View view) {
            super(view);
            _root = view;
            bookImg = (ImageView) view.findViewById(R.id.bookimg);
            bookname = (TextView) view.findViewById(R.id.book_subject);
            author2 = (TextView) view.findViewById(R.id.book_author0);
            company2 = (TextView) view.findViewById(R.id.book_company0);
            booklist2 = (TextView) view.findViewById(R.id.booklist0);
            passpoint2 = (TextView) view.findViewById(R.id.pass_point0);
            authpass2 = (TextView) view.findViewById(R.id.autho_point0);
            bookdays = (TextView) view.findViewById(R.id.book_day0);
            textview11 = (TextView) view.findViewById(R.id.textView11);
            rating_avg = (TextView) view.findViewById(R.id.avg_conetext);
            rating_context = (TextView) view.findViewById(R.id.level_context);
            rating_icon = (ImageView) view.findViewById(R.id.img_icon);
            rating_bar = (RatingBar) view.findViewById(R.id.ratingBar2);
            mNumOfperson = (TextView) view.findViewById(R.id.numOfPerson);
        }

        public void setCurrentHoldItem(int id) {
            _currentHoldItem = id;
        }

        public int getCurrentHoldItem() {
            return _currentHoldItem;
        }

        public Button getButton() {
            applyBtn = (Button) _root.findViewById(R.id.apply_button);
            return applyBtn;
        }

        public void setRateStatus(float rate, String numofperson) {
            int intRate = (int) Math.ceil(rate);
            System.out.println("Intrate :" + intRate);
            rating_bar.setRating(rate);
            rating_avg.setText(String.valueOf(rate));
            mNumOfperson.setText(numofperson + "명 평가함");
            switch (intRate) {
                case 0:
                    rating_icon.setVisibility(View.INVISIBLE);
                    rating_context.setText("아직 평가되지 않았습니다");
                    break;
                case 1:
                    rating_icon.setVisibility(View.VISIBLE);
                    rating_icon.setImageResource(R.drawable.star1);
                    rating_context.setText("너무 쉬워요");
                    break;
                case 2:
                    rating_icon.setVisibility(View.VISIBLE);
                    rating_icon.setImageResource(R.drawable.star2);
                    rating_context.setText("쉬워요");
                    break;
                case 3:
                    rating_icon.setVisibility(View.VISIBLE);
                    rating_icon.setImageResource(R.drawable.star3);
                    rating_context.setText("보통이에요");
                    break;
                case 4:
                    rating_icon.setVisibility(View.VISIBLE);
                    rating_icon.setImageResource(R.drawable.star4);
                    rating_context.setText("어려워요");
                    break;
                case 5:
                    rating_icon.setVisibility(View.VISIBLE);
                    rating_icon.setImageResource(R.drawable.star5);
                    rating_context.setText("너무 어려워요");
                    break;
            }

        }

    }

    public void setOnClickListener(ApplyButtonClickListner listener) {
        _applyButtonClickListener = listener;
    }


}
