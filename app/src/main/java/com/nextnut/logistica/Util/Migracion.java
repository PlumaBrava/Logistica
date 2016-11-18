package com.nextnut.logistica.util;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;

/**
 * Created by perez.juan.jose on 12/11/2016.
 */

public class Migracion extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int DETAIL_CUSTOM_LOADER = 0;
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        getSupportLoaderManager().initLoader(DETAIL_CUSTOM_LOADER, null, this);
        super.onPostCreate(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case DETAIL_CUSTOM_LOADER:
                return new CursorLoader(this, LogisticaProvider.Customs.CONTENT_URI,
                        null,
                        null,
                        null,
                        CustomColumns.LASTNAME_CUSTOM + " ASC , " + CustomColumns.NAME_CUSTOM + " ASC  ");
            default:
                return null;
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case DETAIL_CUSTOM_LOADER:
                    if (data != null && data.moveToFirst()) {

//            mCustomName.setText(data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)));
//            mLastName.setText(data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
//            mDeliveyAddress.setText(data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)));
//            mCity.setText(data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
//            mCurrentPhotoPath=data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM));
//            mIdContact=data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM));
//            mCuit.setText(data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)));
//            mIva.setText(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM)));
//            mSpecial.setChecked(data.getInt(data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM)) > 0);
//
//
//            if (mIdContact != null){
//                button.setBackgroundColor(Color.GREEN);
//                button.setText(getUserName(getContext() ,mIdContact));
//            }
//
//            Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
//            Picasso.with(getContext())
//                    .load(mCurrentPhotoPath)
//                    .placeholder(drawable)
//                    .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                    .into(mImageCustomer);


                    }
            }}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
