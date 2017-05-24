package com.example.asus.activity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.movieInfo;
import com.example.asus.bmobbean.record;
import com.example.asus.bmobbean.recordDAO;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MyToast;
import com.example.asus.greendao.SingleRecordDao;
import com.example.asus.greendao.entity.SingleRecord;
import com.example.asus.util.BitmapUtil;
import com.example.asus.util.JsonParser;
import com.example.asus.util.MD5Util;
import com.example.asus.util.RandomUtil;
import com.example.asus.util.ScreenUtil;
import com.example.asus.view.XfermodeViewP;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.changeskin.SkinManager;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SinglePlayActivity extends BaseActivity {
    private XfermodeViewP mXfermodeView; //剧照模糊层
    private ImageView mImageView; //剧照
    private RelativeLayout mKeyLayout;
    private TextView mScore;
    private TextView mMovieNumTv;
    private LinkedList<movieInfo> mMovieList;
    private int mMovieNum; //此局电影数
    private movieInfo mMovieInfo;
    private int blurRadius;
    private List<View> keyList = new ArrayList<>();
    private boolean onceFocus = true;
    private static final int SCALE_KEY_SCREEN = 10; //屏幕宽度与球宽度的比例
    private static final int KEY_MARGIN_TOP = 2; //球随机分发区域距所处容器顶部之间间隔的球数
    private List<Character> mKeyChar = new ArrayList<>();
    private int chooseKeyNum;//点击key的次数-1

    private int mRightNum;
    private int mSumScore;
    private String mDifficult;
    private String mMovieType;

    private BaseApplication mApplication;
    private User mCurrentUser;
    // 语音听写
    private SpeechRecognizer mySynthesizer;
    private ImageView mImageBt;
    private TextView mMscTv;
    private String mMscStr = "";

    //截图
    private PercentRelativeLayout mRelative;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logd("onCreate");
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_single_play);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        mRelative = (PercentRelativeLayout) findViewById(R.id.relative);
        mXfermodeView = (XfermodeViewP) findViewById(R.id.XfermodeView);
        mImageView = (ImageView) findViewById(R.id.image);
        mKeyLayout = (RelativeLayout) findViewById(R.id.key);
        mScore = (TextView) findViewById(R.id.score);
        mMovieNumTv = (TextView) findViewById(R.id.movieNum);
        mImageBt = (ImageView) findViewById(R.id.imageBt);
        mMscTv = (TextView) findViewById(R.id.mscTv);
        Intent intent = getIntent();
        List<movieInfo> list = (List<movieInfo>) intent.getSerializableExtra("LIST");
        mMovieNum = list.size();
        mMovieList = new LinkedList<>(list);
        mDifficult = intent.getStringExtra("DIFFICULT");
        mMovieType = intent.getStringExtra("TYPE");
        for (int i = 0; i < 3; i++) {
            if (mDifficult.equals(MyConstants.difficults[i])) {
                blurRadius = MyConstants.blurRadius[i];
            }
        }
        mXfermodeView.setOnScoreListener(new XfermodeViewP.ScoreListener() {
            @Override
            public void onUpdate(final int score) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScore.setText("" + score);
                    }
                });
            }
        });
        initNextMovie();
        initMsc();

    }

    private void initMsc() {
        //处理语音合成关键类
        mySynthesizer = SpeechRecognizer.createRecognizer(this, mInitListener);
        mySynthesizer.setParameter(SpeechConstant.DOMAIN, "iat");
        mySynthesizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mySynthesizer.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            logd("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                loge("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerListener mRecognizerDialogListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        //开始录音
        @Override
        public void onBeginOfSpeech() {
            mImageBt.setImageResource(R.mipmap.msc2);
            mMscTv.setText("");
        }

        //结束录音
        @Override
        public void onEndOfSpeech() {
            mImageBt.setImageResource(R.mipmap.msc1);
            String str = mMscStr.replaceAll("[\\p{Punct}\\s]+", "");
            mMscStr = "";
            if (str.equals(mMovieInfo.getMovieName())) {
                MyToast.getInstance().showBottomShortDone(SinglePlayActivity.this, "");
                mRightNum++;
                mSumScore += Integer.parseInt(mScore.getText().toString());
                jump(null);
                return;
            }
            mMscTv.setText(str);
        }

        //听写结果回调接口(返回Json格式结果，用户可参见附录13.1)；
        // 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        // 关于解析Json的代码可参见Demo中JsonParser类；
        // isLast等于true时会话结束
        public void onResult(RecognizerResult results, boolean isLast) {
            mMscStr += JsonParser.parseIatResult(results.getResultString());
        }


        public void onError(SpeechError error) {
            loge(error.getPlainDescription(true));
        }

        //扩展用接口
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

    };

    public void voice(View view) {
        mySynthesizer.startListening(mRecognizerDialogListener);
    }

    public void forHelp(View view) {
        String fileName = null;
        try {
            fileName = MD5Util.getMD5(mMovieInfo.getImage().getUrl());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        File file = new File(MyConstants.CACHE_PATH, fileName);
        BitmapUtil.bitmapToFile(BitmapUtil.getViewBitmap(mRelative),file);
        Tencent mTencent = mApplication.getTencent();
        Bundle bundle = new Bundle();
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,file.getAbsolutePath());
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "看图猜电影");
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        mTencent.shareToQQ(this, bundle, myListener);
    }

    ShareQQListener myListener = new ShareQQListener();

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }

    class ShareQQListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
        }

        @Override
        public void onError(UiError uiError) {
            loge(uiError.errorMessage);
        }

        @Override
        public void onCancel() {

        }
    }


    public void jump(View view) {
        endCurrentMovie();
        boolean endFlag = initNextMovie();
        if (endFlag) {
            if (mCurrentUser != null) {
                updateLocalScore(mCurrentUser.getObjectId());
                updateScore();
            } else {
                updateLocalScore("0");
                showPlayDoneDialog();
            }
            return;
        }
        showKeyAnim();
    }

    private void updateLocalScore(String userId) {
        SingleRecordDao dao = mApplication.getDaoSession().getSingleRecordDao();
        List list = dao.queryBuilder()
                .where(SingleRecordDao.Properties.UserId.eq(userId))
                .where(SingleRecordDao.Properties.Type.eq(mMovieType))
                .list();
        loge("UserId=" + userId + " type=" + mMovieType + " count = " + list.size());
        if (list.size() == 0) {
            //insert
            SingleRecord record = new SingleRecord();
            record.setId(null);
            record.setUserId(userId);
            record.setType(mMovieType);
            recordDAO.creatRecord(record, mDifficult, mRightNum, mMovieNum, mSumScore);
            dao.insert(record);
        } else {
            //update
            recordDAO.updateRecord((SingleRecord) list.get(0), mDifficult, mRightNum, mMovieNum, mSumScore);
            dao.update((SingleRecord) list.get(0));
        }

    }


    private void updateScore() {
        BmobQuery<record> query = new BmobQuery<>();
        query.addWhereEqualTo("username", mCurrentUser.getUsername());
        query.addWhereEqualTo("type", mMovieType);
        query.findObjects(new FindListener<record>() {
            @Override
            public void done(List<record> list, BmobException e) {
                if (e == null) {
                    record record;
                    if (list.size() > 0) {
                        record = list.get(0);
                        recordDAO.updateRecord(record, mDifficult, mRightNum, mMovieNum, mSumScore);
                        record.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    checkCommonException(e, SinglePlayActivity.this);
                                }
                            }
                        });
                    } else {
                        record = new record();
                        record.setUsername(mCurrentUser.getUsername());
                        record.setType(mMovieType);
                        record.setUser(mCurrentUser);
                        recordDAO.creatRecord(record, mDifficult, mRightNum, mMovieNum, mSumScore);
                        record.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e != null) {
                                    checkCommonException(e, SinglePlayActivity.this);
                                }
                            }
                        });
                    }
                    showPlayDoneDialog();
                } else {
                    checkCommonException(e, SinglePlayActivity.this);
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (onceFocus) {
                onceFocus = false;
                showKeyAnim();
            }
        }
    }


    /**
     * key 动画
     */
    private void showKeyAnim() {
        chooseKeyNum = -1;
        int keyLayoutWidth = mKeyLayout.getMeasuredWidth();
        int keyLayoutHeight = mKeyLayout.getMeasuredHeight();
        int keyWidth = keyLayoutWidth / SCALE_KEY_SCREEN;
        logd("mKeyLayoutWidth=" + keyLayoutWidth + " mKeyLayoutHeight=" + keyLayoutHeight);
        int xListSize = keyLayoutWidth / keyWidth;
        List<Integer> xList = new ArrayList<>();
        xList.add(0, keyWidth / 2);
        for (int i = 1; i < xListSize / 2; i++) {
            xList.add(i, xList.get(0) + keyWidth * i);
        }
        xList.add(xListSize / 2, -xList.get(0));
        for (int i = xListSize / 2 + 1; i < xListSize; i++) {
            xList.add(i, -xList.get(i - 5));
        }
        int yListSize = (keyLayoutHeight - KEY_MARGIN_TOP * keyWidth) / keyWidth;
        List<Integer> yList = new ArrayList<>();
        yList.add(0, keyWidth * KEY_MARGIN_TOP);
        for (int i = 1; i < yListSize; i++) {
            yList.add(i, yList.get(0) + keyWidth * i);
        }
        Collections.shuffle(xList);
        Collections.shuffle(yList);
        Collections.shuffle(keyList);
        List<Integer> list = RandomUtil.getRepeatRandomNums(10, yList.size());
        for (int i = 0; i < keyList.size(); i++) {
            //key必须小于8，否则下标越界异常
            ObjectAnimator.ofFloat(keyList.get(i), "translationX", 0F, xList.get(i)).setDuration(1000).start();
            ObjectAnimator.ofFloat(keyList.get(i), "translationY", 0F, yList.get(list.get(i))).setDuration(10000).start();
        }
    }


    /**
     * 结束电影时清除相关数据
     */
    private void endCurrentMovie() {
        mKeyLayout.removeAllViews();
        mKeyChar.clear();
        keyList.clear();
    }

    /**
     * 电影数据初始化
     *
     * @return
     */
    private boolean initNextMovie() {
        try {
            mMovieInfo = mMovieList.pop();
        } catch (NoSuchElementException e) {
            return true;
        }
        mMovieNumTv.setText(mMovieNum - mMovieList.size() + "/" + mMovieNum);
        mScore.setText("100");
        Glide.with(this).load(mMovieInfo.getImage().getUrl()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                //获取的bitmap比mImageView小
                mXfermodeView.setmBgBitmap(((GlideBitmapDrawable) resource).getBitmap(), blurRadius);
                return false;
            }
        }).placeholder(R.drawable.placeholder).into(mImageView);
        char[] chars = mMovieInfo.getKey().toCharArray();
        for (char c : chars) {
            mKeyChar.add(c);
        }
        List<Character> moreChars = RandomUtil.add1or2char(mKeyChar);
        int keyTextViewWidth = ScreenUtil.getScreenWidth(this) / SCALE_KEY_SCREEN;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        for (char c : moreChars) {
            TextView textView = creatKeyTextView(c, keyTextViewWidth);
            mKeyLayout.addView(textView, layoutParams);
            keyList.add(textView);
        }
        return false;
    }


    private void showPlayDoneDialog() {
        View dialogView = View.inflate(this, R.layout.dialog_play_done, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 0, 0, 0, 0);
        TextView movieType = (TextView) dialogView.findViewById(R.id.movieType);
        TextView rightNum = (TextView) dialogView.findViewById(R.id.rightNum);
        TextView sum = (TextView) dialogView.findViewById(R.id.sum);
        TextView average = (TextView) dialogView.findViewById(R.id.average);
        movieType.setText(mMovieType);
        rightNum.setText("正确率：" + mRightNum + "/" + mMovieNum);
        sum.setText("总分：" + mSumScore);
        average.setText("平均分：" + mSumScore / mMovieNum);
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        chooseDialog.setCanceledOnTouchOutside(false);
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        ObjectAnimator.ofFloat(dialogView, "alpha", 0, 1).setDuration(500).start();
    }


    /**
     * 创建宽高为屏幕宽度1/10的TextView
     */
    private TextView creatKeyTextView(char c, int keyTextViewWidth) {
        final TextView textView = new TextView(this);
        textView.setText(c + "");
        textView.setTextSize(keyTextViewWidth / 2 / getResources().getDisplayMetrics().scaledDensity);
        textView.setWidth(keyTextViewWidth);
        textView.setHeight(keyTextViewWidth);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.key_bt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setBackgroundResource(R.drawable.key_choose_bt);
                textView.setOnClickListener(null);
                chooseKeyNum++;
                if (!String.valueOf(mKeyChar.get(chooseKeyNum)).equals(textView.getText().toString())) {
                    MyToast.getInstance().showBottomShortWrong(SinglePlayActivity.this, "");
                    jump(null);
                } else {
                    if (chooseKeyNum == mKeyChar.size() - 1) {
                        MyToast.getInstance().showBottomShortDone(SinglePlayActivity.this, "");
                        mRightNum++;
                        mSumScore += Integer.parseInt(mScore.getText().toString());
                        jump(null);
                    }
                }
            }
        });
        return textView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //退出确认框
            View dialogView = View.inflate(this, R.layout.dialog_exit_confirm, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
            dialog.setView(dialogView, 0, 0, 0, 0);
            TextView mText = (TextView) dialogView.findViewById(R.id.text);
            TextView mExit = (TextView) dialogView.findViewById(R.id.exit);
            TextView mCancel = (TextView) dialogView.findViewById(R.id.cancel);
            mText.setText("提前退出将无法记录本局分数");
            mExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更新服务端数据,更新完后退出
                    finish();

                }
            });
            final Dialog chooseDialog = dialog.show();
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseDialog.dismiss();
                }
            });
            WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            chooseDialog.getWindow().setAttributes(lp);
            ObjectAnimator.ofFloat(dialogView, "alpha", 0, 1).setDuration(500).start();
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
