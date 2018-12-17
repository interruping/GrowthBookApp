package kr.ac.dju.growthbookapp;

import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by geonyounglim on 2017. 6. 2..
 */
public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> implements OnArticleClickListener {


    OnArticleClickListener _listener;

    private List<NewsArticle> _container;
    static final private NewsArticle PROGRESS_LOADING_ITEM = new NewsArticle(0xFFFFFFFF, "0xDEADBEEF", "0xDEADBEEF", "0xDEADBEEF", "0xDEADBEEF");


    public NewsRecyclerViewAdapter() {
        _container = new ArrayList<NewsArticle>();
    }

    public void setOnArticleListener(OnArticleClickListener listener){
        _listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_article,parent,false);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = parent.getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        view.setBackgroundResource(backgroundResource);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.reset();
        NewsArticle toBind = _container.get(position);

        if ( toBind.getId() == 0x7FFFFFFF) {
            holder.getIdTextView().setText("공지");
        } else {
            holder.getIdTextView().setText(Integer.toString(toBind.getId()));
        }

        holder.setURL(toBind.getDetailURL());
        holder.getTitleTextView().setText(toBind.getTitle());
        holder.getAuthorTextView().setText(toBind.getAuthor());
        holder.getDateTextView().setText(toBind.getDate());

        if ( toBind == PROGRESS_LOADING_ITEM ){
            holder.transProgressDummy();
        }
    }
    public final NewsArticle getLoadingProgressItem() {
        return PROGRESS_LOADING_ITEM;
    }

    public void removeLoadingProgressItem() {
        _container.remove(PROGRESS_LOADING_ITEM);
    }
    @Override
    public int getItemCount() {
        return _container.size();
    }
    @Override
    public void OnArticleClick(String id, String title, String author, String date, String url){
        _listener.OnArticleClick(id, title, author, date, url);
    }

    public void addItem(NewsArticle newNewsAriticle) {
        _container.add(newNewsAriticle);
    }

    public void setState(List<NewsArticle> list) {
        _container = list;
    }

    public List<NewsArticle> getState() {
        return _container;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        OnArticleClickListener _listener;

        private LinearLayout _newsArticleContainer;
        private TextView _idTextView;
        private TextView _titleTextView;
        private TextView _authorTextView;
        private TextView _dateTextView;
        private ProgressBar _loadingProgressBar;
        private String _url;

        public ViewHolder(View itemView, OnArticleClickListener listener){
            super(itemView);
            _newsArticleContainer = (LinearLayout)itemView.findViewById(R.id.news_article_container);
            _listener = listener;

            itemView.setOnClickListener(this);
             _idTextView = (TextView)itemView.findViewById(R.id.id_textview);
            _titleTextView = (TextView)itemView.findViewById(R.id.title_textview);
            _authorTextView = (TextView)itemView.findViewById(R.id.author_textview);
            _dateTextView = (TextView)itemView.findViewById(R.id.date_textview);
            _loadingProgressBar = (ProgressBar)itemView.findViewById(R.id.content_loading_progressBar);

            //_loadingProgressBar.setLayoutParams(params);
//            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0,0);
//            params.setMargins(0,0,0,0);
//            params.height = 0;
//            _loadingProgressBar.setLayoutParams(params);
//            _loadingProgressBar.setVisibility(View.INVISIBLE);

        }

        public TextView getIdTextView() {
            return _idTextView;
        }

        public TextView getTitleTextView() {
            return _titleTextView;
        }

        public TextView getAuthorTextView() {
            return _authorTextView;
        }

        public TextView getDateTextView() {
            return _dateTextView;
        }

        public void setURL( String url) {
            _url = url;
        }
        private String _getURL(){ return _url; }

        public void reset() {
            _newsArticleContainer.setVisibility(View.VISIBLE);
            _loadingProgressBar.setVisibility(View.INVISIBLE);
        }

        public void transProgressDummy() {
            _newsArticleContainer.setVisibility(View.INVISIBLE);
            _loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            _listener.OnArticleClick(_idTextView.getText().toString(), _titleTextView.getText().toString(), _authorTextView.getText().toString(), _dateTextView.getText().toString(), _getURL());
        }
    }


}
