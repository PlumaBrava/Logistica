package com.abuseret.logistica;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.abuseret.logistica.data.LogisticaProvider;
import com.abuseret.logistica.data.PickingOrdersDetailColumns;
import com.abuseret.logistica.modelos.CabeceraOrden;
import com.abuseret.logistica.modelos.CabeceraPicking;
import com.abuseret.logistica.modelos.Detalle;
import com.abuseret.logistica.modelos.PrductosxOrden;
import com.abuseret.logistica.modelos.Totales;
import com.abuseret.logistica.rest.PickingOrderProductsAdapter;
import com.abuseret.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.abuseret.logistica.ui.FirebaseRecyclerAdapter;
import com.abuseret.logistica.viewholder.CabeceraPickingViewHolder;
import com.abuseret.logistica.viewholder.CabeceraViewHolder;
import com.abuseret.logistica.viewholder.DetallePickingViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_PICKING;
import static com.abuseret.logistica.util.Constantes.ADAPTER_CABECERA_PICKING;
import static com.abuseret.logistica.util.Constantes.ESQUEMA_PICKING;
import static com.abuseret.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_EN_DELIVERY;
import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_PICKING;
import static com.abuseret.logistica.util.Constantes.PICKING_STATUS_DELIVERY;
import static com.abuseret.logistica.util.Constantes.PICKING_STATUS_INICIAL;
import static com.abuseret.logistica.util.SharePickingOrder.sharePickingOrder;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PickingListFragment extends FragmentBasic {


    private FirebaseRecyclerAdapter<CabeceraPicking, CabeceraPickingViewHolder> mCursorAdapterPickingOrder;
    private FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder> mCustomsOrdersCursorAdapter;
    private FirebaseRecyclerAdapter<Detalle, DetallePickingViewHolder> mCursorAdapterTotalProductos;

    private Query totalProductos;

    private RecyclerView recyclerViewPickingOrder;
    private RecyclerView recyclerViewCustomOrderInPickingOrder;
    private RecyclerView recyclerViewTotalProductos;

    private CardView mPickingOrderTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private TextView mCreationDate;
    private View emptyViewCustomOrder;
    private CabeceraOrden mCabeceraOrdenDato;
    private DataSnapshot mProductosEnOrdenDatos;
    private DataSnapshot mOrdenesEnPickingDatos;

    ArrayList<Task> taskList = new ArrayList<Task>();
    Task<Void> allTask;

    private Detalle mDetalleAnterior;
    private Detalle mDetalleDato;//Tiene los datos a ser cargados en una operacion
    private Double mCantidadDato;//Tiene los datos a ser cargados en una operacion
    private String mproductKeyDato;
    private View emptyViewTotalProducts;

//    private LinearLayout mLinearProductos;
//    private LinearLayout mLinearOrders;

    private PickingOrdersHandler mPickingOrdersHandler;


    private boolean mTwoPane;

    private static final String LOG_TAG = PickingListFragment.class.getSimpleName();


    public void setPickingOrdersHandler(PickingOrdersHandler pOH) {
        mPickingOrdersHandler = pOH;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mPickingOrdersHandler = (PickingOrdersHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {

            //TODO: revisar que se hace con esta parte del codigo que se usaba para que no quede vacio el fragmento al hacer swipe
//            getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, this);
//            getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
//            getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void savePhoto(Bitmap bitmap) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.picking_list_fragment, container, false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewPickingOrder = (RecyclerView) rootView.findViewById(R.id.pickingOrder_list);
        mPickingOrderTile = (CardView) rootView.findViewById(R.id.pickingNumbertitleID);
//        mLinearProductos =(LinearLayout)rootView.findViewById(R.id.linearProductos);
//        mLinearOrders =(LinearLayout)rootView.findViewById(R.id.linearOrders);

        ImageButton fab_save_picking = (ImageButton) mPickingOrderTile.findViewById(R.id.save_picking_Button);
        fab_save_picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//Actualiza el comentario
                mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(PICKING_STATUS_INICIAL)).child(String.valueOf(MainActivity.getmPickingOrderSelected())).child("comentario")
                        .setValue(mTilePickingComent.getText().toString());

                mPickingOrdersHandler.onPickingOrderSelected(0);
                mPickingOrderTile.setVisibility(View.GONE);
//                mLinearProductos.setVisibility(View.GONE);
//                mLinearOrders.setVisibility(View.GONE);
                recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
                recyclerViewTotalProductos.setVisibility(View.GONE);
                recyclerViewPickingOrder.setVisibility(View.VISIBLE);


            }
        });


        mPickingOrderTile.setVisibility(View.GONE);
        mTilePickingOrderNumber = (TextView) mPickingOrderTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent = (EditText) mPickingOrderTile.findViewById(R.id.TitlepickingOrderComents);
        mCreationDate = (TextView) mPickingOrderTile.findViewById(R.id.titlePicckinOder_creationdate);
        recyclerViewPickingOrder.setLayoutManager(layoutManager);

        final View emptyViewPickingOrders = rootView.findViewById(R.id.recyclerview_pickingOrders_empty);
        Query pickingOrderQuery = refPicking_6_List(PICKING_STATUS_INICIAL);
