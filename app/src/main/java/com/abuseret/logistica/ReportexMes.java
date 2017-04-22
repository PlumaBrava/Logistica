package com.abuseret.logistica;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.abuseret.logistica.modelos.Detalle;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_REPORTE_VENTAS_PRODUCTO;

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



    public void reportTotalesXProductoFirebase() {

        mDatabase.child(ESQUEMA_REPORTE_VENTAS_PRODUCTO).child(mEmpresaKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String texto = " Producto   "+"\n";
                texto = texto+"FECHA      Cantidad   Monto "+"\n";
                String producto ="";
                String detalle ="";
                String productkey="";
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Log.i("reporteMes", "productkey " +productkey);
                    detalle ="";
                    String aamm="";
                    for (final DataSnapshot sn : snapshot.getChildren()) {
                        Detalle detalleOrden = sn.getValue(Detalle.class);
                        if(!productkey.equals(snapshot.getKey())) {
                            productkey = snapshot.getKey();
                            Log.i("reporteMes", "productkey" + productkey);
                            texto=texto+detalleOrden.getProducto().getNombreProducto()+"\n";
                        }
                        aamm=sn.getKey();

                        detalle=detalle+" "+aamm+" "+ detalleOrden.getCantidadEntrega()+" "+detalleOrden.getMontoItemEntrega()+" "+detalleOrden.getMontoImpuesto()+"\n";
                        Log.i("reporteMes", "nombreProducto " + detalleOrden.getProducto().getNombreProducto());
                        Log.i("reporteMes", "Cantidad Entrega " + detalleOrden.getCantidadEntrega());
                        Log.i("reporteMes", "Monto Entrega " + detalleOrden.getMontoItemEntrega());
                    }
                    texto=texto+detalle+"\n";
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
