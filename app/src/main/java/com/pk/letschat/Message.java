package com.pk.letschat;

public class Message {
    private String message;
    private String time;
    private String messageType;
    private String receiver;
    private String sender;
    private String messageStatus;

    public Message(String message, String time, String messageType, String receiver, String sender, String messageStatus) {
        this.message = message;
        this.time = time;
        this.messageType = messageType;
        this.receiver = receiver;
        this.sender = sender;
        this.messageStatus = messageStatus;

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