//         = mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(PICKING_STATUS_INICIAL));
        mCursorAdapterPickingOrder = new FirebaseRecyclerAdapter<CabeceraPicking, CabeceraPickingViewHolder>(CabeceraPicking.class, R.layout.picking_orders_list_content,
                CabeceraPickingViewHolder.class, pickingOrderQuery) {


            @Override
            protected void populateViewHolder(CabeceraPickingViewHolder viewHolder, final CabeceraPicking model, int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                Log.i(LOG_TAG, "populateViewHolder :CabeceraRef: " + CabeceraRef.toString());
                viewHolder.mSharePickingOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sharePickingOrder(getContext(), String.valueOf(model.getNumeroDePickingOrden()), model.getComentario());
                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(LOG_TAG, "View.OnClick: " + model.getNumeroDePickingOrden());

                        mTilePickingComent.setText(model.getComentario());
                        mTilePickingOrderNumber.setText(String.valueOf(model.getNumeroDePickingOrden()));
                        mTilePickingComent.setVisibility(View.VISIBLE);

                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                        mCreationDate.setText(sfd.format(new Date(model.getFechaDeCreacion())));

                        mPickingOrdersHandler.onPickingOrderSelected(model.getNumeroDePickingOrden());

                        mCursorAdapterPickingOrder.notifyDataSetChanged();
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerViewPickingOrder.setVisibility(View.GONE);

//                            mLinearOrders.setVisibility(View.VISIBLE);
//                            mLinearProductos.setVisibility(View.VISIBLE);
//                        totalProductos = refPickingTotal_7_List(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
                        muestraTotalesPicking();
                        muestraOrdenesEnPicking();
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerViewCustomOrderInPickingOrder.setVisibility(View.VISIBLE);
                        recyclerViewTotalProductos.setVisibility(View.VISIBLE);
                        mPickingOrderTile.setVisibility(View.VISIBLE);

                    }
                });
                emptyViewPickingOrders.setVisibility(View.GONE);
            }

            @Override
            protected void onItemDismissHolder(CabeceraPicking model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(CabeceraPicking model, int position) {
                pasarPickingAEntrega(model);
            }

        };


        recyclerViewPickingOrder.setAdapter(mCursorAdapterPickingOrder);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapterPickingOrder, ADAPTER_CABECERA_PICKING);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewPickingOrder);


        emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
//        emptyViewTotalProducts.setVisibility(View.GONE);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);
        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerViewTotalProductos.setVisibility(View.GONE);

        recyclerViewCustomOrderInPickingOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);

        recyclerViewCustomOrderInPickingOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_custom_empty);
        emptyViewCustomOrder.setVisibility(View.GONE);


//        recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
//        emptyViewProductosEnStock.setVisibility(View.GONE);

