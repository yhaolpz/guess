package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.example.asus.bmobbean.MatchItem;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.movieInfo;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MyToast;
import com.example.asus.util.RandomUtil;
import com.example.asus.view.PickerView;
import com.google.gson.Gson;
import com.zhy.changeskin.SkinManager;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by yhao on 2017/1/24.
 * <p>
 * 1. 先寻找是否有look用户
 * <p>
 * 2. 有则：
 * <p>
 * 获取skips 更新look用户state为saw
 * <p>
 * 添加自己item，state初始化为saw
 * <p>
 * 更新对手用户targetId、targetUsername
 * <p>
 * 获取movieInfo
 * <p>
 * 开始
 * <p>
 * <p>
 * 2. 无则：
 * <p>
 * 获取电影信息skip（随意skips / 其他则movieInfo）
 * <p>
 * 添加自己item，state初始化为look（期间有人匹配则同样进入此步骤）
 * <p>
 * 监听自己item
 * <p>
 * 自己item被更新为saw (被找到)
 * <p>
 * 自己item被更新targetId、targetUsername
 * <p>
 * 若为随意则获取movieInfo
 * <p>
 * 开始
 */

public class OnlineMatchActivity extends BaseActivity {
    private String mMovieType = "随意";
    private String mDifficult = "一般";
    private User mCurrentUser;
    private BmobRealTimeData rtd;
    private String my_objectId;
    private String target_objectId;
    private String target_userId;
    final int num = 3;
    List<Integer> mSkips = new ArrayList<>();
    List<movieInfo> mMovieInfoList = new ArrayList<>();
    private boolean cancelFlag = false;//能否取消匹配


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_movie_type);
        mCurrentUser = ((BaseApplication) getApplication()).getUser();
        initView();
    }

    private void initBmobRealTimeData() {
        rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                //被更新两次
                //第一次被更新saw
                //第二次被更新targetId targetUsername
                Gson gson = new Gson();
                MatchItem item = gson.fromJson(data.optString("data"), MatchItem.class);
                //data中数据可能不是最新数据，故重新查询
                BmobQuery<MatchItem> query = new BmobQuery();
                query.getObject(item.getObjectId(), new QueryListener<MatchItem>() {
                    @Override
                    public void done(MatchItem matchItem, BmobException e) {
                        if (checkCommonException(e, OnlineMatchActivity.this)) {
                            return;
                        }
                        logd("数据被更新后，重新获取为：" + matchItem.toString());
                        if (matchItem.getTargetID() == null) {
                            showProgressbarWithText("找到对手...");
                            cancelFlag = false;
                        } else {
                            logd("取消监听自己：" + my_objectId);
                            rtd.unsubRowUpdate("MatchItem", my_objectId);
                            target_objectId = matchItem.getTargetID();
                            target_userId = matchItem.getTargetUserId();
                            if (mMovieType.equals("随意")) {
                                addRandomMovieInfo();
                            }
                        }
                    }
                });
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                logd("onConnectCompleted:" + rtd.isConnected());
                rtd.subRowUpdate("MatchItem", my_objectId);
            }
        });
    }

    private void initView() {
        PickerView movieTypePicker = (PickerView) findViewById(R.id.movieTypePicker);
        PickerView movieDifficultPicker = (PickerView) findViewById(R.id.movieDifficultPicker);
        movieTypePicker.setData(new ArrayList<>(Arrays.asList(MyConstants.movieTypes)));
        movieTypePicker.setSelected(MyConstants.movieTypes[0]);
        movieTypePicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mMovieType = text;
            }
        });
        movieDifficultPicker.setData(new ArrayList<>(Arrays.asList(MyConstants.difficults)));
        movieDifficultPicker.setSelected(MyConstants.movieTypes[1]);
        movieDifficultPicker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mDifficult = text;
            }
        });

    }

    public void match(View view) {
        if (progressShowing()) {
            return;
        }
        showProgressbarWithText("正在匹配");
        BmobQuery<MatchItem> query = new BmobQuery<>();
        query.addWhereEqualTo("state", MyConstants.LOOK_STATE);
        query.addWhereEqualTo("movieType", mMovieType);
        query.addWhereEqualTo("difficult", mDifficult);
        query.setLimit(1);
        query.findObjects(mItemFindListener);
    }

    /**
     * 寻找look用户
     */
    private FindListener<MatchItem> mItemFindListener = new FindListener<MatchItem>() {
        @Override
        public void done(List<MatchItem> list, BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            if (list.size() == 0) {
                BmobQuery<movieInfo> query = new BmobQuery<>();
                if (mMovieType.equals("随意")) {
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
                    query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
                    query.count(movieInfo.class, mCountListener);
                } else {
                    String[] types = {mMovieType};
                    query.addWhereContainsAll("types", Arrays.asList(types));
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
                    query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
                    query.findObjects(mMovieInfoFindListener);
                }
            } else {
                showProgressbarWithText("找到对手...");
                final MatchItem matchItem = list.get(0);
                mSkips.clear();
                //获取Skips
                mSkips.addAll(matchItem.getSkips());
                target_objectId = matchItem.getObjectId();
                target_userId = matchItem.getUserId();
                matchItem.setState(MyConstants.SAW_STATE);
                matchItem.update(mUpdateStateListener);
            }
        }
    };

    private void addMyLookItem() {
        MatchItem matchItem = new MatchItem();
        matchItem.setState(MyConstants.LOOK_STATE);
        matchItem.setUserId(mCurrentUser.getObjectId());
        matchItem.setMovieType(mMovieType);
        matchItem.setDifficult(mDifficult);
        matchItem.setSkips(mSkips);
        matchItem.save(mSaveLookListener);
    }

    //添加自己saw item
    private void addMySawItem() {
        MatchItem matchItem = new MatchItem();
        matchItem.setState(MyConstants.SAW_STATE);
        matchItem.setUserId(mCurrentUser.getObjectId());
        matchItem.setMovieType(mMovieType);
        matchItem.setDifficult(mDifficult);
        matchItem.save(mSaveSawListener);
    }

    private void addRandomMovieInfo() {
        mMovieInfoList.clear();
        final int[] index = {0};
        for (int i = 0; i < mSkips.size(); i++) {
            mMovieInfoList.add(i, new movieInfo());
            BmobQuery<movieInfo> query = new BmobQuery<>();
            query.setLimit(1);
            query.setSkip(mSkips.get(i));
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
            final int finalI = i;
            query.findObjects(new FindListener<movieInfo>() {
                @Override
                public void done(List<movieInfo> list, BmobException e) {
                    if (checkCommonException(e, OnlineMatchActivity.this)) {
                        return;
                    }
                    logd(list.get(0).toString());
                    mMovieInfoList.set(finalI, list.get(0));
                    index[0]++;
                    if (index[0] == mSkips.size()) {
                        start();
                    }
                }
            });
        }
    }

    /**
     * 更新look用户state为saw
     */
    private UpdateListener mUpdateStateListener = new UpdateListener() {
        @Override
        public void done(BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            addMySawItem();
        }
    };
    /**
     * 保存自己saw item, 更新对手targetId、target_userId
     */
    private SaveListener<String> mSaveSawListener = new SaveListener<String>() {
        @Override
        public void done(final String objectId, BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            my_objectId = objectId;
            MatchItem matchItem = new MatchItem();
            matchItem.setTargetID(my_objectId);
            matchItem.setTargetUserId(mCurrentUser.getObjectId());
            matchItem.update(target_objectId, mUpdateTargetListener);
        }
    };

    private UpdateListener mUpdateTargetListener = new UpdateListener() {
        @Override
        public void done(BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            if (mMovieType.equals("随意")) {
                addRandomMovieInfo();
            } else {
                BmobQuery<movieInfo> query = new BmobQuery<>();
                String[] types = {mMovieType};
                query.addWhereContainsAll("types", Arrays.asList(types));
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
                query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
                query.findObjects(new FindListener<movieInfo>() {
                    @Override
                    public void done(List<movieInfo> list, BmobException e) {
                        if (checkCommonException(e, OnlineMatchActivity.this)) {
                            return;
                        }
                        if (list.size() < 3) {
                            MyToast.getInstance().showShortWarn(OnlineMatchActivity.this, "没有此类型的电影");
                            return;
                        }
                        mMovieInfoList.clear();
                        for (int i = 0; i < mSkips.size(); i++) {
                            mMovieInfoList.add(i, list.get(mSkips.get(i)));
                        }
                        start();
                    }
                });
            }

        }
    };


    /**
     * 保存自己look item，监听自己
     */
    private SaveListener<String> mSaveLookListener = new SaveListener<String>() {
        @Override
        public void done(final String objectId, BmobException e) {
            my_objectId = objectId;
            //监听时机较晚，可能监听不到数据更新
            logd("监听自己=" + my_objectId);
            initBmobRealTimeData();
            cancelFlag = true;
        }
    };

    /**
     * 获取随意类别电影总数
     */
    private CountListener mCountListener = new CountListener() {
        @Override
        public void done(Integer count, BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            mSkips.clear();
            mSkips.addAll(RandomUtil.getRandomNums(num, count));
            addMyLookItem();
        }
    };

    /**
     * 获取其他类别电影
     */
    private FindListener<movieInfo> mMovieInfoFindListener = new FindListener<movieInfo>() {
        @Override
        public void done(List<movieInfo> list, BmobException e) {
            if (checkCommonException(e, OnlineMatchActivity.this)) {
                return;
            }
            if (list.size() < 3) {
                MyToast.getInstance().showShortWarn(OnlineMatchActivity.this, "没有此类型的电影");
                return;
            }
            mMovieInfoList.clear();
            mMovieInfoList.addAll(list);
            mSkips.clear();
            mSkips.addAll(RandomUtil.getRandomNums(num, list.size()));
            addMyLookItem();
        }
    };


    public void start() {
        hideProgressbar();
        Intent intent = new Intent(OnlineMatchActivity.this, OnlinePlayActivity.class);
        intent.putExtra("LIST", (Serializable) mMovieInfoList);
        intent.putExtra("DIFFICULT", mDifficult);
        intent.putExtra("TYPE", mMovieType);
        intent.putExtra("MY_ID", my_objectId);
        intent.putExtra("TARGET_ID", target_objectId);
        intent.putExtra("TARGET_USEID", target_userId);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (progressShowing()) {
                if (cancelFlag) {
                    showProgressbarWithText("正在取消...");
                    deleteMyLookItem();
                }
                return true;
            }
            finish();
        }
        return false;
    }

    private void deleteMyLookItem() {
        MatchItem matchItem = new MatchItem();
        matchItem.delete(my_objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, OnlineMatchActivity.this)) {
                    return;
                }
                hideProgressbar();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
