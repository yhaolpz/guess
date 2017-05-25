package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.example.asus.common.BaseApplication;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.zhy.changeskin.SkinManager;

public class SelectActivity extends MySwipeBackActivity {

    public static final String MODE_RANK = "rank";
    public static final String MODE_SCORE = "score";
    public static final String MODE = "mode";

    private String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_select);
        LinearLayout line1 = (LinearLayout) findViewById(R.id.line1);
        LinearLayout line2 = (LinearLayout) findViewById(R.id.line2);
        mode = getIntent().getStringExtra(MODE);
        line1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //单人模式
                Intent intent = new Intent(SelectActivity.this, RankActivity.class);
                intent.putExtra(MODE, mode);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        line2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //双人模式
                Intent intent;
                if (TextUtils.equals(mode, MODE_RANK)) {
                    //双人模式排行榜
                    intent = new Intent(SelectActivity.this, DoubleRankTypeActivity.class);
                } else {
                    //双人模式本地记录
                    if (((BaseApplication) getApplication()).getUser() == null) {
                        MyToast.getInstance().showShortWarn(SelectActivity.this,"请先登录");
                        return;
                    }
                    intent = new Intent(SelectActivity.this, DoubleRecordTypeActivity.class);
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
