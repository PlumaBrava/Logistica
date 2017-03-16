package com.nextnut.logistica;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.CabeceraPicking;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.PrductosxOrden;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.modelos.Usuario;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.Imagenes;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.EXTRA_USER;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_FAVORITOS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_DETALLE;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_TOTAL_INICIAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PAGOS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PICKING;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PICKING_TOTAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_REPORTE_VENTAS_CLIENTE;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_REPORTE_VENTAS_PRODUCTO;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_SALDOS_HISTORIAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_SALDO_TOTAL;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_FIREBASE_URL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_USER_KEY;
import static com.nextnut.logistica.util.Constantes.NODO_FAVORITOS;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_DETALLE;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_TOTAL_INICIAL;
import static com.nextnut.logistica.util.Constantes.NODO_PAGOS;
import static com.nextnut.logistica.util.Constantes.NODO_PICKING;
import static com.nextnut.logistica.util.Constantes.NODO_PICKING_TOTAL;
import static com.nextnut.logistica.util.Constantes.NODO_PRODUCTOS_EN_ORDENES_INICIAL;
import static com.nextnut.logistica.util.Constantes.NODO_REPORTE_VENTAS_CLIENTE;
import static com.nextnut.logistica.util.Constantes.NODO_REPORTE_VENTAS_PRODUCTO;
import static com.nextnut.logistica.util.Constantes.NODO_SALDOS_HISTORIAL;
import static com.nextnut.logistica.util.Constantes.NODO_SALDO_TOTAL;
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

    // Total Inicial de Ordenes. 3
    public Boolean mLiberarSemaforoTotalInicial = false; // se pone en true cuando tengo que liberar por accion de este proceso
    public ArrayList<String> mTotalInicialIndex = new ArrayList<String>();
    public ArrayList<String> mTotalInicialIndexLiberar = new ArrayList<String>();

    public ArrayList<Task> mTotalInicialTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mTotalInicialCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarTotalInicialTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarTotalInicialCompetionTask = new ArrayList<TaskCompletionSource<Object>>();

    // Cabecera Orden 1B
    public Boolean mLiberarSemaforoCabeceraOrden = false;
    public ArrayList<String> mCabeceraOrdenIndex = new ArrayList<String>();
    public ArrayList<String> mCabeceraOrdenLiberar = new ArrayList<String>();
    public ArrayList<Task> mCabeceraOrdenTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mCabeceraOrdenCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarCabeceraOrdenTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarcabeceraOrdenCompletionTask = new ArrayList<TaskCompletionSource<Object>>();



    // Productos En Ordenes 5

    public Boolean mLiberarSemaforoProductosEnOrdenes = false;
    public TaskCompletionSource<DataSnapshot> mProductosEnOrdenesCompletionTask;
    public Task mProductosEnOrdenesTask;
    public Task mLiberarProductosEnOrdenesTask;
    public TaskCompletionSource<DataSnapshot> mLiberarProductosEnOrdenesCompletionTask;


    // Cabecera Picking 6
    public Boolean mLiberarSemaforoPicking = false;
    public TaskCompletionSource<DataSnapshot> mPickingCompletionTask;
    public Task mPickingTask;
    public Task mLiberarPickingTask;
    public TaskCompletionSource<DataSnapshot> mLiberarPickingCompletionTask;
    public int mPickingEstado=0;
    public long mPickingNumero=0;


    // Picking Total 7
    public Boolean mLiberarSemaforoPickingTotal = false;
    public ArrayList<String> mPickingTotalIndex = new ArrayList<String>();
    public int mPickingTotalEstado=0;
    public long mPickingTotalNumero=0;
    public ArrayList<String> mPickingTotalIndexLiberar = new ArrayList<String>();
    public ArrayList<Task> mPickingTotalTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mPickingTotalCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarPickingTotalTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarPickingTotalCompletionTask = new ArrayList<TaskCompletionSource<Object>>();


    // Reporte Ventas x Producto 8
    public Boolean mLiberarSemaforoReporteVentasProducto = false;
    public ArrayList<String> mReporteVentasProductoIndex = new ArrayList<String>();
    public ArrayList<String> mReporteVentasProductoIndexLiberar = new ArrayList<String>();//productKey
    public ArrayList<String> mReporteVentasProductoAAMMIndexLiberar = new ArrayList<String>();// aamm (ano, mes)

    public ArrayList<Task> mReporteVentasProductoTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mReporteVentasProductoCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarReporteVentasProductoTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarReporteVentasProductoCompletionTask = new ArrayList<TaskCompletionSource<Object>>();



    // Reporte Ventas x Cliente 9
    public Boolean mLiberarSemaforoReporteVentasCliente = false;
    public ArrayList<String> mReporteVentasClienteIndex = new ArrayList<String>();
    public ArrayList<String> mReporteVentasClienteClienteKeyIndexLiberar = new ArrayList<String>();
    public ArrayList<String> mReporteVentasClienteAAMMIndexLiberar = new ArrayList<String>();
    public ArrayList<String> mReporteVentasClienteProductKeyIndexLiberar = new ArrayList<String>();
    public ArrayList<Task> mReporteVentasClienteTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mReporteVentasClienteCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarReporteVentasClienteTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarReporteVentasClienteCompletionTask = new ArrayList<TaskCompletionSource<Object>>();


    // Saldos total 10
    public Boolean mLiberarSemaforoSaldoTotal = false;
    public ArrayList<String> mSaldosTotalIndex = new ArrayList<String>();
    public ArrayList<String> mSaldosTotalIndexLiberar = new ArrayList<String>();
    public ArrayList<Task> mSaldosTotalTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mSaldosTotalCompletionTask = new ArrayList<TaskCompletionSource<Object>>();
    public ArrayList<Task> mLiberarSaldosTotalTask = new ArrayList<Task>();
    public ArrayList<TaskCompletionSource<Object>> mLiberarSaldosTotalCompletionTask = new ArrayList<TaskCompletionSource<Object>>();




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

    public abstract void savePhoto(Bitmap bitmap);

    public String mCurrentPhotoPath;

    public void uploadImagen(Bitmap bitmap, StorageReference ImagenRef, final ImageView imageView, final ProgressView spinner) {

        if (bitmap != null) {
            // Create a storage reference from our app


            Log.i("subirFotoReturnUri", "onFailure: ImagenRef" + ImagenRef.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datosFoto = baos.toByteArray();
            if (spinner != null) {
                spinner.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
            UploadTask uploadTask = ImagenRef.putBytes(datosFoto);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    if (spinner != null) {
                                                        spinner.setVisibility(View.GONE);
                                                        imageView.setVisibility(View.VISIBLE);
                                                        Log.i("subirFotoReturnUri", "onFailure: -spinner off " + exception.toString());

                                                    }
                                                }
                                            }

            ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                           // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                           Uri downloadUrl = taskSnapshot.getDownloadUrl();


                                           Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_action_redeem);
                                           mCurrentPhotoPath = downloadUrl.toString();
                                           Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath-listener" + mCurrentPhotoPath);
                                           Picasso.with(getContext())
                                                   .load(mCurrentPhotoPath)
//                                                       .placeholder(drawable)
                                                   .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                                   .into(imageView);
                                           if (spinner != null) {
                                               spinner.setVisibility(View.GONE);
                                               imageView.setVisibility(View.VISIBLE);
                                           }

                                       }
                                   }

            ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (spinner != null) {
                        spinner.setProgress((float) progress);
                    }
                    Log.i("subirFotoReturnUri", "spinner progress" + progress);

                }
            });
        }
        Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath-return" + mCurrentPhotoPath);


    }


    public void readBlockTotalInicial(String productoKey) {

//        Lee y bloquea el total Inicial para un pruducto de una empresa
//        Si el producto esta bloqueda Retorna error
//
        mTotalInicialIndex.add(productoKey);// /agrego las Key de los productos bloqueados

        mTotalInicialCompletionTask.add(new TaskCompletionSource<>());
        mTotalInicialTask.add(mTotalInicialCompletionTask.get(mTotalInicialCompletionTask.size() - 1).getTask());
        mTotalInicialTask.get(mTotalInicialTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "readBlockTotalInicial onComplete mTotalInicialTask " + task.toString());
                Log.i(LOG_TAG, "readBlockTotalInicial onComplete  task.isSuccessful() " + task.isSuccessful());
                if (task.isSuccessful()) {
                    mTotalInicialIndexLiberar.add(((DataSnapshot) task.getResult()).getKey());// /agrego las Key de los productos bloqueados

                    mLiberarSemaforoTotalInicial = true;
                }

            }
        });
        mTotalInicialTask.get(mTotalInicialTask.size() - 1).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "readBlockTotalInicial onFailure mTotalInicialTask " + e.toString());
//                mLiberarSemaforoTotalInicial=true;
            }
        });


