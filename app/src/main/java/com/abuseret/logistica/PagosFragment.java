package com.abuseret.logistica;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.abuseret.logistica.modelos.CabeceraOrden;
import com.abuseret.logistica.modelos.CabeceraPicking;
import com.abuseret.logistica.modelos.Pago;
import com.abuseret.logistica.modelos.Totales;
import com.abuseret.logistica.util.MakeCall;
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

import static com.abuseret.logistica.util.Constantes.EXTRA_PAGO;
import static com.abuseret.logistica.util.Constantes.EXTRA_PAGO_KEY;
import static com.abuseret.logistica.util.Constantes.IMAGENES_PAGOS;
import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.abuseret.logistica.util.Constantes.PAGO_STATUS_INICIAL_SIN_COMPENSAR;
import static com.abuseret.logistica.util.Constantes.PICKING_STATUS_DELIVERY;
import static com.abuseret.logistica.util.Imagenes.dimensiona;
import static com.abuseret.logistica.util.Imagenes.selectImage;

public class PagosFragment extends FragmentBasic {


    private EditText mCustomName;
    private EditText mLastName;
    private ImageView mImageCustomer;


    private EditText mFechaPago;
    private EditText mOrdenPickingPago;
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

    private String mPagoKey = null;
    private Pago mPago = null;
    private long mNroPickingAlmacenado;
    private CabeceraPicking datosCabeceraPickingSeleccionada;


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
        mPago = getArguments().getParcelable(EXTRA_PAGO);
        mPagoKey = getArguments().getString(EXTRA_PAGO_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pagos_fragment, container, false);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mCustomName.setEnabled(false);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        mLastName.setEnabled(false);
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

        mOrdenPickingPago = (EditText) rootView.findViewById(R.id.ordenPicking);
        mOrdenPickingPago.setEnabled(false);




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
            mPagoKey = refPagosListado_11(mClienteKey, String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).push().getKey();
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
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);
//
//        SharedPreferences sharedPref = getContext().getSharedPreferences() getDefaultSharedPreferences(getActivity());
//        SharedPreferences pref = getDefaultSharedPreferences(getContext());
         mNroPickingAlmacenado = sharedPref.getLong(getString(R.string.PickingOrderSeleccionada), 0);

        if (mNroPickingAlmacenado > 0) {
            refPicking_6(PICKING_STATUS_DELIVERY, mNroPickingAlmacenado).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    datosCabeceraPickingSeleccionada = dataSnapshot.getValue(CabeceraPicking.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        Log.d(LOG_TAG, "pago-parent mNroPickingAlmacenado:" + mNroPickingAlmacenado);
        if(mNroPickingAlmacenado ==0){
            mOrdenPickingPago.setVisibility(View.VISIBLE);
            Log.d(LOG_TAG, "pago-parent Picking Seleccionado: == 0 " + mNroPickingAlmacenado);
        }else{
            mOrdenPickingPago.setText(String.valueOf (mNroPickingAlmacenado));
        }


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

            Log.i("producto", "onDataChange: ");
            // [START_EXCLUDE]
            Log.i("producto", "onDataChange:cliente.getNombre() " + mPago.getCliente().getNombre());

            mCustomName.setText(mPago.getCliente().getNombre());
            mLastName.setText(mPago.getCliente().getApellido());
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy");


            mFechaPago.setText(sfd.format(new Date(mPago.getFechaDePago())));
            mFechaPago.getText();
            mMontoPago.setText(mPago.getMonto().toString());
            mTipoPago.setSelection(mAdapterTipoPago.getPosition(mPago.getTipoDePago()));
            mOrdenPickingPago.setText(String.valueOf( mPago.getNumeroDePickingOrden()));
            if (mTipoPago.getSelectedItemPosition() == 1) {
                mBancoLinear.setVisibility(View.VISIBLE);
                mBbancoCheque.setText(mPago.getChequeBanco());


                mFechaCheque.setText(sfd.format(new Date(mPago.getChequeFecha())));
                mNuemroCheque.setText(mPago.getChequeNumero());
                mEmisorCheque.setText(mPago.getChequeEmisor());


                Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
                Picasso.with(getContext())
                        .load(mPago.getChequeFotoPath())
                        .placeholder(drawable)
                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .into(mFotoCheque);
            } else {
                mBancoLinear.setVisibility(View.GONE);
            }
        } else {
            Log.i(LOG_TAG, " mDatabase: " + mDatabase);


            Log.i("producto", "onActivityCreated: mProductKey: Null");

        }

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (appBarLayout != null) {
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
        mPago = new Pago(
                mClienteKey,
                mCliente,
                mTipoPago.getSelectedItem().toString(),
                Double.valueOf(mMontoPago.getText().toString()),
                mBbancoCheque.getText().toString(),
                chequeDate,
                mEmisorCheque.getText().toString(),
                mCurrentPhotoPath,
                mNuemroCheque.getText().toString(),
                mUserKey,
                mNroPickingAlmacenado
        );

        if (mPagoKey == null) {
            mPagoKey = refPagosListado_11(mClienteKey, String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).push().getKey();
//            mPagoKey = mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).push().getKey();
        }

        refSaldoTotalClientes_10(mClienteKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CabeceraOrden saldo = dataSnapshot.getValue(CabeceraOrden.class);

                if (saldo == null) {
                    Totales totales = new Totales(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                    saldo = new CabeceraOrden(mClienteKey, mCliente, ORDEN_STATUS_INICIAL, totales, mUsuario.getUsername(), 0);


                }

                Map<String, Object> pagoValues = mPago.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                if(datosCabeceraPickingSeleccionada!=null) {
                    Log.i(LOG_TAG, "Pagos saldos getMontoRecaudad = " +datosCabeceraPickingSeleccionada.getMontoRecaudado() );

                    datosCabeceraPickingSeleccionada.setMontoRecaudado(datosCabeceraPickingSeleccionada.getMontoRecaudado() + mPago.getMonto());
                    childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY,String.valueOf( datosCabeceraPickingSeleccionada.getNumeroDePickingOrden())),datosCabeceraPickingSeleccionada.toMap());
                    childUpdates.put(nodoPagosxPicking(String.valueOf( datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()),mPagoKey),pagoValues);
                }
                childUpdates.put(nodoPagosInicialSinCompensar(mClienteKey, mPagoKey), pagoValues);

                Log.i(LOG_TAG, "Pagos saldos = " + saldo.getTotales().getSaldo());
                saldo.getTotales().setMontoPagado(saldo.getTotales().getMontoPagado() + mPago.getMonto());
                saldo.getTotales().setSaldo(saldo.getTotales().getSaldo() - mPago.getMonto());
                Log.i(LOG_TAG, "Pagos saldos actualizado= " + saldo.getTotales().getSaldo());
                childUpdates.put(nodoSaldoTotalClientes_10(mClienteKey), saldo.toMap());
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public Boolean verification() {
        Boolean isvalid = true;

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

    }


}



