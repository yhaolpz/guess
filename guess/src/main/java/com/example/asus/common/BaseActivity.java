package com.example.asus.common;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.asus.activity.R;

import java.io.IOException;

import cn.bmob.v3.exception.BmobException;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yinghao on 2016/12/29.
 * Email：756232212@qq.com
 */

public class BaseActivity extends FragmentActivity {


    //bmob错误码
    public static final int ERROR_CODE_USERNAME_OR_PASSWORD_ERROR = 101;
    public static final int ERROR_CODE_NO_USER_FOUND = 205;
    public static final int ERROR_CODE_NETWORK_TIME_OUT = 9010;
    public static final int ERROR_CODE_UNKNOW_ERROR = 9015; //连接未认证wifi时出现
    public static final int ERROR_CODE_NETWORK_NOT_AVAILABLE = 9016;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (progressShowing()) {
                hideProgressbar();
                return true;
            }
            finish();
        }
        return false;
    }

    public void logd(String s) {
        Log.d("guess", getClass().getSimpleName() + " : " + s);
    }

    public void loge(String s) {
        Log.e("guess", getClass().getSimpleName() + " : " + s);

    }

    private void initProgressDialogAndShow() {
        View dialogView = View.inflate(this, R.layout.gifview_wait, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 0, 0, 0, 0);
        progressText = (TextView) dialogView.findViewById(R.id.text);
        GifImageView mGifView = (GifImageView) dialogView.findViewById(R.id.gifView);
        try {
            GifDrawable gifDrawable = new GifDrawable(getApplicationContext().getResources(), R.drawable.loading2);
            mGifView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog = dialog.show();
        WindowManager.LayoutParams lp = progressDialog.getWindow().getAttributes();
        progressDialog.setCanceledOnTouchOutside(false);
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        progressDialog.getWindow().setAttributes(lp);
    }

    Dialog progressDialog;
    TextView progressText;

    public void showProgressbar() {
        if (progressDialog == null) {
            initProgressDialogAndShow();
        } else {
            progressDialog.show();
        }
        hideSoftInput();
    }

    public void showProgressbarWithText(String text) {
        if (progressDialog == null) {
            initProgressDialogAndShow();
        } else {
            progressDialog.show();
        }
        progressText.setText(text);
        hideSoftInput();
    }

    public void hideProgressbar() {
        if (progressShowing()) {
            progressDialog.hide();
        }
    }

    public boolean progressShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }


    public boolean checkCommonException(BmobException e, Context context) {
        if (e == null) {
            return false;
        }
        if (e.getErrorCode() == ERROR_CODE_NETWORK_NOT_AVAILABLE) {
            MyToast.getInstance().showShortWarn(context, "无网络连接，请检查您的手机网络");
        } else if (e.getErrorCode() == ERROR_CODE_NETWORK_TIME_OUT) {
            MyToast.getInstance().showShortWarn(context, "请求网络超时，请检查您的手机网络");
        } else if (e.getErrorCode() == ERROR_CODE_UNKNOW_ERROR) {
            loge(e.toString());
            MyToast.getInstance().showShortWarn(context, "网络异常");
        } else {
            loge(e.toString());
        }
        return true;
    }


    @Override
    public void finish() {
        super.finish();
        if (!getClass().getSimpleName().equals("HomeActivity")) {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
        }
    }

    void hideSoftInput() {
        final View v = this.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
