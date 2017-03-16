package com.nextnut.logistica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Totales;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraPickingViewHolder;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.DetalleDeliveryTotalProdutctosViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_DELIVEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_NRO_PICKIG;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_DELIVERED_PARA_COMPENSAR;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_EN_DELIVERING;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_PICKING;
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

    private long mCustomOrderIdSelected;

    View emptyViewTotalProducts;
    private Query totalProductos;
    //    private PickingOrdersCursorAdapter mPickinOrdersAdapter;
//    private PickingOrderProductsAdapter mCursorAdapterTotalProductos;
//    private CustomsOrdersCursorAdapter mCustomsOrdersCursorAdapter;
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
    private TextView mCreationDate;

    private ArrayList<Task> taskList = new ArrayList<Task>();
    private Task<Void> allTask;

    private DataSnapshot mProductosEnOrdenDatos;
    private DataSnapshot mOrdenesEnPickingDatos;

    private CabeceraPicking datosCabeceraPickingSeleccionada;

    private FloatingActionButton fab_new;
    private FloatingActionButton fab_delete;
    private static final int CUSTOM_ORDER_LOADER = 0;
    private static final int PICKING_ORDER_LOADER = 1;
    private static final int PICKING_LOADER_TOTAL_PRODUCTOS = 2;
    private int mItem = 0;

    private LinearLayout mLinearProductos;
    private LinearLayout mLinearOrders;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = DeliveryListFragment.class.getSimpleName();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);


        mDeliveryOrderTile = (CardView) rootView.findViewById(R.id.deliveryOrderNumbertitleID);
        mDeliveryOrderTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDeliveryOrderTile.setVisibility(View.GONE);
                recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);
                recyclerViewTotalProductos.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        mDeliveryOrderTile.setVisibility(View.GONE);
        mTilePickingOrderNumber = (TextView) mDeliveryOrderTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent = (EditText) mDeliveryOrderTile.findViewById(R.id.TitlepickingOrderComents);
        mCreationDate = (TextView) mDeliveryOrderTile.findViewById(R.id.titlePicckinOder_creationdate);

        mLinearProductos = (LinearLayout) rootView.findViewById(R.id.linearProductos);
        mLinearOrders = (LinearLayout) rootView.findViewById(R.id.linearOrders);


        View emptyViewPicking = rootView.findViewById(R.id.recyclerview_pickingOrders_empty);
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

                        if (isNetworkAvailable(getApplicationContext())) {

                            datosCabeceraPickingSeleccionada = model.Copy();
                            mTilePickingComent.setText(model.getComentario());
                            mTilePickingOrderNumber.setText(String.valueOf(model.getNumeroDePickingOrden()));
                            mTilePickingComent.setVisibility(View.VISIBLE);
                            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                            mCreationDate.setText(sfd.format(new Date(model.getFechaDeCreacion())));

                            bloqueoPickingParaTrabajoOffLine();

                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putLong(getString(R.string.PickingOrderSeleccionada), model.getNumeroDePickingOrden());
                            editor.putString(getString(R.string.PickingOrderCommentSeleccionada), model.getComentario());
                            editor.putString(getString(R.string.PickingOrderFechaSeleccionada), mCreationDate.getText().toString());
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
                        } else {
                            onDialogAlert("Para seleccionar Necesita estar conectado a Internet");
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

            }
        };

