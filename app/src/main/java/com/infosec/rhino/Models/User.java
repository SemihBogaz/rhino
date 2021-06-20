package com.infosec.rhino.Models;

public class User {

    private String uid, phoneNumber, name, publicKey;

    public User() {
    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(String uid, String phoneNumber, String name, String publicKey) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.publicKey = publicKey;
    }

    public String getuid() { return uid; }

    public void setuid(String uid) { this.uid = uid; }

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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
