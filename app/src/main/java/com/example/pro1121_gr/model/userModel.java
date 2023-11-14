package com.example.pro1121_gr.model;

import com.google.firebase.Timestamp;

public class userModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;

    private String date;
    private String userId;

    private String FCMtoken;

    public userModel() {
    }

    public userModel(String phoneNumber, String userName, Timestamp now, String date) {
    }

    public userModel(String phone, String username, Timestamp createdTimestamp, String date, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.date = date;
        this.userId = userId;
    }

    public userModel(String phone, String username, Timestamp createdTimestamp, String date, String userId, String FCMtoken) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.date = date;
        this.userId = userId;
        this.FCMtoken = FCMtoken;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFMCToken() {
        return FCMtoken;
    }

    public void setFMCToken(String FMCToken) {
        this.FCMtoken = FMCToken;
    }
}
