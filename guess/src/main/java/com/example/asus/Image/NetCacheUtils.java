package com.example.asus.Image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.util.BitmapUtil;
import com.example.asus.view.XfermodeView;
import com.sina.weibo.sdk.utils.ImageUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yhao on 2017/1/1.
 */

public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 从网络下载图片
     *
     * @param ivPic 显示图片的imageview
     * @param url   下载图片的网络地址
     */
    public void getBitmapFromNet(ImageView ivPic, String url) {
        new BitmapTask().execute(ivPic, url);//启动AsyncTask
    }

    public void getBitmapFromNet(XfermodeView ivPic, String url, int blurRadius) {
        new BitmapTask().execute(ivPic, url, blurRadius);//启动AsyncTask
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView ivPic;
        private String url;
        private int blurRadius = -1;//-1则为头像加载图片

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (ImageView) params[0];
            url = (String) params[1];
            if (params.length == 3) {
                blurRadius = (int) params[2];
                ivPic = (XfermodeView) params[0];
            }
            return downLoadBitmap(ivPic, url);
        }

        /**
         * 更新进度,在主线程中
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                if (blurRadius == -1) {
                    ivPic.setImageBitmap(result);
                } else {
                    ((XfermodeView) ivPic).setmBgBitmap(result, blurRadius);
                }
                //从网络获取图片后,保存至本地缓存
                mLocalCacheUtils.setBitmapToLocal(url, result);
                //保存至内存中
                mMemoryCacheUtils.setBitmapToMemory(url, result);

            }
        }
    }


    /**
     * 网络下载图片
     * 根据指定imageView的尺寸下载指定的图片
     *
     * @param url
     * @return
     */
    private Bitmap downLoadBitmap(ImageView imageView, String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //图片压缩
                byte[] data = BitmapUtil.inputStream2ByteArr(conn.getInputStream());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, options);
                Log.d("guess", "online bitmap: width:" + options.outWidth + "  height:" + options.outHeight);
                options.inSampleSize = BitmapUtil.calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                Log.d("guess", "imageView: width:" + imageView.getWidth() + "  height:" + imageView.getHeight());
                Log.d("guess", "downLoadBitmap: width:" + bitmap.getWidth() + "  height:" + bitmap.getHeight());
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return null;
    }
}