/*3*/
        mDatabase.child(ESQUEMA_ORDENES_TOTAL_INICIAL).child(mEmpresaKey).child(productoKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                Detalle detalle = mutableData.getValue(Detalle.class);


                if (detalle == null) {
                    Log.i(LOG_TAG, "readBlockTotalInicial CabeceraOrden = NuLL- ");
                } else {
                    Log.i(LOG_TAG, "readBlockTotalInicial CabeceraOrden = not NuLL- ");

                    if (detalle.sepuedeModificar()) {
                        Log.i(LOG_TAG, "readBlockTotalInicial Si, se puede Modificar y bloqueo");
                        detalle.bloquear();
                    } else {
                        Log.i(LOG_TAG, "readBlockTotalInicial No se puede Modificar ");
                        Log.i(LOG_TAG, "readBlockTotalInicial Bloqueado " + detalle.getProducto().getNombreProducto());

//                        mTotalInicialCompletionTask.setException(new Exception("Bloqueado"));
                        return Transaction.abort();
                    }

                }

                // Set value and report transaction success
                mutableData.setValue(detalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "readBlockTotalInicial: boolean b" + commited);
                int index = mTotalInicialIndex.indexOf(dataSnapshot.getKey());
                Log.d(LOG_TAG, "readBlockTotalInicial: commited," + commited + " key:" + "-index " + index);

                if (commited) {

                    mTotalInicialCompletionTask.get(index).setResult(dataSnapshot);
                    Log.i(LOG_TAG, "readBlockTotalInicial onComplete True ");
                    Detalle detalle = dataSnapshot.getValue(Detalle.class);
                    if (detalle == null) {
                        Log.d(LOG_TAG, "readBlockTotalInicial:onComplete: detalle.getCantidadOrden() NULL");
                    } else {
                        Log.d(LOG_TAG, "readBlockTotalInicial:onComplete: detalle.Semaforo " + detalle.getSemaforo()
                                + "-" + detalle.getCantidadOrden());
                    }
                } else {


                    if (databaseError == null) {
                        Log.i(LOG_TAG, "readBlockTotalInicial onComplete False " + "error nulo total Incial");
                        mTotalInicialCompletionTask.get(index).setException(new Exception("Error Nulo"));
                    } else {
                        Log.i(LOG_TAG, "readBlockTotalInicial onComplete False " + databaseError.toString());
                        mTotalInicialCompletionTask.get(index).setException(new Exception(databaseError.getMessage()));
                    }
                }


            }
        });
    }

    public void liberarTotalInicial() {
        ArrayList<String> totalInicialIndexLiberarAux = (ArrayList<String>) mTotalInicialIndexLiberar.clone();
        Log.i(LOG_TAG, "liberarTotalInicial onComplete mTotalInicialIndexLiberar.size() " + mTotalInicialIndexLiberar.size());
        for (int a = 0; a < mTotalInicialIndexLiberar.size(); a++) {

            mLiberarTotalInicialCompetionTask.add(new TaskCompletionSource<>());
            mLiberarTotalInicialTask.add(mLiberarTotalInicialCompetionTask.get(mLiberarTotalInicialCompetionTask.size() - 1).getTask());
            mLiberarTotalInicialTask.get(mLiberarTotalInicialTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.i(LOG_TAG, "liberarTotalInicial onComplete liberarTotalInicial " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        Log.i(LOG_TAG, "liberarTotalInicial onComplete key " + ((DataSnapshot) task.getResult()).getKey());
                        int index = mTotalInicialIndexLiberar.indexOf(((DataSnapshot) task.getResult()).getKey());
                        Log.i(LOG_TAG, "liberarTotalInicial onComplete index " + index);

                        if (index > -1) {
                            Log.i("liberarTotalInicial", "Liberacion de la key" + ((DataSnapshot) task.getResult()).getKey() + " - index: " + index);
                            mTotalInicialIndexLiberar.set(index,"0");

                        }
                        if (sonTodosCeros(mTotalInicialIndexLiberar)) {
                            Log.i("liberarTotalInicial", "Liberacion Completa");
                            mLiberarSemaforoTotalInicial = false;
                            mLiberarTotalInicialCompetionTask.clear();
                            mLiberarTotalInicialTask.clear();
                            mTotalInicialIndexLiberar.clear();
                            Log.i(LOG_TAG, "liberarTotalInicial onComplete mLiberarSemaforoTotalInicial" + mLiberarSemaforoTotalInicial);

                        }
                    }

                }
            });
            mLiberarTotalInicialTask.get(mLiberarTotalInicialTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(LOG_TAG, "liberarTotalInicial onFailure liberarTotalInicial " + e.toString());
//                TOdo: ver que hacer cuando falla el relase. cuanto se insiste;
//                liberarTotalInicial(productoKey);
                }
            });

            mDatabase.child(ESQUEMA_ORDENES_TOTAL_INICIAL).child(mEmpresaKey).child(totalInicialIndexLiberarAux.get(a)).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    // Bloqueo la orden para modificaciones,
                    // Actualizo  el esquema y luego lo libero.

                    Detalle detalle = mutableData.getValue(Detalle.class);


                    if (detalle == null) {
                        Log.i(LOG_TAG, "liberarTotalInicial CabeceraOrden = NuLL- ");
                    } else {
                        Log.i(LOG_TAG, "liberarTotalInicial CabeceraOrden = not NuLL-detalle.sepuedeModificar() " + detalle.sepuedeModificar());

                        if (!detalle.sepuedeModificar()) {
                            Log.i(LOG_TAG, "liberarTotalInicial NO, se puede Modificar y liberar");
                            detalle.liberar();
                        } else {
                            Log.i(LOG_TAG, "liberarTotalInicial No se puede Modificar ");
                            return Transaction.abort();
                        }

                    }

                    // Set value and report transaction success
                    mutableData.setValue(detalle);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited,
                                       DataSnapshot dataSnapshot) {
                    int index = mTotalInicialIndexLiberar.indexOf(dataSnapshot.getKey());
                    // Transaction completed
                    Log.d(LOG_TAG, "liberarTotalInicial: boolean b" + commited);
                    if (commited) {
//                    mLiberarSemaforoTotalInicial=false;
                        mLiberarTotalInicialCompetionTask.get(index).setResult(dataSnapshot);
//                    mTotalInicialCompletionTask.setResult(dataSnapshot);
                        Log.i(LOG_TAG, "liberarTotalInicial onComplete True ");
                        Detalle detalle = dataSnapshot.getValue(Detalle.class);
                        if (detalle == null) {
                            Log.d(LOG_TAG, "liberarTotalInicial:onComplete: detalle.getCantidadOrden() NULL");
                        } else {
                            Log.d(LOG_TAG, "liberarTotalInicial:onComplete: detalle.semaforo" + detalle.getSemaforo()
                                    + "-" + detalle.getCantidadOrden());
                        }
                    } else {
                        if (databaseError == null) {
                            mLiberarTotalInicialCompetionTask.get(index).setException(new Exception("Error Nulo Liberar Total Inicial"));
                            Log.i(LOG_TAG, "liberarTotalInicial onComplete False " + "error nulo");
                        } else {
                            mLiberarTotalInicialCompetionTask.get(index).setException(new Exception(databaseError.toString()));
                            Log.i(LOG_TAG, "liberarTotalInicial onComplete False " + databaseError.toString());
                        }
                    }


                }
            });

        }
    }

    public void readBlockCabeceraOrden(long numeroOrdena) {
        String numeroOrden = String.valueOf(numeroOrdena);

//        Lee y bloquea el total Inicial para un pruducto de una empresa
//        Si el producto esta bloqueda Retorna error
//
        Log.i(LOG_TAG, "readBlockCabeceraOrden Numero de orden:" + numeroOrden);
        mCabeceraOrdenIndex.add(String.valueOf(refCabeceraOrden_1B(numeroOrdena)));
        mCabeceraOrdenCompletionTask.add(new TaskCompletionSource<>());
        mCabeceraOrdenTask.add(mCabeceraOrdenCompletionTask.get(mCabeceraOrdenCompletionTask.size() - 1).getTask());
        mCabeceraOrdenTask.get(mCabeceraOrdenTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete mCabeceraOrdenTask " + task.toString());
                Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete is succesfull " + task.isSuccessful());
                if (task.isSuccessful()) {
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete getKey() " + ((DataSnapshot) task.getResult()).getKey());
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete getRef() " + ((DataSnapshot) task.getResult()).getRef());
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete getRef().key " + ((DataSnapshot) task.getResult()).getRef().getKey());
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete getRef().parent " + ((DataSnapshot) task.getResult()).getRef().getParent());
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete getRef().parent.key " + ((DataSnapshot) task.getResult()).getRef().getParent().getKey());

                    mCabeceraOrdenLiberar.add(((DataSnapshot) task.getResult()).getRef().getParent().getKey());// /agrego las Key de los productos bloqueados

                    mLiberarSemaforoCabeceraOrden = true;
                }
            }
        });
        mCabeceraOrdenTask.get(mCabeceraOrdenTask.size() - 1).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "readBlockCabeceraOrden onFailure mCabeceraOrdenTask " + e.toString());
//                mLiberarSemaforoCabeceraOrden=true;

            }
        });

