package com.example.asus.bmobbean;

import cn.bmob.v3.BmobObject;

/**
 * Created by yhao on 2017/4/25.
 *
 */

public class DoubleRecord extends BmobObject {

    private User user;

    private int score1; //简单
    private int score2; //一般
    private int score3; //困难

    public DoubleRecord(User user, int score1, int score2, int score3) {
        this.user = user;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
    }

    @Override
    public String toString() {
        return "DoubleRecord{" +
                "user=" + user +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", score3=" + score3 +
                '}';
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public int getScore3() {
        return score3;
    }

    public void setScore3(int score3) {
        this.score3 = score3;
    }
}
