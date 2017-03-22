package com.example.asus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.asus.activity.R;
import com.example.asus.activity.SinglePlayActivity;
import com.example.asus.util.BlurUtil;

/**
 * Created by Ahab on 2016/10/18.
 */
public class XfermodeView extends ImageView {
    private Bitmap mBgBitmap, mFgBitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    private Path mPath;
    private Context mContext;
    private int mBlurRadius = -1;

    public XfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setAlpha(0);//PorterDuffXfermode进行图层混合时会计算透明通道的值
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        mPaint.setStrokeJoin(Paint.Join.ROUND);//结合处样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);//笔触风格
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//源图像与目标图像相交处绘制，先入为主
        mPath = new Path();//路径
    }

    private ScoreListener mScoreListener;

    public void setOnScoreListener(ScoreListener scoreListener) {
        mScoreListener = scoreListener;
    }

    public interface ScoreListener{
        void onUpdate(int score);
    }

    public void setmBgBitmap(Bitmap bitmap, int blurRadius) {
        this.mBgBitmap = bitmap;
        this.mBlurRadius = blurRadius;
        if (blurRadius == 0) {
            invalidate();
            return;
        }
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        mFgBitmap = BlurUtil.getBlurBitmap(bitmap, blurRadius);
        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawBitmap(mFgBitmap, 0, 0, paint);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBgBitmap != null) {
            Log.d("guess", "onDraw: mBgBitmap w=" + mBgBitmap.getWidth() + " h=" + mBgBitmap.getHeight());
            canvas.drawBitmap(mBgBitmap, 0, 0, null);
        }
        if (mFgBitmap != null) {
            Log.d("guess", "onDraw: mFgBitmap w=" + mFgBitmap.getWidth() + " h=" + mFgBitmap.getHeight());
            if (mBlurRadius > 0) {
                canvas.drawBitmap(mFgBitmap, 0, 0, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBlurRadius == 0) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
                break;
        }
        mCanvas.drawPath(mPath, mPaint);
        invalidate();
        return true;
    }


    //统计刮开区域所占百分比
    private Runnable mRunnable = new Runnable() {
        private int[] mPixels;

        @Override
        public void run() {
            int w = getWidth();
            int h = getHeight();
            float wipeArea = 0;
            float totalArea = w * h;
            mPixels = new int[w * h];
            //获取所有像素信息
            mFgBitmap.getPixels(mPixels, 0, w, 0, 0, w, h);
            //统计擦出的区域
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int index = i + j * w;
                    if (mPixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }
            //计算百分比
            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);
                int score = 100 - percent;
                if (mScoreListener != null) {
                    mScoreListener.onUpdate(score);
                }
            }
        }
    };
}

