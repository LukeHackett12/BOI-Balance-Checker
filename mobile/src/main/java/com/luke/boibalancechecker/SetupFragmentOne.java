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

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static android.content.Context.MODE_PRIVATE;

public class SetupFragmentOne extends Fragment {

    public static final String BOI_ALIAS = "BOI";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_one, container, false);

        final TextInputLayout accountInput = view.findViewById(R.id.textInputID);
        final TextInputEditText accountEdit = view.findViewById(R.id.textEditID);
        MaterialButton nextButton = view.findViewById(R.id.progressSetup);

        nextButton.setOnClickListener(view1 -> {
            if (!validAccountID(accountEdit.getText())) {
                accountInput.setError(getString(R.string.error_account));
            } else {
                accountInput.setError(null); // Clear the error
                try {
                    addToPrefs(accountEdit);
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
                ((NavigationHost) getActivity()).navigateTo(new SetupFragmentTwo(), true); // Navigate to the next Fragment
            }
        });
        return view;
    }

    private void addToPrefs(TextInputEditText accountEdit) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        SharedPreferences accountDetails = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = accountDetails.edit();

        String stringEncrypted = KeyStoreHelper.encrypt(BOI_ALIAS, String.valueOf(accountEdit.getText()));
        editor.putString("accountID", stringEncrypted);
        editor.apply();
    }

    boolean validAccountID(@Nullable Editable text){
        assert text != null;
        return (text.length() == 8);
    }

}


