package kr.ac.dju.growthbookapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dju.book.HttpConn;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApprovedBookFragment extends Fragment implements HttpConn.CallbackListener{
    private Map<String,String> headers;
    private Map<String,String> params = new HashMap<String,String>();
    private String url="";

    public ApprovedBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GetUrl();
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie",cookie);
        con.setPrefixHeaderFields(headers);

        try {
            con.sendRequest(HttpConn.Method.GET, new URL(url), params);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }
        return inflater.inflate(R.layout.fragment_approved_book, container, false);
    }

    public void GetUrl() {
        url = getArguments().getString("url");
        params = (HashMap<String, String>)getArguments().getSerializable("HashMap");

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        System.out.println();

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
