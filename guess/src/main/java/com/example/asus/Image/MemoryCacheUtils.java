package com.example.asus.Image;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by yhao on 2017/1/1.
 */

public class MemoryCacheUtils {
    private LruCache<String,Bitmap> mMemoryCache;

    public MemoryCacheUtils(){
        long maxMemory = Runtime.getRuntime().maxMemory()/8;//得到手机最大允许内存的1/8,即超过指定内存,则开始回收
        //需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
        mMemoryCache=new LruCache<String,Bitmap>((int) maxMemory){
            //用于计算每个条目的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };

    }

    /**
     * 从内存中读图片
     * @param url
     */
    public Bitmap getBitmapFromMemory(String url) {

        Bitmap bitmap = mMemoryCache.get(url);
        return bitmap;

    }

    /**
     * 往内存中写图片
     * @param url
     * @param bitmap
     */
    public void setBitmapToMemory(String url, Bitmap bitmap) {

        mMemoryCache.put(url,bitmap);
    }
}
