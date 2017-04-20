package com.example.asus.activity;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.asus.Image.ImageManager;
import com.example.asus.bmobbean.MatchItem;
import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.movieInfo;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MyToast;
import com.example.asus.util.DensityUtils;
import com.example.asus.util.JsonParser;
import com.example.asus.util.RandomUtil;
import com.example.asus.util.ScreenUtil;
import com.example.asus.view.CircleImageView;
import com.example.asus.view.XfermodeView;
import com.example.asus.view.XfermodeViewP;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.zhy.changeskin.SkinManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class OnlinePlayActivity extends BaseActivity {

    private ImageView mOne;
    private ImageView mTwo;
    private ImageView mThree;
    private List<ImageView> mTargetScoreViewList = new ArrayList<>();
    private CircleImageView mTargetAvatar;
    private CircleImageView mMyAvatar;
    private TextView mTargetName;
    private TextView mMyName;
    private XfermodeViewP mXfermodeView;
    private ImageView mImageView; //剧照
    private TextView mMovieNum;
    private TextView mScore;
    private RelativeLayout mKeyLayout;
    private TextView mMscTv;
    private ImageView mImageBt;

    private BaseApplication mApplication;
    private User mCurrentUser;

    // 语音听写
    private SpeechRecognizer mySynthesizer;
    private String mMscStr = "";

    private LinkedList<movieInfo> mMovieList = new LinkedList<>();
    private int movieNum; //此局电影数
    private movieInfo mMovieInfo;
    private int blurRadius;
    private List<View> keyList = new ArrayList<View>();
    private static final int SCALE_KEY_SCREEN = 10; //屏幕宽度与球宽度的比例
    private static final int KEY_MARGIN_TOP = 1; //球随机分发区域距所处容器顶部之间间隔的球数
    private List<Character> mKeyChar = new ArrayList<>();
    private int chooseKeyNum;//点击key的次数-1
    private int mKeyLayoutWidth;
    private int mKeyLayoutHeight;
    private int mKeyWidth;

    private List<Integer> scoreList = new ArrayList<>();

    private int mRightNum;
    private int mSumScore;
    private String mDifficult;
    private String mMovieType;

    private String my_objectId;
    private String target_objectId;//监听用
    private String target_username;//查询用户信息用

    private BmobRealTimeData rtd;

    private boolean my_ready_flag = false;
    private boolean target_ready_flag = false;
    private Button mTargetReadyBt;
    private Button mReadyBt;
    private TextView mCountDownTv;

    //倒计时
    private int t = 10;
    private boolean cancel_flag;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            t--;
            if (t < 0) {
                new MatchItem().delete(my_objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (checkCommonException(e, OnlinePlayActivity.this)) {
                            return;
                        }
                        finish();
                    }
                });
            }
            mCountDownTv.setText("");
            if (!cancel_flag) {
                mCountDownTv.setText("" + t);
                handler.postDelayed(this, 1000);
            }
        }
    };


    /**
     * 1. 双方准备
     * 3. 开始
     * 4. 其中一玩家答完（不论输赢？），另一玩家开始倒计时10S
     * 5. 切换下一题 尽量一致（<1s）
     * 6. 结束，计分
     * <p>
     * 查看对手信息，添加好友，聊天
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_online_play);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        Intent intent = getIntent();
        List<movieInfo> list = (List<movieInfo>) intent.getSerializableExtra("LIST");
        movieNum = list.size();
        mMovieList.addAll(list);
        mDifficult = intent.getStringExtra("DIFFICULT");
        mMovieType = intent.getStringExtra("TYPE");
        target_objectId = intent.getStringExtra("TARGET_ID");
        my_objectId = intent.getStringExtra("MY_ID");
        target_username = intent.getStringExtra("TARGET_USERNAME");
        for (int i = 0; i < 3; i++) {
            if (mDifficult.equals(MyConstants.difficults[i])) {
                blurRadius = MyConstants.blurRadius[i];
            }
        }
        initView();
        initData();
        initMsc();
        initBmobRealTimeData();
    }


    private void initBmobRealTimeData() {
        rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                Gson gson = new Gson();
                MatchItem item = gson.fromJson(data.optString("data"), MatchItem.class);
                logd("onDataChange item ：" + item.toString());
                if (item.getState().equals(MyConstants.READY_STATE)) {
                    mTargetReadyBt.setText("已准备");
                    target_ready_flag = true;
                    if (my_ready_flag) {
                        start();
                    }
                    handler.postDelayed(runnable, 1000);
                } else if (item.getScores() != null) {
                    List<Integer> scoreList = item.getScores();
                    for (int i = 0; i < scoreList.size(); i++) {
                        if (scoreList.get(i) < 0) {
                            mTargetScoreViewList.get(i).setImageResource(R.mipmap.wrong);
                        } else {
                            mTargetScoreViewList.get(i).setImageResource(R.mipmap.done);
                        }
                        mTargetScoreViewList.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                logd("initBmobRealTimeData  连接成功? " + rtd.isConnected());
                rtd.subRowUpdate("MatchItem", target_objectId);
            }
        });
    }

    //双方都已准备，开始游戏
    private void start() {
        logd("start*************************");
        MatchItem matchItem = new MatchItem();
        matchItem.setState(MyConstants.PLAYING_STATE);
        matchItem.update(my_objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, OnlinePlayActivity.this)) {
                    return;
                }
                logd("playing");
            }
        });
        cancel_flag = true;
        mTargetReadyBt.setVisibility(View.GONE);
        mReadyBt.setVisibility(View.GONE);
        initNextMovie();
        showKeyAnim();
    }

    private void initData() {
        mMyName.setText(mCurrentUser.getName());
        ImageManager.getInstance().disPlay(mMyAvatar, mCurrentUser.getAvatar());
        int borderColor = mCurrentUser.getSex().equals("男") ? Color.parseColor("#36d8ea") :
                mCurrentUser.getSex().equals("女") ? Color.parseColor("#ea665c") : Color.parseColor("#fafafa");
        mMyAvatar.setBorderColor(borderColor);
        mMyAvatar.setBorderWidth(2);
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", target_username);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                ImageManager.getInstance().disPlay(mTargetAvatar, list.get(0).getAvatar());
                mTargetName.setText(list.get(0).getName());
                int borderColor = list.get(0).getSex().equals("男") ? Color.parseColor("#36d8ea") :
                        list.get(0).getSex().equals("女") ? Color.parseColor("#ea665c") : Color.parseColor("#fafafa");
                mTargetAvatar.setBorderColor(borderColor);
                mTargetAvatar.setBorderWidth(2);
            }
        });

    }

    private boolean initNextMovie() {
        try {
            mMovieInfo = mMovieList.pop();
        } catch (NoSuchElementException e) {
            loge(e.getMessage());
            logd("mMovieList.size()" + mMovieList.size());
            return true;
        }
        mMovieNum.setText(movieNum - mMovieList.size() + "/" + movieNum);
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
                MyToast.getInstance().showBottomShortDone(OnlinePlayActivity.this, "");
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

    private void initView() {
        mOne = (ImageView) findViewById(R.id.one);
        mTwo = (ImageView) findViewById(R.id.two);
        mThree = (ImageView) findViewById(R.id.three);
        mTargetScoreViewList.add(0, mOne);
        mTargetScoreViewList.add(1, mTwo);
        mTargetScoreViewList.add(2, mThree);
        mTargetAvatar = (CircleImageView) findViewById(R.id.targetAvatar);
        mXfermodeView = (XfermodeViewP) findViewById(R.id.XfermodeView);
        mImageView = (ImageView) findViewById(R.id.image);
        mMovieNum = (TextView) findViewById(R.id.movieNum);
        mScore = (TextView) findViewById(R.id.score);
        mMyAvatar = (CircleImageView) findViewById(R.id.myAvatar);
        mKeyLayout = (RelativeLayout) findViewById(R.id.key);
        mMscTv = (TextView) findViewById(R.id.mscTv);
        mImageBt = (ImageView) findViewById(R.id.imageBt);
        mTargetName = (TextView) findViewById(R.id.targetName);
        mMyName = (TextView) findViewById(R.id.myName);
        mTargetReadyBt = (Button) findViewById(R.id.targetReadyBt);
        mReadyBt = (Button) findViewById(R.id.readyBt);
        mCountDownTv = (TextView) findViewById(R.id.countDownTv);
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

    }

    public void voice(View view) {
        mySynthesizer.startListening(mRecognizerDialogListener);
    }

    public void ready(View view) {
        logd("target_ready_flag:" + target_ready_flag);
        ((Button) view).setText("已准备");
        final MatchItem matchItem = new MatchItem();
        matchItem.setState(MyConstants.READY_STATE);
        matchItem.update(my_objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, OnlinePlayActivity.this)) {
                    return;
                }
                logd("我已准备");
                my_ready_flag = true;
                if (target_ready_flag) {
                    start();
                }
                handler.postDelayed(runnable, 1000);
            }
        });

    }

    public void forHelp(View view) {
        MyToast.getInstance().showCenterShortWarn(this, "求助");
    }

    public void jump(View view) {
        if (view != null) {
            updateScores(-1);
        }
        endCurrentMovie();
        boolean endFlag = initNextMovie();
        if (endFlag) {
            showPlayDoneDialog();
            if (mCurrentUser != null) {
                updateScore();
            }
            return;
        }
        showKeyAnim();
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
        rightNum.setText("正确率：" + mRightNum + "/" + movieNum);
        sum.setText("总分：" + mSumScore);
        average.setText("平均分：" + mSumScore / movieNum);
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

    private void endCurrentMovie() {
        mKeyLayout.removeAllViews();
        mKeyChar.clear();
        keyList.clear();
    }

    private void updateScore() {
    }

    private void showKeyAnim() {
        chooseKeyNum = -1;
        mKeyLayoutWidth = mKeyLayout.getMeasuredWidth();
        mKeyLayoutHeight = mKeyLayout.getMeasuredHeight();
        mKeyWidth = mKeyLayoutWidth / SCALE_KEY_SCREEN;
        logd("mKeyLayoutWidth=" + mKeyLayoutWidth + " mKeyLayoutHeight=" + mKeyLayoutHeight);
        int xListSize = mKeyLayoutWidth / mKeyWidth;
        List<Integer> xList = new ArrayList<Integer>();
        xList.add(0, mKeyWidth / 2);
        for (int i = 1; i < xListSize / 2; i++) {
            xList.add(i, xList.get(0) + mKeyWidth * i);
        }
        xList.add(xListSize / 2, -xList.get(0));
        for (int i = xListSize / 2 + 1; i < xListSize; i++) {
            xList.add(i, -xList.get(i - 5));
        }
        int yListSize = (mKeyLayoutHeight - KEY_MARGIN_TOP * mKeyWidth) / mKeyWidth;
        List<Integer> yList = new ArrayList<Integer>();
        yList.add(0, mKeyWidth * KEY_MARGIN_TOP);
        for (int i = 1; i < yListSize; i++) {
            yList.add(i, yList.get(0) + mKeyWidth * i);
        }
        Collections.shuffle(xList);
        Collections.shuffle(yList);
        Collections.shuffle(keyList);
        List<Integer> list = RandomUtil.getRepeatRandomNums(10, yList.size());
        for (int i = 0; i < keyList.size(); i++) {
            ObjectAnimator.ofFloat(keyList.get(i), "translationX", 0F, xList.get(i)).setDuration(1000).start();
            ObjectAnimator.ofFloat(keyList.get(i), "translationY", 0F, yList.get(list.get(i))).setDuration(10000).start();
        }
    }

    private TextView creatKeyTextView(char c, int keyTextViewWidth) {
        final TextView textView = new TextView(this);
        textView.setText(c + "");
        textView.setTextSize(DensityUtils.px2sp(this, keyTextViewWidth / 2));
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
                    MyToast.getInstance().showBottomShortWrong(OnlinePlayActivity.this, "");
                    updateScores(-1);
                    jump(null);
                } else {
                    if (chooseKeyNum == mKeyChar.size() - 1) {
                        MyToast.getInstance().showBottomShortDone(OnlinePlayActivity.this, "");
                        int score = Integer.parseInt(mScore.getText().toString());
                        mRightNum++;
                        mSumScore += score;
                        updateScores(score);
                        jump(null);
                    }
                }
            }
        });
        return textView;
    }

    private void updateScores(int score) {
        MatchItem matchItem = new MatchItem();
        scoreList.add(score);
        matchItem.setScores(scoreList);
        matchItem.update(my_objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, OnlinePlayActivity.this)) {
                    return;
                }
                logd("更新分数");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
        new MatchItem().delete(my_objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (checkCommonException(e, OnlinePlayActivity.this)) {
                    return;
                }
                logd("destroy activity delete matchItem id:" + my_objectId);
            }
        });
    }
}
