package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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


    private int mAction;


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_IMAGE_GET = 1889;

    CollapsingToolbarLayout appBarLayout;


    private int mItem = 0;

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

            mItem = getArguments().getInt(ARG_ITEM_ID);
            Log.i(LOG_TAG, "ARG_ITEM_ID" + mItem);
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


        mCustomId = (TextView) rootView.findViewById(R.id.custom_Id);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
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
        return rootView;
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
                batchOperations.add(builder.build());
            } else
//            if      (mAction==PRODUCT_SAVE )
            {

                Log.i(LOG_TAG, "save Modification");
                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Customs.withId(mItem));
                builder.withValue(CustomColumns.NAME_CUSTOM, mCustomName.getText().toString());
                builder.withValue(CustomColumns.LASTNAME_CUSTOM, mLastName.getText().toString());
                builder.withValue(CustomColumns.DELIIVERY_ADDRES_CUSTOM, mDeliveyAddress.getText().toString());
                builder.withValue(CustomColumns.DELIVERY_CITY_CUSTOM, mCity.getText().toString());
                builder.withValue(CustomColumns.IMAGEN_CUSTOM, mCurrentPhotoPath);

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
