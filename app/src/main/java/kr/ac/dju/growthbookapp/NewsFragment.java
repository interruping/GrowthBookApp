package kr.ac.dju.growthbookapp;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


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
 * Created by geonyounglim on 2017. 5. 28..
 */

public class NewsFragment extends NavigationBarFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HttpConn.CallbackListener, OnArticleClickListener, NavigationBarFragment.SearchBarEventListener {

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

    public NewsFragment() {
        super(R.layout.fragment_news, R.id.root_constraint);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.news),(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{
            activeSearch();
        });

        _maxPage = 0;
        _currentPage = 1;
        _isLoading = false;
        _is_refresh = false;

        _pageSet = new HashSet<Integer>();
        _newsRecyclerView = (RecyclerView) result.findViewById(R.id.news_recyclerview);
        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.news_swiperefreshlayout);
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
        setSearchBarEventListener(this);
        onRefresh();
        return result;
    }

    @Override
    public void onClick(View v) {
        MainActivity ma = (MainActivity)getActivity();
        ma.toggleMenu();
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
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds6_1.html"), params);
        } catch (Exception e) {

        }
        _is_refresh = true;

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

        BookServerDataParser parser= new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements subBody = body.getElementsByClass("sub_body");

        Elements items = subBody.get(0).getElementsByClass("tahoma8");

        List<NewsArticle> toAdds = new ArrayList<NewsArticle>();

        Elements anchors = body.getElementsByTag("a");

        for ( Element anchor: anchors ){
            int maxPage = 0;
            String href = anchor.attr("href");
            boolean isPage = href.matches("/ds6_1\\.html\\?db=ds5_1&SK=&SN=&kind3=&idx=&page=[0-9]*");

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
                toAdds.add(new NewsArticle(Integer.parseInt(id), title, url, author, date));
            }

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void OnArticleClick(String id, String title, String author, String date, String url) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("url", url);

        pushFragmentTo(R.id.front_side_container, new NewsAricleDetailFragment(), args);

    }


    @Override
    public void searchBarWillActive() {
        _searchAdapter = new NewsRecyclerViewAdapter();
        _searchAdapter.setOnArticleListener(this);
        _swipeRefreshLayout.setOnRefreshListener(null);
        _newsRecyclerView.removeOnScrollListener(_scrollListener);
        _newsRecyclerView.swapAdapter(_searchAdapter, true);
        _backupCurrentPage = _currentPage;
        _backupMaxPage = _maxPage;
        _currentPage = 1;
        _maxPage = 0;
    }

    @Override
    public void searchBarWillInactive() {
        _currentPage = _backupCurrentPage;
        _maxPage = _backupMaxPage;
        _newsRecyclerView.swapAdapter(_adapter, true);
        _swipeRefreshLayout.setOnRefreshListener(this);
        _newsRecyclerView.addOnScrollListener(_scrollListener);
        _adapter.notifyDataSetChanged();

    }

    @Override
    public void searchBarKeyInput(String input, int count) {
        _isLoading = true;
        if ( input.length() == 0 ){
            return;
        }
        String searchQuery = "";
        try {

            byte[] bytes = input.getBytes("EUC-KR");
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<bytes.length; i++) {
                sb.append(String.format("%%%02X",bytes[i]));
            }
            searchQuery = sb.toString();

        } catch (Exception e){

        }

        HttpConn conn = new HttpConn();
        conn.setUserAgent("GBApp");
        conn.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                BookServerDataParser parser= new BookServerDataParser(s);
                Element body = parser.getBody();
                Elements subBody = body.getElementsByClass("sub_body");

                Elements items = subBody.get(0).getElementsByClass("tahoma8");

                List<NewsArticle> toAdds = new ArrayList<NewsArticle>();

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

                        if(id.equals("[공지]")==false){
                            toAdds.add(new NewsArticle(Integer.parseInt(id), title, url, author, date));
                        }
                    }

                }

                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(()->{
                    openScreen();
                    if( toAdds.size() == 0 ){
                        _searchAdapter.setState(new ArrayList<NewsArticle>());
                        _searchAdapter.addItem(new NewsArticle(404, "일치하는 항목이 없습니다", "", "", ""));
                    }else {
                        _searchAdapter.setState(toAdds);
                    }

                    _searchAdapter.notifyDataSetChanged();
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
        });
        Map<String, String> params = new HashMap<String, String>();
        params.put("db", "ds5_1");
        params.put("SK", "subj");
        params.put("c","list");
        params.put("page", String.valueOf(_currentPage));
        params.put("SN", searchQuery);
        try {
            conn.sendRequest(HttpConn.Method.POST, new URL("https://book.dju.ac.kr/ds6_1.html"), params);
        } catch (Exception e) {

        }

    }

    @Override
    public void searchBarFocused() {

    }

    @Override
    public void searchBarUnfocesed() {

    }
}
