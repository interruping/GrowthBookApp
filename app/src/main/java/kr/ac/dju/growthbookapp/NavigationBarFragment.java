package kr.ac.dju.growthbookapp;

import android.animation.Animator;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * Created by geonyounglim on 2017. 5. 28..
 */

public class NavigationBarFragment extends Fragment {
    final private String TAG = "navigationbar_from_fragment";
    final private int BAR_DEFAULT_HEIGHT_DP  = 55;

    private int _rootConstraintLayoutId;
    private int _layoutFileId;

    private LinearLayout _navigationBar;

    private LinearLayout _searchBar;
    private EditText _searchInput;

    private View _rootView;
    private ConstraintLayout _blockView;
    private View _leftAcc;
    private View _midAcc;
    private View _rightAcc;

    private SearchBarEventListener _searchBarEventListener;
    private boolean _searchBarActive = false;

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
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        acc.setBackgroundResource(backgroundResource);
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
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        acc.setBackgroundResource(backgroundResource);
        acc.setLayoutParams(accParams);
        accParams.weight = 1;
        _rightAcc = acc;
        _rebuiltNavigationBarAccessory();

    }


    public void setBackButton (View.OnClickListener backButtonOnClick) {
        ImageButton backButton = new ImageButton(getActivity());
        backButton.setImageResource(R.drawable.back_button);
        backButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        backButton.setBackgroundColor(Color.TRANSPARENT);
        backButton.setPadding(20,20,20,20);
        backButton.setOnClickListener(backButtonOnClick);
        addLeftAccessoryToNavigatonBar(backButton);
    }

    public void setWithCommonNavigationBar(String title, View.OnClickListener menuButtonOnClick, View.OnClickListener searchButtonOnClick) {


        TextView titleView = new TextView(getActivity());
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(Color.WHITE);
        titleView.setMaxLines(2);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/NotoSansKR-Thin-Hestia.otf");
        titleView.setTypeface(tf);
        titleView.setLineSpacing(1, 1.0f);

        titleView.setBackgroundColor(Color.TRANSPARENT);
        titleView.setGravity(Gravity.CENTER);

        ImageButton menuBtn = new ImageButton(getActivity());
        menuBtn.setImageResource(R.drawable.menu_icon);
        menuBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        menuBtn.setBackgroundColor(Color.TRANSPARENT);
//        menuBtn.setPadding(10, 20, 10, 20);

        ImageButton searchBtn = new ImageButton(getActivity());
        searchBtn.setImageResource(R.drawable.search_icon);
        searchBtn.setBackgroundColor(Color.TRANSPARENT);
        searchBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        searchBtn.setPadding(10,20,10,20);
        menuBtn.setOnClickListener(menuButtonOnClick);
        searchBtn.setOnClickListener(searchButtonOnClick);

        addLeftAccessoryToNavigatonBar(menuBtn);
        addMidAccessoryToNavigatonBar(titleView);
        addRightAccessoryToNavigatonBar(searchBtn);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {

       if ( nextAnim == R.animator.slide_in_right ){
           _blockView.setClickable(false);
       }

        return null;
    }

    public void pushFragmentTo(int srcFrame, Fragment target , Bundle args) {
        MainActivity ma = (MainActivity)getActivity();
        if (ma.isMenuToggled() == true ){

            ma.toggleMenu();
            return;
        }


        _blockView.setClickable(true);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left,
                R.animator.slide_out_right, R.animator.slide_in_right,  R.animator.slide_out_left);

        target.setArguments(args);

        ft.hide(this);
        ft.add(srcFrame, target);
        ft.addToBackStack(null);
        ft.commit();

    }
    public void hideRightAcc(){
        _rightAcc.setVisibility(INVISIBLE);

    }

    public void showRightAcc() {
        _rightAcc.setVisibility(VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        _rootView = inflater.inflate(_layoutFileId, container, false);
        _rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        _navigationBar = new LinearLayout(getActivity());
        _navigationBar.setBackgroundResource(R.color.colorPrimaryDark);

        float density = _rootView.getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams navigationBarParams = new LinearLayout.LayoutParams(0, (int)(BAR_DEFAULT_HEIGHT_DP * density));
        _navigationBar.setPadding(0, 0, 0, 0);
        _navigationBar.setLayoutParams(navigationBarParams);
        _navigationBar.setOrientation(LinearLayout.HORIZONTAL);
        _navigationBar.setBaselineAligned(false);
        _navigationBar.setWeightSum(6f);
        _navigationBar.setElevation(5);

        LinearLayout.LayoutParams searchTextFieldParams = new LinearLayout.LayoutParams(0, (int)(BAR_DEFAULT_HEIGHT_DP * density) );
        _searchBar  = new LinearLayout(getActivity());
        _searchBar.setBackgroundResource(R.color.colorPrimaryDark);
        _searchBar.setLayoutParams(searchTextFieldParams);
        _searchBar.setOrientation(LinearLayout.HORIZONTAL);
        _searchBar.setWeightSum(10f);

        LinearLayout.LayoutParams searchInputParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        searchInputParam.weight = 10f;
        searchInputParam.setMargins(20,20,20,20);
        _searchInput = new EditText(getActivity());
        _searchInput.setBackgroundResource(R.color.colorPrimaryDark);
        _searchInput.setLayoutParams(searchInputParam);
        _searchInput.setMaxLines(1);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        gd.setCornerRadius(20);

        _searchInput.setBackground(gd);
        _searchInput.setPadding(30,0,0,0);
        _searchInput.setHint("검색");
        _searchInput.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorHighLight));
        _searchInput.setFocusable(false);
        _searchInput.setFocusableInTouchMode(false);
        _searchInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_icon, 0, R.drawable.close_icon, 0);

        _searchInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (_searchInput.getRight() - _searchInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        inactiveSearch();
                        return true;
                    }
                }
                return false;
            }});

        _searchBar.addView(_searchInput);

        _blockView = new ConstraintLayout(getActivity());
        ConstraintLayout.LayoutParams blockViewParams = new ConstraintLayout.LayoutParams(0,0);
        _blockView.setLayoutParams(blockViewParams);
        _blockView.setBackgroundColor(Color.BLACK);
        _blockView.setAlpha(0f);
        _blockView.setElevation(100.0f);
        _blockView.setClickable(false);


        ConstraintLayout con = (ConstraintLayout)_rootView.findViewById(_rootConstraintLayoutId);


        _navigationBar.setId(R.id.navigation_fragment_navigation_bar);
        _searchBar.setId(R.id.navigation_fragment_search_edit_text_bar);
        _blockView.setId(R.id.navigation_fragment_block_view);

        con.addView(_searchBar);
        con.addView(_navigationBar);
        con.addView(_blockView);

        ConstraintSet set = new ConstraintSet();
        set.clone(con);
        set.connect(_searchBar.getId(), ConstraintSet.TOP, con.getId(), ConstraintSet.TOP );
        set.connect(_searchBar.getId(), ConstraintSet.START, con.getId(), ConstraintSet.START );
        set.connect(_searchBar.getId(), ConstraintSet.END, con.getId(), ConstraintSet.END );
        set.connect(_navigationBar.getId(), ConstraintSet.TOP, con.getId(), ConstraintSet.TOP  );
        set.connect(_navigationBar.getId(), ConstraintSet.START, con.getId(), ConstraintSet.START );
        set.connect(_navigationBar.getId(), ConstraintSet.END, con.getId(), ConstraintSet.END );

        set.connect(_blockView.getId(), ConstraintSet.TOP, con.getId(), ConstraintSet.TOP);
        set.connect(_blockView.getId(), ConstraintSet.START, con.getId(), ConstraintSet.START );
        set.connect(_blockView.getId(), ConstraintSet.END, con.getId(), ConstraintSet.END );
        set.connect(_blockView.getId(), ConstraintSet.BOTTOM, con.getId(), ConstraintSet.BOTTOM);
        set.applyTo(con);

        con.bringChildToFront(_searchBar);
        con.requestLayout();

        _searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ){
                    if ( _searchBarEventListener != null ) _searchBarEventListener.searchBarFocused();
                } else {
                   if (_searchBarEventListener != null ) _searchBarEventListener.searchBarUnfocesed();
                }
            }
        });

        _searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( _searchBarEventListener != null) _searchBarEventListener.searchBarKeyInput(s.toString(), s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return _rootView;
    }


    public void activeSearch() {
        _searchBarActive = true;
        if(_searchBarEventListener != null) _searchBarEventListener.searchBarWillActive();
        _searchInput.setVisibility(VISIBLE);
        _searchInput.setX(1000);
        _leftAcc.animate().setListener(null).translationX(-_navigationBar.getWidth()).withLayer();
        _rightAcc.animate().setListener(null).translationX(-_navigationBar.getWidth()).withLayer();
        _midAcc.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                _navigationBar.setElevation(0);
                _searchBar.setElevation(5f);
                _searchInput.animate().setListener(null).translationX(0).withLayer();
                _searchInput.setFocusable(true);
                _searchInput.setFocusableInTouchMode(true);
                _searchInput.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(_searchInput, InputMethodManager.SHOW_IMPLICIT);
                _searchBarActive = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).translationX(-_navigationBar.getWidth()).withLayer();
