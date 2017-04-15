package com.example.asus.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.asus.bmobbean.User;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MySwipeBackActivity;

public class EditPersonalDataActivity extends MySwipeBackActivity {
    private TextView mName;
    private TextView mEmail;
    private TextView mSex;
    private TextView mAge;
    private TextView mCity;
    private BaseApplication mApplication;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_data);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        initView();
    }

    private void initView() {
        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mSex = (TextView) findViewById(R.id.sex);
        mAge = (TextView) findViewById(R.id.age);
        mCity = (TextView) findViewById(R.id.city);
        mName.setText(mCurrentUser.getName());
        mCity.setText(mCurrentUser.getCity());
        mAge.setText(mCurrentUser.getAge() == null ? "" : mCurrentUser.getAge() + "岁");
        mEmail.setText(mCurrentUser.getEmail());
        mSex.setText(mCurrentUser.getSex());
    }
}
