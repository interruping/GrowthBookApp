package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dju.book.HttpConn;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dodrn on 2017-07-12.
 */

public class BookAuthTestSubmitAdapter extends BaseAdapter implements HttpConn.CallbackListener{
    private ArrayList<BookAuthtestSubmitData> mData = new ArrayList<>();
    private ApplyCustomDialog mCustomDialog;
    private Map<String,String> headers;
    private BookAuthTestSubmitAdapter _self = this;
    private Map<String,String> param = new HashMap<String,String>();
    private ApproveBookAuthTestSubmitDetail mApprove_Detail;
    private int pos;
    private String mRelut_content;
    public  BookAuthTestSubmitAdapter(ArrayList<BookAuthtestSubmitData> data){

        mData = data;
    }

    public void setBookAuthTestSubmitDetail(ApproveBookAuthTestSubmitDetail self){

        mApprove_Detail = self;
    }

    private View.OnClickListener mApplyClickListener = new View.OnClickListener(){
        public void onClick(View v){


            HttpConn con = new HttpConn();
            HttpConn.CookieStorage cs = HttpConn.CookieStorage.sharedStorage();
            String cookie = cs.getCookie();
            con.setCallBackListener(_self);
            con.setUserAgent("DJUAPP");
            headers = new HashMap<String, String>();
            headers.put("Cookie", cookie);
            headers.put("Referer","https://book.dju.ac.kr/ds2_2.html?tab=1&litem=3");
            con.setPrefixHeaderFields(headers);
            pos = mCustomDialog.getmAuth_position();
            param.put("cmd","save");
            param.put("day", mData.get(pos).getmApply_day());
            param.put("stime1", mData.get(pos).getmApply_stime1());
            param.put("stime2", mData.get(pos).getmApply_stime2());
            param.put("id", mData.get(pos).getmApply_id());
            param.put("no", mData.get(pos).getmApply_no());
            param.put("time", mData.get(pos).getmApply_title());
            param.put("setting_no", mData.get(pos).getmApply_setting_no());
            String apply_url = "https://book.dju.ac.kr/ds_pages/ajax_book.php";

            try {
                con.sendRequest(HttpConn.Method.POST, new URL(apply_url), param);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR:" + e.toString());
            }

            mCustomDialog.dismiss();

        }
    };
    private View.OnClickListener mCancleClickListener = new View.OnClickListener(){
        public void onClick(View v){
            Toast.makeText(v.getContext().getApplicationContext(),"취소되었습니다.", Toast.LENGTH_SHORT ).show();
            mCustomDialog.dismiss();
        }
    };

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int mposition = position;
        Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.approve_book_item_button, parent, false);
        }


        TextView mD_day = (TextView) convertView.findViewById(R.id.D_days);
        TextView mTime = (TextView) convertView.findViewById(R.id.D_time);
        TextView mWeekend = (TextView) convertView.findViewById(R.id.D_weekend);
        TextView mLimt_Time = (TextView) convertView.findViewById(R.id.Time_limit);
        TextView mContent = (TextView) convertView.findViewById(R.id.Note);
        mContent.setSelected(true);
        mContent.setSingleLine(true);
        TextView mAdmit_Limit = (TextView) convertView.findViewById(R.id.Accept);
        Button applyButton = (Button) convertView.findViewById(R.id.Button_Acc);

            applyButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positions = position;
                    mCustomDialog = new ApplyCustomDialog(v.getContext(),
                            mApplyClickListener,
                            mCancleClickListener,
                            positions);

                    mApprove_Detail.setDialog(mCustomDialog);
                    mCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();


                }
            });


        BookAuthtestSubmitData BookAuthtestList = mData.get(position);
        mD_day.setText(BookAuthtestList.getmD_day());
        mTime.setText(BookAuthtestList.getmTime());
        mWeekend.setText(BookAuthtestList.getmWeekend());
        mLimt_Time.setText(BookAuthtestList.getmLimit_Time());
        mContent.setText(BookAuthtestList.getmContent());
        mAdmit_Limit.setText(BookAuthtestList.getmAdmit_Limit());
        String submit = BookAuthtestList.getmSubmit_com();
        if(submit != null && submit.equals("신청완료") ==true){
            applyButton.setText("신청완료");
            applyButton.setBackgroundColor(Color.RED);
            applyButton.setEnabled(false);
        }

        return convertView;
    }


    @Override
    public void requestSuccess(HttpConn httpConn, int i, Map<String, String> map,String s) {
        int select = Integer.parseInt(s);
        switch (select){
            case 1:
                mRelut_content = "신청 되었습니다.";
                break;
            case 2:
                mRelut_content = "신청에 실패 하였습니다.";
                break;
            case 3:
                mRelut_content = "이미 신청 하셨습니다.";
                break;
            case 4:
                mRelut_content = "신청인원이 초과 되었습니다.";
                break;
            case 5:
                mRelut_content = "합격하셨던 시험은 다시 신청 하실수 없습니다.";
                break;
            case 6:
                mRelut_content = "응시가능을 실패하셨습니다. / 관리자에게 문의하세요";
                break;
            case 11:
                mRelut_content = "이미 응시신청을 한 책입니다.";
                break;
            case 15:
                mRelut_content = "2주에 3건초과하여 인증시험을 신청하실 수 없습니다.";
                break;
            default:
                mRelut_content = "다시 한번 확인 부탁드립니다.";

        }

        mApprove_Detail.showAlertView(NavigationBarFragment.AlertType.INFO, "알림", mRelut_content, "확인", new NavigationBarFragment.AlertViewConfirmListener() {
            @Override
            public void alertViewConfirmed(NavigationBarFragment.AlertType type, String title, String description) {

                if(mRelut_content.equals("신청 되었습니다.") == true)
                    mApprove_Detail.unprovedBackButton();

            }
        });

    }

    @Override
    public void requestError(HttpConn httpConn, int i, Map<String, String> map, String s) {

    }

    @Override
    public void requestTimeout(HttpConn httpConn) {

    }


}
