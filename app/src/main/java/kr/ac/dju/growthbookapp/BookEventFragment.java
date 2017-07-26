package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by geonyounglim on 2017. 6. 29..
 */

public class BookEventFragment extends NavigationBarFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HttpConn.CallbackListener, OnArticleClickListener{

    /* reuse of News class*/
    public class BookEvent extends NewsArticle {
        public BookEvent(int id, String title, String detailURL, String author, String date) {
            super(id, title, detailURL, author, date);
        }
    }
    public class BookEventRecyclerViewAdapter extends NewsRecyclerViewAdapter {}



    private RecyclerView _newsRecyclerView;

    private NewsRecyclerViewAdapter _adapter;
    private RecyclerView.OnScrollListener _scrollListener;

    private NewsRecyclerViewAdapter _searchAdapter;

    private SwipeRefreshLayout _swipeRefreshLayout;

    private int _maxPage;
    private int _currentPage;
    private boolean _initialRefresh;
    private boolean _isLoading;

    private boolean _is_refresh;

    private Set<Integer> _pageSet;
    private Set<Integer> _searchPageSet;

    private List<NewsArticle> _backupStatus;
    private int _backupCurrentPage, _backupMaxPage;


    public BookEventFragment(){
        super(R.layout.fragment_book_event, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.book_event), (View v) -> {
            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {
            activeSearch();
        });

        hideRightAcc();

        _maxPage = 0;
        _currentPage = 1;
        _isLoading = false;
        _is_refresh = false;

        _pageSet = new HashSet<Integer>();
        _newsRecyclerView = (RecyclerView) result.findViewById(R.id.book_event_recyclerview);
        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.book_event_swiperefreshlayout);
        _swipeRefreshLayout.setOnRefreshListener(this);
        _swipeRefreshLayout.setRefreshing(true);

        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.colorHighLight), ContextCompat.getColor(getActivity(),R.color.colorStrongHighLight));
        _adapter = new NewsRecyclerViewAdapter();
        _adapter.setOnArticleListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _newsRecyclerView.setLayoutManager(layoutManager);

        _scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    if ( _isLoading == false){
                        _is_refresh = false;
                        onRefresh();

                    }

                }
            }
        };


        _newsRecyclerView.addOnScrollListener(_scrollListener);

        _newsRecyclerView.setAdapter(_adapter);
        //_newsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        _newsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        onRefresh();


        return result;
    }

    @Override
    public void onRefresh() {
        if ( _is_refresh == true ){

            _currentPage = 1;
            _maxPage = 0;
            _adapter.setState(new ArrayList<>());
            _adapter.notifyDataSetChanged();
            _pageSet.clear();

        }

        if ( _pageSet.contains(Integer.valueOf(_currentPage)) == true ) {
            _swipeRefreshLayout.setRefreshing(false);
            _is_refresh = true;
            return;
        }

        _isLoading = true;
        HttpConn conn = new HttpConn();
        conn.setUserAgent("GBApp");
        conn.setCallBackListener(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(_currentPage));
        try {
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds5_1.html"), params);
        } catch (Exception e) {

        }
        _is_refresh = true;

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser= new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements subBody = body.getElementsByClass("sub_body");

        Elements items = subBody.get(0).getElementsByClass("tahoma8");

        List<BookEvent> toAdds = new ArrayList<BookEvent>();

        Elements anchors = body.getElementsByTag("a");

        for ( Element anchor: anchors ){
            int maxPage = 0;
            String href = anchor.attr("href");
            boolean isPage = href.matches("/ds5_1\\.html\\?db=ds5_1&SK=&SN=&kind3=&idx=&page=[0-9]*");

            if ( isPage ){
                String strPage = anchor.text().replace("[","").replace("]","");
                int currentPage = Integer.parseInt(strPage);
                maxPage = currentPage > maxPage ?  currentPage : maxPage;
                _maxPage = maxPage;
            }

        }
        for ( Element item : items) {
            if( item.text().matches("[(][0-9]*[)]") == true){
                Element parent = item.parent();
                Element achor = parent.getElementsByTag("a").get(0);
                String title = achor.text();
                String url = achor.attr("href");

                if ( parent.tagName().equals("div")){
                    parent = parent.parent();
                }

                String id = parent.previousElementSibling().previousElementSibling().text();
                String author = parent.nextElementSibling().nextElementSibling().text();
                String date = parent.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text();

                if(id.equals("[공지]")==true){
                    id = Integer.toString(0x7FFFFFFF);
                }
                toAdds.add(new BookEvent(Integer.parseInt(id), title, url, author, date));
            }

        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{
            _adapter.removeLoadingProgressItem();
            _adapter.notifyDataSetChanged();
            for( NewsArticle article : toAdds){
                _adapter.addItem(article);
            }

            if ( _maxPage  > _currentPage  ){
                _adapter.addItem(_adapter.getLoadingProgressItem());
                _newsRecyclerView.scrollToPosition(_adapter.getItemCount()+1);
            }
            _pageSet.add(Integer.valueOf(_currentPage));


            _currentPage = _currentPage + 1 > _maxPage ? _currentPage : _currentPage+1;
            _isLoading = false;
            _swipeRefreshLayout.setRefreshing(false);
        });



    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {
        _isLoading = false;

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {
        _isLoading = false;

    }

    @Override
    public void OnArticleClick(String id, String title, String author, String date, String url) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("url", url);

        pushFragmentTo(R.id.front_side_container, new NewsAricleDetailFragment(), args);

    }
}
