package com.pk.letschat.DBCalls;


public class MessagesTable {
    private String accountID;
    private String token;
    private String timeStamp;
    private String message;
    private String TIME;
    private String SENDER;
    private String RECEIVER;
    private String MESSAGE_STATUS;
    private String MESSAGE_TYPE;

    public MessagesTable() {
    }

    public MessagesTable(String accountID,String token, String timeStamp, String message, String TIME, String SENDER, String RECEIVER, String MESSAGE_STATUS, String MESSAGE_TYPE) {
        this.accountID=accountID;
        this.token = token;
        this.timeStamp = timeStamp;
        this.message = message;
        this.TIME = TIME;
        this.SENDER = SENDER;
        this.RECEIVER = RECEIVER;
        this.MESSAGE_STATUS = MESSAGE_STATUS;
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getSENDER() {
        return SENDER;
    }

    public void setSENDER(String SENDER) {
        this.SENDER = SENDER;
    }

    public String getRECEIVER() {
        return RECEIVER;
    }

    public void setRECEIVER(String RECEIVER) {
        this.RECEIVER = RECEIVER;
    }

    public String getMESSAGE_STATUS() {
        return MESSAGE_STATUS;
    }

    public void setMESSAGE_STATUS(String MESSAGE_STATUS) {
        this.MESSAGE_STATUS = MESSAGE_STATUS;
    }

    public String getMESSAGE_TYPE() {
        return MESSAGE_TYPE;
    }

    public void setMESSAGE_TYPE(String MESSAGE_TYPE) {
        this.MESSAGE_TYPE = MESSAGE_TYPE;
    }
}
