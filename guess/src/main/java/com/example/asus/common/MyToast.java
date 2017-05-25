package com.example.asus.common;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.activity.R;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by yinghao on 2016/12/29.
 * Email：756232212@qq.com
 */

public class MyToast {

    private static MyToast instance;
    private Toast mToast = null;
    private View mToastView;
    private TextView mText;
    private ImageView mImageView;

    private MyToast() {
        mToastView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_layout, null);
        mText = (TextView) mToastView.findViewById(R.id.message);
        mImageView = (ImageView) mToastView.findViewById(R.id.icon);
    }

    public static MyToast getInstance() {
        if (instance == null) {
            synchronized (MyToast.class) {
                if (instance == null) {
                    instance = new MyToast();
                }
            }
        }
        return instance;
    }

    private Handler mHandler = new Handler();
    private Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast = null;
        }
    };


    public void showShortWarn(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.warn);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 2000);
        mToast.show();
    }

    public void showLongMessage(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.message);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 3500);
        mToast.show();
    }

    public void showShortDone(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.done);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 2000);
        mToast.show();
    }


    public void showBottomShortDone(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.done);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.BOTTOM, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 2000);
        mToast.show();
    }

    public void showLongDone(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.done);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 3500);
        mToast.show();
    }

    public void showBottomShortWrong(Context context, String tvString) {
        if (mToast != null) {
            return;
        }
        mImageView.setBackgroundResource(R.mipmap.wrong);
        mText.setText(tvString);
        mToast = new Toast(context.getApplicationContext());
        mToast.setGravity(Gravity.BOTTOM, 0, 0);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(mToastView);
        mHandler.postDelayed(r, 2000);
        mToast.show();
    }

}
