package com.example.asus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.asus.bmobbean.User;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.listener.PickerListener;
import com.example.asus.view.PickerView;
import com.zhy.changeskin.SkinManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Register2Activity extends MySwipeBackActivity implements BDLocationListener {

    private User mUser = new User();

    private EditText mSex;
    private EditText mAge;
    private EditText mCity;

    private List<String> ageItemList;
    private List<String> sexItemList;

    private PickerView mProvincePickerView;
    private PickerView mCityPickerView;
    private PickerListener mPickerListener;
    private String mSelectCity = "房山"; //初始值


    public LocationClient mLocationClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_register2);
        mUser.setUsername(getIntent().getStringExtra("USERNAME"));
        mUser.setEmail(getIntent().getStringExtra("USERNAME"));
        mUser.setPassword(getIntent().getStringExtra("PASSWORD"));
        mUser.setName(getIntent().getStringExtra("NAME"));
        mSex = (EditText) findViewById(R.id.sex);
        mAge = (EditText) findViewById(R.id.age);
        mCity = (EditText) findViewById(R.id.city);
        ageItemList = new ArrayList<>();
        sexItemList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ageItemList.add("" + i);
        }
        sexItemList.add(0, "男");
        sexItemList.add(1, "女");
        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.registerLocationListener(this);//注册监听函数
        initLocation();
    }

    public void selectSex(View view) {
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
        wheelView.setData(sexItemList);
        if (TextUtils.isEmpty(mSex.getText())) {
            mSex.setText("男");
            wheelView.setSelected("男");
        } else {
            wheelView.setSelected(mSex.getText().toString());
        }
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mSex.setText(text);
            }
        });
    }

    public void selectAge(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_age, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(ageItemList);
        if (TextUtils.isEmpty(mAge.getText())) {
            mAge.setText("20");
            wheelView.setSelected(20 + "");
        } else {
            wheelView.setSelected(mAge.getText().toString());
        }
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mAge.setText(text);
            }
        });
    }

    public void location(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_city, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        mProvincePickerView = (PickerView) dialogView.findViewById(R.id.sp_province);
        mCityPickerView = (PickerView) dialogView.findViewById(R.id.sp_city);
        final Dialog chooseDialog = dialog.show();
        dialogView.findViewById(R.id.bt_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCity.setText(mSelectCity);
                chooseDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.bt_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.start();
                chooseDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        mProvincePickerView.setData(new ArrayList(Arrays.asList(getResources().getStringArray(R.array.province))));
        mProvincePickerView.setOnSelectListener(mPickerListener == null ? mPickerListener = new PickerListener(this,mCityPickerView) : mPickerListener);
        mProvincePickerView.setSelected("北京");
        mCityPickerView.setData(new ArrayList(Arrays.asList(getResources().getStringArray(R.array.北京))));
        mCityPickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mSelectCity = text;
            }
        });
    }


    public void commit(View view) {
        if (TextUtils.isEmpty(mSex.getText())) {
            MyToast.getInstance().showShortWarn(this, "请选择性别");
        } else if (TextUtils.isEmpty(mAge.getText())) {
            MyToast.getInstance().showShortWarn(this, "请选择年龄");
        } else if (TextUtils.isEmpty(mCity.getText())) {
            MyToast.getInstance().showShortWarn(this, "请选择城市");
        } else {
            mUser.setSex(mSex.getText().toString());
            mUser.setAge(Integer.parseInt(mAge.getText().toString()));
            mUser.setCity(mCity.getText().toString());
            mUser.setType("bmob");
            mUser.setScore1(1000);
            mUser.setScore2(1000);
            mUser.setScore3(1000);
            mUser.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        MyToast.getInstance().showShortDone(Register2Activity.this, "注册成功");
                        Intent intent = new Intent();
                        intent.setClass(Register2Activity.this, LoginActivity.class);
                        intent.putExtra("USERNAME", user.getUsername());
                        setResult(RESULT_OK, intent);
                        RegisterActivity.registerActivity.finish();
                        finish();
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right);
                    } else {
                        checkCommonException(e, Register2Activity.this);
                    }
                }
            });
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            logd("网络定位");
            mCity.setText(bdLocation.getCity());
        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
            logd("离线定位");
            mCity.setText(bdLocation.getCity());
        } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
            logd("GPS定位");
            mCity.setText(bdLocation.getCity());
        } else {
            MyToast.getInstance().showShortWarn(this, "定位失败,点击城市重新定位");
            loge("定位失败:  ERROR CODE: " + bdLocation.getLocType());
        }
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
