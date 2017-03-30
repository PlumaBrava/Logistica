package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapterDistinct;
import com.nextnut.logistica.viewholder.ProductViewHolder;

import java.util.ArrayList;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.EXTRA_KEYLIST;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;

public class ProductSectionActivity extends ActivityBasic {
    private FirebaseRecyclerAdapterDistinct<Producto, ProductViewHolder> mAdapter;
//   ProductCursorAdapter mAdapter;
    private static final int CURSOR_LOADER_ID = 0;
    public static final String KEY_RefPRODUCTO = "refProducto";
    public static final String KEY_PRODUCTO_NAME = "ProductoName";
    public static final String KEY_PRODUCTO_PRICE = "ProductPrice";
    public static final String KEY_PRODUCTO_PRICES_ESPECIAL = "ProductPriceEspecial";
    private static final String LOG_TAG = ProductSectionActivity.class.getSimpleName();
    private long mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_section);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayList<String> keys=new ArrayList<String>();
        keys = getIntent().getStringArrayListExtra(EXTRA_KEYLIST);
        mItem = getIntent().getLongExtra("ITEM",-1);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View emptyView = findViewById(R.id.recyclerview_product_empty);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_product_selection);

        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(sglm);
        Query productoQuery = getQuery(mDatabase);

//        keys.add("-KX21qOSklZo9gNY7mrC");
        mAdapter = new FirebaseRecyclerAdapterDistinct<Producto, ProductViewHolder>(Producto.class, R.layout.product_list_item,
                ProductViewHolder.class, productoQuery,keys) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Producto model, final int position) {
                final DatabaseReference postRef = getRef(position);
                emptyView.setVisibility(View.GONE);
                Log.i("ClienteViewHolder", "populateViewHolder(postRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String productKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                               Intent intent = new Intent();
                                                               intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
                                                               intent.putExtra(EXTRA_PRODUCT, model);
                                                               setResult(RESULT_OK, intent);
                                                               finish();


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
        }

        ;

        recyclerView.setAdapter(mAdapter);

//        mAdapter= new ProductCursorAdapter(this,null,emptyView, new ProductCursorAdapter.ProductCursorAdapterOnClickHandler() {
//            @Override
//            public void onClick(long id, ProductCursorAdapter.ViewHolder vh) {
//                Intent intent = new Intent();
//                intent.putExtra(KEY_RefPRODUCTO, id);
//                intent.putExtra(KEY_PRODUCTO_NAME, vh.mNombreProducto.getText());
//
//                intent.putExtra(KEY_PRODUCTO_PRICE,vh.mTextViewPrecio.getText().toString());
//                intent.putExtra(KEY_PRODUCTO_PRICES_ESPECIAL,vh.mTextViewPrecioEspecial.getText().toString());
//                setResult(RESULT_OK, intent);
//                finish();
//
//
//            }
//        });



    }

//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//
//
//        StaggeredGridLayoutManager sglm =
//                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(sglm);
//
//        recyclerView.setAdapter(mAdapter);
//    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//
//        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
//
//        super.onPostCreate(savedInstanceState);
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//
//        String select =
//                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns._ID_PRODUCTO + " NOT IN ( SELECT DISTINCT " +
//                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL+
//                        " FROM "+LogisticaDataBase.CUSTOM_ORDERS_DETAIL+ " WHERE ( "+
//                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "+mItem+" ))";
//
//        return new CursorLoader(
//                this,
//                LogisticaProvider.Products.CONTENT_URI,
//                null,
//                select,
//                null,
//                null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if(data!=null) {
//            mAdapter.swapCursor(data);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
//
//
//    }

//    public class Adapter extends CursorRecyclerViewAdapter<ProductSectionActivity.Adapter.ViewHolder>{
//
//            private int mPosition;
//
//            private Cursor mCursor;
//            private DataSetObserver mDataSetObserver;
//            private boolean mDataIsValid;
//            private int mRowIdColumn;
//            View mEmptyView;
//
//            public Adapter(ProductSectionActivity productSectionActivity, Cursor cursor, View emptyView, ProductCursorAdapter.ProductCursorAdapterOnClickHandler productCursorAdapterOnClickHandler) {
//                super(getApplicationContext(),cursor,emptyView);
//                mRowIdColumn = mDataIsValid ? mCursor.getColumnIndex("_id") : -1;
//                mEmptyView=emptyView;
//                mCursor = cursor;
//            }
//
//
//            @Override
//            public long getItemId(int position) {
//                mCursor.moveToPosition(position);
//                return mCursor.getLong(mCursor.getColumnIndex(CustomColumns.ID_CUSTOM));
//            }
//
//            @Override
//            public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
//
//            }
//
//
//
//
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.product_list_item, parent, false);
//                return new ViewHolder(view);
//            }
//
//
//
//            @Override
//            public int getItemCount() {
//                int cantidad = 0;
//                if (mCursor != null) {
//                    cantidad = mCursor.getCount();
//                }
//                return cantidad;
//            }
//
//            public class ViewHolder extends RecyclerView.ViewHolder {
//                public final View mView;
//
//                public final TextView custonName;
//
//
//                public ViewHolder(View view) {
//
//                    super(view);
//                    mView = view;
//                    custonName = (TextView) view.findViewById(R.id.customNameSpinner);
//
//                }
//
//            }
//
//            public Cursor swapCursor(Cursor newCursor) {
//                if (newCursor == mCursor) {
//                    return null;
//                }
//                final Cursor oldCursor = mCursor;
//                if (oldCursor != null && mDataSetObserver != null) {
//                    oldCursor.unregisterDataSetObserver(mDataSetObserver);
//                }
//                mCursor = newCursor;
//                if (mCursor != null) {
//                    if (mDataSetObserver != null) {
//                        mCursor.registerDataSetObserver(mDataSetObserver);
//                    }
//                    mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
//                    mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
//                    mDataIsValid = true;
//                    notifyDataSetChanged();
//                } else {
//                    mRowIdColumn = -1;
//                    mDataIsValid = false;
//                    mEmptyView.setVisibility(View.VISIBLE );
//                    notifyDataSetChanged();
//                }
//                return oldCursor;
//            }
//    }
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey);
    }

}
