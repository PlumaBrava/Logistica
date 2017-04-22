package com.abuseret.logistica.util;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.abuseret.logistica.ActivityBasic;
import com.abuseret.logistica.data.CustomColumns;
import com.abuseret.logistica.data.LogisticaProvider;
import com.abuseret.logistica.modelos.Cliente;

import java.util.HashMap;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.abuseret.logistica.util.Constantes.NODO_EMPRESA_CLIENTES;

/**
 * Created by perez.juan.jose on 12/11/2016.
 */

public class Migracion extends ActivityBasic implements LoaderManager.LoaderCallbacks<Cursor>  {
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

                        writeNewCliente(
                                data.getString(data.getColumnIndex(CustomColumns.ID_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)),
                                data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)),
                                Double.parseDouble(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM))),
                                data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)),
                                data.getInt (data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM)) > 0,
                                        null,"generico"
                        );
                    }


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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // Usar este esquema para migrar datos.

    public void upMigracionClientes() {
        try {
            Cursor data =getContentResolver().query(LogisticaProvider.Customs.CONTENT_URI,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (data != null && data.getCount() > 0) {
                data.moveToFirst();


                do {

                    writeNewCliente(
                            data.getString(data.getColumnIndex(CustomColumns.ID_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)),
                            Double.parseDouble(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM))),
                            data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)),
                            data.getInt (data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM)) > 0,
                            null,"genericos"
                    );
                } while (data.moveToNext());


            } else {
            }

        } catch (Exception e) {
        }
    }




    // [START basic_write]
    private void writeNewCliente(String Id,
                                 String nombre,
                                 String apellido,
                                 String telefono,
                                 String fotoCliente,
                                 String direccionDeEntrega,
                                 String ciudad,
                                 Double iva,
                                 String cuit,
                                 Boolean especial,
                                 Map<String, String> telefonos,
                                 String perfildePrecios
    ){
        if (true) {//validar formulario
            Log.i("migracionCliente", "writeNewClient: nombre " + nombre);
            Log.i("migracionCliente", "writeNewClient: apellido " + apellido);
            Log.i("migracionCliente", "writeNewClient: telefono " + telefono);
            Log.i("migracionCliente", "writeNewClient: fotoCliente " + fotoCliente);
            Log.i("migracionCliente", "writeNewClient: direccionDeEntrega " + direccionDeEntrega);
            Log.i("migracionCliente", "writeNewClient: ciudad " + ciudad);
            Log.i("migracionCliente", "writeNewClient: IVA " + iva);
            Log.i("migracionCliente", "writeNewClient: cUIT " + cuit);
            Log.i("migracionCliente", "writeNewClient: especial " + especial);


            Cliente cliente = new Cliente("Migracion",
                    nombre,
                    apellido,
                    telefono,
                    fotoCliente,
                    direccionDeEntrega,
                    ciudad,
                    iva,
                    cuit,
                    especial,telefonos,perfildePrecios);

            if (mClienteKey == null) {
                mClienteKey = mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).push().getKey();
            }

            Map<String, Object> clienteValues =  cliente.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(NODO_EMPRESA_CLIENTES + mEmpresaKey +"/"+ Id, clienteValues);

            mDatabase.updateChildren(childUpdates);
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
