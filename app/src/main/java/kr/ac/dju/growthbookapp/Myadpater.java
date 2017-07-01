package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by dodrn on 2017-06-28.
 */

public class Myadpater extends RecyclerView.Adapter<Myadpater.ViewHolder> {
    private ArrayList<BookListData> bookListDatas = new ArrayList<BookListData>();
    private Context mContext;
    public Myadpater(ArrayList<BookListData> bookdata, Context mcontext){
        bookListDatas = bookdata;
        mContext = mcontext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unprovedbook_item,parent,false);
        View container = v.findViewById(R.id.unprovedbook_item_container);
        ViewHolder holder = new ViewHolder(container);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.author2.setText(bookListDatas.get(position).GetBookAuthor());
        holder.authpass2.setText(bookListDatas.get(position).GetAuthPoint());
        holder.booklist2.setText(bookListDatas.get(position).GetBookList());
        holder.company2.setText(bookListDatas.get(position).GetBookCompany());
        holder.passpoint2.setText(bookListDatas.get(position).GetPassPoint());
        holder.bookname.setText(bookListDatas.get(position).GetBookSubject());
        holder.bookdays.setText(bookListDatas.get(position).GetBookDday());
        new DownloadImageTask(holder.bookImg).execute(bookListDatas.get(position).GetBookSrc());


    }

    public void addItem(BookListData data){
        bookListDatas.add(data);
    }
    @Override
    public int getItemCount() {
        return bookListDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImg;
        TextView bookname,author2,company2,booklist2,passpoint2,authpass2,bookdays;

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
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {

                HttpURLConnection conn = (HttpURLConnection)(new java.net.URL(urldisplay)).openConnection();
                InputStream in = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                mIcon11 = BitmapFactory.decodeStream(in);

                System.out.println();
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}