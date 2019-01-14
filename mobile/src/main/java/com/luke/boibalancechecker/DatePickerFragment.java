package com.luke.boibalancechecker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    View viewPast;

    DatePickerFragment(View v){
        this.viewPast = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();

        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,startYear,startMonth,startDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dayString = ((dayOfMonth < 10) ? "0" : "") + dayOfMonth;
        String monthString = (((month+1) < 10) ? "0" : "") + (month+1);
        String dateStr = dayString+"/"+monthString+"/"+year;
        TextView dateTextView = viewPast.findViewById(R.id.dateInput);
        dateTextView.setText(dateStr);
    }
}


