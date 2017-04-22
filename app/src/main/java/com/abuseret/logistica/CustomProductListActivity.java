package com.abuseret.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.abuseret.logistica.modelos.Producto;
import com.abuseret.logistica.ui.FirebaseRecyclerAdapter;
import com.abuseret.logistica.viewholder.ProductViewHolder;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;

/**
 * An activity representing a list of Customs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CustomProductListActivity extends ActivityBasic {

//    private CustomsCursorAdapter mCursorAdapter;
    private FirebaseRecyclerAdapter<Producto, ProductViewHolder> mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;
    private static final int CURSOR_LOADER_ID = 0;
    private long mItem = 0;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;

    private static final String LOG_TAG = CustomProductListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        Log.i("ClienteViewHolder", "Custom Producto mPerfil" + mPerfil.getClientes());
        Log.i("ClienteViewHolder", "Custom Producto  mEmpresaKey" + mEmpresaKey);
        Log.i("ClienteViewHolder", "Custom Producto mEmpresa" + mEmpresa.getNombre());

        final View emptyView = findViewById(R.id.recyclerview_custom_empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.custom_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager( mRecyclerView.getContext()));

        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), CustomDetailActivity.class);
//                intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, 0);
                putExtraFirebase(intent);
                fab_new.setVisibility(View.VISIBLE);
                fab_save.setVisibility(View.GONE);
                fab_delete.setVisibility(View.GONE);
                startActivity(intent);
            }
        });


        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setVisibility(View.GONE);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDetailFragment customDetailFragment = (CustomDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if (customDetailFragment != null) {
                    customDetailFragment.verificationAndsave();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);
                } else {
                }
            }

        });

        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_delete.setVisibility(View.GONE);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDetailFragment customDetailFragment = (CustomDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if (customDetailFragment != null) {
                    customDetailFragment.deleteCustomer();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);
                } else {
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up FirebaseRecyclerAdapter with the Query
        Query clientesPQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Producto, ProductViewHolder>(Producto.class, R.layout.product_list_item,
                ProductViewHolder.class, clientesPQuery) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Producto model, final int position) {
                final DatabaseReference postRef = getRef(position);
                emptyView.setVisibility(View.GONE);
                Log.i("ClienteViewHolder", "CustomProduct populateViewHolder(postRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String customKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                               if (mTwoPane) {
                                                                   mClienteKey=customKey;
                                                                   Bundle arguments = putBundleFirebase();

                                                                   mClienteKey=null;
                                                                   // when rotate the screen the selecction of the second Screen is conserved.
//                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
//
//                                                                   arguments.putString(EXTRA_CLIENTE_KEY, customKey);
//                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);

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
                                                                   Intent intent = new Intent(getApplication(), CustomDetailActivity.class);
                                                                   mClienteKey=customKey;
                                                                   putExtraFirebase(intent);
                                                                   mClienteKey=null;
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


        if (findViewById(R.id.custom_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            Bundle arguments =  putBundleFirebase();
//            arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, mItem);// Fragmet load de last ID.
//            arguments.putInt(CustomDetailFragment.CUSTOM_ACTION, CustomDetailFragment.CUSTOM_DOUBLE_SCREEN);
            CustomDetailFragment fragment = new CustomDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.custom_detail_container, fragment)
                    .commit();
        }
    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//       super.onPostCreate(savedInstanceState);
//        // Set up FirebaseRecyclerAdapter with the Query
//
//        // Set up Layout Manager, reverse layout
//        mManager = new LinearLayoutManager(this);
//        mManager.setReverseLayout(true);
//        mManager.setStackFromEnd(true);
//
//        mDetalleRecyclerView.setLayoutManager(mManager);
//        mDetalleRecyclerView.setHasFixedSize(true);
//        Query customQuery =getQuery(mDatabase);
////                mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey);
//        mAdapter = new FirebaseRecyclerAdapter<Cliente, Cliente1ViewHolder>(Cliente.class, R.layout.custom_list_item,
//                Cliente1ViewHolder.class, customQuery) {
//            @Override
//            protected void populateViewHolder( final Cliente1ViewHolder viewHolder, final Cliente model, final int position) {
//
//                final DatabaseReference postRef = getRef(position);
//                Log.i("ClienteViewHolder", "populateViewHolder(postRef)" + postRef.toString());
//
//                // Set click listener for the whole post view
//                final String clienteKey = postRef.getKey();
//
//                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                                                           @Override
//                                                           public void onClick(View v) {
//
//                                                               if (mTwoPane) {
//                                                                   Bundle arguments = new Bundle();
//                                                                   // when rotate the screen the selecction of the second Screen is conserved.
////                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
////  todo: revisar para que funciones bien con clientes, esto es pegado de productos
//                                                                   arguments.putString(EXTRA_CLIENTE_KEY, clienteKey);
//                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
//
//                                                                   CustomDetailFragment fragment = new CustomDetailFragment();
//                                                                   fragment.setArguments(arguments);
//                                                                   getSupportFragmentManager().beginTransaction()
//                                                                           .addToBackStack(null)
//                                                                           .replace(R.id.product_detail_container, fragment)
//                                                                           .commit();
//
//                                                                   fab_new.setVisibility(View.GONE);
//                                                                   fab_save.setVisibility(View.VISIBLE);
//
//                                                               } else {
//
//                                                                   // Launch PostDetailActivity
//                                                                   Intent intent = new Intent(getApplication(), CustomDetailActivity.class);
//                                                                   intent.putExtra(EXTRA_PRODUCT_KEY, clienteKey);
//                                                                   startActivity(intent);
//                                                               }
//
//                                                           }
//
//                                                       }
//
//                );
//
//                viewHolder.bindToPost(model, new View.OnClickListener()
//
//                        {
//                            @Override
//                            public void onClick(View starView) {
//                                // Need to write to both places the post is stored
////                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
////                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());
////
////                        // Run two transactions
////                        onStarClicked(globalPostRef);
////                        onStarClicked(userPostRef);
//                            }
//                        });
//            }
//        } ;
//        mDetalleRecyclerView.setAdapter(mAdapter);
//    }

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






    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey);
    }

}
