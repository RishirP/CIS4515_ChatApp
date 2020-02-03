package edu.temple.chatapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyService extends Service {

    private final String KEYPAIR_FILENAME = "keypair.dat";
    private final String KEYPAIR_PREFERENCES = "my_preferences_keypair";
    private final String NEW_KEYPAIR_KEY = "NEW_KEYPAIR";
    private KeyPair keys;
    private Boolean new_keypair = true;

    IBinder KeyBinder = new KeyBinder();

    public KeyService() {
        retrieveSavedPreferences();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class KeyBinder extends Binder {
        KeyService getService (){
            return KeyService.this;
        }
    }

    public KeyPair getMyKeyPair(){

        retrieveKeyPair();

        return keys;
    }

    private void createKeyPair(){
        KeyPairGenerator kpg;

        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            keys = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

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

    private void retrieveKeyPair(){
        File file = new File(getFilesDir(),KEYPAIR_FILENAME);

        try {

            if( file.exists() ) {
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
