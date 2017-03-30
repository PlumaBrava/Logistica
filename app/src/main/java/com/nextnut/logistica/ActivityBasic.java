package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.modelos.Usuario;

import static android.content.Intent.EXTRA_USER;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_FIREBASE_URL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_USER_KEY;
import static com.nextnut.logistica.util.UtilFirebase.getDatabase;

/**
 * Created by perez.juan.jose on 14/11/2016.
 *
 *
 *  Esta actividad sirve de base para todas las actividades que se comuniquen con Firebase
 *
 *  Establece conexion con el servidor y lee las variables
 *
 *          mDatabase: Referencia al Nodo Principal de Firebase
 *          mUserKey: Usuario que opera la aplicacion
 *          mPerfilKey: Perfil del mUserKey, quien opera la aplicacion
 *          mEmpresaKey: Clave de Empresa
 *
 *
 *
 *
 *
 */

public class ActivityBasic extends AppCompatActivity {

    public DatabaseReference mDatabase;
    public String mFirebaseUrl;
    public String mUserKey;
    public Usuario mUsuario;
    public String mEmpresaKey;
    public Empresa mEmpresa;
    public Perfil mPerfil;
    public String mClienteKey;
    public String mProductKey;
    public Cliente mCliente;
    public Producto mProducto;
    public static final String LOG_TAG = "ActivityBasic";

    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            mFirebaseUrl = getIntent().getStringExtra(EXTRA_FIREBASE_URL);
            mUserKey = getIntent().getStringExtra(EXTRA_USER_KEY);
            mUsuario = (Usuario) getIntent().getParcelableExtra(EXTRA_USER);
            mEmpresaKey = getIntent().getStringExtra(EXTRA_EMPRESA_KEY);
            mEmpresa = (Empresa) getIntent().getParcelableExtra(EXTRA_EMPRESA);
            mPerfil = (Perfil) getIntent().getParcelableExtra(EXTRA_PERFIL);
            mClienteKey =  getIntent().getStringExtra(EXTRA_CLIENTE_KEY);
            mProductKey =  getIntent().getStringExtra(EXTRA_PRODUCT_KEY);
            mProducto =  getIntent().getParcelableExtra(EXTRA_PRODUCT);
            mCliente =  getIntent().getParcelableExtra(EXTRA_CLIENTE);

//            Log.d(LOG_TAG, "savedInstanceState == nullmFirebaseUrl:" + mFirebaseUrl);
//            Log.d(LOG_TAG, "savedInstanceState == null-mUserKey:" + mUserKey);
//            Log.d(LOG_TAG, "savedInstanceState == null-mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
//            Log.d(LOG_TAG, "savedInstanceState == null-mEmpresaKey:" + mEmpresaKey);
//            Log.d(LOG_TAG, "savedInstanceState == null-mEmpresa:" + mEmpresa.getNombre());
//            Log.d(LOG_TAG, "savedInstanceState == null-Perfil:" + mPerfil.getClientes());
//            Log.d(LOG_TAG, "savedInstanceState == null-mClienteKey:" + mClienteKey);
//            Log.d(LOG_TAG, "savedInstanceState == null-mProductKeyl:" + mProductKey);

        } else {
            leerVariablesGlobales(savedInstanceState);
        }
        // [START create_database_reference]
//        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(mFirebaseUrl) ;
        mDatabase =  getDatabase().getReference();
    }
        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            // Save the user's current state


