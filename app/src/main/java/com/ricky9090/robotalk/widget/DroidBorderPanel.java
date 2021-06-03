package com.ricky9090.robotalk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ricky9090.robotalk.R;

/**
 * Insufficient display space for border panel in Android
 * Instead, simply use a horizontal LinearLayout
 */
public class DroidBorderPanel extends LinearLayout {

    private FrameLayout center;
    private FrameLayout top;
    private FrameLayout right;
    private FrameLayout bottom;
    private FrameLayout left;

    public DroidBorderPanel(Context context) {
        this(context, null);
    }

    public DroidBorderPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DroidBorderPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_droid_border_panel, this, true);

        center = findViewById(R.id.center_container);
        top = findViewById(R.id.top_container);
        right = findViewById(R.id.right_container);
        bottom = findViewById(R.id.bottom_container);
        left = findViewById(R.id.left_container);
    }

    public void addCenter(View centerContent) {
        if (centerContent != null) {
            center.addView(centerContent);
        }
    }

    public void addTop(View topContent) {
        if (topContent != null) {
            top.addView(topContent);
        }
    }

    public void addRight(View rightContent) {
        if (rightContent != null) {
            right.addView(rightContent);
        }

    }

    public void addBottom(View bottomContent) {
        if (bottomContent != null) {
            bottom.addView(bottomContent);
        }
    }

    public void addLeft(View leftContent) {
        if (leftContent != null) {
            left.addView(leftContent);
        }
    }
}
