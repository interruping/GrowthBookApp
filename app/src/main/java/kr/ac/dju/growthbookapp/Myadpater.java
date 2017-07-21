package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dju.book.HttpConn;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by dodrn on 2017-06-28.
 */

public class Myadpater extends RecyclerView.Adapter<Myadpater.ViewHolder> implements HttpConn.CallbackListener {
    private ArrayList<BookListData> bookListDatas = new ArrayList<BookListData>();
    private Context mContext;
    private String mButton = null;
    private ApplyButtonClickListner _applyButtonClickListener;
    private StarRatingBarDialog dialog;
    private String mDevice;
    private Map<String, String> headers;
    private Myadpater _self = this;
    private boolean mUnapproved;


    public Myadpater(ArrayList<BookListData> bookdata, Context mcontext) {
        bookListDatas = bookdata;
        mContext = mcontext;
        mUnapproved = false;
    }

    public void setdevice(String device) {
        this.mDevice = device;
    }

    public void setmButton(String button) {
        mButton = button;
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

        // UnapprovedBookFragment에서 보여주는 신청버튼 조건식
        if (mButton != null && mButton.equals("apply") == true) {
            if (mUnapproved == true) {
                holder.getButton().setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _applyButtonClickListener.applyButtonOnClicked(position);
                            }

                        });
            }
        }

        // ApprovedBookFragment에서 보여주는 난이도 평점 버튼 조건식
        if (mButton != null && mButton.equals("star") == true) {
            holder.getButton().setText("평점주기");
            holder.getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = bookListDatas.get(position).GetBookSubject();
                    String hash_name = MD5(name);
                    dialog = new StarRatingBarDialog(v.getContext(),
                            hash_name,
                            mSubmitClickListener,
                            mCancleClickListener);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

                // RatingBar submit Button class
                View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String mDevice = dialog.getmDevice();
                        float rate = dialog.getmRate();
                        String mHashName = dialog.getmHash_Book_Name();

                        Map<String, String> header = new HashMap<String, String>();
                        HttpConn conn = new HttpConn();
                        conn.setCallBackListener(_self);
                        header.put("Content-type", "application/json");
                        conn.setPrefixHeaderFields(header);
                        try {

                            JSONObject json = new JSONObject();
                            json.put("device_id", mDevice);
                            json.put("book_id", mHashName);
                            json.put("rate", rate);
                            try {
                                conn.sendPOSTRequest(new URL("https://growthbookapp-api.net/adduser"), json.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "제출되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                };

                // RatingBar Cancle Button
                View.OnClickListener mCancleClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                };

            });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View _root;
        ImageView bookImg;
        TextView bookname, author2, company2, booklist2, passpoint2, authpass2, bookdays;
        Button applyBtn;

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


        }

        public Button getButton() {
            applyBtn = (Button) _root.findViewById(R.id.apply_button);
            return applyBtn;
        }

    }


    public void setOnClickListener(ApplyButtonClickListner listener) {
        _applyButtonClickListener = listener;
    }

    private String MD5(String name) {
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


}
