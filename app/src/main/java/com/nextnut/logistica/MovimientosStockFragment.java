package com.nextnut.logistica;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Almacen;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Pago;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.DetalleViewHolder;
import com.rey.material.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_ALMACENES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_STOCK_MOVIMIENTOS;
import static com.nextnut.logistica.util.Constantes.EXTRA_KEYLIST;
import static com.nextnut.logistica.util.Constantes.PAGO_STATUS_INICIAL_SIN_COMPENSAR;
import static com.nextnut.logistica.util.Constantes.REQUEST_PRODUCT;

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
private RecyclerView mDetalleRecyclerView;
private FirebaseRecyclerAdapter<Detalle, DetalleViewHolder> mDetalleAdapter;
    private String mMovimientoKey;
    private Pago mMoviemto;


    public ArrayAdapter<CharSequence> mAdapterTipoMovimiento;
    public ArrayAdapter<CharSequence> mAdapterAlmacen;
    protected ArrayList<String> mAlmacenesList = new ArrayList<>();
    protected ArrayList<String> mProductoList = new ArrayList<>();
    protected  ValueEventListener productListener;

    CollapsingToolbarLayout appBarLayout;
    private Button mBotonSeleccionProduto;
    private ArrayList<String> mKeyList;

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

        ArrayAdapter adapterProductos = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_single_choice,
                mProductoList);
        adapterProductos .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mProductoMovimiento.setAdapter(adapterProductos);

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
        mBotonSeleccionProduto = (Button)rootView.findViewById(R.id.botonSelecionProducto) ;
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
                mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).addListenerForSingleValueEvent(productListener);
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

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    // Get Post object and use the values to update the UI
                    Producto producto = (Producto) child.getValue(Producto.class);
                    // [START_EXCLUDE]
                    Log.i(LOG_TAG, "producto.getNombre() " + producto.getNombreProducto());
                    mProductoList.add(producto.getNombreProducto());




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

        mBotonSeleccionProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ProductSectionActivity.class);
                putExtraFirebase_Fragment(intent);

                intent.putExtra(EXTRA_KEYLIST, mKeyList);
                Log.d(LOG_TAG, "mKeyList: " + mKeyList.toString());
