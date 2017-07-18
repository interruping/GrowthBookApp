package kr.ac.dju.growthbookapp;

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

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 7..
 */

public class OneByOneModifyFragment extends NavigationBarFragment implements View.OnClickListener, HttpConn.CallbackListener{

    private String _userName;
    private EditText _titleEditText;
    private EditText _contentEditText;
    private String _no;
    private String _page;
    private String _url;



    public OneByOneModifyFragment () {
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

        Bundle args = getArguments();

        _titleEditText = (EditText)result.findViewById(R.id.title_editText);
        _contentEditText = (EditText)result.findViewById(R.id.content_editText);

        _titleEditText.setText(args.getString("title"));
        _contentEditText.setText(args.getString("content"));
        _url = "https://book.dju.ac.kr" + args.getString("url");
        try {

            Map<String,String> urlParams = OneByOneDetailFragment.splitQuery(new URL(_url));
            _no = urlParams.get("no");
            _page = urlParams.get("page");
        } catch (Exception e) {

        }



        Button submitButton = (Button)result.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
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
                Element inputId = body.getElementById("input_writer");
                String name = inputId.attr("value");


                Handler mainHandler = new Handler(getActivity().getMainLooper());
                mainHandler.post(() -> {
                    _userName = name;

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
        buffer.append("_modify" + CR);

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

        //https://book.dju.ac.kr/ds6_3.html?db=ds6_3&c=modify&no=276&page=1&kind3=&SK=&SN=&idx=

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + getBoundary());
        headers.put("Content-Length", String.valueOf(content.length()));
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());
        headers.put("Referer", "https://book.dju.ac.kr/ds6_3.html?db=ds6_3&c=modify&no=" + _no + "&page="+ _page + "&kind3=&SK=&SN=&idx=");
        con.setPrefixHeaderFields(headers);

        try{
            con.sendPOSTRequest(new URL("https://book.dju.ac.kr/ds6_3.html?db=ds6_3&c=modify&no=" + _no + "&page= " + _page + "&kind3=&SK=&SN=&idx="), content);
        }catch (Exception e){

        }


    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
