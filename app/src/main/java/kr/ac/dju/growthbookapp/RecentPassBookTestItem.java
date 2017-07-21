package kr.ac.dju.growthbookapp;

/**
 * Created by geonyounglim on 2017. 7. 19..
 */

public class RecentPassBookTestItem {
    private String _description;
    private String _date;
    private String _point;

    public RecentPassBookTestItem ( String description, String date, String point) {
        _description = description;
        _date = date;
        _point = point;
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
