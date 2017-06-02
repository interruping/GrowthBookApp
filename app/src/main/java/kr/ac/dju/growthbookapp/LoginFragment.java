package kr.ac.dju.growthbookapp;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.dju.book.*;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geonyounglim on 2017. 5. 30..
 */

public class LoginFragment extends NavigationBarFragment implements HttpConn.CallbackListener {

    private View _rootView;
    private Button _loginBtn;
    private EditText _idInput;
    private EditText _pwInput;
    private boolean _loginRequested;

    public LoginFragment() {
        super(R.layout.fragment_login, R.id.root_constraint);
        _loginRequested = false;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);

        _loginBtn = (Button)_rootView.findViewById(R.id.login_button);
        //_loginBtn.setEnabled(false);
        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                challengeAuth();
            }
        });

        _idInput =(EditText) _rootView.findViewById(R.id.id_input);
        _pwInput =(EditText) _rootView.findViewById(R.id.pw_input);

//        _idInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                int pwLen = _pwInput.getText().toString().length();
//
//                if ( pwLen == 0 ){
//                    _loginBtn.setEnabled(false);
//                    return;
//                }
//                if ( start == 0 && count==0 && after==1 ){
//                    _loginBtn.setEnabled(true);
//                } else if ( start == 0 && count==1 && after==0) {
//                    _loginBtn.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        _pwInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                int idLen = _idInput.getText().toString().length();
//
//                if ( idLen == 0 ){
//                    _loginBtn.setEnabled(false);
//                    return;
//                }
//
//                if ( start == 0 && count==0 && after==1 ){
//
//                    _loginBtn.setEnabled(true);
//                } else if ( start == 0 && count==1 && after==0) {
//                    _loginBtn.setEnabled(false);
//                }
//
//
//                return;
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

        setWithCommonNavigationBar(getResources().getString(R.string.login),(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{

        });



        return _rootView;
    }

    public void challengeAuth() {
        HttpConn con = new HttpConn();

        con.setCallBackListener(this);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "https://book.dju.ac.kr/ds19_1.html");

        con.setPrefixHeaderFields(headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("c", "member_login");
        params.put("url", "");
        params.put("login_check","1");
        params.put("user_id", _idInput.getText().toString());
        params.put("user_pass", _pwInput.getText().toString());

        try {
            con.sendRequest(HttpConn.Method.POST, new URL("https://book.dju.ac.kr/duri/member.php"), params);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }

    }

    private void _loginFailAlarm() {
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "로그인 실패.", Toast.LENGTH_SHORT);
            toast.show();
            _loginRequested = false;

        });

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        Handler mainHandler = new Handler(getActivity().getMainLooper());




        if ( _loginRequested == true ){

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
                    _loginFailAlarm();
                }
            } else {
                _loginFailAlarm();
            }
            return;
        }



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
            _loginFailAlarm();
            return;
        }
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        cs.setCookie(cookie);

        headerParams.put("Cookie", cookie);
        headerParams.put("Referer", "https://book.dju.ac.kr/ds19_1.html");
        loginCheckRequest.setPrefixHeaderFields(headerParams);
        loginCheckRequest.setCallBackListener(this);
        _loginRequested = true;



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
}