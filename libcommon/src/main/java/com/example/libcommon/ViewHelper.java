package com.example.libcommon;

import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/09 18:05
 * @类描述 ${TODO}
 */
public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_LEFT = 1;
    public static final int RADIUS_RIGHT = 2;
    public static final int RADIUS_TOP = 3;
    public static final int RADIUS_BOTTOM = 4;

    public static void setViewOutLine(View view, AttributeSet set, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = view.getContext().obtainStyledAttributes(set, R.styleable.roundFrameLayout, defStyleAttr, defStyleRes);
        int radius = typedArray.getDimensionPixelOffset(R.styleable.roundFrameLayout_radius, 0);
        int radiusSize = typedArray.getIndex(R.styleable.roundFrameLayout_radiusSize);

        setViewOutLine(view, radius, radiusSize);
    }

    public static void setViewOutLine(View view, int radius, int radiusSide) {
        if (radius <= 0) {
            return;
        }

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int w = view.getWidth(), h = view.getHeight();
                if (w == 0 || h == 0) {
                    return;
                }

                if (radiusSide != RADIUS_ALL) {
                    int left = 0, top = 0, right = w, bottom = h;
                    if (radiusSide == RADIUS_LEFT) {
                        right += radius;
                    } else if (radiusSide == RADIUS_TOP) {
                        bottom += radius;
                    } else if (radiusSide == RADIUS_RIGHT) {
                        left -= radius;
                    } else if (radiusSide == RADIUS_BOTTOM) {
                        top -= radius;
                    }
                    outline.setRoundRect(left, top, right, bottom, radius);
                    return;
                }

                int top = 0, bottom = h, left = 0, right = w;
                if (radius <= 0) {
                    outline.setRect(left, top, right, bottom);
                } else {
                    outline.setRoundRect(left, top, right, bottom, radius);
                }
            }
        });
        view.setClipToOutline(radius > 0);
        view.invalidate();
    }
}
