package com.example.pro1121_gr.model;

import android.graphics.Typeface;

import com.google.firebase.Timestamp;

public class chatMesseageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private CustomTypefaceInfo typeface;

    public chatMesseageModel(String message, String senderId, Timestamp timestamp, CustomTypefaceInfo typeface) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.typeface = typeface;
    }

    public chatMesseageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public CustomTypefaceInfo getTypeface() {
        return typeface;
    }

    public void setTypeface(CustomTypefaceInfo typeface) {
        this.typeface = typeface;
    }
}
