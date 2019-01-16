package com.luke.boibalancechecker.setup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luke.boibalancechecker.helpers.NavigationHost;
import com.luke.boibalancechecker.R;

public class SetupFragmentZero extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_zero, container, false);

        MaterialButton nextButton = view.findViewById(R.id.progressSetup);

        nextButton.setOnClickListener(view1 -> {
            ((NavigationHost) getActivity()).navigateTo(new SetupFragmentOne(), true); // Navigate to the next Fragment
        });
        return view;
    }
}


