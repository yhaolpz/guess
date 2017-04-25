package com.example.asus.common;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.example.asus.activity.R;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.greendao.DaoMaster;
import com.example.asus.greendao.DaoSession;
import com.example.asus.util.SPUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.zhy.changeskin.SkinManager;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2016/12/29.
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

    public void clearUser() {
        this.user = null;
    }

    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }


    public void changeMusic(String music) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (TextUtils.equals(music, "关闭")) {
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 3);
            return;
        }
        if (TextUtils.equals(music, "喜剧之王")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.here_again);
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 0);
        }
        if (TextUtils.equals(music, "权利的游戏")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.main_titles);
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 1);
        }
        if (TextUtils.equals(music, "电锯惊魂")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hello_zepp);
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 2);
        }
        mediaPlayer.setLooping(true);
    }


    public void changeSkin(String editSkin) {
        SkinManager.getInstance().changeSkin(editSkin);
        SPUtil.put(this, MyConstants.SKIN_SET_SP_KEY, editSkin);
    }

    public void changeNum(String editNum) {
        SPUtil.put(this, MyConstants.MOVIE_NUM_SET_SP_KEY, Integer.parseInt(editNum));
    }

    public static BaseApplication instances;

    public static BaseApplication getInstances() {
        return instances;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        instances = this;
        initBmob();
        initMsc();
        initSetting();
        initMusic();
        initLogger();
        SkinManager.getInstance().init(this);
        setDatabase();
    }

    private void initLogger() {
        Logger
                .init("guess")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .methodOffset(2);             // default 0
    }

    private void initMusic() {
        int m = (int) SPUtil.get(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 1);
        changeMusic(MyConstants.musics[m]);
    }

    private void initMsc() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=586fb159");
    }

    private void initSetting() {
        Boolean firstIn = (Boolean) SPUtil.get(this, MyConstants.IS_FIRST_IN_APP_SET_SP_KEY, true);
        if (firstIn) {
            SPUtil.put(this, MyConstants.IS_FIRST_IN_APP_SET_SP_KEY, false);
            SPUtil.put(this, MyConstants.MOVIE_NUM_SET_SP_KEY, 3); //默认单人关卡题数 3
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 1);//默认权利的游戏
            SPUtil.put(this, MyConstants.SKIN_SET_SP_KEY, MyConstants.skins[0]);//默认 defalt 背景
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


    /**
     * 设置greenDao
     */
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
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
