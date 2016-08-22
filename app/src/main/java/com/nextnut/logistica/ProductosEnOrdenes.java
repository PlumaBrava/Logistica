package com.nextnut.logistica;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;

public class ProductosEnOrdenes extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_ITEM_ID = "item_id";
    private long mItem;
    private static final int CUSTOM_LOADER_LIST = 0;
    private TextView modenesXProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_en_ordenes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mItem=getIntent().getExtras().getLong(ARG_ITEM_ID);


        modenesXProducto =(TextView)findViewById(R.id.productosEnOrdenes);
        getSupportLoaderManager().initLoader(CUSTOM_LOADER_LIST, null, this);
    }





    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String select[] = {

/* 0 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
/* 1 */             LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
/* 2 */             LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
/* 3 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
/* 4 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL ,
/* 5 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL,

        };


        return new CursorLoader(
                this,
                LogisticaProvider.reporte.CONTENT_URI,
                select,
                LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderDetailFragment.STATUS_ORDER_INICIAL
                +" and " +LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL +" = "+ mItem,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        String text =" nulo";

        if (data != null && data.moveToFirst()) {
            text = data.getString(5)+ ": "+"\n";
            do {

            text = text+ "orden: "+ data.getString(0);
            text = text+ " "+ data.getString(1);
            text = text+ " "+ data.getString(2);
            text = text+" "+data.getString(4)+"\n";
            } while (data.moveToNext());
        }

        modenesXProducto.setText(text);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
