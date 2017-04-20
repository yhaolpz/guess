package com.example.asus.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.adapter.RankPageAdapter;
import com.example.asus.bmobbean.record;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.view.CircleImageView;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RankTypeActivity extends MySwipeBackActivity {

    private String mType;
    private ImageView mTopImg;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    private List<View> mListViewList;
    private List<TextView> mTipList;
    private List<List<record>> mListRankList;
    private List<RankAdapter> mAdapterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_rank_type);
        mType = getIntent().getStringExtra("TYPE");
        Log.e("TAG", "onCreate:mType " + mType);
        initView();
        initEvent();
    }

    private void initEvent() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() == null) {
                    loadRank(tab.getPosition());
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

    /**
     * @param n 0 1 2 对应于 简单 一般 困难
     */
    private void loadRank(final int n) {
        //按答对题数排序
        String order = "-rsum" + (n + 1);
        BmobQuery<record> query = new BmobQuery<>();
        query.addWhereEqualTo("type", mType);
        query.setLimit(5);
        query.order(order);
        query.include("user");
        mTipList.get(n).setText("正在加载");
        query.findObjects(new FindListener<record>() {
            @Override
            public void done(List<record> list, BmobException e) {
                if (checkCommonException(e, RankTypeActivity.this)) {
                    return;
                }
                if (list.size() == 0) {
                    mTipList.get(n).setText("这里没有数据~");
                } else {
                    mTipList.get(n).setText("");
                    mListRankList.get(n).clear();
                    mListRankList.get(n).addAll(list);
                    mAdapterList.get(n).notifyDataSetChanged();
                }
            }
        });
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
        mListRankList = new ArrayList<>();
        mAdapterList = new ArrayList<>();
        mTipList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.rank_list_view, null);
            ListView listView = (ListView) frameLayout.findViewById(R.id.listView);
            List<record> rankItems = new ArrayList<>();
            RankAdapter adapter = new RankAdapter(i, rankItems);
            listView.setAdapter(adapter);
            mListViewList.add(i, frameLayout);
            mListRankList.add(i, rankItems);
            mAdapterList.add(i, adapter);
            mTipList.add(i, (TextView) frameLayout.findViewById(R.id.tips));
        }
        mViewPager.setAdapter(new RankPageAdapter(mListViewList));
        mTabLayout.setupWithViewPager(mViewPager);
        //加载第一屏
        loadRank(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }


    class RankAdapter extends BaseAdapter {

        private List<record> mRecordList;
        private int diffcult; // 0,1,2  对应简单 一般 困难

        public RankAdapter(int diffcult, List<record> recordList) {
            this.mRecordList = recordList;
            this.diffcult = diffcult;
        }

        @Override
        public int getCount() {
            return mRecordList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(RankTypeActivity.this).inflate(R.layout.item_rank, null);
                viewHolder.mIndex = (TextView) convertView.findViewById(R.id.index);
                viewHolder.mAvatar = (CircleImageView) convertView.findViewById(R.id.avatar);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mRightNum = (TextView) convertView.findViewById(R.id.rightNum);
                viewHolder.mAverage = (TextView) convertView.findViewById(R.id.average);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mIndex.setText("" + (position + 1));
            Glide.with(RankTypeActivity.this)
                    .load(mRecordList.get(position).getUser().getAvatar().getUrl())
                    .placeholder(R.mipmap.avatar)
                    .into(viewHolder.mAvatar);
            viewHolder.mName.setText(mRecordList.get(position).getUser().getName());
            String rightStr = "猜对:";
            String averageStr = "平均:";
            switch (diffcult) {
                case 0:
                    rightStr += mRecordList.get(position).getRsum1();
                    averageStr += mRecordList.get(position).getAverage1();
                    break;
                case 1:
                    rightStr += mRecordList.get(position).getRsum2();
                    averageStr += mRecordList.get(position).getAverage2();
                    break;
                case 2:
                    rightStr += mRecordList.get(position).getRsum3();
                    averageStr += mRecordList.get(position).getAverage3();
                    break;

            }
            viewHolder.mRightNum.setText(rightStr);
            viewHolder.mAverage.setText(averageStr);
            return convertView;
        }
    }

    class ViewHolder {
        TextView mIndex;
        CircleImageView mAvatar;
        TextView mName;
        TextView mRightNum;
        TextView mAverage;
    }


}
