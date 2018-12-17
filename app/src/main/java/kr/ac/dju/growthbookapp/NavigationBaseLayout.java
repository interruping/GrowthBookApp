package kr.ac.dju.growthbookapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created by geonyounglim on 2017. 6. 26..
 */

public class NavigationBaseLayout extends ConstraintLayout
{
    private static final String TAG = NavigationBaseLayout.class.getName();

    public NavigationBaseLayout(Context context) {
        super(context);
    }

    public NavigationBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction()
    {
        int width = getWidth();
        return (width == 0) ? 0 : getX() / (float) width;
    }

    public void setXFraction(float xFraction) {
        int width = getWidth();
        setX((width > 0) ? (xFraction * width) : -9999);
    }
}