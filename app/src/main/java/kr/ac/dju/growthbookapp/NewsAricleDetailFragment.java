package kr.ac.dju.growthbookapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 6. 3..
 */

public class NewsAricleDetailFragment extends NavigationBarFragment implements HttpConn.CallbackListener {
    WebView _detailWebView;
    String _contentURL;
    public NewsAricleDetailFragment() {
        super(R.layout.fragment_newsdetailarticle, R.id.root_constraint);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();

        setWithCommonNavigationBar(args.getString("title"),(View v)->{

        }, (View v)->{

        });

        setBackButton((View v)->{

            getFragmentManager().popBackStack();
        });

        hideRightAcc();

        _detailWebView = (WebView)result.findViewById(R.id.detail_webview);
        _contentURL = args.getString("url");
        HttpConn conn = new HttpConn();

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(this);
        try{
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr" + _contentURL), new HashMap<String, String>());
        } catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }


    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        //WebSettings settings = _detailWebView.getSettings();
        //settings.setDefaultTextEncodingName("euc-kr");

        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Element subBody = body.getElementsByClass("sub_body").get(0);
        String rawHTML = subBody.getElementsByTag("table").get(1).html();
        Handler mainHandler = new Handler(getActivity().getMainLooper());

        mainHandler.post(()->{
//            String resultHTML = "<table style=\"display:inline;\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">" + rawHTML + "</table>";
            String resultHTML =  rawHTML;
            _detailWebView.loadData(resultHTML, "text/html; charset=UTF-8", null);
        });


    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
