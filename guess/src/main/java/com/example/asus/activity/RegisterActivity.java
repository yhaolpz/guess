package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.asus.bmobbean.User;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.util.ValidateUtil;
import com.zhy.changeskin.SkinManager;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RegisterActivity extends MySwipeBackActivity implements View.OnClickListener {

    private EditText mUsername;
    private EditText mName;
    private EditText mPassword;

    public static RegisterActivity registerActivity = null;

    private SwipeBackLayout mSwipeBackLayout;
    private RadioGroup mTrackingModeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_register);
        mUsername = (EditText) findViewById(R.id.username);
        mName = (EditText) findViewById(R.id.name);
        mPassword = (EditText) findViewById(R.id.pasword);
        registerActivity = this;
    }

    public void commit(View view) {
        if (TextUtils.isEmpty(mUsername.getText())) {
            MyToast.getInstance().showShortWarn(this, "请输入邮箱");
        } else if (!ValidateUtil.checkEmail(mUsername.getText().toString())) {
            MyToast.getInstance().showShortWarn(this, "邮箱格式错误");
        } else if (TextUtils.isEmpty(mName.getText())) {
            MyToast.getInstance().showShortWarn(this, "请输入昵称");
        } else if (TextUtils.isEmpty(mPassword.getText())) {
            MyToast.getInstance().showShortWarn(this, "请输入密码");
        } else if (mPassword.getText().toString().length() < 6) {
            MyToast.getInstance().showShortWarn(this, "密码最少为六位");
        } else {
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("username", mUsername.getText().toString());
            showProgressbar();
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    hideProgressbar();
                    if (e == null) {
                        if (list.size() > 0) {
                            MyToast.getInstance().showShortWarn(RegisterActivity.this, "该邮箱已经注册");
                        } else {
                            Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                            intent.putExtra("USERNAME", mUsername.getText().toString());
                            intent.putExtra("PASSWORD", mPassword.getText().toString());
                            intent.putExtra("NAME", mName.getText().toString());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }
                    } else {
                        checkCommonException(e, RegisterActivity.this);
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View view) {

    }
}
