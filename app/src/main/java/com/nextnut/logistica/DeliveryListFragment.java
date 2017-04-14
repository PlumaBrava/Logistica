package com.nextnut.logistica;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.CabeceraPicking;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Compensacion;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Pago;
import com.nextnut.logistica.modelos.ReporteClienteProducto;
import com.nextnut.logistica.modelos.Totales;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraPickingViewHolder;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.DetalleDeliveryTotalProdutctosViewHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_DELIVEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_NRO_PICKIG;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_DELIVERED_PARA_COMPENSAR;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_EN_DELIVERY;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_PICKING;
import static com.nextnut.logistica.util.Constantes.PAGO_STATUS_INICIAL_SIN_COMPENSAR;
import static com.nextnut.logistica.util.Constantes.PICKING_STATUS_DELIVERY;
import static com.nextnut.logistica.util.Constantes.PICKING_STATUS_INICIAL;
import static com.nextnut.logistica.util.Network.isNetworkAvailable;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class DeliveryListFragment extends FragmentBasic
//        Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String ARG_ITEM_ID = "item_id";


    View emptyViewTotalProducts;
    private Query totalProductos;
    View emptyViewCustomOrder;
    private FirebaseRecyclerAdapter<CabeceraPicking, CabeceraPickingViewHolder> mPickinOrdersAdapter;
    private FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder> mCustomsOrdersCursorAdapter;
    private FirebaseRecyclerAdapter<Detalle, DetalleDeliveryTotalProdutctosViewHolder> mCursorAdapterTotalProductos;
//

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTotalProductos;
    private RecyclerView recyclerViewCustomOrderInDeliveyOrder;
    private CardView mDeliveryOrderTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private EditText mMontoRecaudado;
    private TextView mCreationDate;

    private  ImageButton mConectarImpresoraButton;
    private  ImageButton mImprimirButton;

    private ArrayList<Task> taskList = new ArrayList<Task>();
    private Task<Void> allTask;

    private DataSnapshot mProductosEnOrdenDatos;
    private DataSnapshot mProductosEnTotalPickingDatos;
    private DataSnapshot mCaberasOrdenCerrarPickingDatos;
    private DataSnapshot mDataPagoxPicking_Printing;

    private int mVentaProductosIndex;
    private int mCabeceraOrdenIndex;
//    private ArrayList<ArrayList<Detalle>> mListaDetalleDeOrdenes =new ArrayList<>();
    private ArrayList<DataSnapshot> mListaDetalleDeOrdenes =new ArrayList<>();

    private CabeceraPicking datosCabeceraPickingSeleccionada;// Tiene los datos de la cabecera de picking, cuando se hace click en un Picking



    private LinearLayout mLinearProductos;
    private LinearLayout mLinearOrders;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = DeliveryListFragment.class.getSimpleName();



    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    @Override
    public void onAttach(Context context) {
        Log.i(LOG_TAG, "onAttach " );
        super.onAttach(context);


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(LOG_TAG, "onHiddenChanged " );
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(LOG_TAG, "osetUserVisibleHint " +isVisibleToUser);
        if (isVisibleToUser) {
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, " onCreate " );

        super.onCreate(savedInstanceState);


    }

    @Override
    public void savePhoto(Bitmap bitmap) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);


        mDeliveryOrderTile = (CardView) rootView.findViewById(R.id.deliveryOrderNumbertitleID);

        mDeliveryOrderTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "mDeliveryOrderTile: on click " );
                desbloqueoPickingParaTrabajoOffLine();
//
//                mDeliveryOrderTile.setVisibility(View.GONE);
//                recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);
//                recyclerViewTotalProductos.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        mDeliveryOrderTile.setVisibility(View.GONE);


        mConectarImpresoraButton =(ImageButton ) mDeliveryOrderTile.findViewById(R.id.conectarImpresoraDelivey);
        mConectarImpresoraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mImprimirButton =(ImageButton ) mDeliveryOrderTile.findViewById(R.id.ImprimirDelivey);
        mImprimirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaDatosParaImprimirPicking();
            }
        });

        mTilePickingOrderNumber = (TextView) mDeliveryOrderTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent = (EditText) mDeliveryOrderTile.findViewById(R.id.TitlepickingOrderComents);
        mMontoRecaudado = (EditText) mDeliveryOrderTile.findViewById(R.id.montoRecaudado);

        mCreationDate = (TextView) mDeliveryOrderTile.findViewById(R.id.titlePicckinOder_creationdate);

        mLinearProductos = (LinearLayout) rootView.findViewById(R.id.linearProductos);
        mLinearOrders = (LinearLayout) rootView.findViewById(R.id.linearOrders);


       final View emptyViewPicking = rootView.findViewById(R.id.recyclerview_pickingOrders_empty);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Picking Orders
        recyclerView = (RecyclerView) rootView.findViewById(R.id.pickingOrder_list);
        recyclerView.setLayoutManager(layoutManager);

        final View emptyViewPickingOrders = rootView.findViewById(R.id.recyclerview_pickingOrders_empty);
        Query pickingOrderQuery = refPicking_6_List(PICKING_STATUS_DELIVERY);
//         = mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(PICKING_STATUS_INICIAL));
        mPickinOrdersAdapter = new FirebaseRecyclerAdapter<CabeceraPicking, CabeceraPickingViewHolder>(CabeceraPicking.class, R.layout.picking_orders_list_content,
                CabeceraPickingViewHolder.class, pickingOrderQuery) {
            @Override
            protected void populateViewHolder(CabeceraPickingViewHolder viewHolder, final CabeceraPicking model, int position) {
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emptyViewPickingOrders.setVisibility(View.GONE);

                        if (isNetworkAvailable(getContext())) {

                            datosCabeceraPickingSeleccionada = model.Copy();
                            mTilePickingComent.setText(model.getComentario());
                            mTilePickingOrderNumber.setText(String.valueOf(model.getNumeroDePickingOrden()));
                            mTilePickingComent.setVisibility(View.VISIBLE);
                            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                            mCreationDate.setText(sfd.format(new Date(model.getFechaDeCreacion())));

                            bloqueoPickingParaTrabajoOffLine();


                        } else {
                            muestraMensajeEnDialogo("Para seleccionar Necesita estar conectado a Internet");
                        }
                    }

                });
            }

            @Override
            protected void onItemDismissHolder(CabeceraPicking model, int position) {
                pasarPickingAInicial(model);
            }

            @Override
            protected void onItemAcceptedHolder(CabeceraPicking model, int position) {
                pasarPickingACerrado(model);
            }
        };


        recyclerView.setAdapter(mPickinOrdersAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPickinOrdersAdapter, ADAPTER_CABECERA_DELIVEY);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        ImageButton fab_save = (ImageButton) mDeliveryOrderTile.findViewById(R.id.save_picking_Button);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "fab_save: on click " );

                if (isNetworkAvailable(getContext())) {

                    desbloqueoPickingParaTrabajoOffLine();
                } else {
                    muestraMensajeEnDialogo("Para seleccionar Necesita estar conectado a Internet");

                }
            }
        });

        // Productos

        emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);

        recyclerViewCustomOrderInDeliveyOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);
        recyclerViewCustomOrderInDeliveyOrder.setLayoutManager(new LinearLayoutManager(getContext()));


        // Custom Orders
        emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_custom_empty);



        mLinearOrders.setVisibility(View.GONE);
        mLinearProductos.setVisibility(View.GONE);

//        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);


        long nroPickingAlmacenado = sharedPref.getLong(getString(R.string.PickingOrderSeleccionada), 0);