//        mPickinOrdersAdapter = new PickingOrdersCursorAdapter(
//                getContext(),
//                null,
//                emptyViewPicking,
//                new PickingOrdersCursorAdapter.PinckingOrdersCursorAdapterOnClickHandler() {
//                    @Override
//                    public void onClick(long id, PickingOrdersCursorAdapter.ViewHolder vh) {
//                        mTilePickingComent.setText(vh.mpickingOrderComents.getText());
//                        mTilePickingOrderNumber.setText(vh.mPickingOrderNumber.getText());
//                        mTilePickingComent.setVisibility(View.VISIBLE);
//                        mCreationDate.setText(vh.mCreationDate.getText());
//                        mIDPickingOrderSelected = id;
//                        mPickinOrdersAdapter.notifyDataSetChanged();
//
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
//                        recyclerView.setVisibility(View.GONE);
//                        mLinearOrders.setVisibility(View.VISIBLE);
//                        mLinearProductos.setVisibility(View.VISIBLE);
//                        mDeliveryOrderTile.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onDataChange() {
//
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
//                        mPickinOrdersAdapter.notifyDataSetChanged();
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onItemDismissCall(long cursorID) {
//                        mIDPickingOrderSelected = cursorID;
//                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
//                        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_INICIAL);
//                        batchOperations.add(builder.build());
//
//
//                        try {
//                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                            onDataChange();
//                        } catch (RemoteException | OperationApplicationException e) {
//                        } finally {
//                            onDataChange();
//                            upDateWitget(getContext());
//                        }
//                    }
//
//                    @Override
//                    public void onItemAceptedCall(long cursorID) {
//
//                        mIDPickingOrderSelected = cursorID;
//                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
//                        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_CERRADA);
//                        batchOperations.add(builder.build());
//                        try {
//                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                            onDataChange();
//                        } catch (RemoteException | OperationApplicationException e) {
//
//                        } finally {
//                            onDataChange();
//                            upDateWitget(getContext());
//                        }
//                    }
//
//                    @Override
//                    public void onDialogAlert(String message) {
//                        DialogAlerta dFragment = DialogAlerta.newInstance(message);
//                        dFragment.show(getFragmentManager(), "Dialog Fragment");
//                    }
//
//                    @Override
//                    public void sharePickingorder(PickingOrdersCursorAdapter.ViewHolder vh) {
//                        sharePickingOrder(getContext(), vh.mPickingOrderNumber.getText().toString(), mTilePickingComent.getText().toString());
//                    }
//                }
//        );


        recyclerView.setAdapter(mPickinOrdersAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPickinOrdersAdapter, ADAPTER_CABECERA_DELIVEY);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        ImageButton fab_save = (ImageButton) mDeliveryOrderTile.findViewById(R.id.save_picking_Button);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable(getApplicationContext())) {
                    mDeliveryOrderTile.setVisibility(View.GONE);
                    mLinearProductos.setVisibility(View.GONE);
                    mLinearOrders.setVisibility(View.GONE);
                    recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);
                    recyclerViewTotalProductos.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove(getString(R.string.PickingOrderSeleccionada));
                    editor.remove(getString(R.string.PickingOrderCommentSeleccionada));
                    editor.remove(getString(R.string.PickingOrderFechaSeleccionada));
                    editor.commit();
                    desbloqueoPickingParaTrabajoOffLine();
                }else{
                        onDialogAlert("Para seleccionar Necesita estar conectado a Internet");

                }
            }
        });

        // Productos

        emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));


//        mCursorAdapterTotalProductos = new PickingOrderProductsAdapter(getContext(), null,
//                emptyViewTotalProducts,
//                new PickingOrderProductsAdapter.ProductCursorAdapterOnClickHandler() {
//                    @Override
//                    public void onClick(long id, PickingOrderProductsAdapter.ViewHolder vh) {
//
//                    }
//                }
//        );


        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);
        recyclerViewCustomOrderInDeliveyOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);
        recyclerViewCustomOrderInDeliveyOrder.setLayoutManager(new LinearLayoutManager(getContext()));


        // Custom Orders
        emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_custom_empty);
