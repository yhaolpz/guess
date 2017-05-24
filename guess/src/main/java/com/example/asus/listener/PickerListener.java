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
        switch (text) {
            case "北京":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.北京)));
                break;
            case "天津":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.天津)));
                break;
            case "河北":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.河北)));
                break;
            case "山西":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.山西)));
                break;
            case "内蒙古":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.内蒙古)));
                break;
            case "辽宁":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.辽宁)));
                break;
            case "吉林":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.吉林)));
                break;
            case "黑龙江":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.黑龙江)));
                break;
            case "上海":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.上海)));
                break;
            case "江苏":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.江苏)));
                break;
            case "浙江":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.浙江)));
                break;
            case "安徽":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.安徽)));
                break;
            case "福建":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.福建)));
                break;
            case "江西":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.江西)));
                break;
            case "山东":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.山东)));
                break;
            case "河南":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.河南)));
                break;
            case "湖北":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.湖北)));
                break;
            case "湖南":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.湖南)));
                break;
            case "广东":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.广东)));
                break;
            case "广西":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.广西)));
                break;
            case "海南":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.海南)));
                break;
            case "重庆":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.重庆)));
                break;
            case "四川":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.四川)));
                break;
            case "贵州":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.贵州)));
                break;
            case "云南":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.云南)));
                break;
            case "西藏":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.西藏)));
                break;
            case "陕西":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.陕西)));
                break;
            case "甘肃":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.甘肃)));
                break;
            case "青海":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.青海)));
                break;
            case "宁夏":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.宁夏)));
                break;
            case "新疆":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.新疆)));
                break;
            case "台湾":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.台湾)));
                break;
            case "香港":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.香港)));
                break;
            case "澳门":
                cityList = new ArrayList(Arrays.asList(mRes.getStringArray(R.array.澳门)));
                break;
        }
        mCityPickerView.setData(cityList);
    }
}
