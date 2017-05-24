package com.example.asus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.util.TimeUtil;
import com.example.asus.util.ValidateUtil;
import com.zhy.changeskin.SkinManager;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class FindPassActivity extends MySwipeBackActivity {
    private EditText mUsername;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_find_pass);
        mUsername = (EditText) findViewById(R.id.username);
        mSendButton = (Button) findViewById(R.id.sendBt);
    }

    public void send(final View view) {
        if (TextUtils.isEmpty(mUsername.getText())) {
            MyToast.getInstance().showShortWarn(this, "请输入邮箱");
        } else if (!ValidateUtil.checkEmail(mUsername.getText().toString())) {
            MyToast.getInstance().showShortWarn(this, "邮箱格式错误");
        } else {
            final String email = mUsername.getText().toString();
            showProgressbar();
            BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    hideProgressbar();
                    if (e == null) {
                        MyToast.getInstance().showLongDone(FindPassActivity.this, "请到邮箱进行密码重置操作");
                        new TimeUtil(mSendButton, mSendButton.getText().toString()).RunTimer();
                    } else if (e.getErrorCode() == ERROR_CODE_NO_USER_FOUND) {
                        MyToast.getInstance().showShortWarn(FindPassActivity.this, "此用户未注册");
                    } else {
                        checkCommonException(e, FindPassActivity.this);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
