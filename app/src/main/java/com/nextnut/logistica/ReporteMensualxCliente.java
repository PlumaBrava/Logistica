package com.nextnut.logistica;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;

public class ReporteMensualxCliente extends AppCompatActivity {

    private static final String LOG_TAG = ReporteMensualxCliente.class.getSimpleName();
    private TextView modenesXProducto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_mensualx_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        modenesXProducto = (TextView)findViewById(R.id.repoteMensualxCliente);
        reportTotalesXProductoy();
    }


    public void reportTotalesXProductoy(){
        String texto ="";
        String select[] = {


                "strftime('%Y-%m', "+ LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER+ " ) ",

                LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
                LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL+" ) as Qdeliver ",
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL+" ) as Qorder ",
        };



        try {
            Cursor c = getContentResolver().query(LogisticaProvider.reporte.CONTENT_URI,
                    select,
                    null,
                    null,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                String mes=c.getString(0)+"-";
                texto= mes +"     ORDEN            ENTREGADO  \n";
                String cliente=c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM))+" "+c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM));
                texto=texto+cliente+"\n";
                String producto=null;
                do {
                    if(!mes.equals(c.getString(0)+"-")){
                        mes=c.getString(0)+"-";
                        texto= texto+ mes +"\n";
                    }if(!cliente.equals(
                            c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM))+" "+c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM))
                    )) {
                        cliente=c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM))+" "+c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM));
                        texto=texto+cliente+ "\n";
                    }
                    producto=c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO));
                    texto=texto+"    "+producto+": "+c.getString(c.getColumnIndex("Qorder"))+"  -  "+c.getString(c.getColumnIndex("Qdeliver"))+ "\n";

                } while (c.moveToNext());


            } else {
            }

        } catch (Exception e) {
        }
        modenesXProducto.setText(texto);

    }
}
