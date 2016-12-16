package com.nextnut.logistica.util;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaProvider;

import java.util.ArrayList;

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

    // Usar este esquema para migrar datos.

    public void updateDateFormat() {
        try {
            Cursor c =getContentResolver().query(LogisticaProvider.CustomOrders.CONTENT_URI,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();


                do {

                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(c.getLong(0)));
                    builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, "2016-08-15");
                    batchOperations.add(builder.build());
                    getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                } while (c.moveToNext());


            } else {
            }

        } catch (Exception e) {
        }
    }



    // Picking!!!!!



//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//
//
//        switch (id) {
//
//            case CUSTOM_ORDER_LOADER:
//
//                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER
//                };
//
//
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.ShowJoin.CONTENT_URI,
//                        proyection,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + "=" + 1
//                                + " and " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected(),
//                        null,
//                        null);
//
//            case PICKING_ORDER_LOADER:
//
//
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.PickingOrders.CONTENT_URI,
//                        null,
//                        LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PICKING_STATUS_INICIAL,
//                        null,
//                        null);
//
//            case PICKING_LOADER_TOTAL_PRODUCTOS:
//
//
//                String select[] = {
///* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
///* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
///* 2 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
///* 3 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
///* 4 */            "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
///* 5 */            LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
///* 6 */           "max ( " + LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL + " )",
///* 7 */           "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " )",
///* 8 */            "max ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL + " )"};
//
//
//                String where =
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected();
//
//
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.join_customorderDetail_Product_Customer_picking.CONTENT_URI,
//                        select,
//                        where,
//                        null,
//                        null);
//
//            default:
//                return null;
//
//        }
//    }

}