//        mCustomsOrdersCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
//            @Override
//            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {
//
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//
////                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);
//                    arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
//                    CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();
//                    fragment.setArguments(arguments);
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .addToBackStack(null)
//                            .replace(R.id.customorder_detail_container, fragment)
//                            .commit();
//                } else {
//                    Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
//                    intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY);
////                    intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, id);
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
//                        startActivity(intent, activityOptions.toBundle());
//
//                    } else {
//                        startActivity(intent);
//                    }
//
//                }
//
//
//            }
//
//            @Override
//            public void onMakeACall(String ContactID) {
//                makeTheCall(getActivity(), ContactID);
//            }
//
//            @Override
//            public void onDialogAlert(String message) {
//
//            }
//
//            @Override
//            public void onItemDismissCall(long cursorID) {
//                mCustomOrderIdSelected = cursorID;
//            }
//
//            @Override
//            public void onItemAceptedCall(long cursorID) {
//                mCustomOrderIdSelected = cursorID;
//                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
//                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_DELIVERED);
//                batchOperations.add(builder.build());
//                try {
//                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                } catch (RemoteException | OperationApplicationException e) {
//
//                } finally {
//                    onDataChange();
//                }
//
//
//            }
//
//            @Override
//            public void onDataChange() {
//
//                mCursorAdapterTotalProductos.notifyDataSetChanged();
//                mPickinOrdersAdapter.notifyDataSetChanged();
//                mCursorAdapterTotalProductos.notifyDataSetChanged();
//            }
//        }
//
//        );
//
//        recyclerViewCustomOrderInDeliveyOrder.setAdapter(mCustomsOrdersCursorAdapter);

//        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallbackDeleveyCustomOrder(mCustomsOrdersCursorAdapter);
//        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
//        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInDeliveyOrder);


        mLinearOrders.setVisibility(View.GONE);
        mLinearProductos.setVisibility(View.GONE);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        long nroPickingAlmacenado = sharedPref.getLong(getString(R.string.PickingOrderSeleccionada), 0);
        String comentarioPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderCommentSeleccionada), "");
        String fechaPickingAlmacenado = sharedPref.getString(getString(R.string.PickingOrderFechaSeleccionada), "");

        if (nroPickingAlmacenado > 0) {
            datosCabeceraPickingSeleccionada = new CabeceraPicking(mUserKey, nroPickingAlmacenado, PICKING_STATUS_DELIVERY);
            datosCabeceraPickingSeleccionada.setComentario(comentarioPickingAlmacenado);
// Todo: fecha de cabecera sin cargar.

            mTilePickingComent.setText(comentarioPickingAlmacenado);
            mTilePickingOrderNumber.setText(String.valueOf(nroPickingAlmacenado));
            mTilePickingComent.setVisibility(View.VISIBLE);
            mCreationDate.setText(fechaPickingAlmacenado);


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
        Query listadoOrdenesEnPickingQuery = refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERING, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
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
                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
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


//    public boolean exitenCUasignedtoPickingOrder() {
//        String select = "((" + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " NOTNULL) AND ("
//                + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " =?))";
//
//
//        String projection[] = {CustomOrdersColumns.ID_CUSTOM_ORDER};
//        String arg[] = {String.valueOf(mIDPickingOrderSelected)};
//
//
//        Cursor c = getActivity().getContentResolver().query(LogisticaProvider.CustomOrders.CONTENT_URI,
//                null,
//                select,
//                arg,
//                null);
//        return !(c == null || c.getCount() == 0);
//    }


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


    private void bloqueoPickingParaTrabajoOffLine() {

        if (hayTareaEnProceso()||!isNetworkAvailable(getApplicationContext())) {
            return;
        }

        taskList.clear();

        //Bloqueo los totales de Picking.
        refPickingTotal_7_List(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal tiene childre: " +dataSnapshot.hasChildren());
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Detalle det=(Detalle)snapshot.getValue(Detalle.class);
                    Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal:" +snapshot.getKey());
                    Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockPickingTotal:" +det.getProducto().getNombreProducto());
                    readBlockPickingTotal(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden(), snapshot.getKey());
                    taskList.add(mPickingTotalTask.get(i));
                    i++;
                }


                //Bloqueo las cabeceras.
                refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERING,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockCabeceraOrden tiene children: " +dataSnapshot.hasChildren());

                        int i=0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CabeceraOrden cab=(CabeceraOrden)snapshot.getValue(CabeceraOrden.class);
                            Log.i(LOG_TAG, "bloqueoPickingOffLine - readBlockCabeceraOrden:" +cab.getNumeroDeOrden());
                            readBlockCabeceraOrden(cab.getNumeroDeOrden());
                            taskList.add(mCabeceraOrdenTask.get(i));
                            readBlockSaldosTotal(cab.getClienteKey());
                            taskList.add(mSaldosTotalTask.get(i));

                            i++;
                        }

                        readBlockPicking(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
                        taskList.add(mPickingTask);
                        allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
                        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnSuccessListener");
                                liberarArrayTaskCasoExitoso();
                            }});
                        allTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.toString());
                                Log.i(LOG_TAG, "bloqueoPickingOffLine - OnFailureListener AllTask:" + e.getMessage());

                                liberarRecusosTomados();
                                liberarArrayTaskConBloqueos();
                                onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + e.getMessage().toString());



                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel cabeceraOrden:" + databaseError.toString());

