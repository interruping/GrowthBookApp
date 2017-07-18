package kr.ac.dju.growthbookapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geonyounglim on 2017. 7. 18..
 */

public class AutoLoginFragment extends Fragment implements HttpConn.CallbackListener{

    private AutoLoginFragment _self = this;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auto_login, container, false);
        getKey();
        return rootView;
    }

    private void getKey(){
        HttpConn conn = new HttpConn();

        conn.setUserAgent("GBApp");
        conn.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                String skey = null;
                try{
                    JSONObject result = new JSONObject(s);
                    if ( result.get("result").toString().equals("0") == true ) {
                        skey = result.get("skey").toString();
                    } else {

                    }
                } catch (Exception e) {

                }

                String tmpKey = skey;

                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(()->{
                    challengePre(tmpKey);
                });

            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {


                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(()->{

                });

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(()->{

                });
            }
        });
        try {
            String uuid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JSONObject json = new JSONObject();
            json.put("device_id", uuid);
            Map<String,String> header = new HashMap<String,String>();
            header.put("Content-type", "application/json");

            conn.setPrefixHeaderFields(header);
            conn.sendPOSTRequest(new URL("https://growthbookapp-api.net/adduser"),json.toString());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void challengePre(String key){
        UserInfoSafeStorage safe = new UserInfoSafeStorage(getActivity());
        safe.setKey(key);
        UserInfoSafeStorage.UserInfo user = safe.get();

        HttpConn con = new HttpConn();

        con.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                BookServerDataParser parser = new BookServerDataParser(s);
                Element body = parser.getBody();

                Elements inputs = body.getElementsByTag("input");

                if (inputs.size() > 0 ){
                    for ( Element input : inputs ){
                        if ( input.attr("name").equals("login_check") ){
                            String isOk = input.attr("value");

                            if ( isOk.equals("1") ){
                                challenge(user.id, user.pw);
                            } else if ( isOk.equals("0") ) {

                            } //if
                        } //if
                    } //for
                } else { // else of inputs.size() > 0

                }
            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "https://book.dju.ac.kr/ds19_1.html");

        con.setPrefixHeaderFields(headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("txtID", user.id);
        params.put("txtPW", user.pw);

        try {
            con.sendRequest(HttpConn.Method.GET, new URL("https://libweb.dju.ac.kr/users/tjul/go/gobtsusercheck.aspx"), params);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }

    }

    private void challenge(String id, String pw) {
        HttpConn con = new HttpConn();

        con.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                Handler mainHandler = new Handler(getActivity().getMainLooper());

                HttpConn loginCheckRequest = new HttpConn();

                Map<String, String> headerParams = new HashMap<String, String>();

                String raw_cookie = map.get("Set-Cookie");

                Pattern ptDESID2 = Pattern.compile("(DESID2)=[0-9a-f]{10,}[;]");
                Pattern ptDESKEY2 = Pattern.compile("(DESKEY2)=[0-9a-f]{10,}[;]");
                Matcher mtID = ptDESID2.matcher(raw_cookie);
                Matcher mtKEY = ptDESKEY2.matcher(raw_cookie);

                String cookie = "";

                if(mtID.find() && mtKEY.find() ){
                    cookie += mtID.group(0);
                    cookie += " ";
                    cookie += mtKEY.group(0);
                } else {
//                    _loginFailAlarm();
                    return;
                }
                HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
                cs.setCookie(cookie);

                headerParams.put("Cookie", cookie);
                headerParams.put("Referer", "https://book.dju.ac.kr/ds19_1.html");
                loginCheckRequest.setPrefixHeaderFields(headerParams);
                loginCheckRequest.setCallBackListener(_self);

                mainHandler.post(() -> {
                    try{
                        loginCheckRequest.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds2_3.html"), new HashMap<String,String>());
                    } catch( Exception e) {

                    }

                    return;
                });
            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "https://book.dju.ac.kr/ds19_1.html");

        con.setPrefixHeaderFields(headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "member_login");
        params.put("url", "");
        params.put("login_check","1");
        params.put("user_id", id);
        params.put("user_pass", pw);

        try {
            con.sendRequest(HttpConn.Method.POST, new URL("https://book.dju.ac.kr/duri/member.php"), params);
        } catch(Exception e) {

        }
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        BookServerDataParser parser = new BookServerDataParser(s);

        Element body =parser.getBody();
        Element topNavi = body.getElementsByClass("topnavi").get(0);

        if( topNavi != null){
            if (topNavi.html().contains("로그아웃") == true ){
                mainHandler.post(()->{
                    MainActivity ma = (MainActivity)getActivity();
                    ma.loginComplete();



                });

                return;
            } else {

            }
        } else {

        }
    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
