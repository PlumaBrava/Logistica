package com.nextnut.logistica;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.PerfilDePrecio;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.PerfilDePreciosViewHolder;
import com.rey.material.widget.Button;

import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_PERFIL_DE_PRECIOS;
import static com.nextnut.logistica.util.Constantes.NODO_PERFIL_DE_PRECIOS;

//public class EmpresasActivity extends AppCompatActivity {
public class SettingsLogisticaActivity extends ActivityBasic {
    private static final String TAG = "SettingsLogistica";


    private ValueEventListener mUserListener;

    private EditText mPerfilDePrecios;
    private RecyclerView mListadePerfilesDePrecios;
    private com.rey.material.widget.Button mBotonModificar;
    private FirebaseRecyclerAdapter<PerfilDePrecio, PerfilDePreciosViewHolder> mAdapterPerfilDePrecios;

    private String mPerfilDePreciosKey;



//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // [START post_value_event_listener]
//        final ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.i(TAG, "onDataChange: ");
//                // Get Post object and use the values to update the UI
//                mUsuario = dataSnapshot.getValue(Usuario.class);
//
//                Log.i(TAG, "onDataChange: User-name " + mUsuario.getUsername());
//                Log.i(TAG, "onDataChange: User-status " + mUsuario.getStatus());
//                Log.i(TAG, "onDataChange: User-activo " + mUsuario.getActivo());
//                // [START_EXCLUDE]
//
//                if (mUserListener != null) {
////                    mDatabase.child(ESQUEMA_USERS).child(mUser.getUid()).removeEventListener(mUserListener);
//                    mDatabase.child(ESQUEMA_USERS).child(mUserKey).removeEventListener(mUserListener);
//                    Log.i("producto", "onDataChange-removeEventListener ");
//
//                }
//                // [END_EXCLUDE]
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
//                // [START_EXCLUDE]
//                Toast.makeText(getApplication(), "Failed to load User.",
//                        Toast.LENGTH_SHORT).show();
//                // [END_EXCLUDE]
//            }
//        };
//        mUserListener = userListener;
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate " );
        setContentView(R.layout.activity_settings_logistica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mPerfilDePrecios = (EditText) findViewById(R.id.perfilText);
        mPerfilDePrecios.setText("Generico");
        mBotonModificar = (Button) findViewById(R.id.botonModificarPerfil);
        mBotonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writePerfilDePrecios(mPerfilDePrecios.getText().toString());
            }
        });

        mListadePerfilesDePrecios = (RecyclerView) findViewById(R.id.recycler_PerfilDePrecios);
        // use a linear layout manager
//        mListadePerfilesDePrecios.setHasFixedSize(true);
        mListadePerfilesDePrecios.setLayoutManager(new LinearLayoutManager(getApplication()));
        Query productosQuery = getQuery(mDatabase);
        mAdapterPerfilDePrecios = new FirebaseRecyclerAdapter<PerfilDePrecio, PerfilDePreciosViewHolder>(PerfilDePrecio.class, R.layout.perfil_precios_list_content,
                PerfilDePreciosViewHolder.class, productosQuery) {
            @Override
            protected void populateViewHolder(final PerfilDePreciosViewHolder viewHolder, final PerfilDePrecio model, final int position) {

                viewHolder.bindToPost(model, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mPerfilDePrecios.setText(model.getPerfilDePrecio());
                                mPerfilDePreciosKey = getRef(position).getKey();
                            }
                        });
//                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                            }
//                        });

            }

            @Override
            protected void onItemDismissHolder(PerfilDePrecio model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(PerfilDePrecio model, int position) {

            }
        };
        mListadePerfilesDePrecios.setAdapter(mAdapterPerfilDePrecios);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }




    private void writePerfilDePrecios(String perfilDePrecios) {
        Log.d(TAG, "onCreate " );

        if (validateForm()) {
            if (mPerfilDePreciosKey == null) {
                mPerfilDePreciosKey = mDatabase.child(ESQUEMA_PERFIL_DE_PRECIOS).child(mEmpresaKey).push().getKey();
            }
            Log.d(TAG, "mPerfilDePreciosKey:" + mPerfilDePreciosKey);




            HashMap<String, Object> result = new HashMap<>();
            PerfilDePrecio p= new PerfilDePrecio(perfilDePrecios);


            Map<String, Object> childUpdates = new HashMap<>();


            // para el listado de usuarios y su Perfiles por empresa
            childUpdates.put(NODO_PERFIL_DE_PRECIOS  + mEmpresaKey +"/"+mPerfilDePreciosKey, p.toMap());

            mDatabase.updateChildren(childUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "task.isSuccessful(): " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d(TAG, "task.isSuccessful():getResult " + task.getResult());
                                mPerfilDePreciosKey=null;
                                mPerfilDePrecios.setText("");
                            } else {
                                Log.d(TAG, "task.error: " + task.getException().getMessage().toString());

                            }

                        }
                    });


        }
        setEditingEnabled(true);
    }


    private void setEditingEnabled(boolean enabled) {
        mPerfilDePrecios.setEnabled(enabled);
        mBotonModificar.setEnabled(enabled);

    }


    private boolean validateForm() {
        boolean result = true;
// Valida el Perfil de Precios
        if (TextUtils.isEmpty(mPerfilDePrecios.getText().toString())) {
            mPerfilDePrecios.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mPerfilDePrecios.setError(null);
        }


        return result;
    }


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    public boolean onSupportNavigateUp() {
        Log.i(TAG, "onSupportNavigateUp");
        onBackPressed();
        return true;
    }
    public Query getQuery(DatabaseReference databaseReference) {



        return databaseReference.child(ESQUEMA_PERFIL_DE_PRECIOS).child(mEmpresaKey);
    }
}
