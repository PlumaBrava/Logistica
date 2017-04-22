package com.abuseret.logistica;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.abuseret.logistica.modelos.EmpresaPerfil;
import com.abuseret.logistica.ui.FirebaseRecyclerAdapter;
import com.abuseret.logistica.viewholder.EmpresaPerfilViewHolder;

import java.util.HashMap;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_USER_EMPRESA;
import static com.abuseret.logistica.util.Constantes.ESQUEMA_USER_PROPUETO_EMPRESA;
import static com.abuseret.logistica.util.Constantes.NODO_USER_EMPRESA;
import static com.abuseret.logistica.util.Constantes.NODO_USER_PERFIL;
import static com.abuseret.logistica.util.KeyMailConverter.getKeyFromEmail;

//import com.firebase.client.Firebase;


/* Lista los usuarios asignados a USUARIOS_PROPUESTOS-EMPRESA
*
*  Utilizando esta lista el usuario pode elegir con que empresa trabajar.
*     Al seleccionar una empresa,
*
*        Se copia en USUARIO_EMPRESA Y USUARIO_PERFIL los datos de la empresa y del Perfil asignado. Se borran los anteriores.
*        Solo tiene que existir una empresa asignada y un perfil
*
*  Se puede acceder a la pantalla de creacion de empresas.
*/
public class EmpresasListActivity extends ActivityBasic {

    // [START define_database_reference]

    // [END define_database_reference]

    private FirebaseRecyclerAdapter<EmpresaPerfil, EmpresaPerfilViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public static  final String LOG_TAG ="EmpresasListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empesas_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRecycler=(RecyclerView)findViewById(R.id.recycler_empresas_list);
        mRecycler.setHasFixedSize(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear una Empresa
                Intent intent = new Intent(getApplication(), EmpresasActivity.class);
                putExtraFirebase(intent);
                startActivity(intent);
            }
        });

        //todo: revisar porque al apretar la flecha para atras en la barra se ejecuta nuevamente MainActivity y se piereden
        // las variable de firebase


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
    }


    public Query getQuery(DatabaseReference databaseReference) {
        Log.i("EmpresasView", "getQuery user:"+ getKeyFromEmail(mUsuario.getEmail()));
        // Todas las empresas de este User
        return databaseReference.child(ESQUEMA_USER_PROPUETO_EMPRESA)
                .child(getKeyFromEmail(mUsuario.getEmail()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("EmpresasView", "onStart");

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getApplication());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);


        // Set up FirebaseRecyclerAdapter with the Query
        Query empresasQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<EmpresaPerfil, EmpresaPerfilViewHolder> (EmpresaPerfil.class, R.layout.item_empresa,
                EmpresaPerfilViewHolder.class, empresasQuery) {
            @Override
            protected void populateViewHolder(final EmpresaPerfilViewHolder viewHolder, final EmpresaPerfil model, final int position) {
                final DatabaseReference empresaPerfilRef = getRef(position);
                Log.i(LOG_TAG, "populateViewHolder(empresaPerfilRef)"+empresaPerfilRef.toString());

                // Set click listener for the whole post view
                final String empresaPerfilKey = empresaPerfilRef.getKey(); // es el empresaKey. se grabo con esta llave.
                                Log.i(LOG_TAG, "populateViewHolder(empresaPerfilRef)"+empresaPerfilRef.toString());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TOdo: Graba en USUARIO_EMPRESA, la empresa y sus datos
                        //                USUARIO_PEREFIL, el perfil
                        // Borra los datos anteriores porque solo tiene que haber una empresa asignada a por usuario.

                        mDatabase.child(ESQUEMA_USER_EMPRESA).child( mUserKey).removeValue();


                        Map<String, Object> empresaValues =  model.getEmpresa().toMap();
                        Map<String, Object> perfilValues =  model.getPerfil().toMap();

                        Map<String, Object> childUpdates = new HashMap<>();





//                        childUpdates.put(NODO_USER_EMPRESA + mUserKey + "/" , null);
                        // ToDO: En caso de modificaciones en la empresa, impactar a todos los usuarios que tienen a esta empresa.
                        childUpdates.put(NODO_USER_EMPRESA + mUserKey + "/" + empresaPerfilKey, empresaValues);
                        // para asignar el perfil actual al usuario
                        // ToDO: En caso de modificaciones en la empresa, impactar a todos los usuarios que tienen a esta empresa.
                        childUpdates.put(NODO_USER_PERFIL + mUserKey, perfilValues);

                        mDatabase.updateChildren(childUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(LOG_TAG, "task.isSuccessful(): " + task.isSuccessful());
                                        if (task.isSuccessful()) {
                                            mEmpresaKey =empresaPerfilKey;
                                            mEmpresa=model.getEmpresa();
                                            mPerfil=model.getPerfil();
                                            Intent intent = new Intent();
                                            putExtraFirebase(intent);
                                            setResult(RESULT_OK, intent);
                                            finish();



//                                            // Seleccionar una empresa o crear una
//                                            Intent intent = new Intent(getApplication(), MainActivity.class);
//                                            intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
////                            Log.d(TAG, "onAuthSuccess:getChildrenCount()<=0- Crear Empresa " );
//                                            intent.putExtra(EXTRA_EMPRESA,mEmpresa);
//                                            intent.putExtra(EXTRA_PERFIL,perfil );
//                                            startActivity(intent);

                                        } else {
                                            Log.d(LOG_TAG, "task.error: " + task.getException().getMessage().toString());

                                        }

                                    }
                                });


                    }
                });

                //      Determina si la empresa que se esta mostrando es la seleccionada, si es verdad la pinta de rojo.
                if (empresaPerfilKey.equals(mEmpresaKey)) {
                    Log.d(LOG_TAG, "empresaPerfilKey igual mEmpresaKey -- "+empresaPerfilKey+" : " + mEmpresaKey);

                    viewHolder.itemView.setBackgroundColor(Color.RED);
                } else {
                    Log.d(LOG_TAG, "empresaPerfilKey distinto mEmpresaKey -- "+empresaPerfilKey+" : " + mEmpresaKey);
                    viewHolder.itemView.setBackgroundColor(Color.WHITE);
                }

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
            protected void onItemDismissHolder(EmpresaPerfil model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(EmpresaPerfil model, int position) {

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "onBackPressed");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigateUp() {
                Log.i(LOG_TAG, "onNavigateUp()");
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(this, LoginActivity.class));
//        finish();
        return true;
//        onBackPressed();
//        return false;
    }
}
