package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;

import android.view.MenuItem;

import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;

import com.nextnut.logistica.rest.ProductCursorAdapter;
import com.nextnut.logistica.rest.Products;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.Util.DividerItemDecoration;

import java.util.ArrayList;


/**
 * An activity representing a list of Products. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProductDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProductListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    private ProductCursorAdapter mCursorAdapter;

    private ItemTouchHelper mItemTouchHelper;

    Products[] products ={
            new Products("Mercury","imagen 1",1.2),
            new Products("Venus", "imagen 2",2.5),
            new Products("Earth", "imagen 3",3.31),
            new Products("Mars", "imagen 4",4.44),
            new Products("Ceres", "imagen 5",5.55),
            new Products("Jupiter", "imagen 6",6.66),
            new Products("Saturn", "imagen 7",7.77),
            new Products("Uranus", "imagen 8",8.88),
            new Products("Neptune", "imagen 9",9.99),
            new Products("Pluto", "imagen 10",10.10),
            new Products("Eris", "imagen 11",11.11)
    };
    private RecyclerView recyclerView;
    ;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Context context = getContext();
                        Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                        intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, "New Product");

                        startActivity(intent);
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        View emptyView = findViewById(R.id.recyclerview_product_empty);
        recyclerView = (RecyclerView)findViewById(R.id.product_list);
//        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);


//        **************************
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.productos_list);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(recyclerView.getContext()));

        mCursorAdapter = new ProductCursorAdapter(this, null,emptyView);
        recyclerView.setAdapter(mCursorAdapter);



//        recyclerView.addItemDecoration(
//                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

//        **************************
        if (findViewById(R.id.product_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }
//
//    RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.productos_list);
//    recyclerView.setLayoutManager(
//            new LinearLayoutManager(recyclerView.getContext())
//            );

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Cursor c =this.getContentResolver().query(LogisticaProvider.Products.CONTENT_URI,
                null, null, null, null);

        if (c == null || c.getCount() == 0) {
            Log.i(LOG_TAG, "cursor count: null");
//            insertData();
        }

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);

        super.onPostCreate(savedInstanceState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,LogisticaProvider.Products.CONTENT_URI,
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





    public void insertData() {
        Log.d(LOG_TAG, "insert");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(products.length);

        for (Products p : products) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    LogisticaProvider.Products.CONTENT_URI);
            builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, p.getDetalle());
            builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, p.getImagen());
            builder.withValue(ProductsColumns.PRECIO_PRODUCTO, p.getPrecio());
            batchOperations.add(builder.build());
        }

        try {
            this.getContentResolver().applyBatch( LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }


//    public class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final List<DummyContent.DummyItem> mValues;
//
//        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
//            mValues = items;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.product_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(ProductDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                        ProductDetailFragment fragment = new ProductDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.product_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, ProductDetailActivity.class);
//                        intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView mIdView;
//            public final TextView mContentView;
//            public DummyContent.DummyItem mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
//                mContentView = (TextView) view.findViewById(R.id.content);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
//        }
//    }
}
