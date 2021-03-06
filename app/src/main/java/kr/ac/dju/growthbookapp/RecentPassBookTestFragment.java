package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestFragment extends NavigationBarFragment implements SwipeRefreshLayout.OnRefreshListener, HttpConn.CallbackListener, RecentPassBookTestRecyclerViewAdapter.DoRateButtonClickListener, View.OnClickListener{

    private RecyclerView _recyclerView;
    private RecentPassBookTestRecyclerViewAdapter _adapter;

    private SwipeRefreshLayout _swipeRefreshLayout;

    private RecentPassBookTestFragment _self = this;
    private StarRatingBarDialog _dialog;


    public RecentPassBookTestFragment() {
        super(R.layout.fragment_recent_pass_book_test, R.id.root_constraint);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View result = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar("최근 합격 시험",(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{
            activeSearch();
        });

        hideRightAcc();

        _recyclerView = (RecyclerView)result.findViewById(R.id.recent_pass_book_test_recyclerview);
        _adapter = new RecentPassBookTestRecyclerViewAdapter(getActivity());
        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.recent_pass_book_test_swiperefreshlayout);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.colorHighLight), ContextCompat.getColor(getActivity(),R.color.colorStrongHighLight));
        _recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _recyclerView.setAdapter(_adapter);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _adapter.setDoRateButtonClickListener(this);

        onRefresh();
        return result;
    }

    @Override
    public void onRefresh() {
        _adapter.clear();
        _adapter.notifyDataSetChanged();
        HttpConn conn = new HttpConn();

        Map<String, String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        conn.setPrefixHeaderFields(headers);
        conn.setCallBackListener(this);
        try {
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds2_3.html?tab=2"), new HashMap<>());
        } catch (Exception e){

        }

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Element tbody = body.getElementById("mileage_body");
        Elements trs = tbody.getElementsByTag("tr");
        List<RecentPassBookTestItem> toAdd = new ArrayList<RecentPassBookTestItem>();

        for ( Element tr : trs ){
            Elements tds = tr.getElementsByTag("td");
            String date = tds.get(0).text();
            String desc = tds.get(1).text();
            String point = tds.get(2).text();

            if ( point.indexOf("-") != -1 ){
                continue;
            }

            if ( desc.equals("합계") == true ){
                continue;
            }
            RecentPassBookTestItem item = new RecentPassBookTestItem(desc, date, point);
            toAdd.add(item);
        }

        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{
            for (RecentPassBookTestItem item : toAdd) {
                _adapter.addItem(item);
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }

    @Override
    public void doRateButtonClicked(int position) {
        RecentPassBookTestItem item = _adapter.getItem(position);
        String tmpbookName = item.getDescription();
        int lastIndex = tmpbookName.indexOf("도서");
        String bookName = "- " + tmpbookName.substring(0, lastIndex);

        _dialog = new StarRatingBarDialog(getActivity(),
                RecentPassBookTestRecyclerViewAdapter.MD5(bookName),
                this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //취소

                    }
                });

        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();
    }

    @Override
    public void onClick(View v) {
            String mDevice = _dialog.getmDevice();
            float rate = _dialog.getmRate();
            String mHashName = _dialog.getmHash_Book_Name();


            HttpConn conn = new HttpConn();
            conn.setCallBackListener(new HttpConn.CallbackListener() {
                @Override
                public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

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
                json.put("device_id", mDevice);
                json.put("book_id", mHashName);
                json.put("rate", rate);
                int contentLength = json.toString().length();
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-type", "application/json");
                header.put("Content-Length", String.valueOf(contentLength));
                header.put("Cookie", TimeCookieGenarator.OneTimeInstance().gen(String.valueOf(contentLength)));

                conn.setPrefixHeaderFields(header);

                try {
                    conn.sendPOSTRequest(new URL("https://growthbookapp-api.net/addrate"), json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            _dialog.dismiss();
        }

}