package com.nextnut.logistica;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.modelos.Detalle;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_REPORTE_VENTAS_PRODUCTO;

public class ReportexMes extends ActivityBasic {

    private static final String LOG_TAG = ReportexMes.class.getSimpleName();
    private TextView modenesXProducto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_x_mes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        modenesXProducto = (TextView)findViewById(R.id.repoteMensualxCliente);
        reportTotalesXProductoFirebase();
    }


    public void reportTotalesXProductoy(){
        String texto ="";
        String select[] = {
                "strftime('%Y-%m', "+ LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER+ " ) ",
                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL+" ) as Qdeliver ",
                "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL+" ) as Qorder "
        };



        try {
            Cursor c = getContentResolver().query(LogisticaProvider.reportexMes.CONTENT_URI,
                    select,
                    null,
                    null,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                String mes=c.getString(0)+"-";
                texto= mes +"     ORDEN            ENTREGADO  \n";

                String producto=null;
                do {
                    if(!mes.equals(c.getString(0)+"-")){
                        mes=c.getString(0)+"-";
                        texto= texto+ mes +"\n";
                    }
                    producto=c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO));
                    texto=texto+"    "+producto+": "+c.getString(c.getColumnIndex("Qorder"))+"  -  "+c.getString(c.getColumnIndex("Qdeliver"))+ "\n";
                } while (c.moveToNext());
            }

        } catch (Exception e) {
        }
        modenesXProducto.setText(texto);

    }

    public void reportTotalesXProductoFirebase() {

        mDatabase.child(ESQUEMA_REPORTE_VENTAS_PRODUCTO).child(mEmpresaKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String texto = "FECHA   Producto   Cantidad   Monto "+"\n";
                String producto ="";
                String detalle ="";
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productkey = snapshot.getKey();
                    Log.i("reporteMes", "productkey " +productkey);
                    detalle ="";
                    for (final DataSnapshot sn : snapshot.getChildren()) {
                        String aamm = sn.getKey();
                        Log.i("reporteMes", "aamm " + aamm);
                        Detalle detalleOrden = sn.getValue(Detalle.class);
                        producto=detalleOrden.getProducto().getNombreProducto()+"\n";
                        detalle=detalle+aamm+" "+detalleOrden.getProducto().getNombreProducto()+" "+ detalleOrden.getCantidadEntrega()+" "+detalleOrden.getMontoItemEntrega()+"\n";
                        Log.i("reporteMes", "nombreProducto " + detalleOrden.getProducto().getNombreProducto());
                        Log.i("reporteMes", "Cantidad Entrega " + detalleOrden.getCantidadEntrega());
                        Log.i("reporteMes", "Monto Entrega " + detalleOrden.getMontoItemEntrega());
                    }
                    texto=texto+producto+detalle;
                }

                modenesXProducto.setText(texto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            onBackPressed();
//            NavUtils.navigateUpTo(this, new Intent(this, CustomListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
