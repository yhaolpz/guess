package com.example.asus.bmobbean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by yhao on 2017/1/24.
 */

public class MatchItem extends BmobObject {
    private String userId;
    private String state;
    private String targetID;
    private String targetUserId;
    private String movieType;
    private String difficult;
    private String message;
    private List<Integer> skips;
    private List<Integer> scores;


    @Override
    public String toString() {
        return "MatchItem{" +
                "userId='" + userId + '\'' +
                ", state='" + state + '\'' +
                ", targetID='" + targetID + '\'' +
                ", targetUserId='" + targetUserId + '\'' +
                ", movieType='" + movieType + '\'' +
                ", difficult='" + difficult + '\'' +
                ", message='" + message + '\'' +
                ", skips=" + skips +
                ", scores=" + scores +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public List<Integer> getSkips() {
        return skips;
    }

    public void setSkips(List<Integer> skips) {
        this.skips = skips;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }
}
