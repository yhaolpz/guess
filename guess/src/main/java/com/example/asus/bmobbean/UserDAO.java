package com.example.asus.bmobbean;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.asus.common.MyConstants;
import com.example.asus.util.SPUtil;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yinghao on 2017/1/16.
 * Email：756232212@qq.com
 */

public class UserDAO {

    public static final String FILE_NAME = "user_data";


    public static void saveUserToSP(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MyConstants.OBJECT_ID, user.getObjectId());
        editor.putString(MyConstants.USERNAME_KEY, user.getUsername());
        editor.putString(MyConstants.TYPE_KEY, user.getType());
        editor.putString(MyConstants.NAME_KEY, user.getName());
        editor.putInt(MyConstants.AGE_KEY, user.getAge());
        editor.putString(MyConstants.SEX_KEY, user.getSex());
        editor.putString(MyConstants.CITY_KEY, user.getCity());
        editor.putString(MyConstants.EMAIL_KEY, user.getEmail());
        if (user.getAvatar() != null) {
            editor.putString(MyConstants.AVATAR_URL_KEY, user.getAvatar().getFileUrl());
        }
        SPUtil.SharedPreferencesCompat.apply(editor);
    }

    public static void removeUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(MyConstants.OBJECT_ID);
        editor.remove(MyConstants.USERNAME_KEY);
        editor.remove(MyConstants.TYPE_KEY);
        editor.remove(MyConstants.NAME_KEY);
        editor.remove(MyConstants.AGE_KEY);
        editor.remove(MyConstants.SEX_KEY);
        editor.remove(MyConstants.CITY_KEY);
        editor.remove(MyConstants.EMAIL_KEY);
        editor.remove(MyConstants.AVATAR_URL_KEY);
        SPUtil.SharedPreferencesCompat.apply(editor);
    }

    public static void saveUserAvatarToSP(Context context, BmobFile avatar) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MyConstants.AVATAR_URL_KEY, avatar.getFileUrl());
        SPUtil.SharedPreferencesCompat.apply(editor);
    }

    public static User getUserFromSP(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setObjectId(sp.getString(MyConstants.OBJECT_ID, null));
        user.setUsername(sp.getString(MyConstants.USERNAME_KEY, null));
        user.setType(sp.getString(MyConstants.TYPE_KEY, null));
        user.setName(sp.getString(MyConstants.NAME_KEY, null));
        user.setAge(sp.getInt(MyConstants.AGE_KEY, 0));
        user.setSex(sp.getString(MyConstants.SEX_KEY, null));
        user.setCity(sp.getString(MyConstants.CITY_KEY, null));
        user.setEmail(sp.getString(MyConstants.EMAIL_KEY, null));
        if (sp.getString(MyConstants.AVATAR_URL_KEY, null) != null) {
            user.setAvatar(new BmobFile("avatar.jpg", null, sp.getString(MyConstants.AVATAR_URL_KEY, null)));
        }
        return user;
    }

    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(MyConstants.USERNAME_KEY, "");
    }

    public static boolean alreadyLogin(Context context) {
        return SPUtil.contains(context, MyConstants.OBJECT_ID);
    }

}