/*1-B*/
        mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(numeroOrden).child("cabecera").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                CabeceraOrden cabeceraOrden = mutableData.getValue(CabeceraOrden.class);

                if (cabeceraOrden == null) {
                    Log.i(LOG_TAG, "readBlockCabeceraOrden CabeceraOrden = NuLL- ");
                } else {
                    Log.i(LOG_TAG, "readBlockCabeceraOrden CabeceraOrden = not NuLL- cabeceraOrden.sepuedeModificar()" + cabeceraOrden.sepuedeModificar());

                    if (cabeceraOrden.sepuedeModificar()) {
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Si, se puede Modificar y bloqueo");
                        cabeceraOrden.bloquear();
                    } else {
                        Log.i(LOG_TAG, "readBlockCabeceraOrden  blqueado No se puede Modificar Orden " + cabeceraOrden.getNumeroDeOrden());
//                        mCabeceraOrdenCompletionTask.setException(new Exception("Bloqueado"));
                        return Transaction.abort();
                    }

                }

                // Set value and report transaction success
                mutableData.setValue(cabeceraOrden);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: commited: " + commited);
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: size: " + mCabeceraOrdenIndex.size());
//                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: size: " + mCabeceraOrdenIndex.get(0));
//                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: mCabeceraOrdenIndex.indexOf(dataSnapshot.getKey()" + mCabeceraOrdenIndex.indexOf(dataSnapshot.getRef().toString()));
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: mCabeceraOrdenIndex.parent " + dataSnapshot.getRef().getParent().getKey());
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: ref: " + dataSnapshot.getRef());
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: key: " + dataSnapshot.getKey());
                int index = mCabeceraOrdenIndex.indexOf(dataSnapshot.getRef().toString());
                for (int a = 0; a < mCabeceraOrdenIndex.size(); a++) {
                    if (mCabeceraOrdenIndex.get(a).equals(dataSnapshot.getRef().toString())) {
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: son Iguales: " + mCabeceraOrdenIndex.get(a));
                    } else {
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: son Distinto: Index " + mCabeceraOrdenIndex.get(a));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: son Distinto: Index legth" + mCabeceraOrdenIndex.get(a).length());
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: son Distinto: snap " + dataSnapshot.getRef());

                    }
                }

                if (commited) {

                    mCabeceraOrdenCompletionTask.get(index).setResult(dataSnapshot);
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete True ");
                    CabeceraOrden cabeceraOrden = dataSnapshot.getValue(CabeceraOrden.class);
                    if (cabeceraOrden == null) {
                        Log.d(LOG_TAG, "readAndBlockCabeceraOrden Inicial cabecera NULL Task completa");
                    } else {
                        Log.d(LOG_TAG, "readBlockCabeceraOrden Inicial semaforo Task completa" + cabeceraOrden.getSemaforo());
                    }
                } else {


                    if (databaseError == null) {
                        mCabeceraOrdenCompletionTask.get(index).setException(new Exception("error nulo Cabecera Orden"));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Task incompleta error " + "error nulo");
                    } else {
                        mCabeceraOrdenCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Task incompleta " + databaseError.toString());
                    }

                }


            }
        });

    }

    public Boolean sonTodosCeros(ArrayList<String> s) {
        for (int a = 0; a <= (s.size() - 1); a++) {
            if (!s.get(a).equals("0")) {
                return false;
            }

        }
        return true;
    }

    public void liberarCabeceraOrden() {

        ArrayList<String> cabeceraOrdenLiberarAux = (ArrayList<String>) mCabeceraOrdenLiberar.clone();
        for (int a = 0; a < mCabeceraOrdenLiberar.size(); a++) {
            mLiberarcabeceraOrdenCompletionTask.add(new TaskCompletionSource<>());
            mLiberarCabeceraOrdenTask.add(mLiberarcabeceraOrdenCompletionTask.get(mLiberarcabeceraOrdenCompletionTask.size() - 1).getTask());
            mLiberarCabeceraOrdenTask.get(mLiberarCabeceraOrdenTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.i(LOG_TAG, "liberarCabeceraOrden onComplete liberarTotalInicial" + task.toString());
                    if (task.isSuccessful()) {
                        ((DataSnapshot) task.getResult()).getKey();
                        int index = mCabeceraOrdenLiberar.indexOf(((DataSnapshot) task.getResult()).getRef().getParent().getKey());

                        if (index > -1) {
                            mCabeceraOrdenLiberar.set(index, "0");
                            Log.i("liberarCabeceraOrden", "Liberacion " + " - index: " + index);
//                            mLiberarcabeceraOrdenCompletionTask.remove(index);
//                            mLiberarCabeceraOrdenTask.remove(index);
                        }
                        if (sonTodosCeros(mCabeceraOrdenLiberar)) {
                            Log.i("liberarCabeceraOrden", "Liberacion Completa index" + index);
                            mLiberarSemaforoCabeceraOrden = false;
                            mCabeceraOrdenLiberar.clear();
                            mCabeceraOrdenIndex.clear();
                            mLiberarcabeceraOrdenCompletionTask.clear();
                            mLiberarCabeceraOrdenTask.clear();
                            Log.i(LOG_TAG, "liberarCabeceraOrdenonComplete mLiberarSemaforoCabeceraOrden" + mLiberarSemaforoCabeceraOrden);
                            Log.i(LOG_TAG, "liberarCabeceraOrdenonComplete mLiberarcabeceraOrdenCompletionTask" + mLiberarcabeceraOrdenCompletionTask.size());
                            Log.i(LOG_TAG, "liberarCabeceraOrdenonComplete mLiberarCabeceraOrdenTask" + mLiberarCabeceraOrdenTask.size());

                        }


                    }

                }
            });
            mLiberarCabeceraOrdenTask.get(mLiberarCabeceraOrdenTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(LOG_TAG, "liberarCabeceraOrden onFailure liberarTotalInicial " + e.toString());
//                TOdo: ver que hacer cuando falla el relase. cuanto se insiste;
//                liberarTotalInicial(productoKey);
                }
            });


//        *1-B*/
            mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(cabeceraOrdenLiberarAux.get(a)).child("cabecera").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    // Bloqueo la orden para modificaciones,
                    // Actualizo  el esquema y luego lo libero.

                    CabeceraOrden cabeceraOrden = mutableData.getValue(CabeceraOrden.class);

                    if (cabeceraOrden == null) {
                        Log.i(LOG_TAG, "liberarCabeceraOrden CabeceraOrden = NuLL- ");
                    } else {
                        Log.i(LOG_TAG, "liberarCabeceraOrden CabeceraOrden = not NuLL- cabeceraOrden.sepuedeModificar()" + cabeceraOrden.sepuedeModificar());
                        if (!cabeceraOrden.sepuedeModificar()) {
                            Log.i(LOG_TAG, "liberarCabeceraOrden Si, se puede Modificar y bloqueo");
                            cabeceraOrden.liberar();
                        } else {
                            Log.i(LOG_TAG, "liberarCabeceraOrden No se puede Lberar ");
//                        mCabeceraOrdenCompletionTask.setException(new Exception("Bloqueado"));
                            return Transaction.abort();
                        }

                    }

                    // Set value and report transaction success
                    mutableData.setValue(cabeceraOrden);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                    Log.d(LOG_TAG, "liberarCabeceraOrden: boolean b" + commited);
                    int index = mCabeceraOrdenLiberar.indexOf(dataSnapshot.getRef().getParent().getKey());

                    if (commited) {
                        mLiberarcabeceraOrdenCompletionTask.get(index).setResult(dataSnapshot);
                        Log.i(LOG_TAG, "liberarCabeceraOrden onComplete True ");
                        CabeceraOrden cabeceraOrden = dataSnapshot.getValue(CabeceraOrden.class);
                        if (cabeceraOrden == null) {
                            Log.d(LOG_TAG, "liberarCabeceraOrden cabecera NULL Task completa");
                        } else {
                            Log.d(LOG_TAG, "liberarCabeceraOrden semaforo Task completa" + String.valueOf(cabeceraOrden.getSemaforo()));
                        }
                    } else {
//                    mCabeceraOrdenCompletionTask.setException(new Exception("Bloqueado"));


                        if (databaseError == null) {
                            mLiberarcabeceraOrdenCompletionTask.get(index).setException(new Exception("Error Nulo"));
                            Log.i(LOG_TAG, "liberarCabeceraOrden Task incompleta " + "error nulo Liberar Cabecera Orden");
                        } else {
                            mLiberarcabeceraOrdenCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                            Log.i(LOG_TAG, "liberarCabeceraOrden Task incompleta " + databaseError.toString());
                        }

                    }


                }
            });
        }
    }


    public void readBlockProductosEnOrdenes(String productKey, long numeroDeOrdena) {

//        Lee y bloquea prodcutos en Ordenes 5

        String numeroDeOrden = String.valueOf(numeroDeOrdena);
        mProductosEnOrdenesCompletionTask = new TaskCompletionSource<>();
        mProductosEnOrdenesTask = mProductosEnOrdenesCompletionTask.getTask();
        mProductosEnOrdenesTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoProductosEnOrdenes = true;
                }
            }
        });

/*5*/
        mDatabase.child(ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL).child(mEmpresaKey).child(productKey).child(numeroDeOrden).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PrductosxOrden productosEnOrdenes = mutableData.getValue(PrductosxOrden.class);
                if (productosEnOrdenes == null) {
                } else {
                    if (productosEnOrdenes.sepuedeModificar()) {
                        productosEnOrdenes.bloquear();
                    } else {
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(productosEnOrdenes);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                if (commited) {
                    mProductosEnOrdenesCompletionTask.setResult(dataSnapshot);
                    Log.i(LOG_TAG, "readBlockCabeceraOrden Task completa ");

                } else {
                    if (databaseError == null) {
                        mProductosEnOrdenesCompletionTask.setException(new Exception("error nulo"));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Task incompleta error nulo Productos en Ordenes)");

                    } else {
                        mProductosEnOrdenesCompletionTask.setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden Task incompleta " + databaseError.toString());
                    }
                }
            }
        });
    }

    public void liberarProductosEnOrdenes(String productKey, String numeroDeOrden) {

        mLiberarProductosEnOrdenesCompletionTask = new TaskCompletionSource<>();
        mLiberarProductosEnOrdenesTask = mLiberarProductosEnOrdenesCompletionTask.getTask();
        //noinspection unchecked
        mLiberarProductosEnOrdenesTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoProductosEnOrdenes = false;
                    mLiberarProductosEnOrdenesCompletionTask=null;
                    mLiberarProductosEnOrdenesTask=null;

                }
            }
        });
        mLiberarProductosEnOrdenesTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });



