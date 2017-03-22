package com.example.asus.common;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus.activity.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by yhao on 2016/12/29.
 */

public class MyProgressbar {

    private static MyProgressbar instance;
    private RelativeLayout waitView = null;
    TextView textView;

    private MyProgressbar() {
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        waitView = (RelativeLayout) inflater.inflate(R.layout.gifview_wait, null, false);
        GifImageView mGifView = (GifImageView) waitView.findViewById(R.id.gifView);
        try {
            GifDrawable gifDrawable = new GifDrawable(getApplicationContext().getResources(), R.drawable.loading2);
            mGifView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
         textView = (TextView) waitView.findViewById(R.id.text);
        waitView.setVisibility(View.GONE);
        windowManager.addView(waitView, layoutParams);
    }

    boolean show() {
        textView.setText("");
        waitView.setVisibility(View.VISIBLE);
        return true;
    }

    boolean hide() {
        waitView.setVisibility(View.GONE);
        return false;
    }

    boolean showWithText(String text) {
        textView.setText(text);
        waitView.setVisibility(View.VISIBLE);
        return true;
    }

    static MyProgressbar getInstance() {
        if (instance == null) {
            synchronized (MyProgressbar.class) {
                if (instance == null) {
                    instance = new MyProgressbar();
                }
            }
        }
        return instance;
    }

}
