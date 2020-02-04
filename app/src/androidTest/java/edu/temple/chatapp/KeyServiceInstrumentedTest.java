package edu.temple.chatapp;

import android.content.Intent;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ServiceTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeoutException;

import javax.crypto.Cipher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class KeyServiceInstrumentedTest {

    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    @Test
    public void testRequestKeyPair() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),
                        KeyService.class);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        KeyService service =
                ((KeyService.KeyBinder) binder).getService();

        KeyPair keys = service.getMyKeyPair();

        // Verify that the service is working correctly.
        assertNotNull(keys);

        KeyPair keys_2 = service.getMyKeyPair();

        // Verify that a new key pair is nor generated.
        assertEquals( keys.getPublic(), keys_2.getPublic() );
    }

    @Test
    public void testStoreRetrievePartnerKey() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),
                        KeyService.class);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        KeyService service =
                ((KeyService.KeyBinder) binder).getService();

        try{

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            KeyPair keys = kpg.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();

            String publicKeyString = Base64.encodeToString( publicKey.getEncoded(), Base64.DEFAULT);

            service.storePublicKey("Test Partner", publicKeyString);

            // Verify that correct partner key is being retrieved
            RSAPublicKey partnerKey = service.getPublicKey("Test Partner");
            assertEquals( publicKey, partnerKey );

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testEncryptDecrypt() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(ApplicationProvider.getApplicationContext(),
                        KeyService.class);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        KeyService service =
                ((KeyService.KeyBinder) binder).getService();

        try{

            KeyPair keys = service.getMyKeyPair();
            Cipher cipher = Cipher.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();

            String message = "Hello there!";
            String enc_message, dec_message;

            //encrypt
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedText = cipher.doFinal(message.getBytes());
            enc_message = Base64.encodeToString(encryptedText, Base64.DEFAULT);

            //decrypt
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            dec_message = new String(cipher.doFinal(encryptedText));

            // Verify that the message encrypted
            assertNotEquals( message, enc_message );

            // Verify that the message decrypted properly
            assertEquals( message, dec_message );

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
