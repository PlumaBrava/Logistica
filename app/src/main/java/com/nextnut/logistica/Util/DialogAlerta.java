package com.nextnut.logistica.util;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;

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
        return frag;
    }


}