//                liberarRecusosTomados("", PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
//                liberarArrayTaskConBloqueos();
//                onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

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

        if (hayTareaEnProceso()||!isNetworkAvailable(getApplicationContext())) {
            return;
        }

        liberarArrayTaskCasoExitoso();
//        taskList.clear();

        mPickingTotalEstado=PICKING_STATUS_DELIVERY;
        mPickingTotalNumero=datosCabeceraPickingSeleccionada.getNumeroDePickingOrden();

        mPickingEstado=PICKING_STATUS_DELIVERY;
        mPickingNumero=datosCabeceraPickingSeleccionada.getNumeroDePickingOrden();

        //Bloqueo los totales de Picking.
        refPickingTotal_7_List(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal tiene childre: " +dataSnapshot.hasChildren());
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Detalle det=(Detalle)snapshot.getValue(Detalle.class);
                    Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal:" +snapshot.getKey());
                    Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockPickingTotal:" +det.getProducto().getNombreProducto());
                    mPickingTotalIndexLiberar.add(snapshot.getKey());
                    mLiberarSemaforoPickingTotal = true;
//                    readBlockPickingTotal(PICKING_STATUS_DELIVERY,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden(), snapshot.getKey());
//                    taskList.add(mPickingTotalTask.get(i));
                    i++;
                }


                //Bloqueo las cabeceras.
                refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERING,datosCabeceraPickingSeleccionada.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden tiene children: " +dataSnapshot.hasChildren());

                        int i=0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CabeceraOrden cab=(CabeceraOrden)snapshot.getValue(CabeceraOrden.class);
                            Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden Nro:" +cab.getNumeroDeOrden());
                            Log.i(LOG_TAG, "desbloqueoPickingOffLine - readBlockCabeceraOrden ClienteKey:" +cab.getClienteKey());


                            mCabeceraOrdenLiberar.add(snapshot.getKey());
                            mLiberarSemaforoCabeceraOrden = true;

                            mSaldosTotalIndexLiberar.add(cab.getClienteKey());
                            mLiberarSemaforoSaldoTotal = true;
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
//                                onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + e.getMessage().toString());
//
//
//
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(LOG_TAG, "bloqueoPickingOffLine - on cancel cabeceraOrden:" + databaseError.toString());

//                liberarRecusosTomados("", PICKING_STATUS_DELIVERY, datosCabeceraPickingSeleccionada.getNumeroDePickingOrden());
//                liberarArrayTaskConBloqueos();
//                onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + databaseError.getMessage().toString());

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

        if (hayTareaEnProceso()) {
            return;
        }

        taskList.clear();

