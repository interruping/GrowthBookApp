package kr.ac.dju.growthbookapp;

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

/**
 * Created by geonyounglim on 2017. 5. 31..
 */

public class BookListFragment extends NavigationBarFragment {

    private View _rootView;

    public BookListFragment () {
        super(R.layout.fragment_book_list, R.id.root_constraint);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);
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
        MyPagerAdapter adapter = new MyPagerAdapter(pa.getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPager);

        HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
        System.out.println("쿠키값입니다. :" +cs.getCookie());
        return _rootView;
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
