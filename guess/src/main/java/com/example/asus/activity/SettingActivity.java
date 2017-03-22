package com.example.asus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.bmobbean.movieInfo;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.MySwipeBackActivity;

import java.util.Arrays;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SettingActivity extends MySwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        movieInfo movieInfo = new movieInfo();
        movieInfo.addAll("types", Arrays.asList("剧情", "惊悚"));
        movieInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    logd("bmob 保存成功");
                }else{
                    logd("bmob 保存失败："+e.getMessage());
                }
            }
        });

    }
}
