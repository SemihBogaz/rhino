package com.infosec.rhino.Security;

public class Message {

    private String recipient, text, AESKey;

    protected Message(String text, String AESKey) {
        this.text = text;
        this.AESKey = AESKey;
    }


}
