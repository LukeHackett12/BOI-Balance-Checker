package com.luke.boibalancechecker.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;

import com.luke.boibalancechecker.helpers.KeyStoreHelper;
import com.luke.boibalancechecker.helpers.NavigationHost;
import com.luke.boibalancechecker.R;

public class SetupFragmentTwo extends Fragment {

    private static final String BOI_ALIAS = "BOI";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_two, container, false);

        final TextInputLayout phoneNumberText = view.findViewById(R.id.textInputID);
        final TextInputEditText phoneNumberEdit = view.findViewById(R.id.textEditID);
        MaterialButton nextButton = view.findViewById(R.id.progressSetup);

        phoneNumberEdit.post(() -> {
            phoneNumberEdit.requestFocus();
            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imgr.showSoftInput(phoneNumberEdit, InputMethodManager.SHOW_IMPLICIT);
        });

        nextButton.setOnClickListener(view1 -> {
            if (!validPhoneNumber(phoneNumberEdit.getText())) {
                phoneNumberText.setError(getString(R.string.error_phone));
            } else {
                phoneNumberText.setError(null); // Clear the error
                addToPrefs(phoneNumberEdit);
                ((NavigationHost) getActivity()).navigateTo(new SetupFragmentThree(), true); // Navigate to the next Fragment
            }
        });
        return view;
    }

    private void addToPrefs(TextInputEditText phoneEdit) {
        SharedPreferences accountDetails = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = accountDetails.edit();

        String stringEncrypted = KeyStoreHelper.encrypt(BOI_ALIAS, String.valueOf(phoneEdit.getText()));
        editor.putString("phoneNum", stringEncrypted);
        editor.apply();
    }

    boolean validPhoneNumber(@Nullable Editable text){
        assert text != null;
        return (text.length() == 4);
    }
}


