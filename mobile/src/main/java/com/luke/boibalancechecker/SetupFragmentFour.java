package com.luke.boibalancechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetupFragmentFour extends Fragment {

    private static final String BOI_ALIAS = "BOI";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_four, container, false);

        final TextInputLayout sixDigitPinText = view.findViewById(R.id.textInputID);
        final TextInputEditText sixDigitPinEdit = view.findViewById(R.id.textEditID);
        MaterialButton nextButton = view.findViewById(R.id.progressSetup);

        nextButton.setOnClickListener(view1 -> {
            if (!validPhoneNumber(sixDigitPinEdit.getText())) {
                sixDigitPinText.setError(getString(R.string.error_pin));
            } else {
                sixDigitPinText.setError(null); // Clear the error
                addToPrefs(sixDigitPinEdit);
                ((NavigationHost) getActivity()).navigateTo(new BalanceFragment(), true); // Navigate to the next Fragment
            }
        });
        return view;
    }

    private void addToPrefs(TextInputEditText sixDigitPin) {
        SharedPreferences accountDetails = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = accountDetails.edit();

        String stringEncrypted;
        stringEncrypted = KeyStoreHelper.encrypt(BOI_ALIAS, String.valueOf(sixDigitPin.getText()));
        editor.putString("sixDigitCode", stringEncrypted);
        editor.putInt("stage", 1);
        editor.apply();
    }

    boolean validPhoneNumber(@Nullable Editable text){
        assert text != null;
        return (text.length() == 6);
    }
}


