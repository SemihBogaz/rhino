package com.infosec.rhino.Security;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private String recipient, text, AESKey;

    protected Message() {

    }

    protected Message(String text, String AESKey) {
        this.text = text;
        this.AESKey = AESKey;
    }

    public Map<Object, Object> toMap() {
        Map<Object, Object> map = new HashMap<>();
        map.put("recipient", this.recipient);
        map.put("text", this.text);
        map.put("AESKey", this.AESKey);
        return map;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAESKey() {
        return AESKey;
    }

    public void setAESKey(String AESKey) {
        this.AESKey = AESKey;
    }
}
