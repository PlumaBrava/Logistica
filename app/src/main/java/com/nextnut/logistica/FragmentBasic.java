package com.nextnut.logistica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.modelos.Usuario;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.Imagenes;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.UtilFirebase.getDatabase;

/**
 * Created by perez.juan.jose on 17/11/2016.
 */

public abstract class FragmentBasic extends Fragment {

    public DatabaseReference mDatabase;
    public FirebaseStorage mStorage;
    public StorageReference mStorageRef;

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

    public static final String LOG_TAG = "FragmentBasic";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate-savedInstanceState:" + savedInstanceState);
        mFirebaseUrl = getArguments().getString(EXTRA_FIREBASE_URL);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl(Constantes.STORAGE_REFERENCE);

        mUserKey = getArguments().getString(EXTRA_USER_KEY);
        mUsuario = getArguments().getParcelable(EXTRA_USER);
        mEmpresaKey = getArguments().getString(EXTRA_EMPRESA_KEY);
        mEmpresa = getArguments().getParcelable(EXTRA_EMPRESA);
        mPerfil = getArguments().getParcelable(EXTRA_PERFIL);
        mClienteKey = getArguments().getString(EXTRA_CLIENTE_KEY);
        mProductKey = getArguments().getString(EXTRA_PRODUCT_KEY);
        mCliente = getArguments().getParcelable(EXTRA_CLIENTE);
        mProducto = getArguments().getParcelable(EXTRA_PRODUCT);

        Log.d(LOG_TAG, "onCreate-savedInstanceState:" + savedInstanceState);
        Log.d(LOG_TAG, "onCreate-mFirebaseUrl:" + mFirebaseUrl);
        Log.d(LOG_TAG, "onCreate-onAuthStateChanged:mUserKey:" + mUserKey);
        Log.d(LOG_TAG, "onCreate-onAuthStateChanged:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
        Log.d(LOG_TAG, "onCreate-mEmpresaKey:" + mEmpresaKey);
        Log.d(LOG_TAG, "onCreate-mEmpresa:" + mEmpresa.getNombre());
        Log.d(LOG_TAG, "onCreate-Perfil:" + mPerfil.getClientes());
        Log.d(LOG_TAG, "onCreate-mClienteKey:" + mClienteKey);
        Log.d(LOG_TAG, "onCreate-mProductKeyl:" + mProductKey);

        mDatabase = getDatabase().getReference();


    }


    public void putExtraFirebase_Fragment(Intent intent) {   // para pasar información a una actividad
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
        Log.d(LOG_TAG, "putExtraFirebase-mFirebaseUrl:" + mFirebaseUrl);
        Log.d(LOG_TAG, "putExtraFirebase-:mUserKey:" + mUserKey);
        Log.d(LOG_TAG, "putExtraFirebase-:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
        Log.d(LOG_TAG, "putExtraFirebase-mEmpresaKey:" + mEmpresaKey);
        Log.d(LOG_TAG, "putExtraFirebase-mEmpresa,nombre:" + mEmpresa.getNombre());
        Log.d(LOG_TAG, "putExtraFirebase-Perfil,Cliente:" + mPerfil.getClientes());
        Log.d(LOG_TAG, "putExtraFirebase-mClienteKey:" + mClienteKey);
        Log.d(LOG_TAG, "putExtraFirebase-mProductKey" + mProductKey);
//        Log.d(LOG_TAG, "putExtraFirebase-mProducto:" + mProducto.getNombreProducto());
//        Log.d(LOG_TAG, "putExtraFirebase-mCliente,nombre:" + mCliente.getNombre());
    }

    public Bundle putBundleFirebase_Fragment()  // Se usa para enviar información desde una Actividad a un fragment

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
        Log.d(LOG_TAG, "putBundleFirebase-:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
        Log.d(LOG_TAG, "putBundleFirebase-mEmpresaKey:" + mEmpresaKey);
        Log.d(LOG_TAG, "putBundleFirebase-mEmpresa,nombre:" + mEmpresa.getNombre());
        Log.d(LOG_TAG, "putBundleFirebase-Perfil,Cliente:" + mPerfil.getClientes());
        return arguments;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "subirFotoReturnUri,requestCode,:" + requestCode);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // cuando sacamos una foto.
                bitmap = (Bitmap) data.getExtras().get(getString(R.string.data));

            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {// cuando leemos un archivo foto.

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            savePhoto(bitmap);
        }
    }

    public abstract void  savePhoto(Bitmap bitmap);
   public String mCurrentPhotoPath;
    public void uploadImagen(Bitmap bitmap, StorageReference ImagenRef, final ImageView imageView, final ProgressView spinner) {

        if (bitmap != null) {
            // Create a storage reference from our app


            Log.i("subirFotoReturnUri", "onFailure: ImagenRef" + ImagenRef.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datosFoto = baos.toByteArray();
            if (spinner!=null){
                spinner.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
            UploadTask uploadTask = ImagenRef.putBytes(datosFoto);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    if (spinner!=null){
                                                    spinner.setVisibility(View.GONE);
                                                    imageView.setVisibility(View.VISIBLE);
                                                    Log.i("subirFotoReturnUri", "onFailure: -spinner off " + exception.toString());

                                                }}
                                            }

            ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                           Uri downloadUrl = taskSnapshot.getDownloadUrl();


                                           Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_action_redeem);
                                           mCurrentPhotoPath = downloadUrl.toString();
                                           Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath-listener"+mCurrentPhotoPath);
                                           Picasso.with(getContext())
                                                   .load(mCurrentPhotoPath)
//                                                       .placeholder(drawable)
                                                   .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                                   .into(imageView);
                                           if (spinner!=null) {
                                               spinner.setVisibility(View.GONE);
                                               imageView.setVisibility(View.VISIBLE);
                                           }

                                       }
                                   }

            ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (spinner!=null){
                    spinner.setProgress((float) progress);
                    }
                    Log.i("subirFotoReturnUri", "spinner progress" + progress);

                }
            });
        }
        Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath-return"+mCurrentPhotoPath);


    }
}



