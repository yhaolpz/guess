package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.asus.common.MySwipeBackActivity;
import com.zhy.changeskin.SkinManager;

public class RankActivity extends MySwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_rank);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

    public void clickType(View view) {
        String type = view.getTag().toString();
        Intent intent = new Intent(this, RankTypeActivity.class);
        intent.putExtra("TYPE", type);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
