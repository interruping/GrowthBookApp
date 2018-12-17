package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by geonyounglim on 2017. 7. 26..
 */

public class MadeByFragment extends NavigationBarFragment {
    ListView _listView;
    MadeByListAdapter _adapter;

    ArrayList<MadeByListAdapter.Worker> _inputList;

    public MadeByFragment() {
        super(R.layout.fragment_made_by, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View result = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar("만든 사람들",(View v)->{
            MainActivity ma = (MainActivity)this.getActivity();
            ma.toggleMenu();
        }, (View v)->{
            activeSearch();
        });

        hideRightAcc();
        _listView = (ListView)result.findViewById(R.id.made_by_listview);
        _adapter = new MadeByListAdapter();
        _listView.setAdapter(_adapter);

        _inputList = new ArrayList<>();
        _inputList.add(new MadeByListAdapter.Worker("임근영", "안드로이드 개발 및 설계", "Pingt0U", ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.lim)));
        _inputList.add(new MadeByListAdapter.Worker("설지환", "안드로이드 개발 및 서버 연동", "dodrnfkd", ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.seol)));
        _inputList.add(new MadeByListAdapter.Worker("최성민", "API서버 개발 및 설계", "janggu1018", ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.choi)));

        long seed = System.nanoTime();
        Collections.shuffle(_inputList, new Random(seed));

        for (MadeByListAdapter.Worker worker : _inputList ){
            _adapter.addItem(worker);
        }



        return result;
    }
}