/*5*/
        mDatabase.child(ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL).child(mEmpresaKey).child(productKey).child(numeroDeOrden).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PrductosxOrden productosxOrden = mutableData.getValue(PrductosxOrden.class);
                if (productosxOrden == null) {
                } else {
                    if (!productosxOrden.sepuedeModificar()) {
                        productosxOrden.liberar();
                    } else {
                        return Transaction.abort();
                    }
                }
                mutableData.setValue(productosxOrden);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                if (commited) {
                    mLiberarProductosEnOrdenesCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        mLiberarProductosEnOrdenesCompletionTask.setException(new Exception("Error Nulo Liberar Productos en Ordenes"));
                    } else {

                        mLiberarProductosEnOrdenesCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }


    public void limpiarTodosLosSemaforosLiberar() {
        mLiberarSemaforoTotalInicial = false;
        mLiberarSemaforoCabeceraOrden = false;
        mLiberarSemaforoProductosEnOrdenes = false;
        mLiberarSemaforoPicking = false;
        mLiberarSemaforoPickingTotal = false;
    }


    public void readBlockPicking(int estado, Long numeroPicking) {

//        Lee y bloquea  Ordenes de Picking 6

        mPickingEstado=estado;
        mPickingNumero=numeroPicking;
        mPickingCompletionTask = new TaskCompletionSource<>();
        mPickingTask = mPickingCompletionTask.getTask();
        mPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoPicking = true;
                }
            }
        });

        Log.i(LOG_TAG, "readBlockPicking estado "+estado +" - numeroPicking "+numeroPicking);
/*6*/
        mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(estado)).child(String.valueOf(numeroPicking)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CabeceraPicking cabeceraPicking = mutableData.getValue(CabeceraPicking.class);

                if (cabeceraPicking == null) {
                    Log.i(LOG_TAG, "readBlockPicking cabeceraPicking NULL");

                } else {
                    if (cabeceraPicking.sepuedeModificar()) {
                        Log.i(LOG_TAG, "readBlockPicking Cabecera Picking "+cabeceraPicking.getNumeroDePickingOrden() +"-"+cabeceraPicking.getTotales().getMontoEntregado());
                        cabeceraPicking.bloquear();
                    } else {
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(cabeceraPicking);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                if (commited) {
                    Log.i(LOG_TAG, "readBlockPicking Task completa !!");
                    CabeceraPicking cabeceraPicking = dataSnapshot.getValue(CabeceraPicking.class);
                    Log.i(LOG_TAG, "readBlockPicking Cabecera Picking "+cabeceraPicking.getNumeroDePickingOrden() +"-"+cabeceraPicking.getTotales().getMontoEntregado());
                    mPickingCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        Log.i(LOG_TAG, "readBlockPicking Task incompleta " + "error nulo Picking");
                        mPickingCompletionTask.setException(new Exception("error nulo"));
                    } else {
                        Log.i(LOG_TAG, "readBlockPicking Task incompleta " + databaseError.toString());
                        mPickingCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarPicking(int estado, long numeroPicking) {

        mLiberarPickingCompletionTask = new TaskCompletionSource<>();
        mLiberarPickingTask = mLiberarPickingCompletionTask.getTask();
        //noinspection unchecked
        mLiberarPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoPicking = false;
                    mLiberarPickingCompletionTask=null;
                    mLiberarPickingTask=null;
                    mPickingEstado=0;
                    mPickingNumero=0;
                }
            }
        });
        mLiberarPickingTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

/*6*/
        mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(estado)).child(String.valueOf(numeroPicking)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CabeceraPicking cabeceraPicking = mutableData.getValue(CabeceraPicking.class);
                if (cabeceraPicking == null) {
                } else {
                    if (!cabeceraPicking.sepuedeModificar()) {
                        cabeceraPicking.liberar();
                    } else {
                        return Transaction.abort();
                    }
                }
                mutableData.setValue(cabeceraPicking);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                if (commited) {
                    mLiberarPickingCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        mLiberarPickingCompletionTask.setException(new Exception("Error Nulo Liberar Picking"));
                    } else {
                        mLiberarPickingCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void readBlockPickingTotal(int estado, long numeroPicking, String productKey) {

//        Lee y bloquea  Ordenes ce Picking 6
        mPickingTotalIndex.add(productKey);
        mPickingTotalEstado=estado;
        mPickingTotalNumero=numeroPicking;
        Log.i("readBlockPickingTotal", "estado " + estado + " numeroPicking " + numeroPicking + " productKey " + productKey);

        mPickingTotalCompletionTask.add(new TaskCompletionSource<>());
        mPickingTotalTask.add(mPickingTotalCompletionTask.get(mPickingTotalCompletionTask.size() - 1).getTask());
        mPickingTotalTask.get(mPickingTotalTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("readBlockPickingTotal", "task.isSuccessful()" + task.isSuccessful());
                    mPickingTotalIndexLiberar.add(((DataSnapshot) task.getResult()).getKey());// /agrego las Key de los productos bloqueados
                    mLiberarSemaforoPickingTotal = true;
                }
            }
        });

/*7*/
        mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(String.valueOf(estado)).child(String.valueOf(numeroPicking)).child(productKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Detalle detalle = mutableData.getValue(Detalle.class);
                if (detalle == null) {
                    Log.i("readBlockPickingTotal", " tdetalle == null");
                } else {
                    if (detalle.sepuedeModificar()) {
                        Log.i("readBlockPickingTotal", "detalle.sepuedeModificar() " + detalle.sepuedeModificar());
                        detalle.bloquear();
                    } else {
                        Log.i("readBlockPickingTotal", "Bloqueado " + detalle.getProducto().getNombreProducto());
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(detalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                int index = mPickingTotalIndex.indexOf(dataSnapshot.getKey());
                Log.i("readBlockPickingTotal", "onComplete index" + index);
                if (commited) {
                    Log.i("readBlockPickingTotal", "Task completa commited " + commited);
                    mPickingTotalCompletionTask.get(index).setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        Log.i("readBlockPickingTotal", "Task incompletadatabaseError == null");
                        mPickingTotalCompletionTask.get(index).setException(new Exception("error nulo Picking Total"));
                    } else {
                        Log.i("readBlockPickingTotal", "Task incompleta databaseError == " + databaseError.toString());
                        mPickingTotalCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarPickingTotal(int estado, long numeroPicking) {
        Log.i("LiberarPicking", "estado" +  estado);
        Log.i("LiberarPicking", "numeroPicking" +String.valueOf(numeroPicking));

        ArrayList<String> pickingTotalIndexLiberarAux = (ArrayList<String>) mPickingTotalIndexLiberar.clone();
        for (int a = 0; a < mPickingTotalIndexLiberar.size(); a++) {
            Log.i("LiberarPicking", "mPickingTotalIndexLiberar.get(a)" + mPickingTotalIndexLiberar.get(a));
            mLiberarPickingTotalCompletionTask.add(new TaskCompletionSource<>());
            mLiberarPickingTotalTask.add(mLiberarPickingTotalCompletionTask.get(mLiberarPickingTotalCompletionTask.size() - 1).getTask());
            //noinspection unchecked
            mLiberarPickingTotalTask.get(mLiberarPickingTotalTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.i("LiberarPicking", "task.isSuccessful()" + task.isSuccessful());
                        int index = mPickingTotalIndexLiberar.indexOf(((DataSnapshot) task.getResult()).getKey());
                        Log.i("LiberarPicking", "getKey: " + ((DataSnapshot) task.getResult()).getKey() + " index: " + index);
                        if (index > -1) {
                            Log.i("LiberarPicking", "Liberacion de la key" + ((DataSnapshot) task.getResult()).getKey() + " - index: " + index);
                            mPickingTotalIndexLiberar.set(index, "0");

                        }
                        if (sonTodosCeros(mPickingTotalIndexLiberar)) {
                            Log.i("LiberarPicking", "Liberacion Completa");
                            mLiberarSemaforoPicking = false;
                            mLiberarPickingTotalCompletionTask.clear();
                            mLiberarPickingTotalTask.clear();
                            mPickingTotalIndexLiberar.clear();
                            mPickingTotalIndex.clear();
                            mPickingTotalNumero=0;
                            mPickingTotalEstado=0;
                        }
                    }
                }
            });
            mLiberarPickingTotalTask.get(mLiberarPickingTotalTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

/*7*/


            mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(String.valueOf(estado)).child(String.valueOf(numeroPicking)).child(pickingTotalIndexLiberarAux.get(a)).runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Detalle detalle = mutableData.getValue(Detalle.class);
                    if (detalle == null) {
                        Log.i("LiberarPicking", "detalle null");

                    } else {
                        Log.i("LiberarPicking", "detalle" + detalle.getProducto().getNombreProducto() + " " + detalle.getSemaforo());

                        if (!detalle.sepuedeModificar()) {
                            detalle.liberar();
                        } else {
                            return Transaction.abort();
                        }
                    }
                    mutableData.setValue(detalle);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                    int index = mPickingTotalIndexLiberar.indexOf(dataSnapshot.getKey());
                    Log.i("LiberarPicking", "onComplete  dataSnapshot.getKey() " + dataSnapshot.getKey());

                    Log.i("LiberarPicking", "onComplete  index " + index);
                    if (commited) {
                        mLiberarPickingTotalCompletionTask.get(index).setResult(dataSnapshot);
                    } else {
                        if (databaseError == null) {
                            mLiberarPickingTotalCompletionTask.get(index).setException(new Exception("Error Nulo liberar Picking Total"));
                        } else {
                            mLiberarPickingTotalCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                        }
                    }
                }
            });
        }
    }


    public void readBlockReporteVentasProducto( String productKey,String aamm) {

//        Lee y bloquea  Reporte de Ventas Producto 8
        mReporteVentasProductoIndex.add(productKey);

        Log.i("RB_ReporteVentaProd", " productKey " + productKey);

        mReporteVentasProductoCompletionTask.add(new TaskCompletionSource<>());
        mReporteVentasProductoTask.add(mReporteVentasProductoCompletionTask.get(mReporteVentasProductoCompletionTask.size() - 1).getTask());
        mReporteVentasProductoTask.get(mReporteVentasProductoTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("RB_ReporteVentaProd", "task.isSuccessful()" + task.isSuccessful());
                    Log.i("RB_ReporteVentaProd", ".getKey()" + ((DataSnapshot) task.getResult()).getKey());
                    Log.i("RB_ReporteVentaProd", ".getParentKey" + ((DataSnapshot) task.getResult()).getRef().getParent().getKey());

                    Pair<String,String> parProducto_AAMM=new Pair(((DataSnapshot) task.getResult()).getRef().getParent().getKey(),((DataSnapshot) task.getResult()).getKey());
                    Log.i("RB_ReporteVentaProd", "parProducto" + parProducto_AAMM.toString());


                    mReporteVentasProductoIndexLiberar.add(((DataSnapshot) task.getResult()).getRef().getParent().getKey());// /agrego las Key de los productos bloqueados
                    mReporteVentasProductoAAMMIndexLiberar.add(((DataSnapshot) task.getResult()).getKey());// /agrego AAMM bloqueados

                    mLiberarSemaforoReporteVentasProducto = true;
                }
            }
        });

