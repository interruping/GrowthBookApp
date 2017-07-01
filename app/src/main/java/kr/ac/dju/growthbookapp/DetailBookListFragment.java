package kr.ac.dju.growthbookapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dju.book.HttpConn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dodrn on 2017-06-03.
 */

public class DetailBookListFragment extends NavigationBarFragment {

    private Map<String, String> unapproved = new HashMap<String, String>();
    private Map<String, String> approve = new HashMap<String, String>();
    private Map<String, String> approved = new HashMap<String, String>();
    private String url="";
    private View _rootView;
    private  String key="";
    public DetailBookListFragment(){
        super(R.layout.fragment_detailbooklist, R.id.root_constraint);

    }




    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);
        _rootView.setBackgroundColor(Color.WHITE);
        setWithCommonNavigationBar(getResources().getString(R.string.booklist),(View v)->{

            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{

        });



        TabLayout tableLayout = (TabLayout)_rootView.findViewById(R.id.tl_tabs);
        tableLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));

        ViewPager viewPager = (ViewPager)_rootView.findViewById(R.id.vp_pager);

        Fragment[] arrFragments = new Fragment[3];
        arrFragments[0] =new UnapprovedBookFragment();
        arrFragments[1] = new ApproveBookFragment();
        arrFragments[2] = new ApprovedBookFragment();

        AppCompatActivity pa = (AppCompatActivity)getActivity();
        DetailBookListFragment.MyPagerAdapter adapter = new DetailBookListFragment.MyPagerAdapter(pa.getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPager);

        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        System.out.println("쿠키값입니다. :" +cs.getCookie());
        GetKey();
        GetUrl();
        GetDataApprove(arrFragments[1]);
        GetDataApproved(arrFragments[2]);
        GetDataUnapprove(arrFragments[0]);

        return _rootView;
    }

    public void GetKey() {
        if(getArguments() != null){
            key = getArguments().getString("key");
        }
    }

    public void GetDataApproved(Fragment ft) {

        Bundle senddata = new Bundle();
        senddata.putSerializable("HashMap", (Serializable) unapproved);
        senddata.putString("url",url);
        ft.setArguments(senddata);
    }

    public void GetDataApprove(Fragment ft) {
        Bundle senddata = new Bundle();
        senddata.putSerializable("HashMap", (Serializable) approve);
        senddata.putString("url",url);
        ft.setArguments(senddata);
    }

    public void GetDataUnapprove(Fragment ft) {
        Bundle senddata = new Bundle();
        senddata.putSerializable("HashMap", (Serializable) unapproved);
        senddata.putString("url",url);
        ft.setArguments(senddata);
    }

    public void GetUrl() {

        if (key.equals("01")) {

            unapproved.put("tab", "1");
            unapproved.put("litem", "");

            approve.put("tap", "2");
            approve.put("litem", "");

            approved.put("tap", "3");
            approved.put("litem", "");
            url = "https://book.dju.ac.kr/ds2_11.html";
        }
        else{
            unapproved.put("tap","1");
            approve.put("tap","2");
            approved.put("tap","3");
            url = "https://book.dju.ac.kr/ds2_2.html";
            if(key.equals("02")){
                unapproved.put("Iitem","9");
                approve.put("Iitem","9");
                approved.put("Iitem","9");
            }
            if(key.equals("03")){
                unapproved.put("Iitem","3");
                approve.put("Iitem","3");
                approved.put("Iitem","3");
            }
            if(key.equals("04")){
                unapproved.put("Iitem","2");
                approve.put("Iitem","2");
                approved.put("Iitem","2");
            }
            if(key.equals("05")){
                unapproved.put("Iitem","5");
                approve.put("Iitem","5");
                approved.put("Iitem","5");
            }
            if(key.equals("06")){
                unapproved.put("Iitem","7");
                approve.put("Iitem","7");
                approved.put("Iitem","7");
            }
            if(key.equals("07")){
                unapproved.put("Iitem","10");
                approve.put("Iitem","10");
                approved.put("Iitem","10");
            }
            if(key.equals("08")){
                unapproved.put("Iitem","6");
                approve.put("Iitem","6");
                approved.put("Iitem","6");
            }

        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private  Fragment[] arrFragments;

        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments = arrFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch(position){
                case 0:
                    return "독서인증도서";
                case 1:
                    return "인증신청도서";
                case 2:
                    return "인증완료도서";
                default:
                    return "";
            }
        }
    }
}
