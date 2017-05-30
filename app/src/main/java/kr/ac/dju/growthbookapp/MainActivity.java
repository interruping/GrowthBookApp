package kr.ac.dju.growthbookapp;

import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import com.dju.book.*;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Boolean _slideToggle;
    private FrameLayout _frontSideContainer;

    private Map<Integer, Fragment> _fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        _slideToggle = false;
        _frontSideContainer = (FrameLayout)findViewById(R.id.front_side_container);
        _frontSideContainer.setClipToPadding(false);
        _frontSideContainer.setBackgroundColor(Color.WHITE);
        _frontSideContainer.setElevation(50.0f);

        _fragments = new HashMap<Integer, Fragment>();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.front_side_container, new NewsFragment());
        fragmentTransaction.commit();

        FrameLayout frmLyt = (FrameLayout) findViewById(R.id.front_side_container);
        frmLyt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    public void toggleMenu() {
        if (_slideToggle == false) {

            _frontSideContainer.animate().setListener(null).translationX(700).withLayer();
            _slideToggle = true;
        } else {

            _frontSideContainer.animate().setListener(null).translationX(0).withLayer();
            _slideToggle = false;
        }
    }

    private void _replaceFragment(int targetId) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(targetId){
            case R.layout.fragment_news:
                fragmentTransaction.replace(R.id.front_side_container,new NewsFragment());
                break;
            case R.layout.fragment_login:
                fragmentTransaction.replace(R.id.front_side_container,new LoginFragment());
                break;
            case R.layout.fragment_book_list:
                fragmentTransaction.replace(R.id.front_side_container, new BookListFragment());
        }
        fragmentTransaction.commit();
        _frontSideContainer.animate().setListener(null).translationX(0).withLayer();
        _slideToggle = false;

    }

    public void switchFrontFragment(int targetId){
        _frontSideContainer.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                _replaceFragment(targetId);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).translationX(_frontSideContainer.getWidth()).withLayer();
    }

    public void loginComplete() {
        switchFrontFragment(R.layout.fragment_news);
    }

}
