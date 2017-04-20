package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.asus.common.MySwipeBackActivity;
import com.zhy.changeskin.SkinManager;

public class RankActivity extends MySwipeBackActivity {

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_rank);
        mode = getIntent().getStringExtra(SelectActivity.MODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    public void clickType(View view) {
        String type = view.getTag().toString();
        Intent intent = null;
        if (TextUtils.equals(mode, SelectActivity.MODE_RANK)) {
            intent = new Intent(this, RankTypeActivity.class);
        }
        if (TextUtils.equals(mode, SelectActivity.MODE_SCORE)) {
            intent = new Intent(this, ScoreTypeActivity.class);
        }
        intent.putExtra("TYPE", type);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
