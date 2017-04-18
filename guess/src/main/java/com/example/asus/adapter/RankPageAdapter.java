package com.example.asus.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Created by yhao on 2017/4/18.
 */

public class RankPageAdapter extends PagerAdapter {

    private List<View> viewLists;
    private String[] tabStrs = new String[]{"简单", "一般", "困难"};

    public RankPageAdapter(List<View> listViews) {
        this.viewLists = listViews;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabStrs[position];
    }
}
