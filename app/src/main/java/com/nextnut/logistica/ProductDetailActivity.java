package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;



/**
 * An activity representing a single Product detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ProductListActivity}.
 */
public class ProductDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProductDetailActivity.class.getSimpleName();
    private int mAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Save", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ProductDetailFragment productDetailFragment=(ProductDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.product_detail_container);
                if(productDetailFragment!=null){
                    productDetailFragment.verificationAndsave();
                    Log.i(LOG_TAG,"no null fragment");
                }else {
                    Log.i(LOG_TAG,"null fragment");
                }
            }
        });

//        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
//        fab_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Delete", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                ProductDetailFragment productDetailFragment=(ProductDetailFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.product_detail_container);
//                if(productDetailFragment!=null){
//                    productDetailFragment.deleteProduct();
//                    Log.i(LOG_TAG,"no null fragment");
//                }else {
//                    Log.i(LOG_TAG,"null fragment");
//                }
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
            arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, getIntent().getLongExtra(ProductDetailFragment.ARG_ITEM_ID,0));
            mAction= getIntent().getIntExtra(ProductDetailFragment.PRODUCT_ACTION,ProductDetailFragment.PRODUCT_SELECTION);
            arguments.putInt(ProductDetailFragment.PRODUCT_ACTION,mAction);
            Log.i(LOG_TAG, "CUSTOM_ACTION" + mAction);
            ProductDetailFragment fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            if(mAction==ProductDetailFragment.PRODUCT_NEW){
//                fab_delete.setVisibility(View.GONE);
                fab_save.setVisibility(View.VISIBLE);
            }else {
//                fab_delete.setVisibility(View.VISIBLE);
                fab_save.setVisibility(View.VISIBLE);
            }

            Log.i(LOG_TAG,"PRODUCT_ACTION"+ getIntent().getIntExtra(ProductDetailFragment.PRODUCT_ACTION,ProductDetailFragment.PRODUCT_SELECTION));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, fragment)
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
            NavUtils.navigateUpTo(this, new Intent(this, ProductListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }
    }
