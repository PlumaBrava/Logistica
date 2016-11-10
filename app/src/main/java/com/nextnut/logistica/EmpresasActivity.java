package com.nextnut.logistica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.Imagenes;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA;
import static com.nextnut.logistica.util.Constantes.NODO_USER_EMPRESA;
import static com.nextnut.logistica.util.Constantes.NODO_USER_PROPUETO_EMPRESA;
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.Imagenes.selectImage;

public class EmpresasActivity extends AppCompatActivity {
    private static final String TAG = " EmpresasActivity";
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private EditText mEmpresaNombre;
    private EditText mEmpresaCuit;
    private EditText mEmpresaCiudad;
    private EditText mEmpresaDireccion;
    private EditText mEmpresaCodigoPostal;
    private EditText mEmpresaTelefono;
    private ImageView mEmpresaLogo;
    private String mEmpresaLogoURL;
    private FirebaseUser mUser;
    private String mEmpresaKey;
    FloatingActionButton mfab;
    public ProgressView spinner;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empesas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl(Constantes.STORAGE_REFERENCE);


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // [END initialize_database_ref]

        mEmpresaNombre = (EditText) findViewById(R.id.empresa_nombre);
        mEmpresaCuit = (EditText) findViewById(R.id.empresa_cuit);
        mEmpresaCiudad = (EditText) findViewById(R.id.empresa_ciudad);
        mEmpresaDireccion = (EditText) findViewById(R.id.empresa_Direccion);
        mEmpresaCodigoPostal = (EditText) findViewById(R.id.empresa_CodigoPostal);
        mEmpresaTelefono = (EditText) findViewById(R.id.empresa_telefono);
        mEmpresaLogo = (ImageView) findViewById(R.id.empresa_Logo);
        mEmpresaLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(EmpresasActivity.this);
            }
        });

        Drawable drawable = dimensiona(getApplicationContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(getApplicationContext())
                .load(drawable.toString())
                .resize(200, 200)
                .placeholder(drawable)
                .centerCrop()
                .into(mEmpresaLogo);


        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditingEnabled(false);

                writeNewEmpresa(mUser, mEmpresaNombre.getText().toString(), mEmpresaCuit.getText().toString(), mEmpresaCiudad.getText().toString(), mEmpresaDireccion.getText().toString(), mEmpresaCodigoPostal.getText().toString(), mEmpresaTelefono.getText().toString(), mEmpresaLogoURL);

//                // Get the data from an ImageView as bytes
//                mEmpresaLogo.setDrawingCacheEnabled(true);
//                mEmpresaLogo.buildDrawingCache();
//                Bitmap bitmap = mEmpresaLogo.getDrawingCache();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] data = baos.toByteArray();
//
//                // Create a storage reference from our app
//                StorageReference storageRef = mStorage.getReferenceFromUrl("gs://logistica-144918.appspot.com");
//
//// Create a reference to "mountains.jpg"
//                StorageReference mountainsRef = storageRef.child("mountains.jpg");
//
//// Create a reference to 'images/mountains.jpg'
//                StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
//
//
//                UploadTask uploadTask = mountainsRef.putBytes(data);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Log.i("foto","OnFalilure: "+ exception);
//                        // Handle unsuccessful uploads
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Log.i("foto","onSuccess: "+ taskSnapshot.toString());
//
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                    }
//                });
//
//
//
//

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        spinner = (ProgressView) findViewById(R.id.progressBarEmpresa);
        spinner.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    // [START basic_write]
    private void writeNewEmpresa(FirebaseUser user, String nombre, String cuit, String ciudad, String direccion, String codigoPostal, String telegono, String logo) {
        if (validateForm()) {
            if (mEmpresaKey == null) {
                mEmpresaKey = mDatabase.child(ESQUEMA_EMPRESA).push().getKey();
            }
            Log.d(TAG, "mEmpresaKey:" + mEmpresaKey);
            Log.d(TAG, "user.getEmail():" + user.getEmail());
            Log.d(TAG, "user.getUid():" + user.getUid());

            Empresa empresa = new Empresa(user.getUid(), nombre, cuit, ciudad, direccion, codigoPostal, telegono, logo);
            Map<String, Object> empresaValues = empresa.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(NODO_EMPRESA + mEmpresaKey, empresaValues);
            childUpdates.put(NODO_USER_EMPRESA + user.getUid() + "/" + mEmpresaKey, empresaValues);
            childUpdates.put(NODO_USER_PROPUETO_EMPRESA+ getKeyFromEmail(user.getEmail() )+ "/" + mEmpresaKey, empresaValues);
            mDatabase.updateChildren(childUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "task.isSuccessful(): " + task.isSuccessful());
                            if (task.isSuccessful()) {

                            } else {
                                Log.d(TAG, "task.error: " + task.getException().getMessage().toString());

                            }

                        }
                    });


        }
        setEditingEnabled(true);
    }

    private String getKeyFromEmail(String email) {
        if ( email !=null){
            String a =email.toLowerCase()
                    .replace('.','-')
                    .replace('#','N')
                    .replace('[','P')
                    .replace(']','p')
                    .replace('/','B');
            return a;
        }
        return email;
    }

    private void setEditingEnabled(boolean enabled) {
        mEmpresaNombre.setEnabled(enabled);
        mEmpresaCuit.setEnabled(enabled);
        mEmpresaCiudad.setEnabled(enabled);
        mEmpresaDireccion.setEnabled(enabled);
        mEmpresaDireccion.setEnabled(enabled);
        if (enabled) {
            mfab.setVisibility(View.VISIBLE);
        } else {
            mfab.setVisibility(View.GONE);
        }
    }


    private boolean validateForm() {
        boolean result = true;
// Valida el Nombre
        if (TextUtils.isEmpty(mEmpresaNombre.getText().toString())) {
            mEmpresaNombre.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmpresaNombre.setError(null);
        }
// Valida el Cuit
        if (TextUtils.isEmpty(mEmpresaCuit.getText().toString())) {
            mEmpresaCuit.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmpresaCuit.setError(null);
        }
// Valida el Ciudad
        if (TextUtils.isEmpty(mEmpresaCiudad.getText().toString())) {
            mEmpresaCiudad.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmpresaCiudad.setError(null);
        }
//Valida Direccion
        if (TextUtils.isEmpty(mEmpresaDireccion.getText().toString())) {
            mEmpresaDireccion.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmpresaDireccion.setError(null);
        }


        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // cuando sacamos una foto.
                bitmap = (Bitmap) data.getExtras().get(getString(R.string.data));

            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {// cuando leemos un archivo foto.

//                mEmpresaLogoURL = getString(R.string.file) + saveImageSelectedReturnPath(getApplication(), data);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (bitmap != null) {
                // Create a storage reference from our app
                if (mEmpresaKey == null) {
                    mEmpresaKey = mDatabase.child("empresa").push().getKey();
                }
                // Crear una referencia a la foto. (directorio Imagenes/mProductoKey
                StorageReference ImagenRef = mStorageRef.child("images/" + mEmpresaKey);
                Log.i("subirFotoReturnUri", "onFailure: -spinner ON 11" + mEmpresaKey);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datosFoto = baos.toByteArray();
                spinner.setVisibility(View.VISIBLE);
                mEmpresaLogo.setVisibility(View.GONE);
                UploadTask uploadTask = ImagenRef.putBytes(datosFoto);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        spinner.setVisibility(View.GONE);
                                                        mEmpresaLogo.setVisibility(View.VISIBLE);
                                                        Log.i("subirFotoReturnUri", "onFailure: -spinner off " + exception.toString());

                                                    }
                                                }

                ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                               // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                               Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                               Log.i("subirFotoReturnUri", "onSuccess: Spinner Off");
                                               Drawable drawable = dimensiona(getApplicationContext(), R.drawable.ic_action_action_redeem);
