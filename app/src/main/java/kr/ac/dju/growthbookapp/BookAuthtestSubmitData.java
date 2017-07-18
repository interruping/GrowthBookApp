package kr.ac.dju.growthbookapp;

/**
 * Created by dodrn on 2017-07-12.
 */

public class BookAuthtestSubmitData {
    private String mD_day;
    private String mTime;
    private String mWeekend;
    private String mLimit_Time;
    private String mContent;
    private String mAdmit_Limit;
    private String mSubmit_com;
    private String mApply_day;
    private String mApply_stime1;
    private String mApply_stime2;
    private String mApply_id;
    private String mApply_no;
    private String mApply_title;
    private String mApply_setting_no;



    public BookAuthtestSubmitData(String d_day, String time, String weekend, String limit_time, String content, String admit_limit,String submit_com,
                                  String apply_day, String apply_stime1, String apply_stime2, String apply_id, String apply_idx, String apply_title, String apply_setting_no) {
        mD_day = d_day;
        mTime = time;
        mWeekend = weekend;
        mLimit_Time = limit_time;
        mContent = content;
        mAdmit_Limit = admit_limit;
        mSubmit_com = submit_com;
        mApply_day = apply_day;
        mApply_stime1 = apply_stime1;
        mApply_stime2 = apply_stime2;
        mApply_id = apply_id;
        mApply_no = apply_idx;
        mApply_title = apply_title;
        mApply_setting_no = apply_setting_no;
    }

    public String getmD_day() {
    return mD_day;
}

    public String getmTime() {
    return mTime;
}

    public String getmWeekend() {
    return mWeekend;
}

    public String getmLimit_Time() {
    return mLimit_Time;
}

    public String getmContent() {
    return mContent;
}

    public String getmAdmit_Limit() {
    return mAdmit_Limit;
}

    public String getmSubmit_com() { return mSubmit_com;}

    public String getmApply_day() {return mApply_day;}

    public String getmApply_stime1() {return mApply_stime1;}

    public String getmApply_stime2() {return mApply_stime2;}

    public String getmApply_id() {return mApply_id;}

    public String getmApply_no() {return mApply_no;}

    public String getmApply_title() {return mApply_title;}

    public String getmApply_setting_no() {return mApply_setting_no;}
}