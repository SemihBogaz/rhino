package com.infosec.rhino.Security;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;

import com.infosec.rhino.Models.Message;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("unused")
public final class Cryptography {

    private static Cryptography cryptography;

    private static final String RSA = "RSA";
    private static final String RSAwPadding = "RSA/ECB/PKCS1Padding";
    private static final String AES = "AES";
    private static final String RSA_PUBLIC_SAVE_LOCATION = "rsa_public_key.txt";
    private static final String RSA_PRIVATE_SAVE_LOCATION = "rsa_private_key.txt";
    private static final String RSA_HISTORY_SAVE_LOCATION = "rsa_history.txt";
    private static final String AES_SAVE_LOCATION = "aes_key.txt";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey secretKey;
    private CachedMap<String, PrivateKey> keyHistory;

    private Cryptography() {
        this.keyHistory = new CachedMap<>();
    }

    public static Cryptography getInstance() {
        return cryptography;
    }

    /**
     * Creates and returns a new instance of Cryptography object.
     * All keys are initialized and saved automatically.
     * Note that any key that must be saved to an outside storage must be done afterwards.
     *
     * @param context The application context required for the IO operations.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void newInstance(Context context) {
        Cryptography newInstance = new Cryptography();
        try {
            newInstance.reinitialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cryptography = newInstance;
    }

    /**
     * Creates a new Cryptography object and initializes the fields from the local storage.
     *
     * @param context The application context required for the IO operations.
     * @throws IOException if reading fails for any reason.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void loadInstance(Context context) throws Exception {
        Cryptography newInstance = new Cryptography();
        newInstance.setSecretKey((SecretKey) readKey(context, AES_SAVE_LOCATION));
        newInstance.setPrivateKey((PrivateKey) readKey(context, RSA_PRIVATE_SAVE_LOCATION));
        newInstance.setPublicKey((PublicKey) readKey(context, RSA_PUBLIC_SAVE_LOCATION));
        newInstance.setKeyHistory(readKeyHistory(context));
        cryptography = newInstance;
    }

    /**
     * Re-initializes the Keys in this object.
     * Note that if an error occurs it means that the hardcoded static fields in this class is erroneous.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reinitialize(Context context) {
        try {
            this.updateRSAKeyPair(context);
            this.updateAESKey(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and returns a new public-private key pair for RSA.
     * Saves the encoded keys to internal storage.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRSAKeyPair(Context context) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
        generator.initialize(2048);
        KeyPair result = generator.generateKeyPair();
        this.publicKey = result.getPublic();
        this.privateKey = result.getPrivate();
        saveKey(context, result.getPublic(), RSA_PUBLIC_SAVE_LOCATION);
        saveKey(context, result.getPrivate(), RSA_PRIVATE_SAVE_LOCATION);
        this.keyHistory.put(this.getPublicKeyString(), this.privateKey);
        saveKeyHistory(context, this.keyHistory);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAESKey(Context context) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        SecretKey secretKey = generator.generateKey();
        this.secretKey = secretKey;
        saveKey(context, secretKey, AES_SAVE_LOCATION);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveKey(Context context, Key key, String filename) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(key);
        objectOutputStream.close();
    }

    /**
     * Reads and restores a key from internal storage.
     *
     * @param context     The application context required for IO operations.
     * @param filename    The filepath of the file.
     * @return The key restored from the string.
     * @throws IOException in case the reading fails.
     **/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Key readKey(Context context, String filename) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = context.openFileInput(filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Key result = (Key) objectInputStream.readObject();
        objectInputStream.close();
        return result;
    }

    private static CachedMap<String, PrivateKey> readKeyHistory(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = context.openFileInput(Cryptography.RSA_HISTORY_SAVE_LOCATION);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        @SuppressWarnings("unchecked") CachedMap<String, PrivateKey> result = (CachedMap<String, PrivateKey>) objectInputStream.readObject();
        objectInputStream.close();
        return result;
    }

