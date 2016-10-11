package com.nextnut.logistica.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.PickingOrderProductsAdapter;

/**
 * Created by perez.juan.jose on 14/09/2016.
 */
public class SharePickingOrder {

    static public void sharePickingOrder(Context context, String pickingOrderID, String coment) {


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);


        shareIntent.setType("text/plain");

        String mensaje = "";

//                        String select[] = {
///* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
///* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
///* 2 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER,
///* 3 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
///* 4 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,
///* 5 */             "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
///* 6 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL,
///* 7 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
///* 8 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
///* 9 */             LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
///* 10 */           "sum( " + LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL+ " )",
///* 11 */            "sum( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL+ " )",
///* 12 */            "count( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL+ " )",
///* 13 */            "max( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL+ " )"};
//
//                        String where = LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderDetailFragment.STATUS_ORDER_PICKING
//                                + " and " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + vh.mPickingOrderNumber.getText().toString();


        String select[] = {
/* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
/* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
/* 2 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
/* 3 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
/* 4 */            "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
/* 5 */            LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
/* 6 */           "max ( " + LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL + " )",
/* 7 */           "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " )",
/* 8 */            "max ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL + " )"};


        String where =

                LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + pickingOrderID;


        mensaje = context.getResources().getString(R.string.PickingOrderNumber) + pickingOrderID + "\n" + "\n";

        Cursor c = context.getContentResolver().query(LogisticaProvider.join_customorderDetail_Product_Customer_picking.CONTENT_URI, select, where, null, null);
        if (c != null && c.moveToFirst()) {

            do {

                mensaje = mensaje + c.getString(PickingOrderProductsAdapter.COLUMN_NOMBRE_PRODUCTO) + ": " + c.getDouble(PickingOrderProductsAdapter.COLUMN_QTOTAL_PICKING) + "\n";

            } while (c.moveToNext());
        }
        mensaje = mensaje + "\n" + coment + "\n";

        shareIntent.putExtra(Intent.EXTRA_TEXT, mensaje);

        context.startActivity(shareIntent);
    }

}
