package com.example.asus.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.asus.activity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yinghao on 2017/1/6.
 * Email：756232212@qq.com
 */

public class PickerView extends View {

    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3.0f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 2;

    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint;

    //xml中可配置
    private float mMaxTextSize;
    private float mMinTextSize;

    private static final int DEFAULT_MAX_TEXT_SIZE = 50;
    private static final int DEFAULT_MIN_TEXT_SIZE = 25;
    private static final boolean DEFAULT_LOOP_DISPLAY = false;


    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 100;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }

    };

    public PickerView(Context context) {
        this(context, null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PickerView);
        mMaxTextSize = ta.getDimensionPixelSize(R.styleable.PickerView_max_textSize, DEFAULT_MAX_TEXT_SIZE);
        mMinTextSize = ta.getDimensionPixelSize(R.styleable.PickerView_min_textSize, DEFAULT_MIN_TEXT_SIZE);
        boolean loop_display = ta.getBoolean(R.styleable.PickerView_loop_display, DEFAULT_LOOP_DISPLAY);
        ta.recycle();
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null)
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
    }

    public void setData(List<String> datas) {
        mDataList = datas;
        mCurrentSelected = datas.size() / 2;
        invalidate();
    }

    /**
     * 选择选中的item的index
     *
     * @param selected
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        int distance = mDataList.size() / 2 - mCurrentSelected;
        if (distance < 0)
            for (int i = 0; i < -distance; i++) {
                moveHeadToTail();
                mCurrentSelected--;
            }
        else if (distance > 0)
            for (int i = 0; i < distance; i++) {
                moveTailToHead();
                mCurrentSelected++;
            }
        invalidate();
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++)
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
    }

    private void moveHeadToTail() {
        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
    }

    private void moveTailToHead() {
        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        // draw 中避免创建对象
        if (isInit)
            drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        mPaint.setTextSize((mMaxTextSize - mMinTextSize) * scale + mMinTextSize);
        mPaint.setColor(this.getResources().getColor(android.R.color.white));
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) ((float) (mViewHeight / 2.0 + mMoveLen) - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected), (float)(mViewWidth / 2.0), baseline, mPaint);
        for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mMinTextSize * position + type* mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        mPaint.setTextSize((mMaxTextSize - mMinTextSize) * scale + mMinTextSize);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) ((float) (mViewHeight / 2.0 + type * d) - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position), (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {

        mMoveLen += (event.getY() - mLastDownY);
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
        }

        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public interface onSelectListener {
        void onSelect(String text);
    }
}