//        mCabeceraOrdenDato = cabeceraOrden;

        readBlockCabeceraOrden(cabeceraOrden.getNumeroDeOrden());
        mCabeceraOrdenTask.get(0).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    final CabeceraOrden dataCabecera = ((DataSnapshot) mCabeceraOrdenTask.get(0).getResult()).getValue(CabeceraOrden.class);
                    refDetalleOrden_4_ListaXOrden(dataCabecera.getNumeroDeOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mProductosEnOrdenDatos = dataSnapshot;
                            readBlockSaldosTotal(cabeceraOrden.getClienteKey());
                            taskList.add(mSaldosTotalTask.get(0));
                            Log.i(LOG_TAG, "pasarOrdenEntrega-  status picking " + PICKING_STATUS_DELIVERY + " cabeceraOrden.getNumeroDePickingOrden() " + cabeceraOrden.getNumeroDePickingOrden());
                            readBlockPicking(PICKING_STATUS_DELIVERY, cabeceraOrden.getNumeroDePickingOrden());
                            taskList.add(mPickingTask);
                            int i = 0;
                            for (DataSnapshot snapshot : mProductosEnOrdenDatos.getChildren()) {
                                Log.i(LOG_TAG, "pasarOrdenEntrega- " + snapshot.getKey());
                                String productKey = snapshot.getKey();


                                readBlockPickingTotal(PICKING_STATUS_DELIVERY, cabeceraOrden.getNumeroDePickingOrden(), productKey);/*7*/
                                taskList.add(mPickingTotalTask.get(i));


                                SimpleDateFormat aamm = new SimpleDateFormat("yyyy-MM");

                                Log.i(LOG_TAG, "pasarOrdenEntrega TIMESTAMP- " + aamm.format(new Date(System.currentTimeMillis())));


                                Log.i(LOG_TAG, "pasarOrdenEntrega TIMESTAMP- " + aamm.toString());
                                readBlockReporteVentasCliente(dataCabecera.getClienteKey(), productKey, aamm.format(new Date(System.currentTimeMillis())));
                                taskList.add(mReporteVentasClienteTask.get(i));
                                readBlockReporteVentasProducto(productKey, aamm.format(new Date(System.currentTimeMillis())));
                                taskList.add(mReporteVentasProductoTask.get(i));


                                i++;
                            }

                            allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));

                            allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(LOG_TAG, "pasarOrdenEntrega Completo el bloqueo- ");

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    Map<String, Object> totalInicialDetalleValues = null;
                                    int i = 0;
                                    //            SimpleDateFormat aamm = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    SimpleDateFormat aamm = new SimpleDateFormat("yyyy-MM");
                                    for (DataSnapshot productoEnOrden : mProductosEnOrdenDatos.getChildren()) {
                                        // busca el producto dentro de la orden
                                        String productKey = productoEnOrden.getKey();
                                        Detalle detalleOrden = productoEnOrden.getValue(Detalle.class);

                                        // creamos una copia con cantidad cero para usar en otras estructuras.
                                        Detalle detalleOrdenAux = detalleOrden.copy();
                                        detalleOrdenAux.modificarCantidadProductoDeOrden(0.0); // modificamos la cantidad a Cero para usarla para el calculo de Total
                                        detalleOrdenAux.modificarCantidadProductoDeEntrega(0.0); // modificamos la cantidad a Cero para usarla para el calculo de Total


                                        Detalle detallePickingTotal = ((DataSnapshot) mPickingTotalTask.get(i).getResult()).getValue(Detalle.class);


                                        if (detallePickingTotal == null) {
                                            // di no existe esta estructura, se crea una en cero.
                                            Log.i(LOG_TAG, "pasarOrdenEntrega TotalPicking= NULL");
                                            detallePickingTotal = new Detalle(0.0, detalleOrden.getProducto(), null);
                                            detallePickingTotal.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                        } else {
//                                            Log.i(LOG_TAG, "pasarOrdenEntrega TotalPicking=" + ((DataSnapshot) mTotalInicialTask.get(i).getResult()).getKey() );
                                            Log.i(LOG_TAG, "pasarOrdenEntrega- PickingTotal nombre Producto" + detallePickingTotal.getProducto().getNombreProducto());
                                            detallePickingTotal.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                        }

                                            /*7 */
                                        detallePickingTotal.liberar();
                                        childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, cabeceraOrden.getNumeroDePickingOrden(), productKey), detallePickingTotal.toMap());


                                            /*5*/
