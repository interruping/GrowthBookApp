package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RecyclerView _oneByOneRecyclerView;
    private OneByOneRecycleViewAdapter _adapter;
    private SwipeRefreshLayout _swipeRefreshLayout;


    public OneByOneFragment() {
        super(R.layout.fragment_one_by_one, R.id.root_constraint);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.one_by_one), (View v) -> {
            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {
            activeSearch();
        });

        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.one_by_one_swiperefreshlayout);
        _swipeRefreshLayout.setColorSchemeColors(0xFF61BD6D, 0xFF41A85F);
        _swipeRefreshLayout.setOnRefreshListener(this);
        _swipeRefreshLayout.setRefreshing(true);

        _oneByOneRecyclerView = (RecyclerView) result.findViewById(R.id.one_by_one_recyclerview);

        _adapter = new OneByOneRecycleViewAdapter();
        _adapter.setOnArticleListener(this);

        LinearLayoutManager layoutManger = new LinearLayoutManager(getActivity());
        layoutManger.setOrientation(LinearLayout.VERTICAL);

        _oneByOneRecyclerView.setLayoutManager(layoutManger);

        _oneByOneRecyclerView.setAdapter(_adapter);
        _oneByOneRecyclerView.setItemAnimator(new DefaultItemAnimator());

        onRefresh();
        return result;
    }

    @Override
    public void onRefresh() {
        HttpConn conn = new HttpConn();

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(this);
        HttpConn.CookieStorage storage = HttpConn.CookieStorage.sharedStorage();

        Map<String, String> header = new HashMap<String,String>();
        header.put("Cookie", storage.getCookie());


        conn.setPrefixHeaderFields(header);
        try {
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds6_3.html"), new HashMap<String, String>());
        } catch (Exception e ){

        }


    }

    @Override
    public void OnArticleClick(String id, String title, String author, String date, String url) {

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements subBody = body.getElementsByClass("sub_body");

        Elements items = subBody.get(0).getElementsByClass("tahoma8");

        List<OneByOneItem> toAdds = new ArrayList<OneByOneItem>();

        Elements anchors = body.getElementsByTag("a");


        for (Element item : items) {
            if (item.text().matches("[(][0-9]*[)]") == true) {
                Element parent = item.parent();
                Element achor = parent.getElementsByTag("a").get(0);
                String title = achor.text();
                String url = achor.attr("href");

                if (parent.tagName().equals("div")) {
                    parent = parent.parent();
                }

                String id = parent.previousElementSibling().previousElementSibling().text();
                String author = parent.nextElementSibling().nextElementSibling().text();
                String date = parent.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().text();

                if (id.equals("[공지]") == true) {
                    id = Integer.toString(0x7FFFFFFF);
                }
                toAdds.add(new OneByOneItem(Integer.parseInt(id), title, url, author, date));
            }

            Handler mainHandler = new Handler(getActivity().getMainLooper());
            mainHandler.post(() -> {
                for (OneByOneItem oneByOneItem : toAdds) {
                    _adapter.addItem(oneByOneItem);
                }
                _swipeRefreshLayout.setRefreshing(false);
            });


        }
    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}