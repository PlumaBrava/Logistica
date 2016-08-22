package com.nextnut.logistica;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nextnut.logistica.Util.CurrencyToDouble;
import com.nextnut.logistica.Util.CustomTextWatcher;
import com.nextnut.logistica.Util.NumberTextWatcher;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.dummy.DummyContent;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import android.Manifest.permission_group.*;

/**
 * A fragment representing a single Custom detail screen.
 * This fragment is either contained in a {@link CustomListActivity}
 * in two-pane mode (on tablets) or a {@link CustomDetailActivity}
 * on handsets.
 */
public class CustomDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    public static final String ARG_ITEM_ID = "item_id";
    public static final String CUSTOM_ACTION = "product_action";
    public static final int CUSTOM_NEW = 0;
    public static final int CUSTOM_DOUBLE_SCREEN = 1;
    public static final int CUSTOM_SAVE = 2;
    public static final int CUSTOM_SELECTION = 3;
    private static final int DETAIL_CUSTOM_LOADER = 0;
    private static final int DEFAULT_DETAIL_CUSTOM_LOADER = 1;


    String mCurrentPhotoPath = null;


    private TextView mCustomId;
    private EditText mCustomName;
    private EditText mLastName;
    private EditText mDeliveyAddress;
    private EditText mCity;
    private Button button;
    private ImageView mImageCustomer;
    private EditText mCuit;
    private EditText mIva;
    private CheckBox mSpecial;


    private int mAction;


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_IMAGE_GET = 1889;

    CollapsingToolbarLayout appBarLayout;


    private long mItem = 0;

    private static final String LOG_TAG = CustomDetailFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = (long)getArguments().getLong(ARG_ITEM_ID);
            Log.i(LOG_TAG, "ARG_ITEM_IDfrag: " + mItem);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("ID :" + mItem);
            }
        }
        if (getArguments().containsKey(CUSTOM_ACTION)) {
            mAction = getArguments().getInt(CUSTOM_ACTION);
            Log.i(LOG_TAG, "CUSTOM_ACTION" + mAction);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_detail, container, false);


//        mCustomId = (TextView) rootView.findViewById(R.id.custom_Id);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mImageCustomer = (ImageView) rootView.findViewById(R.id.custom_imagen);
        mDeliveyAddress = (EditText) rootView.findViewById(R.id.custom_delivery_address);
        mCity = (EditText) rootView.findViewById(R.id.custom_city);

        mCustomName.addTextChangedListener(new TextWatcher() {
                                               public void afterTextChanged(Editable s) {
                                                   Log.i(LOG_TAG, "OnEditorActionListener afterTextChanged s: " + s.toString());
                                               }

                                               public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                               }

                                               public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                                                           InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                                                                   Context.INPUT_METHOD_SERVICE);
                                                           View view = getActivity().getCurrentFocus();
                                                           if (view != null) {
                                                               inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                                           }
//                                                           verifyName(b.toString());
                                                           mCustomName.setText(b.toString());
                                                       }
                                                   }
                                               }
                                           }
        );
        mLastName.addTextChangedListener(new CustomTextWatcher(mLastName));
        mDeliveyAddress.addTextChangedListener(new CustomTextWatcher( mDeliveyAddress));
        mCity.addTextChangedListener(new CustomTextWatcher(mCity));


        mCuit = (EditText) rootView.findViewById(R.id.custom_cuit);
        mIva = (EditText) rootView.findViewById(R.id.custom_iva);
        mSpecial = (CheckBox) rootView.findViewById(R.id.custom_special);

        return rootView;
    }

    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    String number;
    String mIdContact;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
               ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContext().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int column3 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
               number = cursor.getString(column1);
                String name = cursor.getString(column2);
                mIdContact = cursor.getString(column3);
                if (mCustomId!=null){
                    button.setBackgroundColor(Color.GREEN);
                }
                Log.e(LOG_TAG, "phone-Number: "+number);
                Log.e(LOG_TAG, "phone-name: "+name);
                Log.e(LOG_TAG, "phone- _id : "+ mIdContact );

                button.setText(name);
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "phone-Number: "+"PERMISSION No GRANTED");


                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.CALL_PHONE)) {
                        Log.e(LOG_TAG, "phone-Number: "+"Notificar el pedido");
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        Log.e(LOG_TAG, "phone-Number: "+"pace el pedido luego de verificar si debe");
                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        Log.e(LOG_TAG, "phone-Number: "+"pace el pedido");
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                else {
                    Log.e(LOG_TAG, "phone-Number: "+"llama directo, esta autorizado");
//                    makePhoneCall(mIdContact);

                }


            }
        }
    }
   final private int  MY_PERMISSIONS_REQUEST_CALL_PHONE =123;
   final private int  MY_PERMISSIONS_REQUEST_READ_CONTACT =124;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "phone-Number: "+"PERMISSION_autorizado");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (mCustomId!=null){
                        button.setBackgroundColor(Color.GREEN);
                    }
