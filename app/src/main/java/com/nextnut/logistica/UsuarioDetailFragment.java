package com.nextnut.logistica;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Usuario;
import com.nextnut.logistica.modelos.UsuarioPerfil;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.DialogAlerta;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Switch;

import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_USER_KEY;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_USERS;

/**
 * A fragment representing a single Usuario detail screen.
 * This fragment is either contained in a {@link UsuarioListActivity}
 * in two-pane mode (on tablets) or a {@link UsuarioDetailActivity}
 * on handsets.
 */
public class UsuarioDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final String DIALOG_FRAGMENT = "Dialog Fragment";


    public static  final String LOG_TAG ="UsuarioDetalle";

    private EditText mEmail;
    private EditText mEmailConfirmation;
    private Switch mUserSwitch;
    private CheckBox mPerfil_usuarios;
    private CheckBox mPerfil_productos;
    private CheckBox mPerfil_clientes;
    private CheckBox mPerfil_reportes;
    private CheckBox mPerfil_ordenes;
    private CheckBox mPerfil_preparar;
    private CheckBox mPerfil_entregar;
    private CheckBox mPerfil_pagos;
    private CheckBox mPerfil_stock;

    /**
     * The dummy content this fragment is presenting.
     */
    private ProgressView mSpinner;
    private View mFormulario;
    private static final String TAG = "UsuarioDetailFragmet";

    private String mUserKey;
    private String mEmpresaKey;
    private Empresa mEmpresa;
    private String mUserId;


    private DatabaseReference mDatabase;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsuarioDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getContext().getResources().getString(R.string.title_usuario_detail));
            }
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        mStorageRef = mStorage.getReferenceFromUrl(Constantes.STORAGE_REFERENCE);



        // Get post key from intent
        mUserKey = getArguments().getString(EXTRA_USER_KEY);
        mEmpresaKey = getArguments().getString(EXTRA_EMPRESA_KEY);
        mEmpresa= (Empresa) getArguments().getParcelable(EXTRA_EMPRESA);
        Log.d(TAG, "empresa: Nombre " +  mEmpresa.getNombre());
        Log.d(TAG, "empresa: Ciudad " +  mEmpresa.getCiudad());
        Log.d(TAG, "empresa: Direccion " +  mEmpresa.getDireccion());
        Log.d(TAG, "empresa: Cuit " +  mEmpresa.getCuit());

        if (mUserKey != null) { // Si exite mProductKey es que estamos modificando un producto.
            // Initialize Database
//            mDatabase = FirebaseDatabase.getInstance().getReference();

//                    .child("empresa").child("producto").push().getKey();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.usuario_detail, container, false);

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.usuario_detail)).setText("d-"+mItem.details);
//        }

        mSpinner = (ProgressView) rootView.findViewById(R.id.progressUsarios);
        mSpinner.setVisibility(View.GONE);

        mFormulario=(View) rootView.findViewById(R.id.form_usuarioDetail);

        mEmail=(EditText)rootView.findViewById(R.id.email);
        mEmailConfirmation=(EditText)rootView.findViewById(R.id.email_confirmation);
        mUserSwitch=(Switch) rootView.findViewById(R.id.userSwitch);
        mPerfil_usuarios=(CheckBox) rootView.findViewById(R.id.perfil_usuarios);
        mPerfil_productos=(CheckBox) rootView.findViewById(R.id.perfil_productos);
        mPerfil_clientes=(CheckBox) rootView.findViewById(R.id.perfil_clientes);
        mPerfil_reportes=(CheckBox) rootView.findViewById(R.id.perfil_reportes);
        mPerfil_ordenes=(CheckBox) rootView.findViewById(R.id.perfil_ordenes);
        mPerfil_preparar=(CheckBox) rootView.findViewById(R.id.perfil_preparar);
        mPerfil_entregar=(CheckBox) rootView.findViewById(R.id.perfil_entregar);
        mPerfil_pagos=(CheckBox) rootView.findViewById(R.id.perfil_pagos);
        mPerfil_stock=(CheckBox) rootView.findViewById(R.id.perfil_stock);


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        if (mUserKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
            Log.i(LOG_TAG, "onActivityCreated: " + mUserKey);
            // Add value event listener to show the data.
            // [START post_value_event_listener]
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(LOG_TAG, "onDataChange: ");
                    // Get Post object and use the values to update the UI
                    UsuarioPerfil usuarioPerfil = dataSnapshot.getValue(UsuarioPerfil.class);

                    mEmail.setText(usuarioPerfil.getUsuario().getEmail());
                    mEmailConfirmation.setVisibility(View.GONE);
                    mUserSwitch.setChecked(usuarioPerfil.getUsuario().getActivo());
                    mPerfil_usuarios.setChecked(usuarioPerfil.getPerfil().getUsuarios());
                    mPerfil_productos.setChecked(usuarioPerfil.getPerfil().getProductos());
                    mPerfil_clientes.setChecked(usuarioPerfil.getPerfil().getClientes());
                    mPerfil_reportes.setChecked(usuarioPerfil.getPerfil().getReportes());
                    mPerfil_ordenes.setChecked(usuarioPerfil.getPerfil().getOrdenes());
                    mPerfil_preparar.setChecked(usuarioPerfil.getPerfil().getPreparar());
                    mPerfil_entregar.setChecked(usuarioPerfil.getPerfil().getEntregar());
                    mPerfil_pagos.setChecked(usuarioPerfil.getPerfil().getPagos());
                    mPerfil_stock.setChecked(usuarioPerfil.getPerfil().getStock());


//                    Log.i("producto", "onDataChange-mCurrentPhotoPath: " + mCurrentPhotoPath);

//                    Picasso.with(getActivity())
//                            .load(mCurrentPhotoPath)
//                            .placeholder(R.drawable.ic_action_action_redeem)
//                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                            .into(mImageProducto);
//
//                    if (appBarLayout != null) {
//                        {
//                            appBarLayout.setTitle(producto.getNombreProducto());
//                        }
//                    }
//                    // ya tenemos los datos que queremos modificar por lo tanto desconectamos el listener!
//                    if (mProductListener != null) {
//                        mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mProductKey).removeEventListener(mProductListener);
//                        Log.i("producto", "onDataChange-removeEventListener ");
//
//                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(getContext(), "Failed to load Products.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };

            mDatabase.child(NODO_EMPRESA_USERS).child(mEmpresaKey).child(mUserKey).addListenerForSingleValueEvent(userListener);

            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops



        } else {
            Log.i("producto", "onActivityCreated: mProductKey: Null");

        }

        super.onActivityCreated(savedInstanceState);
    }

    public String getmEmail() {
        return mEmail.getText().toString();
    }

    // [START basic_write]
   public void writeNewUser() {
        if (validateForm()) {


//            String empresaKey ="-KV63YXYpmtO_bRnVfQk";
            DatabaseReference referenceEmpresaUser =mDatabase.child(Constantes.NODO_EMPRESA_USERS);

            // Verifica que si existe ese usuario en NewUser
            Query myNewUsers= referenceEmpresaUser.child(mEmpresaKey).orderByChild("usuario/email");
            myNewUsers
                    .startAt(mEmail.getText().toString())
                    .endAt(mEmail.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "mail Children count"+ dataSnapshot.getChildrenCount());
                            Log.d(TAG, "mail key"+ dataSnapshot.getKey());
                            if(dataSnapshot.getChildrenCount()>0){
                                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametcantbenull));
                                // Show DialogFragment
                                mEmail.setError(getString(R.string.usuario_emailyaExisite));
                                dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
                            Log.d(TAG, "mail Children count"+ dataSnapshot.getChildrenCount());
                            Log.d(TAG, "mail key"+ dataSnapshot.getKey());
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                                String category = (String) messageSnapshot.child("category").getValue();
                                Log.d(TAG, "mail key snapshot"+ messageSnapshot.getKey());
                                Usuario u = messageSnapshot.getValue(Usuario.class);
//                                Log.d(TAG, "mail user:"+ u.getEmail()+" - "+u.getPerfil().getClientes());
                            }

                            return ;
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "mail Cancelled "+ databaseError.toString());

                        }
                    });

            String keyUsuario = referenceEmpresaUser.child(mEmpresaKey).push().getKey();
            String photoUrl =null;

            Perfil perfil = new Perfil(keyUsuario,mPerfil_usuarios.isChecked(), mPerfil_productos.isChecked(),
                    mPerfil_clientes.isChecked(), mPerfil_reportes.isChecked(),mPerfil_ordenes.isChecked(),
                    mPerfil_preparar.isChecked(), mPerfil_entregar.isChecked(), mPerfil_pagos.isChecked(), mPerfil_stock.isChecked());

            Usuario user =new Usuario("new",mEmail.getText().toString(),photoUrl,"inicial",false) ;

            UsuarioPerfil usuarioPerfil =new UsuarioPerfil (mUserId, user,perfil);
            Map<String, Object> usuarioPerfilValues = usuarioPerfil.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/empresa/" + key, empresaValues);
            childUpdates.put(Constantes.NODO_EMPRESA_USERS + mEmpresaKey+ "/" + keyUsuario , usuarioPerfilValues);
