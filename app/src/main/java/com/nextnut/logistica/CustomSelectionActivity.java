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
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.ClienteViewHolder;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;

public class CustomSelectionActivity extends ActivityBasic  {
    private FirebaseRecyclerAdapter<Cliente, ClienteViewHolder> mAdapter;


//    Adapter mAdapter;
    public static String RESULTADO = "resultado";
    private static final String LOG_TAG = CustomSelectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_custom_selection);

//        recyclerView.setLayoutManager(new LinearLayoutManager( getContext());


        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

//        mAdapter = new Adapter(null);
//        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);

        // Set up FirebaseRecyclerAdapter with the Query
        Query clientesQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Cliente, ClienteViewHolder>(Cliente.class, R.layout.custom_list_item,
                ClienteViewHolder.class, clientesQuery) {
            @Override
            protected void populateViewHolder(final ClienteViewHolder viewHolder, final Cliente model, final int position) {
                final DatabaseReference postRef = getRef(position);
//                emptyView.setVisibility(View.GONE);
                Log.i("ClienteViewHolder", "populateViewHolder(postRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String customKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

//                                                               if (mTwoPane) {
//                                                                   Bundle arguments = new Bundle();
//                                                                   // when rotate the screen the selecction of the second Screen is conserved.
////                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
////
//                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
//
//                                                                   ProductDetailFragment fragment = new ProductDetailFragment();
//                                                                   fragment.setArguments(arguments);
//                                                                  arguments.putString(EXTRA_PRODUCT_KEY, customKey);
//                                                                    getSupportFragmentManager().beginTransaction()
//                                                                           .addToBackStack(null)
//                                                                           .replace(R.id.product_detail_container, fragment)
//                                                                           .commit();
////
////                                                                   fab_new.setVisibility(View.GONE);
////                                                                   fab_save.setVisibility(View.VISIBLE);
//
//                                                               } else {




                                                                   Intent intent = new Intent();
                                                                   intent.putExtra(EXTRA_CLIENTE_KEY, customKey);
                                                                   intent.putExtra(EXTRA_CLIENTE, model);
                                                                   setResult(RESULT_OK, intent);
                                                                   finish();



//
//                                                                   // Launch PostDetailActivity
//                                                                   Intent intent = new Intent(getApplication(), ProductDetailActivity.class);
//                                                                   intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
//                                                                   startActivity(intent);
//
//                                                               }

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
            protected void onItemDismissHolder(Cliente model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(Cliente model, int position) {

            }
        }

        ;

        recyclerView.setAdapter(mAdapter);

    }


//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//
//        StaggeredGridLayoutManager sglm =
//                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(sglm);
//
//        recyclerView.setAdapter(mAdapter);
//    }





//    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
//
//        private Cursor mCursor;
//        private DataSetObserver mDataSetObserver;
//        private boolean mDataIsValid;
//        private int mRowIdColumn;
//
//        public Adapter(Cursor cursor) {
//            mCursor = cursor;
//        }
//
//
//        @Override
//        public long getItemId(int position) {
//            mCursor.moveToPosition(position);
//            return mCursor.getLong(mCursor.getColumnIndex(CustomColumns.ID_CUSTOM));
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.spiner_custom_layout, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//
//
//            if (mCursor == null) {
//            } else {
//
//                mCursor.moveToPosition(position);
//
//                holder.customCity.setText(mCursor.getString(mCursor.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
//                holder.custonName.setText(mCursor.getString(mCursor.getColumnIndex(CustomColumns.NAME_CUSTOM)) + " " +
//                        mCursor.getString(mCursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
//
//                Drawable drawable = dimensiona(CustomSelectionActivity.this, R.drawable.ic_action_action_redeem);
//                Picasso.with(CustomSelectionActivity.this)
//
//                        .load(mCursor.getString(mCursor.getColumnIndex(CustomColumns.IMAGEN_CUSTOM)))
//                        .resize(holder.photoCliente.getMaxWidth(), holder.photoCliente.getMaxHeight())
//                        .placeholder(drawable)
//                        .centerCrop()
//                        .into(holder.photoCliente);
//
//
//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent();
//                        intent.putExtra(RESULTADO, getItemId(holder.getAdapterPosition()));
//                        setResult(RESULT_OK, intent);
//                        finish();
//
//
//                    }
//                });
//
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            int cantidad = 0;
//            if (mCursor != null) {
//                cantidad = mCursor.getCount();
//            }
//            return cantidad;
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//
//            public final ImageView photoCliente;
//            public final TextView customCity;
//            public final TextView custonName;
//
//
//            public ViewHolder(View view) {
//
//                super(view);
//                mView = view;
//                photoCliente = (ImageView) view.findViewById(R.id.photoCliente);
//                customCity = (TextView) view.findViewById(R.id.customCity);
//                custonName = (TextView) view.findViewById(R.id.customNameSpinner);
//
//            }
//
//        }
//
//        public Cursor swapCursor(Cursor newCursor) {
//            if (newCursor == mCursor) {
//                return null;
//            }
//            final Cursor oldCursor = mCursor;
//            if (oldCursor != null && mDataSetObserver != null) {
//                oldCursor.unregisterDataSetObserver(mDataSetObserver);
//            }
//            mCursor = newCursor;
//            if (mCursor != null) {
//                if (mDataSetObserver != null) {
//                    mCursor.registerDataSetObserver(mDataSetObserver);
//                }
//                mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
//                mDataIsValid = true;
//                notifyDataSetChanged();
//            } else {
//                mRowIdColumn = -1;
//                mDataIsValid = false;
//                notifyDataSetChanged();
//            }
//            return oldCursor;
//        }
//    }


    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey);
    }

}
