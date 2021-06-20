package com.infosec.rhino.Models;

public class User {

    private String uid,phoneNumber, name;

    public User() {
    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(String uid,String phoneNumber, String name) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
