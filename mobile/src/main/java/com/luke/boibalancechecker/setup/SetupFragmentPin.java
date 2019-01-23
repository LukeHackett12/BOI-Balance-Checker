package com.luke.boibalancechecker.setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luke.boibalancechecker.R;
import com.luke.boibalancechecker.views.BalanceActivity;
import com.luke.boibalancechecker.helpers.KeyStoreHelper;

public class SetupFragmentPin extends Fragment {

    private static final String BOI_ALIAS = "BOI";
    boolean confirming = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin_setup, container, false);

        StringBuilder pinEntered = new StringBuilder();
        StringBuilder pinConfirmed = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", view.getContext().getPackageName());
            Button pinButton = view.findViewById(id);

            String number = "" + i;
            pinButton.setOnClickListener(v -> {
                if (confirming) {
                    if (pinConfirmed.length() == 5) {
                        pinConfirmed.append(number);
                        String entered = pinEntered.toString();
                        String confirmed = pinConfirmed.toString();
                        savePin(entered, confirmed);
                    } else {
                        pinConfirmed.append(number);
                        addDot(pinConfirmed.length(), view);
                    }
                } else {
                    if (pinEntered.length() == 5) {
                        pinEntered.append(number);
                        changeStage(view);
                    } else {
                        pinEntered.append(number);
                        addDot(pinEntered.length(), view);
                    }
                }
            });

        }

        return view;
    }

    void savePin(String entered, String confirmed) {
        if(entered.equals(confirmed)){
            SharedPreferences accountDetails = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = accountDetails.edit();

            String stringEncrypted = KeyStoreHelper.encrypt(BOI_ALIAS, entered);
            editor.putString("pin", stringEncrypted);
            editor.putString("stage", KeyStoreHelper.encrypt(BOI_ALIAS, "2"));
            editor.apply();

            Toast.makeText(this.getContext(),"Saved Pin",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this.getContext(), BalanceActivity.class);
            startActivity(intent);
        }
    }

    void changeStage(View view) {
        for (int i = 1; i <= 6; i++) {
            int id = getResources().getIdentifier("pindigit_" + i, "id", view.getContext().getPackageName());
            ImageView pinDot = view.findViewById(id);
            pinDot.setColorFilter(0);
        }

        confirming = true;
        TextView text = view.findViewById(R.id.textPrompt);
        text.setText(getString(R.string.confirm_pin));
    }

    private void addDot(int length, View view) {
        int id = getResources().getIdentifier("pindigit_" + length, "id", view.getContext().getPackageName());
        ImageView pinDot = view.findViewById(id);
        pinDot.setColorFilter(getResources().getColor(R.color.darkerGrey));
    }
}
