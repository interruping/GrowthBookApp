package kr.ac.dju.growthbookapp;

import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;


import com.dju.book.*;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Boolean _slideToggle;
    private FrameLayout _frontSideContainer;
    private BackMenuFragment _backMenuFragment;
    private int _returnFragment;
    private Map<Integer, Fragment> _fragments;
    private boolean _isNeedAlertLogin;
    private boolean _isFailAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "fonts/NotoSansKR-DemiLight-Hestia.otf"))
        .addBold(Typekit.createFromAsset(this,"fonts/NotoSansKR-Bold-Hestia.otf"));



        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        _returnFragment = R.layout.fragment_news;
        _isNeedAlertLogin = false;
        _isFailAutoLogin = false;
        _slideToggle = false;
        _backMenuFragment = (BackMenuFragment)getFragmentManager().findFragmentById(R.id.back_side_fragment);
        _frontSideContainer = (FrameLayout)findViewById(R.id.front_side_container);
        _frontSideContainer.setClipToPadding(false);
        _frontSideContainer.setBackgroundColor(Color.WHITE);
        _frontSideContainer.setElevation(50.0f);

        _fragments = new HashMap<Integer, Fragment>();

        Fragment initFragment = null;
        UserInfoSafeStorage safe = new UserInfoSafeStorage(this);
        if ( safe.isSafeUsed() ){
            initFragment = new AutoLoginFragment();
        } else {
            initFragment = new NewsFragment();
        }


        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.front_side_container, initFragment);
        fragmentTransaction.commit();

        FrameLayout frmLyt = (FrameLayout) findViewById(R.id.front_side_container);
        frmLyt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public class LayoutSize {
        public float width;
        public float height;
    }

    public LayoutSize getMainActivityLayoutSize(){

        LayoutSize result = new LayoutSize();
        ConstraintLayout maRootLayout = (ConstraintLayout)findViewById(R.id.main_activity_root);
        float width = maRootLayout.getMeasuredWidth();
        float height = maRootLayout.getMeasuredHeight();
        result.width = width;
        result.height = height;

        return result;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            refreshMenuPosition();
            return;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            refreshMenuPosition();
            return;
        }
    }

    public static float convertPixelsToDp(float px, Context context){

        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float dp = px / (metrics.densityDpi / 160f);

        return dp;

    }

    public void refreshMenuPosition () {
        if (_slideToggle == true) {
            _frontSideContainer.setX((float)((getMainActivityLayoutSize().height)*0.7));
        }
    }


    public void toggleMenu() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if ( getFragmentManager().getBackStackEntryCount() > 1 ) {
            getFragmentManager().popBackStack();
        }

        if (_slideToggle == false) {


            _frontSideContainer.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    BackMenuFragment backMenuFragment = (BackMenuFragment)getFragmentManager().findFragmentById(R.id.back_side_fragment);
                    backMenuFragment.activeItemClick();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).translationX((float)((getMainActivityLayoutSize().width)*0.7)).withLayer();
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
                break;
            case R.layout.fragment_mileage:
                fragmentTransaction.replace(R.id.front_side_container, new MileageFragment());
                break;
            case R.layout.fragment_book_event:
                fragmentTransaction.replace(R.id.front_side_container, new BookEventFragment());
                break;
            case R.layout.fragment_one_by_one:
                fragmentTransaction.replace(R.id.front_side_container, new OneByOneFragment());
                break;
            case R.layout.fragment_recent_pass_book_test:
                fragmentTransaction.replace(R.id.front_side_container, new RecentPassBookTestFragment());
                break;
            case R.layout.fragment_recent_request_test:
                fragmentTransaction.replace(R.id.front_side_container, new RecentRequestTestFragment());
                break;
            case R.layout.fragment_made_by:
                fragmentTransaction.replace(R.id.front_side_container, new MadeByFragment());
                break;

        }
        fragmentTransaction.commit();
        _frontSideContainer.animate().setListener(null).translationX(0).withLayer();
        _slideToggle = false;

    }

    public boolean isNeedAlertLogin() {
        if ( _isNeedAlertLogin == true ) {
            _isNeedAlertLogin = false;
            return true;
        }
        return false;
    }

    public void switchFrontFragment(int targetId){

        boolean isLogin = HttpConn.CookieStorage.sharedStorage().getCookie().length() == 0 ? false : true;


        _frontSideContainer.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if ( targetId != R.layout.fragment_login ){
                    _returnFragment = targetId;
                }

                switch ( targetId ) {
                    case R.layout.fragment_mileage:
                    case R.layout.fragment_one_by_one:
                    case R.layout.fragment_book_list:
                    case R.layout.fragment_recent_pass_book_test:
                    case R.layout.fragment_recent_request_test:
                        if ( isLogin == false ){
                            _isNeedAlertLogin = true;
                            _replaceFragment(R.layout.fragment_login);
                        } else {
                            _replaceFragment(targetId);
                        }
                        break;
                    default:
                        _replaceFragment(targetId);
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).translationX(_frontSideContainer.getWidth()).withLayer();
    }

    public void returnPrevLogin () {
        _backMenuFragment.changeToLogout();
        if ( _returnFragment == 0)
            return;

        int toReturn = _returnFragment;
        _returnFragment = 0;
        switchFrontFragment(toReturn);
    }

    public void loginComplete() {
        _backMenuFragment.changeToLogout();
        switchFrontFragment(R.layout.fragment_mileage);
    }

    public boolean isFailAutoLogin () {
        if ( _isFailAutoLogin == true ) {
            _isFailAutoLogin = false;
            return true;
        }
        return false;
    }

    public void autoLoginFailTask() {
        _isNeedAlertLogin = false;
        _isFailAutoLogin = true;
        _replaceFragment(R.layout.fragment_login);
    }

    public boolean isMenuToggled() {
        return _slideToggle;
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0){
            toggleMenu();
        } else {

            super.onBackPressed();
        }
    }

}