//                                        childUpdates.put(nodoProductosXOrdenInicial_5(productKey, mCabeceraOrdenDato.getNumeroDeOrden()), null);

                                            /*4 */ //No se modifica, queda igual
//                                            childUpdates.put(nodoDetalleOrden_4(mCabeceraOrdenDato.getNumeroDeOrden(), productKey), null);


                                        // Reporte de Ventas de Productos /8/


                                        Detalle ventasProducto = ((DataSnapshot) mReporteVentasProductoTask.get(i).getResult()).getValue(Detalle.class);
// todo: Revisar
                                        if (ventasProducto == null) {
                                            // di no existe esta estructura, se crea una en cero.
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasProducto= NULL");
                                            ventasProducto = new Detalle(0.0, detalleOrden.getProducto(), null);
                                            ventasProducto.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                        } else {
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasProducto=" + ((DataSnapshot) mReporteVentasProductoTask.get(i).getResult()).getKey() + "- nombre Producto" + ((DataSnapshot) mReporteVentasProductoTask.get(i).getResult()).getValue(Detalle.class).getProducto().getNombreProducto());

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getMontoItemEntrega()=" + ventasProducto.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getCantidadEntrega()=" + ventasProducto.getCantidadEntrega());

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrden getMontoItemEntrega=" + detalleOrden.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenget CantidadEntrega=" + detalleOrden.getCantidadEntrega());

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenAux getMontoItemEntrega=" + detalleOrdenAux.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenAux CantidadEntrega=" + detalleOrdenAux.getCantidadEntrega());
                                            ventasProducto.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getMontoItemEntrega()=" + ventasProducto.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getCantidadEntrega()=" + ventasProducto.getCantidadEntrega());

                                        }
                                        ventasProducto.liberar();
                                        childUpdates.put(nodoReporteVentasProducto_8(productKey, aamm.format(new Date(System.currentTimeMillis()))), ventasProducto.toMap());


                                        // Reporte de Ventas Por Cliente /9/


                                        Detalle ventasCliente = ((DataSnapshot) mReporteVentasClienteTask.get(i).getResult()).getValue(Detalle.class);
