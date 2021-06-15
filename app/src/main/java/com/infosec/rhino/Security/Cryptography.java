package com.infosec.rhino.Security;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.RequiresApi;

import com.infosec.rhino.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Cryptography {

    private static final String RSA = "RSA";
    private static final String AES = "RSA";
    private static final String RSA_SAVE_LOCATION = "rsa_key.txt";
    private static final String AES_SAVE_LOCATION = "aes_key.txt";
    private final Context context;

    public Cryptography(Context context) {
        this.context = context;
    }

    public static KeyPair generateNewRSAPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static SecretKey generateNewAESKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        return generator.generateKey();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveKey(Context context, Key key, String filename) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        byte[] encodedKey = Base64.getEncoder().encode(key.getEncoded());
        outputStream.write(encodedKey);
        outputStream.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encodeToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public void updateAES() {

    }

    /**
     * Restores a RSA public key from an Base64-encoded String.
     * @param key A Public Key string in Base64.
     * @return A new Public Key for RSA. Null if generation fails.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getRSAPublicKey(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Restores a RSA private key from an Base64-encoded String.
     * @param key A private Key string in Base64.
     * @return A new private Ley for RSA. Null if generation fails.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey getRSAPrivateKey(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509privateKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePrivate(X509privateKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Encrypts given text using own Secret AES key and recipient's public RSA key.
     * @param text The string to be encrypted.
     * @param recipient The user that the message will be delivered to.
     * @return A new message object with the encrypted text and the encrypted AES key.
     **/
    public Message encryptMessage(String text, User recipient) {

        return new Message("", "");
    }

}
