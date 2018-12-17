package kr.ac.dju.growthbookapp;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestItem {
    private String _description;
    private String _date;
    private String _point;

    private static int autoInc = 0;
    private int _id;

    public RecentPassBookTestItem ( String description, String date, String point) {
        _id = autoInc;
        autoInc++;
        _description = description;
        _date = date;
        _point = point;
    }

    public int getId () {
        return _id;
    }

    public String getDescription () {
        return _description;
    }

    public String getDate() {
        return _date;
    }

    public String getPoint() {
        return _point;
    }
}
