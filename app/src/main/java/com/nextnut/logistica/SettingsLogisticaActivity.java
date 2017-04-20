package com.nextnut.logistica;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_PERFIL_DE_PRECIOS;
import static com.nextnut.logistica.util.Constantes.NODO_PERFIL_DE_PRECIOS;
import static com.nextnut.logistica.util.Constantes.REQUEST_ENABLE_BT;

//public class EmpresasActivity extends AppCompatActivity {
public class SettingsLogisticaActivity extends ActivityBasic {
    private static final String TAG = "SettingsLogistica";


    private ValueEventListener mUserListener;

    private EditText mPerfilDePrecios;
    private RecyclerView mListadePerfilesDePrecios;
    private com.rey.material.widget.Button mBotonModificar;
    private FirebaseRecyclerAdapter<PerfilDePrecio, PerfilDePreciosViewHolder> mAdapterPerfilDePrecios;

    BluetoothAdapter mBluetoothAdapter;
    private com.rey.material.widget.Spinner mImpresoraBT;
    public ArrayAdapter<CharSequence> mAdapterNombresBT;
    List<CharSequence> mNombreDeDispositvosBT = new ArrayList<CharSequence>();


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
        Log.d(TAG, "onCreate ");
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


        mImpresoraBT = (Spinner) findViewById(R.id.nombreImpresora);

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Log.i("zebra22", "size:mBluetoothAdapter Null" + mBluetoothAdapter);
            }
            Log.i("zebra22", "size:mBluetoothAdapter  mBluetoothAdapter.isEnabled() " + mBluetoothAdapter.isEnabled());

            if (!mBluetoothAdapter.isEnabled()) {
                Log.i("zebra22", "Activa Bluetooth ");

                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
            }

            cargoNombreDeDispostivosBT();

//            myLabel.setText("Bluetooth device found.");

        } catch (Exception e) {
            e.printStackTrace();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void cargoNombreDeDispostivosBT() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.i("zebra22", "size:" + pairedDevices.size());
        if (pairedDevices.size() > 0) {


            for (BluetoothDevice device : pairedDevices) {

                // RPP300 is the name of the bluetooth printer device
                // we got this name from the list of paired devices
                Log.i("zebra22", "name:" + device.getName());
                Log.i("zebra22", "getAddress():" + device.getAddress());
                Log.i("zebra22", "describeContents():" + device.describeContents());
                Log.i("zebra22", "BondState():" + device.getBondState());
                mNombreDeDispositvosBT.add(device.getName());


            }

            mAdapterNombresBT = new ArrayAdapter<CharSequence>(getApplication(), android.R.layout.simple_spinner_item, mNombreDeDispositvosBT);

            SharedPreferences sharedPref = getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);
            String BTseleccionado = sharedPref.getString(getString(R.string.BTPreferenica),"Sin sileccionar");
            Log.i("zebra22", "BTseleccionado:" + BTseleccionado);

            int seleccion=mAdapterNombresBT.getPosition(BTseleccionado);
            mImpresoraBT.setSelection(seleccion);
// Specify the layout to use when the list of choices appears
// Apply the adapter to the spinner
            mImpresoraBT.setAdapter(mAdapterNombresBT);
            mAdapterNombresBT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mImpresoraBT.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    Log.i("zebra22", "parent: " + parent.toString());
                    Log.i("zebra22", "view: " + view.toString());
                    Log.i("zebra22", "position: " + position);
                    Log.i("zebra22", "id: " + id);
                    Log.i("zebra22", "parent.getSelectedItem(): " + parent.getSelectedItem());
                    parent.getSelectedItem();
                    SharedPreferences sharedPref = getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.BTPreferenica), parent.getSelectedItem().toString());
                    editor.commit();

                }
            });
        }
    }

    private void writePerfilDePrecios(String perfilDePrecios) {
        Log.d(TAG, "onCreate ");

        if (validateForm()) {
            if (mPerfilDePreciosKey == null) {
                mPerfilDePreciosKey = mDatabase.child(ESQUEMA_PERFIL_DE_PRECIOS).child(mEmpresaKey).push().getKey();
            }
            Log.d(TAG, "mPerfilDePreciosKey:" + mPerfilDePreciosKey);


            HashMap<String, Object> result = new HashMap<>();
            PerfilDePrecio p = new PerfilDePrecio(perfilDePrecios);


            Map<String, Object> childUpdates = new HashMap<>();


            // para el listado de usuarios y su Perfiles por empresa
            childUpdates.put(NODO_PERFIL_DE_PRECIOS + mEmpresaKey + "/" + mPerfilDePreciosKey, p.toMap());

            mDatabase.updateChildren(childUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "task.isSuccessful(): " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d(TAG, "task.isSuccessful():getResult " + task.getResult());
                                mPerfilDePreciosKey = null;
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


    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        Log.d(LOG_TAG, "zebra22 ActivityResult requestCode " + requestCode);
        Log.d(LOG_TAG, "zebra22 ActivityResult resultCode " + resultCode);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            cargoNombreDeDispostivosBT();
            Log.d(LOG_TAG, "zebra22 ActivityResult REQUEST_ENABLE_BT " + resultCode);

        }
    }

    public Query getQuery(DatabaseReference databaseReference) {


        return databaseReference.child(ESQUEMA_PERFIL_DE_PRECIOS).child(mEmpresaKey);
    }
}
