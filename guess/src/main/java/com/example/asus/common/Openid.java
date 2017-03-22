package com.example.asus.common;

/**
 * Created by Ahab on 2016/10/30.
 */
public class Openid {
    private String openid;
    private String access_token;
    private long expires_in;

    public String getOpenid() {
        return openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public long getExpires_in_save() {

        return System.currentTimeMillis() + expires_in * 1000;
    }
    public long getExpires_in_load() {

        return (expires_in -System.currentTimeMillis()) / 1000;
    }

    public String getExpires_in() {
        return expires_in+"";
    }


    @Override
    public String toString() {
        return "Openid{" +
                "openid='" + openid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
