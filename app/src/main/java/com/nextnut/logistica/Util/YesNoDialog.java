package com.nextnut.logistica.Util;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by perez.juan.jose on 12/06/2016.
 */
public class YesNoDialog extends DialogFragment
{
//    private EditText mEditText;

    // 1. Defines the listener interface with a method passing back data result.
    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public YesNoDialog()
    {

    }

    public static YesNoDialog newInstance(String title,String message) {
        YesNoDialog frag = new YesNoDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        // 2. Setup a callback when the "Done" button is pressed on keyboard
//        mEditText.setOnEditorActionListener(this);

        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)



                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
//
                        sendBackResult();
//                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
//                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);


                    }
                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        ((ProductDetailFragment)getTargetFragment()).doNegativeClick();
////                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
//                    }
//                })
                .create();
    }


    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (EditorInfo.IME_ACTION_DONE == actionId) {
//            // Return input text back to activity through the implemented listener
//            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
//            listener.onFinishEditDialog(mEditText.getText().toString());
//            // Close the dialog and return back to the parent activity
//            dismiss();
//            return true;
//        }
//        return false;
//    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();
        listener.onFinishEditDialog("juan JJ");
        dismiss();
    }


}
