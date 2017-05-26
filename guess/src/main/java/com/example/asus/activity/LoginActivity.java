package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.common.Openid;
import com.example.asus.util.SPUtil;
import com.example.asus.util.ValidateUtil;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.changeskin.SkinManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends MySwipeBackActivity {
    private EditText mUsername;
    private EditText mPassword;
    private static final int reqCode_register = 1;
    private static final int reqCode_findPass = 2;

    private BaseApplication mApplication;

    //qq
    private Tencent mTencent;//QQ主要操作对象
    private IUiListener loginListener;//授权登录回调监听器
    private IUiListener userInfoListener; //获取用户信息监听器
    private UserInfo qqUserInfo;

    //weibo
    private SsoHandler mSsoHandler;
    private WbAuthListener mWbAuthListener;
    private AsyncWeiboRunner mAsyncWeiboRunner;
    private RequestListener mUserListener;


    public static int resultCode_login = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().register(this);
        setContentView(R.layout.activity_login);
        mApplication = (BaseApplication) getApplication();
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mUsername.setText(UserDAO.getUserName(this));
    }


    private void initWeibo() {
        mSsoHandler = new SsoHandler(this);
        mAsyncWeiboRunner = new AsyncWeiboRunner(getApplicationContext());
        mWbAuthListener = new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                logd(oauth2AccessToken.toString());
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(
                        BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIBO,
                        oauth2AccessToken.getToken(),
                        oauth2AccessToken.getExpiresTime() + "",
                        oauth2AccessToken.getUid());
                WeiboParameters params = new WeiboParameters(MyConstants.WEIBO_APP_KEY);
                params.put("access_token", oauth2AccessToken.getToken());
                params.put("uid", oauth2AccessToken.getUid());
                logd("授权登录成功");
                loginWithAuth(authInfo, "weibo", params);
            }

            @Override
            public void cancel() {

            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                loge(wbConnectErrorMessage.toString());
            }
        };

        mUserListener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jo = new JSONObject(response);
                        final User user = User.getCurrentUser(User.class);
                        logd(user.toString());
                        if (user.getScore1() == null) {
                            //当第一次登录
                            user.setName(jo.getString("screen_name"));
                            user.setSex(jo.getString("gender").equals("m") ? "男" : "女");
                            user.setCity(jo.getString("location"));
                            user.setScore1(1000);
                            user.setScore2(1000);
                            user.setScore3(1000);
                            user.setAge(20);
                            user.setAvatar(new BmobFile("avatar.jpg", null, jo.getString("avatar_large")));
                            user.setType("weibo");
                        }
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                hideProgressbar();
                                if (checkCommonException(e, LoginActivity.this)) {
                                    return;
                                }
                                mApplication.setUser(user);
                                goHome();
                            }
                        });
                    } catch (JSONException e) {
                        loge(e.toString());
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                loge(e.toString());

            }
        };
    }

    private void initQQ() {
        mTencent = mApplication.getTencent();
        Openid o = loadOpenid();
        if (o == null || o.getExpires_in_load() <= 0) {
            //第一次登录或登录过期
        } else {
            mTencent.setOpenId(o.getOpenid());
            mTencent.setAccessToken(o.getAccess_token(), "" + o.getExpires_in_load());
        }
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object value) {
                Gson gson = new Gson();
                Openid openid = gson.fromJson(value.toString(), Openid.class);
                saveOpenid(openid);
                logd(value.toString());
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(
                        BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ,
                        openid.getAccess_token(),
                        openid.getExpires_in(),
                        openid.getOpenid());
                loginWithAuth(authInfo, "qq", null);
            }

            @Override
            public void onError(UiError uiError) {
                loge(uiError.errorDetail);
            }

            @Override
            public void onCancel() {
            }
        };

        userInfoListener = new IUiListener() {
            @Override
            public void onError(UiError arg0) {
                loge("获取qq用户信息失败");
            }

            @Override
            public void onComplete(Object arg0) {
                if (arg0 == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    final User user = User.getCurrentUser(User.class);//只有object id
                    if (TextUtils.isEmpty(user.getName())) {
                        user.setName(jo.getString("nickname"));
                    }
                    if (TextUtils.isEmpty(user.getSex())) {
                        user.setSex(jo.getString("gender"));
                    }
                    if (TextUtils.isEmpty(user.getCity())) {
                        user.setCity(jo.getString("city"));
                    }
                    if (null == user.getScore1()) {
                        user.setScore1(1000);
                    }
                    if (null == user.getScore2()) {
                        user.setScore2(1000);
                    }
                    if (null == user.getScore3()) {
                        user.setScore3(1000);
                    }
                    if (null == user.getAge()) {
                        user.setAge(20);
                    }
                    if (user.getAvatar() == null) {
                        user.setAvatar(new BmobFile("avatar.jpg", null, jo.getString("figureurl_qq_2")));
                    }
                    user.setType("qq");
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            hideProgressbar();
                            if (checkCommonException(e, LoginActivity.this)) {
                                return;
                            }
                            mApplication.setUser(user);
                            goHome();
                        }
                    });
                } catch (Exception e) {
                    loge(e.getMessage());
                }finally {
                    hideProgressbar();
                }
            }

            @Override
            public void onCancel() {
            }
        };
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, reqCode_register);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void goHome() {
        setResult(resultCode_login);
        finish();
    }


    public void findPass(View view) {
        Intent intent = new Intent(this, FindPassActivity.class);
        startActivityForResult(intent, reqCode_findPass);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    // bmob账号登录
    public void login(View view) {
        if (TextUtils.isEmpty(mUsername.getText())) {
            MyToast.getInstance().showShortWarn(LoginActivity.this, "请输入邮箱");
        } else if (!ValidateUtil.checkEmail(mUsername.getText().toString())) {
            MyToast.getInstance().showShortWarn(LoginActivity.this, "邮箱格式错误");
        } else if (TextUtils.isEmpty(mPassword.getText())) {
            MyToast.getInstance().showShortWarn(LoginActivity.this, "请输入密码");
        } else if (mPassword.getText().toString().length() < 6) {
            MyToast.getInstance().showShortWarn(LoginActivity.this, "密码最少为六位");
        } else {
            showProgressbarWithText("正在登录");
            User user = new User();
            user.setUsername(mUsername.getText().toString());
            user.setPassword(mPassword.getText().toString());
            user.login(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    hideProgressbar();
                    mApplication.setUser(user);
                    if (e == null) {
                        goHome();
                    } else if (e.getErrorCode() == ERROR_CODE_USERNAME_OR_PASSWORD_ERROR) {
                        MyToast.getInstance().showShortWarn(LoginActivity.this, "账号/密码错误");
                    } else {
                        checkCommonException(e, LoginActivity.this);
                    }
                }
            });
        }
    }

    //第三方账号关联登录或注册bmob账号
    private void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo, final String type, final WeiboParameters params) {
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject jsonObject, BmobException e) {
                if (e == null) {
                    if (type.equals("qq")) {
                        qqUserInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        qqUserInfo.getUserInfo(userInfoListener);
                    } else if (type.equals("weibo")) {
                        mAsyncWeiboRunner.requestAsync(MyConstants.WEIBO_USERINFO_URL, params, "GET", mUserListener);
                    }
                } else {
                    loge("loginWithAuth 关联登录失败");
                    hideProgressbar();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCode_register && resultCode == RESULT_OK) {
            String username = data.getStringExtra("USERNAME");
            mUsername.setText(username);
            mPassword.setText("");
            mPassword.requestFocus();
            return;
        }

        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void weiboLogin(View view) {
        showProgressbarWithText("请稍等");
        initWeibo();
        mSsoHandler.authorize(mWbAuthListener);
    }


    public void qqLogin(View view) {
        showProgressbarWithText("请稍等");
        initQQ();
        mTencent.login(this, "all", loginListener);
    }

    private Openid loadOpenid() {
        String openid = (String) SPUtil.get(this, "OPENID", "");
        Openid o = new Openid();
        if (openid.equals("")) {
            return null;
        } else {
            o.setOpenid(openid);
            o.setAccess_token((String) SPUtil.get(this, "ACCESS_TOKEN", ""));
            o.setExpires_in((Long) SPUtil.get(this, "EXPIRES_IN", 0L));
            return o;
        }
    }

    private void saveOpenid(Openid openid) {
        SPUtil.put(this, "OPENID", openid.getOpenid());
        SPUtil.put(this, "ACCESS_TOKEN", openid.getAccess_token());
        SPUtil.put(this, "EXPIRES_IN", openid.getExpires_in_save());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTencent != null) { //处理内存泄漏
            mTencent = null;
            loginListener = null;
            userInfoListener = null;
        }
        SkinManager.getInstance().unregister(this);
    }

}
