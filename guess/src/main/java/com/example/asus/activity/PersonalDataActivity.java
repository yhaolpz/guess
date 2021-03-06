package com.example.asus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.bmobbean.User;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.util.BitmapUtil;
import com.example.asus.view.CircleImageView;
import com.zhy.changeskin.SkinManager;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PersonalDataActivity extends MySwipeBackActivity {


    private CircleImageView mAvatar;
    private TextView mName;
    private LinearLayout mSexDataLayout;
    private ImageView mSexIcon;
    private TextView mAge;
    private TextView mCity;
    private TextView mEmail;

    private static final int reqCode_takePhoto = 1;// 拍照
    private static final int reqCode_seletePhoto = 2;// 从相册中选择
    private static final int reqCode_cropPhoto = 3;// 剪裁图片

    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final String AVATAR_FILE_NAME = "avatar.jpg";

    private File tempPhotoFile;
    private File tempAvatarFile;


    private BaseApplication mApplication;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_personal_data);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        mAvatar = (CircleImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSexDataLayout = (LinearLayout) findViewById(R.id.sex_data_layout);
        mSexIcon = (ImageView) findViewById(R.id.sex_icon);
        mAge = (TextView) findViewById(R.id.age);
        mCity = (TextView) findViewById(R.id.city);
        mEmail = (TextView) findViewById(R.id.email);
        Glide.with(this).load(mCurrentUser.getAvatar() == null ? R.mipmap.avatar : mCurrentUser.getAvatar().getUrl()).into(mAvatar);
    }

    //从 editPersonalDataActivity 返回后更新一下界面
    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = mApplication.getUser();
        mName.setText(mCurrentUser.getName());
        mCity.setText(mCurrentUser.getCity());
        mAge.setText(mCurrentUser.getAge() == null ? "" : mCurrentUser.getAge() + "岁");
        mEmail.setText(mCurrentUser.getEmail());
        if (mCurrentUser.getSex().equals("男")) {
            mSexIcon.setBackgroundResource(R.mipmap.boy);
            mSexDataLayout.setBackgroundResource(R.drawable.boy_data_shape);
        } else {
            mSexIcon.setBackgroundResource(R.mipmap.girl);
            mSexDataLayout.setBackgroundResource(R.drawable.gril_data_shape);
        }
    }


    public void editData(View view) {
        Intent intent = new Intent(this, EditPersonalDataActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void updateAvatar(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_pic, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        TextView takePhoto = (TextView) dialogView.findViewById(R.id.takePhoto);
        TextView seletePhoto = (TextView) dialogView.findViewById(R.id.select);
        TextView cancel = (TextView) dialogView.findViewById(R.id.cancel);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.dismiss();
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                tempPhotoFile = new File(MyConstants.CACHE_PATH, PHOTO_FILE_NAME);
                Uri uri = Uri.fromFile(tempPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, reqCode_takePhoto);
            }
        });
        seletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, reqCode_seletePhoto);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog.dismiss();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == reqCode_seletePhoto) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == reqCode_takePhoto) {
            crop(Uri.fromFile(tempPhotoFile));
        } else if (requestCode == reqCode_cropPhoto) {
            if (data != null) {
                showProgressbarWithText("更新中");
                Bitmap bitmap = data.getParcelableExtra("data");
                tempAvatarFile = new File(MyConstants.CACHE_PATH, AVATAR_FILE_NAME);
                BitmapUtil.bitmapToFile(bitmap, tempAvatarFile);
                final User user = new User();
                BmobFile bmobFile = new BmobFile(tempAvatarFile);
                user.setAvatar(bmobFile);
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            updateUser(user);
                        } else {
                            checkCommonException(e, PersonalDataActivity.this);
                        }
                        try {
                            tempPhotoFile.delete();
                            tempAvatarFile.delete();
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                });
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUser(final User user) {
        user.update(mCurrentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, PersonalDataActivity.this)) {
                    return;
                }
                Glide.with(PersonalDataActivity.this).load(user.getAvatar() == null ? R.mipmap.avatar : user.getAvatar().getUrl()).into(mAvatar);
            }
        });
        if (mCurrentUser.getAvatar() != null) {
            mCurrentUser.getAvatar().delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    checkCommonException(e, PersonalDataActivity.this);
                }
            });
        }
        mCurrentUser.setAvatar(user.getAvatar());
        mApplication.saveAvatar(user.getAvatar());
        HomeActivity.IS_UPDATE_AVATAR_FLAG = true;
    }

    /*
    * 剪切图片
    */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// 不返回缩略图
        startActivityForResult(intent, reqCode_cropPhoto);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }

}
