package com.example.libcommon;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/09 17:51
 * @类描述 ${TODO}
 */
public class RoundFrameLayout extends FrameLayout{
    public RoundFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ViewHelper.setViewOutLine(this, attrs, defStyleAttr, defStyleRes);
    }
    public void setViewOutline(int radius, int radiusSide) {
        ViewHelper.setViewOutLine(this, radius, radiusSide);
    }

}