/*8*/
        refReporteVentasProducto_8(productKey,aamm ).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Detalle detalle = mutableData.getValue(Detalle.class);
                if (detalle == null) {
                    Log.i("RB_ReporteVentaProd", " tdetalle == null");
                } else {
                    if (detalle.sepuedeModificar()) {
                        Log.i("RB_ReporteVentaProd", "detalle.sepuedeModificar() " + detalle.sepuedeModificar());
                        detalle.bloquear();
                    } else {
                        Log.i("RB_ReporteVentaProd", "Bloqueado " + detalle.getProducto().getNombreProducto());
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(detalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                Log.i("RB_ReporteVentaProd", "Producto Key" + dataSnapshot.getRef().getParent().getKey());
                int index = mReporteVentasProductoIndex.indexOf(dataSnapshot.getRef().getParent().getKey());
                Log.i("RB_ReporteVentaProd", "onComplete index" + index);
                if (commited) {
                    Log.i("RB_ReporteVentaProd", "Task completa commited " + commited);
                    mReporteVentasProductoCompletionTask.get(index).setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        Log.i("RB_ReporteVentaProd", "Task incompletadatabaseError == null");
                        mReporteVentasProductoCompletionTask.get(index).setException(new Exception("error nulo Ventas Producto"));
                    } else {
                        Log.i("RB_ReporteVentaProd", "Task incompleta databaseError == " + databaseError.toString());
                        mReporteVentasProductoCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarReporteVentasProducto() {

        Log.i("ReporteVentasProducto", "mReporteVentasProductoIndexLiberar.size()" + mReporteVentasProductoIndexLiberar.size());
        for (int a = 0; a < mReporteVentasProductoIndexLiberar.size(); a++) {

            mLiberarReporteVentasProductoCompletionTask.add(new TaskCompletionSource<>());
            mLiberarReporteVentasProductoTask.add(mLiberarReporteVentasProductoCompletionTask.get(mLiberarReporteVentasProductoCompletionTask.size() - 1).getTask());
            //noinspection unchecked
            mLiberarReporteVentasProductoTask.get(mLiberarReporteVentasProductoTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.i("ReporteVentasProducto", "task.isSuccessful()" + task.isSuccessful());
                        int index = mReporteVentasProductoIndexLiberar.indexOf(((DataSnapshot) task.getResult()).getRef().getParent().getKey());
                        Log.i("ReporteVentasProducto", "getKey: " + ((DataSnapshot) task.getResult()).getRef().getParent().getKey() + " index: " + index);
                        if (index > -1) {
                            Log.i("LiberarPicking", "Liberacion de la key" + ((DataSnapshot) task.getResult()).getKey() + " - index: " + index);
                            mReporteVentasProductoIndexLiberar.set(index, "0");

                        }
                        if (sonTodosCeros(mReporteVentasProductoIndexLiberar)) {
                            Log.i("LiberarPicking", "Liberacion Completa");
                            mLiberarSemaforoReporteVentasProducto = false;
                            mLiberarReporteVentasProductoCompletionTask.clear();
                            mLiberarReporteVentasProductoTask.clear();
                            mReporteVentasProductoIndexLiberar.clear();
                            mReporteVentasProductoAAMMIndexLiberar.clear();
                        }
                    }
                }
            });
            mLiberarReporteVentasProductoTask.get(mLiberarReporteVentasProductoTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

/*8*/

            refReporteVentasProducto_8(mReporteVentasProductoIndexLiberar.get(a),mReporteVentasProductoAAMMIndexLiberar.get(a)).runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Detalle detalle = mutableData.getValue(Detalle.class);
                    if (detalle == null) {
                        Log.i("ReporteVentasProducto", "detalle null");

                    } else {
                        Log.i("ReporteVentasProducto", "detalle" + detalle.getProducto().getNombreProducto() + " " + detalle.getSemaforo());

                        if (!detalle.sepuedeModificar()) {
                            detalle.liberar();
                        } else {
                            return Transaction.abort();
                        }
                    }
                    mutableData.setValue(detalle);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {

                    Log.i("ReporteVentasProducto", "mReporteVentasProductoIndexLiberar " + mReporteVentasProductoIndexLiberar.toString());
                    Log.i("ReporteVentasProducto", "onComplete  dataSnapshot.getKey()parebt " + dataSnapshot.getRef().getParent().getKey());
                    Log.i("ReporteVentasProducto", "onComplete  dataSnapshot.getKey() " + dataSnapshot.getKey());
                    int index = mReporteVentasProductoIndexLiberar.indexOf(dataSnapshot.getRef().getParent().getKey());
                    Log.i("ReporteVentasProducto", "onComplete  index " + index);
                    if (commited) {
                        mLiberarReporteVentasProductoCompletionTask.get(index).setResult(dataSnapshot);
                    } else {
                        if (databaseError == null) {
                            mLiberarReporteVentasProductoCompletionTask.get(index).setException(new Exception("Error Nulo Liberar Ventas Producto"));
                        } else {
                            mLiberarReporteVentasProductoCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                        }
                    }
                }
            });
        }
    }


    public void readBlockReporteVentasCliente(String cliente, String productKey,String aamm) {

//        Lee y bloquea  Reporte de Ventas Cliente 9
        mReporteVentasClienteIndex.add(productKey);

        Log.i("RB_ReporteVentaCliente", " productKey " + productKey);

        mReporteVentasClienteCompletionTask.add(new TaskCompletionSource<>());
        mReporteVentasClienteTask.add(mReporteVentasClienteCompletionTask.get(mReporteVentasClienteCompletionTask.size() - 1).getTask());
        mReporteVentasClienteTask.get(mReporteVentasClienteTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("RB_ReporteVentaCliente", "task.isSuccessful()" + task.isSuccessful());
                    Log.i("RB_ReporteVentaCliente", "ClienteKey" + ((DataSnapshot) task.getResult()).getRef().getParent().getParent().getKey());
                    Log.i("RB_ReporteVentaCliente", "ProductKey" + ((DataSnapshot) task.getResult()).getRef().getParent().getKey());
                    Log.i("RB_ReporteVentaCliente", "AAMM" + ((DataSnapshot) task.getResult()).getKey());
                    mReporteVentasClienteClienteKeyIndexLiberar.add(((DataSnapshot) task.getResult()).getRef().getParent().getParent().getKey());// /agrego las Key de los productos bloqueados
                    mReporteVentasClienteProductKeyIndexLiberar.add(((DataSnapshot) task.getResult()).getRef().getParent() .getKey());// /agrego las Key de los productos bloqueados
                    mReporteVentasClienteAAMMIndexLiberar.add(((DataSnapshot) task.getResult()).getKey());// /agrego las Key de los productos bloqueados
                    mLiberarSemaforoReporteVentasCliente = true;
                }
            }
        });

