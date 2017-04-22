package com.abuseret.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.abuseret.logistica.modelos.Almacen;
import com.abuseret.logistica.ui.FirebaseRecyclerAdapter;
import com.abuseret.logistica.viewholder.AlmacenesViewHolder;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_ALMACENES;
import static com.abuseret.logistica.util.Constantes.EXTRA_ALMACEN_KEY;

/**
 * An activity representing a list of Customs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AlmacenesListActivity extends ActivityBasic {


    private FirebaseRecyclerAdapter<Almacen, AlmacenesViewHolder> mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;

    private long mItem = 0;


    private RecyclerView mRecyclerView;


    private static final String LOG_TAG = AlmacenesListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almacenes_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        Log.i("ClienteViewHolder", "mPerfil" + mPerfil.getClientes());
        Log.i("ClienteViewHolder", "mEmpresaKey" + mEmpresaKey);
        Log.i("ClienteViewHolder", "mEmpresa" + mEmpresa.getNombre());

        final View emptyView = findViewById(R.id.recyclerview_almacenes_empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.almacenes_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager( mRecyclerView.getContext()));

        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), AlmacenesDetailActivity.class);
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

                AlmacenesDetailFragment almacenesDetailFragment = (AlmacenesDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if (almacenesDetailFragment != null) {
                    almacenesDetailFragment.verificationAndsave();
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

                AlmacenesDetailFragment almacenesDetailFragment = (AlmacenesDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if (almacenesDetailFragment != null) {
                    almacenesDetailFragment.deleteCustomer();
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
        Query clientesQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Almacen, AlmacenesViewHolder>(Almacen.class, R.layout.almacenes_list_item,
                AlmacenesViewHolder.class, clientesQuery) {
            @Override
            protected void populateViewHolder(final AlmacenesViewHolder viewHolder, final Almacen model, final int position) {
                final DatabaseReference postRef = getRef(position);
                emptyView.setVisibility(View.GONE);
                Log.i("almacenList", "populateViewHolder - almacenRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String alamcenKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {
                                                               Log.i("almacenList", "populateViewHolder on click-almacenKey" + alamcenKey);
                                                               if (mTwoPane) {
                                                                   Log.i("almacenList", "populateViewHolder mTwoPane " + mTwoPane);

                                                                   Bundle arguments = putBundleFirebase();
                                                                   arguments.putString(EXTRA_ALMACEN_KEY,alamcenKey);
                                                                   // when rotate the screen the selecction of the second Screen is conserved.
//                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
//
//                                                                   arguments.putString(EXTRA_CLIENTE_KEY, customKey);
//                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);

                                                                   AlmacenesDetailFragment fragment = new AlmacenesDetailFragment();
                                                                   fragment.setArguments(arguments);
                                                                   getSupportFragmentManager().beginTransaction()
                                                                           .addToBackStack(null)
                                                                           .replace(R.id.custom_detail_container, fragment)
                                                                           .commit();

                                                                   fab_new.setVisibility(View.GONE);
                                                                   fab_save.setVisibility(View.VISIBLE);

                                                               } else {
                                                                   Log.i("almacenList", "populateViewHolder mTwoPane " + mTwoPane);
                                                                   // Launch PostDetailActivity
                                                                   Intent intent = new Intent(getApplication(), AlmacenesDetailActivity.class);
//                                                                     intent.putExtra(EXTRA_USER_KEY, mUserKey);
                                                                   intent.putExtra(EXTRA_ALMACEN_KEY, alamcenKey);
                                                                   putExtraFirebase(intent);
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
            protected void onItemDismissHolder(Almacen model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(Almacen model, int position) {

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

            Bundle arguments = putBundleFirebase();
            CustomDetailFragment fragment = new CustomDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.custom_detail_container, fragment)
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

//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }






    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(ESQUEMA_ALMACENES).child(mEmpresaKey);
    }



}
