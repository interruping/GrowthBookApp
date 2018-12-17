package kr.ac.dju.growthbookapp;



import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.dju.book.HttpConn;
import com.dju.book.BookServerDataParser;


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
public class UnapprovedBookFragment extends Fragment implements HttpConn.CallbackListener, Myadpater.ApplyButtonClickListner, NavigationBarFragment.SearchBarEventListener {
    private Map<String, String> headers;

    //    private Map<String,String> param = new HashMap<~>();
    private String url = "";
    private String _litem = "";
    private int maxPage = 0;
    private int nowPage = 2;
    private boolean pass = true;
    private RecyclerView mRecyclerView;
    private Myadpater mAdapter;
    private Myadpater _searchAdapter;

    private int _backupMaxPage = 0;
    private int _backupCurrentPage = 2;

    private RecyclerView.OnScrollListener _scrollListener;

    private ArrayList<BookListData> bookArrayList = new ArrayList<BookListData>();

    private DetailBookListFragment _parent;

    private SwipeRefreshLayout _swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener _refreshListener;

    private UnapprovedBookFragment _self = this;

    private Myadpater _currentTargetAdapter = null;

    public UnapprovedBookFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_unapproved_book, container, false);

        _swipeRefreshLayout = (SwipeRefreshLayout)result.findViewById(R.id.unproved_swipeRefreshLayout);
        _swipeRefreshLayout.setRefreshing(true);

        _refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                maxPage = 0;
                nowPage = 2;
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                _self.onRefresh(1);
            }
        };

        _swipeRefreshLayout.setOnRefreshListener(_refreshListener);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.colorHighLight), ContextCompat.getColor(getActivity(),R.color.colorStrongHighLight));
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        GetUrl();
        Map<String, String> paramss = new HashMap<String, String>();
        paramss.put("page", "1");
        paramss.put("litem", _litem);
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

        _scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int passVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + passVisiblesItems) >= totalItemCount) {
                    if (getPassCard() == true)
                        if (nowPage <= maxPage) {

                            paramss.put("page", String.valueOf(nowPage));
                            onRefresh(nowPage);
                            nowPage++;

                        }

                }
            }

            private boolean getPassCard() {
                return pass;
            }


        };

        mRecyclerView.addOnScrollListener(_scrollListener);
        mAdapter = new Myadpater(bookArrayList, this.getActivity());
        _currentTargetAdapter = mAdapter;

        mAdapter.setOnClickListener(this);
        mAdapter.settingForUnapproved();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return result;
    }
    public void onRefresh(int page) {
        setPassCard(false);
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        Map<String, String> paramss = new HashMap<String, String>();
        paramss.put("page", String.valueOf(page));
        paramss.put("litem", _litem);

        try {
            con.sendRequest(HttpConn.Method.GET, new URL(url), paramss);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }
    }
    // DetailBookListFragment  자신 가져오기
    public void setParentDetailFragment(DetailBookListFragment parent) {

        _parent = parent;
    }

    private void setPassCard(boolean ok) {
        pass = ok;
    }

    public void GetUrl() {
        url = getArguments().getString("url");
        HashMap<String,String> tmp =  (HashMap<String, String>) getArguments().getSerializable("HashMap");
        _litem = tmp.get("litem");

    }

    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map, String s) {

        BookServerDataParser parser = new BookServerDataParser(s);
        Element body = parser.getBody();
        Elements booklists = body.getElementsByClass("menter_ul_contents img_content");
        List<BookListData> temp = new ArrayList<BookListData>();
        if (maxPage == 0) {
            Elements pages = body.getElementsByTag("a");
            Element page = pages.last();
            String maxPages = page.text();
            maxPages = maxPages.replace("[", "");
            maxPages = maxPages.replace("]", "");
            try {
                maxPage = Integer.parseInt(maxPages);
            } catch (Exception e) {

            }

        }

        for (Element booklist : booklists) {
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
            Element ul = uls.first();
            Elements authors = ul.select("li.menter_li2_content");
            Element author = authors.first();
            String authorin = author.text();

            // 책 출판사 및 출판일 추출 코드
            ul = uls.get(1);
            Elements companyAndDay = ul.select("li.menter_li2_content");
            Element company = companyAndDay.first();
            Element day = companyAndDay.last();
            String bookcompany = company.text();
            String bookday = day.text();

            // 책 합격점수 및 인증포인트 추출 코드
            ul = uls.get(3);
            Elements passAndauth = ul.select("li.menter_li2_content");
            Element pass = passAndauth.first();
            Element auth = passAndauth.last();
            String passpoint = pass.text();
            String authpoint = auth.text();

            // 책 도서 분류 추출 코드
            ul = uls.get(2);
            Elements booklogs = ul.select("li.menter_li2_content");
            Element booklog = booklogs.first();
            String bookList = booklog.text();

            // apply button 코드
            lis = booklist.select("li.mentor_li_content");
            li = lis.first();
            Elements applys = li.getElementsByClass("menter_li_div_title");
            Element apply = applys.first();
            Elements buttons = apply.getElementsByTag("img");
            Element button = buttons.first();
            String idx = button.attr("idx");
            String question = button.attr("question");
            String dnum = button.attr("dnum");
            Elements values = body.getElementsByClass("window2");
            Element value = values.first();
            Element val = value.nextElementSibling();
            String va = val.attr("value");

            BookListData data = new BookListData(bookname, booksrc, authorin, bookList, bookcompany, bookday, passpoint, authpoint);
            data.setApplyAttr(idx, question, dnum, va);

            temp.add(data);
        }

        Handler mainHandler = new Handler(getActivity().getMainLooper());
        mainHandler.post(() -> {
            _currentTargetAdapter.removeLoadingDummy();
            if ( _currentTargetAdapter == _searchAdapter ){
                _currentTargetAdapter.clear();
                _currentTargetAdapter.notifyDataSetChanged();
            }

            for (BookListData data : temp) {
                _currentTargetAdapter.addItem(data);
                _currentTargetAdapter.notifyDataSetChanged();
            }
            if (maxPage >= nowPage) {
                _currentTargetAdapter.addLoadingDummy();
                mRecyclerView.scrollToPosition(_currentTargetAdapter.getItemCount() + 1);
            }

            _swipeRefreshLayout.setRefreshing(false);
            _currentTargetAdapter.notifyDataSetChanged();
            setPassCard(true);


        });
    }


    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }

    @Override
    public void applyButtonOnClicked(int position) {
        String dnum = _currentTargetAdapter.getItem(position).GetDnum();
        if (Integer.parseInt(dnum) < 20) {
            _parent.showAlertView(NavigationBarFragment.AlertType.INFO, "신청 할 수 없습니다.", "아직 출제가 되지 않았습니다.", "확인", null);
            return;
        }

        String title = _currentTargetAdapter.getItem(position).GetBookSubject();
        String idx = _currentTargetAdapter.getItem(position).GetIdx();
        String question = _currentTargetAdapter.getItem(position).GetQuestion();
        String value = _currentTargetAdapter.getItem(position).Getvalue();

        _parent.transToTestSubmitDetail(title, idx, value, question);
    }

    @Override
    public void searchBarWillActive() {
        _searchAdapter = new Myadpater(new ArrayList<>(), getActivity());
        _currentTargetAdapter = _searchAdapter;
        _searchAdapter.settingForUnapproved();
        _searchAdapter.setOnClickListener(this);
        _swipeRefreshLayout.setOnRefreshListener(null);
        _swipeRefreshLayout.setEnabled(false);
        mRecyclerView.removeOnScrollListener(_scrollListener);
        mRecyclerView.swapAdapter(_searchAdapter, true);
        _backupCurrentPage = nowPage;
        _backupMaxPage = maxPage;
        nowPage = 2;
        maxPage = 0;
    }

    @Override
    public void searchBarWillInactive() {
        nowPage = _backupCurrentPage;
        maxPage = _backupMaxPage;
        mRecyclerView.swapAdapter(mAdapter, true);
        _currentTargetAdapter = mAdapter;
        _swipeRefreshLayout.setOnRefreshListener(_refreshListener);
        _swipeRefreshLayout.setEnabled(true);
        mRecyclerView.addOnScrollListener(_scrollListener);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void searchBarKeyInput(String input, int count) {
        if (count < 2){
            return;
        }

        setPassCard(false);
        String searchQuery = "";
        try {

            byte[] bytes = input.getBytes("EUC-KR");
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<bytes.length; i++) {
                sb.append(String.format("%%%02X",bytes[i]));
            }
            searchQuery = sb.toString();

        } catch (Exception e){

        }
        _currentTargetAdapter.clear();
        _currentTargetAdapter.notifyDataSetChanged();
        HttpConn con = new HttpConn();
        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        String cookie = cs.getCookie();
        con.setCallBackListener(this);
        con.setUserAgent("DJUAPP");
        headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        con.setPrefixHeaderFields(headers);
        Map<String, String> paramss = new HashMap<String, String>();
        paramss.put("search_name", searchQuery);

        String tmpUrl = url + "?" + "litem=" + _litem;
        try {
            con.sendRequest(HttpConn.Method.POST, new URL(tmpUrl), paramss);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR:" + e.toString());
        }
    }

    @Override
    public void searchBarFocused() {

    }

    @Override
    public void searchBarUnfocesed() {

    }
}