/*9*/
        refReporteVentasClientes_9(cliente,productKey,aamm ).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Detalle detalle = mutableData.getValue(Detalle.class);
                if (detalle == null) {
                    Log.i("RB_ReporteVentaCliente", " tdetalle == null");
                } else {
                    if (detalle.sepuedeModificar()) {
                        Log.i("RB_ReporteVentaCliente", "detalle.sepuedeModificar() " + detalle.sepuedeModificar());
                        detalle.bloquear();
                    } else {
                        Log.i("RB_ReporteVentaCliente", "Bloqueado " + detalle.getProducto().getNombreProducto());
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(detalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                Log.i("RB_ReporteVentaCliente", "onComplete dataSnapshot.getKey()" + dataSnapshot.getKey());
                Log.i("RB_ReporteVentaCliente", "onComplete getRef().getParent().getKey()" + dataSnapshot.getRef().getParent().getKey());
                int index = mReporteVentasClienteIndex.indexOf(dataSnapshot.getRef().getParent().getKey());
                Log.i("RB_ReporteVentaCliente", "onComplete index" + index);
                if (commited) {
                    Log.i("RB_ReporteVentaCliente", "Task completa commited " + commited);
                    mReporteVentasClienteCompletionTask.get(index).setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        Log.i("RB_ReporteVentaCliente", "Task incompletadatabaseError == null");
                        mReporteVentasClienteCompletionTask.get(index).setException(new Exception("error nulo Ventas Clientes"));
                    } else {
                        Log.i("RB_ReporteVentaCliente", "Task incompleta databaseError == " + databaseError.toString());
                        mReporteVentasClienteCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarReporteVentasCliente() {
        for (int a = 0; a < mReporteVentasClienteClienteKeyIndexLiberar.size(); a++) {

            mLiberarReporteVentasClienteCompletionTask.add(new TaskCompletionSource<>());
            mLiberarReporteVentasClienteTask.add(mLiberarReporteVentasClienteCompletionTask.get(mLiberarReporteVentasClienteCompletionTask.size() - 1).getTask());
            //noinspection unchecked
            mLiberarReporteVentasClienteTask.get(mLiberarReporteVentasClienteTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.i("ReporteVentasCliente", "task.isSuccessful()" + task.isSuccessful());
                        int index = mReporteVentasClienteClienteKeyIndexLiberar.indexOf(((DataSnapshot) task.getResult()).getRef().getParent().getParent().getKey());
                        Log.i("ReporteVentasCliente", "getKey: " + ((DataSnapshot) task.getResult()).getKey() + " index: " + index);
                        if (index > -1) {
                            Log.i("LiberarCliente", "Liberacion de la key" + ((DataSnapshot) task.getResult()).getKey() + " - index: " + index);
                            mReporteVentasClienteClienteKeyIndexLiberar.set(index, "0");

                        }
                        if (sonTodosCeros(mReporteVentasClienteClienteKeyIndexLiberar)) {
                            Log.i("Liberar Cliente", "Liberacion Completa");
                            mLiberarSemaforoReporteVentasCliente = false;
                            mLiberarReporteVentasClienteCompletionTask.clear();
                            mLiberarReporteVentasClienteTask.clear();
                            mReporteVentasClienteClienteKeyIndexLiberar.clear();
                            mReporteVentasClienteProductKeyIndexLiberar.clear();
                            mReporteVentasClienteAAMMIndexLiberar.clear();
                        }
                    }
                }
            });
            mLiberarReporteVentasClienteTask.get(mLiberarReporteVentasClienteTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

/*9*/

            refReporteVentasClientes_9(mReporteVentasClienteClienteKeyIndexLiberar.get(a),mReporteVentasClienteProductKeyIndexLiberar.get(a),mReporteVentasClienteAAMMIndexLiberar.get(a)).runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Detalle detalle = mutableData.getValue(Detalle.class);
                    if (detalle == null) {
                        Log.i("ReporteVentasCliente", "detalle null");

                    } else {
                        Log.i("ReporteVentasCliente", "detalle" + detalle.getProducto().getNombreProducto() + " " + detalle.getSemaforo());

                        if (!detalle.sepuedeModificar()) {
                            detalle.liberar();
                        } else {
                            return Transaction.abort();
                        }
                    }
                    mutableData.setValue(detalle);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                    int index = mReporteVentasClienteProductKeyIndexLiberar.indexOf(dataSnapshot.getRef().getParent().getKey());
                    Log.i("ReporteVentasCliente", "ProductKey " + dataSnapshot.getRef().getParent().getKey());

                    Log.i("ReporteVentasCliente", "onComplete  index " + index);
                    if (commited) {
                        mLiberarReporteVentasClienteCompletionTask.get(index).setResult(dataSnapshot);
                    } else {
                        if (databaseError == null) {
                            mLiberarReporteVentasClienteCompletionTask.get(index).setException(new Exception("Error Nulo Liberar Ventas Cliente"));
                        } else {
                            mLiberarReporteVentasClienteCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                        }
                    }
                }
            });
        }
    }


    public void readBlockSaldosTotal(String cliente) {

//        Lee y bloquea  Reporte de Ventas Cliente 9
        mSaldosTotalIndex.add(cliente);

        Log.i("RB_SaldosTotal", " cliente " + cliente);

        mSaldosTotalCompletionTask.add(new TaskCompletionSource<>());
        mSaldosTotalTask.add(mSaldosTotalCompletionTask.get(mSaldosTotalCompletionTask.size() - 1).getTask());
        mSaldosTotalTask.get(mSaldosTotalTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("RB_SaldosTotal", "task.isSuccessful()" + task.isSuccessful());
                    mSaldosTotalIndexLiberar.add(((DataSnapshot) task.getResult()).getKey());// /agrego las Key de los productos bloqueados
                    mLiberarSemaforoSaldoTotal = true;
                }
            }
        });

/*10*/
        refSaldoTotalClientes_10(cliente ).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CabeceraOrden saldos = mutableData.getValue(CabeceraOrden.class);
                if (saldos == null) {
                    Log.i("RB_SaldosTotal", " tdetalle == null");
                } else {
                    if (saldos.sepuedeModificar()) {
                        Log.i("RB_SaldosTotal", "detalle.sepuedeModificar() " + saldos.sepuedeModificar());
                        saldos.bloquear();
                    } else {
                        Log.i("RB_SaldosTotal", "Bloqueado " + saldos.getCliente().getNombre());
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(saldos);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                int index = mSaldosTotalIndex.indexOf(dataSnapshot.getKey());
                Log.i("RB_SaldosTotal", "onComplete index" + index);
                if (commited) {
                    Log.i("RB_SaldosTotal", "Task completa commited " + commited);
                    mSaldosTotalCompletionTask.get(index).setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        Log.i("RB_SaldosTotal", "Task incompletadatabaseError == null");
                        mSaldosTotalCompletionTask.get(index).setException(new Exception("error nulo Saldo Total"));
                    } else {
                        Log.i("RB_SaldosTotal", "Task incompleta databaseError == " + databaseError.toString());
                        mSaldosTotalCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarSaldosTotal() {
//        ArrayList<String> ReporteSaldosTotalLiberarAux = (ArrayList<String>) mSaldosTotalIndexLiberar.clone();
        for (int a = 0; a < mSaldosTotalIndexLiberar.size(); a++) {

            mLiberarSaldosTotalCompletionTask.add(new TaskCompletionSource<>());
            mLiberarSaldosTotalTask.add(mLiberarSaldosTotalCompletionTask.get(mLiberarSaldosTotalCompletionTask.size() - 1).getTask());
            //noinspection unchecked
            mLiberarSaldosTotalTask.get(mLiberarSaldosTotalTask.size() - 1).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.i("SaldosTotal", "task.isSuccessful()" + task.isSuccessful());
                        int index = mSaldosTotalIndexLiberar.indexOf(((DataSnapshot) task.getResult()).getKey());
                        Log.i("SaldosTotal", "getKey: " + ((DataSnapshot) task.getResult()).getKey() + " index: " + index);
                        if (index > -1) {
                            Log.i("SaldosTotal", "Liberacion de la key" + ((DataSnapshot) task.getResult()).getKey() + " - index: " + index);
                            mSaldosTotalIndexLiberar.set(index, "0");

                        }
                        if (sonTodosCeros(mSaldosTotalIndexLiberar)) {
                            Log.i("SaldosTotal", "Liberacion Completa");
                            mLiberarSemaforoSaldoTotal = false;
                            mLiberarSaldosTotalCompletionTask.clear();
                            mLiberarSaldosTotalTask.clear();
                            mSaldosTotalIndex.clear();
                            mSaldosTotalIndexLiberar.clear();
                        }
                    }else{
                        Log.i("SaldosTotal", "mLiberarSaldosTotalTask-task.isNOT Successful()" );
                    }
                }
            });
            mLiberarSaldosTotalTask.get(mLiberarSaldosTotalTask.size() - 1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("SaldosTotal", "mLiberarSaldosTotalTask-onFailure"+e.toString());
                }
            });

