package com.infosec.rhino.Models;

public class Message {
    private String messageId, text, senderId, RecipientId, AESKey, RSAPublicKey;
    private boolean isEncrypted;
    private long timestamp;

    public Message() {
    }

    public Message(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAESKey() {
        return AESKey;
    }

    public void setAESKey(String AESKey) {
        this.AESKey = AESKey;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getRecipientId() {
        return RecipientId;
    }

    public void setRecipientId(String recipientId) {
        RecipientId = recipientId;
    }

    public String getRSAPublicKey() {
        return RSAPublicKey;
    }

    public void setRSAPublicKey(String RSAPublicKey) {
        this.RSAPublicKey = RSAPublicKey;
    }
}
