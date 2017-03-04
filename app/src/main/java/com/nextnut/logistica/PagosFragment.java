package com.nextnut.logistica;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Pago;
import com.nextnut.logistica.util.MakeCall;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_PAGOS;
import static com.nextnut.logistica.util.Constantes.IMAGENES_PAGOS;
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.Imagenes.selectImage;

/**
 * A fragment representing a single Custom detail screen.
 * This fragment is either contained in a {@link CustomListActivity}
 * in two-pane mode (on tablets) or a {@link CustomDetailActivity}
 * on handsets.
 */
public class PagosFragment extends FragmentBasic {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    private EditText mCustomName;
    private EditText mLastName;
    private ImageView mImageCustomer;


    private EditText mFechaPago;
    private EditText mMontoPago;
    private com.rey.material.widget.Spinner mTipoPago;
    private LinearLayout mBancoLinear;
    private EditText mBbancoCheque;
    private EditText mFechaCheque;
    private EditText mNuemroCheque;
    private EditText mEmisorCheque;
    private ImageView mFotoCheque;
    public ProgressView spinner;
    private String mFotoChequePath;

    private String mPagoKey;


    public ArrayAdapter<CharSequence> mAdapterTipoPago;


    CollapsingToolbarLayout appBarLayout;


    private static final String LOG_TAG = PagosFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PagosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "pago-mStorageRef:" + mStorageRef.toString());

        AppCompatActivity activity = (AppCompatActivity) this.getContext();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            if (mClienteKey == null) {
                appBarLayout.setTitle(getResources().getString(R.string.NuevoPago_pagos_fragment));
            } else
                appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text) + " ");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pagos_fragment, container, false);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
//        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
//                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
//                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        mImageCustomer = (ImageView) rootView.findViewById(R.id.custom_imagen);
//        mImageCustomer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectImage(PagosFragment.this);
//            }
//        });


        mFechaPago = (EditText) rootView.findViewById(R.id.fechaDePago);

        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.dayFormat));

        mFechaPago.setText(sdf.format(new Date(System.currentTimeMillis())));

        mMontoPago = (EditText) rootView.findViewById(R.id.monto);


        mTipoPago = (Spinner) rootView.findViewById(R.id.tipoPago);

        mAdapterTipoPago = ArrayAdapter.createFromResource(getContext(),
                R.array.tipoDePago_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapterTipoPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mTipoPago.setAdapter(mAdapterTipoPago);
        mTipoPago.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                Log.d(LOG_TAG, "pago-parent:" + parent.toString());
                Log.d(LOG_TAG, "pago-view:" + view.toString());
                Log.d(LOG_TAG, "pago-position:" + position);
                Log.d(LOG_TAG, "pago-id:" + id);
                Log.d(LOG_TAG, "pago-mTipoPago:getSelectedItem()" + mTipoPago.getSelectedItem().toString());
                Log.d(LOG_TAG, "pago-mTipoPago:getSelectedItem()" + mTipoPago.getAdapter().getItem(position));
                if (position == 1) {
                    mBancoLinear.setVisibility(View.VISIBLE);
                } else {
                    mBancoLinear.setVisibility(View.GONE);
                }

            }
        });


        mBancoLinear = (LinearLayout) rootView.findViewById(R.id.datosBancariosLinear);
        mBancoLinear.setVisibility(View.GONE);
        mBbancoCheque = (EditText) rootView.findViewById(R.id.banco);
        mFechaCheque = (EditText) rootView.findViewById(R.id.fechadeCheque);
        mFechaCheque.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Dialog.Builder builder = null;

//                builder = new DatePickerDialog.Builder(isLightTheme ? R.style.Material_App_Dialog_DatePicker_Light :  R.style.Material_App_Dialog_DatePicker){
                builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
