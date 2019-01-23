package com.luke.boibalancechecker.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luke.boibalancechecker.R;
import com.luke.boibalancechecker.adapters.PagerAdapter;
import com.luke.boibalancechecker.helpers.KeyStoreHelper;
import com.luke.boibalancechecker.helpers.NavigationHost;
import com.luke.boibalancechecker.login.LoginActivity;
import com.luke.boibalancechecker.setup.SetupFragmentZero;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    public static final String BOI_ALIAS = "BOI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            KeyStoreHelper.createKeys( this, BOI_ALIAS);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);


        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        PrivateKey privateKey = null;
        try {
            privateKey = (PrivateKey) keyStore.getKey(BOI_ALIAS, null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        String stage = "0";
        try {
            stage = KeyStoreHelper.decrypt(privateKey, prefs.getString("stage", null));
        } catch (Exception e){
            System.out.println("KeyStore fucked up");
        }

        switch (Integer.parseInt(stage)) {
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new SetupFragmentZero())
                        .commit();
                break;
            case 1:
                setContentView(R.layout.activity_app);
                Toolbar toolbar =  findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                TabLayout tabLayout = findViewById(R.id.tab_layout);
                tabLayout.addTab(tabLayout.newTab().setText("Account Balance"));
                tabLayout.addTab(tabLayout.newTab().setText("Settings"));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                final ViewPager viewPager = findViewById(R.id.pager);
                final PagerAdapter adapter = new PagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                defaultTabLayoutCreation(tabLayout, viewPager);
                break;
            case 2:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void defaultTabLayoutCreation(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
