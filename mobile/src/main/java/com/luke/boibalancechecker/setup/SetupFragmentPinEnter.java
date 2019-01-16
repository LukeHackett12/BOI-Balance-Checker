package com.luke.boibalancechecker.setup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luke.boibalancechecker.R;

public class SetupFragmentPinEnter extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin_setup, container, false);

        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("button_"+i, "id", view.getContext().getPackageName());
            Button pinButton = view.findViewById(id);
            pinButton.setOnClickListener(v -> {

            });
        }
        return view;
    }
}
