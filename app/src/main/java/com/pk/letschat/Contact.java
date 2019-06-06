package com.pk.letschat;

public class Contact{
    private String contactName;
    private String phoneNumber;
    private String uid;
    private String Status;
    private String contactPic;

    public Contact() {
    }

    public Contact(String contactName, String phoneNumber, String uid, String status, String contactPic) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        Status = status;
        this.contactPic = contactPic;
    }

    public String getContactPic() {
        return contactPic;
    }

    public void setContactPic(String contactPic) {
        this.contactPic = contactPic;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
