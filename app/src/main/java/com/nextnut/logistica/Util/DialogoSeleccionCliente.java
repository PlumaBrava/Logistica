package com.nextnut.logistica.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by perez.juan.jose on 15/06/2016.
 */
public class DialogoSeleccionCliente extends DialogFragment {

    public onOptionSelected monOptionSelected;

    public void onOptionSelected(int id){};

    public interface onOptionSelected{
       public void onOptionSelected(int id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] items = {"Español", "Inglés", "Francés"};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle("Selección")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        monOptionSelected.onOptionSelected(item);
                        Log.i("Dialogos", "Opción elegida: " + items[item]);
                    }
                });

        return builder.create();
    }
}