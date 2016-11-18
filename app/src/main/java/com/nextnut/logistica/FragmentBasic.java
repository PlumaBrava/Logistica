package com.nextnut.logistica;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Usuario;

import static android.content.Intent.EXTRA_USER;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_FIREBASE_URL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_USER_KEY;
import static com.nextnut.logistica.util.UtilFirebase.getDatabase;

/**
 * Created by perez.juan.jose on 17/11/2016.
 */

public class FragmentBasic  extends Fragment {

    public DatabaseReference mDatabase;

    public String mFirebaseUrl;
    public String mUserKey;
    public Usuario mUsuario;
    public String mEmpresaKey;
    public Empresa mEmpresa;
    public Perfil mPerfil;
    public String mClienteKey;
    public String mProductKey;
    public static final String LOG_TAG = "FragmentBasic";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate-savedInstanceState:" + savedInstanceState);
        mFirebaseUrl=getArguments().getString(EXTRA_FIREBASE_URL);
        mUserKey= getArguments().getString(EXTRA_USER_KEY);
        mUsuario= getArguments().getParcelable(EXTRA_USER);
        mEmpresaKey = getArguments().getString(EXTRA_EMPRESA_KEY);
        mEmpresa= getArguments().getParcelable(EXTRA_EMPRESA);
        mPerfil= getArguments().getParcelable(EXTRA_PERFIL);
        mClienteKey = getArguments().getString(EXTRA_CLIENTE_KEY);
        mProductKey = getArguments().getString(EXTRA_PRODUCT_KEY);

        Log.d(LOG_TAG, "onCreate-savedInstanceState:" + savedInstanceState);
        Log.d(LOG_TAG, "onCreate-mFirebaseUrl:" + mFirebaseUrl);
        Log.d(LOG_TAG, "onCreate-onAuthStateChanged:mUserKey:" + mUserKey);
        Log.d(LOG_TAG, "onCreate-onAuthStateChanged:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
        Log.d(LOG_TAG, "onCreate-mEmpresaKey:" + mEmpresaKey);
        Log.d(LOG_TAG, "onCreate-mEmpresa:" + mEmpresa.getNombre());
        Log.d(LOG_TAG, "onCreate-Perfil:" + mPerfil.getClientes());
        Log.d(LOG_TAG, "onCreate-mClienteKey:" + mClienteKey);
        Log.d(LOG_TAG, "onCreate-mProductKeyl:" + mProductKey);

        mDatabase =  getDatabase().getReference(); ;

    }
}
