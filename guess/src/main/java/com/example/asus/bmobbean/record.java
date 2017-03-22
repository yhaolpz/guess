package com.example.asus.bmobbean;

import cn.bmob.v3.BmobObject;

/**
 * Created by yinghao on 2017/1/20.
 * Email：756232212@qq.com
 */

public class record extends BmobObject {

    //用户账号
    private String username;

    //电影类型
    private String type;

    //答题数  （简单/一般/困难         下同）
    private int sum1;
    private int sum2;
    private int sum3;

    //答题正确数
    private int rsum1;
    private int rsum2;
    private int rsum3;

    //总分
    private int sumScore1;
    private int sumScore2;
    private int sumScore3;

    //平均分
    private int average1;
    private int average2;
    private int average3;


    @Override
    public String toString() {
        return "record{" +
                "username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", sum1=" + sum1 +
                ", sum2=" + sum2 +
                ", sum3=" + sum3 +
                ", rsum1=" + rsum1 +
                ", rsum2=" + rsum2 +
                ", rsum3=" + rsum3 +
                ", sumScore1=" + sumScore1 +
                ", sumScore2=" + sumScore2 +
                ", sumScore3=" + sumScore3 +
                ", average1=" + average1 +
                ", average2=" + average2 +
                ", average3=" + average3 +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSum1() {
        return sum1;
    }

    public void setSum1(int sum1) {
        this.sum1 = sum1;
    }

    public int getSum2() {
        return sum2;
    }

    public void setSum2(int sum2) {
        this.sum2 = sum2;
    }

    public int getSum3() {
        return sum3;
    }

    public void setSum3(int sum3) {
        this.sum3 = sum3;
    }

    public int getRsum1() {
        return rsum1;
    }

    public void setRsum1(int rsum1) {
        this.rsum1 = rsum1;
    }

    public int getRsum2() {
        return rsum2;
    }

    public void setRsum2(int rsum2) {
        this.rsum2 = rsum2;
    }

    public int getRsum3() {
        return rsum3;
    }

    public void setRsum3(int rsum3) {
        this.rsum3 = rsum3;
    }

    public int getSumScore1() {
        return sumScore1;
    }

    public void setSumScore1(int sumScore1) {
        this.sumScore1 = sumScore1;
    }

    public int getSumScore2() {
        return sumScore2;
    }

    public void setSumScore2(int sumScore2) {
        this.sumScore2 = sumScore2;
    }

    public int getSumScore3() {
        return sumScore3;
    }

    public void setSumScore3(int sumScore3) {
        this.sumScore3 = sumScore3;
    }

    public int getAverage1() {
        return average1;
    }

    public void setAverage1(int average1) {
        this.average1 = average1;
    }

    public int getAverage2() {
        return average2;
    }

    public void setAverage2(int average2) {
        this.average2 = average2;
    }

    public int getAverage3() {
        return average3;
    }

    public void setAverage3(int average3) {
        this.average3 = average3;
    }
}
