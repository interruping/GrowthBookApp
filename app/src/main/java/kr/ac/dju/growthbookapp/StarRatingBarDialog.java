package kr.ac.dju.growthbookapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import static kr.ac.dju.growthbookapp.R.id.ratingBar;

/**
 * Created by dodrn on 2017-07-18.
 */

public class StarRatingBarDialog extends Dialog{

    RatingBar mStarBar;
    TextView mRaing_Text;
    Button mSubmit_Button;
    Button mCancle_Button;
    ImageView mRaing_View;
    float mRate;
    String mHash_Book_Name;

    private View.OnClickListener mSubmitClickListener;
    private View.OnClickListener mCancleClcikListner;

    public StarRatingBarDialog(@NonNull Context context,String hashname, View.OnClickListener mSubmitClickListener,
                               View.OnClickListener mCancleClcikListner) {
        super(context);

        this.mSubmitClickListener = mSubmitClickListener;
        this.mCancleClcikListner = mCancleClcikListner;
        this.mHash_Book_Name = hashname;
    }

    public String getmHash_Book_Name(){
        return mHash_Book_Name;
    }
    public float getmRate(){
        return mRate;
    }

    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.BOTTOM;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        Window window = getWindow();
        lpWindow.dimAmount = 0.8f;
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        // Custom Dialog 사이즈 조절


        lpWindow.copyFrom(window.getAttributes());
        lpWindow.width = WindowManager.LayoutParams.MATCH_PARENT;
        lpWindow.height = WindowManager.LayoutParams.MATCH_PARENT;

        //Custom Dialog Animation 효과
        lpWindow.windowAnimations = R.style.CustomDialogAnimation;


        // Custom Dialog 셋팅
        window.setAttributes(lpWindow);

        setContentView(R.layout.star_bar_rating_dialog);
        mStarBar = (RatingBar)findViewById(ratingBar);
        mRaing_Text = (TextView)findViewById(R.id.rating_text);
        mRaing_View = (ImageView)findViewById(R.id.ratingimg);
        mRaing_View.setVisibility(View.INVISIBLE);

        mStarBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()  == event.ACTION_MOVE){
                    float rating = mStarBar.getRating();

                    int ratings = (int)Math.round(rating);

                    mRaing_View.setVisibility(View.VISIBLE);
                    mRaing_Text.setVisibility(View.VISIBLE);


                    switch (ratings){
                        case 0: {
                            mRaing_Text.setVisibility(View.INVISIBLE);
                            mRaing_View.setVisibility(View.INVISIBLE);
                            mSubmit_Button.setEnabled(false);
                            break;
                        }
                        case 1: {
                            mRaing_Text.setText("너무 쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star1);
                            break;
                        }
                        case 2: {
                            mRaing_Text.setText("쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star2);
                            break;
                        }
                        case 3: {
                            mRaing_Text.setText("보통이예요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star3);
                            break;
                        }
                        case 4: {
                            mRaing_Text.setText("어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star4);
                            break;
                        }
                        case 5: {
                            mRaing_Text.setText("너무 어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star5);
                            break;
                        }
                        default:
                            break;
                    }
                }
                if(event.getAction() == event.ACTION_DOWN){
                    float rating = mStarBar.getRating();

                    int ratings = (int)Math.round(rating);

                    mRaing_View.setVisibility(View.VISIBLE);
                    mRaing_Text.setVisibility(View.VISIBLE);


                    switch (ratings){
                        case 0: {
                            mRaing_Text.setVisibility(View.INVISIBLE);
                            mRaing_View.setVisibility(View.INVISIBLE);
                            mSubmit_Button.setEnabled(false);
                            break;
                        }
                        case 1: {
                            mRaing_Text.setText("너무 쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star1);
                            break;
                        }
                        case 2: {
                            mRaing_Text.setText("쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star2);
                            break;
                        }
                        case 3: {
                            mRaing_Text.setText("보통이예요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star3);
                            break;
                        }
                        case 4: {
                            mRaing_Text.setText("어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star4);
                            break;
                        }
                        case 5: {
                            mRaing_Text.setText("너무 어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star5);
                            break;
                        }
                        default:
                            break;
                    }                }

                if(event.getAction() ==event.ACTION_UP){

                    float rating = mStarBar.getRating();
                    mRate = rating;
                    int ratings = (int)Math.round(rating);

                    mRaing_View.setVisibility(View.VISIBLE);
                    mRaing_Text.setVisibility(View.VISIBLE);


                    switch (ratings){
                        case 0: {
                            mRaing_Text.setVisibility(View.INVISIBLE);
                            mRaing_View.setVisibility(View.INVISIBLE);
                            mSubmit_Button.setEnabled(false);
                            break;
                        }
                        case 1: {
                            mRaing_Text.setText("너무 쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star1);
                            break;
                        }
                        case 2: {
                            mRaing_Text.setText("쉬워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star2);
                            break;
                        }
                        case 3: {
                            mRaing_Text.setText("보통이예요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star3);
                            break;
                        }
                        case 4: {
                            mRaing_Text.setText("어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star4);
                            break;
                        }
                        case 5: {
                            mRaing_Text.setText("너무 어려워요");
                            mSubmit_Button.setEnabled(true);
                            mRaing_View.setImageResource(R.drawable.star5);
                            break;
                        }
                        default:
                            break;
                    }
                }

                return false;
            }
        });

//        mStarBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating,
//                                        boolean fromUser) {
//                int ratings = (int)rating;
//
//                mRaing_View.setVisibility(View.VISIBLE);
//                mRaing_Text.setVisibility(View.VISIBLE);
//
//                mRate = rating;
//                switch (ratings){
//                    case 0: {
//                        mRaing_Text.setVisibility(View.INVISIBLE);
//                        mRaing_View.setVisibility(View.INVISIBLE);
//                        mSubmit_Button.setEnabled(false);
//                        break;
//                    }
//                    case 1: {
//                        mRaing_Text.setText("너무 쉬워요");
//                        mSubmit_Button.setEnabled(true);
//                        mRaing_View.setImageResource(R.drawable.star1);
//                        break;
//                    }
//                    case 2: {
//                        mRaing_Text.setText("쉬워요");
//                        mSubmit_Button.setEnabled(true);
//                        mRaing_View.setImageResource(R.drawable.star2);
//                        break;
//                    }
//                    case 3: {
//                        mRaing_Text.setText("보통이예요");
//                        mSubmit_Button.setEnabled(true);
//                        mRaing_View.setImageResource(R.drawable.star3);
//                        break;
//                    }
//                    case 4: {
//                        mRaing_Text.setText("어려워요");
//                        mSubmit_Button.setEnabled(true);
//                        mRaing_View.setImageResource(R.drawable.star4);
//                        break;
//                    }
//                    case 5: {
//                        mRaing_Text.setText("너무 어려워요");
//                        mSubmit_Button.setEnabled(true);
//                        mRaing_View.setImageResource(R.drawable.star5);
//                        break;
//                    }
//                    default:
//                        break;
//                }
//
//
//            }
//        });

        mSubmit_Button = (Button)findViewById(R.id.submit);
        mCancle_Button = (Button)findViewById(R.id.cancle);


        if(mSubmit_Button != null && mCancle_Button != null) {
            mSubmit_Button.setOnClickListener(mSubmitClickListener);
            mCancle_Button.setOnClickListener(mCancleClcikListner);
        }else if (mSubmit_Button !=null
                && mCancle_Button == null) {
            mSubmit_Button.setOnClickListener(mSubmitClickListener);
        }else{

        }
    }



}
