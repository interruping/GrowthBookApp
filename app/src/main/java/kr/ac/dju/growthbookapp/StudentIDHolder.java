package kr.ac.dju.growthbookapp;

/**
 * Created by geonyounglim on 2017. 7. 24..
 */

public class StudentIDHolder {

    static private StudentIDHolder _instance;
    private String _studentID;

    private StudentIDHolder () {
        _studentID = null;
    }


    public static StudentIDHolder getInstance() {
        if ( _instance == null ){
            _instance = new StudentIDHolder();
        }
        return _instance;
    }

    public void storeID(String studentID) {
        _studentID = studentID;
    }
    public boolean isHold() {
        return _instance == null ? false : true;
    }
    public String getID() {
        return _studentID;
    }
}
