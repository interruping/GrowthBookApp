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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by dodrn on 2017-06-28.
 */

public class Myadpater extends RecyclerView.Adapter<Myadpater.ViewHolder> implements View.OnClickListener{
    private ArrayList<BookListData> bookListDatas = new ArrayList<BookListData>();
    private Context mContext;
    private View.OnClickListener _onClickListener;
    private int item_Position;
    public Myadpater(ArrayList<BookListData> bookdata, Context mcontext){
        bookListDatas = bookdata;
        mContext = mcontext;
    }

    public int getItem_Position(){
        return item_Position;
    }


    public Context getContext() {
        return mContext;
    }

    public ArrayList<BookListData>  getBookLIstDatas(){
        return bookListDatas;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unprovedbook_item,parent,false);
        View container = v.findViewById(R.id.unprovedbook_item_container);
        Button applyButton = (Button)v.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(container);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        setItemPosition(position);
        holder.author2.setText(bookListDatas.get(position).GetBookAuthor());
        holder.authpass2.setText(bookListDatas.get(position).GetAuthPoint());
        holder.booklist2.setText(bookListDatas.get(position).GetBookList());
        holder.company2.setText(bookListDatas.get(position).GetBookCompany());
        holder.passpoint2.setText(bookListDatas.get(position).GetPassPoint());
        holder.bookname.setText(bookListDatas.get(position).GetBookSubject());
        holder.bookdays.setText(bookListDatas.get(position).GetBookDday());

       Picasso.with(mContext)
               .load(bookListDatas.get(position).GetBookSrc())

               .into(holder.bookImg);


    }

    @Override
    public int getItemCount() {
        return bookListDatas.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        _onClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        _onClickListener.onClick(v);
    }

    public void setItemPosition(int itemPosition) {
        item_Position = itemPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView bookImg;
        TextView bookname,author2,company2,booklist2,passpoint2,authpass2,bookdays;
        int position;

        public int getposition(){
            return position;
        }
        public ViewHolder(View view) {
            super(view);

            bookImg = (ImageView)view.findViewById(R.id.bookimg);
            bookname = (TextView)view.findViewById(R.id.book_subject);
            author2 = (TextView)view.findViewById(R.id.book_author0);
            company2 = (TextView)view.findViewById(R.id.book_company0);
            booklist2 = (TextView)view.findViewById(R.id.booklist0);
            passpoint2 = (TextView)view.findViewById(R.id.pass_point0);
            authpass2 = (TextView)view.findViewById(R.id.autho_point0);
            bookdays = (TextView)view.findViewById(R.id.book_day0);


        }

        @Override
        public void onClick(View v) {
            position = getAdapterPosition();
            Log.i("",String.valueOf(position));}
    }


    }
