package com.example.asus.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.GridView;
import android.widget.ImageView;


import com.example.asus.activity.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yinghao on 2016/12/26.
 * Email：756232212@qq.com
 */

public class ImageLoader {
    private LruCache<String, Bitmap> mCaches;
    private GridView mGridView;
    private Set<mAsynctask> mTask;
    private String[] mPaths;
    private String[] mIDs;
    private Context mContext;

    public ImageLoader(Context context, GridView gridView, String[] paths, String[] ids) {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mGridView = gridView;
        mPaths = paths;
        mIDs = ids;
        mContext = context;
        mTask = new HashSet<>();
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            mCaches.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return mCaches.get(key);
    }


    public void showImageByAsyncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
//            imageView.setImageResource(R.drawable.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void LoadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            if (i < mPaths.length) {
                String path = mPaths[i];
                String id = mIDs[i];
                Bitmap bitmap = getBitmapFromCache(path);
                if (bitmap == null) {
                    mAsynctask task = new mAsynctask(path, id);
                    task.execute(path);
                    mTask.add(task);
                } else {
                    ImageView imageView = (ImageView) mGridView.findViewWithTag(path);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (mAsynctask task : mTask) {
                task.cancel(true);
            }
        }
    }


    private class mAsynctask extends AsyncTask<String, Void, Bitmap> {
        private String mUrl;

        mAsynctask(String url,String id) {
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            long time = System.currentTimeMillis();
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            Log.i("AAA", ": " + (System.currentTimeMillis() - time));
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mGridView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }


//    public Bitmap getBitMapFromURL(String UrlString) {
//        Bitmap bitmap;
//        InputStream is = null;
//        try {
//            URL url = new URL(UrlString);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            is = new BufferedInputStream(connection.getInputStream());
//            bitmap = BitmapFactory.decodeStream(is);
//            connection.disconnect();
//            return bitmap;
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
}
