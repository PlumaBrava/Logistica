package com.nextnut.logistica;

import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Almacen;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Pago;
import com.rey.material.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_ALMACENES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PAGOS;
import static com.nextnut.logistica.util.Constantes.PAGO_STATUS_INICIAL_SIN_COMPENSAR;

/**
 * A fragment representing a single Custom detail screen.
 * This fragment is either contained in a {@link CustomListActivity}
 * in two-pane mode (on tablets) or a {@link CustomDetailActivity}
 * on handsets.
 */
public class MovimientosStockFragment extends FragmentBasic {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    private com.rey.material.widget.Spinner mTipoMovimiento;
    private com.rey.material.widget.Spinner mOrigenMovimiento;
    private com.rey.material.widget.Spinner mDestinoMovimiento;
    private com.rey.material.widget.Spinner mProductoMovimiento;


    private EditText mFechaMovimiento;
    private EditText mCantidadMoviMiento;
//    private LinearLayout mBancoLinear;
    private EditText mUsuarioMovimiento;
//    private EditText mFechaCheque;
//    private EditText mNuemroCheque;
//    private EditText mEmisorCheque;
//    private ImageView mFotoCheque;
//    public ProgressView spinner;

    private String mMovimientoKey;
    private Pago mMoviemto;


    public ArrayAdapter<CharSequence> mAdapterTipoMovimiento;
    public ArrayAdapter<CharSequence> mAdapterAlmacen;
    protected ArrayList<String> mAlmacenesList = new ArrayList<>();


    CollapsingToolbarLayout appBarLayout;


    private static final String LOG_TAG = MovimientosStockFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovimientosStockFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "pago-mStorageRef:" + mStorageRef.toString());

        AppCompatActivity activity = (AppCompatActivity) this.getContext();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {

                appBarLayout.setTitle(getResources().getString(R.string.TituloMovimiento));

        }


    }

    public void startForm(){
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_multiple_choice,
                mAlmacenesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrigenMovimiento.setAdapter(adapter);
        mDestinoMovimiento.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movimientos_stock_fragment, container, false);

        mTipoMovimiento=(Spinner) rootView.findViewById(R.id.tipoMovimento);


        mOrigenMovimiento=(Spinner) rootView.findViewById(R.id.origenMovimento);
        mDestinoMovimiento=(Spinner) rootView.findViewById(R.id.destinoMovimiento);
        mProductoMovimiento=(Spinner) rootView.findViewById(R.id.productoMovimiento);

        mFechaMovimiento = (EditText) rootView.findViewById(R.id.fechaMovimiento);

        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.dayFormat));

        mFechaMovimiento.setText(sdf.format(new Date(System.currentTimeMillis())));

        mCantidadMoviMiento = (EditText) rootView.findViewById(R.id.cantidadMovimiento);

        mAdapterTipoMovimiento = ArrayAdapter.createFromResource(getContext(),
                R.array.tipoMovimiento_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapterTipoMovimiento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mTipoMovimiento.setAdapter(mAdapterTipoMovimiento);
        mTipoMovimiento.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                Log.d(LOG_TAG, "pago-parent:" + parent.toString());
                Log.d(LOG_TAG, "pago-view:" + view.toString());
                Log.d(LOG_TAG, "pago-position:" + position);
                Log.d(LOG_TAG, "pago-id:" + id);
//                Log.d(LOG_TAG, "pago-mTipoPago:getSelectedItem()" + mTipoPago.getAdapter().getItem(position));
                if (position == 1) {
//                    mBancoLinear.setVisibility(View.VISIBLE);
                } else {
//                    mBancoLinear.setVisibility(View.GONE);
                }

            }
        });

