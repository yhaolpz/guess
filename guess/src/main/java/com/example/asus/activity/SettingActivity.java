package com.example.asus.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.movieInfo;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.util.GlideCacheUtil;
import com.example.asus.util.SPUtil;
import com.example.asus.view.PickerView;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingActivity extends MySwipeBackActivity {
    private TextView mMusic;
    private TextView mSkin;
    private TextView mMovieNum;
    private TextView mCache;

    private String editMusic;
    private String editSkin;
    private String editNum;

    private List<String> musicItemList;
    private List<String> skinItemList;
    private List<String> numItemList;

    private BaseApplication mApplication;

    private GlideCacheUtil mGlideCacheUtil = new GlideCacheUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_setting);
        initData();
        initView();
    }

    private void initView() {
        mMusic = (TextView) findViewById(R.id.music);
        mSkin = (TextView) findViewById(R.id.skin);
        mMovieNum = (TextView) findViewById(R.id.movieNum);
        mCache = (TextView) findViewById(R.id.cacheSize);
        int m = (int) SPUtil.get(this, MyConstants.PLAY_MUSIC_SET_SP_KEY, 1);
        String n = (String) SPUtil.get(this, MyConstants.SKIN_SET_SP_KEY, MyConstants.skins[0]);
        int num = (int) SPUtil.get(this, MyConstants.MOVIE_NUM_SET_SP_KEY, 3);
        mMusic.setText(MyConstants.musics[m]);
        mSkin.setText(n);
        mMovieNum.setText(num + "");
        mCache.setText(mGlideCacheUtil.getCacheSize(this));
    }

    private void initData() {
        mApplication = (BaseApplication) getApplication();
        musicItemList = new ArrayList<>();
        skinItemList = new ArrayList<>();
        numItemList = new ArrayList<>();
        musicItemList.addAll(Arrays.asList(MyConstants.musics));
        skinItemList.addAll(Arrays.asList(MyConstants.skins));
        numItemList.addAll(Arrays.asList(MyConstants.nums));
    }

    public void editSkin(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_sex, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(skinItemList);
        editSkin = mSkin.getText().toString();
        wheelView.setSelected(editSkin);
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                editSkin = text;
            }
        });
        chooseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!TextUtils.equals(editSkin, mSkin.getText())) {
                    mApplication.changeSkin(editSkin);
                    mSkin.setText(editSkin);
                }
            }
        });
    }

    public void editMusic(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_sex, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(musicItemList);
        editMusic = mMusic.getText().toString();
        wheelView.setSelected(editMusic);
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                editMusic = text;
            }
        });
        chooseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!TextUtils.equals(editMusic, mMusic.getText())) {
                    mApplication.changeMusic(editMusic);
                    mApplication.startMusic();
                    mMusic.setText(editMusic);
                }
            }
        });
    }

    public void editNum(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_sex, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(numItemList);
        editNum = mMovieNum.getText().toString();
        wheelView.setSelected(editNum);
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                editNum = text;
            }
        });
        chooseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!TextUtils.equals(editNum, mMovieNum.getText())) {
                    mApplication.changeNum(editNum);
                    mMovieNum.setText(editNum);
                }
            }
        });
    }


    public void cleanCache(View view) {
        mGlideCacheUtil.clearImageDiskCache(this, new GlideCacheUtil.clearListener() {
            @Override
            public void done() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCache.setText(mGlideCacheUtil.getCacheSize(SettingActivity.this));
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
