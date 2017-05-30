package kr.ac.dju.growthbookapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return _rootView;
    }
}
