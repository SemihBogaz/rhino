package com.infosec.rhino;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.infosec.rhino.Security.Cryptography;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String phoneNumber, name, publicKey;

    public User() {

    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public User(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        try {
            KeyPair keyPair = Cryptography.generateNewRSAPair();
            this.publicKey = Cryptography.encodeToBase64(keyPair.getPublic());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.publicKey = null;
        }
    }

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

    public Map<Object, Object> toMap() {
        Map<Object, Object> result = new HashMap<>();
        result.put("phoneNumber", phoneNumber);
        result.put("name", name);
        result.put("publicKey", publicKey);
        return result;
    }
}