//            Log.d(LOG_TAG, "onSaveInstanceState-mFirebaseUrl:" + mFirebaseUrl);
//
//            Log.d(LOG_TAG, "onSaveInstanceState-onAuthStateChanged:mUserKey:" + mUserKey);
//            Log.d(LOG_TAG, "onSaveInstanceState-onAuthStateChanged:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
//
//
//            Log.d(LOG_TAG, "onSaveInstanceState-mEmpresaKey:" + mEmpresaKey);
//            Log.d(LOG_TAG, "onSaveInstanceState-mEmpresa:" + mEmpresa.getNombre());
//
//            Log.d(LOG_TAG, "onSaveInstanceState-Perfil:" + mPerfil.getClientes());
//            Log.d(LOG_TAG, "onSaveInstanceState-mClienteKey:" + mClienteKey);
//            Log.d(LOG_TAG, "onSaveInstanceState-mProductKey:" +mProductKey);


            savedInstanceState.putString(EXTRA_FIREBASE_URL, mFirebaseUrl);
            savedInstanceState.putString(EXTRA_USER_KEY, mUserKey);
            savedInstanceState.putParcelable(EXTRA_USER, mUsuario);
            savedInstanceState.putString(EXTRA_EMPRESA_KEY, mEmpresaKey);
            savedInstanceState.putParcelable(EXTRA_EMPRESA, mEmpresa);
            savedInstanceState.putParcelable(EXTRA_PERFIL, mPerfil);
            savedInstanceState.putString(EXTRA_CLIENTE_KEY,mClienteKey);
            savedInstanceState.putString(EXTRA_PRODUCT_KEY,mProductKey);
            savedInstanceState.putParcelable(EXTRA_PRODUCT,mProducto );
            savedInstanceState.putParcelable(EXTRA_CLIENTE, mCliente);

            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
        }

        public void onRestoreInstanceState(Bundle savedInstanceState) {
            // Always call the superclass so it can restore the view hierarchy
            super.onRestoreInstanceState(savedInstanceState);

            // Restore state members from saved instance
            mFirebaseUrl=savedInstanceState.getString(EXTRA_FIREBASE_URL);
            mUserKey= savedInstanceState.getString(EXTRA_USER_KEY);
            mUsuario= savedInstanceState.getParcelable(EXTRA_USER);
            mEmpresaKey = savedInstanceState.getString(EXTRA_EMPRESA_KEY);
            mEmpresa= savedInstanceState.getParcelable(EXTRA_EMPRESA);
            mPerfil= savedInstanceState.getParcelable(EXTRA_PERFIL);
            mClienteKey = savedInstanceState.getString(EXTRA_CLIENTE_KEY);
            mProductKey = savedInstanceState.getString(EXTRA_PRODUCT_KEY);
            mProducto =  savedInstanceState.getParcelable(EXTRA_PRODUCT);
            mCliente =  savedInstanceState.getParcelable(EXTRA_CLIENTE);

            // Always call the superclass so it can restore the view hierarchy
            super.onRestoreInstanceState(savedInstanceState);

            // Restore state members from saved instance

            Log.d(LOG_TAG, "onRestoreInstanceState-mFirebaseUrl:" + mFirebaseUrl);
            Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUserKey:" + mUserKey);
            Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
            Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresaKey:" + mEmpresaKey);
            Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresa:" + mEmpresa.getNombre());
            Log.d(LOG_TAG, "onRestoreInstanceState-Perfil:" + mPerfil.getClientes());
        }

        public void leerVariablesGlobales(Bundle savedInstanceState ){  // lee las variables que se enviaron a una actividad o se grabaron al destruirla
            mFirebaseUrl=savedInstanceState.getString(EXTRA_FIREBASE_URL);
            mUserKey= savedInstanceState.getString(EXTRA_USER_KEY);
            mUsuario= savedInstanceState.getParcelable(EXTRA_USER);
            mEmpresaKey = savedInstanceState.getString(EXTRA_EMPRESA_KEY);
            mEmpresa= savedInstanceState.getParcelable(EXTRA_EMPRESA);
            mPerfil= savedInstanceState.getParcelable(EXTRA_PERFIL);
            mClienteKey = savedInstanceState.getString(EXTRA_CLIENTE_KEY);
            mProductKey = savedInstanceState.getString(EXTRA_PRODUCT_KEY);
            mProducto =  savedInstanceState.getParcelable(EXTRA_PRODUCT);
            mCliente =  savedInstanceState.getParcelable(EXTRA_CLIENTE);

            // Always call the superclass so it can restore the view hierarchy

            // Restore state members from saved instance

            Log.d(LOG_TAG, "leerVariablesGlobales-mFirebaseUrl:" + mFirebaseUrl);
            Log.d(LOG_TAG, "leerVariablesGlobales-:mUserKey:" + mUserKey);
            Log.d(LOG_TAG, "leerVariablesGlobales-:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
            Log.d(LOG_TAG, "leerVariablesGlobales-mEmpresaKey:" + mEmpresaKey);
            Log.d(LOG_TAG, "leerVariablesGlobales-mEmpresa,nombre:" + mEmpresa.getNombre());
            Log.d(LOG_TAG, "leerVariablesGlobales-Perfil,Cliente:" + mPerfil.getClientes());
        }

public void putExtraFirebase(Intent intent){   // para pasar información a una actividad
    intent.putExtra(EXTRA_FIREBASE_URL, mDatabase.getRef().toString());
    intent.putExtra(EXTRA_USER_KEY, mUserKey);
    intent.putExtra(EXTRA_USER, mUsuario);
    intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
    intent.putExtra(EXTRA_EMPRESA, mEmpresa);
    intent.putExtra(EXTRA_PERFIL, mPerfil);
    intent.putExtra(EXTRA_CLIENTE_KEY, mClienteKey);
    intent.putExtra(EXTRA_PRODUCT_KEY, mProductKey);
    intent.putExtra(EXTRA_PRODUCT, mProducto);
    intent.putExtra(EXTRA_CLIENTE, mCliente);
//    Log.d(LOG_TAG, "putExtraFirebase-mFirebaseUrl:" + mFirebaseUrl);
//    Log.d(LOG_TAG, "putExtraFirebase-:mUserKey:" + mUserKey);
//    Log.d(LOG_TAG, "putExtraFirebase-:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
//    Log.d(LOG_TAG, "putExtraFirebase-mEmpresaKey:" + mEmpresaKey);
//    Log.d(LOG_TAG, "putExtraFirebase-mEmpresa,nombre:" + mEmpresa.getNombre());
//    Log.d(LOG_TAG, "putExtraFirebase-Perfil,Cliente:" + mPerfil.getClientes());
}

public Bundle putBundleFirebase()  // Se usa para enviar información desde una Actividad a un fragment

    {
        Bundle arguments = new Bundle();

        arguments.putString(EXTRA_FIREBASE_URL, mDatabase.getRef().toString());
        arguments.putString(EXTRA_USER_KEY, mUserKey);
        arguments.putParcelable(EXTRA_USER, mUsuario);
        arguments.putString(EXTRA_EMPRESA_KEY, mEmpresaKey);
        arguments.putParcelable(EXTRA_EMPRESA, mEmpresa);
        arguments.putParcelable(EXTRA_PERFIL, mPerfil);
        arguments.putString(EXTRA_CLIENTE_KEY, mClienteKey);
        arguments.putString(EXTRA_PRODUCT_KEY, mProductKey);
        arguments.putParcelable(EXTRA_PRODUCT, mProducto);
        arguments.putParcelable(EXTRA_CLIENTE, mCliente);
        Log.d(LOG_TAG, "putBundleFirebase-mFirebaseUrl:" + mFirebaseUrl);
        Log.d(LOG_TAG, "putBundleFirebase-:mUserKey:" + mUserKey);
        Log.d(LOG_TAG, "putBundleFirebase-:mUsuario:" + mUsuario.getUsername()+" - "+mUsuario.getEmail());
        Log.d(LOG_TAG, "putBundleFirebase-mEmpresaKey:" + mEmpresaKey);
        Log.d(LOG_TAG, "putBundleFirebase-mEmpresa,nombre:" + mEmpresa.getNombre());
        Log.d(LOG_TAG, "putBundleFirebase-Perfil,Cliente:" + mPerfil.getClientes());
        return arguments;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
