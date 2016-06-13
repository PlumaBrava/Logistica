package com.nextnut.logistica.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by perez.juan.jose on 31/05/2016.
 */
public class NumberTextWatcher implements TextWatcher {


    private final EditText et;


    public NumberTextWatcher(EditText editText) {

        this.et = editText;

    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        if(s != null && !s.toString().isEmpty()){


            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

//            current = formatted;
            et.setText(formatted);
            et.setSelection(formatted.length());

            for(int i =0 ; i < s.length(); i++) {
                if (s.charAt(i) == '\n') {
                    InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    View view = et.getRootView();
                    if (view != null) {
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } else {

                }



            }

        }




        et.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(s != null && !s.toString().isEmpty()){

            et.removeTextChangedListener(this);
            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

//            current = formatted;
            et.setText(formatted);
            et.setSelection(formatted.length());

            et.addTextChangedListener(this);
        }

    }
}