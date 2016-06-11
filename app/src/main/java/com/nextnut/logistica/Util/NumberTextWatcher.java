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

//    private final DecimalFormat df;
//    private final DecimalFormat dfnd;
    private final EditText et;
    private boolean hasFractionalPart;
    private int trailingZeroCount;

    public NumberTextWatcher(EditText editText) {
//        df = new DecimalFormat(pattern);
//        df.setDecimalSeparatorAlwaysShown(true);
//        df.setDecimalFormatSymbols( new NumberFormat.getCurrencyInstance());
//        dfnd = new DecimalFormat("#,###.00");
        this.et = editText;
//        hasFractionalPart = false;
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


//            et.addTextChangedListener(this);
        }


//        if (s != null && !s.toString().isEmpty()) {
//            try {
//                int inilen, endlen;
//                inilen = et.getText().length();
//                String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace("$","");
//                Number n = df.parse(v);
//                int cp = et.getSelectionStart();
//                if (hasFractionalPart) {
//                    StringBuilder trailingZeros = new StringBuilder();
//                    while (trailingZeroCount-- > 0)
//                        trailingZeros.append('0');
//                    et.setText(df.format(n) + trailingZeros.toString());
//                } else {
//                    et.setText(dfnd.format(n));
//                }
//                et.setText("$".concat(et.getText().toString()));
//                endlen = et.getText().length();
//                int sel = (cp + (endlen - inilen));
//                if (sel > 0 && sel < et.getText().length()) {
//                    et.setSelection(sel);
//                } else if (trailingZeroCount > -1) {
//                    et.setSelection(et.getText().length() - 3);
//                } else {
//                    et.setSelection(et.getText().length());
//                }
//            } catch (NumberFormatException | ParseException e) {
//                e.printStackTrace();
//            }
//        }

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



//        int index = s.toString().indexOf(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
//        trailingZeroCount = 0;
//        if (index > -1) {
//            for (index++; index < s.length(); index++) {
//                if (s.charAt(index) == '0')
//                    trailingZeroCount++;
//                else {
//                    trailingZeroCount = 0;
//                }
//            }
//            hasFractionalPart = true;
//        } else {
//            hasFractionalPart = false;
//        }
    }
}