//        String comentarioPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderCommentSeleccionada), "");
//        String fechaPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderFechaSeleccionada), "");
        Log.i(LOG_TAG, "nroPickingAlmacenado: " + nroPickingAlmacenado);

        if (nroPickingAlmacenado > 0) {
            refPicking_6(PICKING_STATUS_DELIVERY,nroPickingAlmacenado).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    datosCabeceraPickingSeleccionada = dataSnapshot.getValue(CabeceraPicking.class);
//                    datosCabeceraPickingSeleccionada.setComentario(comentarioPickingAlmacenado);


                    mTilePickingComent.setText(datosCabeceraPickingSeleccionada.getComentario());
                    mTilePickingOrderNumber.setText(String.valueOf(datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()));
                    mTilePickingComent.setVisibility(View.VISIBLE);
                    mMontoRecaudado.setText(datosCabeceraPickingSeleccionada.getMontoRecaudado().toString());
                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    mCreationDate.setText(sfd.format(new Date(datosCabeceraPickingSeleccionada.getFechaDeCreacion())));



                    mPickinOrdersAdapter.notifyDataSetChanged();

                    muestraTotalesProductosDelivery();
                    muestraOrdenesEnDelivey();

                    mCursorAdapterTotalProductos.notifyDataSetChanged();
                    recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.VISIBLE);
                    recyclerViewTotalProductos.setVisibility(View.VISIBLE);


                    recyclerView.setVisibility(View.GONE);
                    mLinearOrders.setVisibility(View.VISIBLE);
                    mLinearProductos.setVisibility(View.VISIBLE);
                    mDeliveryOrderTile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
    }


    public void muestraOrdenesEnDelivey() {
        Query listadoOrdenesEnPickingQuery = refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
        Log.i(LOG_TAG, "muestraOrdenesEnPicking:MainActivity.getmPickingOrderSelected(): " + datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
        Log.i(LOG_TAG, "muestraOrdenesEnPicking:Query: " + listadoOrdenesEnPickingQuery.getRef().toString());

        mCustomsOrdersCursorAdapter = new FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder>(CabeceraOrden.class, R.layout.customorder_list_content,
                CabeceraViewHolder.class, listadoOrdenesEnPickingQuery) {
            @Override
            protected void populateViewHolder(final CabeceraViewHolder viewHolder, final CabeceraOrden model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                emptyViewCustomOrder.setVisibility(View.GONE);
                Log.i(LOG_TAG, "muestraOrdenesEnPicking:CabeceraRef: " + CabeceraRef.toString());

                // Set click listener for the whole post view
                final String orderKey = CabeceraRef.getKey();
                Log.i(LOG_TAG, "muestraOrdenesEnPicking:orderKey: " + orderKey);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {


                                                           }
                                                       }
                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {
                                Log.d(LOG_TAG, "muestraOrdenesEnPicking:onClick model: " + model.getCliente().getNombre());
                                Log.d(LOG_TAG, "muestraOrdenesEnPicking:onClick");
                                Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                                mCliente = model.getCliente();
                                putExtraFirebase_Fragment(intent);
                                intent.putExtra(EXTRA_CABECERA_ORDEN, model);
                                Log.d(LOG_TAG, "muestraOrdenesEnPickin CabeceraPicking Nro: " + datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
                                intent.putExtra(EXTRA_NRO_PICKIG, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(CabeceraOrden model, int position) {
                Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getNumeroDeOrden());

            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {


                Log.i(LOG_TAG, "muestraOrdenesEndelivery Modelo: Numero de orden- " + model.getNumeroDeOrden());
                pasarOrdenAEntregadaParaCompensar(model);


            }
        };


        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(mCustomsOrdersCursorAdapter, ADAPTER_CABECERA_ORDEN_EN_DELIVEY);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInDeliveyOrder);


        recyclerViewCustomOrderInDeliveyOrder.setAdapter(mCustomsOrdersCursorAdapter);
    }

    public void muestraTotalesProductosDelivery() {
        totalProductos = refPickingTotal_7_List(PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
        mCursorAdapterTotalProductos = new FirebaseRecyclerAdapter<Detalle, DetalleDeliveryTotalProdutctosViewHolder>(Detalle.class, R.layout.picking_product_item,
                DetalleDeliveryTotalProdutctosViewHolder.class, totalProductos) {
            @Override
            protected void populateViewHolder(final DetalleDeliveryTotalProdutctosViewHolder viewHolder, final Detalle model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);

                emptyViewTotalProducts.setVisibility(View.GONE);
                Log.i(LOG_TAG, "adapter:CabeceraRef: " + CabeceraRef.toString());

                // Set click listener for the whole post view
                final String productKey = CabeceraRef.getKey();
                Log.i(LOG_TAG, "adapter:orderKey: " + productKey);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                           }
                                                       }
                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {
                                Log.d(LOG_TAG, "adapter:onClick model: " + model.getProducto().getNombreProducto());
                                Log.d(LOG_TAG, "adapter:onClick");
//                                Intent intent = new Intent(getContext(), ProductosEnOrdenes.class);
//                                mCliente=model.getCliente();
                                mProducto = model.getProducto();
                                mProductKey = productKey;
//                                mDetalleAnterior = model;
//                                putExtraFirebase_Fragment(intent);
//
////                                intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
////                                intent.putExtra(EXTRA_PRODUCT, model.getProducto());
//                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//
//                                startActivity(intent);

//                                showDialogNumberPicker(mProductKey);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Detalle model, int position) {
                Log.d(LOG_TAG, "onItemDismissHolder");
            }

            @Override
            protected void onItemAcceptedHolder(Detalle model, int position) {
                Log.d(LOG_TAG, "onItemAcceptedHolder");

            }
        };
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);
    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, " oonViewCreated " );
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, " onActivityCreated " );


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, " onViewStateRestored " );
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i(LOG_TAG, " onStart" );
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void bloqueoPickingParaTrabajoOffLine() {

        if (hayTareaEnProceso() || !isNetworkAvailable(getContext())) {
            return;
        }

        taskList.clear();

        //Bloqueo los totales de Picking.
        refPickingTotal_7_List(PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal tiene childre: " + dataSnapshot.hasChildren());
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Detalle det = (Detalle) snapshot.getValue(Detalle.class);
                    Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal:" + snapshot.getKey());
                    Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal:" + det.getProducto().getNombreProducto());
                    readBlockPickingTotal(PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden(), snapshot.getKey());
                    taskList.add(mPickingTotalTask.get(i));
                    i++;
                }


                //Bloqueo las cabeceras.
                refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockCabeceraOrden tiene children: " + dataSnapshot.hasChildren());

                        int i = 0;
                        int j=0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CabeceraOrden cab = (CabeceraOrden) snapshot.getValue(CabeceraOrden.class);
                            Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockCabeceraOrden:" + cab.getNumeroDeOrden());
                            readBlockCabeceraOrden(cab.getNumeroDeOrden());
                            taskList.add(mCabeceraOrdenTask.get(i));
                            // bloqueo el saldo si no fue anteriormente bloqueado por otra orden
                            Log.i(LOG_TAG, "bloqueoPickingOffLine - SaldoNoRepetido Index:" + mSaldosTotalIndex.indexOf(cab.getClienteKey()));
                            if(!(mSaldosTotalIndex.indexOf(cab.getClienteKey())>-1)){
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - SaldoNoRepetido:" + cab.getNumeroDeOrden());
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - SaldoNoRepetido:" + cab.getClienteKey());
                            readBlockSaldosTotal(cab.getClienteKey());
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - SaldoNoRepetido Index after:" + mSaldosTotalIndex.indexOf(cab.getClienteKey()));
                            taskList.add(mSaldosTotalTask.get(j));
                                j++;
                            }
                            i++;
                        }

                        readBlockPicking(PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
                        taskList.add(mPickingTask);
                        allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
                        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnSuccessListener");
                                liberarArrayTaskCasoExitoso();
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putLong(getString(R.string.PickingOrderSeleccionada), datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
//                                editor.putString(getString(R.string.PickingOrderCommentSeleccionada), datosCabeceraPickingSeleccionada.getComentario());
//                                editor.putString(getString(R.string.PickingOrderFechaSeleccionada), mCreationDate.getText().toString());
                                editor.commit();


                                mPickinOrdersAdapter.notifyDataSetChanged();

                                muestraTotalesProductosDelivery();
                                muestraOrdenesEnDelivey();

                                mCursorAdapterTotalProductos.notifyDataSetChanged();
                                recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.VISIBLE);
                                recyclerViewTotalProductos.setVisibility(View.VISIBLE);


                                recyclerView.setVisibility(View.GONE);
                                mLinearOrders.setVisibility(View.VISIBLE);
                                mLinearProductos.setVisibility(View.VISIBLE);
                                mDeliveryOrderTile.setVisibility(View.VISIBLE);

                            }
                        });
                        allTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.toString());
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.getMessage());

                                liberarRecusosTomados();
                                liberarArrayTaskConBloqueos();
                                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + e.getMessage().toString());


                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel cabeceraOrden:" + databaseError.toString());

