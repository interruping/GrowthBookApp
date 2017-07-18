package kr.ac.dju.growthbookapp;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dju.book.BookServerDataParser;
import com.dju.book.HttpConn;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApprovedBookFragment extends Fragment implements HttpConn.CallbackListener{
    private Map<String,String> headers;
    private Map<String,String> paramss = new HashMap<String,String>();
    private String url="";
    private int maxPage =0;
    private int nowPage = 2;
    private boolean pass =true;
    private RecyclerView mRecyclerView;
    private Myadpater mAdapter;
    private ArrayList<BookListData> bookArrayList = new ArrayList<BookListData>();

    public ApprovedBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_unapproved_book, container, false);



        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        GetUrl();
        paramss.put("page","1");
        try {
            con.sendRequest(HttpConn.Method.GET, new URL(url), paramss);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }

        mRecyclerView = (RecyclerView) result.findViewById(R.id.unproved_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int passVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                if((visibleItemCount + passVisiblesItems) >=totalItemCount){
                    if(getPassCard() == true)
                        if(nowPage <=maxPage) {
                            paramss.put("page", String.valueOf(nowPage));
                            onRefresh(nowPage);
                            nowPage++;

                        }

                }
            }

            private boolean getPassCard() {
                return pass;
            }

            private void onRefresh(int page)  {
                setPassCard(false);
                HttpConn con = new HttpConn();
                HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
                String cookie = cs.getCookie();
                con.setCallBackListener(ApprovedBookFragment.this);
                con.setUserAgent("DJUAPP");
                headers = new HashMap<String, String>();
                headers.put("Cookie", cookie);
                con.setPrefixHeaderFields(headers);

                paramss.put("page", String.valueOf(page));

                try {
                    con.sendRequest(HttpConn.Method.GET, new URL(url), paramss);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR:" + e.toString());
                }

            }
        });
        mAdapter = new Myadpater(bookArrayList, this.getActivity());
        mAdapter.setmButton("star");
        mAdapter.setdevice(getArguments().getString("device"));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return result;
    }
    private void setPassCard(boolean ok) {
        pass = ok;
    }

    public void GetUrl() {
        url = getArguments().getString("url");
        paramss = (HashMap<String, String>)getArguments().getSerializable("HashMap");

    }


    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements booklists = body.getElementsByClass("menter_ul_contents img_content");
        List<BookListData> temp = new ArrayList<BookListData>();
        if(maxPage ==0){
            Elements pages = body.getElementsByTag("a");
            Element page = pages.last();
            String maxPages = page.text();
            if(maxPages.equals("") ==false) {
                maxPages = maxPages.replace("[", "");
                maxPages = maxPages.replace("]", "");
                maxPage = Integer.parseInt(maxPages);
            }
        }

        for(Element booklist : booklists)
        {
            //책 이름 추출 코드
            Elements lis = booklist.select("li.mentor_li_content");
            Element li = lis.first();
            Elements booknames = li.getElementsByClass("menter_li_div_title");
            String bookname = booknames.first().text();

            //책 이미지 추출 코드
            Element img = booklist.getElementsByTag("img").first();
            String booksrc = img.attr("src");
            if(booksrc.indexOf("http") == -1){

                booksrc = booksrc.replaceFirst(".", "");
                booksrc = "https://book.dju.ac.kr" + booksrc;
            }

            // 책 저자 추출 코드
            lis = booklist.select("li.mentor_li_content");
            li = lis.first();
            Elements uls = li.getElementsByTag("ul");
            Element ul = uls.first();
            Elements authors = ul.select("li.menter_li2_content");
            Element author= authors.first();
            String authorin = author.text();

            // 책 출판사 및 출판일 추출 코드
            ul= uls.get(1);
            Elements companyAndDay = ul.select("li.menter_li2_content");
            Element company = companyAndDay.first();
            Element day = companyAndDay.last();
            String bookcompany = company.text();
            String bookday = day.text();

            // 책 합격점수 및 인증포인트 추출 코드
            ul= uls.get(3);
            Elements passAndauth = ul.select("li.menter_li2_content");
            Element pass = passAndauth.first();
            Element auth = passAndauth.last();
            String passpoint = pass.text();
            String authpoint = auth.text();

            // 책 도서 분류 추출 코드
            ul= uls.get(2);
            Elements booklogs = ul.select("li.menter_li2_content");
            Element booklog = booklogs.first();
            String bookList = booklog.text();


            temp.add(new BookListData(bookname, booksrc,authorin,bookList,bookcompany, bookday,passpoint,authpoint));
        }

        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{

            for ( BookListData data : temp ){
                bookArrayList.add(data);
                mAdapter.notifyDataSetChanged();
            }
            setPassCard(true);
        });

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }
}
