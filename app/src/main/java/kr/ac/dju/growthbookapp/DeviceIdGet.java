package kr.ac.dju.growthbookapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dodrn on 2017-07-18.
 */

public class DeviceIdGet implements HttpConn.CallbackListener {

    private Map<String, String> headers;
    private Map<String, String> param = new HashMap<>();
    private String mDeivce;

    public DeviceIdGet() {

    }

    public String getDeivce() {
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        param.put("litem", "9");
        param.put("tab", "1");
        param.put("page", "1");

        try {
            con.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds2_2.html"), param);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }

        return mDeivce;
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);

        Element body = parser.getBody();


        Elements values = body.getElementsByClass("window2");
        Element value = values.first();
        Element val = value.nextElementSibling();
        String va = val.attr("value");
        mDeivce = va;
    }


    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}