//                liberarRecusosTomados("", PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
//                liberarArrayTaskConBloqueos();
//                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel total Picking:" + databaseError.toString());

//                li DialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

            }
        });


    }


    private void desbloqueoPickingParaTrabajoOffLine() {

        if (hayTareaEnProceso() || !isNetworkAvailable(getContext())) {
            return;
        }
        Log.i(LOG_TAG, "desbloqueoPickingOffLine - liberarArrayTaskCasoExitoso ");

        liberarArrayTaskCasoExitoso();
//        taskList.clear();

        mPickingTotalEstado = PICKING_STATUS_DELIVERY;
        mPickingTotalNumero = datosCabeceraPickingSeleccionada.getNumeroDePickingOrden();

        mPickingEstado = PICKING_STATUS_DELIVERY;
        mPickingNumero = datosCabeceraPickingSeleccionada.getNumeroDePickingOrden();

        //Bloqueo los totales de Picking.
        refPickingTotal_7_List(PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal tiene childre: " + dataSnapshot.hasChildren());
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Detalle det = (Detalle) snapshot.getValue(Detalle.class);
                    Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal:" + snapshot.getKey());
                    Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal:" + det.getProducto().getNombreProducto());
                    mPickingTotalIndexLiberar.add(snapshot.getKey());
                    mLiberarSemaforoPickingTotal = true;
//                    readBlockPickingTotal(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden(), snapshot.getKey());
//                    taskList.add(mPickingTotalTask.get(i));
                    i++;
                }


                //Bloqueo las cabeceras.
                refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden tiene children: " + dataSnapshot.hasChildren());

                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CabeceraOrden cab = (CabeceraOrden) snapshot.getValue(CabeceraOrden.class);
                            Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden Nro:" + cab.getNumeroDeOrden());
                            Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden ClienteKey:" + cab.getClienteKey());


                            mCabeceraOrdenLiberar.add(snapshot.getKey());
                            mLiberarSemaforoCabeceraOrden = true;
                            Log.i(LOG_TAG, "desbloqueoPickingOffLine - SaldoNoRepetido Index antes:" + mSaldosTotalIndexLiberar.indexOf(cab.getClienteKey()));

                            if(!(mSaldosTotalIndexLiberar.indexOf(cab.getClienteKey())>-1)){
                                Log.i(LOG_TAG, "desbloqueoPickingOffLine - SaldoNoRepetido Nro Orden:" + cab.getNumeroDeOrden());
                                Log.i(LOG_TAG, "desbloqueoPickingOffLine - SaldoNoRepetido cliente Key:" + cab.getClienteKey());
                                mSaldosTotalIndexLiberar.add(cab.getClienteKey());
                                mLiberarSemaforoSaldoTotal = true;
                                Log.i(LOG_TAG, "desbloqueoPickingOffLine - SaldoNoRepetido Index after:" + mSaldosTotalIndexLiberar.indexOf(cab.getClienteKey()));

                            }


//                            readBlockCabeceraOrden(cab.getNumeroDeOrden());
//                            taskList.add(mCabeceraOrdenTask.get(i));
//                            readBlockSaldosTotal(cab.getClienteKey());
//                            taskList.add(mSaldosTotalTask.get(i));

                            i++;
                        }

//                        readBlockPicking(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
                        mLiberarSemaforoPicking = true;
                        liberarRecusosTomados();
//                        taskList.add(mPickingTask);
//                        allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
//                        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnSuccessListener");
//                                liberarArrayTaskCasoExitoso();
//                            }});
//                        allTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.toString());
//                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.getMessage());
//
//                                liberarRecusosTomados();
//                                liberarArrayTaskConBloqueos();
//                                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + e.getMessage().toString());
//
//
//
//                            }
//                        });

                        mDeliveryOrderTile.setVisibility(View.GONE);
                        mLinearProductos.setVisibility(View.GONE);
                        mLinearOrders.setVisibility(View.GONE);
                        recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);
                        recyclerViewTotalProductos.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
//                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(getString(R.string.PickingOrderSeleccionada));
//                        editor.remove(getString(R.string.PickingOrderCommentSeleccionada));
//                        editor.remove(getString(R.string.PickingOrderFechaSeleccionada));
                        editor.commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel cabeceraOrden:" + databaseError.toString());

//                liberarRecusosTomados("", PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
//                liberarArrayTaskConBloqueos();
//                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel total Picking:" + databaseError.toString());

