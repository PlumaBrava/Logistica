package com.nextnut.logistica.util;


import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by perez.juan.jose on 12/06/2016.
 */
public class DialogAlerta extends DialogFragment {

    public DialogAlerta()
    {

    }

    public static DialogAlerta newInstance(String title) {
        DialogAlerta frag = new DialogAlerta();

        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        Log.i("onDialogAlert:", "fragment " +title);
        return frag;
    }

//    public void DFragmen(String title)
//    {
//
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//            View rootView = inflater.inflate(R.layout.dialogfragment, container, false);
//
//            getDialog().setTitle(getArguments().getString("title"));
//
//            // Do something else
//            return rootView;
//
//    }
}