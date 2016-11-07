package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.rest.ProductCursorAdapter;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.ProductViewHolder;

import java.util.ArrayList;

//import com.google.android.gms.common.api.GoogleApiClient;


public class ProductListActivity extends AppCompatActivity {
    //        implements LoaderManager.LoaderCallbacks<Cursor> {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Producto, ProductViewHolder> mAdapter;
    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    private long mItem = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


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
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        mUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, 0);
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
        View emptyView = findViewById(R.id.recyclerview_product_empty);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.product_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

//        mCursorAdapter = new ProductCursorAdapter(this, null, emptyView, new ProductCursorAdapter.ProductCursorAdapterOnClickHandler() {
//
//            @Override
//            public void onClick(long id, ProductCursorAdapter.ViewHolder vh) {
//
//                mItem=id;
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//                     // when rotate the screen the selecction of the second Screen is conserved.
//                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
//                    arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
//
//                    ProductDetailFragment fragment = new ProductDetailFragment();
//                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction()
//
//                            .addToBackStack(null)
//                            .replace(R.id.product_detail_container, fragment)
//                            .commit();
//
//                    fab_new.setVisibility(View.GONE);
//                    fab_save.setVisibility(View.VISIBLE);
//
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
//                    intent.putExtra(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
//                    intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, id);
//                    fab_new.setVisibility(View.VISIBLE);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Pair<View, String> p1 = Pair.create((View) vh.mphotoProducto, getString(R.string.detail_icon_transition_imagen));
//                        Pair<View, String> p2 = Pair.create((View) vh.mTextViewPrecio, getString(R.string.detail_icon_transition_price));
//                        Pair<View, String> p3 = Pair.create((View) vh.mTextViewNombre, getString(R.string.detail_icon_transition_name));
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(ProductListActivity.this,   p1,p2,p3);
//                        startActivity(intent, activityOptions.toBundle());
//
//                    } else {
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
//        recyclerView.setAdapter(mCursorAdapter);


        // Set up FirebaseRecyclerAdapter with the Query
        Query productosQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Producto, ProductViewHolder>(Producto.class, R.layout.product_list_item,
                ProductViewHolder.class, productosQuery) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Producto model, final int position) {
                final DatabaseReference postRef = getRef(position);
                Log.i("EmpresasView", "populateViewHolder(postRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                               if (mTwoPane) {
                                                                   Bundle arguments = new Bundle();
                                                                   // when rotate the screen the selecction of the second Screen is conserved.
//                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
//
                                                                   arguments.putString(ProductDetailFragment.EXTRA_PRODUCT_KEY, postKey);
                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);

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
                                                                   Intent intent = new Intent(getApplication(), ProductDetailActivity.class);
                                                                   intent.putExtra(ProductDetailFragment.EXTRA_PRODUCT_KEY, postKey);
                                                                   startActivity(intent);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Pair<View, String> p1 = Pair.create((View) ((ProductViewHolder)viewHolder.itemView).mphotoProducto, getString(R.string.detail_icon_transition_imagen));
//                        Pair<View, String> p2 = Pair.create((View) vh.mTextViewPrecio, getString(R.string.detail_icon_transition_price));
//                        Pair<View, String> p3 = Pair.create((View) vh.mTextViewNombre, getString(R.string.detail_icon_transition_name));
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(ProductListActivity.this,   p1,p2,p3);
//                        startActivity(intent, activityOptions.toBundle());
//
//                    } else {
//                        startActivity(intent);
//                        }
                                                               }

                                                           }

                                                       }

                );
                // Determine if the current user has liked this post and set UI accordingly
//                if (model.stars.containsKey(getUid())) {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
//                } else {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
//                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {
                                // Need to write to both places the post is stored
//                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
//                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
//
//                        // Run two transactions
//                        onStarClicked(globalPostRef);
//                        onStarClicked(userPostRef);
                            }
                        }

                );
            }
        }

        ;
        recyclerView.setAdapter(mAdapter);


        if (

                findViewById(R.id.product_detail_container)

                        != null)

        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;


            Bundle arguments = new Bundle();
            arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, mItem);// Fragmet load de last ID.
            arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_DOUBLE_SCREEN);
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
//        Log.i("EmpresasView", "getQuery user:"+mUserId);
        // Todas las empresas de este User
        return databaseReference.child("empresa-producto");
//                .child(mUserId);
    }


}