//                li DialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

            }
        });


    }


    private void pasarOrdenAEntregadaParaCompensar(final CabeceraOrden cabeceraOrden) {
        Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- cabeceraOrden.getEstado() " + cabeceraOrden.getEstado());
        Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- ORDEN_STATUS_DELIVERED_PARA_COMPENSAR " + ORDEN_STATUS_DELIVERED_PARA_COMPENSAR);
        Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- ORDEN_STATUS_DELIVERED_PARA_COMPENSAR " + (cabeceraOrden.getEstado()>=ORDEN_STATUS_DELIVERED_PARA_COMPENSAR));

        if(cabeceraOrden.getEstado()>=ORDEN_STATUS_DELIVERED_PARA_COMPENSAR){
            muestraMensajeEnDialogo("Orden ya entregada");
            return;
        }

        if (hayTareaEnProceso()) {
            return;
        }


        refDetalleOrden_4_ListaXOrden(cabeceraOrden.getNumeroDeOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProductosEnOrdenDatos = dataSnapshot;

                totalProductos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mProductosEnTotalPickingDatos = dataSnapshot;

                        refSaldoTotalClientes_10(cabeceraOrden.getClienteKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                CabeceraOrden saldo = dataSnapshot.getValue(CabeceraOrden.class);

                                Map<String, Object> childUpdates = new HashMap<>();
                                Map<String, Object> productosEnTotalPickingValues = null;
                                int i = 0;
                                for (DataSnapshot productoEnOrden : mProductosEnOrdenDatos.getChildren()) {
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- " + productoEnOrden.getKey());
                                    // busca el producto dentro de la orden
                                    String productKey = productoEnOrden.getKey();
                                    Detalle detalleOrden = productoEnOrden.getValue(Detalle.class);
                                    // creamos una copia con cantidad cero para usar en otras estructuras.
                                    Detalle detalleOrdenAux = detalleOrden.copy();
                                    detalleOrdenAux.modificarCantidadProductoDeOrden(0.0); // modificamos la cantidad a Cero para usarla para el calculo de Total
                                    detalleOrdenAux.modificarCantidadProductoDeEntrega(0.0); // modificamos la cantidad a Cero para usarla para el calculo de Total
                                    Boolean encontroLaLLave = false;
                                    Detalle detalleTotalPicking=null;
                                    for (DataSnapshot productoEnTotalPicking : mProductosEnTotalPickingDatos.getChildren()) {
                                        String productTotalPickingKey = productoEnTotalPicking.getKey();

                                        detalleTotalPicking = productoEnTotalPicking.getValue(Detalle.class);

                                        if (productKey.equals(productTotalPickingKey)) {
                                            Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- producto" + detalleOrden.getProducto().getNombreProducto());
                                            Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- Cantidad endtregada Orden" + detalleOrden.getCantidadEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- Cantidad entregada Picking" +detalleTotalPicking.getCantidadEntrega());

                                            //Actualizar totales de picking
                                            encontroLaLLave = true;
                                            detalleTotalPicking.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                            Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- Cantidad entregada Picking modificado" +detalleTotalPicking.getCantidadEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- Cantidad entregada Picking Actualizada" +detalleTotalPicking.getCantidadEntrega());
                                            //Actualizo los totales entregado por producto en la orden de Picking
                                            childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, cabeceraOrden.getNumeroDePickingOrden(), productKey), detalleTotalPicking.toMap());
                                            break;

                                        }
                                    }
                                    if (!encontroLaLLave) {
                                          detalleTotalPicking = new Detalle(0.0, detalleOrden.getProducto(), null);
                                          detalleTotalPicking.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                        Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar- Cantidad entregada Picking Actualizada" +detalleTotalPicking.getCantidadEntrega());
                                        //Actualizo los totales entregado por producto en la orden de Picking
                                        childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, cabeceraOrden.getNumeroDePickingOrden(), productKey), detalleTotalPicking.toMap());

                                    }


                                }


                                //Actualizo el Monto total Entregado en el Picking
                                datosCabeceraPickingSeleccionada.getTotales().setSumaMontoEntregado(cabeceraOrden.getTotales().getMontoEntregado());
                                datosCabeceraPickingSeleccionada.setUsuarioEntrega(mUsuario.getUsername());
                                childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY,String.valueOf ( datosCabeceraPickingSeleccionada.getNumeroDePickingOrden())), datosCabeceraPickingSeleccionada.toMap());

                                //Actualizo las cabeceras de Ordenes
                                cabeceraOrden.setEstado(ORDEN_STATUS_DELIVERED_PARA_COMPENSAR);
                                cabeceraOrden.bloquear();
                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERY, cabeceraOrden.getNumeroDeOrden(), cabeceraOrden.getNumeroDePickingOrden()),cabeceraOrden.toMap());
                                childUpdates.put(nodoCabeceraOrdenList_ParaCompensar(cabeceraOrden.getClienteKey(),cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());
//                                childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_DELIVERED_PARA_COMPENSAR, cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());
                                childUpdates.put(nodoCabeceraOrden_1B(cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());

                                Double iva=0.0;
                                if(!cabeceraOrden.getCliente().getEspecial()){
                                    iva=cabeceraOrden.getCliente().getIva();
                                }

                                if (saldo == null) {
                                    // di no existe esta estructura, se crea una en cero.
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar saldosTotal= NULL");

                                    Totales totales = new Totales(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                                    saldo = new CabeceraOrden(cabeceraOrden.getClienteKey(), cabeceraOrden.getCliente(), ORDEN_STATUS_INICIAL, totales, "Sistema", 00);
                                    saldo.setUsuarioCreador("Sistema");
                                    saldo.bloquear();
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar saldos = "+saldo.getTotales().getSaldo());
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar monto entreado = "+cabeceraOrden.getTotales().getMontoEntregado());

                                    saldo.getTotales().setMontoEntregado(saldo.getTotales().getMontoEntregado() + (cabeceraOrden.getTotales().getMontoEntregado()*(1+iva/100)));
                                    saldo.getTotales().setSaldo(saldo.getTotales().getSaldo() + (cabeceraOrden.getTotales().getMontoEntregado()*(1+iva/100)));
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar saldos actualizado= "+saldo.getTotales().getSaldo());

                                } else {
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar saldos = "+saldo.getTotales().getSaldo());
                                    saldo.getTotales().setMontoEntregado(saldo.getTotales().getMontoEntregado() + (cabeceraOrden.getTotales().getMontoEntregado()*(1+iva/100)));
                                    saldo.getTotales().setSaldo(saldo.getTotales().getSaldo() + (cabeceraOrden.getTotales().getMontoEntregado()*(1+iva/100)));
                                    Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar saldos actualizado= "+saldo.getTotales().getSaldo());

                                }
                                childUpdates.put(nodoSaldoTotalClientes_10(cabeceraOrden.getClienteKey()), saldo.toMap());

                                mClienteKey = cabeceraOrden.getClienteKey();
                                mCliente = cabeceraOrden.getCliente();

                                Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar - : abro pagos" );
                                Intent intent = new Intent(getContext(), PagosActivity.class);
                                putExtraFirebase_Fragment(intent);
                                startActivity(intent);

                                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.i(LOG_TAG, "pasarOrdenAEntregadaParaCompensar updateChildren-onFailure " + e.toString());

                                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {




                                    }
                                });
                            }

                            @Override // Caso de error en la lectura del saldo
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override // Cancelacion en la lectura del total de productos de en Picking
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override // Cancelacion en la lectura del Productos en la oreden
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }

public void compensarCuentaOffine(String clienteKey){
    refCabeceraOrdenList_ParaCompensar(clienteKey).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final DataSnapshot ordenesSinCompensar=dataSnapshot;

            refPagosListado_11(mClienteKey,String.valueOf(PAGO_STATUS_INICIAL_SIN_COMPENSAR)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot pagosSinCompensar=dataSnapshot;

                   for( DataSnapshot ordenCabecera : ordenesSinCompensar.getChildren()){
                    String nroOrden = ordenCabecera.getKey();
                    CabeceraOrden orden = ordenCabecera.getValue(CabeceraOrden.class);

                       if(orden.sepuedeModificar()){
                       for( DataSnapshot pago : pagosSinCompensar.getChildren()){
                           String pagoKey = ordenCabecera.getKey();
                           Pago p = pago.getValue(Pago.class);

                           Compensacion compesacion=new Compensacion(orden,p,pagoKey,mUserKey);


                       }
                       }else{
                           continue;
                       }
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


}



    private void pasarPickingAInicial(final CabeceraPicking cabeceraPicking) {
        Log.i(LOG_TAG, "pasarPickingAEntrega Numero de picking- " + cabeceraPicking.getNumeroDePickingOrden());
        if (hayTareaEnProceso()) {
            return;
        }

        taskList.clear();
        // leo y bloqueo picking
        readBlockPicking(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden());


        mPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraPicking cabeceraPicking1 = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);


                    Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key lee cabeceras");
                    //leo las ordenes asociadas al picking y las bloqueo
                    refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERY, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden lee cabeceras Llegaron los datos");
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key " + dataSnapshot.getKey());
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden Ref " + dataSnapshot.getRef());
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden ChildrenCount" + dataSnapshot.getChildrenCount());
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                              CabeceraOrden cabeceraOrden= (CabeceraOrden) snapshot.getValue(CabeceraOrden.class);
                                if (cabeceraOrden.getEstado()>= ORDEN_STATUS_EN_DELIVERY){
                                    Log.i(LOG_TAG, "pasarPickingAEntrega Hay Ordenes Entregadas" + cabeceraOrden.getNumeroDeOrden());

                                    muestraMensajeEnDialogo("Ya existen ordenes Entregadas, no se puede regresar a Preparar");
                                    return;
                                }

                            }

                            int i = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String nroOrden = snapshot.getKey();
                                Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key " + nroOrden);

                                readBlockCabeceraOrden(Long.parseLong(nroOrden));
                                taskList.add(mCabeceraOrdenTask.get(i));
                                i++;
                            }

                            //leo los totales de productos asociadas al picking y las bloqueo
                            refPickingTotal_7_List(PICKING_STATUS_INICIAL, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i = 0;
                                    mProductosEnOrdenDatos = dataSnapshot;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String productkey = snapshot.getKey();
                                        Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Picking Total key " + productkey);

                                        readBlockPickingTotal(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden(), productkey);
                                        taskList.add(mPickingTotalTask.get(i));
                                        i++;
                                    }

                                    allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
                                    allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(LOG_TAG, "pasarPickingAEntrega Completo el bloqueo- ");

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            Map<String, Object> totalInicialDetalleValues = null;
                                            Map<String, Object> pickingTotalValues = null;
                                            int i = 0;
                                            //Recorro el total de Productos en Picking (7)

                                            for (DataSnapshot productoEnOrden : mProductosEnOrdenDatos.getChildren()) {
                                                // busca el producto dentro de la orden
                                                String productKey = productoEnOrden.getKey();
                                                Detalle pickingTotal = productoEnOrden.getValue(Detalle.class);
                                                pickingTotal.liberar();
                                                // cambio de estado el total de picking 7
                                                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden(), productKey), null);
                                                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL, cabeceraPicking.getNumeroDePickingOrden(), productKey), pickingTotal.toMap());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega Product key- " + productKey + " " + pickingTotal.getProducto().getNombreProducto());

                                            }

                                            // Actualizacion de Cabecera de Picking (6)
                                            final CabeceraPicking cabeceraPicking = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);
                                            Log.i(LOG_TAG, "pasarPickingAEntrega mPickingTask-isSuccessful(): " + mPickingTask.isSuccessful());
                                            Log.i(LOG_TAG, "pasarPickingAEntrega mPickingTask-getKey(): " + ((DataSnapshot) mPickingTask.getResult()).getKey());
                                            Log.i(LOG_TAG, "pasarPickingAEntrega mPickingTask-getREF(): " + ((DataSnapshot) mPickingTask.getResult()).getRef());

                                            if (cabeceraPicking == null) {
                                                // si es nulo se trataria de un error puesto que existe

                                                Log.i(LOG_TAG, "pasarPickingAEntrega TotalInicial Detalle = NuLL- ");
                                            } else {
                                                cabeceraPicking.liberar();

                                            }

                                            /*6 */
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), null);
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), cabeceraPicking.toMap());

                                            for (int a = 0; a < mCabeceraOrdenTask.size(); a++) {
                                                DataSnapshot cabeceraOrdenes = ((DataSnapshot) mCabeceraOrdenTask.get(a).getResult());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega cabeceraOrdenes key = " + cabeceraOrdenes.getKey());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega cabeceraOrdenes ref = " + cabeceraOrdenes.getRef());

                                                String nroDeOrden = cabeceraOrdenes.getKey();
                                                CabeceraOrden cabeceraOrden = cabeceraOrdenes.getValue(CabeceraOrden.class);
                                                Log.i(LOG_TAG, "pasarPickingAEntrega nroDeOrden = " + nroDeOrden);
                                                Log.i(LOG_TAG, "pasarPickingAEntrega nroDeOrden = " + cabeceraOrden.getNumeroDeOrden());

                                    /*2 */
                                                cabeceraOrden.setEstado(ORDEN_STATUS_PICKING);
                                                cabeceraOrden.liberar();
                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERY, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), null);

                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_PICKING, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), cabeceraOrden.toMap());
