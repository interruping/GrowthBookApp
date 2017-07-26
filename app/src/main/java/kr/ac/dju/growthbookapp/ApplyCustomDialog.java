package kr.ac.dju.growthbookapp;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by dodrn on 2017-07-14.
 */

public class ApplyCustomDialog extends Dialog {

    private TextView mApplyTextview;
    private Button mApplyButton;
    private Button mCancleButton;
    private CheckBox mCheckBox;
    private int mAuth_position;
    private View.OnClickListener mApplyClickListener;
    private View.OnClickListener mCancleClcikListner;
    private boolean orientation;
    private ApproveBookAuthTestSubmitDetail detail_self;


    public int getmAuth_position() {return mAuth_position;}

    public void setDetail_self(ApproveBookAuthTestSubmitDetail detail){
        detail_self = detail;
    }
    public boolean setOrientation(){
        orientation = detail_self.getconfig();
        return orientation;
    }
    public ApplyCustomDialog(Context context, View.OnClickListener mApplyClickListener,
                             View.OnClickListener mCancleClcikListner, int position) {
        super(context);
        mAuth_position = position;
        this.mApplyClickListener = mApplyClickListener;
        this.mCancleClcikListner = mCancleClcikListner;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 외부 화면 흐리게 보이기
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.BOTTOM;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        Window window = getWindow();
        lpWindow.dimAmount = 0.8f;
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        // Custom Dialog 사이즈 조절


        lpWindow.copyFrom(window.getAttributes());
        lpWindow.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lpWindow.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // Custom Dialog 셋팅
        window.setAttributes(lpWindow);

        //Custom Dialog Animation 효과


        // layout 설정
        if (setOrientation() == true) {
            setContentView(R.layout.book_auth);
        }
        else if(setOrientation() == false) {
            setContentView(R.layout.book_auth_landscape);
        }
        else{
            setContentView(R.layout.book_auth);
        }

        basicSetting();




    }


    public void basicSetting() {
//        scrollView = (ScrollView)findViewById(R.id.mainCon);
//        scrollView.setScrollbarFadingEnabled(false);
        mApplyTextview = (TextView) findViewById(R.id.textView2);
        mApplyTextview.setScrollbarFadingEnabled(false);
        mApplyTextview.setMovementMethod(ScrollingMovementMethod.getInstance());
        mApplyButton = (Button) findViewById(R.id.Apply_Button);
        mCancleButton = (Button) findViewById(R.id.Cancle_Button);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    mApplyButton.setEnabled(true);
                }
                if(isChecked == false){
                    mApplyButton.setEnabled(false);
                }
            }
        });

        if(mApplyButton != null && mCancleButton != null) {
            mApplyButton.setOnClickListener(mApplyClickListener);
            mCancleButton.setOnClickListener(mCancleClcikListner);
        }else if (mApplyButton !=null
                && mCancleButton == null) {
            mApplyButton.setOnClickListener(mApplyClickListener);
        }else{

        }

    }

}
