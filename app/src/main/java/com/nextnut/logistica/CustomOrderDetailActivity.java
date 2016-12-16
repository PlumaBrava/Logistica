package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.Producto;

import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.REQUEST_PRODUCT;
import static com.nextnut.logistica.util.Constantes.UPDATE_CUSTOMER;

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

            arguments.putLong(CustomOrderDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(CustomOrderDetailFragment.ARG_ITEM_ID,0));
            int mAction = getIntent().getIntExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
            arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, mAction);
            CabeceraOrden cabeceraOrden= getIntent().getParcelableExtra(EXTRA_CABECERA_ORDEN);
            Log.d(LOG_TAG, "orden:onComplete: getClienteKey " + cabeceraOrden.getClienteKey());
            Log.d(LOG_TAG, "orden:onComplete: getCliente().getNombre() " + cabeceraOrden.getCliente().getNombre());
            Log.d(LOG_TAG, "orden:onComplete: cabeceraOrden.getTotales().getCantidadDeOrdenesClientes() " + cabeceraOrden.getTotales().getCantidadDeOrdenesClientes());


            arguments=putBundleFirebase();
            arguments.putParcelable(EXTRA_CABECERA_ORDEN , cabeceraOrden);
            CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.customorder_detail_container, fragment)
                    .commit();
        }
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
            NavUtils.navigateUpTo(this, new Intent(this, CustomOrderListFragment.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode ==UPDATE_CUSTOMER && resultCode == RESULT_OK) {
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

        if (requestCode ==REQUEST_PRODUCT && resultCode == RESULT_OK) {
            String res = data.getExtras().getString(RESULTADO);
            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);

            if (fragmentCustomOrder != null) {
                Producto p=(Producto) data.getExtras().getParcelable(EXTRA_PRODUCT);
                fragmentCustomOrder.agregarProductoAlaOrden(
                        p.getCantidadDefault()*1.0,
                        data.getExtras().getString(EXTRA_PRODUCT_KEY),
                        p);

            }


        }


    }
}
