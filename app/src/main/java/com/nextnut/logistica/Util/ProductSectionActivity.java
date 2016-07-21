package com.nextnut.logistica.Util;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.CursorRecyclerViewAdapter;
import com.nextnut.logistica.rest.ProductCursorAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;

public class ProductSectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
   ProductCursorAdapter mAdapter;
    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = ProductSectionActivity.class.getSimpleName();
    private long mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_section);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mItem = getIntent().getLongExtra("ITEM",-1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View emptyView = findViewById(R.id.recyclerview_product_empty);
        View recyclerView = findViewById(R.id.content_product_selection);
//        mAdapter = new Adapter(null,emptyView);
        mAdapter= new ProductCursorAdapter(this,null,emptyView, new ProductCursorAdapter.ProductCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, ProductCursorAdapter.ViewHolder vh) {
                Log.i(LOG_TAG,"setupRecyclerView"+id);
                Intent intent = new Intent();
                intent.putExtra("refProducto", id);
                intent.putExtra("ProductoName", vh.mTextViewNombre.getText());
//                CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
                intent.putExtra("ProductPrice",vh.mTextViewPrecio.getText().toString());
                Log.i(LOG_TAG,"ProductPrice"+vh.mTextViewPrecio.getText().toString());
                setResult(RESULT_OK, intent);
                finish();


            }
        });

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {


        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String select =
                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns._ID_PRODUCTO + " NOT IN ( SELECT DISTINCT " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL+
                        " FROM "+LogisticaDataBase.CUSTOM_ORDERS_DETAIL+ " WHERE ( "+
                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "+mItem+" ))";

        Log.i(LOG_TAG,"onCreateLoader");
        return new CursorLoader(
                this,
                LogisticaProvider.Products.CONTENT_URI,
                null,
                select,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null) {
            Log.i(LOG_TAG,"onLoadFinished");
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG,"onLoaderReset");
        mAdapter.swapCursor(null);


    }
//
//    public class Adapter extends CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
//            extends RecyclerView.Adapter<VH>{

    public class Adapter extends CursorRecyclerViewAdapter<ProductSectionActivity.Adapter.ViewHolder>{

        private int mPosition;

        private Cursor mCursor;
        private DataSetObserver mDataSetObserver;
        private boolean mDataIsValid;
        private int mRowIdColumn;
        View mEmptyView;

        public Adapter(ProductSectionActivity productSectionActivity, Cursor cursor, View emptyView, ProductCursorAdapter.ProductCursorAdapterOnClickHandler productCursorAdapterOnClickHandler) {
            super(getApplicationContext(),cursor,emptyView);
            mRowIdColumn = mDataIsValid ? mCursor.getColumnIndex("_id") : -1;
            mEmptyView=emptyView;
            mCursor = cursor;
        }


        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mCursor.getColumnIndex(CustomColumns.ID_CUSTOM));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        }


//        @Override
//        public void onBindViewHolder(ViewHolder viewHolder, int position) {
//            mPosition=position;
//            if(!mDataIsValid){
//                throw new IllegalStateException("This should only be called when Cursor is valid");
//            }
//            if(!mCursor.moveToPosition(position)){
//                throw new IllegalStateException("Could not move cursor to position " + position);
//            }
//
//            if (mCursor == null) {
//                Log.i(LOG_TAG, "cursor Nulo");
//            } else{
//
//                mCursor.moveToPosition(position);
//
//
//
//                String text1 = mCursor.getString(mCursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM))+" "+mCursor.getString(mCursor.getColumnIndex(CustomColumns.NAME_CUSTOM));
//                viewHolder.custonName.setText(text1);
//
//                Log.i("LOG_TAG", "ID: " + Long.toString(getItemId(position)));
//
//                viewHolder.custonName.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Log.i(LOG_TAG,"nClickListener"+", valor id:"+getItemId(mPosition));
//                        Intent intent = new Intent();
//                        intent.putExtra("resultado",getItemId(mPosition);
//                        setResult(RESULT_OK, intent);
//                        finish();
//
//
//
//                    }
//                });
//
//            }
//        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(LOG_TAG,"onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_item, parent, false);
            return new ViewHolder(view);
        }



        @Override
        public int getItemCount() {
            int cantidad = 0;
            if (mCursor != null) {
                cantidad = mCursor.getCount();
            }
            return cantidad;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;

            public final TextView custonName;


            public ViewHolder(View view) {

                super(view);
                Log.i(LOG_TAG,"ViewHolder(");
                mView = view;
                custonName = (TextView) view.findViewById(R.id.customNameSpinner);

            }

        }

        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return null;
            }
            final Cursor oldCursor = mCursor;
            if (oldCursor != null && mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
            mCursor = newCursor;
            if (mCursor != null) {
                if (mDataSetObserver != null) {
                    mCursor.registerDataSetObserver(mDataSetObserver);
                }
                mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
                mDataIsValid = true;
                notifyDataSetChanged();
            } else {
                mRowIdColumn = -1;
                mDataIsValid = false;
                mEmptyView.setVisibility(View.VISIBLE );
                notifyDataSetChanged();
            }
            return oldCursor;
        }
    }

}
