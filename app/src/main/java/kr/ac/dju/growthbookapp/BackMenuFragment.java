package kr.ac.dju.growthbookapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by geonyounglim on 2017. 5. 28..
 */

public class BackMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    public ListView _menuListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_back_menu, container, false);

        _menuListView = (ListView)rootView.findViewById(R.id.menu_list_view);

        MenuListAdapter adapter = new MenuListAdapter();
        _menuListView.setAdapter(adapter);


        adapter.addItem( R.layout.fragment_news,ContextCompat.getDrawable(getActivity().getApplicationContext()
                ,R.drawable.news_icon), getResources().getString(R.string.news));
        adapter.addItem( 0, ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.event_icon), getResources().getString(R.string.event));
        adapter.addItem( 0,ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.mileage_icon), getResources().getString(R.string.mileage));
        adapter.addItem( 0,ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.one_by_one_icon), getResources().getString(R.string.one_by_one));
        adapter.addItem( R.layout.fragment_book_list,ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.booklist_icon), getResources().getString(R.string.booklist));

        _menuListView.setOnItemClickListener(this);


        Button btn = (Button)rootView.findViewById(R.id.loginout_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity)getActivity();
                ma.switchFrontFragment(R.layout.fragment_login);
            }
        });

        return rootView;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MainActivity ma = (MainActivity)getActivity();


        ma.switchFrontFragment((int)id);
    }
}
