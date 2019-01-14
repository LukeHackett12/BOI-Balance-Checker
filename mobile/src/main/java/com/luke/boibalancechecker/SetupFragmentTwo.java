package com.luke.boibalancechecker;

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

public class SetupFragmentTwo extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_two, container, false);

        final TextInputLayout phoneNumberText = view.findViewById(R.id.textInputID);
        final TextInputEditText phoneNumberEdit = view.findViewById(R.id.textEditID);
        MaterialButton nextButton = view.findViewById(R.id.progressSetup);

        nextButton.setOnClickListener(view1 -> {
            if (!validPhoneNumber(phoneNumberEdit.getText())) {
                phoneNumberText.setError(getString(R.string.error_phone));
            } else {
                phoneNumberText.setError(null); // Clear the error
                ((NavigationHost) getActivity()).navigateTo(new SetupFragmentThree(), true); // Navigate to the next Fragment
            }
        });
        return view;
    }

    boolean validPhoneNumber(@Nullable Editable text){
        assert text != null;
        return (text.length() == 4);
    }
}


