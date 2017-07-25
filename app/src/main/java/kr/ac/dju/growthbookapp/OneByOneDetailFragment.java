package kr.ac.dju.growthbookapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 12..
 */

public class OneByOneDetailFragment extends NavigationBarFragment implements HttpConn.CallbackListener, View.OnClickListener {


    WebView _detailWebView;
    String _contentURL;

    String _deleteURL;
    String _modifyURL;
    String _replyURL;

    Button _deleteButton;
    Button _modifyButton;
    Button _replyButton;

    public final int FUNCTION_FRAGMENT = 0xDADDAD77;
    public static String REFRESH = "REFRESH";

    public final int RETURN_TO_ROOT = 0xDEADDAD2;
    public static String RETURN2ROOT = "RETURN2ROOT";

    private OneByOneDetailFragment _self = this;

    public OneByOneDetailFragment() {
        super(R.layout.fragment_one_by_one_detail, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(args.getString("title"), (View v) -> {
            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {
            activeSearch();
        });

        setBackButton((View v)->{
            getFragmentManager().popBackStack();
        });

        hideRightAcc();

        _detailWebView = (WebView)result.findViewById(R.id.detail_webview);

        _deleteButton = (Button)result.findViewById(R.id.delete_button);
        _modifyButton = (Button)result.findViewById(R.id.modify_button);
        _replyButton = (Button)result.findViewById(R.id.reply_button);



        _deleteButton.setOnClickListener(this);
        _modifyButton.setOnClickListener(this);
        _replyButton.setOnClickListener(this);

        _contentURL = args.getString("url");


        loadContent();
        return result;
    }

    private void loadContent() {
        HttpConn conn = new HttpConn();
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());

        conn.setPrefixHeaderFields(headers);

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(this);

        try{
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr" + _contentURL), new HashMap<String, String>());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showLoading() {
        _deleteButton.setEnabled(false);
        _modifyButton.setEnabled(false);
        _replyButton.setEnabled(false);

    }

    public void hideLoaing() {
        _deleteButton.setEnabled(true);
        _modifyButton.setEnabled(true);
        _replyButton.setEnabled(true);
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);

        Element body = parser.getBody();
        Element subBody = body.getElementsByClass("sub_body").get(0);
        String rawHTML = subBody.getElementsByTag("table").get(1).html();

        Elements anchors = body.getElementsByTag("a");

        for ( Element anchor : anchors ){

            String alt;
            int size = anchor.children().size();

            if ( size == 0) {
                continue;
            }

            Element childnode = anchor.child(0);

            if ( childnode != null && childnode.hasAttr("alt") == true)
                alt = anchor.child(0).attr("alt");
            else
                alt = "";

            if ( alt.equals("답변글") ){
                _replyURL = anchor.attr("href");
            } else if ( alt.equals("글수정") ){
                _modifyURL = anchor.attr("href");
            } else if ( alt.equals("글삭제") ) {
                _deleteURL = anchor.attr("href");
            }



        }


        Handler mainHandler = new Handler(getActivity().getMainLooper());

        mainHandler.post(()->{
            String resultHTML =  rawHTML;
            _detailWebView.loadData(resultHTML, "text/html; charset=UTF-8", null);
        });

        return;
    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        int id1 = id;

        switch (v.getId()) {
            case R.id.delete_button:
                _deleteThisPosting();
                break;
            case R.id.modify_button:
                _modifyThisPosting();
                break;
            case R.id.reply_button:
                _replyThisPosting();
                break;
        }


    }
    private void _replyThisPosting(){
        HttpConn conn = new HttpConn();
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        conn.setPrefixHeaderFields(headers);

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                BookServerDataParser parser = new BookServerDataParser(s);
                Element body = parser.getBody();

                Elements inputs = body.getElementsByTag("input");
                String title = "";
                for ( Element input : inputs ) {
                    if ( input.attr("type").equals("text") && input.attr("name").equals("subj")) {
                        title = input.attr("value");
                    }
                }

                Element content = body.getElementById("sample_contents_source");

                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("content", content.text());
                args.putString("url", _replyURL);

                Fragment modifyFragment = new OneByOneReplyFragment();
                modifyFragment.setTargetFragment(_self, RETURN_TO_ROOT);
                pushFragmentTo(R.id.front_side_container,modifyFragment,args);

            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });

        try{
            conn.sendRequest(HttpConn.Method.GET ,  new URL("https://book.dju.ac.kr" + _replyURL), new HashMap<>());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void _modifyThisPosting() {
        HttpConn conn = new HttpConn();
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        conn.setPrefixHeaderFields(headers);

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                BookServerDataParser parser = new BookServerDataParser(s);
                Element body = parser.getBody();

                Element title = body.getElementById("input_subj");
                Element content = body.getElementById("sample_contents_source");

                Bundle args = new Bundle();
                args.putString("title", title.attr("value"));
                args.putString("content", content.text());
                args.putString("url", _modifyURL);

                Fragment modifyFragment = new OneByOneModifyFragment();
                modifyFragment.setTargetFragment(_self, FUNCTION_FRAGMENT);

                pushFragmentTo(R.id.front_side_container,modifyFragment,args);

            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });

        try{
            conn.sendRequest(HttpConn.Method.GET ,  new URL("https://book.dju.ac.kr" + _modifyURL), new HashMap<>());
        } catch (Exception e){
            e.printStackTrace();
        }



    }
    private void _deleteThisPosting(){
        HttpConn conn = new HttpConn();
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        URL toDelete = null;
        try{
            toDelete = new URL("https://book.dju.ac.kr" + _deleteURL);
        }catch (Exception e) {

        }

        headers.put("Referer", toDelete.toString());
        conn.setPrefixHeaderFields(headers);

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

                showAlertView(AlertType.INFO, "글 삭제 완료", "질문 게시판으로 돌아갑니다", "확인", new AlertViewConfirmListener() {
                    @Override
                    public void alertViewConfirmed(AlertType type, String title, String description) {
                        Intent intent = new Intent();

                        intent.putExtra(OneByOneFragment.DETAIL_RETURN, "REFRESH");
                        Fragment fragment = getTargetFragment();
                        fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        getFragmentManager().popBackStack();
                    }
                });

           }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });

        try{

            Map<String,String> getParams = splitQuery(toDelete);

            Map<String,String> params = new HashMap<String,String>();
//            c:_delete
//            db:ds6_3
//            no:229
//            page:1
//            kind3:
//            SK:subj
//            SN:
//            idx:
            params.put("c", "_delete");
            params.put("db", "ds6_3");
            params.put("no", getParams.get("no"));
            params.put("page", getParams.get("page"));
            params.put("kind3","");
            params.put("SK", "subj");
            params.put("SN","");
            params.put("idx","");
            params.put("x","2");
            params.put("y","3");

            conn.sendRequest(HttpConn.Method.POST , toDelete, params);
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FUNCTION_FRAGMENT && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                String value = data.getStringExtra(REFRESH);
                if(value.equals(REFRESH)) {
                    loadContent();
                }
            }
        } else if ( requestCode == RETURN_TO_ROOT && resultCode == Activity.RESULT_OK ) {
            if (data != null) {
                String value = data.getStringExtra(RETURN2ROOT);
                if (value.equals(RETURN2ROOT)) {
                    Intent intent = new Intent();

                    intent.putExtra(OneByOneFragment.DETAIL_RETURN, "REFRESH");
                    Fragment fragment = getTargetFragment();
                    fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            }
        }


    }
}

