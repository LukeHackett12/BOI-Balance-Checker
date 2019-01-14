package com.luke.boibalancechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetupFragmentThree extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_three, container, false);

        MaterialButton nextButton = view.findViewById(R.id.progressSetup);
        TextView dateText = view.findViewById(R.id.dateInput);

        dateText.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment(view);
            datePickerFragment.show(getFragmentManager(), "Date Picker");
        });
        nextButton.setOnClickListener(view1 -> {
            try {
                if (!validDOB((String) dateText.getText())) {
                    dateText.setError(getString(R.string.error_date));
                } else {
                    dateText.setError(null); // Clear the error
                    ((NavigationHost) getActivity()).navigateTo(new SetupFragmentFour(), true); // Navigate to the next Fragment
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return view;
    }

    boolean validDOB(@Nullable String text) throws ParseException {
        assert text != null;

        DateFormat format = new SimpleDateFormat("DD/mm/yyyy", Locale.ENGLISH);
        Date date = format.parse(text);
        Date now = new Date();

        return !date.after(now);
    }

}


