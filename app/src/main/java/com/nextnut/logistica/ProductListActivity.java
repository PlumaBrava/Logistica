package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;

import android.view.MenuItem;


import com.google.android.gms.common.api.GoogleApiClient;
import com.nextnut.logistica.data.LogisticaProvider;

import com.nextnut.logistica.rest.ProductCursorAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;


import java.util.ArrayList;




public class ProductListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    private long mItem=0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private ProductCursorAdapter mCursorAdapter;


    private ItemTouchHelper mItemTouchHelper;


    private RecyclerView recyclerView;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;

    private ArrayList<ContentProviderOperation> batchOperations;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "New Product", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Context context = getContext();
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, 0);
                intent.putExtra(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_NEW);
                Log.i(LOG_TAG,"PRODUCT_MODIFICACION"+ false);
                fab_new.setVisibility(View.VISIBLE);
//                fab_save.setVisibility(View.GONE);
//                fab_delete.setVisibility(View.GONE);
                startActivity(intent);
            }
        });

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setVisibility(View.GONE);
                fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Save product", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ProductDetailFragment productDetailFragment=(ProductDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.product_detail_container);
                if(productDetailFragment!=null){
                    productDetailFragment.verificationAndsave();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
//                    fab_delete.setVisibility(View.GONE);
                    Log.i(LOG_TAG,"no null fragment");
                }else {
                    Log.i(LOG_TAG,"null fragment");
                }
            }

        });

//        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
//        fab_delete.setVisibility(View.GONE);
//        fab_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Delete Product", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                ProductDetailFragment productDetailFragment=(ProductDetailFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.product_detail_container);
//                if(productDetailFragment!=null){
//                    productDetailFragment.deleteProduct();
//                    fab_new.setVisibility(View.VISIBLE);
//                    fab_save.setVisibility(View.GONE);
//                    fab_delete.setVisibility(View.GONE);
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
        View emptyView = findViewById(R.id.recyclerview_product_empty);
        recyclerView = (RecyclerView) findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mCursorAdapter = new ProductCursorAdapter(this, null, emptyView, new ProductCursorAdapter.ProductCursorAdapterOnClickHandler() {

            @Override
            public void onClick(long id, ProductCursorAdapter.ViewHolder vh) {
                Log.i(LOG_TAG, "clicked id: " + id);
                Log.i(LOG_TAG, "mTwoPane: " + mTwoPane);

                mItem=id;
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                     // when rotate the screen the selecction of the second Screen is conserved.
                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
                    arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);

                    ProductDetailFragment fragment = new ProductDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()

//                            .setTransition(R.anim.slide_in_left)
//
//                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            .addToBackStack(null)
                            .replace(R.id.product_detail_container, fragment)
                            .commit();
//                    animateViewsIn();

                    fab_new.setVisibility(View.GONE);
                    fab_save.setVisibility(View.VISIBLE);
//                    fab_delete.setVisibility(View.VISIBLE);

                } else {
                    Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                    intent.putExtra(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
                    intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, id);
                    fab_new.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.i("ProductListActivity", "makeSceneTransitionAnimation");
//                        Pair<View,String> p= new Pair<View, String>(vh.mphotoProducto, getString(R.string.detail_icon_transition_name));
                        Pair<View, String> p1 = Pair.create((View) vh.mphotoProducto, getString(R.string.detail_icon_transition_imagen));
                        Pair<View, String> p2 = Pair.create((View) vh.mTextViewPrecio, getString(R.string.detail_icon_transition_price));
                        Pair<View, String> p3 = Pair.create((View) vh.mTextViewNombre, getString(R.string.detail_icon_transition_name));
                        ActivityOptionsCompat activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(ProductListActivity.this,   p1,p2,p3);
                        startActivity(intent, activityOptions.toBundle());

                    } else {
                        Log.i("ProductListActivity", "makeSceneTransitionAnimation Normal");
                        startActivity(intent);
                    }
                }
            }
        });
        recyclerView.setAdapter(mCursorAdapter);



//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(recyclerView);


        if (findViewById(R.id.product_detail_container) != null ) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;


            Bundle arguments = new Bundle();
            arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, mItem);// Fragmet load de last ID.
            arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_DOUBLE_SCREEN);
            Log.i(LOG_TAG,"PRODUCT_MODIFICACION"+ false);
            ProductDetailFragment fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, fragment)
                    .commit();
//            animateViewsIn();
        }


    }



    private void animateViewsIn() {
        ViewGroup root = (ViewGroup) findViewById(R.id.product_detail_container);
        int count = root.getChildCount();
        float offset = getResources().getDimensionPixelSize(R.dimen.offset_y);
        Interpolator interpolator =
                AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);

        // loop over the children setting an increasing translation y but the same animation
        // duration + interpolation
        for (int i = 0; i < count; i++) {
            View view = root.getChildAt(i);
            view.setVisibility(View.VISIBLE);
            view.setTranslationY(offset);
            view.setAlpha(0.85f);
            // then animate back to natural position
            view.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setInterpolator(interpolator)
                    .setDuration(1000L)
                    .start();
            // increase the offset distance for the next view
            offset *= 1.5f;
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        super.onPostCreate(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, LogisticaProvider.Products.CONTENT_URI,
                null,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }





}