//                    makePhoneCall(mIdContact );
//                Intent intent = new Intent(Intent.ACTION_CALL,
//                        Uri.parse("tel:"+number));
//                startActivity(intent);

                } else {
                    Log.e(LOG_TAG, "phone-Number: "+"PERMISSION_rehazado");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public void makePhoneCall(String id ) {
        Log.e(LOG_TAG, "phone-Number: "+"makePhoneCal : "+id);
                    Cursor cursor = null;
                    String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<String>();
                    int phoneIdx = 0;

        // Here, thisActivity is the current activity
                        if (ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.e(LOG_TAG, "phone-Number: " + "READ_CONTACTS No GRANTED");


//                            // Should we show an explanation?
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                    android.Manifest.permission.READ_CONTACTS)) {
//                                Log.e(LOG_TAG, "phone-Number: " + "Notificar el pedido READ_CONTACTS");
//                                // Show an expanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.READ_CONTACTS},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACT);
                                Log.e(LOG_TAG, "phone-Number: " + "Hace el pedido luego de verificar si debe READ_CONTACTS");
                            } else {

                                // No explanation needed, we can request the permission.


                                try {


                                    cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[] { id }, null);
                                    phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    Log.e(LOG_TAG, "phone-Number: " + "curor count : " + cursor.getCount());
                                    Log.e(LOG_TAG, "phone-Number: " + "curor toString : " + cursor.toString());
                                    if (cursor.moveToFirst()) {
                                        while (cursor.isAfterLast() == false) {
                                            phoneNumber = cursor.getString(phoneIdx);
                                            Log.e(LOG_TAG, "phone-Number: " + "phoneNumber : " + phoneNumber);
                                            allNumbers.add(phoneNumber);
                                            cursor.moveToNext();

                                        }
                                    } else {
                                        //no results actions
                                    }


                                } catch (Exception e) {
                                    Log.e(LOG_TAG, "phone-Number: " + "Exception" + e.toString());
                                    //error actions
                                } finally {
                                    Log.e(LOG_TAG, "phone-Number: " + "finally");
                                    if (cursor != null) {
                                        cursor.close();
                                    }

                                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Choose a number");
                                    builder.setItems(items, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            String selectedNumber = items[item].toString();
                                            selectedNumber = selectedNumber.replace("-", "");
                                            Log.e(LOG_TAG, "phone-Number: " + "mULTIPLE llamando a: " + selectedNumber);
                                            Intent intent = new Intent(Intent.ACTION_CALL,
                                                    Uri.parse("tel:" + selectedNumber));
                                            startActivity(intent);

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
                                        startActivity(intent);
                                    }

                                    if (phoneNumber.length() == 0) {
                                        //no numbers found actions
                                    }
                                }
                            }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (mAction) {
            case CUSTOM_DOUBLE_SCREEN:
                if (mItem == 0) {
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-default DETAIL_PRODUCT_LOADER");
                    getLoaderManager().initLoader(DEFAULT_DETAIL_CUSTOM_LOADER, null, this);
                } else {
                    getLoaderManager().initLoader(DETAIL_CUSTOM_LOADER, null, this);
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-DETAIL_PRODUCT_LOADER");
                }
                break;
            case CUSTOM_SELECTION:
                getLoaderManager().initLoader(DETAIL_CUSTOM_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_SELECTION");
                break;
            default:
                break;
        }

//        getLoaderManager().initLoader(NAME_PRODUCT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    public void verificationAndsave() {

        Log.i(LOG_TAG, "save");
        if (verification()) {
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
            if (mAction == CUSTOM_NEW && mItem == 0) {
                Log.i(LOG_TAG, "save New");

                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Customs.CONTENT_URI);
                builder.withValue(CustomColumns.NAME_CUSTOM, mCustomName.getText().toString());
                builder.withValue(CustomColumns.LASTNAME_CUSTOM, mLastName.getText().toString());
                builder.withValue(CustomColumns.DELIIVERY_ADDRES_CUSTOM, mDeliveyAddress.getText().toString());
                builder.withValue(CustomColumns.DELIVERY_CITY_CUSTOM, mCity.getText().toString());
                builder.withValue(CustomColumns.IMAGEN_CUSTOM, mCurrentPhotoPath);
                builder.withValue(CustomColumns.REFERENCE_CUSTOM, mIdContact);
                builder.withValue(CustomColumns.CUIT_CUSTOM, mCuit.getText().toString());
                builder.withValue(CustomColumns.IVA_CUSTOM, mIva.getText().toString());
                builder.withValue(CustomColumns.SPECIAL_CUSTOM,mSpecial.isChecked()?1:0);

                batchOperations.add(builder.build());
            } else
//            if      (mAction==PRODUCT_SAVE )
            {

                Log.i(LOG_TAG, "save Modification");
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.Customs.withId(mItem));
                builder.withValue(CustomColumns.NAME_CUSTOM, mCustomName.getText().toString());
                builder.withValue(CustomColumns.LASTNAME_CUSTOM, mLastName.getText().toString());
                builder.withValue(CustomColumns.DELIIVERY_ADDRES_CUSTOM, mDeliveyAddress.getText().toString());
                builder.withValue(CustomColumns.DELIVERY_CITY_CUSTOM, mCity.getText().toString());
                builder.withValue(CustomColumns.IMAGEN_CUSTOM, mCurrentPhotoPath);
                builder.withValue(CustomColumns.REFERENCE_CUSTOM, mIdContact);
                builder.withValue(CustomColumns.CUIT_CUSTOM, mCuit.getText().toString());
                builder.withValue(CustomColumns.IVA_CUSTOM, mIva.getText().toString());
                builder.withValue(CustomColumns.SPECIAL_CUSTOM,mSpecial.isChecked()?1:0);
                batchOperations.add(builder.build());

            }

            try {
//
//
                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }
            getActivity().onBackPressed();
        }
    }

    public void deleteCustomer() {

        Log.i(LOG_TAG, "deleteCustomer");
        Log.i(LOG_TAG, "mAction" + mAction);
        Log.i(LOG_TAG, " mItem" + mItem);
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mAction == CUSTOM_SELECTION && mItem != 0) {

            Log.i(LOG_TAG, "entro");
//            ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete();
//            batchOperations.add(builder.build());
            getActivity().getContentResolver().delete(
                    LogisticaProvider.Customs.withId(mItem), null, null);
            getActivity().onBackPressed();
        }

    }

    public Boolean verification(){
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader" + id);
        switch (id) {
            case DETAIL_CUSTOM_LOADER:
                Log.e(LOG_TAG, "onCreateLoader-DETAIL_PRODUCT_LOADER");

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.Customs.withId(mItem),
                        null,
                        null,
                        null,
                        null);


            case DEFAULT_DETAIL_CUSTOM_LOADER:
                Log.e(LOG_TAG, "onCreateLoader-DETAIL_PRODUCT_LOADER");
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.Customs.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);




            default:
                Log.e(LOG_TAG, "onCreateLoader-Default");
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "onLoadFinished");
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case DETAIL_CUSTOM_LOADER:
                    if (data != null && data.moveToFirst()) {
                        Log.e(LOG_TAG, "DETAIL_PRODUCT_LOADER data != null && data.moveToFirst() cantidad" + data.getCount());
                    }
                    break;
                case DEFAULT_DETAIL_CUSTOM_LOADER:
                    if (data != null && data.moveToFirst()) {
                        Log.e(LOG_TAG, "DEFAULT_DETAIL_PRODUCT_LOADERdata != null && data.moveToFirst() cantidad" + data.getCount());
                    }
                    break;


            }
            mCustomName.setText(data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)));
            mLastName.setText(data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
            mDeliveyAddress.setText(data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)));
            mCity.setText(data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
            mCurrentPhotoPath=data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM));
            mIdContact=data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM));
            mCuit.setText(data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)));
            mIva.setText(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM)));
            mSpecial.setChecked(data.getInt(data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM))>0 ?true:false);


            if (mIdContact != null){
            button.setBackgroundColor(Color.GREEN);
                Log.e(LOG_TAG, "button green");
            } else
            {  Log.e(LOG_TAG, "button null");}


            Picasso.with(getContext())
                    .load(mCurrentPhotoPath)
                    .into(mImageCustomer);

            


            if (appBarLayout != null) {
                {
                    appBarLayout.setTitle(data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM))+" "+
                            data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM))
                    );
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG, "onLoaderReset");
    }
}