//                                        childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_EN_DELIVERY, cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());
                                                childUpdates.put(nodoCabeceraOrden_1B(cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());

                                            }

                                            mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.i(LOG_TAG, "pasarPickingAEntrega updateChildren-onFailure " + e.toString());
                                                    liberarRecusosTomados();
                                                    liberarArrayTaskConBloqueos();
                                                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {
                                                                             liberarArrayTaskCasoExitoso();
                                                                             Log.i(LOG_TAG, "pasarPickingAEntrega - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                                                                         }
                                                                     }

                                            );
                                        }
                                    });
                                    allTask.addOnFailureListener(new OnFailureListener() {
                                                                     @Override
                                                                     public void onFailure(@NonNull Exception e) {
                                                                         liberarRecusosTomados();
                                                                         liberarArrayTaskConBloqueos();
                                                                         muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                                                     }
                                                                 }

                                    );

                                }


                                @Override // Listado de Picking... Listen for single value
                                public void onCancelled(DatabaseError databaseError) {
                                    liberarRecusosTomados();
                                    liberarArrayTaskConBloqueos();
                                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                }
                            });
                        }

                        @Override // Listado de cabceras... Listen for single value
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key lee cabeceras onCancelled");
                            liberarRecusosTomados();
                            liberarArrayTaskConBloqueos();
                            muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                        }
                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() { // bloqueo de la orden de Picking.
            @Override
            public void onFailure(@NonNull Exception e) {
                liberarRecusosTomados();
                liberarArrayTaskConBloqueos();
                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Orden");
            }
        });
    }

    private void pasarPickingACerrado(final CabeceraPicking cabeceraPicking) {


        Log.i(LOG_TAG, "pasarPickingACerrado Numero de picking- " + cabeceraPicking.getNumeroDePickingOrden());
        if (hayTareaEnProceso() && isNetworkAvailable(getContext())) {
            return;
        }

        taskList.clear();
        // leo y bloqueo picking
        readBlockPicking(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden());
        final String aamm =(new SimpleDateFormat("yyyy-MM").format(new Date(System.currentTimeMillis()))).toString();

        mPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull final Task task) {
                if (task.isSuccessful()) {
                    CabeceraPicking cabeceraPicking1 = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);

//                    final SimpleDateFormat aamm = new SimpleDateFormat("yyyy-MM");



                    Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo Orden key lee cabeceras");
                    //leo las ordenes asociadas al picking y las bloqueo
                    refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERY, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mCaberasOrdenCerrarPickingDatos=dataSnapshot;
                            Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo Orden lee cabeceras Llegaron los datos");
                            Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo Orden key " + dataSnapshot.getKey());
                            Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo Orden Ref " + dataSnapshot.getRef());
                            Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo Orden ChildrenCount" + dataSnapshot.getChildrenCount());

                            int i = 0;
                           mVentaProductosIndex =0;
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String nroOrden = snapshot.getKey();
                                final CabeceraOrden cabeceraOrden= (CabeceraOrden) snapshot.getValue(CabeceraOrden.class);

                                readBlockCabeceraOrden(Long.parseLong(nroOrden));
                                taskList.add(mCabeceraOrdenTask.get(i));
                                i++;
                                final ArrayList<Detalle> listadeDetalles= new ArrayList<Detalle>();
                                refDetalleOrden_4_ListaXOrden(Long.parseLong(nroOrden)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mListaDetalleDeOrdenes.add(dataSnapshot);
                                        Log.i(LOG_TAG, "pasarPickingACerrado lectura mListaDetalleDeOrdenes.size " + mListaDetalleDeOrdenes.size());


                                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String productkey = snapshot.getKey();

                                            Detalle detalleOrden = snapshot.getValue(Detalle.class);
                                            Log.i(LOG_TAG, "pasarPickingACerrado lectura Detalles productoKey " + snapshot.getKey());
                                            Log.i(LOG_TAG, "pasarPickingACerrado lectura Detalles Producto " + detalleOrden.getProducto().getNombreProducto());
                                            Log.i(LOG_TAG, "pasarPickingACerrado lectura Detalles Cantidad Entregada " + detalleOrden.getCantidadEntrega());
//                                            listadeDetalles.add(detalleOrden);
                                            Log.i(LOG_TAG, "pasarPickingACerrado  listadeDetalles.size " + listadeDetalles.size());
                                            Log.i(LOG_TAG, "pasarPickingACerrado cabeceraOrden.getClienteKey()" + snapshot.getRef().getParent().getKey());
                                            Log.i(LOG_TAG, "pasarPickingACerrado cabeceraOrden.getClienteKey()" + cabeceraOrden.getClienteKey());
                                            Log.i(LOG_TAG, "pasarPickingACerrado productkey" + productkey);
                                            Log.i(LOG_TAG, "pasarPickingACerrado aamm" + aamm);

                                            if (!mReporteVentasClienteIndex.isEmpty()) {
                                                if (mReporteVentasClienteIndex.indexOf(cabeceraOrden.getClienteKey() + productkey + aamm) > -1) {
                                                   Log.i(LOG_TAG, "pasarPickingACerrado la clave ya existe: "+cabeceraOrden.getClienteKey() + productkey + aamm );

                                                    continue;
                                                } else {
                                                    Log.i(LOG_TAG, "pasarPickingACerrado la clave nueva: " +cabeceraOrden.getClienteKey() + productkey + aamm);

                                                    readBlockReporteVentasCliente(cabeceraOrden.getClienteKey(), productkey, aamm);
                                                    taskList.add(mReporteVentasClienteTask.get(mVentaProductosIndex));
                                                    mVentaProductosIndex++;
                                                }
                                            } else {
                                                Log.i(LOG_TAG, "pasarPickingACerrado la clave vacia: "+cabeceraOrden.getClienteKey() + productkey + aamm );
                                                readBlockReporteVentasCliente(cabeceraOrden.getClienteKey(), productkey, aamm);
                                                taskList.add(mReporteVentasClienteTask.get(mVentaProductosIndex));
                                                mVentaProductosIndex++;
                                            }

//
//                                            if(listadeDetalles.size()==dataSnapshot.getChildrenCount()){
////                                            mListaDetalleDeOrdenes.add(listadeDetalles);
//                                            Log.i(LOG_TAG, "pasarPickingACerrado lectura mListaDetalleDeOrdenes.size " + mListaDetalleDeOrdenes.size());
//                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                            }

                            //leo los totales de productos asociadas al picking y las bloqueo
                            refPickingTotal_7_List(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i = 0;

                                    mProductosEnOrdenDatos = dataSnapshot;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String productkey = snapshot.getKey();
                                        Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo venta Producto " + productkey+" - "+aamm);
                                        readBlockReporteVentasProducto(productkey,aamm);
                                        taskList.add(mReporteVentasProductoTask.get(i));
//                                        for (DataSnapshot cabeceraOrdenSnapshot : mCaberasOrdenCerrarPickingDatos.getChildren()) {
//                                            CabeceraOrden cabeceraOrden= (CabeceraOrden) cabeceraOrdenSnapshot.getValue(CabeceraOrden.class);
//                                            Log.i(LOG_TAG, "pasarPickingACerrado Bloqueo venta Client:"+ cabeceraOrden.getClienteKey()+"Producto " + productkey+" - "+aamm.toStrin/g());
//                                            readBlockReporteVentasCliente(cabeceraOrden.getClienteKey(),productkey,aamm.toString());
//                                            taskList.add(mReporteVentasProductoTask.get(mVentaProductosIndex));
//                                           mVentaProductosIndex++;
//                                        }
                                        i++;
                                    }

                                    allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
                                    allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(LOG_TAG, "pasarPickingACerrado listadeDetalles Completo el bloqueo- ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            Map<String, Object> totalInicialDetalleValues = null;
                                            Map<String, Object> pickingTotalValues = null;




                                            //Recorro el total de Productos en Picking (7)
                                            int i = 0;
                                            for (DataSnapshot productoEnOrden : mProductosEnOrdenDatos.getChildren()) {
                                                // busca el producto dentro de la orden
                                                String productKey = productoEnOrden.getKey();
                                                Detalle pickingTotal = productoEnOrden.getValue(Detalle.class);
                                                pickingTotal.liberar();

                                                DataSnapshot reporteventaProdSnap = ((DataSnapshot) mReporteVentasProductoTask.get(i).getResult());
                                                Detalle detalleReporteProducto = reporteventaProdSnap.getValue(Detalle.class);
                                                if (detalleReporteProducto == null) {
                                                    // di no existe esta estructura, se crea una en cero.
                                                    Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteProducto = NULL");
                                                    detalleReporteProducto = new Detalle(0.0, pickingTotal.getProducto(), null);
                                                } else {
                                                    Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteProducto != NULL");

                                                }
                                                detalleReporteProducto.setCantidadEntrega(detalleReporteProducto.getCantidadEntrega() + pickingTotal.getCantidadEntrega());
                                                detalleReporteProducto.setMontoItemEntrega(detalleReporteProducto.getMontoItemEntrega() + pickingTotal.getMontoItemEntrega());
                                                detalleReporteProducto.liberar();
                                                childUpdates.put(nodoReporteVentasProducto_8(productKey, aamm), detalleReporteProducto.toMap());

                                                Log.i(LOG_TAG, "pasarPickingACerrado Reporte Producto total picking- " + productKey + " " + pickingTotal.getProducto().getNombreProducto()+" - "+pickingTotal.getCantidadEntrega());
                                                Log.i(LOG_TAG, "pasarPickingACerrado Product Infome " + productKey + " " +detalleReporteProducto.getProducto().getNombreProducto() +" - "+detalleReporteProducto.getMontoItemEntrega());
                                                i++;
                                            }

                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");

                                            // Actualizacion de Cabecera de Picking (6)
                                            CabeceraPicking cabeceraPicking = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);
                                            Log.i(LOG_TAG, "pasarPickingACerrado mPickingTask-isSuccessful(): " + mPickingTask.isSuccessful());
                                            Log.i(LOG_TAG, "pasarPickingACerrado mPickingTask-getKey(): " + ((DataSnapshot) mPickingTask.getResult()).getKey());
                                            Log.i(LOG_TAG, "pasarPickingACerrado mPickingTask-getREF(): " + ((DataSnapshot) mPickingTask.getResult()).getRef());

                                            if (cabeceraPicking == null) {
                                                // si es nulo se trataria de un error puesto que existe

                                                Log.i(LOG_TAG, "pasarPickingACerrado TotalInicial Detalle = NuLL- ");
                                            } else {
                                                cabeceraPicking.liberar();

                                            }

                                            /*6 */
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), null);
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), cabeceraPicking.toMap());
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");
                                            Log.i(LOG_TAG, "pasarPickingACerrado ------------------------------------ ");


                                            for (int h=0;h<mReporteVentasClienteIndex.size();h++) {

                                                Log.i(LOG_TAG, "pasarPickingACerrado RRRRRRRRRRRReproteVentas " +h);
                                                Log.i(LOG_TAG, "pasarPickingACerrado RRRRRRRRRRRReproteVentas " +h);
                                                Log.i(LOG_TAG, "pasarPickingACerrado RRRRRRRRRRRReproteVentas " +h);

                                                DataSnapshot reporteventaclienteSnap = ((DataSnapshot) mReporteVentasClienteTask.get(h).getResult());
                                                ReporteClienteProducto detalleReporteCliente = reporteventaclienteSnap.getValue(ReporteClienteProducto.class);

                                                if(detalleReporteCliente==null){
                                                    Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteCliente: null " );
                                                }else{
                                                    Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteCliente: " + detalleReporteCliente.getDetalle().getProducto().getNombreProducto() + " - " +
                                                            detalleReporteCliente.getDetalle().getCantidadEntrega());
                                                }

                                                for (int a = 0; a < mCabeceraOrdenTask.size(); a++) {

                                                    DataSnapshot cabeceraOrdenes = ((DataSnapshot) mCabeceraOrdenTask.get(a).getResult());
                                                    CabeceraOrden cabeceraOrden = cabeceraOrdenes.getValue(CabeceraOrden.class);
                                                    String nroDeOrden = String.valueOf(cabeceraOrden.getNumeroDeOrden());
                                                    Log.i(LOG_TAG, "pasarPickingACerrado nroDeOrden = " + nroDeOrden+" cliente: "+ cabeceraOrden.getCliente().getNombre()+" - "+cabeceraOrden.getClienteKey());

                                                    DataSnapshot listaDetallesOrdenes = mListaDetalleDeOrdenes.get(a);
                                                    Log.i(LOG_TAG, "pasarPickingACerrado mListaDetalleDeOrdenes : "+a+ " = "+ mListaDetalleDeOrdenes.get(a));

                                                    detallesFor:
                                                    for (DataSnapshot detallesDeOrdenSnap : listaDetallesOrdenes.getChildren()) {
                                                        String productKey = detallesDeOrdenSnap.getKey();
                                                        Detalle detalleOrden = detallesDeOrdenSnap.getValue(Detalle.class);
                                                        Log.i(LOG_TAG, "pasarPickingACerrado listaDetallesOrdenes Nro Orden = " +detallesDeOrdenSnap.getRef().getParent().getKey() );
                                                        Log.i(LOG_TAG, "pasarPickingACerrado listaDetallesOrdenes  = " + detalleOrden.getProducto().getNombreProducto()+" - "+productKey+" - "+detalleOrden.getCantidadEntrega());
//                                                        int index = mReporteVentasClienteIndex.indexOf(cabeceraOrden.getClienteKey() + productKey + aamm);
                                                        if (!mReporteVentasClienteIndex.get(h).equals(cabeceraOrden.getClienteKey() + productKey + aamm)){
                                                            Log.i(LOG_TAG, "pasarPickingACerrado distintos");

                                                            continue detallesFor;
                                                        }
                                                        if (detalleReporteCliente == null) {
                                                            // di no existe esta estructura, se crea una en cero.
                                                            Log.i(LOG_TAG, "pasarPickingACerrado ventasCliente = NULL");
                                                            Detalle det = new Detalle(0.0, detalleOrden.getProducto(), cabeceraOrden.getCliente());
                                                            detalleReporteCliente = new ReporteClienteProducto(cabeceraOrden.getCliente(),det);

                                                        } else {
                                                            Log.i(LOG_TAG, "pasarPickingACerrado ventasCliente != NULL");
                                                        }
                                                            detalleReporteCliente.getDetalle().setCantidadEntrega(detalleReporteCliente.getDetalle().getCantidadEntrega() + detalleOrden.getCantidadEntrega());
                                                            detalleReporteCliente.getDetalle().setMontoItemEntrega(detalleReporteCliente.getDetalle().getMontoItemEntrega() + detalleOrden.getMontoItemEntrega());
                                                            detalleReporteCliente.liberar();
                                                            childUpdates.put(nodoReporteVentasClientes_9(cabeceraOrden.getClienteKey(), productKey, aamm), detalleReporteCliente.toMap());
                                                            Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteCliente.getCantidadEntrega() "+detalleReporteCliente.getDetalle().getCantidadEntrega());
                                                            Log.i(LOG_TAG, "pasarPickingACerrado detalleReporteCliente.getMontoEntrega() "+detalleReporteCliente.getDetalle().getMontoItemEntrega());
                                                            Log.i(LOG_TAG, "pasarPickingACerrado XXXXXXXXXXXXXXXXXXXXXXXXXX");
                                                            Log.i(LOG_TAG, "pasarPickingACerrado XXXXXXXXXXXXXXXXXXXXXXXXXX");
                                                            Log.i(LOG_TAG, "pasarPickingACerrado XXXXXXXXXXXXXXXXXXXXXXXXXX");



                                                    } // Cierre For que recorre Detalles
                                                    cabeceraOrden.liberar();

//                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERY, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), cabeceraOrden.toMap());
                                                    childUpdates.put(nodoCabeceraOrden_1B(cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());

                                                }// Cierre for que recorre las cabeceras
                                            }// cirre for Reportes Clientes
                                            mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.i(LOG_TAG, "pasarPickingACerrado updateChildren-onFailure " + e.toString());
                                                    liberarRecusosTomados();
                                                    liberarArrayTaskConBloqueos();
                                                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {
                                                                             liberarArrayTaskCasoExitoso();
                                                                             Log.i(LOG_TAG, "ppasarPickingACerrado - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                                                                         }
                                                                     }

                                            );

                                        }
                                    });
                                    allTask.addOnFailureListener(new OnFailureListener() {
                                                                     @Override
                                                                     public void onFailure(@NonNull Exception e) {
                                                                         Log.i(LOG_TAG, "pasarPickingACerrado Fallo allTAsk " + e.toString());

                                                                         liberarRecusosTomados();
                                                                         liberarArrayTaskConBloqueos();
                                                                         muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                                                     }
                                                                 }

                                    );

                                }


                                @Override // Listado de Totales de  Picking... Listen for single value
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i(LOG_TAG, "pasarPickingACerrado lectura de Totales de Picking - onCancelled "+databaseError.toString());
                                    liberarRecusosTomados( );
                                    liberarArrayTaskConBloqueos();
                                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                }
                            });
                        }

                        @Override // Listado de cabceras... Listen for single value
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(LOG_TAG, "pasarPickingACerrado lectura de cabecera Orden - onCancelled "+databaseError.toString());
                            liberarRecusosTomados();
                            liberarArrayTaskConBloqueos();
                            muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                        }
                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() { // bloqueo de la orden de Picking.
            @Override
            public void onFailure(@NonNull Exception e) {//Lectura y Bloqueo de Picking fallo
                liberarRecusosTomados( );
                liberarArrayTaskConBloqueos();
                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Picking");
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");

        if(hayTareaEnProceso()){
            liberarRecusosTomados();
            return;
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume()");

        //        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Mis Preferencias", Context.MODE_PRIVATE);


        long nroPickingAlmacenado = sharedPref.getLong(getString(R.string.PickingOrderSeleccionada), 0);
//        String comentarioPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderCommentSeleccionada), "");
//        String fechaPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderFechaSeleccionada), "");
        Log.i(LOG_TAG, "nroPickingAlmacenado: " + nroPickingAlmacenado);
        if (nroPickingAlmacenado > 0) {
            refPicking_6(PICKING_STATUS_DELIVERY,nroPickingAlmacenado).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    datosCabeceraPickingSeleccionada = dataSnapshot.getValue(CabeceraPicking.class);
//                    datosCabeceraPickingSeleccionada.setComentario(comentarioPickingAlmacenado);


                    mTilePickingComent.setText(datosCabeceraPickingSeleccionada.getComentario());
                    mTilePickingOrderNumber.setText(String.valueOf(datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()));
                    mTilePickingComent.setVisibility(View.VISIBLE);
                    mMontoRecaudado.setText(datosCabeceraPickingSeleccionada.getMontoRecaudado().toString());
                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    mCreationDate.setText(sfd.format(new Date(datosCabeceraPickingSeleccionada.getFechaDeCreacion())));



                    mPickinOrdersAdapter.notifyDataSetChanged();

                    muestraTotalesProductosDelivery();
                    muestraOrdenesEnDelivey();

                    mCursorAdapterTotalProductos.notifyDataSetChanged();
                    recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.VISIBLE);
                    recyclerViewTotalProductos.setVisibility(View.VISIBLE);


                    recyclerView.setVisibility(View.GONE);
                    mLinearOrders.setVisibility(View.VISIBLE);
                    mLinearProductos.setVisibility(View.VISIBLE);
                    mDeliveryOrderTile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


    }
        super.onResume();
    }




    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
//                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.i("zebra22", "size:" + pairedDevices.size());
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    Log.i("zebra22", "name:" + device.getName());
                    Log.i("zebra22", "getAddress():" + device.getAddress());
                    Log.i("zebra22", "describeContents():" + device.describeContents());
                    Log.i("zebra22", "BondState():" + device.getBondState());

                    if (device.getName().equals("XXXXJ154501680")) {

                        mmDevice = device;
                        mImprimirButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                }
            }

