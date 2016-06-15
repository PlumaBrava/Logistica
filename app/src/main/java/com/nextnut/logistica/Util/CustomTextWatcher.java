package com.nextnut.logistica.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by perez.juan.jose on 14/06/2016.
 */
public class CustomTextWatcher implements TextWatcher {

    private final EditText et;

    private static final String LOG_TAG = CustomTextWatcher.class.getSimpleName();

    public CustomTextWatcher(EditText editText) {

        this.et = editText;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        if(s != null && !s.toString().isEmpty()){

        Boolean modifyText = false;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                Log.i(LOG_TAG, "OnEditorActionListener onTextChanged,-existeProductName Enter detected");
                modifyText = true;
            } else {
                b.append(s.charAt(i));
            }
            if (modifyText) {
                // hide keyboard before calling the done action
                InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                View view =  et.getRootView();
//
//                        getActivity().getCurrentFocus();

                if (view != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                et.setText(b.toString());
            }
        }

        }

        et.addTextChangedListener(this);
    }

}
