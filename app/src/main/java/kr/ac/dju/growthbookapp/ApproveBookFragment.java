package kr.ac.dju.growthbookapp;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
public class ApproveBookFragment extends Fragment implements HttpConn.CallbackListener{
    private Map<String,String> headers;
    private Map<String,String> params = new HashMap<String,String>();
    private String url="";
    private int maxPage =0;
    private int nowPage = 2;
    private boolean pass =true;
    private RecyclerView mRecyclerView;
    private ApproveMyadapter mdAdapter;
    private ArrayList<BookListData> bookArrayList = new ArrayList<>();
    private RequestCancelAlertView _requestee;
    private DetailBookListFragment mDetail_self;
    private ApproveBookFragment _self = this;
    private SwipeRefreshLayout _swipeRefreshLayout;

    public void setDetailBookListFragment(DetailBookListFragment dt){
        mDetail_self = dt;
    }
    public interface RequestCancelAlertView {
        public void showCancelAlertView(Runnable confirmCallback);
    }

    public void setRequestCancelAlertView(RequestCancelAlertView requestee){
        _requestee = requestee;
    }

    public ApproveBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_unapproved_book, container, false);
        mdAdapter = new ApproveMyadapter(bookArrayList, this.getActivity());

        _swipeRefreshLayout = (SwipeRefreshLayout) result.findViewById(R.id.unproved_swipeRefreshLayout);
        _swipeRefreshLayout.setRefreshing(true);
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                maxPage = 0;
                nowPage = 2;
                mdAdapter.clear();
                mdAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mdAdapter);

                HttpConn con = new HttpConn();
                HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
                String cookie = cs.getCookie();
                con.setCallBackListener(_self);
                con.setUserAgent("DJUAPP");
                headers = new HashMap<String, String>();
                headers.put("Cookie", cookie);


                con.setPrefixHeaderFields(headers);
                GetUrl();
                params.put("page", "1");
                try {
                    con.sendRequest(HttpConn.Method.GET, new URL(url), params);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR:" + e.toString());
                }
            }
        });


        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);


        con.setPrefixHeaderFields(headers);
        GetUrl();
        params.put("page", "1");
        try {
            con.sendRequest(HttpConn.Method.GET, new URL(url), params);
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
                if ((visibleItemCount + passVisiblesItems) >= totalItemCount) {
                    if (getPassCard() == true)
                        if (nowPage <= maxPage) {
                            params.put("page", String.valueOf(nowPage));
                            onRefresh(nowPage);
                            nowPage++;

                        }

                }
            }

            private boolean getPassCard() {
                return pass;
            }

            private void onRefresh(int page) {
                setPassCard(false);
                mRecyclerView.setAdapter(mdAdapter);
                HttpConn con = new HttpConn();
                HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
                String cookie = cs.getCookie();
                con.setCallBackListener(ApproveBookFragment.this);
                con.setUserAgent("DJUAPP");
                headers = new HashMap<String, String>();
                headers.put("Cookie", cookie);
                con.setPrefixHeaderFields(headers);

                params.put("page", String.valueOf(page));

                try {
                    con.sendRequest(HttpConn.Method.GET, new URL(url), params);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR:" + e.toString());
                }

            }
        });
        mRecyclerView.setAdapter(mdAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return result;
    }
    private void setPassCard(boolean ok) {
        pass = ok;
    }
    public void GetUrl() {
        url = getArguments().getString("url");
        params= (HashMap<String, String>) getArguments().getSerializable("HashMap");

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {
        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements booklists = body.getElementsByClass("menter_ul_contents img_content");
        List<ApproveBookListData> temp = new ArrayList<ApproveBookListData>();
        if(maxPage ==0){
            Elements pages = body.getElementsByTag("a");
            Element page = pages.last();
            String maxPages = page.text();
            if(maxPages.equals("") == false ) {
                maxPages = maxPages.replace("[", "");
                maxPages = maxPages.replace("]", "");
                maxPage = Integer.parseInt(maxPages);
            }
        }

        for(Element booklist : booklists) {
            //책 이름 추출 코드
            Elements lis = booklist.select("li.mentor_li_content");
            Element li = lis.first();
            Elements booknames = li.getElementsByClass("menter_li_div_title");
            String bookname = booknames.first().text();

            //책 이미지 추출 코드
            Element img = booklist.getElementsByTag("img").first();
            String booksrc = img.attr("src");
            if (booksrc.indexOf("http") == -1) {

                booksrc = booksrc.replaceFirst(".", "");
                booksrc = "https://book.dju.ac.kr" + booksrc;
            }

            // 책 저자 추출 코드
            lis = booklist.select("li.mentor_li_content");
            li = lis.first();
            Elements uls = li.getElementsByTag("ul");



            // 책 출판사 및 저자 추출 코드
            Element ul = uls.get(0);
            Elements AuthorAndCompany = ul.select("li.menter_li2_content");
            Element author = AuthorAndCompany.first();
            Element company = AuthorAndCompany.last();
            String bookcompany = company.text();
            String authorin = author.text();


            // 출판일 및 도서분류 추출 코드
            ul = uls.get(1);
            Elements DayAndBooklist = ul.select("li.menter_li2_content");
            Element day = DayAndBooklist.first();
            Element booklog = DayAndBooklist.last();
            String bookday = day.text();
            String bookList = booklog.text();


            // 합격점수 및 인증포인트 코드
            ul = uls.get(3);
            Elements PassAndAuth = ul.select("li.menter_li2_content");
            Element pass = PassAndAuth.first();
            Element auth = PassAndAuth.last();
            String passpoint = pass.text();
            String authpoint = auth.text();

            // 응시일 및 신청일 코드
            ul = uls.get(4);
            Elements DdayAndApprove = ul.select("li.menter_li2_content");
            Element d_day = DdayAndApprove.first();
            Element app_day = DdayAndApprove.last();
            String ddays = d_day.text();
            String appdays = app_day.text();

            // 내용 코드
            ul = uls.get(5);
            Elements contexts = ul.getElementsByTag("li");
            Element context = contexts.get(1);
            String context_book = context.text();

            //취소 값 idx

            Element ecancle_button = contexts.get(2);
            Elements mcancle_button = ecancle_button.getElementsByTag("img");
            Element mcancle_button1 = mcancle_button.first();
            String cancle_button;

            if(mcancle_button1 == null){
                cancle_button = null;
            }
            else{
                cancle_button = mcancle_button1.attr("idx");
            }
            temp.add(new ApproveBookListData(bookname, booksrc, authorin, bookList, bookcompany, bookday, passpoint,
                    authpoint,ddays,appdays,context_book, cancle_button));
        }

        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(()->{

            for ( ApproveBookListData data : temp ){
                bookArrayList.add(data);
                mdAdapter.notifyDataSetChanged();
            }

            if (temp.size() == 0){
                mRecyclerView.setAdapter(new EmptyRecyclerViewAdapter());
            }
            _swipeRefreshLayout.setRefreshing(false);
            setPassCard(true);
        });
        }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }

    public class ApproveBookListData extends BookListData {
        private String _dDay;
        private String _requestDate;
        private String _additionalContent;
        private String _cancleButton;

        public ApproveBookListData(String name, String src, String bookauthor, String list, String company, String day, String pass,
                                   String autho, String dDay, String requestDate, String additionalContent, String cancle) {
            super(name, src, bookauthor, list, company, day, pass, autho);
            _dDay = dDay;
            _requestDate = requestDate;
            _additionalContent = additionalContent;
            _cancleButton = cancle;
        }
        public String get_cancleButton() {
            return _cancleButton;
        }

        public String Get_dDay(){
            return _dDay;

        }

        public String Get_requestDate(){
            return _requestDate;

        }

        public String Get_additionalContent(){
            return _additionalContent;
        }
    }




    public class ApproveMyadapter extends Myadpater implements HttpConn.CallbackListener{

        private  ApproveMyadapter _self = this;
        public ApproveMyadapter(ArrayList<BookListData> bookdata, Context mcontext) {
            super(bookdata, mcontext);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_approve_book,parent,false);
            View container = v.findViewById(R.id.approvebook_item_container);
            ViewHolder holder = new ApproveViewHolder(container);
            return holder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ArrayList<BookListData> arrayList = getBookLIstDatas();

            holder.author2.setText(arrayList.get(position).GetBookAuthor());
            holder.authpass2.setText(arrayList.get(position).GetAuthPoint());
            holder.booklist2.setText(arrayList.get(position).GetBookList());
            holder.company2.setText(arrayList.get(position).GetBookCompany());
            holder.passpoint2.setText(arrayList.get(position).GetPassPoint());
            holder.bookname.setText(arrayList.get(position).GetBookSubject());
            holder.bookdays.setText(arrayList.get(position).GetBookDday());

            ApproveViewHolder exHolder = (ApproveViewHolder) holder;

            exHolder.D_day.setText(((ApproveBookListData) arrayList.get(position)).Get_dDay());
            exHolder.approve_day.setText(((ApproveBookListData) arrayList.get(position)).Get_requestDate());
            exHolder.context.setText(((ApproveBookListData) arrayList.get(position)).Get_additionalContent());
            Picasso.with(getContext())
                    .load(arrayList.get(position).GetBookSrc())
                    .into(holder.bookImg);

            if(((ApproveBookListData) arrayList.get(position)).get_cancleButton() == null){
                exHolder.getButtons().setVisibility(View.GONE);
            }
            else {
                exHolder.getButtons().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _requestee.showCancelAlertView(()->{
                            String idx = ((ApproveBookListData) arrayList.get(position)).get_cancleButton();
                            String cmd = "del";

                            params.put("no",idx);
                            params.put("cmd",cmd);

                            HttpConn con = new HttpConn();
                            HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
                            String cookie = cs.getCookie();
                            con.setCallBackListener(_self);
                            con.setUserAgent("DJUAPP");
                            headers = new HashMap<String, String>();
                            headers.put("Cookie", cookie);
                            con.setPrefixHeaderFields(headers);
                            GetUrl();

                            try {
                                con.sendRequest(HttpConn.Method.GET, new URL(url), params);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("ERROR:" + e.toString());
                            }
                        });



                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return getBookLIstDatas().size();
        }

        @Override
        public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

            Handler mainHandler = new Handler(getActivity().getMainLooper());
            mainHandler.post(() -> {
                         mDetail_self.notifystateData();
                        _swipeRefreshLayout.setRefreshing(false);
            });
        }

        @Override
        public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

        }

        @Override
        public void requestTimeout(HttpConn httpConn) {

        }
    }

    public static class ApproveViewHolder extends Myadpater.ViewHolder{
        View _root;
        TextView D_day, approve_day, context;
        Button cancle_Button;
        public ApproveViewHolder(View view) {
            super(view);
            _root = view;
            D_day = (TextView)view.findViewById(R.id.D_dayInfo);
            approve_day = (TextView)view.findViewById(R.id.Approve_dayInfo);
            context = (TextView)view.findViewById(R.id.Context_Info);


        }
        public Button getButtons () {
            cancle_Button = (Button)_root.findViewById(R.id.cancle_button);
            return cancle_Button;
        }


    }
}
