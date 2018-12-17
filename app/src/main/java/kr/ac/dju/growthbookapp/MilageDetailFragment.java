package kr.ac.dju.growthbookapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by geonyounglim on 2017. 8. 2..
 */

public class MilageDetailFragment extends NavigationBarFragment {
    public MilageDetailFragment() {
        super(R.layout.fragment_mileage_detail, R.id.root_constraint);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar("순위별 등급표", (View v) -> {
            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View view) -> {

        });

        setBackButton((View v)->{

            getFragmentManager().popBackStack();
        });

        hideRightAcc();

        ListView descList = (ListView)result.findViewById(R.id.milage_detail_listview);

        LevelDescriptionListAdapter adapter = new LevelDescriptionListAdapter();
        descList.setAdapter(adapter);
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.diamond_rank), "다이아몬드 (1위 ~ 30위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.titanium_rank), "티타늄 (31위 ~ 100위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.gold_rank), "골드 (101위 ~ 200위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.silver_rank), "실버 (201위 ~ 500위)");
        adapter.addItem(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.bronze_rank), "브론즈 (501위 ~ 1000위)");

        return result;

    }
}
