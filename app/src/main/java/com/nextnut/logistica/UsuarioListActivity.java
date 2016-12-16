package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.UsuarioPerfil;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.UsuarioPerfilViewHolder;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_USERS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_USER_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;

/**
 * This activity representing a list of Usuarios que administra una empresa.
 *
 *      Nodo /EMPRESA_USERS/
 *      modelo de datos: UsuarioPerfil
 *
 *  Desde esta actividad se ven los usuarios y si su usuario tiene a la empresa seleccionada para trabajar.
 *
 *  Desde aqui se pueden: dar de alta Nuevos Usuarios
 *                        modificar su perfil
 *                        desactivarlos.
 *
 *
 *
 *
 */
public class UsuarioListActivity extends ActivityBasic{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    // [END define_database_reference]

    private FirebaseRecyclerAdapter<UsuarioPerfil, UsuarioPerfilViewHolder> mAdapter;
    private boolean mTwoPane;
    private static final String TAG = "UsuarioListActivity";
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRecycler=(RecyclerView)findViewById(R.id.usuario_list);
        mRecycler.setHasFixedSize(true);

        Query userEmpresa= mDatabase.child(ESQUEMA_USER_EMPRESA).child(mUserKey);
        userEmpresa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onAuthSuccess:getChildrenCount: " + dataSnapshot.getChildrenCount());

                if(dataSnapshot.getChildrenCount()<=0){
                    // Crear una empresa
                    Log.d(TAG, "onAuthSuccess:getChildrenCount()<=0- No hay empresa Asignada " );

                }

                else {
                    Log.d(TAG, "onAuthSuccess:getChildrenCount: - hay empresa Asignada ");
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                                String category = (String) messageSnapshot.child("category").getValue();
                        Log.d(TAG, "onAuthSuccess-KEY"+ messageSnapshot.getKey());
                        Empresa u = messageSnapshot.getValue(Empresa.class);
                        mEmpresa=u;
                        mEmpresaKey=messageSnapshot.getKey();
                        Log.d(TAG, "onAuthSuccess-EMPRESA Key:"+mEmpresaKey);
                        Log.d(TAG, "onAuthSuccess-EMPRESA NOMBRE:"+ u.getNombre()+" - "+u.getCiudad());
                        startAdapter();

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onAuthSuccess "+ databaseError.toString());

            }
        });

//        mRecycler=(RecyclerView)findViewById(R.id.recycler_empresas_list);
//        mRecycler.setHasFixedSize(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), UsuarioDetailActivity.class);
//                intent.putExtra(UsuarioDetailFragment.EXTRA_USER_KEY, null);
                intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
                Log.d(TAG, "empresa: Nombre " + mEmpresa.getNombre());
                Log.d(TAG, "empresa: Ciudad " + mEmpresa.getCiudad());
                Log.d(TAG, "empresa: Direccion " + mEmpresa.getDireccion());
                Log.d(TAG, "empresa: Cuit " + mEmpresa.getCuit());
                intent.putExtra(EXTRA_EMPRESA,mEmpresa);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }





        if (findViewById(R.id.usuario_detail_container) != null) {
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
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public Query getQuery(DatabaseReference databaseReference) {
        Log.d(TAG, "getQuery empresa:"+ mEmpresaKey);
        // Todas las empresas de este User
        return databaseReference.child(ESQUEMA_EMPRESA_USERS)
                .child(mEmpresaKey);
    }

//    @Override
    public void startAdapter() {
//        super.onStart();
        Log.d(TAG, "startAdapter");

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getApplication());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);


        // Set up FirebaseRecyclerAdapter with the Query
        Query empresasQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<UsuarioPerfil, UsuarioPerfilViewHolder> (UsuarioPerfil.class, R.layout.usuario_list_content,
                UsuarioPerfilViewHolder.class, empresasQuery) {
            @Override
            protected void populateViewHolder(final UsuarioPerfilViewHolder viewHolder, final UsuarioPerfil model, final int position) {
                final DatabaseReference empresa_usuarioRef = getRef(position);
                Log.d(TAG, "populateViewHolder(postRef)"+empresa_usuarioRef.toString());

                // Set click listener for the whole post view
                final String userKey = empresa_usuarioRef.getKey();
                Log.d(TAG, "populateViewHolder(postRef)"+empresa_usuarioRef.toString());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch UserDetailActivity
                      Intent intent = new Intent(getApplication(), UsuarioDetailActivity.class);
                      putExtraFirebase(intent);
                      intent.putExtra(EXTRA_EMPRESA,mEmpresa);
                      startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
//                if (model.stars.containsKey(getUid())) {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
//                } else {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
//                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
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
                });
            }

            @Override
            protected void onItemDismissHolder(UsuarioPerfil model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(UsuarioPerfil model, int position) {

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        if (mAdapter != null) {
            Log.d(TAG, "mAdapter.cleanup()");
            mAdapter.cleanup();
        }
    }


}
