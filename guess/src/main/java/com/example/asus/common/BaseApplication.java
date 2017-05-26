package com.example.asus.common;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.example.asus.activity.R;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.greendao.DaoMaster;
import com.example.asus.greendao.DaoSession;
import com.example.asus.util.SPUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
//import com.squareup.leakcanary.LeakCanary;
import com.tencent.tauth.Tencent;
import com.zhy.changeskin.SkinManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2016/12/29.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        initBmob();
        initWeibo();
        initMsc();
        initSetting();
        initMusic();
        SkinManager.getInstance().init(this);
        setDatabase();
        initLeakCanary();
    }

    private MediaPlayer mediaPlayer;

    //qq   登录界面和游戏界面分享用
    Tencent mTencent;

    public Tencent getTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(MyConstants.QQ_APPID, this);
        }
        return mTencent;
    }

    //bmob账号
    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 2);
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


    private void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }

    private void initWeibo() {
        WbSdk.install(this, new AuthInfo(this, MyConstants.WEIBO_APP_KEY, MyConstants.WEIBO_REDIRECT_URL, MyConstants.WEIBO_SCOPE));
    }


    private void initMusic() {
        int m = (int) SPUtil.get(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 2);
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
            SPUtil.put(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 2);//默认关闭
            SPUtil.put(this, MyConstants.SKIN_SET_SP_KEY, MyConstants.skins[0]);//默认 defalt 背景
        }
    }


    private void initBmob() {
        String BMOB_APPID = "6651c9bc691b3dd33c7e653179961e28";
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


    private DaoSession mDaoSession;

    private void setDatabase() {
        /*
      设置greenDao
     */
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
