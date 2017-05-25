package com.example.asus.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.greendao.DoubleRecordDao;
import com.example.asus.greendao.entity.DoubleRecord;
import com.zhy.changeskin.SkinManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DoubleRecordTypeActivity extends MySwipeBackActivity {

    private TabLayout mTabLayout;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<TextView> mTipList;
    private List<List<DoubleRecord>> mListRankList;
    private List<RankAdapter> mAdapterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_rank_type);
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


        DoubleRecordDao dao = BaseApplication.getInstances().getDaoSession().getDoubleRecordDao();
        List list = dao.queryBuilder()
                .where(DoubleRecordDao.Properties.UserId.eq(BaseApplication.getInstances().getUser().getObjectId()))
                .where(DoubleRecordDao.Properties.Diffcult.eq(MyConstants.difficults[n]))
                .orderAsc(DoubleRecordDao.Properties.Time)
                .limit(10)
                .list();
        if (list.size() == 0) {
            mTipList.get(n).setText("这里没有数据~");
        } else {
            mTipList.get(n).setText("");
            mListRankList.get(n).clear();
            mListRankList.get(n).addAll(list);
            mAdapterList.get(n).notifyDataSetChanged();
        }
    }


    private void initView() {
        ImageView topImg = (ImageView) findViewById(R.id.topImg);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        Glide.with(this).load(R.drawable.placeholder).into(topImg);
        List<View> listViewList = new ArrayList<>();
        mListRankList = new ArrayList<>();
        mAdapterList = new ArrayList<>();
        mTipList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.rank_list_view, null);
            ListView listView = (ListView) frameLayout.findViewById(R.id.listView);
            List<DoubleRecord> records = new ArrayList<>();
            RankAdapter adapter = new RankAdapter(records);
            listView.setAdapter(adapter);
            listViewList.add(i, frameLayout);
            mListRankList.add(i, records);
            mAdapterList.add(i, adapter);
            mTipList.add(i, (TextView) frameLayout.findViewById(R.id.tips));
        }
        viewPager.setAdapter(new RankPageAdapter(listViewList));
        mTabLayout.setupWithViewPager(viewPager);
        //加载第一屏
        loadRank(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }


    class RankAdapter extends BaseAdapter {

        private List<DoubleRecord> mRecordList;

        RankAdapter(List<DoubleRecord> recordList) {
            this.mRecordList = recordList;
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
                convertView = LayoutInflater.from(DoubleRecordTypeActivity.this).inflate(R.layout.item_double_record, null);
                viewHolder.mTime = (TextView) convertView.findViewById(R.id.time);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mScore = (TextView) convertView.findViewById(R.id.score);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTime.setText(formatter.format(mRecordList.get(position).getTime()));
            viewHolder.mName.setText(mRecordList.get(position).getTargetId());
            int score = mRecordList.get(position).getScore();
            String rightStr = (score > 0 ? "+" : "-") + score;
            viewHolder.mScore.setText(rightStr);
            return convertView;
        }
    }

    class ViewHolder {
        TextView mTime;
        TextView mName;
        TextView mScore;
    }


}
