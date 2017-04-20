package com.example.asus.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yhao on 2017/4/20.
 */

@Entity
public class SingleRecord {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "USERID")
    private String userId;

    @Property(nameInDb = "TYPE")
    private String type;

    @Property(nameInDb = "SUM1")
    private int sum1;

    @Property(nameInDb = "SUM2")
    private int sum2;

    @Property(nameInDb = "SUM3")
    private int sum3;

    @Property(nameInDb = "RSUM1")
    private int rsum1;

    @Property(nameInDb = "RSUM2")
    private int rsum2;

    @Property(nameInDb = "RSUM3")
    private int rsum3;

    @Property(nameInDb = "SUMSCORE1")
    private int sumScore1;

    @Property(nameInDb = "SUMSCORE2")
    private int sumScore2;

    @Property(nameInDb = "SUMSCORE3")
    private int sumScore3;

    @Property(nameInDb = "AVERAGE1")
    private int average1;

    @Property(nameInDb = "AVERAGE2")
    private int average2;

    @Property(nameInDb = "AVERAGE3")
    private int average3;

    @Generated(hash = 528176009)
    public SingleRecord(Long id, String userId, String type, int sum1, int sum2,
            int sum3, int rsum1, int rsum2, int rsum3, int sumScore1, int sumScore2,
            int sumScore3, int average1, int average2, int average3) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.sum1 = sum1;
        this.sum2 = sum2;
        this.sum3 = sum3;
        this.rsum1 = rsum1;
        this.rsum2 = rsum2;
        this.rsum3 = rsum3;
        this.sumScore1 = sumScore1;
        this.sumScore2 = sumScore2;
        this.sumScore3 = sumScore3;
        this.average1 = average1;
        this.average2 = average2;
        this.average3 = average3;
    }

    @Override
    public String toString() {
        return "SingleRecord{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
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

    @Generated(hash = 1328547924)
    public SingleRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSum1() {
        return this.sum1;
    }

    public void setSum1(int sum1) {
        this.sum1 = sum1;
    }

    public int getSum2() {
        return this.sum2;
    }

    public void setSum2(int sum2) {
        this.sum2 = sum2;
    }

    public int getSum3() {
        return this.sum3;
    }

    public void setSum3(int sum3) {
        this.sum3 = sum3;
    }

    public int getRsum1() {
        return this.rsum1;
    }

    public void setRsum1(int rsum1) {
        this.rsum1 = rsum1;
    }

    public int getRsum2() {
        return this.rsum2;
    }

    public void setRsum2(int rsum2) {
        this.rsum2 = rsum2;
    }

    public int getRsum3() {
        return this.rsum3;
    }

    public void setRsum3(int rsum3) {
        this.rsum3 = rsum3;
    }

    public int getSumScore1() {
        return this.sumScore1;
    }

    public void setSumScore1(int sumScore1) {
        this.sumScore1 = sumScore1;
    }

    public int getSumScore2() {
        return this.sumScore2;
    }

    public void setSumScore2(int sumScore2) {
        this.sumScore2 = sumScore2;
    }

    public int getSumScore3() {
        return this.sumScore3;
    }

    public void setSumScore3(int sumScore3) {
        this.sumScore3 = sumScore3;
    }

    public int getAverage1() {
        return this.average1;
    }

    public void setAverage1(int average1) {
        this.average1 = average1;
    }

    public int getAverage2() {
        return this.average2;
    }

    public void setAverage2(int average2) {
        this.average2 = average2;
    }

    public int getAverage3() {
        return this.average3;
    }

    public void setAverage3(int average3) {
        this.average3 = average3;
    }


}
