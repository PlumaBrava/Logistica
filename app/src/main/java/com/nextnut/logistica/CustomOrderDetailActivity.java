package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.nextnut.logistica.util.ProductSectionActivity;

/**
 * An activity representing a single CustomOrder detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CustomOrderListFragment}.
 */
public class CustomOrderDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = CustomOrderDetailActivity.class.getSimpleName();

    private int mAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customorder_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);



//        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
//        fab_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//                CustomOrderDetailFragment customOrderDetailFragment=(CustomOrderDetailFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
//                if (customOrderDetailFragment!=null){
//                    customOrderDetailFragment.SaveCustomOrder();
//                    Log.i(LOG_TAG,"no null fragment");
//                }else {
//                    Log.i(LOG_TAG,"null fragment");
//                }
//
//            }
//
//        });
//
//        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
//        fab_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "fab_delete:Replace with your own detail action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//                CustomOrderDetailFragment customOrderDetailFragment=(CustomOrderDetailFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
//                if (customOrderDetailFragment!=null){
//                    customOrderDetailFragment.deleteCustomOrder();
//                    Log.i(LOG_TAG,"no null fragment");
//                }else {
//                    Log.i(LOG_TAG,"null fragment");
//                }
//
//
//
//
//            }
//        });

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
            Bundle arguments = new Bundle();
            arguments.putLong(CustomOrderDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(CustomOrderDetailFragment.ARG_ITEM_ID,0));
            mAction= getIntent().getIntExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION,CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
            arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION,mAction);
            CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.customorder_detail_container, fragment)
                    .commit();
//            if(mAction==CustomDetailFragment.CUSTOM_NEW){
//                fab_delete.setVisibility(View.GONE);
//                fab_save.setVisibility(View.VISIBLE);
//            }else {
//                fab_delete.setVisibility(View.VISIBLE);
//                fab_save.setVisibility(View.VISIBLE);
//            }

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
        Log.i(LOG_TAG, "LLego resultado ok" );
        Log.i(LOG_TAG, "LLego requestCode: "+requestCode );
        Log.i(LOG_TAG, "LLego resultCode: "+resultCode );
//        if (requestCode == MainActivity. REQUEST_CUSTOMER && resultCode == RESULT_OK) {
//            Bundle bundle = data.getExtras();
//
//            if (bundle == null){ Log.i(LOG_TAG, "LLego resyktado ok" + "bundleNULL");}
//            else {Log.i(LOG_TAG, "LLego resyktado ok" + "bundle ok: " + bundle.toString());
//
//                for (String key : bundle.keySet()) {
//                    Object value = bundle.get(key);
//                    Log.i("resyktado", String.format("%s %s (%s)", key,
//                            value.toString(), value.getClass().getName()));
//                }
//
//
//            }
//
//            String res = bundle .getString("resultado");
//            Log.i(LOG_TAG, "LLego resyktado ok " + res);
//            Log.i(LOG_TAG, "LLego resyktado ok int " + bundle .getInt("resultado"));
//            long customRef = bundle.getLong("resultado");
//
//            Log.i(LOG_TAG, "LLego resyktado ok long " + customRef);
//
//            if (customRef != 0) {
////            if ( mItem != 0) {
//                Log.i(LOG_TAG, "save New");
//                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrders.CONTENT_URI);
//                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customRef);
//                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
//                String formattedDate = df.format(new Date());
//                Log.i(LOG_TAG, "formattedDate:" + formattedDate);
//                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
//                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);
//
//                batchOperations.add(builder.build());
//                try {
//
//                    getApplicationContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                } catch (RemoteException | OperationApplicationException e) {
//                    Log.e(LOG_TAG, "Error applying batch insert", e);
//                }
//
//            }
//            Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
//            intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//            Log.i(LOG_TAG, "ARG_ITEM_ID: 1" + customRef);
//            Log.i(LOG_TAG, "CUSTOM_ACTION" + CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
////
//            Log.i("ProductListActivity", "makeSceneTransitionAnimation Normal");
//            startActivity(intent);
////            }
//
//        }

        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode == CustomOrderDetailFragment. UPDATE_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                long customRef = bundle.getLong("resultado");

                Log.i(LOG_TAG, "LLego resyktado ok long " + customRef);

                if (customRef != 0) {

                    CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


                    if (fragmentCustomOrder != null) {
                        Log.i(LOG_TAG, "Picking Frament Not Null");
                        fragmentCustomOrder.upDateCustomer(customRef);


                    } else {
                        Log.i(LOG_TAG, "Picking Frament  Null");
                    }
                }
            }
        }

        if (requestCode == CustomOrderDetailFragment. REQUEST_PRODUCT && resultCode == RESULT_OK) {
            String res = data.getExtras().getString("resultado");
            Log.i(LOG_TAG, "REQUEST_PRODUCT "+ data.getExtras().getString("ProductoName") );

            Log.i(LOG_TAG, "REQUEST_PRODUCT ProductPrice"+  data.getExtras().getString("ProductPrice") );

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
//                    findFragmentByTag("android:switcher:" + (R.id.container) + ":" + mViewPager.getCurrentItem());

            if (fragmentCustomOrder != null) {
                Log.i(LOG_TAG,"Picking Frament Not Null");
                fragmentCustomOrder.saveCustomOrderProduct(data.getExtras().getLong(ProductSectionActivity.KEY_RefPRODUCTO),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_NAME),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICES_ESPECIAL),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICE)

                );

            }else {
                Log.i(LOG_TAG, "Picking Frament  Null");
            }


        }


    }
}
