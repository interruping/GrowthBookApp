package kr.ac.dju.growthbookapp;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApproveBookAuthTestSubmitDetail extends NavigationBarFragment implements HttpConn.CallbackListener {
    private Map<String, String> headers;
    private ArrayList<BookAuthtestSubmitData> apply_Data = new ArrayList<>();
    private Map<String, String> apply_attr = new HashMap<>();
    private String mUrl;
    public BookAuthTestSubmitAdapter adapter;
    private String apply_value;
    private ApplyCustomDialog mdialog;
    private DetailBookListFragment mDetail_self;

    private boolean config;
    public ApproveBookAuthTestSubmitDetail() {
        // Required empty public constructor
        super(R.layout.fragment_approve_book_button, R.id.root_constraint);

    }
    public void setmUnapprove_self(DetailBookListFragment un){
        mDetail_self = un;
    }

    public void setDialog(ApplyCustomDialog dialog) {
        mdialog = dialog;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        if(mdialog == null){
            boolean temp;
            switch (newConfig.orientation) {
                case Configuration.ORIENTATION_LANDSCAPE: {
                    temp = false;
                    config = temp;
                    break;
                }
                case Configuration.ORIENTATION_PORTRAIT:
                   temp = true;
                    config = temp;
                    break;

            }

        }
            if(mdialog != null) {
                switch (newConfig.orientation) {
                    case Configuration.ORIENTATION_LANDSCAPE: {
                        mdialog.setContentView(R.layout.book_auth_landscape);
                        mdialog.basicSetting();
                        break;
                    }
                    case Configuration.ORIENTATION_PORTRAIT:
                        mdialog.setContentView(R.layout.book_auth);
                        mdialog.basicSetting();
                        break;

                }

            }
        }
        public boolean getconfig(){
            return config;
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        String name = args.getString("title");
        name = name.replace("-", "");
        setWithCommonNavigationBar(name, (View v) -> {

            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {

        });

        setBackButton((View v) -> {

            getFragmentManager().popBackStack();
        });

        hideRightAcc();

        apply_value = getArguments().getString("value");
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        mUrl = "https://book.dju.ac.kr/ds_pages/ajax_book.php";

        ListView listView;

        adapter = new BookAuthTestSubmitAdapter(apply_Data);
        adapter.setBookAuthTestSubmitDetail(this);

        apply_attr = (HashMap<String, String>) getArguments().getSerializable("HashMap");
        try {
            con.sendRequest(HttpConn.Method.POST, new URL(mUrl), apply_attr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView = (ListView) result.findViewById(R.id.Approve_List_Info);
        listView.setAdapter(adapter);

        // 화면모드가 가로인지 세로인지를 변수에 저장
        config = getConfiguration();

        return result;
    }

    // 화면모드가 가로인지 세로인지를 알려주는 함수
    public boolean getConfiguration(){
        Configuration config = getResources().getConfiguration();
        boolean temp;
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            temp = false;
            return temp;
        }
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
            temp = true;
            return temp;
        }
        return true;
    }

    // 인증 시험 신청 제출시 다시 DetailFragment로 전환
    public void unprovedBackButton() {
        mDetail_self.setmBackState(true);
        getFragmentManager().popBackStack();
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        List<BookAuthtestSubmitData> temp = new ArrayList<>();
        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements tbodys = body.getElementsByTag("tbody");
        Element tbody = tbodys.first();
        Elements trs = tbody.getElementsByClass("table_tr");
        for (Element tr : trs) {
            Elements td = tr.getElementsByTag("td");

            Element ed_day = td.get(1);
            String d_day = ed_day.text();

            Element etime = td.get(3);
            String time = etime.text();

            Element eweekend = td.get(5);
            String weekend = eweekend.text();

            Element elimt_time = td.get(7);
            String limt_time = elimt_time.text();

            Element econtent = td.get(9);
            String content = econtent.text();

            Element eadmite_limt = td.get(11);
            String admite_limt = eadmite_limt.text();


            Element apply_Buttons = td.get(13);
            String submit_com = apply_Buttons.text();
            if (submit_com.equals("신청완료") == false) {

                String apply_day = apply_Buttons.attr("day");
                String apply_stime1 = apply_Buttons.attr("stime1");
                String apply_stime2 = apply_Buttons.attr("stime2");
                String apply_idx = apply_Buttons.attr("idx");
                String apply_title = apply_Buttons.attr("title");
                String apply_setting_no = apply_Buttons.attr("setting_no");
                temp.add(new BookAuthtestSubmitData(d_day, time, weekend, limt_time, content, admite_limt, null, apply_day, apply_stime1,
                        apply_stime2, apply_value, apply_idx, apply_title, apply_setting_no));
            } else {
                temp.add(new BookAuthtestSubmitData(d_day, time, weekend, limt_time, content, admite_limt,
                        submit_com, null, null, null, null, null, null, null));
            }
        }
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(() -> {

            for (BookAuthtestSubmitData data : temp) {
                apply_Data.add(data);
                adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
