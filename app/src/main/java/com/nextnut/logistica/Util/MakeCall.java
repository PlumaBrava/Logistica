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

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        mActivity = aplication;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    android.Manifest.permission.CALL_PHONE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            makePhoneCall(ContactID);

        }


    }

    public static void makePhoneCall(String id) {
        Cursor cursor = null;
        String phoneNumber = "";
        int type = 0;
        String phoneType = "";
        List<String> allNumbers = new ArrayList<String>();
        int phoneIdx = 0;
        int displayNameKeyIdx = 0;

        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACT);
        } else {

            // No explanation needed, we can request the permission.


            try {


                cursor = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                displayNameKeyIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {
                        phoneNumber = cursor.getString(phoneIdx);
                        type = cursor.getInt(displayNameKeyIdx);
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:

                                phoneType = mContext.getResources().getString(R.string.NomberTypeHome);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                phoneType = mContext.getResources().getString(R.string.NomberTypeMovile);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                phoneType = mContext.getResources().getString(R.string.NomberTypeWork);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                phoneType = mContext.getResources().getString(R.string.NomberTypeFaxWork);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                phoneType =  mContext.getResources().getString(R.string.NomberTypeMain);
                                break;
                            default:
                                phoneType = mContext.getResources().getString(R.string.NomberTypeOtro);
                        }
                        allNumbers.add(phoneType + " : " + phoneNumber);
                        cursor.moveToNext();

                    }
                } else {
                    //no results actions
                }


            } catch (Exception e) {
                //error actions
            } finally {

                if (cursor != null) {
                    cursor.close();
                }

                final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getResources().getString(R.string.MakeCall_Choose));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedNumber = items[item].toString();
                        selectedNumber = selectedNumber.replace("-", "");
                        String selectedNumber1[] = selectedNumber.split(":");
                        Intent intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse(mContext.getString(R.string.MakeCall_Tel) + selectedNumber1[1]));
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse(mContext.getString(R.string.MakeCall_Tel)+ selectedNumber));
                    mContext.startActivity(intent);
                }

                if (phoneNumber.length() == 0) {
                    //no numbers found actions
                }
            }
        }
    }

    public static void makePhoneCallCliente(Activity aplication,Cliente cliente) {
        mContext = aplication;
        mActivity = aplication;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    android.Manifest.permission.CALL_PHONE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {


            ArrayList mListaTelefonos;

            mListaTelefonos = new ArrayList();
            mListaTelefonos.addAll(cliente.getTelefonos().entrySet());
//
//7

            Log.i("call", "cabeceraOrden.getCliente(). mData.sie - " + mListaTelefonos.size());
            Log.i("call", "cabeceraOrden.getCliente().getTelefono() - no nulo");
            Log.i("call", "cabeceraOrden.getCliente().getTelefono() - " + cliente.getTelefonos().toString());
            Log.i("call", "cabeceraOrden.getCliente().getTelefono().isEmpty() - " + cliente.getTelefonos().isEmpty());
            Log.i("call", "cabeceraOrden.getCliente().getTelefono()..size() - " + cliente.getTelefonos().size());


            final String[] items = new String[mListaTelefonos.size()];
            if (!cliente.getTelefonos().isEmpty()) {
                for (int i = 0; i < mListaTelefonos.size(); i++) {
                    Map.Entry<String, String> item = (Map.Entry) mListaTelefonos.get(i);
                    Log.i("call", "cabeceraOrden.getCliente(). item.getKey()- " + item.getKey());
                    Log.i("call", "cabeceraOrden.getCliente(). item.getValue() - " + item.getValue());
                    items[i] = item.getKey() + " : " + item.getValue().toString();

                }

            }


//                = allNumbers.toArray(new String[allNumbers.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getResources().getString(R.string.MakeCall_Choose));

            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    String selectedNumber = items[item].toString();
                    selectedNumber = selectedNumber.replace("-", "");
                    String selectedNumber1[] = selectedNumber.split(":");
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse(mContext.getString(R.string.MakeCall_Tel) + selectedNumber1[1]));
                    if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mContext.startActivity(intent);

                }
            });
            AlertDialog alert = builder.create();
            if (mListaTelefonos.size() > 1) {
                alert.show();
            } else {
                String selectedNumber = items.toString();
                selectedNumber = selectedNumber.replace("-", "");
                Intent intent = new Intent(Intent.ACTION_CALL,
                        Uri.parse(mContext.getString(R.string.MakeCall_Tel) + selectedNumber));
                mContext.startActivity(intent);
            }

//                if (items.length() == 0) {
//                    //no numbers found actions
//                }
        }


    }


        public static String getUserName(Context context ,String id) {

            Cursor cursor;
            String userName = null;
            try {
                cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                if (cursor.moveToFirst()) {

                        userName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                }
            } catch (Exception e) {
                Log.i(LOG_TAG, mContext.getResources().getString(R.string.InformeError) + e.toString());

            }
        return  userName;
    }



}
