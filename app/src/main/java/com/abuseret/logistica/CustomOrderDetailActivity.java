package com.abuseret.logistica;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.abuseret.logistica.modelos.CabeceraOrden;
import com.abuseret.logistica.modelos.Detalle;
import com.abuseret.logistica.modelos.Producto;

import static com.abuseret.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.abuseret.logistica.util.Constantes.EXTRA_NRO_PICKIG;
import static com.abuseret.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.abuseret.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.abuseret.logistica.util.Constantes.REQUEST_ENABLE_BT;
import static com.abuseret.logistica.util.Constantes.REQUEST_PRODUCT;
import static com.abuseret.logistica.util.Constantes.UPDATE_CUSTOMER;

/**
 * An activity representing a single CustomOrder detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CustomOrderListFragment}.
 */
public class CustomOrderDetailActivity extends ActivityBasic {

    private static final String LOG_TAG = CustomOrderDetailActivity.class.getSimpleName();
    private static final String RESULTADO = "resultado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customorder_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

//            arguments.putLong(CustomOrderDetailFragment.ARG_ITEM_ID,
//                    getIntent().getLongExtra(CustomOrderDetailFragment.ARG_ITEM_ID,0));
//            int mAction = getIntent().getIntExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
//            arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, mAction);
            CabeceraOrden cabeceraOrden = getIntent().getParcelableExtra(EXTRA_CABECERA_ORDEN);

            Long nroPicking = getIntent().getLongExtra(EXTRA_NRO_PICKIG, 0);
            Log.d(LOG_TAG, "orden:onComplete: getClienteKey " + cabeceraOrden.getClienteKey());
            Log.d(LOG_TAG, "orden:onComplete: orden Estatado " + cabeceraOrden.getEstado());
            Log.d(LOG_TAG, "orden:onComplete: getCliente().getNombre() " + cabeceraOrden.getCliente().getNombre());
            Log.d(LOG_TAG, "orden:onComplete: cabeceraOrden.getTotales().getCantidadDeOrdenesClientes() " + cabeceraOrden.getTotales().getCantidadDeOrdenesClientes());
            Log.d(LOG_TAG, "orden:onComplete: cabeceraOrden.getCliente().Perfildeprecios " + cabeceraOrden.getCliente().getPerfilDePrecios());
            Log.d(LOG_TAG, "orden:onComplete: mCliente.Perfildeprecios " + mCliente.getPerfilDePrecios());


            arguments = putBundleFirebase();
            arguments.putParcelable(EXTRA_CABECERA_ORDEN, cabeceraOrden);
            arguments.putLong(EXTRA_NRO_PICKIG, nroPicking);
            CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.customorder_detail_container, fragment)
                    .commit();
        }

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
////        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
////        filter.addAction(BluetoothDevice.ACTION_FOUND);
////        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
////        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothDevice.ACTION_FOUND :");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothDevice.ACTION_ACL_CONNECTED :");
                CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
                if (fragmentCustomOrder != null) {
                    fragmentCustomOrder.beginListenForData();;
                } else {
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothAdapter.ACTION_DISCOVERY_FINISHED :");

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED");

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothDevice.ACTION_ACL_DISCONNECTED ");

            }
            else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                Log.i("zebra22-Receiver", "BluetoothDevice.ACTION_PAIRING_REQUEST ");

            }
        }
    };

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
            NavUtils.navigateUpTo(this, new Intent(this, CustomOrderListFragment.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        Log.d(LOG_TAG, "zebra22 ActivityResult requestCode "+requestCode);
        Log.d(LOG_TAG, "zebra22 ActivityResult resultCode "+resultCode);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
        {
            Log.d(LOG_TAG, "zebra22 ActivityResult REQUEST_ENABLE_BT "+resultCode);
            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
            if (fragmentCustomOrder != null) {
                fragmentCustomOrder.conectaLaImpresoraSeleccionada();
            } else {
            }


        }
        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode == UPDATE_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                long customRef = bundle.getLong(RESULTADO);
                if (customRef != 0) {

                    CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
                    if (fragmentCustomOrder != null) {
                        fragmentCustomOrder.upDateCustomer(customRef);
                    } else {
                    }
                }
            }
        }

        if (requestCode == REQUEST_PRODUCT && resultCode == RESULT_OK) {
            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
            Producto p = (Producto) data.getExtras().getParcelable(EXTRA_PRODUCT);
            Log.d(LOG_TAG, "perf Llego Producto");
            Log.d(LOG_TAG, "perf Llego getPrecioParaPerfil " + p.getPrecioParaPerfil(mCliente.getPerfilDePrecios()));
            Log.d(LOG_TAG, "perf Llego getPrecioEspecialPerfil " + p.getPrecioEspecialPerfil(mCliente.getPerfilDePrecios()));
            Log.d(LOG_TAG, "perf Llego mCliente.getEspecial()" + mCliente.getEspecial());
            Log.d(LOG_TAG, "perf Llego mCliente.getNombre()" + mCliente.getNombre());

            if (fragmentCustomOrder != null) {
                if ((p.getPrecioParaPerfil(mCliente.getPerfilDePrecios()) == 0 && !mCliente.getEspecial()) ||
                        p.getPrecioEspecialPerfil(mCliente.getPerfilDePrecios()) == 0 && mCliente.getEspecial()) {
                    Log.d(LOG_TAG, "perf Prducto mal pefil");

                    fragmentCustomOrder.muestraMensaje("Revise el Perfil de Precios para "+p.getNombreProducto() +" y "+ mCliente.getNombre());
                } else {
                    if (fragmentCustomOrder != null) {


                        Detalle detalle = new Detalle(0.0, p, mCliente);
                        fragmentCustomOrder.abmDetalleDeOrden(p.getCantidadDefault() * 1.0, data.getExtras().getString(EXTRA_PRODUCT_KEY), detalle);

//                        Producto p=(Producto) data.getExtras().getParcelable(EXTRA_PRODUCT);
//                fragmentCustomOrder.abmDetalleDeOrden(
//                        p.getCantidadDefault()*1.0,
//                        data.getExtras().getString(EXTRA_PRODUCT_KEY),
//                        p);

                    }
                }
            }

        }


    }

//    @Override
//    protected void onDestroy() {
//        this.unregisterReceiver(mReceiver);
//        super.onDestroy();
//    }

    @Override
    protected void onPause() {
        Log.i("zebra22", "onPause() Orden " );
        this.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("zebra22", "onResume() main" );
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onStart() {

        Log.i("zebra22", "onStart() Orden" );

        // Registra el receiver para la conexion del bluetooth impresora.
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        this.registerReceiver(mReceiver, filter);
        super.onStart();
    }

}
