package com.example.asus.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yhao on 2017/4/26.
 */

@Entity
public class DoubleRecord {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "USERID")
    private String userId;

    @Property(nameInDb = "TARGETID")
    private String targetId;

    @Property(nameInDb = "DIFFCULT")
    private String diffcult;

    @Property(nameInDb = "TIME")
    private Date time;

    @Property(nameInDb = "SCORE")
    private int score;

    @Generated(hash = 1570570533)
    public DoubleRecord(Long id, String userId, String targetId, String diffcult,
            Date time, int score) {
        this.id = id;
        this.userId = userId;
        this.targetId = targetId;
        this.diffcult = diffcult;
        this.time = time;
        this.score = score;
    }

    @Generated(hash = 607814573)
    public DoubleRecord() {
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

    public String getTargetId() {
        return this.targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDiffcult() {
        return this.diffcult;
    }

    public void setDiffcult(String diffcult) {
        this.diffcult = diffcult;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }




}
