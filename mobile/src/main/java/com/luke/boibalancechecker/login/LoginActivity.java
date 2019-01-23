package com.luke.boibalancechecker.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.luke.boibalancechecker.R;
import com.luke.boibalancechecker.helpers.KeyStoreHelper;
import com.luke.boibalancechecker.views.BalanceActivity;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static com.luke.boibalancechecker.views.MainActivity.BOI_ALIAS;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StringBuilder pinEntered = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", getApplicationContext().getPackageName());
            Button pinButton = findViewById(id);

            String number = "" + i;
            pinButton.setOnClickListener(v -> {
                if (pinEntered.length() == 5) {
                        pinEntered.append(number);
                    try {
                        if(isValidPin(pinEntered.toString())){
                            Intent intent = new Intent(getApplicationContext(), BalanceActivity.class);
                            startActivity(intent);
                        }
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (UnrecoverableKeyException e) {
                        e.printStackTrace();
                    }
                } else {
                        pinEntered.append(number);
                        addDot(pinEntered.length());
                    }
            });

        }
    }

    private boolean isValidPin(String pin) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(BOI_ALIAS, null);

        SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String storedPin = KeyStoreHelper.decrypt(privateKey, prefs.getString("pin", null));
        return (pin.equals(storedPin));
    }

    private void addDot(int length) {
        int id = getResources().getIdentifier("pindigit_" + length, "id", getApplicationContext().getPackageName());
        ImageView pinDot = findViewById(id);
        pinDot.setColorFilter(getResources().getColor(R.color.darkerGrey));
    }
}
