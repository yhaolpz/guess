package com.example.asus.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.adapter.RankPageAdapter;
import com.example.asus.bmobbean.User;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.greendao.SingleRecordDao;
import com.example.asus.greendao.entity.SingleRecord;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.List;

public class ScoreTypeActivity extends MySwipeBackActivity {
    private String mType;
    private ImageView mTopImg;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<View> mListViewList;

    private List<TextView> mSumList;
    private List<TextView> mRsumList;
    private List<TextView> mSumScoreList;
    private List<TextView> mAverageList;

    private BaseApplication mApplication;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_score_type);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        mType = getIntent().getStringExtra("TYPE");
        Log.e("TAG", "onCreate:mType " + mType);
        initView();
        initEvent();
    }

    private void initView() {
        mTopImg = (ImageView) findViewById(R.id.topImg);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        for (int i = 0; i < MyConstants.movieTypes.length; i++) {
            if (TextUtils.equals(MyConstants.movieTypes[i], mType)) {
                Glide.with(this).load(MyConstants.movieTypesImg[i]).placeholder(R.drawable.placeholder).into(mTopImg);
            }
        }
        mListViewList = new ArrayList<>();
        mSumList = new ArrayList<>();
        mRsumList = new ArrayList<>();
        mSumScoreList = new ArrayList<>();
        mAverageList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RelativeLayout frameLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.score_single, null);
            mListViewList.add(i, frameLayout);
            TextView sum = (TextView) frameLayout.findViewById(R.id.sum);
            TextView rsum = (TextView) frameLayout.findViewById(R.id.rsum);
            TextView sumScore = (TextView) frameLayout.findViewById(R.id.sumScore);
            TextView average = (TextView) frameLayout.findViewById(R.id.average);
            mSumList.add(sum);
            mRsumList.add(rsum);
            mSumScoreList.add(sumScore);
            mAverageList.add(average);
        }
        mViewPager.setAdapter(new RankPageAdapter(mListViewList));
        mTabLayout.setupWithViewPager(mViewPager);
        loadScore();
    }

    private void loadScore() {
        SingleRecordDao dao = mApplication.getDaoSession().getSingleRecordDao();
        String userId = mCurrentUser == null ? "0" : mCurrentUser.getObjectId();
        List list = dao.queryBuilder()
                .where(SingleRecordDao.Properties.UserId.eq(userId))
                .where(SingleRecordDao.Properties.Type.eq(mType))
                .list();
        if (!list.isEmpty()) {
            SingleRecord record = (SingleRecord) list.get(0);
            loge(record.toString());
            for (int i = 0; i < 3; i++) {
                int sum = 0, rsum = 0, sumScore = 0, average = 0;
                switch (i) {
                    case 0:
                        sum = record.getSum1();
                        rsum = record.getRsum1();
                        sumScore = record.getSumScore1();
                        average = record.getAverage1();
                        break;
                    case 1:
                        sum = record.getSum2();
                        rsum = record.getRsum2();
                        sumScore = record.getSumScore2();
                        average = record.getAverage2();
                        break;
                    case 2:
                        sum = record.getSum3();
                        rsum = record.getRsum3();
                        sumScore = record.getSumScore3();
                        average = record.getAverage3();
                        break;
                }
                mSumList.get(i).setText("" + sum);
                mRsumList.get(i).setText("" + rsum);
                mSumScoreList.get(i).setText("" + sumScore);
                mAverageList.get(i).setText("" + average);
            }
        }

    }

    private void initEvent() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() == null) {
//                    loadScore(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getTag() == null) {
                    tab.setTag("selected");
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