//        mLinearOrders.setVisibility(View.GONE);
//        mLinearProductos.setVisibility(View.GONE);

        if(savedInstanceState!=null && MainActivity.getmPickingOrderSelected()>0){
//            mTilePickingComent.setText(model.getComentario());
            mTilePickingOrderNumber.setText(String.valueOf(MainActivity.getmPickingOrderSelected()));
            mTilePickingComent.setVisibility(View.VISIBLE);
            mTilePickingComent.setText(savedInstanceState.getString("comentario"));
            mCreationDate.setText(savedInstanceState.getString("fecha"));



            Log.i(LOG_TAG, "savedInstanceStatePicking:  " + savedInstanceState.toString());

            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

//            mCreationDate.setText(sfd.format(new Date(model.getFechaDeCreacion())));

//            mPickingOrdersHandler.onPickingOrderSelected(model.getNumeroDePickingOrden());

            mCursorAdapterPickingOrder.notifyDataSetChanged();
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
            recyclerViewPickingOrder.setVisibility(View.GONE);

//                            mLinearOrders.setVisibility(View.VISIBLE);
//                            mLinearProductos.setVisibility(View.VISIBLE);
//                        totalProductos = refPickingTotal_7_List(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
            muestraTotalesPicking();
            muestraOrdenesEnPicking();
            mCursorAdapterTotalProductos.notifyDataSetChanged();
            recyclerViewCustomOrderInPickingOrder.setVisibility(View.VISIBLE);
            recyclerViewTotalProductos.setVisibility(View.VISIBLE);
            mPickingOrderTile.setVisibility(View.VISIBLE);


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



    public void muestraOrdenesEnPicking() {
        Query listadoOrdenesEnPickingQuery = refCabeceraOrden_2_List(ORDEN_STATUS_PICKING, MainActivity.getmPickingOrderSelected());
        Log.i(LOG_TAG, "muestraOrdenesEnPicking:MainActivity.getmPickingOrderSelected(): " + MainActivity.getmPickingOrderSelected());
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
//                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(CabeceraOrden model, int position) {
                Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getNumeroDeOrden());
                if (MainActivity.mPickingOrderSelected == 0) {

                    muestraMensajeEnDialogo(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
                } else {

                    Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getNumeroDeOrden());
                    pasarOrdenAInicial(model);

                }
            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {


                if (MainActivity.mPickingOrderSelected == 0) {

                    muestraMensajeEnDialogo(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
                } else {

                    Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getNumeroDeOrden());
                    pasarOrdenAInicial(model);


                }
            }
        };


        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(mCustomsOrdersCursorAdapter, ADAPTER_CABECERA_ORDEN_EN_PICKING);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInPickingOrder);

        recyclerViewCustomOrderInPickingOrder.setAdapter(mCustomsOrdersCursorAdapter);
    }

    public void muestraTotalesPicking() {
        totalProductos = refPickingTotal_7_List(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
        mCursorAdapterTotalProductos = new FirebaseRecyclerAdapter<Detalle, DetallePickingViewHolder>(Detalle.class, R.layout.picking_total_item,
                DetallePickingViewHolder.class, totalProductos) {
            @Override
            protected void populateViewHolder(final DetallePickingViewHolder viewHolder, final Detalle model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);

                emptyViewTotalProducts.setVisibility(View.GONE);
                Log.i(LOG_TAG, "adapter:CabeceraRef: " + CabeceraRef.toString());

                // Set click listener for the whole post view
                final String productKey = CabeceraRef.getKey();
                Log.i(LOG_TAG, "adapter:orderKey: " + productKey);



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
                                mDetalleAnterior = model;
//                                putExtraFirebase_Fragment(intent);
//
////                                intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
////                                intent.putExtra(EXTRA_PRODUCT, model.getProducto());
//                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//
//                                startActivity(intent);

                                showDialogNumberPicker(mProductKey);
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

    public interface PickingOrdersHandler {
        void onPickingOrderSelected(long pickingOrderID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(LOG_TAG, "onSaveInstanceState write ");


        outState.putString("comentario", mTilePickingComent.getText().toString());
        outState.putString("fecha", mCreationDate.getText().toString());
    }

    @Override
    public void onStart() {
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


    public void saveNewPickingOrder() {

        mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child("cabecera").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Totales cabecera_picking = mutableData.getValue(Totales.class);
                if (cabecera_picking == null) {
                    Log.d(LOG_TAG, "orden: cabecera Null");
                    cabecera_picking = new Totales();
                    cabecera_picking.setCantidadDeOrdenesPicking(1);
                } else {
                    Log.d(LOG_TAG, "orden: cabecera ID" + cabecera_picking.getCantidadDeOrdenesPicking());
                    cabecera_picking.setCantidadDeOrdenesPicking(cabecera_picking.getCantidadDeOrdenesPicking() + 1);                            // Unstar the post and remove self from stars
                }

                // Set value and report transaction success
                mutableData.setValue(cabecera_picking);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "orden:onComplete:  databaseError" + databaseError);
                Log.d(LOG_TAG, "orden:onComplete: boolean b" + commited);
                Totales ordenes1a = dataSnapshot.getValue(Totales.class);
                long numeroOrdenPicking = ordenes1a.getCantidadDeOrdenesPicking();
                Log.d(LOG_TAG, "orden:onComplete: ID " + numeroOrdenPicking);

                if (commited) {
                    // preparo la cabecera de orden del cliente

                    //Totales en cero
                    Totales totales = new Totales(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                    CabeceraPicking cabeceraPicking = new CabeceraPicking(mUserKey, numeroOrdenPicking, PICKING_STATUS_INICIAL);
                    cabeceraPicking.setTotales(totales);
                    cabeceraPicking.setUsuarioCreador(mUsuario.getUsername());
                    Map<String, Object> cabeceraOrdenValues = cabeceraPicking.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
//7

//                    childUpdates.put(NODO_PICKING + mEmpresaKey +"/"+PICKING_STATUS_INICIAL+"/"+ numeroOrden, cabeceraOrdenValues);
                    childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL, String.valueOf(numeroOrdenPicking)), cabeceraOrdenValues);
                    mDatabase.updateChildren(childUpdates);
                }

            }
        });


    }


    public void showDialogNumberPicker(final String productKey) {


            Log.i("Picker", " KG o Metro " + mDetalleAnterior.getProducto().getTipoUnidad());
            if (mDetalleAnterior.getProducto().getTipoUnidad().equals("Kg") || mDetalleAnterior.getProducto().getTipoUnidad().equals("Metro")) {
                Log.i("Picker", " KG o Metro");

                final Dialog d = new Dialog(getContext());
                d.setTitle(getString(R.string.quantityPicker));
                d.setContentView(R.layout.dialog_text_picker);
                Button b1 = (Button) d.findViewById(R.id.button1);
                Button b2 = (Button) d.findViewById(R.id.button2);
                final EditText np = (EditText) d.findViewById(R.id.cantidad_text);
                if (mDetalleAnterior.getCantidadPicking() == 0.0) {
                    Log.d("Picker", "mDetalleAnterior.getCantidadPicking() null ");

                    np.setText(String.valueOf(mDetalleAnterior.getCantidadOrden()));
                } else {
                    Log.d("Picker", "mDetalleAnterior.getCantidadPicking() != null ");
                    np.setText(String.valueOf(mDetalleAnterior.getCantidadPicking()));
                }
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("detalle1", "showDialogNumberPicker-detalle) " + mDetalleAnterior.getCantidadPicking());
                        Log.d("detalle1", "showDialogNumberPicker-np.getValue() " + np.getText().toString());
                        abmDetalleDePicking(Double.valueOf(np.getText().toString()), productKey, mDetalleAnterior);


                        d.dismiss();
                    }

                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();


            } else if (mDetalleAnterior.getProducto().getTipoUnidad().equals("Unidad")) {

                final Dialog d = new Dialog(getContext());
                d.setTitle(getString(R.string.quantityPicker));
                d.setContentView(R.layout.dialog_number_picker);
                Button b1 = (Button) d.findViewById(R.id.button1);
                Button b2 = (Button) d.findViewById(R.id.button2);
                final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
                Log.d("Picker", "Default: " + mDetalleAnterior.getProducto().getCantidadDefault());
                Log.d("Picker", "Man: " + mDetalleAnterior.getProducto().getCantidadMaxima());
                Log.d("Picker", "Mix: " + mDetalleAnterior.getProducto().getCantidadMinima());
                Log.d("Picker", "Picking: " + mDetalleAnterior.getCantidadPicking());
                Log.d("Picker", "Orden: " + mDetalleAnterior.getCantidadOrden());
                Log.d("Picker", "Entrega: " + mDetalleAnterior.getCantidadEntrega());

                Log.d("Picker", "Picking: intValue " + mDetalleAnterior.getCantidadPicking().intValue());
                Log.d("Picker", "Orden:intValue " + mDetalleAnterior.getCantidadOrden().intValue());
                Log.d("Picker", "Entrega:intValue " + mDetalleAnterior.getCantidadEntrega().intValue());

                np.setMaxValue(mDetalleAnterior.getProducto().getCantidadMaxima());
                if (mDetalleAnterior.getCantidadPicking() == 0.0) {
                    Log.d("Picker", "mDetalleAnterior.getCantidadPicking() null ");

                    np.setValue(mDetalleAnterior.getCantidadOrden().intValue());
                } else {
                    Log.d("Picker", "mDetalleAnterior.getCantidadPicking() != null ");
                    np.setValue(mDetalleAnterior.getCantidadPicking().intValue());
                }
                np.setMinValue(mDetalleAnterior.getProducto().getCantidadMinima());
                np.setWrapSelectorWheel(true);
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    }
                });
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("detalle1", "showDialogNumberPicker-detalle) " + mDetalleAnterior.getCantidadPicking());
                        Log.d("detalle1", "showDialogNumberPicker-np.getValue() " + np.getValue());
                        abmDetalleDePicking((double) np.getValue(), productKey, mDetalleAnterior);


                        d.dismiss();
                    }

                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();


            }

    }

    // abmDetalleDeOrden
    // si Detalle viene con cantidad =0 se entiende que es un producto Nuevo.
    // Si cantidad es cero se entiende que se saca de la orden
    public void abmDetalleDePicking(final Double cantidad, String productoKey, Detalle detalle) {

        if (hayTareaEnProceso()) {
            return;
        }

        mCantidadDato = cantidad; // Es la nueva cantidad que queremos tener
        mproductKeyDato = productoKey;
        mDetalleDato = detalle; // tiene los valores del detalle que se quiere modificar


        Log.i(LOG_TAG, "abmDetalleDeOrden cantidad " + cantidad + " productokey " + productoKey + " Producto " + detalle.getProducto().getNombreProducto());


        readBlockPickingTotal(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected(), productoKey);/*7*/
        taskList.add(mPickingTotalTask.get(0));


        Task<Void> allTask;
        allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                Detalle totalPickingDetalle;

                Detalle nuevoDetalleOrden = mDetalleDato.copy();


                DataSnapshot dataDetallePicking = (DataSnapshot) mPickingTotalTask.get(0).getResult();

                // do something with db data?
                if (dataDetallePicking.exists()) {
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask key =" + dataDetallePicking.getKey());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetallePicking.hasChildren());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetallePicking.getChildrenCount());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.exists() =" + dataDetallePicking.exists());

                    dataDetallePicking.getKey();
                    totalPickingDetalle = dataDetallePicking.getValue(Detalle.class);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask =" + dataDetallePicking.getKey() + "- nombre Producto" + dataDetallePicking.getValue(Detalle.class).getProducto().getNombreProducto()
                            + "- cantidad Orden" + dataDetallePicking.getValue(Detalle.class).getCantidadOrden());

                } else {
                    // si no existe el producto en el listado se crea.
                    totalPickingDetalle = new Detalle(0.0, mDetalleDato.getProducto(), null);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask = NuLL- ");

                }


                // Todo: procesar los datos
                // liberar los semaforos para grabar


                totalPickingDetalle.liberar();

                //Actualizo Detalle de Picking 7
                totalPickingDetalle.modificarCantidadProductoDePicking(mCantidadDato);


                Map<String, Object> totalPickingValues = totalPickingDetalle.toMap();


                Map<String, Object> childUpdates = new HashMap<>();




