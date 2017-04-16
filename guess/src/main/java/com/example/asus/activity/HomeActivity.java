package com.example.asus.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus.Image.ImageManager;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.service.MusicService;
import com.example.asus.util.AnimUtil;
import com.example.asus.view.CircleImageView;
import com.example.asus.view.SlidingMenu;
import com.zhy.changeskin.SkinManager;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        mApplication = (BaseApplication) getApplication();
        logd("onCreate");
        setContentView(R.layout.activity_home);
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
            ImageManager.getInstance().disPlay(mAvatarBigger, mCurrentUser.getAvatar());
            ImageManager.getInstance().disPlay(mAvatar, mCurrentUser.getAvatar());
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
        logd("IS_UPDATE_AVATAR_FLAG:" + IS_UPDATE_AVATAR_FLAG);
        if (IS_UPDATE_AVATAR_FLAG) {
            ImageManager.getInstance().disPlay(mAvatar, mApplication.getUser().getAvatar());
            ImageManager.getInstance().disPlay(mAvatarBigger, mApplication.getUser().getAvatar());
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
            ImageManager.getInstance().disPlay(mAvatarBigger, mCurrentUser.getAvatar());
            mName.setText(mCurrentUser.getName());
        } else {
            mAvatarBigger.setImageResource(R.mipmap.avatar);
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
        if (mCurrentUser != null) {
            ImageManager.getInstance().disPlay(mAvatar, mCurrentUser.getAvatar());
        } else {
            mAvatar.setImageResource(R.mipmap.avatar);
        }
        playWalkGifAnim();
    }

    public void singleMode(View view) {
        AnimUtil.playScaleAnim(view);
        Intent intent = new Intent(this, MovieTypeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void onLineMode(View view) {
        AnimUtil.playScaleAnim(view);
        if (isLogin()) {
            Intent intent = new Intent(this, OnlineMatchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    public void ranking(View view) {
        AnimUtil.playScaleAnim(view);
    }

    public void personal_data(View view) {
        if (isLogin()) {
            Intent intent = new Intent(this, PersonalDataActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    public void my_score(View view) {

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

}
