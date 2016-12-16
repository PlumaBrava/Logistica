package com.nextnut.logistica.widget;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.data.ProductsColumns;

import static com.nextnut.logistica.util.Constantes.PICKING_STATUS_DELIVERY;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LogisticaWidgetService extends RemoteViewsService {
    private Cursor data = null;
    public final String LOG_TAG = LogisticaWidgetService.class.getSimpleName();


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                String select[] = {
  /* 0 */           LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.ID_PICKING_ORDERS,
/* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
/* 2 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
/* 3 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
/* 4 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
/* 5 */            "sum ( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL +" )",
/* 6 */            LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
/* 7 */           "max ( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL +" )",
/* 8 */           "sum ( "+  LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL +" )",
/* 9 */            "max  ( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL+" )"};


                String where =
                        LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PICKING_STATUS_DELIVERY;


                data = getContentResolver().query(
                        LogisticaProvider.join_witget.CONTENT_URI,
                        select,where, null,null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }


            @Override

            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;

                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);
                String picking= getResources().getString(R.string.appwidget_picking);
                String product= getResources().getString(R.string.appwidget_product);
                views.setTextViewText(R.id.nombreProducto,picking+data.getString(0)+ product+data.getString(2));
                views.setTextViewText(R.id.cantidad,  data.getString(5));
                views.setTextViewText(R.id.cantidadPicking,  data.getString(7));
                views.setTextViewText(R.id.cantidadTOTALDelivery,  data.getString(8));
                final Intent fillInIntent = new Intent();
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.picking_product_item);
            }

            @Override
            public int getViewTypeCount() {
                return data.getCount();
            }

            @Override
            public long getItemId(int i) {
                return i;
            }


            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
