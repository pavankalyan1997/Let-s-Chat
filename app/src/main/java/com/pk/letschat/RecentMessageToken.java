package com.pk.letschat;

public class RecentMessageToken {
    String token;
    String timeStamp;

    public RecentMessageToken(String token, String timeStamp) {
        this.token = token;
        this.timeStamp = timeStamp;
    }

    public RecentMessageToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
