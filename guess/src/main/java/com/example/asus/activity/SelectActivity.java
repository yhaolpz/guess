package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.example.asus.common.MySwipeBackActivity;
import com.zhy.changeskin.SkinManager;

public class SelectActivity extends MySwipeBackActivity {
    private LinearLayout mLine1;
    private LinearLayout mLine2;

    public static final String MODE_RANK = "rank";
    public static final String MODE_SCORE = "score";
    public static final String MODE = "mode";

    private String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_select);
        mLine1 = (LinearLayout) findViewById(R.id.line1);
        mLine2 = (LinearLayout) findViewById(R.id.line2);
        mode = getIntent().getStringExtra(MODE);
        loge("onCreate mode=" + mode);
        mLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, RankActivity.class);
                intent.putExtra(MODE, mode);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        mLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (TextUtils.equals(mode, MODE_RANK)) {
                    intent = new Intent(SelectActivity.this, RankTypeActivity.class);
                } else {
                    //TODO 双人模式本地记录表
                    intent = new Intent(SelectActivity.this, RankTypeActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