//            myLabel.setText("Bluetooth device found.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            Log.i("zebra22", "openBT() :");
//            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
 * after opening a connection to bluetooth printer device,
 * we have to listen and check if a data were sent to be printed.
 */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            Log.i("zebra22", "beginListenForDat :");
            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {
                                Log.i("zebra22", "beginListenForDat a:bytesAvailable > 0: " + bytesAvailable);
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    Log.i("zebra22", "beginListenForDat b-: " + (char) (b & 0xFF));
                                    if (b == delimiter) {
                                        Log.i("zebra22", "beginListenForDat b: " + "enter");
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        Log.i("zebra22", "data received1x :");
                                        Log.i("zebra22", data);
//                                        Log.i("zebra22","data received1 :"+ data);
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                                Log.i("zebra22", "data received2 :" + data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
//                            Log.i("zebra22","beginListenForDat :bytesAvailable <= 0");

                        } catch (IOException ex) {
                            Log.i("zebra22", "beginListenForDat :IOException ex");
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            Log.i("zebra22", "beginListenForDat :IOException ex function");
            e.printStackTrace();
        }
    }




    public void buscaDatosParaImprimirPicking(){

        refPagosxPickingList(String.valueOf(datosCabeceraPickingSeleccionada.getNumeroDePickingOrden())).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Pagos asociados al Picking
                mDataPagoxPicking_Printing = dataSnapshot;
                try {
                    sendData();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            String msg = null;
            // the text typed by the user
//            String msg = myTextbox.getText().toString();
//            msg += "\n";
//
//            mmOutputStream.write(msg.getBytes());
//
//              msg="! U1 setvar \"device.languages\" \"zpl\"";
//            mmOutputStream.write(msg.getBytes());
////
//
//
//            msg="! U1 getvar \"allcv\"";
//            msg="! U1 getvar \"device.languages\"";
//            mmOutputStream.write(msg.getBytes());
//            msg= "! U1 getvar \"zpl.system_error\"";

            // comienzo de comando
            msg = "^XA";
            mmOutputStream.write(msg.getBytes());
//            msg="^LT000";
//            mmOutputStream.write(msg.getBytes());

//            msg="^HH"; //return configuration Label
//            mmOutputStream.write(msg.getBytes());


//            msg="~WC"; //print configuration Label
//            mmOutputStream.write(msg.getBytes());


            //Label Legth  in dots 8dots/mm. (203 dpi)

            msg = "^LL600";

            int h = 50;
            int i = 30;


//
//            Log.i(LOG_TAG, "printing product Key-mDataSanpshotPrinting " + mCaberasOrdenCerrarPickingDatos.getChildrenCount());
//            Log.i(LOG_TAG, "printing product Key-mDataCabecerasParaCompensarPrinting " + mDataCabecerasParaCompensarPrinting.getChildrenCount());
//            Log.i(LOG_TAG, "printing product Key-mDataPagosSinCompensarPrinting " + mDataPagosSinCompensarPrinting.getChildrenCount());
//            Log.i(LOG_TAG, "printing product total " + i*(mDataSanpshotPrinting.getChildrenCount()+mDataCabecerasParaCompensarPrinting.getChildrenCount()+
//                    mDataPagosSinCompensarPrinting.getChildrenCount()));
//            msg = "^LL"+(i*(mDataSanpshotPrinting.getChildrenCount()+mDataCabecerasParaCompensarPrinting.getChildrenCount()+
//                    mDataPagosSinCompensarPrinting.getChildrenCount())+600);

            mmOutputStream.write(msg.getBytes());

            Log.i(LOG_TAG, "printing msg " + msg);


            // FO x,y- x: margen derecho, y: distancia al origen.

            //  ADN:alto de letra,ancho de letra (letra horizontal- normal)
            //  ADR:alto de letra,ancho de letra (letra vertical-Mira al Margen )
            //  ADI:alto de letra,ancho de letra (letra horizontal - Invertida)
            //  ADB:alto de letra,ancho de letra (letra vertical-Mira al centro de la etiqueta)




//            SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
//            String formattedDate = df.format(new Date());
            String formattedDate = df.format(datosCabeceraPickingSeleccionada.getFechaEntrega());


            msg = "^FO310," + h + "^ADN,24,10^FD" + "feecha:" + formattedDate + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i + i;

            msg = "^FO5," + h + "^ADN,36,20^FD" + "Preparacion Nro: " + mDataPagoxPicking_Printing.getKey() + "^FS";
            mmOutputStream.write(msg.getBytes());


            h = h + i + i;
            msg = "^FO5," + h + "^ADN,36,20^FD" + datosCabeceraPickingSeleccionada.getUsuarioEntrega() + " " + datosCabeceraPickingSeleccionada.getUsuarioPicking() + "^FS";
            mmOutputStream.write(msg.getBytes());


            h = h + i + i;
            msg = "^FO5," + h + "^ADN,24,15^FD" + "Monto Recaudado: "+datosCabeceraPickingSeleccionada.getMontoRecaudado() + "^FS";
            mmOutputStream.write(msg.getBytes());

//            h = h + i + i;
//            msg = "^FO5," + h + "^ADN,24,10^FD" + mCabeceraOrden.getCliente().getDireccionDeEntrega() + "^FS";
//            mmOutputStream.write(msg.getBytes());
//
//            h = h + i - 5;
//            msg = "^FO5," + h + "^ADN,24,10^FD" + mCabeceraOrden.getCliente().getCiudad() + "^FS";
//            mmOutputStream.write(msg.getBytes());

            h = h + i;

            msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + "Cliente" + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + "Tipo" + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + "Monto" + "^FS";
            mmOutputStream.write(msg.getBytes());

//            msg = "^FO430," + (h + i) + "^ADN,24,10^FD" + "Total" + "^FS";
//            mmOutputStream.write(msg.getBytes());

            h = h + i;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            Double totalEspecial = 0.0;
            Double totalComun = 0.0;


            for (DataSnapshot data : mDataPagoxPicking_Printing.getChildren()) {

                Log.i(LOG_TAG, "printing pago Key- " + data.getKey());
                String pagoKey = data.getKey();
                Pago pago = data.getValue(Pago.class);


                Cliente cliente = pago.getCliente();
                Double monto =pago.getMonto();

                if(cliente.getEspecial()){
                    totalEspecial=totalEspecial+monto;
                }else{
                    totalComun=totalComun+monto;
                }




                msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + cliente.getNombre()+" "+cliente.getApellido() + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + pago.getTipoDePago() + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + format.format(pago.getMonto()) + "^FS";
                mmOutputStream.write(msg.getBytes());

                h = h + i;
            }


            h = h + i * 3;

                msg = "^FO5," + h + "^ADN,24,15^FD" + "Total Especial: " + "^FS";
                mmOutputStream.write(msg.getBytes());


                msg = "^FO310," + h + "^ADN,24,15^FD" + format.format(totalEspecial) + "^FS";
                mmOutputStream.write(msg.getBytes());

                h = h + i + i;

                msg = "^FO5," + h + "^ADN,36,20^FD" + "Total Comun: " + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(totalComun) + "^FS";
                mmOutputStream.write(msg.getBytes());

                h = h + i + i;

////            h = h - i;
//            msg = "^FO5," + h + "^ADN,36,20^FD" + "Total: " + "^FS";
//            mmOutputStream.write(msg.getBytes());
//
//            msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(totalOrden * (1 + mIvaCalculo / 100)) + "^FS";
//            mmOutputStream.write(msg.getBytes());
//
//            // Saldo
//            h = h + i + i;
//            msg = "^FO5," + h + "^ADN,36,20^FD" + "Saldo: " + "^FS";
//            mmOutputStream.write(msg.getBytes());
//
//            msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(mSaldoPrinting.getTotales().getSaldo()) + "^FS";
//            mmOutputStream.write(msg.getBytes());
//
//// Recorro Ordenes sin Compensar
//            for (DataSnapshot data : mDataCabecerasParaCompensarPrinting.getChildren()) {
//
//                Log.i(LOG_TAG, "mDataCabecerasParaCompensarPrinting- " + data.getKey());
//                h = h + i;
//                CabeceraOrden cabeceraOrden = data.getValue(CabeceraOrden.class);
//
//                msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + "orden" + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
//                msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + cabeceraOrden.getNumeroDeOrden() + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
//                msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + format.format(cabeceraOrden.getTotales().getMontoEntregado()) + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
//                msg = "^FO430," + (h + i) + "^ADN,24,10^FD" + format.format(cabeceraOrden.getTotales().getSaldo()) + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
//
//            }
//
//            // Recorro Pagos
//            for (DataSnapshot data : mDataPagosSinCompensarPrinting.getChildren()) {
//
//                h = h + i;
//                Log.i(LOG_TAG, "mDataPagosSinCompensarPrinting- " + data.getKey());
//
//                Pago pago = data.getValue(Pago.class);
//
//                msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + pago.getTipoDePago() + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
////                msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + cabeceraOrden.getNumeroDeOrden() + "^FS";
////                mmOutputStream.write(msg.getBytes());
//
//                msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + format.format(pago.getMonto()) + "^FS";
//                mmOutputStream.write(msg.getBytes());
//
////                msg = "^FO430," + (h + i) + "^ADN,24,10^FD" + format.format(cabeceraOrden.getTotales().getSaldo()) + "^FS";
////                mmOutputStream.write(msg.getBytes());
//
//            }


            // Print a barCode
//            msg="^B8N,100,Y,N";
//            mmOutputStream.write(msg.getBytes());
//
//            msg="^FD1234567";
//            mmOutputStream.write(msg.getBytes());


//
//            msg="^FS";
//            mmOutputStream.write(msg.getBytes());


//            This prints a box one wide by one inch long and the thickness of the line is 2 dots.
////            Width: 1.5 inch; Height: 1 inch; Thickness: 10; Color: default; Rounding: 5
//            msg="^FO0,0^GB300,200,10,,5^FS";
//            mmOutputStream.write(msg.getBytes());


////            Line. horizontal
//            msg="^^FO50,300^GB400,1,4,^FS";
//            mmOutputStream.write(msg.getBytes());

//            //            Print a vertical line.
//            msg="^FO100,50^GB1,400,4^FS";
//            mmOutputStream.write(msg.getBytes());


            // Imprime en inversa.

//            msg="^PR1";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FO100,100";
//            mmOutputStream.write(msg.getBytes());
//            msg="^GB70,70,70,,3^FS";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FO200,100";
//            mmOutputStream.write(msg.getBytes());
//            msg="^GB70,70,70,,3^FS";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FO300,100";
//            mmOutputStream.write(msg.getBytes());
//            msg="^GB70,70,70,,3^FS";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FO400,100";
//            mmOutputStream.write(msg.getBytes());
//            msg="^GB70,70,70,,3^FS";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FO107,110^CF0,70,93";
//            mmOutputStream.write(msg.getBytes());
//            msg="^FR^FDREVERSE^FS";
//            mmOutputStream.write(msg.getBytes());


            // Fin  de comando

            msg = "^XZ";
            mmOutputStream.write(msg.getBytes());

            closeBT();

//            msg="^FD";
//            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
//            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
//            sendButton.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