//
//        mBancoLinear = (LinearLayout) rootView.findViewById(R.id.datosBancariosLinear);
//        mBancoLinear.setVisibility(View.GONE);
        mUsuarioMovimiento = (EditText) rootView.findViewById(R.id.usuarioMovimiento);

        ValueEventListener almacenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    // Get Post object and use the values to update the UI
                    Almacen almacen = (Almacen) child.getValue(Almacen.class);
                    // [START_EXCLUDE]
                    Log.i(LOG_TAG, "almacen.getNombre() " + almacen.getNombre());
                    mAlmacenesList.add(almacen.getNombre());
                }
                startForm();
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.i(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getContext(), "Failed to load Products.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mDatabase.child(ESQUEMA_ALMACENES).child(mEmpresaKey).addListenerForSingleValueEvent(almacenListener);


        return rootView;
    }


    @Override
    public void savePhoto(Bitmap bitmap) {
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (mUserKey != null) {
            mUsuarioMovimiento.setText(mUsuario.getUsername());

            if (mMovimientoKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
                Log.i("pago", "mMovimientoKey " + mMovimientoKey);
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

//                    mCustomName.setText(cliente.getNombre());
//                    mLastName.setText(cliente.getApellido());
                        mCurrentPhotoPath = cliente.getFotoCliente();
                        // todo: reemplazar por un listado de telefonos.
//                        mIdContact=null;// aqui deber ir la referencia al Id Android de contacto.
//
//                        mCuit.setText(cliente.getReponsable());
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

//                    Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
//                    Picasso.with(getContext())
//                            .load(mCurrentPhotoPath)
//                            .placeholder(drawable)
//                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                            .into(mFotoCheque);


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


        long chequeDate = 0;

//        if (!TextUtils.isEmpty(mFechaCheque.getText().toString())) {
//            Log.i("pago", "mFechaCheque no nula");
//            Log.i("pago", "mFechaCheque day format " + getResources().getString(R.string.dayFormat));
//            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.dayFormat));
//            Date date = null;
//            try {
//                date = sdf.parse(mFechaCheque.getText().toString());
//                chequeDate = date.getTime();
//                Log.i("pago", "mFechaCheque no nula en try " + chequeDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        mMoviemto = new Pago(
//                mClienteKey,
//                mCliente,
//                mTipoPago.getSelectedItem().toString(),
//                Double.valueOf(mCantidadMoviMiento.getText().toString()),
//                mUsuarioMovimiento.getText().toString(),
//                chequeDate,
//                mEmisorCheque.getText().toString(),
//                mCurrentPhotoPath,
//                mNuemroCheque.getText().toString(),
//                mUserKey
//        );

        if (mMovimientoKey == null) {
            mMovimientoKey =  refPagosListado_11(mClienteKey,String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).push().getKey();
//            mMovimientoKey = mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).push().getKey();
        }

        refSaldoTotalClientes_10(mClienteKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CabeceraOrden saldo = dataSnapshot.getValue(CabeceraOrden.class);


                Map<String, Object> pagoValues = mMoviemto.toMap();
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put(nodoPagosInicialSinCompensar(mClienteKey, mMovimientoKey), pagoValues);

                Log.i(LOG_TAG, "Pagos saldos = "+saldo.getTotales().getSaldo());
                saldo.getTotales().setMontoPagado(saldo.getTotales().getMontoPagado() + mMoviemto.getMonto());
                saldo.getTotales().setSaldo(saldo.getTotales().getSaldo() - mMoviemto.getMonto());
                Log.i(LOG_TAG, "Pagos saldos actualizado= "+saldo.getTotales().getSaldo());
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
            @Override // Caso de error en la lectura del saldo
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if (TextUtils.isEmpty(mCantidadMoviMiento.getText().toString())) {
            mCantidadMoviMiento.setError(getResources().getString(R.string.Required));
            isvalid = false;
        } else {
            mCantidadMoviMiento.setError(null);
        }
//        if (mTipoPago.getSelectedItem().toString().equals(((String[]) getResources().getStringArray(R.array.tipoDePago_array))[1])) {
//            // Valida el Banco
//            if (TextUtils.isEmpty(mUsuarioMovimiento.getText().toString())) {
//                mUsuarioMovimiento.setError(getResources().getString(R.string.Required));
//                isvalid = false;
//            } else {
//                mUsuarioMovimiento.setError(null);
//            }


//        }

        return isvalid;
    }

    public void setEditingEnabled(boolean enabled) {
        mFechaMovimiento.setEnabled(enabled);
        mCantidadMoviMiento.setEnabled(enabled);
//        mTipoPago.setEnabled(enabled);
//        mUsuarioMovimiento.setEnabled(enabled);
//        mFechaCheque.setEnabled(enabled);
//        mEmisorCheque.setEnabled(enabled);
//        mNuemroCheque.setEnabled(enabled);


    }


}



