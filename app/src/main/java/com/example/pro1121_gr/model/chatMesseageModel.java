package com.example.pro1121_gr.model;

import com.google.firebase.Timestamp;

public class chatMesseageModel {
    private String messageID;
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private CustomTypefaceInfo typeface;

    public chatMesseageModel(String messageID,String message, String senderId, Timestamp timestamp, CustomTypefaceInfo typeface) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.typeface = typeface;
        this.messageID = messageID;
    }

    public chatMesseageModel() {
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
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
