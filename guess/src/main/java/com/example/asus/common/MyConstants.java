package com.example.asus.common;

import com.example.asus.activity.R;

import java.util.HashMap;
import java.util.Map;

public interface MyConstants {
    /**
     * 微博
     */
    String WEIBO_APP_KEY = "2665363319";
    //当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
    String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    // WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
    String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    String WEIBO_USERINFO_URL = "https://api.weibo.com/2/users/show.json";


    /**
     * QQ
     */
    String QQ_APPID = "1105715199";


    /**
     * 游戏内容相关
     */
    String[] movieTypes = {
            "随意", "剧情", "漫威",
            "科幻", "惊悚", "犯罪",
            "爱情", "喜剧", "动作",
            "动漫", "美剧", "韩剧",
            "英剧"};
    String[] difficults = {"简单", "一般", "困难"};
    int[] blurRadius = {0, 5, 25};

    String LOOK_STATE = "look"; //寻找对手
    String SAW_STATE = "saw";    //找到对手
    String READY_STATE = "ready"; //准备
    String PLAYING_STATE = "playing"; //开始


    /**
     * 背景音乐
     */
    String[] musics = {
            "喜剧之王",
            "权利的游戏",
            "电锯惊魂",
            "关闭"
    };

    /**
     * 皮肤
     */
    String[] skins = {
            "default",
            "black"
    };

    /**
     * 设置 sp key
     */
    String IS_FIRST_IN_APP_SET_SP_KEY = "isFirstIn";
    String MOVIE_NUM_SET_SP_KEY = "movieNum";
    String PLAY_MUSIC_SET_SP_KEY = "playMusic";   //存储 0 1 2 3 对应 musics
    String SKIN_SET_SP_KEY = "skin";   //存储 "default", "black"


    /**
     * 用户信息  key
     */
    String OBJECT_ID = "object_id";
    String USERNAME_KEY = "username";
    String TYPE_KEY = "type";
    String NAME_KEY = "name";
    String SEX_KEY = "sex";
    String AGE_KEY = "age";
    String CITY_KEY = "city";
    String AVATAR_URL_KEY = "avatarUrl";
    String EMAIL_KEY = "email";


}