/*10*/

            refSaldoTotalClientes_10(mSaldosTotalIndexLiberar.get(a)).runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    CabeceraOrden saldos = mutableData.getValue(CabeceraOrden.class);
                    if (saldos == null) {
                        Log.i("SaldosTotal", "detalle null");

                    } else {
                        Log.i("SaldosTotal", "Apellido" + saldos.getCliente().getApellido());

                        if (!saldos.sepuedeModificar()) {
                            saldos.liberar();
                        } else {
                            return Transaction.abort();
                        }
                    }
                    mutableData.setValue(saldos);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                    int index = mSaldosTotalIndexLiberar.indexOf(dataSnapshot.getKey());
                    Log.i("SaldosTotal", "onComplete  dataSnapshot.getKey() " + dataSnapshot.getKey());

                    Log.i("SaldosTotal", "onComplete  index " + index);
                    if (commited) {
                        mLiberarSaldosTotalCompletionTask.get(index).setResult(dataSnapshot);
                    } else {
                        if (databaseError == null) {
                            mLiberarSaldosTotalCompletionTask.get(index).setException(new Exception("Error Nulo liberar Saldo Total"));
                        } else {
                            mLiberarSaldosTotalCompletionTask.get(index).setException(new Exception(databaseError.toString()));
                        }
                    }
                }
            });
        }
    }


    public void onDialogAlert(String mensaje) {
        AlertDialog.Builder alert;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(getContext());
        }
        alert.setMessage(mensaje);
        alert.create().show();
        alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    public Boolean  hayTareaEnProceso() {
        Log.i(LOG_TAG, "hayTareaEnProceso " );
        Log.i(LOG_TAG, "hayTareaEnProceso  mTotalInicialIndex.size()"+ mTotalInicialIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mTotalInicialIndexLiberar.size()"+ mTotalInicialIndexLiberar.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mCabeceraOrdenLiberar.size()"+ mCabeceraOrdenLiberar.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mCabeceraOrdenIndex.size()"+ mCabeceraOrdenIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mPickingTotalIndex.size()"+ mPickingTotalIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mPickingTotalIndexLiberar.size("+ mPickingTotalIndexLiberar.size() );

        Log.i(LOG_TAG, "hayTareaEnProceso  mReporteVentasProductoIndex.size()"+ mReporteVentasProductoIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mReporteVentasProductoIndexLiberar.size("+ mReporteVentasProductoIndexLiberar.size() );

        Log.i(LOG_TAG, "hayTareaEnProceso  mReporteVentasCliente.size()"+ mReporteVentasClienteIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mReporteVentasClienteClienteKeyIndexLiberar.size("+ mReporteVentasClienteClienteKeyIndexLiberar.size() );

        Log.i(LOG_TAG, "hayTareaEnProceso  mSaldosTotalIndex.size()"+ mSaldosTotalIndex.size() );
        Log.i(LOG_TAG, "hayTareaEnProceso  mSaldosTotalIndexLiberar.size("+ mSaldosTotalIndexLiberar.size() );



        int tamanoTareas =
                        //3
                        mTotalInicialIndex.size() +
                        mTotalInicialIndexLiberar.size() +
                        //1B
                        mCabeceraOrdenLiberar.size() +
                        mCabeceraOrdenIndex.size() +
                        //7
                        mPickingTotalIndex.size() +
                        mPickingTotalIndexLiberar.size() +

                                //8
                                + mReporteVentasProductoIndex.size() +
                                + mReporteVentasProductoIndexLiberar.size() +


                                //9
                                + mReporteVentasClienteIndex.size()+
                                + mReporteVentasClienteClienteKeyIndexLiberar.size() +

                                //10
                                + mSaldosTotalIndex.size() +
                                + mSaldosTotalIndexLiberar.size() ;



        if (tamanoTareas > 0) {
            onDialogAlert("aguarde un momento, A "+tamanoTareas);
            Log.i(LOG_TAG, "hayTareaEnProceso suma "+tamanoTareas );

            return true;
        }

        Log.i(LOG_TAG, "hayTareaEnProceso  mProductosEnOrdenesTask "+ mProductosEnOrdenesTask  );
        Log.i(LOG_TAG, "hayTareaEnProceso  mLiberarProductosEnOrdenesTask"+ mLiberarProductosEnOrdenesTask );
        Log.i(LOG_TAG, "hayTareaEnProceso  mPickingTask"+ mPickingTask);
        Log.i(LOG_TAG, "hayTareaEnProceso  mLiberarPickingTask"+ mLiberarPickingTask);


        if(
        // Productos En Ordenes 5

        mProductosEnOrdenesTask != null||
        mLiberarProductosEnOrdenesTask != null||

        // Cabecera Picking 6
        mPickingTask != null ||
        mLiberarPickingTask != null){
         onDialogAlert("aguarde un momento, B");
         Log.i(LOG_TAG, "hayTareaEnProceso B" );

        return true;
    }
        else{
         return false;
     }
    }
    public void liberarArrayTaskCasoExitoso() {

        // En el caso exitoso, se liberan todos los recursos, los que se usaron para el bloqueo y los que se marcan para liberar en caso de error.
        // El desbloqueo se marca dentro de la tarea de modificacion de datos para escribir en firebase una sola vez.


        // Total Inicial de Ordenes. 3
        mTotalInicialIndex.clear();
        mTotalInicialTask.clear();
        mTotalInicialCompletionTask.clear();

        mLiberarSemaforoTotalInicial = false;

        mTotalInicialIndexLiberar.clear();

        mLiberarTotalInicialTask.clear();
        mLiberarTotalInicialCompetionTask.clear();




        // Cabecera Orden 1B
        mCabeceraOrdenIndex.clear();
        mCabeceraOrdenTask.clear();
        mCabeceraOrdenCompletionTask.clear();

        mCabeceraOrdenLiberar.clear();
        mLiberarcabeceraOrdenCompletionTask.clear();
        mLiberarCabeceraOrdenTask.clear();
        mLiberarSemaforoCabeceraOrden = false;





        // Productos En Ordenes 5
       mLiberarSemaforoProductosEnOrdenes = false;
        mProductosEnOrdenesCompletionTask=null;
       mProductosEnOrdenesTask=null;
        mLiberarProductosEnOrdenesTask=null;
        mLiberarProductosEnOrdenesCompletionTask=null;


        // Cabecera Picking 6
        mLiberarSemaforoPicking = false;
        mPickingCompletionTask=null;
        mPickingTask=null;
        mLiberarPickingTask=null;
        mLiberarPickingCompletionTask=null;


        // Picking Total 7

        mPickingTotalIndex.clear();
        mPickingTotalTask.clear();
        mPickingTotalCompletionTask.clear();
        mLiberarPickingTotalCompletionTask.clear();
        mLiberarPickingTotalTask.clear();
        mPickingTotalIndexLiberar.clear();
        mLiberarSemaforoPickingTotal = false;

        // Reporte Ventas Producto 8

        mReporteVentasProductoIndex.clear();
        mReporteVentasProductoTask.clear();
        mReporteVentasProductoCompletionTask.clear();
        mLiberarReporteVentasProductoCompletionTask.clear();
        mLiberarReporteVentasProductoTask.clear();
        mReporteVentasProductoIndexLiberar.clear();
        mReporteVentasProductoAAMMIndexLiberar.clear();
        mLiberarSemaforoReporteVentasProducto = false;

        // Reporte Ventas Cliente 9

        mReporteVentasClienteIndex.clear();
        mReporteVentasClienteTask.clear();
        mReporteVentasClienteCompletionTask.clear();
        mLiberarReporteVentasClienteCompletionTask.clear();
        mLiberarReporteVentasClienteTask.clear();
        mReporteVentasClienteClienteKeyIndexLiberar.clear();
        mReporteVentasClienteProductKeyIndexLiberar.clear();
        mReporteVentasClienteAAMMIndexLiberar.clear();
        mLiberarSemaforoReporteVentasCliente = false;

        // Saldos Total 10

        mSaldosTotalIndex.clear();
        mSaldosTotalTask.clear();
        mSaldosTotalCompletionTask.clear();
        mLiberarSaldosTotalCompletionTask.clear();
        mLiberarSaldosTotalTask.clear();
        mSaldosTotalIndexLiberar.clear();
        mLiberarSemaforoSaldoTotal = false;

    }

    public void liberarArrayTaskConBloqueos() {

        // En los casos con Bloqueo solo se libera Index, task y CompletionTask.
        // Los recursos que se usan para liberar los bloqueos se limpian dentro de la tarea de liberacion de recuroso

        // Total Inicial de Ordenes. 3
        mTotalInicialIndex.clear();
        mTotalInicialTask.clear();
        mTotalInicialCompletionTask.clear();

//        mLiberarSemaforoTotalInicial = false;
//        mTotalInicialIndexLiberar.clear();
//        mLiberarTotalInicialTask.clear();
//        mLiberarTotalInicialCompetionTask.clear();




        // Cabecera Orden 1B
        mCabeceraOrdenIndex.clear();
        mCabeceraOrdenTask.clear();
        mCabeceraOrdenCompletionTask.clear();

//        mCabeceraOrdenLiberar.clear();
//        mLiberarcabeceraOrdenCompletionTask.clear();
//        mLiberarCabeceraOrdenTask.clear();
//        mLiberarSemaforoCabeceraOrden = false;





        // Productos En Ordenes 5
//       mLiberarSemaforoProductosEnOrdenes = false;
        mProductosEnOrdenesCompletionTask=null;
        mProductosEnOrdenesTask=null;
//        mLiberarProductosEnOrdenesTask=null;
//        mLiberarProductosEnOrdenesCompletionTask=null;


        // Cabecera Picking 6
//        mLiberarSemaforoPicking = false;
        mPickingCompletionTask=null;
        mPickingTask=null;
//        mLiberarPickingTask=null;
//        mLiberarPickingCompletionTask=null;


        // Picking Total 7

        mPickingTotalIndex.clear();
        mPickingTotalTask.clear();
        mPickingTotalCompletionTask.clear();
//        mLiberarPickingTotalCompletionTask.clear();
//        mLiberarPickingTotalTask.clear();
//        mPickingTotalIndexLiberar.clear();
//        mLiberarSemaforoPickingTotal = false;

        // Reporte Ventas Producto 8

        mReporteVentasProductoIndex.clear();
        mReporteVentasProductoTask.clear();
        mReporteVentasProductoCompletionTask.clear();

        // Reporte Ventas Cliente 9

        mReporteVentasClienteIndex.clear();
        mReporteVentasClienteTask.clear();
        mReporteVentasClienteCompletionTask.clear();

        // Saldos Total 10

        mSaldosTotalIndex.clear();
        mSaldosTotalTask.clear();
        mSaldosTotalCompletionTask.clear();


    }


//    public void liberarRecusosTomados(String productKey, int statusPicking, long nroPicking) {
    public void liberarRecusosTomados() {
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoTotalInicial " + mLiberarSemaforoTotalInicial);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoCabeceraOrden " + mLiberarSemaforoCabeceraOrden);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoProductosEnOrdenes " + mLiberarSemaforoProductosEnOrdenes);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoPicking " + mLiberarSemaforoPicking);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoPickingTotal " + mLiberarSemaforoPickingTotal);

        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoReporteVentasProducto " + mLiberarSemaforoReporteVentasProducto);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoReporteVentasCliente " + mLiberarSemaforoReporteVentasCliente);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoSaldoTotal "+mLiberarSemaforoSaldoTotal) ;

        if (mLiberarSemaforoTotalInicial) {
            //3 multiples productos.
            liberarTotalInicial();
        }
        if (mLiberarSemaforoCabeceraOrden) {
            //1B multiples ordenes.
            liberarCabeceraOrden();
        }
//        if (mLiberarSemaforoProductosEnOrdenes) {
            //5--un producto
//            liberarProductosEnOrdenes(nomeroDeOrden, mProductKey);
//        }
        if (mLiberarSemaforoPicking) {
            //6
            liberarPicking(mPickingEstado, mPickingNumero);
        }
        if (mLiberarSemaforoPickingTotal) {
            //7-multiples productos
            liberarPickingTotal(mPickingTotalEstado, mPickingTotalNumero);
        }

        if (mLiberarSemaforoReporteVentasProducto) {
            //8-Reporte Ventas Productos
            liberarReporteVentasProducto();
        }

        if (mLiberarSemaforoReporteVentasCliente) {
            //9-Reporte Ventas Clientes
            liberarReporteVentasCliente();
        }
        if (mLiberarSemaforoSaldoTotal) {
            //10 - Saldos Totales
            liberarSaldosTotal();
        }

    }

    public DatabaseReference refCabeceraOrden_1B(long nuemeroOrden) {
        return mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(nuemeroOrden)).child("cabecera");
    }

    public String nodoCabeceraOrden_1B(long nuemeroOrden) {
        return NODO_ORDENES + mEmpresaKey + "/" + String.valueOf(nuemeroOrden) + "/" + "cabecera";

    }

    public DatabaseReference refDetalleOrden_1C(long nuemeroOrden, String productKey) {
        return mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(nuemeroOrden)).child(productKey);
    }

    public String nodoDetalleOrden_1C(long nuemeroOrden, String productKey) {
        return NODO_ORDENES + mEmpresaKey + "/" + String.valueOf(nuemeroOrden) + "/" + productKey;

    }

    public DatabaseReference refDetalleOrden_4(long nuemeroOrden, String productKey) {
        return mDatabase.child(ESQUEMA_ORDENES_DETALLE).child(mEmpresaKey).child(String.valueOf(nuemeroOrden)).child(productKey);
    }

    public String nodoDetalleOrden_4(long nuemeroOrden, String productKey) {
        return NODO_ORDENES_DETALLE + "/" + mEmpresaKey + "/" + nuemeroOrden + "/" + productKey;
    }

    public DatabaseReference refDetalleOrden_4_ListaXOrden(long nuemeroOrden) {//trae todos los Productos de una Orden
        return mDatabase.child(ESQUEMA_ORDENES_DETALLE).child(mEmpresaKey).child(String.valueOf(nuemeroOrden));
    }

    public String nodoDetalleOrden_4_ListaXOrden(long nuemeroOrden) {//trae todos los Productos de una Orden
        return NODO_ORDENES_DETALLE + "/" + mEmpresaKey + "/" + nuemeroOrden;
    }

    public DatabaseReference refCabeceraOrden_2(int statusOrden, long nuemeroOrdena) {
        String statusString = String.valueOf(statusOrden);
        return mDatabase.child(ESQUEMA_ORDENES_CABECERA).child(mEmpresaKey).child(statusString).child(String.valueOf(nuemeroOrdena));
    }

    public String nodoCabeceraOrden_2(int statusOrden, long nuemeroOrdena) {
        String statusString = String.valueOf(statusOrden);
        return NODO_ORDENES_CABECERA + "/" + mEmpresaKey + "/" + statusString + "/" + nuemeroOrdena;
    }

    public DatabaseReference refCabeceraOrden_2Status(int statusOrden, long nuemeroOrdena, long nroPicking) {
        String statusString = String.valueOf(statusOrden);
        String nroPickingString = String.valueOf(nroPicking);
        return mDatabase.child(ESQUEMA_ORDENES_CABECERA).child(mEmpresaKey).child(statusString).child(nroPickingString).child(String.valueOf(nuemeroOrdena));
    }

    public String nodoCabeceraOrden_2Status(int statusOrden, long nuemeroOrdena, long nroPicking) {
        String statusString = String.valueOf(statusOrden);
        String nroPickingString = String.valueOf(nroPicking);
        return NODO_ORDENES_CABECERA + "/" + mEmpresaKey + "/" + statusString + "/" + nroPickingString + "/" + nuemeroOrdena;
    }

    public DatabaseReference refCabeceraOrden_2_List(int statusOrden, long nroPicking) {
        String statusString = String.valueOf(statusOrden);
        return mDatabase.child(ESQUEMA_ORDENES_CABECERA).child(mEmpresaKey).child(String.valueOf(statusString)).child(String.valueOf(nroPicking));
    }


    public DatabaseReference refProductosXOrdenInicial_5(String productKey, long nuemeroOrden) {
        return mDatabase.child(ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL).child(mEmpresaKey).child(productKey).child(String.valueOf(nuemeroOrden));
    }

    public String nodoProductosXOrdenInicial_5(String productKey, long nuemeroOrden) {
        return NODO_PRODUCTOS_EN_ORDENES_INICIAL + "/" + mEmpresaKey + "/" + productKey + "/" + nuemeroOrden;
    }

    public DatabaseReference refPicking_6(int statusPicking, long numeroPicking) {
        return mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(String.valueOf(numeroPicking));

    }

    public String nodoPicking_6(int statusPicking, String numeroPicking) {
        return NODO_PICKING + mEmpresaKey + "/" + statusPicking + "/" + numeroPicking;
    }

    public DatabaseReference refPicking_6_List(int statusPicking) {
        return mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(statusPicking));
//        return mDatabase.child(NODO_PICKING).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(String.valueOf(numeroPicking));

    }

    public String nodoPicking_6_List(int statusPicking) {
        return NODO_PICKING + mEmpresaKey + "/" + statusPicking;
    }


    public DatabaseReference refPickingTotal_7_List(int statusPicking, long nroPicking) {
        return mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(String.valueOf(nroPicking));
//        return mDatabase.child(NODO_PICKING).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(String.valueOf(numeroPicking));

    }

    public String nodoPickingTotal_7_List(int statusPicking) {
        return NODO_PICKING_TOTAL + mEmpresaKey + "/" + statusPicking;
    }

    public DatabaseReference refPickingTotal_7(int statusPicking, String productKey) {
        return mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(productKey);
//        return mDatabase.child(NODO_PICKING).child(mEmpresaKey).child(String.valueOf(statusPicking)).child(String.valueOf(numeroPicking));

    }

    public String nodoPickingTotal_7(int statusPicking, long nroPicking, String productKey) {
        return NODO_PICKING_TOTAL + mEmpresaKey + "/" + statusPicking + "/" + nroPicking + "/" + productKey;
    }


    public DatabaseReference refTotalInicial_3(String productoKey) {
        return mDatabase.child(ESQUEMA_ORDENES_TOTAL_INICIAL).child(mEmpresaKey).child(productoKey);
    }

    public String nodoTotalInicial_3(String productoKey) {
        return NODO_ORDENES_TOTAL_INICIAL + mEmpresaKey + "/" + (productoKey);
    }