    private static void saveKeyHistory(Context context, CachedMap<String, PrivateKey> keyHistory) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(Cryptography.RSA_HISTORY_SAVE_LOCATION, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(keyHistory);
        objectOutputStream.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String encodeToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


    /**
     * Restores a RSA public key from an Base64-encoded String.
     *
     * @param key A Public Key string in Base64.
     * @return A new Public Key for RSA. Null if generation fails.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PublicKey getRSAPublicKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Restores a RSA private key from an Base64-encoded String.
     *
     * @param key A private Key string in Base64.
     * @return A new private Ley for RSA. Null if generation fails.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PrivateKey getRSAPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            PKCS8EncodedKeySpec privateKey = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePrivate(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Restores and returns an AES Secret Key given in Base64 encoded form.
     *
     * @param key Base64 encoded string containing an AES key.
     * @return A SecretKeySpec extracted from the input.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static SecretKey getAESSecretKey(String key) {
        byte[] decoded = Base64.getDecoder().decode(key);
        return new SecretKeySpec(Arrays.copyOf(decoded, 16), AES);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String RSA_encrypt(String text, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSAwPadding);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String RSA_decrypt(String text, Key key) throws Exception {
        byte[] decodedText = Base64.getDecoder().decode(text);
        Cipher cipher = Cipher.getInstance(RSAwPadding);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodedText));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String AES_encrypt(String text, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String AES_decrypt(String text, SecretKey key) throws Exception {
        byte[] decodedText = Base64.getDecoder().decode(text);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(decodedText));
    }

    /**
     * Encrypts given text using own Secret AES key and recipient's public RSA key.
     *
     * @param message      The message to be encrypted.
     * @param recipientPK The public key of the user that the message will be delivered to.
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encryptMessage(Message message, String recipientPK) {
        try {
            String encryptedText = AES_encrypt(message.getText(), this.secretKey);
            String encryptedKey = RSA_encrypt(encodeToBase64(this.secretKey), getRSAPublicKey(recipientPK));
            message.setText(encryptedText);
            message.setAESKey(encryptedKey);
            message.setRSAPublicKey(recipientPK);
            message.setEncrypted(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ENCRYPT", "Encryption failed.");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decryptMessage(Message message) {
        try {
            String decryptedAESKey;
            if (this.keyHistory.containsKey(message.getRSAPublicKey())) {
                decryptedAESKey = RSA_decrypt(message.getAESKey(), this.keyHistory.get(message.getRSAPublicKey()));
            } else {
                decryptedAESKey = RSA_decrypt(message.getAESKey(), this.secretKey);
            }
            String decryptedText = AES_decrypt(message.getText(), getAESSecretKey(decryptedAESKey));
            message.setText(decryptedText);
            message.setEncrypted(false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DECRYPT", "Decryption failed.");
        }

    }

    public static String getRSA() {
        return RSA;
    }

    public static String getAES() {
        return AES;
    }

    public static String getRsaPublicSaveLocation() {
        return RSA_PUBLIC_SAVE_LOCATION;
    }

    public static String getRsaPrivateSaveLocation() {
        return RSA_PRIVATE_SAVE_LOCATION;
    }

    public static String getAesSaveLocation() {
        return AES_SAVE_LOCATION;
    }

    private PrivateKey getPrivateKey() {
        return privateKey;
    }

    private void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    private PublicKey getPublicKey() {
        return publicKey;
    }

    private void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    private SecretKey getSecretKey() {
        return secretKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPublicKeyString() {
        return encodeToBase64(this.publicKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPrivateKeyString() {
        return encodeToBase64(this.privateKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getSecretKeyString() {
        return encodeToBase64(this.secretKey);
    }

    private void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public LinkedHashMap<String, PrivateKey> getKeyHistory() {
        return keyHistory;
    }

    public void setKeyHistory(CachedMap<String, PrivateKey> keyHistory) {
        this.keyHistory = keyHistory;
    }

    private static class CachedMap<T, V> extends LinkedHashMap<T, V> {
        private final int maxCapacity = 25;

        @Override
        protected boolean removeEldestEntry(Entry<T, V> eldest) {
            return this.size() > maxCapacity;
        }
    }
}