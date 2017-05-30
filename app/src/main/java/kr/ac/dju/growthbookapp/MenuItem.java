package kr.ac.dju.growthbookapp;

import android.graphics.drawable.Drawable;

/**
 * Created by geonyounglim on 2017. 5. 29..
 */

public class MenuItem {
    private int _id;
    private Drawable _menuIcon;
    private String _menuString;

    public MenuItem(int id,Drawable menuIcon, String menuString ) {
        _id = id;
        _menuIcon = menuIcon;
        _menuString = menuString;
    }

    public int getId() { return _id; }

    public Drawable getMenuIcon() {
        return _menuIcon;
    }

    public String getMenuString() {
        return _menuString;
    }
}