// Total de ventas de la empresa por mes 8
    public DatabaseReference refReporteVentasProducto_8(String productoKey,String aamm) {
        return mDatabase.child(ESQUEMA_REPORTE_VENTAS_PRODUCTO).child(mEmpresaKey).child(productoKey).child(aamm);
    }

    public String nodoReporteVentasProducto_8(String productoKey,String aamm) {
        return NODO_REPORTE_VENTAS_PRODUCTO + mEmpresaKey + "/" + (productoKey)+ "/" + (aamm);
    }

    // Total de ventas de la empresa, Cliente por mes 9
    public DatabaseReference refReporteVentasClientes_9(String cliente, String productoKey,String aamm) {
        return mDatabase.child(ESQUEMA_REPORTE_VENTAS_CLIENTE).child(mEmpresaKey).child(cliente).child(productoKey).child(aamm);
    }

    public String nodoReporteVentasClientes_9(String cliente,String productoKey,String aamm) {
        return NODO_REPORTE_VENTAS_CLIENTE + mEmpresaKey + "/" +cliente+ "/" + (productoKey)+ "/" + (aamm);
    }


    // Saldos x Client 10
    public DatabaseReference refSaldoTotalClientes_10(String cliente) {
        return mDatabase.child(ESQUEMA_SALDO_TOTAL).child(mEmpresaKey).child(cliente);
    }

    public String nodoSaldoTotalClientes_10(String cliente) {
        return NODO_SALDO_TOTAL + mEmpresaKey + "/" +cliente ;
    }

    // Saldos x Client 10
    public DatabaseReference refSaldoTotalClientes_10List() {
        return mDatabase.child(ESQUEMA_SALDO_TOTAL).child(mEmpresaKey);
    }

    public String nodoSaldoTotalClientes_10List() {
        return NODO_SALDO_TOTAL + mEmpresaKey ;
    }

    // Pagos 11-Retoran un puntero a las claves de pago (listado)
    public DatabaseReference refPagosListado_11(String cliente) {
        return mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(cliente);    }

    public String nodoPagosListado_11(String cliente) {
        return NODO_PAGOS + mEmpresaKey + "/" +cliente ;
    }

    public DatabaseReference refPagos(String cliente,String pagoKey) {
        return mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(cliente).child(pagoKey);
    }

    public String nodoPagos(String cliente,String pagoKey) {
        return NODO_PAGOS + mEmpresaKey + "/" +cliente +"/" +(pagoKey);
    }

    // Saldos x Client 12
    public DatabaseReference refHistorialPagosListado_12(String cliente) {
        return mDatabase.child(ESQUEMA_SALDOS_HISTORIAL).child(mEmpresaKey).child(cliente);
    }

    public String nodoHistorialPagosListado_12(String cliente) {
        return NODO_SALDOS_HISTORIAL + mEmpresaKey + "/" +cliente ;
    }


    // Favoritos
    public DatabaseReference refProductoFavoritoDeCliente(String clienteKey, String productKey) {
        return mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(clienteKey).child(productKey);
    }

    public String nodoProductoFavoritoDeCliente(String clienteKey, String productKey) {
        return NODO_FAVORITOS + mEmpresaKey + "/" + clienteKey + "/" + productKey;
    }

    public DatabaseReference refListaFavoritosXCliente(String clienteKey) {
        return mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(clienteKey);
    }

    public String nodoListaFavoritosXCliente(String clienteKey) {
        return NODO_FAVORITOS + mEmpresaKey + "/" + clienteKey;
    }

}