;    }
    public void inactiveSearch() {
        _searchBarActive = false;
        if( _searchBarEventListener != null) _searchBarEventListener.searchBarWillInactive();

        _searchInput.setFocusable(false);
        _searchInput.setFocusableInTouchMode(false);
        _searchInput.setFocusable(true);
        _searchInput.setFocusableInTouchMode(true);
        _searchInput.clearFocus();
        _searchInput.getText().clear();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        _searchInput.animate().translationX(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                _navigationBar.setElevation(5f);
                _searchBar.setElevation(0f);
                _searchInput.setVisibility(INVISIBLE);
                _leftAcc.animate().setListener(null).translationX(0).withLayer();
                _midAcc.animate().setListener(null).translationX(0).withLayer();
                _rightAcc.animate().setListener(null).translationX(0).withLayer();
                _searchBarActive = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public boolean isSearchBarActive() {
        return _searchBarActive;
    }

    public void setSearchBarEventListener( SearchBarEventListener listener ){
        _searchBarEventListener = listener;
    }

    public interface SearchBarEventListener {
        public void searchBarWillActive();
        public void searchBarWillInactive();
        public void searchBarKeyInput(String input, int count);
        public void searchBarFocused();
        public void searchBarUnfocesed();

    }
    public enum AlertType {
        WARNING, ERROR, UNKNOWNERROR, INFO, FAIL
    }

    public interface AlertViewConfirmListener {
        public void alertViewConfirmed(AlertType type, String title, String description );

    }

    public interface AlertViewConfirmCancelListener extends AlertViewConfirmListener {
        public void alertViewIsCanceled();
    }



    public void _makeAlertView ( boolean cancel ,AlertType type, String title, String description, String buttonString, @Nullable AlertViewConfirmListener listener ) {
        ConstraintLayout alertView = !cancel ? (ConstraintLayout)getActivity().getLayoutInflater().inflate(R.layout.alert_view, null)
                                            : (ConstraintLayout)getActivity().getLayoutInflater().inflate(R.layout.alert_cancel_view, null) ;

        ImageView imageView = (ImageView) alertView.findViewById(R.id.alert_image);
        TextView alertTitleView = (TextView) alertView.findViewById(R.id.alert_title);
        TextView alertDescriptionView = (TextView) alertView.findViewById(R.id.alert_description);
        Button confirmBotton = (Button) alertView.findViewById(R.id.confirm_button);
        alertTitleView.setText(title);
        alertDescriptionView.setText(description);

        switch ( type ){
            case WARNING:
                alertTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorHighLight));
                imageView.setImageResource(R.drawable.warning);
                break;
            case ERROR:
                alertTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorStrongHighLight));
                imageView.setImageResource(R.drawable.error_icon);
                break;
            case UNKNOWNERROR:
                alertTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorStrongHighLight));
                imageView.setImageResource(R.drawable.unknown_error_icon);
                break;
            case INFO:
                alertTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                imageView.setImageResource(R.drawable.info_icon);
                break;
            case FAIL:
                alertTitleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorStrongHighLight));
                imageView.setImageResource(R.drawable.fail_icon);
                break;

        }

        _blockView.setClickable(true);
        _blockView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                _blockView.animate().setListener(null);

                ConstraintLayout con = (ConstraintLayout)_rootView.findViewById(_rootConstraintLayoutId);
                ConstraintSet set = new ConstraintSet();
                alertView.setVisibility(INVISIBLE);
                alertView.setElevation(200.0f);

                Button confirmButton = (Button)alertView.findViewById(R.id.confirm_button);
                confirmButton.setText(buttonString);

                if ( cancel ){
                    Button cancelButton = (Button)alertView.findViewById(R.id.cancel_button);
                    cancelButton.setText("취소");
                }

                con.addView(alertView);
                set.clone(con);
                set.connect(alertView.getId(), ConstraintSet.TOP, con.getId(), ConstraintSet.TOP );
                set.connect(alertView.getId(), ConstraintSet.START, con.getId(), ConstraintSet.START );
                set.connect(alertView.getId(), ConstraintSet.END, con.getId(), ConstraintSet.END );
                set.connect(alertView.getId(), ConstraintSet.BOTTOM, con.getId(), ConstraintSet.BOTTOM);
                set.applyTo(con);
                alertView.setY(2000f);
                alertView.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        alertView.setVisibility(VISIBLE);
                        confirmBotton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _removeAlertView(alertView,listener,type,title,description);

                            }
                        });

                        if ( cancel ) {
                            Button cancelButton = (Button)alertView.findViewById(R.id.cancel_button);
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _removeAlerViewWithCancel(alertView,listener,type,title,description);

                                }});
                        }
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) { }
                    @Override
                    public void onAnimationCancel(Animator animation) { }
                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                }).translationY(0).withLayer();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).alpha(0.6f);

    }

    public void showAlertView (AlertType type, String title, String description, String buttonString, @Nullable AlertViewConfirmListener listener ) {

        _makeAlertView(false, type, title, description, buttonString, listener);


    }

    public void showAlertCancelView (AlertType type, String title, String description, String buttonString, @Nullable AlertViewConfirmCancelListener listener) {
        _makeAlertView(true, type, title, description, buttonString, listener);
    }



    private void _removeAlertView(ConstraintLayout alertView, @Nullable AlertViewConfirmListener listener, AlertType type, String title, String description) {

        ConstraintLayout con = (ConstraintLayout) _rootView.findViewById(_rootConstraintLayoutId);
        con.removeView(alertView);
        _blockView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                _blockView.setClickable(false);
                if (listener != null) {
                    listener.alertViewConfirmed(type, title, description);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        }).alpha(0f).withLayer();
    }

    public void _removeAlerViewWithCancel(ConstraintLayout alertView, @Nullable AlertViewConfirmListener listener, AlertType type, String title, String description) {

        ConstraintLayout con = (ConstraintLayout) _rootView.findViewById(_rootConstraintLayoutId);
        con.removeView(alertView);
        _blockView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                _blockView.setClickable(false);
                if (listener != null) {
                    AlertViewConfirmCancelListener tListener = (AlertViewConfirmCancelListener) listener;
                    tListener.alertViewIsCanceled();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        }).alpha(0f).withLayer();
    }

    public void blockScreen() {
        _blockView.setClickable(true);
    }

    public void openScreen() {
        _blockView.setClickable(false);
    }
}
