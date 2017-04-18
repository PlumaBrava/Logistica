package com.nextnut.logistica;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.ReporteClienteProducto;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_REPORTE_VENTAS_CLIENTE;

public class ReporteMensualxCliente extends ActivityBasic {

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

        reportTotalesXClienteFirebase();
    }




    public void reportTotalesXClienteFirebase() {

        mDatabase.child(ESQUEMA_REPORTE_VENTAS_CLIENTE).child(mEmpresaKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String texto = "FECHA   Producto   Cantidad   Monto "+"\n";
                String producto ="";
                String ClienteText ="";
                String detalle ="";
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String clienteKey = snapshot.getKey() ;
                    ClienteText = "";

                    Log.i("reporteMes", "clienteKey  " + clienteKey);
                    for (final DataSnapshot sn : snapshot.getChildren()) {
                        String productkey = sn.getKey();
                        Log.i("reporteMes", "productkey " + productkey);
                        detalle = "";
                        for (final DataSnapshot s : sn.getChildren()) {
                            String aamm = s.getKey();
                            Log.i("reporteMes", "aamm " + aamm);
                            ReporteClienteProducto reporte = s.getValue(ReporteClienteProducto.class);
                            if(ClienteText.equals("")){
                                ClienteText=reporte.getCliente().getApellido()+" "+reporte.getCliente().getNombre();
                                texto=texto+ "\n"+ClienteText+ "\n";
                            }
                            producto = reporte.getDetalle().getProducto().getNombreProducto() + "\n";
                            detalle = detalle + aamm + " " + reporte.getDetalle().getProducto().getNombreProducto() + " " + reporte.getDetalle().getCantidadEntrega() + " " + reporte.getDetalle().getMontoItemEntrega() +" " + reporte.getDetalle().getMontoImpuesto() + "\n";
                            Log.i("reporteMes", "nombreProducto " + reporte.getDetalle().getProducto().getNombreProducto());
                            Log.i("reporteMes", "Cantidad Entrega " + reporte.getDetalle().getCantidadEntrega());
                            Log.i("reporteMes", "Monto Entrega " + reporte.getDetalle().getMontoItemEntrega());
                        }
                        texto = texto +  detalle;
                    }
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
