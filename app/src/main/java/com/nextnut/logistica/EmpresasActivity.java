package com.nextnut.logistica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.util.Imagenes;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Imagenes.resize;
import static com.nextnut.logistica.util.Imagenes.saveImageSelectedReturnPath;
import static com.nextnut.logistica.util.Imagenes.savePhotoReturnPath;
import static com.nextnut.logistica.util.Imagenes.selectImage;

public class EmpresasActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;

    private EditText mEmpresaNombre;
    private EditText mEmpresaCuit;
    private EditText mEmpresaCiudad;
    private EditText mEmpresaDireccion;
    private EditText mEmpresaCodigoPostal;
    private EditText mEmpresaTelefono;
    private ImageView mEmpresaLogo;
    private String mUserId;
    FloatingActionButton mfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empesas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
//        mStorage.
//                gs://logistica-144918.appspot.com/joaco.jpg
        mUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        // [END initialize_database_ref]

        mEmpresaNombre= (EditText)findViewById(R.id.empresa_nombre);
        mEmpresaCuit = (EditText)findViewById(R.id.empresa_cuit);
        mEmpresaCiudad= (EditText)findViewById(R.id.empresa_ciudad);
        mEmpresaDireccion= (EditText)findViewById(R.id.empresa_Direccion);
        mEmpresaCodigoPostal= (EditText)findViewById(R.id.empresa_CodigoPostal);
        mEmpresaTelefono= (EditText)findViewById(R.id.empresa_telefono);
        mEmpresaLogo= (ImageView) findViewById(R.id.empresa_Logo);
        mEmpresaLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(EmpresasActivity.this);
            }
        });

        Drawable drawable = resize(getApplicationContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(getApplicationContext())
                .load(drawable.toString())
                .resize(200,200)
                .placeholder(drawable)
                .centerCrop()
                .into(mEmpresaLogo);


        mfab = (FloatingActionButton) findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditingEnabled(false);
                // Get the data from an ImageView as bytes
                mEmpresaLogo.setDrawingCacheEnabled(true);
                mEmpresaLogo.buildDrawingCache();
                Bitmap bitmap = mEmpresaLogo.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Create a storage reference from our app
                StorageReference storageRef = mStorage.getReferenceFromUrl("gs://logistica-144918.appspot.com");

// Create a reference to "mountains.jpg"
                StorageReference mountainsRef = storageRef.child("mountains.jpg");

// Create a reference to 'images/mountains.jpg'
                StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");


                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("foto","OnFalilure: "+ exception);
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("foto","onSuccess: "+ taskSnapshot.toString());

                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        writeNewEmpresa(mUserId,mEmpresaNombre.getText().toString(), mEmpresaCuit.getText().toString(),mEmpresaCiudad.getText().toString(),mEmpresaDireccion.getText().toString(),mEmpresaCodigoPostal.getText().toString(),mEmpresaTelefono.getText().toString(),downloadUrl.toString());

                    }
                });





                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }



    // [START basic_write]
    private void writeNewEmpresa(String userId,String nombre , String cuit, String ciudad, String direccion, String codigoPostal, String telegono, String logo) {
        if (validateForm()) {
            String key = mDatabase.child("empresa").push().getKey();
            Empresa empresa = new Empresa(userId, nombre, cuit, ciudad, direccion, codigoPostal, telegono, logo);
            Map<String, Object> empresaValues = empresa.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/empresa/" + key, empresaValues);
            childUpdates.put("/user-empresa/" + userId + "/" + key, empresaValues);
            mDatabase.updateChildren(childUpdates);

        }
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
        Drawable drawable = resize(getApplicationContext(), R.drawable.ic_action_action_redeem);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
               String mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getBaseContext(),(Bitmap) data.getExtras().get(getString(R.string.data)));
                Picasso.with(getApplicationContext())
                        .load(mCurrentPhotoPath)
                        .placeholder(drawable)
                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .into(mEmpresaLogo);

            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {


                String mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getBaseContext(),data);


                mEmpresaLogo.setBackgroundColor(Color.TRANSPARENT);
                Picasso.with(getApplication())
                        .load(mCurrentPhotoPath)
                        .placeholder(drawable)
                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .into(mEmpresaLogo);

            }

        }
    }

}
