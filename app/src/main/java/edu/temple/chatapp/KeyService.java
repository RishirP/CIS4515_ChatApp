package edu.temple.chatapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

public class KeyService extends Service {

    private final String KEYPAIR_FILENAME = "keypair.dat";
    private final String KEYPAIR_PREFERENCES = "my_preferences_keypair";
    private final String NEW_KEYPAIR_KEY = "NEW_KEYPAIR";
    private KeyPair keys;
    private Boolean new_keypair = true;

    IBinder keyBinder = new KeyBinder();

    public KeyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        retrieveSavedPreferences();
        return keyBinder;
    }

    public class KeyBinder extends Binder {
        KeyService getService (){
            return KeyService.this;
        }
    }

    /**
     * create a keypair or retrieve one from storage
     *
     * @return KeyPair
     */
    public KeyPair getMyKeyPair(){

        File file = new File(getFilesDir(),KEYPAIR_FILENAME);

        try {
            if( file.exists() && !new_keypair ) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);

                keys = (KeyPair) ois.readObject();
                ois.close();
                fis.close();
            }else{
                createKeyPair();
                saveKeyPair();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return keys;
    }

    /**
     * Store partner public key
     *
     * @param partnerName String
     * @param publicKey String
     */
    public void storePublicKey (String partnerName, String publicKey){
        File file = new File(getFilesDir(),partnerName + ".key");
        try {
            byte[] key = Base64.decode( publicKey,Base64.DEFAULT);
            X509EncodedKeySpec pub_key_spec = new X509EncodedKeySpec( key );
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKey public_key = (RSAPublicKey) fact.generatePublic( pub_key_spec );

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject( public_key );
            oos.flush();
            oos.close();
            fos.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get partner public key from storage
     *
     * @param partnerName String
     * @return RSAPublicKey
     */
    public RSAPublicKey getPublicKey(String partnerName){
        File file = new File(getFilesDir(),partnerName + ".key");
        RSAPublicKey public_key = null;

        try{
            if( file.exists() ) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);

                public_key = (RSAPublicKey) ois.readObject();
                ois.close();
                fis.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return public_key;
    }

    /**
     * Create new keypair
     *
     */
    private void createKeyPair(){
        KeyPairGenerator kpg;

        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            keys = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store generated keypair
     *
     */
    private void saveKeyPair(){
        File file = new File(getFilesDir(),KEYPAIR_FILENAME);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject( keys );
            oos.flush();
            oos.close();
            fos.close();

            new_keypair = false;
            savePreferences();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void savePreferences(){
        SharedPreferences.Editor editor = getSharedPreferences(KEYPAIR_PREFERENCES,MODE_PRIVATE).edit();
        editor.putBoolean(NEW_KEYPAIR_KEY, new_keypair);
        editor.apply();
    }

    private void retrieveSavedPreferences(){
        SharedPreferences prefs = getSharedPreferences(KEYPAIR_PREFERENCES,MODE_PRIVATE);
        new_keypair = prefs.getBoolean(NEW_KEYPAIR_KEY, true);
    }
}
