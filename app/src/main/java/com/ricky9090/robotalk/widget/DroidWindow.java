package com.ricky9090.robotalk.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ricky9090.robotalk.R;

public class DroidWindow extends FrameLayout {

    private TextView windowTitle;
    private ImageView windowClose;
    private LinearLayout windowMenuLinear;
    private FrameLayout windowContent;

    private Handler uiHandler;

    public DroidWindow(@NonNull Context context) {
        this(context, null);
    }

    public DroidWindow(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DroidWindow(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_droid_window, this, true);
        windowTitle = findViewById(R.id.window_title);
        windowClose = findViewById(R.id.window_close);
        windowMenuLinear = findViewById(R.id.window_menu);
        windowContent = findViewById(R.id.window_content);
    }

    public void setTitle(String title) {
        windowTitle.setText(title);
    }

    public void addMenu(View menu) {
        windowMenuLinear.addView(menu);
    }

    public void addWindowContent(View content) {
        windowContent.addView(content);
    }

    public void setWindowCloseListner(View.OnClickListener listner) {
        windowClose.setOnClickListener(listner);
    }

    public void setUiHandler(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public void onClose() {
        if (uiHandler != null) {
            Message msg = Message.obtain();
            msg.what = 100;
            msg.obj = this.hashCode();
            uiHandler.sendMessage(msg);
        }
    }
}
