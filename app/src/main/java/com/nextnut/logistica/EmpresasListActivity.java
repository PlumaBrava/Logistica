package com.nextnut.logistica;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.EmpresaPerfil;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.EmpresaPerfilViewHolder;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_USER_PROPUETO_EMPRESA;
import static com.nextnut.logistica.util.KeyMailConverter.getKeyFromEmail;


/* Lista los usuarios asignados a USUARIOS_PROPUESTOS-EMPRESA

Utilizando esta lista el usuario pode elegir con que empresa trabajar.
Al seleccionar una empresa,

Se copia en USUARIO_EMPRESA Y USUARIO_PERFIL los datos de la empresa y del Perfil asignado. Se borran los anteriores.
Solo tiene que existir una empresa asignada y un perfil

 */
public class EmpresasListActivity extends AppCompatActivity {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<EmpresaPerfil, EmpresaPerfilViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String mUserMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("EmpresasView", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empesas_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mUserMail= FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mRecycler=(RecyclerView)findViewById(R.id.recycler_empresas_list);
        mRecycler.setHasFixedSize(true);







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public Query getQuery(DatabaseReference databaseReference) {
        Log.i("EmpresasView", "getQuery user:"+ getKeyFromEmail(mUserMail));
        // Todas las empresas de este User
        return databaseReference.child(ESQUEMA_USER_PROPUETO_EMPRESA)
                .child(getKeyFromEmail(mUserMail));
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
                final DatabaseReference postRef = getRef(position);
                Log.i("EmpresasView", "populateViewHolder(postRef)"+postRef.toString());

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
//                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
//                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
//                        startActivity(intent);
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


}
