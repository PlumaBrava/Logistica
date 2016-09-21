package com.nextnut.logistica;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;

public class ReportexMes extends AppCompatActivity {

    private static final String LOG_TAG = ReportexMes.class.getSimpleName();
    private TextView modenesXProducto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_mensualx_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        modenesXProducto = (TextView)findViewById(R.id.repoteMensualxCliente);
        reportTotalesXProductoy();
    }


    public void reportTotalesXProductoy(){
        String texto ="";
        String select[] = {


                "strftime('%Y-%m', "+ LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER+ " ) ",


                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL+" ) as Qdeliver ",
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL+" ) as Qorder "
        };

        Log.i(LOG_TAG, "Informe -  select: " +select[0] );

//       Total por Cliente
//        LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
//                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
//                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL+" ) ",
//                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL+" ) ",
//    };

        try {
            Cursor c = getContentResolver().query(LogisticaProvider.reportexMes.CONTENT_URI,
                    select,
                    null,
                    null,
                    null,
                    null);

//            LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO
            if (c != null && c.getCount() > 0) {
                Log.i(LOG_TAG, "Informe - count: " + c.getCount());
                c.moveToFirst();

                String mes=c.getString(0)+"-";
                Log.i(LOG_TAG, "Informe -ano mes"+mes);
                texto= mes +"     ORDEN            ENTREGADO  \n";

                String producto=null;
                do {
                    if(!mes.equals(c.getString(0)+"-")){
                        mes=c.getString(0)+"-";
                        texto= texto+ mes +"\n";
                        Log.i(LOG_TAG, "Informe -ano mes"+mes);
                    }
                    producto=c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO));
                    Log.i(LOG_TAG, "Informe -     PRODUCT_NAME: " + producto+": "+c.getString(c.getColumnIndex("Qdeliver"))+c.getString(c.getColumnIndex("Qorder")));
                    texto=texto+"    "+producto+": "+c.getString(c.getColumnIndex("Qorder"))+"  -  "+c.getString(c.getColumnIndex("Qdeliver"))+ "\n";

                } while (c.moveToNext());


            } else {
                Log.i(LOG_TAG, "Informe - count: " + "null");
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, " Informe Error applying batch insert", e);
        }
        modenesXProducto.setText(texto);

    }
}
