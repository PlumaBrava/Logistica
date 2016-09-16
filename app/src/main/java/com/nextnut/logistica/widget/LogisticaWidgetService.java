package com.nextnut.logistica.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.nextnut.logistica.MainActivity;
import com.nextnut.logistica.PickingListFragment;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.data.ProductsColumns;
import com.squareup.picasso.Picasso;

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
                Log.i(LOG_TAG, "emoteViewsFactory-onCreate: " );     // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                Log.i(LOG_TAG, "emoteViewsFactory-onDataSetChanged: " );

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
                        LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PickingListFragment.PICKING_STATUS_DELIVERY;
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + 1;                     ;


                data = getContentResolver().query(
                        LogisticaProvider.join_witget.CONTENT_URI,
                        select,where, null,null);
//                        select, null, null,null);
                Binder.restoreCallingIdentity(identityToken);

                if (data!=null && data.moveToFirst()) {
                    Log.i(LOG_TAG, "emoteViewsFactory-Data: " + data.getCount());
                }else {
                    Log.i(LOG_TAG, "emoteViewsFactory-Data null: ");

                }

            }

            @Override
            public void onDestroy() {
                Log.i(LOG_TAG, "on Destroy: " +  data.getCount());
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                if(data==null){

                    Log.i(LOG_TAG, "get count: Null");
                }
                else {
                    Log.i(LOG_TAG, "get count: "+data.getCount());
                }
                return data == null ? 0 : data.getCount();
            }


            @Override

            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    Log.i(LOG_TAG, "getViewAt Incorrect : " + position);
                    return null;

                }
                Log.i(LOG_TAG, "getViewAt position " + position);
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);


//                mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
//                mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
//                mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
////            mTextViewDescition = (TextView) view.findViewById(R.id.descriptionProducto);
//                mTextcantidad = (TextView) view.findViewById(R.id.cantidad);
//                mTextcantidadPicking = (TextView) view.findViewById(R.id.cantidadPicking);
//                mTextcantidadDelivey = (TextView) view.findViewById(R.id.cantidadTOTALDelivery);
//
///********/
//                viewHolder.mDetalleOrderId=cursor.getLong(COLUMN_ID_PRODUCTO);
//                viewHolder.mphotString=cursor.getString(COLUMN_IMAGEN_PRODUCTO);
//                Picasso.with(viewHolder.mphotoProducto.getContext())
//
//                        .load(cursor.getString(COLUMN_IMAGEN_PRODUCTO))
//                        .resize(96, 96)
//                        .placeholder(R.drawable.ic_action_action_redeem)
//                        .centerCrop()
//                        .into(viewHolder.mphotoProducto);
//
//                viewHolder.mTextViewNombre.setText(cursor.getString(COLUMN_NOMBRE_PRODUCTO));
////                NumberFormat format = NumberFormat.getCurrencyInstance();
////               Log.i("OrderDetailCursorAda:", "mRefPickingDetail prod: "+cursor.getString(6)+ "cant: "+Double.toString(cursor.getDouble(5))+
////                       "cantPincking: "+Double.toString(cursor.getDouble(11))+ " pickinID : "+cursor.getLong(10));
//
////                viewHolder.mTextViewPrecio.setText(format.format(cursor.getDouble(4)));
//                viewHolder.mTextcantidad.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_ORDENES)));
////                viewHolder.mTextcantidadPicking.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_PICKING)/cursor.getInt(COLUMN_OORDERS_COUNT)));
//                viewHolder.mTextcantidadPicking.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_PICKING)));
//
//                viewHolder.mTextcantidadDelivey.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_DELIVERY)));

                /********/
                String picking= getResources().getString(R.string.appwidget_picking);
                String product= getResources().getString(R.string.appwidget_product);
                views.setTextViewText(R.id.nombreProducto,picking+data.getString(0)+ product+data.getString(2));
//                views.setTextViewText(R.id.precioProducto, Utilies.getScores(data.getInt(INDEX_SCORE_HOME_GOALS), data.getInt(INDEX_SCORE_AWAY_GOALS)));
                views.setTextViewText(R.id.cantidad,  data.getString(5));
                views.setTextViewText(R.id.cantidadPicking,  data.getString(7));
                views.setTextViewText(R.id.cantidadTOTALDelivery,  data.getString(8));

//                Log.i(LOG_TAG, "geting view data_ID: " + data.getInt(position));
                final Intent fillInIntent = new Intent();

//                Uri ScoreUri = DatabaseContract.scores_table.buildScoreWithDate();
//                fillInIntent.setData(ScoreUri);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
//

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                Log.i(LOG_TAG, "LoadingView: " );
                return new RemoteViews(getPackageName(), R.layout.picking_product_item);
            }

            @Override
            public int getViewTypeCount() {
                Log.i(LOG_TAG, "ViewTypeCount: "+data.getCount() );
                return data.getCount();
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

//            @Override
//            public long getItemId(int position) {
//                Log.i(LOG_TAG, "Get Item ID: " +  position);
//                if (data.moveToPosition(position))
//                    return data.getLong(position);
//                return position;
//
//            }

            @Override
            public boolean hasStableIds() {

                Log.i(LOG_TAG, "has StableIds: " );
                return true;
            }
        };
    }


}
