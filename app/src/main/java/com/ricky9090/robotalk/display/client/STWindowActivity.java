package com.ricky9090.robotalk.display.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ricky9090.robotalk.R;
import com.ricky9090.smallworld.SmallWorld;

import java.lang.ref.WeakReference;

public class STWindowActivity extends AppCompatActivity {

    private STUIHandler stuiHandler;

    private SwingClientImpl screenClient;
    private SmallWorld smallWorld;
    private FrameLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st_window);
        container = findViewById(R.id.container);
        stuiHandler = new STUIHandler(this);
        screenClient = new SwingClientImpl(stuiHandler, this);
        smallWorld = new SmallWorld(getApplication());
        smallWorld.connectScreenClient(screenClient);
    }

    @Override
    protected void onResume() {
        super.onResume();

        smallWorld.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenClient.clearWindow();
    }

    public void addWindow(View window) {
        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }
        container.addView(window);
    }

    static class STUIHandler extends Handler {

        WeakReference<STWindowActivity> activity;

        public STUIHandler(STWindowActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (activity.get() == null) {
                return;
            }

            STWindowActivity stActivity = activity.get();
            if (msg.what == 99) {
                View window = (View) msg.obj;
                stActivity.addWindow(window);
                return;
            }

            SwingClientImpl.UIAction uiAction = (SwingClientImpl.UIAction) msg.obj;
            stActivity.screenClient.onUpdate(uiAction.target, uiAction.action);
        }
    }
}
