package com.nextnut.logistica.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by perez.juan.jose on 07/09/2016.
 */
public class MakeCall {
    private static final String LOG_TAG = MakeCall.class.getSimpleName();
    final static public int MY_PERMISSIONS_REQUEST_READ_CONTACT = 124;
    final static public int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;
    static Context mContext;
    static Activity mActivity;

    public static void makeTheCall(Activity aplication,
                                   String ContactID) {
        mContext = aplication;
        mActivity = (Activity) aplication;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "phone-Number: " + "PERMISSION No GRANTED");


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mActivity,
                    android.Manifest.permission.CALL_PHONE)) {
                Log.e(LOG_TAG, "phone-Number: " + "Notificar el pedido");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions((Activity) mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                Log.e(LOG_TAG, "phone-Number: " + "pace el pedido luego de verificar si debe");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                Log.e(LOG_TAG, "phone-Number: " + "pace el pedido");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e(LOG_TAG, "phone-Number: " + "llama directo, esta autorizado");
            makePhoneCall(ContactID);

        }


    }

    public static void makePhoneCall(String id) {
        Log.e(LOG_TAG, "phone-Number: " + "makePhoneCal : " + id);
        Cursor cursor = null;
        String phoneNumber = "";
        int type = 0;
        String phoneType = "";
        List<String> allNumbers = new ArrayList<String>();
        int phoneIdx = 0;
        int displayNameKeyIdx = 0;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "phone-Number: " + "READ_CONTACTS No GRANTED");


//                            // Should we show an explanation?
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                    android.Manifest.permission.READ_CONTACTS)) {
//                                Log.e(LOG_TAG, "phone-Number: " + "Notificar el pedido READ_CONTACTS");
//                                // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACT);
            Log.e(LOG_TAG, "phone-Number: " + "Hace el pedido luego de verificar si debe READ_CONTACTS");
        } else {

            // No explanation needed, we can request the permission.


            try {


                cursor = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[] { id }, null);
                phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                displayNameKeyIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                Log.e(LOG_TAG, "phone-Number: " + "curor count : " + cursor.getCount());
                Log.e(LOG_TAG, "phone-Number: " + "curor toString : " + cursor.toString());
//                Log.e(LOG_TAG, "displayName 0: " + cursor.getString( cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {
                        Log.e(LOG_TAG, "displayName1: " + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        phoneNumber = cursor.getString(phoneIdx);
                        type = cursor.getInt(displayNameKeyIdx);
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                phoneType = "HOME";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                phoneType = "MOVILE";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                phoneType = "WORK";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                phoneType = "FAX WORK";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                phoneType = "MAIN";
                                break;
                            default:
                                phoneType = "Otro";
                        }
                        Log.e(LOG_TAG, "phone-Number: " + "multiple : " + phoneType + " : " + phoneNumber);
                        allNumbers.add(phoneType + " : " + phoneNumber);
                        cursor.moveToNext();

                    }
                } else {
                    //no results actions
                }


            } catch (Exception e) {
                Log.e(LOG_TAG, "phone-Number: " + "Exception" + e.toString());
                //error actions
            } finally {

                if (cursor != null) {
                    cursor.close();
                }

                final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose a number");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedNumber = items[item].toString();
                        selectedNumber = selectedNumber.replace("-", "");
                        String selectedNumber1[] = selectedNumber.split(":");
                        Log.e(LOG_TAG, "phone-Number: " + "mULTIPLE llamando. selectedNumber1.length: " + selectedNumber1.length);
                        Log.e(LOG_TAG, "phone-Number: " + "mULTIPLE llamando a: " + selectedNumber1[1]);
                        Intent intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + selectedNumber1[1]));


//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mContext.startActivity(intent);

                    }
                });
                AlertDialog alert = builder.create();
                if (allNumbers.size() > 1) {
                    alert.show();
                } else {
                    String selectedNumber = phoneNumber.toString();
                    selectedNumber = selectedNumber.replace("-", "");
                    Log.e(LOG_TAG, "phone-Number: " + "sIMPLE llamando a: " + selectedNumber);
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + selectedNumber));
                    mContext.startActivity(intent);
                }

                if (phoneNumber.length() == 0) {
                    //no numbers found actions
                }
            }
        }
    }




        public static String getUserName(Context context ,String id) {

            Cursor cursor;
            String userName = null;
            try {
                cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[] { id }, null);
                Log.i(LOG_TAG, "getUserName: " + "curor count : " + cursor.getCount());
                Log.i(LOG_TAG, "getUserName: " + "curor toString : " + cursor.toString());
//                Log.e(LOG_TAG, "displayName 0: " + cursor.getString( cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                if (cursor.moveToFirst()) {

                        Log.i(LOG_TAG, "getUserName: " + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        userName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                }
            } catch (Exception e) {
                Log.i(LOG_TAG, "getUserName: " + "Exception" + e.toString());

            }
        return  userName;
    }



}
