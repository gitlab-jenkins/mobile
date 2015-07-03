package com.hampay.mobile.android.component.edittext;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by amir on 7/2/15.
 */
public class AccountNoFormat implements TextWatcher {

    private String current = "";
    private String format = "###/##/###/#########";
    String[] splitedFormat = format.split("/");
    List<Integer> splitedFormatLengh;
    private Calendar cal = Calendar.getInstance();

    private EditText et;


    public AccountNoFormat(EditText et) {

        this.et = et;

        splitedFormatLengh = new ArrayList<Integer>();
        for (String slipted : splitedFormat){
            splitedFormatLengh.add(slipted.length());
        }


    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        et.setText(s + "");

        for (int i : splitedFormatLengh){
            if (s.length() == i){
                et.setText(s + "-");
            }
        }


    }
}