//                intent.putExtra("ITEM", mItem);
                getActivity().startActivityForResult(intent, REQUEST_PRODUCT);

            }

        });

        mDetalleRecyclerView = (RecyclerView) rootView.findViewById(R.id.product_list_customOrder);
        mKeyList = new ArrayList<String>();
        mMovimientoKey="xx11qq";
        Query productosAsignados = mDatabase.child(ESQUEMA_STOCK_MOVIMIENTOS).child(mEmpresaKey).child(mMovimientoKey);
        mDetalleAdapter = new FirebaseRecyclerAdapter<Detalle, DetalleViewHolder>(Detalle.class, R.layout.order_detail_item,
                DetalleViewHolder.class, productosAsignados) {
            @Override
            protected void populateViewHolder(final DetalleViewHolder viewHolder, final Detalle model, final int position) {
                final DatabaseReference detalleRef = getRef(position);
//                if (mCabeceraOrden.getEstado() >= ORDEN_STATUS_EN_DELIVERY) {
//                    Log.i(LOG_TAG, "adapter:detalleRef:ORDEN_STATUS_EN_DELIVERY " + mCabeceraOrden.getEstado());
//                    viewHolder.setDeliveryState();
//                    viewHolder.mTextViewPrecioDelivery.setVisibility(View.VISIBLE);
//                    viewHolder.mTextcantidadDelivery.setVisibility(View.VISIBLE);
//                    viewHolder.mTextToalDelivery.setVisibility(View.VISIBLE);
//                    viewHolder.mfavorito.setVisibility(View.GONE);
//
//                } else {
//                    Log.i(LOG_TAG, "adapter:detalleRef:NO ORDEN_STATUS_EN_DELIVERY " + mCabeceraOrden.getEstado());
//                }
//
//                emptyView.setVisibility(View.GONE);
                Log.i(LOG_TAG, "adapter:detalleRef: " + detalleRef.toString());

                // Set click listener for the whole post view
                final String productKey = detalleRef.getKey();

                viewHolder.mfavorito.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
//                                                                viewHolder.mfavorito.setChecked(!viewHolder.mfavorito.isChecked());
                                                                Log.d(LOG_TAG, "favorito viewHolder nombre: " + viewHolder.mTextViewNombre);
                                                                Log.d(LOG_TAG, "favorito model-Producto: " + model.getProducto().getNombreProducto());
                                                                Log.d(LOG_TAG, "favorito  productkey: " + productKey);
//                                                                onFavorite(viewHolder, model, productKey);

                                                            }
                                                        }
                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View view) {
                                Log.d(LOG_TAG, "adapter:onClick model: " + model.getProducto().getNombreProducto());
//                                mDetalleAnterior = model;
//                                showDialogNumberPicker(productKey);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Detalle model, int position) {
                // TODO: ACTUALIZAR TOTALES !!!
//                mDetalleAnterior = model;
//                if (mCabeceraOrden.getEstado() >= ORDEN_STATUS_EN_DELIVERY) {
//                } else {
//                    abmDetalleDeOrden(0.0, getRef(position).getKey(), model);
////                borrarProductoDeOrden(getRef(position).getKey(), model);
//                    Log.d(LOG_TAG, " onItemDismissHolder: " + model.getProducto().getNombreProducto() + " pos: " + position);
//                    Log.d(LOG_TAG, " onItemDismissHolder: " + " key: " + getRef(position).getKey());
//                }
//                mDetalleRecyclerView.setEnabled(false);
            }

            @Override
            protected void onItemAcceptedHolder(Detalle model, int position) {
                Log.d(LOG_TAG, "onItemAcceptedHolder: " + model.getProducto().getNombreProducto() + " pos: " + position);
            }
        };


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
//                ValueEventListener customListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.i("producto", "onDataChange: ");
//                        Log.i("producto", "onDataChange: count " + dataSnapshot.getChildrenCount());
//                        // Get Post object and use the values to update the UI
//                        Cliente cliente = (Cliente) dataSnapshot.getValue(Cliente.class);
//                        // [START_EXCLUDE]
//                        Log.i("producto", "onDataChange:cliente.getNombre() " + cliente.getNombre());
//
////                    mCustomName.setText(cliente.getNombre());
////                    mLastName.setText(cliente.getApellido());
//                        mCurrentPhotoPath = cliente.getFotoCliente();
//                        // todo: reemplazar por un listado de telefonos.
////                        mIdContact=null;// aqui deber ir la referencia al Id Android de contacto.
////
////                        mCuit.setText(cliente.getReponsable());
////                        mIva.setText(Double.toString(cliente.getIva()));
////                        mSpecial.setChecked(cliente.getEspecial());
////                        Log.i("TelefonosArrayAdapter", "telefonos .size()antes" + telefonos.entrySet().size());
////
////                        telefonos=cliente.getTelefonos();
////                        Log.i("TelefonosArrayAdapter", "telefonos .size()" + telefonos.entrySet().size());
//////                        telefonos.clear();
////                        for(Map.Entry<String,String> entry : cliente.getTelefonos().entrySet()) {
////                            telefonos.put(entry.getKey(),entry.getValue());
////                            Log.i("TelefonosArrayAdapter", "telefonos .key" + entry.getKey());
////                            Log.i("TelefonosArrayAdapter", "telefonos .value" + entry.getValue());
////                        }
////                        mAdapterTelefonos.swap(telefonos);
//////                        mListadeTelefonos.invalidate();
//////                        mAdapterTelefonos.notifyDataSetChanged();
//////                        mAdapterTelefonos.notifyAll();
////
////                        if (mIdContact != null){
////                            button.setBackgroundColor(Color.GREEN);
////                            button.setText(getUserName(getContext() ,mIdContact));
////                        }
//
////                    Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
////                    Picasso.with(getContext())
////                            .load(mCurrentPhotoPath)
////                            .placeholder(drawable)
////                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
////                            .into(mFotoCheque);
//
//
//                        if (appBarLayout != null) {
//                            {
//                                appBarLayout.setTitle(cliente.getNombre() + " " + cliente.getApellido());
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Getting Post failed, log a message
//                        Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
//                        // [START_EXCLUDE]
//                        Toast.makeText(getContext(), "Failed to load Products.",
//                                Toast.LENGTH_SHORT).show();
//                        // [END_EXCLUDE]
//                    }
//                };
//                Log.i(LOG_TAG, " mDatabase: " + mDatabase);
//                Log.i(LOG_TAG, " ESQUEMA_PAGOS: " + ESQUEMA_PAGOS);
//                Log.i(LOG_TAG, " mEmpresaKey: " + mEmpresaKey);
//                Log.i(LOG_TAG, " mEmpresaKey: " + mEmpresaKey);
//                mDatabase.child(ESQUEMA_PAGOS).child(mEmpresaKey).child(mEmpresaKey).addListenerForSingleValueEvent(customListener);
//

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


    public void agregaProductoAlMovimiento(final Double cantidad, String productoKey, Detalle detalle) {


        Log.i(LOG_TAG, "abmDetalleDeOrden cantidad " + cantidad + " productokey " + productoKey + " Producto " + detalle.getProducto().getNombreProducto());
        if (hayTareaEnProceso()) {
            return;
        }
        mKeyList.add(productoKey);
//        mCantidadDato = cantidad; // Es la nueva cantidad que queremos tener
//        mproductKeyDato = productoKey;
//        mDetalleDato = detalle; // tiene los valores del detalle que se quiere modificar
//
//
//        readBlockCabeceraOrden(mCabeceraOrden.getNumeroDeOrden());
//        readBlockTotalInicial(productoKey);
//        readBlockProductosEnOrdenes(productoKey, mCabeceraOrden.getNumeroDeOrden());
//        Log.i(LOG_TAG, "mCabeceraOrdenTask.get(0) " + mCabeceraOrdenTask.get(0));
//        Log.i(LOG_TAG, "mTotalInicialTask.get(0) " + mTotalInicialTask.get(0));
//        Log.i(LOG_TAG, "mProductosEnOrdenesTask " + mProductosEnOrdenesTask);
//
//
//        Task<Void> allTask;
//        allTask = Tasks.whenAll(mCabeceraOrdenTask.get(0), mTotalInicialTask.get(0), mProductosEnOrdenesTask);
//        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                CabeceraOrden cabeceraOrden;
//                Detalle totalInicialDetalle;
//                PrductosxOrden prductosxOrden;
//                Detalle nuevoDetalleOrden = mDetalleDato.copy();
//
//                DataSnapshot dataCabecera = (DataSnapshot) mCabeceraOrdenTask.get(0).getResult();
//
//                // do something with db data?
//                if (dataCabecera.exists()) {
//                    dataCabecera.getKey();
//                    cabeceraOrden = dataCabecera.getValue(CabeceraOrden.class);
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask =" + dataCabecera.getKey() + "- monto cab" + dataCabecera.getValue(CabeceraOrden.class).getTotales().getMontoEnOrdenes());
//
//                } else {
//                    //Debe existir, de lo contrario es un error.
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask = NuLL- ");
//                    return;
//                }
//
//
//                DataSnapshot dataDetalleTotalInicial = (DataSnapshot) mTotalInicialTask.get(0).getResult();
//
//                // do something with db data?
//                if (dataDetalleTotalInicial.exists()) {
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask key =" + dataDetalleTotalInicial.getKey());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetalleTotalInicial.hasChildren());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetalleTotalInicial.getChildrenCount());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.exists() =" + dataDetalleTotalInicial.exists());
//
//                    dataDetalleTotalInicial.getKey();
//                    totalInicialDetalle = dataDetalleTotalInicial.getValue(Detalle.class);
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask =" + dataDetalleTotalInicial.getKey() + "- nombre Producto" + dataDetalleTotalInicial.getValue(Detalle.class).getProducto().getNombreProducto()
//                            + "- cantidad Orden" + dataDetalleTotalInicial.getValue(Detalle.class).getCantidadOrden());
//
//                } else {
//                    // si no existe el producto en el listado se crea.
//                    totalInicialDetalle = new Detalle(0.0, mDetalleDato.getProducto(), null);
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask = NuLL- ");
//
//                }
//
//
////                DataSnapshot dataProductosEnOrdenes = (DataSnapshot) mProductosEnOrdenesTask.getResult();
////
////
////                if (dataProductosEnOrdenes.exists()) {
////                    dataProductosEnOrdenes.getKey();
////                    prductosxOrden = dataProductosEnOrdenes.getValue(PrductosxOrden.class);
//////                    data.getValue(Detalle.class);
////                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask key =" + dataProductosEnOrdenes.getKey());
////                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.hasChildren() =" + dataProductosEnOrdenes.hasChildren());
////                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.hasChildren() =" + dataProductosEnOrdenes.getChildrenCount());
////                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.exists() =" + dataProductosEnOrdenes.exists());
////                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask NombreProd =" + dataProductosEnOrdenes.getValue(PrductosxOrden.class).getDetalle().getProducto().getNombreProducto());
////
////
////                } else {
//                prductosxOrden = new PrductosxOrden(mCabeceraOrden.getCliente(), new Detalle(0.0, mDetalleDato.getProducto(), null));
//                Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask = NuLL- ");
////                }
//
//                // Todo: procesar los datos
//                // liberar los semaforos para grabar
//                cabeceraOrden.liberar();
//                nuevoDetalleOrden.liberar();
//                totalInicialDetalle.liberar();
//                prductosxOrden.liberar();
//                if (mDetalleDato.getCantidadOrden() == 0 && mCantidadDato != 0.0)//ingresa un nuevo producto
//                {
//                    Log.i(LOG_TAG, "abmDetalleDeOrden ingresa un nuevo producto ");
//                    //Actualizo cabecera de Orden 1B ( Ingresa producto en orden suma 1 a la cantidad de Items y ajusta el monto total
//                    // Se ajusta Totales
//                    cabeceraOrden.ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());
//
//                    //Actualizo productos por Orden 5. Es necesario bloquear este esquema? o se puede planchar directamente???
//                    prductosxOrden.getDetalle().ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());
//
//                    //Actualizo Detalle de Orden para 1C y 4
//                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);
//
//                    //Actualizo Total Inicial (3).
//                    totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);
//
//                    mKeyList.add(mproductKeyDato);
//                } else if (mDetalleDato.getCantidadOrden() > 0 && mCantidadDato > 0)// Se modifica una cantidad
//                {
//                    Log.i(LOG_TAG, "abmDetalleDeOrden Se modifica una cantidad ");
//
//                    //Actualizo cabecera de Orden 1B ( Ingresa producto en orden suma 1 a la cantidad de Items y ajusta el monto total
//                    // Se ajusta Totales
//                    cabeceraOrden.modificarCantidadProductoEnOrden(mCantidadDato, mDetalleDato);
//
//                    //Actualizo productos por Orden 5. Es necesario bloquear este esquema? o se puede planchar directamente???
//                    //Idem al anterior, se plantacha
//                    prductosxOrden.getDetalle().ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());
//
//                    //Actualizo Detalle de Orden para 1C y 4
//                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);
//
//                    //Actualizo Total Inicial (3).
//                    totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);
//                } else if (mCantidadDato == 0.0)// se borra un producto
//                {
//                    Log.i(LOG_TAG, "abmDetalleDeOrden se borra un producto ");
//                    mKeyList.remove(mproductKeyDato);
//                    // modifico el detalle nuevo para impactar totalInicialDetalle.
//                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);
//                    // Actualizo Cabecera
//                    cabeceraOrden.getTotales().sacarProductoDeOrden(mDetalleDato);
//                    //Actualizo Total Inicial (3).
//                    if (mDetalleDato.getCantidadOrden().equals(totalInicialDetalle.getCantidadOrden()))
//                        totalInicialDetalle = null;
//                    else {
//                        totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);
//                    }
//                    //Actualizo Detalle de Orden para 1C y 4
//                    nuevoDetalleOrden = null;
//                    //Actualizo productos por Orden 5
//                    prductosxOrden = null;
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mKeyList " + mKeyList.toString());
//
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mproductKeyDato " + mproductKeyDato);
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mKeyList " + mKeyList.toString());
//
//                }
//
//                // Todo: Escribir en la Firebase simultaneament.
//
//                mCabeceraOrden.getTotales().setCantidadDeProductosDiferentes(cabeceraOrden.getTotales().getCantidadDeProductosDiferentes());
//                mCabeceraOrden.getTotales().setMontoEnOrdenes(cabeceraOrden.getTotales().getMontoEnOrdenes());
//
//
//                Map<String, Object> cabeceraOrdenValues = null;
//                Map<String, Object> detalleOrdenValues = null;
//                Map<String, Object> totalInicialDetalleValues = null;
//                Map<String, Object> prductosxOrdenValues = null;
//
//                if (cabeceraOrden != null) {
//                    cabeceraOrdenValues = cabeceraOrden.toMap();
//                }
//                if (nuevoDetalleOrden != null) {
//                    detalleOrdenValues = nuevoDetalleOrden.toMap();
//                }
//                if (totalInicialDetalle != null) {
//                    totalInicialDetalleValues = totalInicialDetalle.toMap();
//                }
//                if (prductosxOrden != null) {
//                    prductosxOrdenValues = prductosxOrden.toMap();
//                }
//
//                Map<String, Object> childUpdates = new HashMap<>();
//
//
//                Log.i(LOG_TAG, "abmDetalleDeOrden refCabeceraOrden_1B " + refCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()).toString());
//                Log.i(LOG_TAG, "abmDetalleDeOrden refCabeceraOrden_2 " + refCabeceraOrden_2(ORDEN_STATUS_INICIAL, mCabeceraOrden.getNumeroDeOrden()).toString());
//                Log.i(LOG_TAG, "abmDetalleDeOrden refDetalleOrden_1C " + refDetalleOrden_1C(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato).toString());
//                Log.i(LOG_TAG, "abmDetalleDeOrden refDetalleOrden_4 " + refDetalleOrden_4(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato).toString());
//                Log.i(LOG_TAG, "abmDetalleDeOrden refProductosXOrdenInicial_5 " + refProductosXOrdenInicial_5(mproductKeyDato, mCabeceraOrden.getNumeroDeOrden()).toString());
//                Log.i(LOG_TAG, "abmDetalleDeOrden refTotalInicial_3 " + refTotalInicial_3(mproductKeyDato).toString());
//
//
//
///*1B*/
//                childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);
///*2 */
//                childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_INICIAL, mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);
///*1c*/
//                childUpdates.put(nodoDetalleOrden_1C(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
///*4 */
//                childUpdates.put(nodoDetalleOrden_4(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
///*5 */
//                childUpdates.put(nodoProductosXOrdenInicial_5(mproductKeyDato, mCabeceraOrden.getNumeroDeOrden()), prductosxOrdenValues);
///*3 */
//                childUpdates.put(nodoTotalInicial_3(mproductKeyDato), totalInicialDetalleValues);
//
//
//                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren-onFailure " + e.toString());
//                        liberarRecusosTomados();
//                        liberarArrayTaskConBloqueos();
//                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
//                        NumberFormat format = NumberFormat.getCurrencyInstance();
//                        mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
//                        mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
//
//                        liberarArrayTaskCasoExitoso();
//
//                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren - OnCompleteListener task.isSuccessful():" + task.isSuccessful());
//
//                    }
//                });
//
//
//            }
//        });
//        allTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i(LOG_TAG, "abmDetalleDeOrden addOnFailureListener= allTask" + e.toString());
//                muestraMensajeEnDialogo("No se pudo bloquear");
//                liberarRecusosTomados();
//                liberarArrayTaskConBloqueos();
//                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR));
//            }
//        });


    }



}



