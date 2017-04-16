package com.example.asus.common;

import android.app.Application;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.asus.activity.R;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.util.SPUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2016/12/29.
 *
 */

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static final String BMOB_APPID = "6651c9bc691b3dd33c7e653179961e28";

    private MediaPlayer mediaPlayer;

    //bmob账号
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        Log.i(TAG, "setUser: " + user.toString());
        UserDAO.saveUserToSP(this, user);
    }

    public void saveAvatar(BmobFile avatar) {
        this.user.setAvatar(avatar);
        UserDAO.saveUserAvatarToSP(this, avatar);
    }

    public void clearUser(){
        this.user = null;
    }

    public void startMusic() {
        mediaPlayer.start();
    }

    public void stopMusic() {
        mediaPlayer.pause();
    }

    //QQ账号
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        initBmob();
        initMsc();
        initSetting();
        initMusic();
    }

    private void initMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.main_titles);
        mediaPlayer.setLooping(true);
    }

    private void initMsc() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=586fb159");
    }

    private void initSetting() {
        Boolean firstIn = (Boolean) SPUtil.get(this, MyConstants.IS_FIRST_IN_APP_SET_SP_KEY, true);
        if (firstIn) {
//            SPUtil.put(this, MyConstants.IS_FIRST_IN_APP_SET_SP_KEY, false);
            SPUtil.put(this, MyConstants.MOVIE_NUM_SET_SP_KEY, 3);
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, true);
        } else {
        }
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate: ");
        super.onTerminate();
    }


    private void initBmob() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId(BMOB_APPID)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(6)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }


    private List<AppCompatActivity> activityList = new LinkedList<AppCompatActivity>();

    public void addActivity(AppCompatActivity activity) {
        activityList.add(activity);
    }

    public void finishActivity() {
        for (AppCompatActivity activity : activityList) {
            activityList.remove(activity);
            activity.finish();
        }
    }
}
