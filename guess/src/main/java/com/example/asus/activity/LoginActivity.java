package com.example.asus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.asus.bmobbean.User;
import com.example.asus.bmobbean.UserDAO;
import com.example.asus.common.BaseActivity;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MyConstants;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.common.Openid;
import com.example.asus.util.SPUtil;
import com.example.asus.util.ValidateUtil;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
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
    private String scope;//应用需要获得哪些API的权限
    private UserInfo qqUserInfo;

    //weibo
    private AuthInfo mAuthInfo;
    private WeiboAuthListener weiboAuthListener;
    private SsoHandler mSsoHandler;
    private AsyncWeiboRunner mAsyncWeiboRunner;
    private RequestListener mUserListener;

    public static int resultCode_login = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mApplication = (BaseApplication) getApplication();
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mUsername.setText(UserDAO.getUserName(this));
    }


    private void initWeibo() {
        mAuthInfo = new AuthInfo(this, MyConstants.WEIBO_APP_KEY, MyConstants.WEIBO_REDIRECT_URL, MyConstants.WEIBO_SCOPE);
        mAsyncWeiboRunner = new AsyncWeiboRunner(this);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        weiboAuthListener = new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                logd(mAccessToken.toString());
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(
                        BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIBO,
                        mAccessToken.getToken(),
                        mAccessToken.getExpiresTime() + "",
                        mAccessToken.getUid());
                WeiboParameters params = new WeiboParameters(MyConstants.WEIBO_APP_KEY);
                params.add("access_token", mAccessToken.getToken());
                params.add("uid", mAccessToken.getUid());
                loginWithAuth(authInfo, "weibo", params);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                loge(e.getMessage());
            }

            @Override
            public void onCancel() {
            }
        };

        mUserListener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                hideProgressbar();
                if (!TextUtils.isEmpty(response)) {
                    // 调用 User#parse 将JSON串解析成User对象
                    logd("response:" + response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        User user = User.getCurrentUser(User.class);
                        if (TextUtils.isEmpty(user.getName())) {
                            user.setName(jo.getString("name"));
                        }
                        if (TextUtils.isEmpty(user.getSex())) {
                            user.setSex(jo.getString("gender").equals("m") ? "男" : "女");
                        }
                        if (TextUtils.isEmpty(user.getCity())) {
                            user.setCity(jo.getString("location"));
                        }
                        if (user.getAvatar() == null) {
                            user.setAvatar(new BmobFile("avatar.jpg", null, jo.getString("avatar_large")));
                        }
                        user.setType("weibo");
                        logd("changed:" + user.toString());
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    logd("bmob账户更新weibo账号相关信息成功");
                                } else {
                                    loge("bmob账户更新weibo账号相关信息失败");
                                    checkCommonException(e, LoginActivity.this);
                                }
                            }
                        });
                        mApplication.setUser(user);
                        goHome();
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
        mTencent = Tencent.createInstance(MyConstants.QQ_APPID, this.getApplicationContext());
        scope = "all";
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
                hideProgressbar();
                if (arg0 == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    final User user = User.getCurrentUser(User.class);//只有object id
                    logd("getCurrentUser:" + user.toString());
                    logd("jo:" + jo.toString());
                    if (TextUtils.isEmpty(user.getName())) {
                        user.setName(jo.getString("nickname"));
                    }
                    if (TextUtils.isEmpty(user.getSex())) {
                        user.setSex(jo.getString("gender"));
                    }
                    if (TextUtils.isEmpty(user.getCity())) {
                        user.setCity(jo.getString("city"));
                    }
                    if (user.getAvatar() == null) {
                        user.setAvatar(new BmobFile("avatar.jpg", null, jo.getString("figureurl_qq_2")));
                    }
                    user.setType("qq");
                    logd("changed:" + user.toString());
                    user.update(user.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            //回调两次done
                            if (e == null) {
                                logd("bmob账户更新qq账号相关信息成功");
                                mApplication.setUser(user);
                                goHome();
                            }else{
//                                loge("bmob账户更新qq账号相关信息失败");
//                                checkCommonException(e, LoginActivity.this);
                            }
                        }
                    });
                } catch (Exception e) {
                    loge(e.getMessage());
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
        logd("go home");
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
            User user = new User();
            user.setUsername(mUsername.getText().toString());
            user.setPassword(mPassword.getText().toString());
            showProgressbar();
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
                    logd("关联成功登录 type:" + type);
                    if (type.equals("qq")) {
                        qqUserInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        qqUserInfo.getUserInfo(userInfoListener);
                    } else if (type.equals("weibo")) {
                        mAsyncWeiboRunner.requestAsync(MyConstants.WEIBO_USERINFO_URL,
                                params, "GET", mUserListener);
                    }
                } else {
                    loge("关联登录失败");
                    loge(e.toString());
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
                showProgressbar();
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (mSsoHandler != null) {
            showProgressbar();
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void weiboLogin(View view) {
        showProgressbar();
        initWeibo();
        mSsoHandler.authorize(weiboAuthListener);
    }


    public void qqLogin(View view) {
        showProgressbar();
        initQQ();
        mTencent.login(this, scope, loginListener);
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

}
