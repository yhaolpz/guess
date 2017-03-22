package com.example.asus.bmobbean;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by yhao on 2016/12/28.
 */

public class User extends BmobUser {
    private String name;
    private String sex;
    private String city;
    private Integer age;
    private String type;//bmob qq weibo
    private BmobFile avatar;

    @Override
    public String toString() {
        return "User{" +
                "type='" + type + '\'' +
                "id='" + getObjectId() + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", sex='" + sex + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
