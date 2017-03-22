package com.example.asus.Image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.asus.util.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by yhao on 2017/1/1.
 */

public class LocalCacheUtils {

    public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/guess";

    /**
     * 从本地读取图片
     *
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url) {
        String fileName = null;//把图片的url当做文件名,并进行MD5加密
        try {
            fileName = MD5Util.getMD5(url);
            File file = new File(CACHE_PATH, fileName);
            if (!file.exists()) {
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            Log.i("guess", "getBitmapFromLocal: " + file.getAbsolutePath());
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = MD5Util.getMD5(url);//把图片的url当做文件名,并进行MD5加密
            File file = new File(CACHE_PATH, fileName);

            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                Log.i("guess", "setBitmapToLocal: " + parentFile.getAbsolutePath());
                parentFile.mkdirs();
            }
            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
