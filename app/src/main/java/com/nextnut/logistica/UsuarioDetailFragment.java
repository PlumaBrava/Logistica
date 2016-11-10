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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.User;
import com.nextnut.logistica.util.DialogAlerta;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Switch;

import java.util.HashMap;
import java.util.Map;

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

    public String getmEmail() {
        return mEmail.getText().toString();
    }

    // [START basic_write]
   public void writeNewUser() {
        if (validateForm()) {


            String empresaKey ="-KV63YXYpmtO_bRnVfQk";
            DatabaseReference referenceEmpresa_NewUser =mDatabase.child("empresa-NewUser");

            // Verifica que si existe ese usuario en NewUser
            Query myNewUsers= referenceEmpresa_NewUser.child(empresaKey).orderByChild("email");
            myNewUsers
                    .startAt(mEmail.getText().toString())
                    .endAt(mEmail.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()<=0){
                                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametcantbenull));
                                // Show DialogFragment
                                mEmail.setError(getString(R.string.usuario_emailyaExisite));
                                dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
                            Log.d(TAG, "mail Children count"+ dataSnapshot.getChildrenCount());
                            Log.d(TAG, "mail key"+ dataSnapshot.getKey());
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                                String category = (String) messageSnapshot.child("category").getValue();
                                Log.d(TAG, "mail key snapshot"+ messageSnapshot.getKey());
                                User u = messageSnapshot.getValue(User.class);
                                Log.d(TAG, "mail user:"+ u.getEmail()+" - "+u.getPerfil().getClientes());
                            }

                            return ;
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "mail Cancelled "+ databaseError.toString());

                        }
                    })



            ;

            String keyUsuario = referenceEmpresa_NewUser.child(empresaKey).push().getKey();
            String photoUrl =null;

            Perfil perfil = new Perfil(mPerfil_usuarios.isChecked(), mPerfil_productos.isChecked(),
                    mPerfil_clientes.isChecked(), mPerfil_reportes.isChecked(),mPerfil_ordenes.isChecked(),
                    mPerfil_preparar.isChecked(), mPerfil_entregar.isChecked(), mPerfil_pagos.isChecked(), mPerfil_stock.isChecked());

            User user =new User("new",mEmail.getText().toString(),photoUrl,"inicial",false,perfil) ;

            Map<String, Object> userValues = user.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/empresa/" + key, empresaValues);
            childUpdates.put("/empresa-NewUser/" + empresaKey+ "/" + keyUsuario , userValues);
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
