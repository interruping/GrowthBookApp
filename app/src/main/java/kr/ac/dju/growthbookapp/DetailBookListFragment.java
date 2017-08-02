package kr.ac.dju.growthbookapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dodrn on 2017-06-03.
 */

public class DetailBookListFragment extends NavigationBarFragment implements ApproveBookFragment.RequestCancelAlertView, NavigationBarFragment.SearchBarEventListener, ViewPager.OnPageChangeListener{
    private Map<String,String> applyattr = new HashMap<>();
    private Map<String, String> unapproved = new HashMap<String, String>();
    private Map<String, String> approve = new HashMap<String, String>();
    private Map<String, String> approved = new HashMap<String, String>();
    private String url="";
    private View _rootView;
    private  String key="";
    private String device;
    private DetailBookListFragment _self;
    private ApproveBookAuthTestSubmitDetail submit_self;
    private  DetailBookListFragment.MyPagerAdapter adapter;
    private boolean mBackState = false;
    private boolean _enterPage0Flag = false;


    private SearchBarEventListener _searchListener;

    @Override
    public void showCancelAlertView(Runnable confirmCallback) {
        showAlertCancelView(AlertType.WARNING, "취소하시겠습니까?", "취소 시 5점 감점", "신청", new AlertViewConfirmCancelListener() {
            @Override
            public void alertViewIsCanceled() {

            }

            @Override
            public void alertViewConfirmed(AlertType type, String title, String description) {
                confirmCallback.run();
            }
        });

    }

    public DetailBookListFragment(){
        super(R.layout.fragment_detailbooklist, R.id.root_constraint);
        _self = this;
    }


    public void setmBackState(boolean state){
        mBackState = state;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        _rootView = super.onCreateView(inflater, container, savedInstanceState);
        _rootView.setBackgroundColor(Color.WHITE);

        Bundle args = getArguments();
        setWithCommonNavigationBar(args.getString("title"),(View v)->{

            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{
            activeSearch();
        });

        setBackButton((View v)->{

            getFragmentManager().popBackStack();
        });


        TabLayout tableLayout = (TabLayout)_rootView.findViewById(R.id.tl_tabs);
        tableLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));

        ViewPager viewPager = (ViewPager)_rootView.findViewById(R.id.vp_pager);

        Fragment[] arrFragments = new Fragment[3];

        AppCompatActivity pa = (AppCompatActivity)getActivity();
        adapter = new DetailBookListFragment.MyPagerAdapter(pa.getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        adapter.notifyDataSetChanged();
        tableLayout.setupWithViewPager(viewPager);



        getFragmentManager().addOnBackStackChangedListener(new android.app.FragmentManager.OnBackStackChangedListener() {
            @Override

            public void onBackStackChanged() {

                if(mBackState == true) {

                    adapter.notifyDataSetChanged();
                    mBackState = false;
                }
            }
        });
        setSearchBarEventListener(this);

        GetKey();
        GetUrl();


        return _rootView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if( position == 0 && positionOffset == 0.0){
            _enterPage0Flag = true;
            showRightAcc();
        }
    }

    @Override
    public void onPageSelected(int position) {
        if ( position == 0 ){
            showRightAcc();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if ( _enterPage0Flag == true ){
            _enterPage0Flag = false;
            return;
        }
        if ( isSearchBarActive() == true ){
            inactiveSearch();
        }

        hideRightAcc();
    }

    @Override
    public void searchBarWillActive() {
        _searchListener.searchBarWillActive();
    }

    @Override
    public void searchBarWillInactive() {
        _searchListener.searchBarWillInactive();
    }

    @Override
    public void searchBarKeyInput(String input, int count) {
        _searchListener.searchBarKeyInput(input, count);
    }

    @Override
    public void searchBarFocused() {
        _searchListener.searchBarFocused();
    }

    @Override
    public void searchBarUnfocesed() {
        _searchListener.searchBarUnfocesed();
    }

    public void notifystateData(){
        adapter.notifyDataSetChanged();
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
        senddata.putString("device",device);
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

    public void transToTestSubmitDetail(String title, String no, String loginId, String question){
        ApproveBookAuthTestSubmitDetail temp = new ApproveBookAuthTestSubmitDetail();
        Bundle params = new Bundle();
        submit_self = temp;
        submit_self.setmUnapprove_self(this);
        applyattr.put("cmd","search");
        applyattr.put("no", no);
        applyattr.put("id", loginId);
        applyattr.put("question", question);
        params.putString("title",title);
        params.putString("value", loginId);
        params.putSerializable("HashMap", (Serializable) applyattr);
        temp.setArguments(params);
        pushFragmentTo(R.id.front_side_container,submit_self, params);

    }
    public void showError(String title, String desc){
        showAlertView(AlertType.INFO, title, desc, "확인", null);
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

                    UnapprovedBookFragment tmp = (UnapprovedBookFragment)arrFragments[0];
                    _searchListener = tmp;
                    tmp.setParentDetailFragment(_self);
                    GetDataUnapprove(arrFragments[0]);
                    return arrFragments[0];}
                case 1:{
                    arrFragments[1] = new ApproveBookFragment();
                    ApproveBookFragment fragment = (ApproveBookFragment)arrFragments[1];
                    fragment.setRequestCancelAlertView(_self);
                    fragment.setDetailBookListFragment(_self);
                    GetDataApprove( arrFragments[1]);
                    return  arrFragments[1];}
                case 2:{
                    arrFragments[2] = new ApprovedBookFragment();
                    ApprovedBookFragment _tmp = (ApprovedBookFragment)arrFragments[2];
                    _tmp.setParentFragment(_self);
                    GetDataApproved(arrFragments[2]);
                    return arrFragments[2];}
                default:
                    return null;
                }

            }
        }


}






