package com.example.asus.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yhao on 2017/1/1.
 */

public class MD5Util {

    public static String getMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(str.getBytes());
        byte[] m = md5.digest(); //加密
        return getString(m);
    }

    private static String getString(byte[] b){
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(aB);
        }
        return sb.toString();
    }
}
