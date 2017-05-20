package com.example.asus.listener;

import android.content.Context;
import android.content.res.Resources;

import com.example.asus.activity.R;
import com.example.asus.view.PickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yhao on 2017/5/20.
 */

public class PickerListener implements PickerView.onSelectListener {

    private PickerView mCityPickerView;
    private Resources mRes;

    public PickerListener(Context context, PickerView cityPickerView) {
        mRes = context.getResources();
        mCityPickerView = cityPickerView;
    }

    @Override
    public void onSelect(String text) {
        List cityList = null;
        if (text.equals("北京")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.北京)));
        } else if (text.equals("天津")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.天津)));
        } else if (text.equals("河北")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.河北)));
        } else if (text.equals("山西")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.山西)));
        } else if (text.equals("内蒙古")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.内蒙古)));
        } else if (text.equals("辽宁")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.辽宁)));
        } else if (text.equals("吉林")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.吉林)));
        } else if (text.equals("黑龙江")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.黑龙江)));
        } else if (text.equals("上海")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.上海)));
        } else if (text.equals("江苏")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.江苏)));
        } else if (text.equals("浙江")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.浙江)));
        } else if (text.equals("安徽")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.安徽)));
        } else if (text.equals("福建")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.福建)));
        } else if (text.equals("江西")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.江西)));
        } else if (text.equals("山东")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.山东)));
        } else if (text.equals("河南")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.河南)));
        } else if (text.equals("湖北")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.湖北)));
        } else if (text.equals("湖南")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.湖南)));
        } else if (text.equals("广东")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.广东)));
        } else if (text.equals("广西")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.广西)));
        } else if (text.equals("海南")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.海南)));
        } else if (text.equals("重庆")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.重庆)));
        } else if (text.equals("四川")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.四川)));
        } else if (text.equals("贵州")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.贵州)));
        } else if (text.equals("云南")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.云南)));
        } else if (text.equals("西藏")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.西藏)));
        } else if (text.equals("陕西")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.陕西)));
        } else if (text.equals("甘肃")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.甘肃)));
        } else if (text.equals("青海")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.青海)));
        } else if (text.equals("宁夏")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.宁夏)));
        } else if (text.equals("新疆")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.新疆)));
        } else if (text.equals("台湾")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.台湾)));
        } else if (text.equals("香港")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.香港)));
        } else if (text.equals("澳门")) {
            cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.澳门)));
        }
        mCityPickerView.setData(cityList);
    }
}