//                        String date = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                        String date = dialog.getFormattedDate(new SimpleDateFormat(getResources().getString(R.string.dayFormat)));
                        Toast.makeText(getContext(), "Date is " + date, Toast.LENGTH_SHORT).show();
                        mFechaCheque.setText(date);
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);

            }
        });

        mNuemroCheque = (EditText) rootView.findViewById(R.id.numeroDeCheque);
        mEmisorCheque = (EditText) rootView.findViewById(R.id.emisorDeCheque);
        mFotoCheque = (ImageView) rootView.findViewById(R.id.foto_cheque);
        mFotoCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(PagosFragment.this);


            }

        });
        Picasso.with(getActivity())
                .load(mFotoChequePath)
                .placeholder(R.drawable.ic_action_action_redeem)
                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .into(mFotoCheque);

        spinner = (ProgressView) rootView.findViewById(R.id.progressBarCheque);
        //  spinner.setVisibility(View.GONE);


        return rootView;
    }


    @Override
    public void savePhoto(Bitmap bitmap) {
        if (mPagoKey == null) {
            mPagoKey = refPagosListado_11("mClienteKey").push().getKey();
        }
        StorageReference ImagenRef = mStorageRef.child(IMAGENES_PAGOS).child(mEmpresaKey).child(mPagoKey);
        uploadImagen(bitmap, ImagenRef, mFotoCheque, spinner);
        Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath" + mCurrentPhotoPath);

    }

    ;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MakeCall.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    if (mCustomId!=null){
//                        button.setBackgroundColor(Color.GREEN);
//                    }


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (mClienteKey != null) {
            mCustomName.setText(mCliente.getNombre());
            mLastName.setText(mCliente.getApellido());

            if (mCliente.getFotoCliente() == null) {
                mImageCustomer.setBackgroundColor(Color.BLUE);
            } else {
                mImageCustomer.setBackgroundColor(Color.TRANSPARENT);
            }
            Picasso.with(getActivity())
                    .load(mCliente.getFotoCliente())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                    .into(mImageCustomer);
        }


        if (mPagoKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
            Log.i("pago", "mPagoKey " + mPagoKey);
            // [START post_value_event_listener]
            ValueEventListener customListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("producto", "onDataChange: ");
                    Log.i("producto", "onDataChange: count " + dataSnapshot.getChildrenCount());
                    // Get Post object and use the values to update the UI
                    Cliente cliente = (Cliente) dataSnapshot.getValue(Cliente.class);
                    // [START_EXCLUDE]
                    Log.i("producto", "onDataChange:cliente.getNombre() " + cliente.getNombre());

                    mCustomName.setText(cliente.getNombre());
                    mLastName.setText(cliente.getApellido());
                    mCurrentPhotoPath = cliente.getFotoCliente();
                    // todo: reemplazar por un listado de telefonos.
//                        mIdContact=null;// aqui deber ir la referencia al Id Android de contacto.
//
//                        mCuit.setText(cliente.getCuit());
//                        mIva.setText(Double.toString(cliente.getIva()));
//                        mSpecial.setChecked(cliente.getEspecial());
//                        Log.i("TelefonosArrayAdapter", "telefonos .size()antes" + telefonos.entrySet().size());
//
//                        telefonos=cliente.getTelefonos();
//                        Log.i("TelefonosArrayAdapter", "telefonos .size()" + telefonos.entrySet().size());
////                        telefonos.clear();
//                        for(Map.Entry<String,String> entry : cliente.getTelefonos().entrySet()) {
//                            telefonos.put(entry.getKey(),entry.getValue());
//                            Log.i("TelefonosArrayAdapter", "telefonos .key" + entry.getKey());
//                            Log.i("TelefonosArrayAdapter", "telefonos .value" + entry.getValue());
//                        }
//                        mAdapterTelefonos.swap(telefonos);
////                        mListadeTelefonos.invalidate();
////                        mAdapterTelefonos.notifyDataSetChanged();
////                        mAdapterTelefonos.notifyAll();
//
//                        if (mIdContact != null){
//                            button.setBackgroundColor(Color.GREEN);
//                            button.setText(getUserName(getContext() ,mIdContact));
//                        }

                    Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
                    Picasso.with(getContext())
                            .load(mCurrentPhotoPath)
                            .placeholder(drawable)
                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                            .into(mFotoCheque);


                    if (appBarLayout != null) {
                        {
                            appBarLayout.setTitle(cliente.getNombre() + " " + cliente.getApellido());
                        }
                    }

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
            Log.i(LOG_TAG, " mDatabase: " + mDatabase);
            mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(mClienteKey).addListenerForSingleValueEvent(customListener);


        } else {
            Log.i("producto", "onActivityCreated: mProductKey: Null");

        }

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (appBarLayout != null) {
//            if (mItem==0){
//                appBarLayout.setTitle(getResources().getString(R.string.custom_new)+ mItem);
//            }else
//                appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text)+" "+ mItem);
        }
    }

    public void verificationAndsave() {

        if (verification()) {//validar formulario
            Log.i("pago", "verificacion exitosa");
            savePago();

        } else {

            Log.i("pago", "verificacion  no exitosa");
            setEditingEnabled(true);
        }
    }

    public void savePago() {
        Log.i("pago", "savePago");


        String pagoKey = null;
        long chequeDate = 0;

        if (!TextUtils.isEmpty(mFechaCheque.getText().toString())) {
            Log.i("pago", "mFechaCheque no nula");
            Log.i("pago", "mFechaCheque day format " + getResources().getString(R.string.dayFormat));
            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.dayFormat));
            Date date = null;
            try {
                date = sdf.parse(mFechaCheque.getText().toString());
                chequeDate = date.getTime();
                Log.i("pago", "mFechaCheque no nula en try " + chequeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Pago pago = new Pago(
                mClienteKey,
                mCliente,
                mTipoPago.getSelectedItem().toString(),
                Double.valueOf(mMontoPago.getText().toString()),
                mBbancoCheque.getText().toString(),
                chequeDate,
                mEmisorCheque.getText().toString(),
                mCurrentPhotoPath,
                mNuemroCheque.getText().toString(),
                mUserKey
        );

        if (pagoKey == null) {
            pagoKey = mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).push().getKey();
        }

        Map<String, Object> pagoValues = pago.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(nodoPagos(mClienteKey, pagoKey), pagoValues);

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("pago", "onComplete");

//                setEditingEnabled(true);
//                getActivity().onBackPressed();
            }
        });

        setEditingEnabled(true);
        getActivity().onBackPressed();
    }


    public Boolean verification() {
        Boolean isvalid = true;


//        private long fechaDePago;
//        private String usuarioCreador;
//        private Double monto;
//        private String tipoDePago;
//        private String chequeBanco;
//        private String chequeNumero;
//        private String chequeEmisor;
//        private String chequeFotoPath;
//        private long chequeFecha;
//        private int estado;
//        private Boolean semaforo=true;

//        if(mClienteKey==null){
//            isvalid=false;
//        }
//        if(mCliente==null){
//            isvalid=false;
//        }

//        if(mTipoPago.getAdapter(Integer.valueOf(mTipoPago.getSelectedItem().toString())))

        // Valida el Monto
        if (TextUtils.isEmpty(mMontoPago.getText().toString())) {
            mMontoPago.setError(getResources().getString(R.string.Required));
            isvalid = false;
        } else {
            mMontoPago.setError(null);
        }
        if (mTipoPago.getSelectedItem().toString().equals(((String[]) getResources().getStringArray(R.array.tipoDePago_array))[1])) {
            // Valida el Banco
            if (TextUtils.isEmpty(mBbancoCheque.getText().toString())) {
                mBbancoCheque.setError(getResources().getString(R.string.Required));
                isvalid = false;
            } else {
                mBbancoCheque.setError(null);
            }


        }
//            if( mCustomName.getText().toString().equals(null))
//        {
//            isvalid =false;
//            mCustomName.setBackgroundColor(Color.RED);
//        } else {
//            mCustomName.setBackgroundColor(Color.TRANSPARENT);
//        }
//
//
//        if( mLastName.getText().toString().equals(""))
//        {
//            isvalid =false;
//            mLastName.setBackgroundColor(Color.RED);
//        } else {
//            mLastName.setBackgroundColor(Color.TRANSPARENT);
//        }

        return isvalid;
    }

    public void setEditingEnabled(boolean enabled) {
        mFechaPago.setEnabled(enabled);
        mMontoPago.setEnabled(enabled);
        mTipoPago.setEnabled(enabled);
        mBbancoCheque.setEnabled(enabled);
        mFechaCheque.setEnabled(enabled);
        mEmisorCheque.setEnabled(enabled);
        mNuemroCheque.setEnabled(enabled);

//        if (enabled) {
//            mfab.setVisibility(View.VISIBLE);
//        } else {
//            mfab.setVisibility(View.GONE);
//        }
    }


}



