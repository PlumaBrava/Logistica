package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.ProductViewHolder;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;

//import com.google.android.gms.common.api.GoogleApiClient;


public class ProductListActivity extends ActivityBasic {
    //        implements LoaderManager.LoaderCallbacks<Cursor> {
//    private static final String LOG_TAG = ProductDetailFragment.class.getSimpleName();
    private FirebaseRecyclerAdapter<Producto, ProductViewHolder> mAdapter;
    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private RecyclerView mRecyclerView;


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


//    private ProductCursorAdapter mCursorAdapter;
//
//
//    private ItemTouchHelper mItemTouchHelper;


    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;

//    private ArrayList<ContentProviderOperation> batchOperations;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product_list);
        setContentView(R.layout.activity_product_list);
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        mUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//            getIntent().getStringExtra(EXTRA_USER_KEY);
//        mPerfil = (Perfil) getIntent().getParcelableExtra(EXTRA_PERFIL);
//        mEmpresaKey = getIntent().getStringExtra(EXTRA_EMPRESA_KEY);
//        mEmpresa = (Empresa) getIntent().getParcelableExtra(EXTRA_EMPRESA);

        Log.i("Producto", "mPerfil" + mPerfil.getClientes());
        Log.i("Producto", "mEmpresaKey" + mEmpresaKey);
        Log.i("Producto", "mEmpresa" + mEmpresa.getNombre());
        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                putExtraFirebase(intent);
//                intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, 0);
                intent.putExtra(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_NEW);
                fab_new.setVisibility(View.VISIBLE);
                startActivity(intent);
            }
        });

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setVisibility(View.GONE);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDetailFragment productDetailFragment = (ProductDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.product_detail_container);
                if (productDetailFragment != null) {
                    productDetailFragment.verificationAndsave();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                }
            }

        });


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        final View emptyView = findViewById(R.id.recyclerview_product_empty);
        final View emptyView = findViewById(R.id.recyclerview_product_empty);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.product_list_recyclerView);
        mRecyclerView = (RecyclerView) findViewById(R.id.product_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));


        // Set up FirebaseRecyclerAdapter with the Query
        Query productosQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Producto, ProductViewHolder>(Producto.class, R.layout.product_list_item,
                ProductViewHolder.class, productosQuery) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Producto model, final int position) {
                final DatabaseReference postRef = getRef(position);
                Log.i("Producto", "populateViewHolder(postRef)" + postRef.toString());
                emptyView.setVisibility(View.GONE);
                // Set click listener for the whole post view
                final String productKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                               if (mTwoPane) {
                                                                   mProductKey=productKey;
                                                                   Bundle arguments = putBundleFirebase();
                                                                   mProductKey=null;

                                                                   ProductDetailFragment fragment = new ProductDetailFragment();
                                                                   fragment.setArguments(arguments);
                                                                   getSupportFragmentManager().beginTransaction()
                                                                           .addToBackStack(null)
                                                                           .replace(R.id.product_detail_container, fragment)
                                                                           .commit();

                                                                   fab_new.setVisibility(View.GONE);
                                                                   fab_save.setVisibility(View.VISIBLE);

                                                               } else {

                                                                   // Launch PostDetailActivity
                                                                   mProductKey=productKey;
                                                                   Intent intent = new Intent(getApplication(), ProductDetailActivity.class);
                                                                   putExtraFirebase(intent);
                                                                   mProductKey=null;
                                                                   startActivity(intent);

                                                               }

                                                           }

                                                       }

                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {

                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Producto model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(Producto model, int position) {

            }
        }

        ;
        mRecyclerView.setAdapter(mAdapter);


        if (findViewById(R.id.product_detail_container) != null)

        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;


            Bundle arguments = putBundleFirebase();
            ProductDetailFragment fragment = new ProductDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, fragment)
                    .commit();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.i(LOG_TAG, "android.R.id.home");
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            onBackPressed();
//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

//        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        super.onPostCreate(savedInstanceState);
    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(this, LogisticaProvider.Products.CONTENT_URI,
//                null,
//                null,
//                null,
//                null);
//
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mCursorAdapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mCursorAdapter.swapCursor(null);
//    }


    public Query getQuery(DatabaseReference databaseReference) {


        Log.i("Producto", "ESQUEMA_EMPRESA_PRODUCTOS)-->" + ESQUEMA_EMPRESA_PRODUCTOS+"/"+mEmpresaKey);

        return databaseReference.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey);
//        return databaseReference.child(ESQUEMA_EMPRESA_PRODUCTOS);
    }


    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "onBackPressed");
        super.onBackPressed();
    }

    public boolean onSupportNavigateUp() {
        Log.i(LOG_TAG, "onSupportNavigateUp");
        onBackPressed();
        return true;
    }
}