//                                               mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getContext(), (Bitmap) data.getExtras().get(getString(R.string.data)));
                                               mEmpresaLogoURL = downloadUrl.toString();

                                               Picasso.with(getApplication())
                                                       .load(mEmpresaLogoURL)
//                                                       .placeholder(drawable)
                                                       .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                                       .into(mEmpresaLogo);
                                               mEmpresaLogo.setVisibility(View.VISIBLE);
                                               spinner.setVisibility(View.GONE);
                                           }
                                       }

                ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        spinner.setProgress((float) progress);
                        Log.i("subirFotoReturnUri", "spinner progress" + progress);

                        System.out.println("Upload is " + progress + "% done");
                    }
                });
            }
        }

        ///////////////////

//        Drawable drawable = dimensiona(getApplicationContext(), R.drawable.ic_action_action_redeem);
//        if (resultCode == Activity.RESULT_OK) {
//
//            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//               String mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getBaseContext(),(Bitmap) data.getExtras().get(getString(R.string.data)));
//                Picasso.with(getApplicationContext())
//                        .load(mCurrentPhotoPath)
//                        .placeholder(drawable)
//                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                        .into(mEmpresaLogo);
//
//            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {
//
//
//                String mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getBaseContext(),data);
//
//
//                mEmpresaLogo.setBackgroundColor(Color.TRANSPARENT);
//                Picasso.with(getApplication())
//                        .load(mCurrentPhotoPath)
//                        .placeholder(drawable)
//                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                        .into(mEmpresaLogo);
//
//            }
//
//        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Empresas Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}