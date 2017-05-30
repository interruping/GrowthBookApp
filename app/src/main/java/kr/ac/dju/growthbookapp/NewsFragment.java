package kr.ac.dju.growthbookapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by geonyounglim on 2017. 5. 28..
 */

public class NewsFragment extends NavigationBarFragment implements View.OnClickListener {
    public NewsFragment() {
        super(R.layout.fragment_news, R.id.root_constraint);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        setWithCommonNavigationBar(getResources().getString(R.string.news),(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{

        });

        return result;
    }

    @Override
    public void onClick(View v) {
        MainActivity ma = (MainActivity)getActivity();
        ma.toggleMenu();
    }
}
