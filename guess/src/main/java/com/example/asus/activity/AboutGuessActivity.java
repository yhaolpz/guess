package com.example.asus.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.util.AppUtil;
import com.zhy.changeskin.SkinManager;

public class AboutGuessActivity extends MySwipeBackActivity {
    private TextView mVersionTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_about_guess);
        initView();
    }

    private void initView() {
        mVersionTv = (TextView) findViewById(R.id.version_tv);
        mVersionTv.setText("v"+AppUtil.getVersionName(this));
    }

    public void feedBack(View view) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
