package com.example.asus.bmobbean;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2016/12/28.
 */

public class movieInfo extends BmobObject {
    private String movieName;
    private String key;
    private BmobFile image;
    private List<String> types;


    @Override
    public String toString() {
        return "movieInfo{" +
                ", key='" + key + '\'' +
                ", movieName='" + movieName + '\'' +
                '}';
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
