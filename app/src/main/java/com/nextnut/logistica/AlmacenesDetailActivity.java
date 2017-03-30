package com.nextnut.logistica;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import static com.nextnut.logistica.util.Constantes.EXTRA_ALMACEN_KEY;

/**
 * An activity representing a single Custom detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CustomListActivity}.
 */
public class AlmacenesDetailActivity extends ActivityBasic {
    private static final String LOG_TAG = AlmacenesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almacen_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlmacenesDetailFragment almacenesDetailFragment=(AlmacenesDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.almacen_detail_container);
                if (almacenesDetailFragment!=null){
                    Log.i("almacenes", "almacenesDetailFragment!=null");
                    almacenesDetailFragment.verificationAndsave();
                }else{
                    Log.i("almacenes", "almacenesDetailFragment=null");

                }
            }

        });

        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlmacenesDetailFragment almacenesDetailFragment=(AlmacenesDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.almacen_detail_container);
                if (almacenesDetailFragment!=null){
                    Log.i("almacenes", "almacenesDetailFragment!=null");
                    almacenesDetailFragment.deleteCustomer();
                }else{
                    Log.i("almacenes", "almacenesDetailFragment=null");

                }

            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments =  putBundleFirebase();
            arguments.putString(EXTRA_ALMACEN_KEY,getIntent().getStringExtra(EXTRA_ALMACEN_KEY));
//            Bundle arguments = new Bundle();
//            arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, getIntent().getLongExtra(CustomDetailFragment.ARG_ITEM_ID,0));
//            int mAction = getIntent().getIntExtra(CustomDetailFragment.CUSTOM_ACTION, CustomDetailFragment.CUSTOM_SELECTION);
//            arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, mActCion);
//
//            arguments.putParcelable(EXTRA_EMPRESA, getIntent().getParcelableExtra(EXTRA_EMPRESA));
//            arguments.putString(EXTRA_EMPRESA_KEY, getIntent().getStringExtra(EXTRA_EMPRESA_KEY));
//            arguments.putParcelable(EXTRA_PERFIL,getIntent().getParcelableExtra(EXTRA_PERFIL));

            if(mClienteKey==null){//Si la key es null se trata de un cliente Nuevo
                fab_delete.setVisibility(View.GONE);
                fab_save.setVisibility(View.VISIBLE);
            }else {
                fab_delete.setVisibility(View.VISIBLE);
                fab_save.setVisibility(View.VISIBLE);
            }


            AlmacenesDetailFragment fragment = new AlmacenesDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.almacen_detail_container, fragment)
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
            onBackPressed();
//            NavUtils.navigateUpTo(this, new Intent(this, CustomListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