//            childUpdates.put(Constantes.NODO_USER_PROPUETO_EMPRESA + mEmpresaKey+ "/" + keyUsuario , usuarioPerfilValues);
            mDatabase.updateChildren(childUpdates);

        }
    }


    // Bloqueamos para que no se modifique mientras se graba.
    private void setEditingEnabled(boolean enabled) {
        mEmail.setEnabled(enabled);
        mEmailConfirmation.setEnabled(enabled);

        mUserSwitch.setEnabled(enabled);
        mPerfil_usuarios.setEnabled(enabled);
        mPerfil_productos.setEnabled(enabled);
        mPerfil_clientes.setEnabled(enabled);
        mPerfil_reportes.setEnabled(enabled);
        mPerfil_ordenes.setEnabled(enabled);
        mPerfil_preparar.setEnabled(enabled);
        mPerfil_entregar.setEnabled(enabled);
        mPerfil_pagos.setEnabled(enabled);
        mPerfil_stock.setEnabled(enabled);

//                if (enabled) {
//            mfab.setVisibility(View.VISIBLE);
//        } else {
//            mfab.setVisibility(View.GONE);
//        }
    }



    private boolean validateForm() {
        boolean result = true;
// Valida el mEmail
        if (TextUtils.isEmpty(mEmail.getText().toString())) {
            mEmail.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmail.setError(null);
        }
// Valida el mEmailConfirmation
        if (TextUtils.isEmpty(mEmailConfirmation.getText().toString())) {
            mEmailConfirmation.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmailConfirmation.setError(null);
        }
// Valida el mEmailConfirmation == mEmail
        if (!mEmail.getText().toString().equals(mEmailConfirmation.getText().toString())) {
            mEmail.setError(getResources().getString(R.string.usuario_emailConfirmacionError));
            mEmailConfirmation .setError(getResources().getString(R.string.usuario_emailConfirmacionError));
            result = false;
        } else {
            mEmail.setError(null);
            mEmailConfirmation.setError(null);
        }
        return result;
    }
}
