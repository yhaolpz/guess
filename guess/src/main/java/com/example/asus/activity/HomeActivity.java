package com.example.asus.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.view.CircleImageView;
import com.example.asus.view.SlidingMenu;
import com.zhy.changeskin.SkinManager;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, SlidingMenu.OnMenuToggleListener {

    private SlidingMenu mSlidingMenu;
    private RelativeLayout mContentLayout;
    private CircleImageView mAvatar;
    private GifImageView mHomeGif;
    private GifDrawable mGifDrawable;
    private CircleImageView mAvatarBigger;
    private TextView mName;
    private BaseApplication mApplication;
    public static boolean IS_UPDATE_AVATAR_FLAG = false;
    private User mCurrentUser;
    private int requestCode_login = 1;
    private Button mLogoutBt;

    //TODO 内存泄漏
    //TODO 启动引导页
    //TODO 设置界面添加应用版本信息,帮助，反馈
    //TODO 收费开通 sexy类别
    //TODO 删掉系统级progressBar，换一个体验友好的进度条
    //TODO 所有出错地方都及时给提示，不要杳无音信~
    //TODO bgm是不是有点吵？
    //TODO bug :MovieTypeActivity : errorCode:10076,errorMsg:Qps beyond the limit: 10.%!(EXTRA int=11, string=111.143.57.41)
    //TODO bug :E/guess: OnlinePlayActivity : errorCode:502,errorMsg:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_home);
        mApplication = (BaseApplication) getApplication();
        initUser();
        initContentView();
        initMenuView();
        mApplication.startMusic();
    }


    private void initUser() {
        if (UserDAO.alreadyLogin(this)) {
            mCurrentUser = UserDAO.getUserFromSP(this);
            mApplication.setUser(mCurrentUser);
        } else {
            mCurrentUser = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCode_login && resultCode == LoginActivity.resultCode_login) {
            mCurrentUser = mApplication.getUser();
            if (mCurrentUser.getAvatar() != null) {
                Glide.with(this).load(mCurrentUser.getAvatar().getUrl()).into(mAvatarBigger);
                Glide.with(this).load(mCurrentUser.getAvatar().getUrl()).into(mAvatar);
            }
            mName.setText(mCurrentUser.getName());
            mLogoutBt.setText("退出");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onOpen() {
        mGifDrawable.stop();
    }

    @Override
    public void onClose() {
        mGifDrawable.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return !mSlidingMenu.isOpen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = mApplication.getUser();
        if (mCurrentUser != null) {
            mName.setText(mCurrentUser.getName());
        }
        logd("IS_UPDATE_AVATAR_FLAG:" + IS_UPDATE_AVATAR_FLAG);
        if (IS_UPDATE_AVATAR_FLAG) {
            Glide.with(this).load(mCurrentUser.getAvatar() == null ?
                    R.mipmap.avatar : mCurrentUser.getAvatar().getUrl()).into(mAvatarBigger);
            Glide.with(this).load(mCurrentUser.getAvatar() == null ?
                    R.mipmap.avatar : mCurrentUser.getAvatar().getUrl()).into(mAvatar);
            IS_UPDATE_AVATAR_FLAG = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logd("onDestroy");
        mGifDrawable.recycle();
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mAvatar) {
            mSlidingMenu.toggle();
        } else if (view == mContentLayout) {
            if (mSlidingMenu.isOpen()) {
                mSlidingMenu.closeMenu();
            }
        } else if (view == mName) {
            if (isLogin()) {
                Intent intent = new Intent(this, PersonalDataActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }
    }

    private void initMenuView() {
        mAvatarBigger = (CircleImageView) findViewById(R.id.avatar_bigger);
        mName = (TextView) findViewById(R.id.name);
        mLogoutBt = (Button) findViewById(R.id.logoutBt);
        mName.setOnClickListener(this);
        if (mCurrentUser != null) {
            mName.setText(mCurrentUser.getName());
            Glide.with(this).load(mCurrentUser.getAvatar() == null ?
                    R.mipmap.avatar : mCurrentUser.getAvatar().getUrl()).into(mAvatarBigger);
        } else {
            Glide.with(this).load(R.mipmap.avatar).into(mAvatarBigger);
            mName.setText("未登录");
            mLogoutBt.setText("登录");
        }
    }

    private void initContentView() {
        mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        mAvatar = (CircleImageView) findViewById(R.id.avatar);
        mContentLayout = (RelativeLayout) findViewById(R.id.contentLayout);
        mHomeGif = (GifImageView) findViewById(R.id.homeGif);
        try {
            mGifDrawable = new GifDrawable(getResources(), R.drawable.walk);
            mHomeGif.setImageDrawable(mGifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAvatar.setOnClickListener(this);
        mContentLayout.setOnClickListener(this);
        mSlidingMenu.setOnTouchListener(this);
        mSlidingMenu.setOnMenuToggleListener(this);
        if (mCurrentUser != null && mCurrentUser.getAvatar() != null) {
            Glide.with(this).load(mCurrentUser.getAvatar().getUrl()).into(mAvatar);
        } else {
            Glide.with(this).load(R.mipmap.avatar).into(mAvatar);
        }
        playWalkGifAnim();
    }

    public void singleMode(View view) {
        playScaleAnim(view);
        Intent intent = new Intent(this, MovieTypeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void onLineMode(View view) {
        playScaleAnim(view);
        if (isLogin()) {
            Intent intent = new Intent(this, OnlineMatchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    public void ranking(View view) {
        playScaleAnim(view);
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra(SelectActivity.MODE, SelectActivity.MODE_RANK);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void my_score(View view) {
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra(SelectActivity.MODE, SelectActivity.MODE_SCORE);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void personal_data(View view) {
        if (isLogin()) {
            Intent intent = new Intent(this, PersonalDataActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }


    public void setting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void logout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestCode_login);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        if (mCurrentUser != null) {
            User.logOut();
            mApplication.clearUser();
            UserDAO.removeUser(this);
            mCurrentUser = null;
            mAvatar.setImageResource(R.mipmap.avatar);
            mAvatarBigger.setImageResource(R.mipmap.avatar);
            mName.setText("未登录");
            mLogoutBt.setText("登录");
        }
    }

    private void playWalkGifAnim() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mHomeGif, "scaleX", 0.3f, 0.6f).setDuration(10000);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mHomeGif, "scaleY", 0.3f, 0.6f).setDuration(10000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }

    private boolean isLogin() {
        if (mCurrentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, requestCode_login);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return false;
        }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //退出确认框
            View dialogView = View.inflate(this, R.layout.dialog_exit_confirm, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
            dialog.setView(dialogView, 0, 0, 0, 0);
            TextView mText = (TextView) dialogView.findViewById(R.id.text);
            TextView mExit = (TextView) dialogView.findViewById(R.id.exit);
            TextView mCancel = (TextView) dialogView.findViewById(R.id.cancel);
            mText.setText("确定退出游戏吗");
            mExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更新服务端数据,更新完后退出
                    HomeActivity.this.finish();

                }
            });
            final Dialog chooseDialog = dialog.show();
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseDialog.dismiss();
                }
            });
            WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            chooseDialog.getWindow().setAttributes(lp);
            ObjectAnimator.ofFloat(dialogView, "alpha", 0, 1).setDuration(500).start();
            return true;
        }
        return false;
    }

    public  void playScaleAnim(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f).setDuration(100);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f).setDuration(100);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }
}