// todo: Revisar
                                        if (ventasCliente == null) {
                                            // di no existe esta estructura, se crea una en cero.
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente = NULL");
                                            ventasCliente = new Detalle(0.0, detalleOrden.getProducto(), null);
                                            ventasCliente.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);
                                        } else {
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente =" + ((DataSnapshot) mReporteVentasClienteTask.get(i).getResult()).getKey() + "- nombre Producto" + ((DataSnapshot) mReporteVentasClienteTask.get(i).getResult()).getValue(Detalle.class).getProducto().getNombreProducto());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getMontoItemEntrega()=" + ventasCliente.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getCantidadEntrega()=" + ventasCliente.getCantidadEntrega());

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrden getMontoItemEntrega=" + detalleOrden.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenget CantidadEntrega=" + detalleOrden.getCantidadEntrega());

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenAux getMontoItemEntrega=" + detalleOrdenAux.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente detalleOrdenAux CantidadEntrega=" + detalleOrdenAux.getCantidadEntrega());


                                            ventasCliente.modificarCantidadEnTotalDelivey(detalleOrden, detalleOrdenAux);

                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getMontoItemEntrega()=" + ventasCliente.getMontoItemEntrega());
                                            Log.i(LOG_TAG, "pasarOrdenEntrega ventasCliente ventas getCantidadEntrega()=" + ventasCliente.getCantidadEntrega());

                                        }
                                        ventasCliente.liberar();
                                        childUpdates.put(nodoReporteVentasClientes_9(cabeceraOrden.getClienteKey(), productKey, aamm.format(new Date(System.currentTimeMillis()))), ventasCliente.toMap());

                                        i++;
                                    }

                                    // Actualizacion de totales en Picking (6)


                                    DataSnapshot d = ((DataSnapshot) mPickingTask.getResult());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega d.getRef() " + d.getRef());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega d.getRef() " + d.getKey());

                                    CabeceraPicking cabeceraPicking = d.getValue(CabeceraPicking.class);
                                    Log.i(LOG_TAG, "pasarOrdenEntrega getMontoEntregado " + cabeceraPicking.getTotales().getMontoEntregado());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega cabeceraOrden.getTotales().getMontoEntregado( " + cabeceraOrden.getTotales().getMontoEntregado());
                                    cabeceraPicking.getTotales().setSumaMontoEntregado(cabeceraOrden.getTotales().getMontoEntregado());
                                    cabeceraPicking.liberar();
                                    childUpdates.put(nodoPicking_6(PICKING_STATUS_DELIVERY, ((DataSnapshot) mPickingTask.getResult()).getKey()), cabeceraPicking.toMap());


                                /*2 */
                                    cabeceraOrden.setEstado(ORDEN_STATUS_DELIVERED_PARA_COMPENSAR);
                                    cabeceraOrden.liberar();
                                    cabeceraOrden.setNumeroDePickingOrden(cabeceraOrden.getNumeroDePickingOrden());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega getNumeroDeOrden() " + cabeceraOrden.getNumeroDeOrden());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega getNumeroDePickingOrden() " + cabeceraOrden.getNumeroDePickingOrden());

                                    childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERING, cabeceraOrden.getNumeroDeOrden(), cabeceraOrden.getNumeroDePickingOrden()), null);
                                    childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_DELIVERED_PARA_COMPENSAR, cabeceraOrden.getNumeroDeOrden(), cabeceraOrden.getNumeroDePickingOrden()), cabeceraOrden.toMap());
                                    childUpdates.put(nodoCabeceraOrden_1B(cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());


                                    CabeceraOrden saldosTotal = ((DataSnapshot) mSaldosTotalTask.get(0).getResult()).getValue(CabeceraOrden.class);

                                    if (saldosTotal == null) {
                                        // di no existe esta estructura, se crea una en cero.
                                        Log.i(LOG_TAG, "pasarOrdenEntrega saldosTotal= NULL");

                                        Totales totales = new Totales(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                                        saldosTotal = new CabeceraOrden(cabeceraOrden.getClienteKey(), cabeceraOrden.getCliente(), ORDEN_STATUS_INICIAL, totales, "Sistema", 00);
                                        cabeceraOrden.setUsuarioCreador("Sistema");

                                        saldosTotal.getTotales().setMontoEntregado(saldosTotal.getTotales().getMontoEntregado() + cabeceraOrden.getTotales().getMontoEntregado());
                                    } else {
//                                        Log.i(LOG_TAG, "pasarOrdenEntrega saldosTotal=" + ((DataSnapshot) mSaldosTotalTask.get(0).getResult()).getKey() + "- nombre Producto" + ((DataSnapshot) mSaldosTotalTask.get(0).getResult()).getValue(CabeceraOrden.class).getCliente().getNombre());
//                                        Log.i(LOG_TAG, "pasarOrdenEntrega saldosTotal=" + saldosTotal.getTotales().getMontoEntregado() + "- cabeceraOrden.getTotales().getMontoEntregado()" + cabeceraOrden.getTotales().getMontoEntregado());
                                        saldosTotal.getTotales().setMontoEntregado(saldosTotal.getTotales().getMontoEntregado() + cabeceraOrden.getTotales().getMontoEntregado());

                                    }
                                    saldosTotal.liberar();
                                    childUpdates.put(nodoSaldoTotalClientes_10(cabeceraOrden.getClienteKey()), saldosTotal.toMap());


                                    mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.i(LOG_TAG, "pasarOrdenEntrega updateChildren-onFailure " + e.toString());
                                            liberarRecusosTomados();
                                            liberarArrayTaskConBloqueos();
                                            onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            liberarArrayTaskCasoExitoso();
//                                            mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());
//                                            mCantidadTotal.setText(String.valueOf( mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
//                                            mKeyList.add(mproductKeyDato);
                                            mClienteKey = cabeceraOrden.getClienteKey();
                                            mCliente = cabeceraOrden.getCliente();

                                            Log.i(LOG_TAG, "pasarOrdenEntrega - OnCompleteListener task.isSuccessful():" + task.isSuccessful());
                                            Intent intent = new Intent(getApplicationContext(), PagosActivity.class);
                                            putExtraFirebase_Fragment(intent);
                                            startActivity(intent);
                                        }
                                    });
                                }


                            });
                            allTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(LOG_TAG, "pasarOrdenEntrega - OnFailureListener:" + e.toString());
                                    Log.i(LOG_TAG, "pasarOrdenEntrega - OnFailureListener:" + e.getMessage());

                                    liberarRecusosTomados();
                                    liberarArrayTaskConBloqueos();
                                    onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + e.getMessage().toString());

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.i(LOG_TAG, "pasarOrdenAPickingl Operacion fallo- " + task.getException().toString());
                    Log.i(LOG_TAG, "pasarOrdenAPickingl Operacion fallo- " + task.getException().getMessage());
                    liberarRecusosTomados();
                    liberarArrayTaskConBloqueos();
                    onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + task.getException().getMessage().toString());
                }
            }
        });
   }




    private void pasarPickingAInicial(final CabeceraPicking cabeceraPicking) {
        Log.i(LOG_TAG, "pasarPickingAEntrega Numero de picking- " + cabeceraPicking.getNumeroDePickingOrden());
        if (hayTareaEnProceso()) {
            return;
        }


        // leo y bloqueo picking
        readBlockPicking(PICKING_STATUS_DELIVERY, cabeceraPicking.getNumeroDePickingOrden());


        mPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraPicking cabeceraPicking1 = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);


                    Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key lee cabeceras");
                    //leo las ordenes asociadas al picking y las bloqueo
                    refCabeceraOrden_2_List(ORDEN_STATUS_EN_DELIVERING, cabeceraPicking.getNumeroDePickingOrden()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERING, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), null);

                                                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_PICKING, cabeceraOrden.getNumeroDeOrden(), cabeceraPicking.getNumeroDePickingOrden()), cabeceraOrden.toMap());
