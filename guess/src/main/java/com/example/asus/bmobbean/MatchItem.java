package com.example.asus.bmobbean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by yhao on 2017/1/24.
 */

public class MatchItem extends BmobObject {
    private String username;
    private String state;
    private String targetID;
    private String targetUsername;
    private String movieType;
    private String difficult;
    private List<Integer> skips;
    private List<Integer> scores;


    @Override
    public String toString() {
        return "MatchItem{" +
                "username='" + username + '\'' +
                ", state='" + state + '\'' +
                ", targetID='" + targetID + '\'' +
                ", targetUsername='" + targetUsername + '\'' +
                ", movieType='" + movieType + '\'' +
                ", difficult='" + difficult + '\'' +
                ", skips=" + skips +
                ", scores=" + scores +
                '}';
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
