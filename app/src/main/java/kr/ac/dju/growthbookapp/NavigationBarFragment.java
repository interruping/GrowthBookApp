package kr.ac.dju.growthbookapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by geonyounglim on 2017. 5. 28..
 */

public class NavigationBarFragment extends Fragment {
    final private String TAG = "navigationbar_from_fragment";
    final private int BAR_DEFAULT_HEIGHT_DP  = 55;

    private int _rootConstraintLayoutId;
    private int _layoutFileId;
    private LinearLayout _navigationBar;

    private View _rootView;
    private View _leftAcc;
    private View _midAcc;
    private View _rightAcc;

    public NavigationBarFragment( int layoutFileId, int rootConstraintLayoutId){
        super();

        _layoutFileId = layoutFileId;
        _rootConstraintLayoutId = rootConstraintLayoutId;
    }

    private void _rebuiltNavigationBarAccessory() {
        _navigationBar.removeAllViews();

        if ( _leftAcc != null){
            _navigationBar.addView(_leftAcc);
        }else {
            View empty = new View(getActivity());
            LinearLayout.LayoutParams accParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            accParams.weight = 1;
            empty.setBackgroundColor(0x00ffffff);
            _navigationBar.addView(empty);
        }
        if ( _midAcc != null){
            _navigationBar.addView(_midAcc);
        }else {
            View empty = new View(getActivity());
            LinearLayout.LayoutParams accParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            accParams.weight = 4;
            empty.setBackgroundColor(0x00ffffff);
            _navigationBar.addView(empty);
        }
        if ( _rightAcc != null){
            _navigationBar.addView(_rightAcc);
        }else {
            View empty = new View(getActivity());
            LinearLayout.LayoutParams accParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            accParams.weight = 1;
            empty.setBackgroundColor(0x00ffffff);
            _navigationBar.addView(empty);
        }

    }

    public void addLeftAccessoryToNavigatonBar(View acc) {
        LinearLayout.LayoutParams accParams =new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        accParams.weight = 1;

        acc.setLayoutParams(accParams);
        _leftAcc = acc;
        _rebuiltNavigationBarAccessory();
    }

    public void addMidAccessoryToNavigatonBar(View acc) {
        LinearLayout.LayoutParams accParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

        accParams.weight = 4;
        acc.setLayoutParams(accParams);
        _midAcc = acc;
        _rebuiltNavigationBarAccessory();
    }

    public void addRightAccessoryToNavigatonBar(View acc) {
        LinearLayout.LayoutParams accParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        acc.setLayoutParams(accParams);
        accParams.weight = 1;
        _rightAcc = acc;
        _rebuiltNavigationBarAccessory();

    }

    public void testCall(){

    }

    public void setWithCommonNavigationBar(String title, View.OnClickListener menuButtonOnClick, View.OnClickListener searchButtonOnClick) {


        TextView titleView = new TextView(getActivity());
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(Color.WHITE);
        titleView.setBackgroundColor(Color.TRANSPARENT);
        titleView.setGravity(Gravity.CENTER);

        ImageButton menuBtn = new ImageButton(getActivity());
        menuBtn.setImageResource(R.drawable.menu_icon);
        menuBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        menuBtn.setBackgroundColor(Color.TRANSPARENT);
        menuBtn.setPadding(20, 20, 20, 20);

        ImageButton searchBtn = new ImageButton(getActivity());
        searchBtn.setImageResource(R.drawable.search_icon);
        searchBtn.setBackgroundColor(Color.TRANSPARENT);
        searchBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        menuBtn.setOnClickListener(menuButtonOnClick);
        searchBtn.setOnClickListener(searchButtonOnClick);

        addLeftAccessoryToNavigatonBar(menuBtn);
        addMidAccessoryToNavigatonBar(titleView);
        addRightAccessoryToNavigatonBar(searchBtn);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        _rootView = inflater.inflate(_layoutFileId, container, false);



        _navigationBar = new LinearLayout(getActivity());
        _navigationBar.setBackgroundResource(R.color.colorPrimary);

        float density = _rootView.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams navigationBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(BAR_DEFAULT_HEIGHT_DP * density) );

        _navigationBar.setPadding(0, 0, 0, 0);
        _navigationBar.setLayoutParams(navigationBarParams);
        _navigationBar.setOrientation(LinearLayout.HORIZONTAL);
        _navigationBar.setBaselineAligned(false);
        _navigationBar.setWeightSum(6f);
        _navigationBar.setElevation(20.0f);

        ConstraintLayout con = (ConstraintLayout)_rootView.findViewById(_rootConstraintLayoutId);

        con.addView(_navigationBar);

        ConstraintSet set = new ConstraintSet();
        set.clone(con);
        set.connect(_navigationBar.getId(), ConstraintSet.TOP, con.getId(), ConstraintSet.TOP );
        set.connect(_navigationBar.getId(), ConstraintSet.LEFT, con.getId(), ConstraintSet.LEFT );
        set.connect(_navigationBar.getId(), ConstraintSet.RIGHT, con.getId(), ConstraintSet.RIGHT );
        set.applyTo(con);

        return _rootView;
    }
}
