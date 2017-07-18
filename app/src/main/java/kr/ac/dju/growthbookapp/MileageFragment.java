package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geonyounglim on 2017. 7. 2..
 */

public class MileageFragment extends NavigationBarFragment implements HttpConn.CallbackListener {
    private TextView _bookPointLevelTextView;
    private TextView _mileagePointTextView;
    private TextView _bookBorrowPointTextView;
    private TextView _bookAuthPointTextView;
    private TextView _bookSeatPointTextView;
    private TextView _bookEventPointTextView;
    private ImageView _levelImageView;
    public MileageFragment (){
        super(R.layout.fragment_mileage, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar("마일리지", (View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        },(View view) ->{

        });

        ListView descList = (ListView)result.findViewById(R.id.book_level_desc_listview);

        LevelDescriptionListAdapter adapter = new LevelDescriptionListAdapter();
        descList.setAdapter(adapter);


        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.diamond_rank), "다이아몬드 (1위 ~ 30위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.titanium_rank), "티타늄 (31위 ~ 100위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.gold_rank), "골드 (101위 ~ 200위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.silver_rank), "실버 (201위 ~ 500위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.bronze_rank), "브론즈 (501위 ~ 1000위)");
        _bookPointLevelTextView = (TextView)result.findViewById(R.id.book_point_level_textview);
        _mileagePointTextView = (TextView)result.findViewById(R.id.milage_point_textview);
        _bookBorrowPointTextView = (TextView)result.findViewById(R.id.book_borrow_point_textview);
        _bookAuthPointTextView = (TextView)result.findViewById(R.id.book_auth_point_textview);
        _bookSeatPointTextView = (TextView)result.findViewById(R.id.book_seat_point_textview);
        _bookEventPointTextView = (TextView)result.findViewById(R.id.book_event_point_textview);
        _levelImageView = (ImageView)result.findViewById(R.id.book_level_image);


        HttpConn conn = new HttpConn();
        conn.setUserAgent("GBApp");

        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Cookie", HttpConn.CookieStorage.sharedStorage().getCookie());


        conn.setPrefixHeaderFields(headers);
        conn.setCallBackListener(this);
        try {
            conn.sendRequest(HttpConn.Method.GET, new URL("https://book.dju.ac.kr/ds2_3.html"), new HashMap<String, String>());
        } catch (Exception e) {

        }

        return result;
    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);

        Element body = parser.getBody();

        Element milageDiv = body.getElementById("mileage");
        Element milageDivR = body.getElementById("mileage_r");
        String milagePoint = milageDiv.getElementsByClass("mtext").get(0).text();
        Elements mtexts =  milageDivR.getElementsByClass("mtext");

        String rankSrc = null;
        if ( mtexts != null && mtexts.size() != 0 ) {
            Element img = milageDivR.getElementsByClass("mtext").get(0).child(0);
            String tmp = img.attr("src");
            rankSrc = tmp;
        }else {
            String tmp = "";
            rankSrc = tmp;
        }





        Element pointContainer = milageDiv.getElementsByClass("mtest_content").get(0);
        String borrowPoint = pointContainer.getElementsByTag("li").get(0).text();
        String authPoint = pointContainer.getElementsByTag("li").get(1).text();
        String seatPoint = pointContainer.getElementsByTag("li").get(2).text();
        String eventPoint = pointContainer.getElementsByTag("li").get(3).text();

        String rankStr = rankSrc;
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{

            if (rankStr.equals("ds_imgs/mileage/5.png")) {
                _levelImageView.setImageResource(R.drawable.diamond_rank);
                _bookPointLevelTextView.setText("현재 회원님은 다이아몬드입니다.");
            } else if (rankStr.equals("ds_imgs/mileage/4.png")) {
                _levelImageView.setImageResource(R.drawable.titanium_rank);
                _bookPointLevelTextView.setText("현재 회원님은 티타늄입니다.");
            } else if (rankStr.equals("ds_imgs/mileage/3.png")) {
                _levelImageView.setImageResource(R.drawable.gold_rank);
                _bookPointLevelTextView.setText("현재 회원님은 골드입니다.");
            } else if (rankStr.equals("ds_imgs/mileage/2.png")) {
                _levelImageView.setImageResource(R.drawable.silver_rank);
                _bookPointLevelTextView.setText("현재 회원님은 실버입니다.");
            } else if (rankStr.equals("ds_imgs/mileage/1.png")) {
                _levelImageView.setImageResource(R.drawable.bronze_rank);
                _bookPointLevelTextView.setText("현재 회원님은 브론즈입니다.");
            } else {
                _levelImageView.setVisibility(View.INVISIBLE);
                _bookPointLevelTextView.setText("현재 회원님은 순위권 밖입니다.");
            }
            _mileagePointTextView.setText("마일리지 점수 : " + milagePoint);
            _bookBorrowPointTextView.setText("도서대출 포인트 : " + borrowPoint);
            _bookAuthPointTextView.setText("도서인증 포인트 : " + authPoint);
            _bookSeatPointTextView.setText("좌석 포인트 : " + seatPoint);
            _bookEventPointTextView.setText("이벤트 포인트 : " + eventPoint);
        });

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