/*7 */
                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected(), mproductKeyDato), totalPickingValues);


                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren-onFailure " + e.toString());
                        liberarRecusosTomados();
                        liberarArrayTaskConBloqueos();

//                        liberarRecusosTomados(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());
//                        mCantidadTotal.setText(String.valueOf( mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
//                        mKeyList.add(mproductKeyDato);
                        liberarArrayTaskCasoExitoso();

                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren - OnCompleteListener task.isSuccessful():" + task.isSuccessful());
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                    }
                });
//

            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "abmDetalleDeOrden addOnFailureListener= allTask" + e.toString());
                liberarRecusosTomados();
                liberarArrayTaskConBloqueos();


            }
        });


    }

    public void saveCantidadPicking(PickingOrderProductsAdapter.ViewHolder vh, String cantidad) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (vh.mRefPickingDetail != 0) {
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrdersDetail.withId(vh.mRefPickingDetail));
            builder.withValue(PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL, cantidad);

            batchOperations.add(builder.build());
        } else {
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrdersDetail.CONTENT_URI);

            builder.withValue(PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL, MainActivity.getmPickingOrderSelected());
            builder.withValue(PickingOrdersDetailColumns.REF_PRODUCT_PICKING_ORDERS_DETAIL, vh.mRefProduct);
            builder.withValue(PickingOrdersDetailColumns.PRODUCT_NAME_PICKING_ORDERS_DETAIL, vh.mTextViewNombre.getText().toString());
            builder.withValue(PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL, cantidad);
            batchOperations.add(builder.build());
        }


        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);


        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, getString(R.string.InformeError), e);
        }


    }

    private void pasarOrdenAInicial(final CabeceraOrden cabeceraOrden) {
        Log.i(LOG_TAG, "pasarOrdenAInicial Numero de orden- " + cabeceraOrden.getNumeroDeOrden());
        if (hayTareaEnProceso()) {
            return;
        }


        mCabeceraOrdenDato = cabeceraOrden;

        readBlockCabeceraOrden(cabeceraOrden.getNumeroDeOrden());
        mCabeceraOrdenTask.get(0).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraOrden dataCabecera = ((DataSnapshot) mCabeceraOrdenTask.get(0).getResult()).getValue(CabeceraOrden.class);
                    refDetalleOrden_4_ListaXOrden(dataCabecera.getNumeroDeOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mProductosEnOrdenDatos = dataSnapshot;
                            readBlockPicking(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
                            taskList.add(mPickingTask);
                            int i = 0;
                            for (DataSnapshot snapshot : mProductosEnOrdenDatos.getChildren()) {
                                Log.i(LOG_TAG, "pasarOrdenAInicial onDetalleOrden Key- " + snapshot.getKey());
                                String productKey = snapshot.getKey();
                                readBlockTotalInicial(productKey);
                                taskList.add(mTotalInicialTask.get(i)); /*5*/
                                readBlockPickingTotal(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected(), productKey);/*7*/
                                taskList.add(mPickingTotalTask.get(i));
                                i++;
                            }

                            allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));

                            allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(LOG_TAG, "pasarOrdenAInicial Completo el bloqueo- ");

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    Map<String, Object> totalInicialDetalleValues = null;
                                    Map<String, Object> pickingTotalValues = null;
                                    int i = 0;
                                    for (DataSnapshot productoEnOrden : mProductosEnOrdenDatos.getChildren()) {
                                        // busca el producto dentro de la orden
                                        String productKey = productoEnOrden.getKey();
                                        Detalle detalleOrden = productoEnOrden.getValue(Detalle.class);

                                        // creamos una copia con cantidad cero para usar en otras estructuras.
                                        Detalle detalleOrdenAux = detalleOrden.copy();
                                        detalleOrdenAux.modificarCantidadProductoDeOrden(0.0); // modificamos la cantidad a Cero para usarla para el calculo de Total


                                        // Actualizacion de Totales en Ordenes (3)


                                        Detalle detalleOrdenTotalInicial = ((DataSnapshot) mTotalInicialTask.get(i).getResult()).getValue(Detalle.class);
                                        if (detalleOrdenTotalInicial == null) {
                                            // si es nulo se trataria de un error puesto que existe en la orden y deberia estar sumado en el total
                                            detalleOrdenTotalInicial = new Detalle(0.0, detalleOrden.getProducto(), null);
                                            detalleOrdenTotalInicial.modificarCantidadEnTotalInicial(detalleOrden, detalleOrdenAux);
                                            Log.i(LOG_TAG, "pasarOrdenAInicial TotalInicial Detalle = NuLL- ");
                                        } else {
                                            detalleOrdenTotalInicial.modificarCantidadEnTotalInicial(detalleOrden, detalleOrdenAux);

                                        }
                                        detalleOrdenTotalInicial.liberar();
                                        totalInicialDetalleValues = detalleOrdenTotalInicial.toMap();
                                            /*3 */
                                        childUpdates.put(nodoTotalInicial_3(productKey), totalInicialDetalleValues);


                                        // Actualizacion de totales en Picking (7)


                                        Detalle detallePickingTotal = ((DataSnapshot) mPickingTotalTask.get(i).getResult()).getValue(Detalle.class);

                                        if (detallePickingTotal == null) {
                                            // si es nulo se trataria de un error puesto que existe en la orden y deberia estar sumado en el total
                                            Log.i(LOG_TAG, "pasarOrdenAInicial etallePickingTotal  Detalle = NuLL- ");
                                        } else {
                                            detallePickingTotal.modificarCantidadEnTotalInicial(detalleOrdenAux, detalleOrden);
                                            if (detallePickingTotal.getCantidadOrden() == 0) {
                                                pickingTotalValues = null;
                                            } else {
                                                detallePickingTotal.liberar();
                                                pickingTotalValues = detallePickingTotal.toMap();
                                            }

                                        }




                                            /*7 */

                                        childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected(), productKey), pickingTotalValues);


                                            /*5 Prodcutos en Ordenes */
                                        PrductosxOrden detallexOrden = new PrductosxOrden(mCabeceraOrdenDato.getCliente(), detalleOrden);
                                        Log.i(LOG_TAG, "orden:SaveDetalleInicialTotales detalle NuLL- ");
                                        childUpdates.put(nodoProductosXOrdenInicial_5(productKey, mCabeceraOrdenDato.getNumeroDeOrden()), detallexOrden.toMap());

                                            /*4 */ //No se modifica, queda igual
