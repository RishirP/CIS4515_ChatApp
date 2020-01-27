package edu.temple.chatapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class KeyService extends Service {

    IBinder KeyBinder = new KeyBinder();

    public KeyService() {
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
}
