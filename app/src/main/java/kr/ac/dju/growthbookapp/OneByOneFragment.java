package kr.ac.dju.growthbookapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
 * Created by geonyounglim on 2017. 7. 1..
 */

public class OneByOneFragment extends NavigationBarFragment implements SwipeRefreshLayout.OnRefreshListener, OnArticleClickListener, HttpConn.CallbackListener{

    public class OneByOneItem extends NewsArticle {
        public OneByOneItem(int id, String title, String detailURL, String author, String date) {
            super(id, title, detailURL, author, date);
        }
    }
    public class OneByOneRecycleViewAdapter extends NewsRecyclerViewAdapter {}

    public OneByOneFragment() {
        super(R.layout.fragment_one_by_one, R.id.root_constraint);
    }

    private final int DETAIL_FRAGMENT_CODE = 0xDEADBEEF;
    public static final String DETAIL_RETURN = "DETAIL_RETURL";

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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.one_by_one), (View v) -> {
            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {
            activeSearch();
        });

        _maxPage = 0;
        _currentPage = 1;
        _isLoading = false;
        _is_refresh = false;

        _pageSet = new HashSet<Integer>();
        _newsRecyclerView = (RecyclerView) result.findViewById(R.id.one_by_one_recyclerview);
        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.one_by_one_swiperefreshlayout);
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

        ImageButton writeButton = (ImageButton)result.findViewById(R.id.write_button);
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                pushFragmentTo(R.id.front_side_container, new OneByOneWriteQuestionFragment(), new Bundle());
            }
        });

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
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        conn.setPrefixHeaderFields(headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(_currentPage));
        try {
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds6_3.html"), params);
        } catch (Exception e) {

        }
        _is_refresh = true;

    }

    @Override
    public void OnArticleClick(String id, String title, String author, String date, String url) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("url", url);
        Fragment toRead = new OneByOneDetailFragment();
        toRead.setTargetFragment(this, DETAIL_FRAGMENT_CODE);
        pushFragmentTo(R.id.front_side_container, toRead, args);
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

        //boolean isPage = href.matches("/ds6_3\\.html\\?db=ds6_3&SK=&SN=&kind3=&idx=&page=[0-9]*");

        BookServerDataParser parser= new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements subBody = body.getElementsByClass("sub_body");

        Elements items = subBody.get(0).getElementsByClass("tahoma8");

        List<OneByOneItem> toAdds = new ArrayList<OneByOneItem>();

        Elements anchors = body.getElementsByTag("a");

        for ( Element anchor: anchors ){
            int maxPage = 0;
            String href = anchor.attr("href");
            boolean isPage = href.matches("/ds6_3\\.html\\?db=ds6_3&SK=&SN=&kind3=&idx=&page=[0-9]*");

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

//                if(id.equals("[공지]")==true){
//                    id = Integer.toString(0x7FFFFFFF);
//                }
                toAdds.add(new OneByOneItem(Integer.parseInt(id), title, url, author, date));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DETAIL_FRAGMENT_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                String value = data.getStringExtra(DETAIL_RETURN);
                if(value != null) {
                    onRefresh();
                }
            }
        }
    }
}
