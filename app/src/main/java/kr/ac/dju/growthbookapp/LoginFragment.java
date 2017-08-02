package kr.ac.dju.growthbookapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.dju.book.*;

import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geonyounglim on 2017. 5. 30..
 */

public class LoginFragment extends NavigationBarFragment implements HttpConn.CallbackListener, CompoundButton.OnCheckedChangeListener {

    private View _rootView;
    private Button _loginBtn;
    private EditText _idInput;
    private EditText _pwInput;
    private boolean _loginRequested;
    private Switch _autoLoginSwitch;
    private String _encryptKey;
    private LoginFragment _self = this;
    public LoginFragment() {
        super(R.layout.fragment_login, R.id.root_constraint);
        _loginRequested = false;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);

        _loginBtn = (Button)_rootView.findViewById(R.id.login_button);

        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                MainActivity ma = (MainActivity)_self.getActivity();
                if ( ma.isMenuToggled() == true ){
                    ma.toggleMenu();
                    return;
                }

                challengeAuthPre();
            }
        });



        _idInput =(EditText) _rootView.findViewById(R.id.id_input);
        _pwInput =(EditText) _rootView.findViewById(R.id.pw_input);
        _autoLoginSwitch = (Switch) _rootView.findViewById(R.id.auto_login);
        _autoLoginSwitch.setOnCheckedChangeListener(this);
        _loginBtn.setEnabled(false);
        UserInfoSafeStorage safe = new UserInfoSafeStorage(getActivity());
        if (safe.isSafeUsed() == true) {
            _autoLoginSwitch.setOnCheckedChangeListener(this);
            //_autoLoginSwitch.setOnCheckedChangeListener(null);
            _autoLoginSwitch.setChecked(true);
            _autoLoginSwitch.setText("자동 로그인 켜짐");

        } else if ( safe.isSafeUsed() == false ){
            _autoLoginSwitch.setChecked(false);
            _autoLoginSwitch.setText("자동 로그인 꺼짐");
        }


        _idInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity)_self.getActivity();
                if (ma.isMenuToggled() == true ){
                    ma.toggleMenu();
                }
            }
        });

        _pwInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity)_self.getActivity();
                if (ma.isMenuToggled() == true ){
                    ma.toggleMenu();
                }
            }
        });
        _idInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MainActivity ma = (MainActivity)_self.getActivity();
                if (ma.isMenuToggled() == true ){
                    ma.toggleMenu();
                }
            }
        });

        _pwInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MainActivity ma = (MainActivity)_self.getActivity();
                if (ma.isMenuToggled() == true ){
                    ma.toggleMenu();
                }
            }
        });


        _idInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int pwLen = _pwInput.getText().toString().length();

                if ( pwLen == 0 ){
                    _loginBtn.setEnabled(false);
                    return;
                }
                if ( start == 0 && count==0 && after==1 ){
                    _loginBtn.setEnabled(true);
                } else if ( start == 0 && count==1 && after==0) {
                    _loginBtn.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        _pwInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                int idLen = _idInput.getText().toString().length();

                if ( idLen == 0 ){
                    _loginBtn.setEnabled(false);
                    return;
                }

                if ( start == 0 && count==0 && after==1 ){

                    _loginBtn.setEnabled(true);
                } else if ( start == 0 && count==1 && after==0) {
                    _loginBtn.setEnabled(false);
                }


                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        setWithCommonNavigationBar(getResources().getString(R.string.login),(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();

            ma.toggleMenu();
        }, (View v)->{

        });


        hideRightAcc();

        MainActivity ma = (MainActivity)this.getActivity();


        if ( ma.isNeedAlertLogin() == true ){
            showAlertView(AlertType.WARNING, "로그인이 필요합니다.", "초기 비밀번호 주민번호 뒷자리", "확인", null);
        }

        if ( ma.isFailAutoLogin() == true ) {
            _autoLoginSwitch.setChecked(false);
            showAlertView(AlertType.UNKNOWNERROR, "자동 로그인 실패.", "파일이 손상되었습니다. 수동으로 로그인 해 주세요", "확인", null);
        }

        return _rootView;
    }
    public void challengeAuthPre() {
        //https://libweb.dju.ac.kr/users/tjul/go/gobtsusercheck.aspx
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
                                challengeAuth();
                            } else if ( isOk.equals("0") ) {
                                showAlertView(AlertType.INFO, "로그인 실패", "아이디 혹은 비밀번호를 확인하세요.", "확인", null);
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
        params.put("txtID", _idInput.getText().toString());
        params.put("txtPW", _pwInput.getText().toString());

        try {
            con.sendRequest(HttpConn.Method.GET, new URL("https://libweb.dju.ac.kr/users/tjul/go/gobtsusercheck.aspx"), params);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }

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

        }

    }

    private void _loginFailAlarm() {
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{
            showAlertView(AlertType.INFO, "로그인 실패", "웹 서버 에러 관리자에게 문의하세요.", "확인", null);
            _loginRequested = false;

        });

    }

    private void _autoLoginCheck() {
        if ( _autoLoginSwitch.isChecked() == true){
            UserInfoSafeStorage safe = new UserInfoSafeStorage(getActivity());
            UserInfoSafeStorage.UserInfo user = new UserInfoSafeStorage.UserInfo();
            user.id = _idInput.getText().toString();
            user.pw = _pwInput.getText().toString();
            safe.setKey(_encryptKey);
            safe.put(user);

        }
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
                        _autoLoginCheck();
                        StudentIDHolder.getInstance().storeID(_idInput.getText().toString());
                        MainActivity ma = (MainActivity)getActivity();
                        //ma.loginComplete();
                        ma.returnPrevLogin();


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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if ( isChecked == true ){
            _autoLoginSwitch.setText("암호화 키 다운로드 중...");
            _loginBtn.setEnabled(false);
            _idInput.setEnabled(false);
            _pwInput.setEnabled(false);

            HttpConn conn = new HttpConn();

            conn.setUserAgent("GBApp");
            conn.setCallBackListener(new HttpConn.CallbackListener() {
                @Override
                public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                    try{
                        JSONObject result = new JSONObject(s);
                        if ( result.get("result").toString().equals("0") == true ) {
                            _encryptKey = result.get("skey").toString();
                        } else {

                        }
                    } catch (Exception e) {

                    }


                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(()->{

                        _autoLoginSwitch.setText("자동 로그인 켜짐");
                        _idInput.setEnabled(true);
                        _pwInput.setEnabled(true);
                    });

                }

                @Override
                public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {


                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(()->{
                        showAlertView(AlertType.ERROR, "자동 로그인을 사용할 수 없습니다", "암호화키 다운로드를 실패했습니다", "확인", null);
                        _autoLoginSwitch.setChecked(false);
                        _autoLoginSwitch.setText("자동 로그인 꺼짐");
                        _idInput.setEnabled(true);
                        _pwInput.setEnabled(true);
                    });

                }

                @Override
                public void requestTimeout(HttpConn httpConn) {
                    Handler mainHandler = new Handler(getActivity().getMainLooper());
                    mainHandler.post(()->{
                        showAlertView(AlertType.ERROR, "자동 로그인을 사용할 수 없습니다", "암호화키 다운로드를 실패했습니다", "확인", null);
                        _autoLoginSwitch.setChecked(false);
                        _autoLoginSwitch.setText("자동 로그인 꺼짐");
                        _idInput.setEnabled(true);
                        _pwInput.setEnabled(true);
                    });
                }
            });
            try {
                String uuid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                JSONObject json = new JSONObject();
                json.put("device_id", uuid);
                Map<String,String> header = new HashMap<String,String>();
                header.put("Content-type", "application/json");
                header.put("Content-Length", String.valueOf(json.toString().length()));
                header.put("Cookie", TimeCookieGenarator.OneTimeInstance().gen(String.valueOf(json.toString().length())));

                conn.setPrefixHeaderFields(header);
                conn.sendPOSTRequest(new URL("https://growthbookapp-api.net:9000/adduser"), json.toString());
            } catch ( Exception e ) {
                e.printStackTrace();
            }

        } else if ( isChecked == false ) {
            UserInfoSafeStorage safe = new UserInfoSafeStorage(getActivity());
            safe.delete();
            _autoLoginSwitch.setText("자동 로그인 꺼짐");

            _idInput.setEnabled(true);
            _pwInput.setEnabled(true);
        }

    }
}