//                                            childUpdates.put(nodoDetalleOrden_4(mCabeceraOrdenDato.getNumeroDeOrden(), productKey), null);

                                        i++;
                                    }

                                    // Actualizacion de totales en Picking (6)
                                    CabeceraPicking cabeceraPicking = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);
                                    cabeceraPicking.liberar();
                                    childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL, ((DataSnapshot) mPickingTask.getResult()).getKey()), cabeceraPicking.toMap());


                                /*2 */
                                    mCabeceraOrdenDato.setEstado(ORDEN_STATUS_INICIAL);
                                    mCabeceraOrdenDato.setNumeroDePickingOrden(0);
                                    mCabeceraOrdenDato.liberar();
                                    childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_PICKING, mCabeceraOrdenDato.getNumeroDeOrden(), MainActivity.getmPickingOrderSelected()), null);
                                    childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_INICIAL, mCabeceraOrdenDato.getNumeroDeOrden()), mCabeceraOrdenDato.toMap());
                                    childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrdenDato.getNumeroDeOrden()), mCabeceraOrdenDato.toMap());

                                    mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.i(LOG_TAG, "pasarOrdenAInicial updateChildren-onFailure " + e.toString());

                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            liberarArrayTaskCasoExitoso();
                                            Log.i(LOG_TAG, "pasarOrdenAInicial - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                                        }
                                    });
                                }


                            });
                            allTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    liberarRecusosTomados();
                                    liberarArrayTaskConBloqueos();
                                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + "allTask");


                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.i(LOG_TAG, "pasarOrdenAInicial Operacion fallo- " + task.getException().toString());
                    liberarRecusosTomados();
                    liberarArrayTaskConBloqueos();
                    muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Orden");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                liberarRecusosTomados();
                liberarArrayTaskConBloqueos();
                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Orden");
            }
        });
    }

    private void pasarPickingAEntrega(final CabeceraPicking cabeceraPicking) {
        Log.i(LOG_TAG, "pasarPickingAEntrega Numero de picking- " + cabeceraPicking.getNumeroDePickingOrden());
        if (hayTareaEnProceso()) {
            return;
        }


        // leo y bloqueo picking
        readBlockPicking(PICKING_STATUS_INICIAL, cabeceraPicking.getNumeroDePickingOrden());


        mPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraPicking cabeceraPicking1 = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);


                    Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key lee cabeceras");
                    //leo las ordenes asociadas al picking y las bloqueo
                    refCabeceraOrden_2_List(ORDEN_STATUS_PICKING, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden lee cabeceras Llegaron los datos");
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key " + dataSnapshot.getKey());
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden Ref " + dataSnapshot.getRef());
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden ChildrenCount" + dataSnapshot.getChildrenCount());
                            mOrdenesEnPickingDatos = dataSnapshot;
                            int i = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String nroOrden = snapshot.getKey();
                                Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key " + nroOrden);

//                        CabeceraOrden cabeceraOrden= (CabeceraOrden) snapshot.getValue(CabeceraOrden.class);
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

                                        readBlockPickingTotal(PICKING_STATUS_INICIAL, cabeceraPicking.getNumeroDePickingOrden(), productkey);
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
                                                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL, cabeceraPicking.getNumeroDePickingOrden(), productKey), null);
                                                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden(), productKey), pickingTotal.toMap());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega Product key- " + productKey + " " + pickingTotal.getProducto().getNombreProducto());

                                            }

                                            // Actualizacion de Cabecera de Picking (6)
                                            CabeceraPicking cabeceraPicking = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);
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
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), null);
                                            childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY, String.valueOf(cabeceraPicking.getNumeroDePickingOrden())), cabeceraPicking.toMap());

                                            for (int a = 0; a < mCabeceraOrdenTask.size(); a++) {
                                                DataSnapshot cabeceraOrdenes = ((DataSnapshot) mCabeceraOrdenTask.get(a).getResult());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega cabeceraOrdenes key = " + cabeceraOrdenes.getKey());
                                                Log.i(LOG_TAG, "pasarPickingAEntrega cabeceraOrdenes ref = " + cabeceraOrdenes.getRef());

                                                String nroDeOrden = cabeceraOrdenes.getKey();
                                                CabeceraOrden cabeceraOrden = cabeceraOrdenes.getValue(CabeceraOrden.class);
                                                Log.i(LOG_TAG, "pasarPickingAEntrega nroDeOrden = " + nroDeOrden);
                                                Log.i(LOG_TAG, "pasarPickingAEntrega nroDeOrden = " + cabeceraOrden.getNumeroDeOrden());

                                    /*2 */
                                                cabeceraOrden.setEstado(ORDEN_STATUS_EN_DELIVERY);
                                                cabeceraOrden.liberar();
                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_PICKING, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), null);

                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERY, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), cabeceraOrden.toMap());
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
}