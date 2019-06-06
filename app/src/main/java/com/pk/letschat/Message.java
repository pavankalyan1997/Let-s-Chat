package com.pk.letschat;

public class Message {
    private String message;
    private String messageStatus;
    private String messageType;
    private String receiver;
    private String sender;
    private String time;


    public Message(String message, String messageStatus, String messageType, String receiver, String sender, String time) {
        this.message = message;
        this.messageStatus = messageStatus;
        this.messageType = messageType;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
