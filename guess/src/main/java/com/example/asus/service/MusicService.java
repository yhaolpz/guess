package com.example.asus.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.asus.activity.R;

/**
 * Created by yhao on 2017/4/16.
 *
 *
 *
 */

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    private MediaPlayer mediaPlayer;

    /**
     *   以绑定形式使用此Service 获取Service实例
     */
    private final IBinder mBinder = new LocalBinder();
    public class  LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        mediaPlayer = MediaPlayer.create(this, R.raw.main_titles);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        return mBinder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart: ");
    }
}
