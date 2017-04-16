package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.asus.bmobbean.movieInfo;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.util.RandomUtil;
import com.example.asus.util.SPUtil;
import com.example.asus.view.PickerView;
import com.zhy.changeskin.SkinManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class MovieTypeActivity extends MySwipeBackActivity {
    private PickerView mMovieTypePicker;
    private PickerView mMovieDifficultPicker;

    private String mMovieType = "随意";
    private String mDifficult = "一般";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_movie_type);
        initView();
    }

    private void initView() {
        mMovieTypePicker = (PickerView) findViewById(R.id.movieTypePicker);
        mMovieDifficultPicker = (PickerView) findViewById(R.id.movieDifficultPicker);
        mMovieTypePicker.setData(new ArrayList<String>(Arrays.asList(MyConstants.movieTypes)));
        mMovieTypePicker.setSelected(MyConstants.movieTypes[0]);
        mMovieTypePicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mMovieType = text;
            }
        });
        mMovieDifficultPicker.setData(new ArrayList<String>(Arrays.asList(MyConstants.difficults)));
        mMovieDifficultPicker.setSelected(MyConstants.movieTypes[1]);
        mMovieDifficultPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mDifficult = text;
            }
        });

    }

    public void match(View view) {
        final int num = (int) SPUtil.get(this, MyConstants.MOVIE_NUM_SET_SP_KEY, 3);
        BmobQuery<movieInfo> query = new BmobQuery<movieInfo>();
        if (mMovieType.equals("随意")) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
            showProgressbar();
            query.count(movieInfo.class, new CountListener() {
                @Override
                public void done(Integer count, BmobException e) {
                    if (e == null) {
                        logd(mMovieType + "  size " + count);
                        final List<Integer> skipNums = RandomUtil.getRandomNums(num, count);
                        final List<movieInfo> chooseMovies = new ArrayList<>();
                        for (int skipNum : skipNums) {
                            BmobQuery<movieInfo> query = new BmobQuery<movieInfo>();
                            query.setLimit(1);
                            query.setSkip(skipNum);
                            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
                            query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
                            query.findObjects(new FindListener<movieInfo>() {
                                @Override
                                public void done(List<movieInfo> list, BmobException e) {
                                    if (e == null) {
                                        for (int i = 0; i < list.size(); i++) {
                                            logd(list.get(i).toString());
                                        }
                                        chooseMovies.add(list.get(0));
                                        if (chooseMovies.size() == skipNums.size()) {
                                            play(chooseMovies);
                                        }
                                    } else {
                                        checkCommonException(e, MovieTypeActivity.this);
                                    }
                                    hideProgressbar();
                                }
                            });
                        }
                    } else {
                        checkCommonException(e, MovieTypeActivity.this);
                    }
                    hideProgressbar();
                }
            });
        } else {
            String[] types = {mMovieType};
            query.addWhereContainsAll("types", Arrays.asList(types));
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));// 每30分钟从后台更新一次电影数据
            boolean isCache = query.hasCachedResult(movieInfo.class);
            logd(" type:" + mMovieType + "  isCache = " + isCache);
            showProgressbar();
            query.findObjects(new FindListener<movieInfo>() {
                @Override
                public void done(List<movieInfo> list, BmobException e) {
                    if (e == null) {
                        logd("download movieInfo list size" + list.size());
                        if (list.size() >= 3) {
                            play(chooseMovie(list, num));
                        } else {
                            MyToast.getInstance().showShortWarn(MovieTypeActivity.this, "没有此类型的电影");
                        }
                    } else {
                        checkCommonException(e, MovieTypeActivity.this);
                    }
                    hideProgressbar();
                }
            });
        }
    }

    private List<movieInfo> chooseMovie(List<movieInfo> list, int num) {
        hideProgressbar();
        Collections.shuffle(list);
        List<movieInfo> chooseMovies = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            chooseMovies.add(list.get(i));
        }
        return chooseMovies;
    }

    private void play(List<movieInfo> chooseMovies) {
        Intent intent = new Intent(MovieTypeActivity.this, SinglePlayActivity.class);
        intent.putExtra("LIST", (Serializable) chooseMovies);
        intent.putExtra("DIFFICULT", mDifficult);
        intent.putExtra("TYPE", mMovieType);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