//                                        childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_EN_DELIVERING, cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());
                                                childUpdates.put(nodoCabeceraOrden_1B(cabeceraOrden.getNumeroDeOrden()), cabeceraOrden.toMap());

                                            }

                                            mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.i(LOG_TAG, "pasarPickingAEntrega updateChildren-onFailure " + e.toString());
                                                    liberarRecusosTomados();
                                                    liberarArrayTaskConBloqueos();
                                                    onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

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
                                                                         onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                                                     }
                                                                 }

                                    );

                                }


                                @Override // Listado de Picking... Listen for single value
                                public void onCancelled(DatabaseError databaseError) {
                                    liberarRecusosTomados();
                                    liberarArrayTaskConBloqueos();
                                    onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                                }
                            });
                        }

                        @Override // Listado de cabceras... Listen for single value
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(LOG_TAG, "pasarPickingAEntrega Bloqueo Orden key lee cabeceras onCancelled");
                            liberarRecusosTomados();
                            liberarArrayTaskConBloqueos();
                            onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                        }
                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() { // bloqueo de la orden de Picking.
            @Override
            public void onFailure(@NonNull Exception e) {
                liberarRecusosTomados();
                liberarArrayTaskConBloqueos();
                onDialogAlert(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Orden");
            }
        });
    }

}
