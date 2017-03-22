package com.example.asus.Image;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.activity.R;
import com.example.asus.util.BitmapUtil;
import com.example.asus.view.XfermodeView;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2017/1/1.
 */

public class ImageManager {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private static String TAG = "ImageManager";

    private static ImageManager instance = null;

    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    private ImageManager(){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    /**
     *     头像加载方法
     * @param ivPic
     * @param file
     */
    public void disPlay(ImageView ivPic, BmobFile file) {
        if (file == null) {
            ivPic.setImageResource(R.mipmap.avatar);
            return;
        }
        Bitmap bitmap;
        String url = file.getFileUrl();
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            Log.i(TAG, "从内存获取图片");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            Log.i(TAG, "从本地获取图片");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        //网络缓存
        Log.i(TAG, "从网络获取图片");
        mNetCacheUtils.getBitmapFromNet(ivPic,url);
    }

    /**
     *    XfermodeView加载图片方法
     * @param ivPic
     * @param file
     * @param blurRadius
     */
    public void disPlay(XfermodeView ivPic, BmobFile file, int blurRadius) {
        if (file == null) {
            ivPic.setImageResource(R.mipmap.defaultimg);
            return;
        }
        Bitmap bitmap;
        String url = file.getFileUrl();
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setmBgBitmap(bitmap, blurRadius);
            Log.i(TAG, "从内存获取图片");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setmBgBitmap(bitmap, blurRadius);
            Log.i(TAG, "从本地获取图片");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        //网络缓存
        Log.i(TAG, "从网络获取图片");
        mNetCacheUtils.getBitmapFromNet(ivPic, url, blurRadius);
    }

}
