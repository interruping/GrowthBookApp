package kr.ac.dju.growthbookapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 7..
 */

public class OneByOneReplyFragment extends NavigationBarFragment implements View.OnClickListener, HttpConn.CallbackListener{

    private String _userName;
    private EditText _titleEditText;
    private EditText _contentEditText;
    private String _no;
    private String _page;
    private String _url;
    private Button _submitButton;
    private ProgressBar _progressBar;

    public OneByOneReplyFragment () {
        super(R.layout.fragment_one_by_one_write_question, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result =  super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.one_by_one_write_question), (View v)->{

        },(View view) ->{

        });

        setBackButton((View v)->{
            getFragmentManager().popBackStack();
        });

        hideRightAcc();
        Bundle args = getArguments();

        _titleEditText = (EditText)result.findViewById(R.id.title_editText);
        _contentEditText = (EditText)result.findViewById(R.id.content_editText);
        _submitButton = (Button)result.findViewById(R.id.submit_button);
        _progressBar = (ProgressBar) result.findViewById(R.id.write_progressBar);

        _titleEditText.setText(args.getString("title"));
        _contentEditText.setText(args.getString("content"));

        _titleEditText.setEnabled(false);
        _contentEditText.setEnabled(false);
        _submitButton.setEnabled(false);

        _url = "https://book.dju.ac.kr" + args.getString("url");
        try {

            Map<String,String> urlParams = OneByOneDetailFragment.splitQuery(new URL(_url));
            _no = urlParams.get("no");
            _page = urlParams.get("page");
        } catch (Exception e) {

        }



        _submitButton.setOnClickListener(this);
        getUserInfo();
        return result;

    }


    private void getUserInfo() {
        HttpConn con = new HttpConn();
        con.setUserAgent("GBApp");

        con.setCallBackListener(new HttpConn.CallbackListener() {
            @Override
            public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
                BookServerDataParser parser = new BookServerDataParser(s);
                Element body = parser.getBody();


                String tmpName = "";
                Elements inputs = body.getElementsByTag("input");
                for ( Element input : inputs ){
                    if ( !(input.hasAttr("type") && input.hasAttr("name")) ){
                        continue;
                    }

                    if ( input.attr("type").equals("text") && input.attr("name").equals("name") ) {
                        tmpName = input.attr("value");
                    }
                }
                String tt = tmpName;

                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(() -> {
                    _userName = tt;
                    _titleEditText.setEnabled(true);
                    _contentEditText.setEnabled(true);
                    _submitButton.setEnabled(true);
                    _progressBar.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {


            }

            @Override
            public void requestTimeout(HttpConn httpConn) {

            }
        });

        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Cookie",HttpConn.CookieStorage.sharedStorage().getCookie());
        con.setPrefixHeaderFields(headers);


        try {
            con.sendRequest(HttpConn.Method.GET, new URL(_url), new HashMap<>());


        } catch (Exception e) {

        }


    }

    private static String getBoundary() {

        return "----WebKitFormBoundaryoL7BPt1THtjL1mS1";

    }

    private StringBuffer bindContent() {
        StringBuffer buffer = new StringBuffer();

        String DD = "--";
        String CR = "\r\n";
        String PRE = "Content-Disposition: form-data; name=\"";

        String END = "\"" + CR;

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "c" + END);
        buffer.append(CR);
        buffer.append("_reply" + CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "db" + END);
        buffer.append(CR);
        buffer.append("ds6_3" + CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "no" + END);
        buffer.append(CR);
        buffer.append(_no + CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "page" + END);
        buffer.append(CR);
        buffer.append(_page + CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "repass" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "kind3" + END);
        buffer.append(CR);
        buffer.append(CR);


        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "SK" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "SN" + END);
        buffer.append(CR);
        buffer.append(CR);


        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "tem" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "alfile" + END);
        buffer.append(CR);
        buffer.append(CR);
        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "alfile2" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "idx" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "name" + END);
        buffer.append(CR);
        try{
            buffer.append(new String(_userName.getBytes("x-windows-949"), "iso-8859-1") + CR);

        } catch (Exception e){

        }

        buffer.append(DD + getBoundary() + CR);
        buffer.append(PRE + "subj" + END);
        buffer.append(CR);
        //buffer.append(_titleEditText.getText() + CR);
        try{
            buffer.append(new String(_titleEditText.getText().toString().getBytes("x-windows-949"), "iso-8859-1") + CR);

        } catch (Exception e){

        }

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "ext_url_link" + END);
        buffer.append(CR);
        buffer.append(CR);

        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "upload0\"; filename=\"" + END);
        buffer.append("Content-Type: application/octet-stream" + CR);
        buffer.append(CR);
        buffer.append(CR);


        buffer.append(DD + getBoundary()+ CR);
        buffer.append(PRE + "upload1\"; filename=\"" + END);
        buffer.append("Content-Type: application/octet-stream" + CR);
        buffer.append(CR);
        buffer.append(CR);
//
//
//        buffer.append(DD + getBoundary()+ CR);
//        buffer.append(PRE + "level" + END);
//        buffer.append(CR);
//        buffer.append("1" + CR);
//
//        buffer.append(DD + getBoundary()+ CR);
//        buffer.append(PRE + "code_name" + END);
//        buffer.append(CR);
//        buffer.append(CR);
//
//

        buffer.append(DD + getBoundary() + CR);
        buffer.append(PRE + "tx_content" + END);
        buffer.append(CR);
        //buffer.append(_contentEditText.getText() + CR);
        try{
            buffer.append(new String(_contentEditText.getText().toString().getBytes("x-windows-949"), "iso-8859-1") + CR);

        } catch (Exception e){

        }

        buffer.append(DD + getBoundary()+ DD + CR);

        return buffer;



    }

    @Override
    public void onClick(View v) {
        //submit 버튼이 클릭되었을 때

        HttpConn con = new HttpConn();

        con.setCallBackListener(this);

        con.setUserAgent("GBApp");

        String content = bindContent().toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + getBoundary());
        headers.put("Content-Length", String.valueOf(content.length()));
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        headers.put("Referer", "https://book.dju.ac.kr/ds6_3.html?db=ds6_3&c=reply&no=" + _no + "&page="+ _page + "&kind3=&SK=&SN=&idx=");
        con.setPrefixHeaderFields(headers);

        try{
            con.sendPOSTRequest(new URL("https://book.dju.ac.kr/ds6_3.html?db=ds6_3&c=reply&no=" + _no + "&page= " + _page + "&kind3=&SK=&SN=&idx="), content);
        }catch (Exception e){

        }


    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{
            showAlertView(AlertType.INFO, "답글이 작성되었습니다", "게시판으로 돌아갑니다", "확인", new AlertViewConfirmListener() {
                @Override
                public void alertViewConfirmed(AlertType type, String title, String description) {
                    Intent intent = new Intent();
                    intent.putExtra(OneByOneDetailFragment.RETURN2ROOT, OneByOneDetailFragment.RETURN2ROOT);
                    Fragment fragment = getTargetFragment();
                    fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });

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
}
