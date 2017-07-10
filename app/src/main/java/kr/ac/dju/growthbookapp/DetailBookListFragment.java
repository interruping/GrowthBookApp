package kr.ac.dju.growthbookapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
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
        System.out.println(">>>>>>>>>>>>>>디테일");
        System.out.println(">>>>>>>>>>>>>>디테일");
        System.out.println(">>>>>>>>>>>>>>디테일");
        System.out.println(">>>>>>>>>>>>>>디테일");
        System.out.println(">>>>>>>>>>>>>>디테일");
        System.out.println(">>>>>>>>>>>>>>디테일");

        _rootView = super.onCreateView(inflater, container, savedInstanceState);
        _rootView.setBackgroundColor(Color.WHITE);
        Bundle args = getArguments();
        setWithCommonNavigationBar(args.getString("title"),(View v)->{

            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{

        });

        setBackButton((View v)->{

            getFragmentManager().popBackStack();
        });

        TabLayout tableLayout = (TabLayout)_rootView.findViewById(R.id.tl_tabs);
        tableLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));

        ViewPager viewPager = (ViewPager)_rootView.findViewById(R.id.vp_pager);

        Fragment[] arrFragments = new Fragment[3];

        AppCompatActivity pa = (AppCompatActivity)getActivity();
        DetailBookListFragment.MyPagerAdapter adapter = new DetailBookListFragment.MyPagerAdapter(pa.getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        adapter.notifyDataSetChanged();
        tableLayout.setupWithViewPager(viewPager);


        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        //System.out.println("쿠키값입니다. :" +cs.getCookie());
        GetKey();
        GetUrl();


        return _rootView;
    }

    public void GetKey() {
        if(getArguments() != null){
            key = getArguments().getString("key");
        }
    }

    public void GetDataApproved(Fragment ft) {

        Bundle senddata = new Bundle();
        senddata.putSerializable("HashMap", (Serializable) approved);
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

        if (key.equals("1")) {

            unapproved.put("tab", "1");
            unapproved.put("litem", "");

            approve.put("tab", "2");
            approve.put("litem", "");

            approved.put("tab", "3");
            approved.put("litem", "");
            url = "https://book.dju.ac.kr/ds2_11.html";
        }
        else{
            unapproved.put("tab","1");
            approve.put("tab","2");
            approved.put("tab","3");
            url = "https://book.dju.ac.kr/ds2_2.html";
            if(key.equals("2")){
                unapproved.put("litem","9");
                approve.put("litem","9");
                approved.put("litem","9");
            }
            if(key.equals("3")){
                unapproved.put("litem","3");
                approve.put("litem","3");
                approved.put("litem","3");
            }
            if(key.equals("4")){
                unapproved.put("litem","2");
                approve.put("litem","2");
                approved.put("litem","2");
            }
            if(key.equals("5")){
                unapproved.put("litem","5");
                approve.put("litem","5");
                approved.put("litem","5");
            }
            if(key.equals("6")){
                unapproved.put("litem","7");
                approve.put("litem","7");
                approved.put("litem","7");
            }
            if(key.equals("7")){
                unapproved.put("litem","10");
                approve.put("litem","10");
                approved.put("litem","10");
            }
            if(key.equals("8")){
                unapproved.put("litem","6");
                approve.put("litem","6");
                approved.put("litem","6");
            }

        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter{

        private  Fragment[] arrFragments;




        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments = arrFragments;
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
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

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: {
                    arrFragments[0] = new UnapprovedBookFragment();
                    GetDataUnapprove(arrFragments[0]);
                    return arrFragments[0];}
                case 1:{
                    arrFragments[1] = new ApproveBookFragment();
                    GetDataApprove( arrFragments[1]);
                    return  arrFragments[1];}
                case 2:{
                    arrFragments[2] = new ApprovedBookFragment();
                    GetDataApproved(arrFragments[2]);
                    return arrFragments[2];}
                default:
                    return null;
                }

            }
        }



    }


