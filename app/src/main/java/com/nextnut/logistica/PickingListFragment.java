package com.nextnut.logistica;

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
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.CabeceraPicking;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.PrductosxOrden;
import com.nextnut.logistica.modelos.Totales;
import com.nextnut.logistica.rest.PickingOrderProductsAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraPickingViewHolder;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.DetalleViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_PICKING;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_PICKING;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PICKING;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ORDER_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.ORDER_STATUS_PICKING;
import static com.nextnut.logistica.util.Constantes.PICKING_STATUS_INICIAL;
import static com.nextnut.logistica.util.SharePickingOrder.sharePickingOrder;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PickingListFragment extends FragmentBasic {


    private long mCustomOrderIdSelected;

    private long mIDPickingOrderSelected;

    private FirebaseRecyclerAdapter<CabeceraPicking, CabeceraPickingViewHolder> mCursorAdapterPickingOrder;
    private FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder> mCustomsOrdersCursorAdapter;
    private FirebaseRecyclerAdapter<Detalle, DetalleViewHolder> mCursorAdapterTotalProductos;

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

    ArrayList<Task> taskList = new ArrayList<Task>();
    Task<Void> allTask;

    private Detalle mDetalleAnterior;
    private Detalle mDetalleDato;//Tiene los datos a ser cargados en una operacion
    private Double mCantidadDato;//Tiene los datos a ser cargados en una operacion
    private String mproductKeyDato;

//    private LinearLayout mLinearProductos;
//    private LinearLayout mLinearOrders;

    private PickingOrdersHandler mPickingOrdersHandler;


//    public static final int PICKING_STATUS_INICIAL = 0;
//    public static final int PICKING_STATUS_DELIVERY = 1;
//    public static final int PICKING_STATUS_CERRADA = 2;
//    public static final int PICKING_STATUS_DELETED = 3;

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
                updatePickingOrder(MainActivity.mPickingOrderSelected);
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
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerViewPickingOrder.setVisibility(View.GONE);

//                            mLinearOrders.setVisibility(View.VISIBLE);
//                            mLinearProductos.setVisibility(View.VISIBLE);
                        totalProductos = refPickingTotal_7_List(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
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

            }

        };

//        mCursorAdapterPickingOrder = new PickingOrdersCursorAdapter(
//                getContext(),
//                null,
//                ,
//                new PickingOrdersCursorAdapter.PinckingOrdersCursorAdapterOnClickHandler() {
//                    @Override
//                    public void onClick(long id, PickingOrdersCursorAdapter.ViewHolder vh) {
//                        if (mPickingOrdersHandler != null) {
//
//                            mTilePickingComent.setText(vh.mpickingOrderComents.getText());
//                            mTilePickingOrderNumber.setText(vh.mPickingOrderNumber.getText());
//                            mTilePickingComent.setVisibility(View.VISIBLE);
//                            mCreationDate.setText(vh.mCreationDate.getText());
//                            mPickingOrdersHandler.onPickingOrderSelected(id);
//
//
//                            mCursorAdapterPickingOrder.notifyDataSetChanged();
//                            mCursorAdapterTotalProductos.notifyDataSetChanged();
//                            recyclerViewPickingOrder.setVisibility(View.GONE);
//
//                            mLinearOrders.setVisibility(View.VISIBLE);
//                            mLinearProductos.setVisibility(View.VISIBLE);
//
////                            recyclerViewCustomOrderInPickingOrder.setVisibility(View.VISIBLE);
////                            recyclerViewTotalProductos.setVisibility(View.VISIBLE);
//                            mPickingOrderTile.setVisibility(View.VISIBLE);
//
//                        }
//                    }
//
//                    @Override
//                    public void onDataChange() {
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
//                        mCursorAdapterPickingOrder.notifyDataSetChanged();
//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onItemDismissCall(long cursorID) {
//                        mIDPickingOrderSelected = cursorID;
//                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//
//                        alert.setMessage(getContext().getResources().getString(R.string.detete_picking_order));
//                        alert.setNegativeButton(getContext().getResources().getString(R.string.detete_picking_order_cancel), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//
//                                onDataChange();
//                                dialog.cancel();
//                            }
//                        });
//                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//
//
//                                if (exitenCUasignedtoPickingOrder()) {
//
//
//                                    onDialogAlert(getResources().getString(R.string.detete_picking_order_Existen_CustomOrders_Asigned));
//                                } else {
//
//
//                                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
//                                    builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELETED);
//                                    batchOperations.add(builder.build());
//
//
//                                    try {
//
//                                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                                        onDataChange();
//
//
//                                    } catch (RemoteException | OperationApplicationException e) {
//
//
//                                    } finally {
//                                        onDataChange();
//                                    }
//                                }
//
//                            }
//                        });
//                        alert.create().show(); // btw show() creates and shows it..
//
//
//                    }
//
//                    @Override
//                    public void onItemAceptedCall(long cursorID) {
//                        mIDPickingOrderSelected = cursorID;
//
//
//                        if (!exitenCUasignedtoPickingOrder()) {
//
//                            onDialogAlert(getResources().getString(R.string.detete_picking_order_NO_Existen_CustomOrders_Asigned));
//                            onDataChange();
//                        } else {
//
//
//                            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
//                            builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELIVERY);
//                            batchOperations.add(builder.build());
//
//
//                            try {
//
//                                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                                onDataChange();
//
//
//                            } catch (RemoteException | OperationApplicationException e) {
//
//
//                            } finally {
//                                onDataChange();
//                                upDateWitget(getContext());
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onDialogAlert(String message) {
//
//                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//
//                        alert.setMessage(message);
//
//                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//
//
//                            }
//                        });
//                        alert.create().show();
//                        mCursorAdapterPickingOrder.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void sharePickingorder(PickingOrdersCursorAdapter.ViewHolder vh) {
//                        sharePickingOrder(getContext(), vh.mPickingOrderNumber.getText().toString(), mTilePickingComent.getText().toString());
//                    }
//                }
//        );


        recyclerViewPickingOrder.setAdapter(mCursorAdapterPickingOrder);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapterPickingOrder, ADAPTER_CABECERA_PICKING);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewPickingOrder);


        final View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
//        emptyViewTotalProducts.setVisibility(View.GONE);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);
        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));


        totalProductos = refPickingTotal_7_List(PICKING_STATUS_INICIAL, MainActivity.getmPickingOrderSelected());
        mCursorAdapterTotalProductos = new FirebaseRecyclerAdapter<Detalle, DetalleViewHolder>(Detalle.class, R.layout.order_detail_item,
                DetalleViewHolder.class, totalProductos) {
            @Override
            protected void populateViewHolder(final DetalleViewHolder viewHolder, final Detalle model, final int position) {
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
////                                mCliente=model.getCliente();
//                                mProducto = model.getProducto();
//                                mProductKey = productKey;
//                                mDetalleAnterior=model;
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
        recyclerViewTotalProductos.setVisibility(View.GONE);

        recyclerViewCustomOrderInPickingOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);

        recyclerViewCustomOrderInPickingOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_custom_empty);
        emptyViewCustomOrder.setVisibility(View.GONE);


        Query listadoOrdenesEnPickingQuery = refCabeceraOrden_2_List(ORDER_STATUS_PICKING);
        mCustomsOrdersCursorAdapter = new FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder>(CabeceraOrden.class, R.layout.customorder_list_content,
                CabeceraViewHolder.class, listadoOrdenesEnPickingQuery) {
            @Override
            protected void populateViewHolder(final CabeceraViewHolder viewHolder, final CabeceraOrden model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                emptyViewCustomOrder.setVisibility(View.GONE);
                Log.i(LOG_TAG, "adapter:CabeceraRef: " + CabeceraRef.toString());

                // Set click listener for the whole post view
                final String orderKey = CabeceraRef.getKey();
                Log.i(LOG_TAG, "adapter:orderKey: " + orderKey);

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
                                Log.d(LOG_TAG, "adapter:onClick model: " + model.getCliente().getNombre());
                                Log.d(LOG_TAG, "adapter:onClick");
                                Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                                mCliente = model.getCliente();
                                putExtraFirebase_Fragment(intent);
                                intent.putExtra(EXTRA_CABECERA_ORDEN, model);
                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(CabeceraOrden model, int position) {
                Log.i(LOG_TAG, "pasarOrdenAInicial Modelo: Numero de orden- " + model.getNumeroDeOrden());
                if (MainActivity.mPickingOrderSelected == 0) {

                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
                } else {

                    Log.i(LOG_TAG, "pasarOrdenAInicial Modelo: Numero de orden- " + model.getNumeroDeOrden());
                    pasarOrdenAInicial(model);

                }
            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {


                if (MainActivity.mPickingOrderSelected == 0) {

                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
                } else {

                    Log.i(LOG_TAG, "pasarOrdenAInicial Modelo: Numero de orden- " + model.getNumeroDeOrden());
                    pasarOrdenAInicial(model);


                }
            }
        };


//        mCustomsOrdersCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyViewCustomOrder, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
//            @Override
//            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {
//
//
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//
////                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);
//
//                    arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
//
//                    CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();
//                    fragment.setArguments(arguments);
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .addToBackStack(null)
//                            .replace(R.id.customorder_detail_container, fragment)
//                            .commit();
//
//
//                } else {
//                    Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
//                    intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
////                    intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, id);
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//
//                        Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
//                        startActivity(intent, activityOptions.toBundle());
//
//                    } else {
//
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
//
//                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
//                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_INICIAL);
//                builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, null);
//                batchOperations.add(builder.build());
//
//
//                try {
//                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//
//
//                } catch (RemoteException | OperationApplicationException e) {
//
//
//                } finally {
//                    onDataChange();
//                }
//
//            }
//
//            @Override
//            public void onItemAceptedCall(long cursorID) {
//                mCustomOrderIdSelected = cursorID;
//
//            }
//
//            @Override
//            public void onDataChange() {
//                mCursorAdapterTotalProductos.notifyDataSetChanged();
//                mCursorAdapterPickingOrder.notifyDataSetChanged();
//                mCursorAdapterTotalProductos.notifyDataSetChanged();
//                upDateWitget(getContext());
//            }
//        }
//
//        );


        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(mCustomsOrdersCursorAdapter, ADAPTER_CABECERA_ORDEN_EN_PICKING);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInPickingOrder);

        recyclerViewCustomOrderInPickingOrder.setAdapter(mCustomsOrdersCursorAdapter);
//        recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
//        emptyViewCustomOrder.setVisibility(View.GONE);

//        mLinearOrders.setVisibility(View.GONE);
//        mLinearProductos.setVisibility(View.GONE);

        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
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

    public void updatePickingOrder(long idPickingOrder) {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(idPickingOrder));
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        String formattedDate = df.format(new Date());
        builder.withValue(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS, formattedDate);


        builder.withValue(PickingOrdersColumns.COMMENTS_PICKING_ORDERS, mTilePickingComent.getText().toString());

        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

        }
    }



    public void showDialogNumberPicker(final String productKey) {

        {

            final Dialog d = new Dialog(getContext());
            d.setTitle(getString(R.string.quantityPicker));
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            Log.d("Picker", "Default: " + mDetalleAnterior.getProducto().getCantidadDefault());
            Log.d("Picker", "Man: " + mDetalleAnterior.getProducto().getCantidadMaxima());
            Log.d("Picker", "Mix: " + mDetalleAnterior.getProducto().getCantidadMinima());
            Log.d("Picker", "PickingCantidad: " + mDetalleAnterior.getCantidadPicking());

            np.setMaxValue(mDetalleAnterior.getProducto().getCantidadMaxima());
            if (mDetalleAnterior.getCantidadOrden() == null) {
                np.setValue(mDetalleAnterior.getProducto().getCantidadDefault());
            } else {
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
                        abmDetalleDePicking((double) np.getValue(),productKey,mDetalleAnterior);


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

        mCantidadDato = cantidad; // Es la nueva cantidad que queremos tener
        mproductKeyDato = productoKey;
        mDetalleDato = detalle; // tiene los valores del detalle que se quiere modificar


        Log.i(LOG_TAG, "abmDetalleDeOrden cantidad " + cantidad + " productokey " + productoKey + " Producto " + detalle.getProducto().getNombreProducto());
        limpiarTodosLosSemaforosLiberar();

        // todo: ver donde pongo la limpeza de estos vectores para que funcione en rotaciones o sin red. Tengo que asegurar que libere.

//        mTotalInicialTask.clear();
//        mTotalInicialCompletionTask.clear();
//        mTotalInicialIndex.clear();

        mPickingTotalIndex.clear();
        mPickingTotalIndexLiberar.clear();
        mPickingTotalTask.clear();
        mPickingTotalCompletionTask.clear();
        mLiberarPickingTotalTask.clear();
        mLiberarPickingTotalCompletionTask.clear();

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
                    totalPickingDetalle = new Detalle(0.0, mDetalleDato.getProducto(),null);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask = NuLL- ");

                }




                // Todo: procesar los datos
                // liberar los semaforos para grabar


                totalPickingDetalle.liberar();

                    //Actualizo Detalle de Picking 7
                totalPickingDetalle.modificarCantidadProductoDePicking(mCantidadDato);


                Map<String, Object> totalPickingValues=totalPickingDetalle.toMap();


                Map<String, Object> childUpdates = new HashMap<>();




/*7 */
                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL,MainActivity.getmPickingOrderSelected() ,mproductKeyDato), totalPickingValues);


                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren-onFailure "+e.toString());
                        liberarPickingTotal(PICKING_STATUS_INICIAL,MainActivity.getmPickingOrderSelected());
//                        liberarRecusosTomados(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());
//                        mCantidadTotal.setText(String.valueOf( mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
//                        mKeyList.add(mproductKeyDato);
                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren - OnCompleteListener task.isSuccessful():"+task.isSuccessful());

                    }
                });
//

            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "abmDetalleDeOrden addOnFailureListener= allTask" + e.toString());
                liberarPickingTotal(PICKING_STATUS_INICIAL,MainActivity.getmPickingOrderSelected());

//                liberarRecusosTomados(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato);

            }
        });


////1b -Bloqueo toda la orden y puedo modificar toda sus dependencias.  Pues modifico mientra tengo tomada la orden.
//        mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).child("cabecera").runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                // Bloqueo la orden para modificaciones,
//                // Actualizo  el esquema y luego lo libero.
//
//                CabeceraOrden cabeceraOrden1b = mutableData.getValue(CabeceraOrden.class);
//                if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
//                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden cabeceraOrden1b-null, es un error");
//                    return Transaction.success(mutableData);
//                } else {
//
//
//
//
//
//                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden 1c-NOT null");
//                    Detalle detalle = new Detalle();
//                    detalle.ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
//                    mCabeceraOrden.getTotales().ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
//
//                    Map<String, Object> cabeceraOrdenValues = mCabeceraOrden.toMap();
//                    Map<String, Object> detalleOrdenValues = detalle.toMap();
//                    Map<String, Object> childUpdates = new HashMap<>();
///*1c*/
//                    childUpdates.put(NODO_ORDENES + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
///*4 */
//                    childUpdates.put(NODO_ORDENES_DETALLE + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
///*2 */
//                    childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" +ORDER_STATUS_INICIAL+ "/" + mCabeceraOrden.getNumeroDeOrden(), cabeceraOrdenValues);
//
//                    mDatabase.updateChildren(childUpdates);
//
//                    Log.i("ClienteViewHolder", "saveCustomOrderProductproductoKey" + productoKey);
//                    Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Nombre" + producto.getNombreProducto());
//                    Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Precio" + producto.getPrecio());
//
///*3*/               Detalle detalleInicial=detalle;
//                    detalleInicial.setCantidadOrden(0.0);
//                    detalleInicial.setMontoItemOrden(0.0);
//                    mDetalleAnterior=detalleInicial;
//                    saveDetalleInicialTotales( cantidad, productoKey); // cantidad es el valor nuevo y detalle tiene el anterior en este caso esta en cero
///*5*/
//                    saveOrdenProductoXCliente(productoKey, detalle); // tiene el valor final que se graba en la orden
//                    mKeyList.add(productoKey); // Agrega productkey para que no se repita.
//                }
//
//
//                // Set value and report transaction success
//                mutableData.setValue(mCabeceraOrden);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean commited,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete:  databaseError" + databaseError);
//                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: boolean b" + commited);
//                CabeceraOrden cabecera_orden = dataSnapshot.getValue(CabeceraOrden.class);
//
//                long numeroOrden = cabecera_orden.getNumeroDeOrden();
//                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: ID " + numeroOrden);
//                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: Monto Orden " + cabecera_orden.getTotales().getMontoEnOrdenes());
//
//                mCantidadTotal.setText("Items: " + String.valueOf(cabecera_orden.getTotales().getCantidadDeProductosDiferentes()));
//                NumberFormat format = NumberFormat.getCurrencyInstance();
//                mMontoTotal.setText("Monto Orden" + format.format(cabecera_orden.getTotales().getMontoEnOrdenes()));
//                mMontoTotalDelivey.setText("Monto Entregado" + format.format(cabecera_orden.getTotales().getMontoEntregado()));
//
//            }
//        });
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
        mTotalInicialTask.clear();
        mTotalInicialCompletionTask.clear();
        mTotalInicialIndex.clear();
        mTotalInicialIndexLiberar.clear();
        mLiberarTotalInicialCompetionTask.clear();
        mLiberarTotalInicialTask.clear();


        mPickingTotalIndex.clear();
        mPickingTotalIndexLiberar.clear();
        mPickingTotalTask.clear();
        mPickingTotalCompletionTask.clear();
        mLiberarPickingTotalTask.clear();
        mLiberarPickingTotalCompletionTask.clear();


        mCabeceraOrdenDato = cabeceraOrden;

        readBlockCabeceraOrden(cabeceraOrden.getNumeroDeOrden());
        mCabeceraOrdenTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraOrden dataCabecera = ((DataSnapshot) mCabeceraOrdenTask.getResult()).getValue(CabeceraOrden.class);
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
                                        PrductosxOrden detallexOrden   = new PrductosxOrden(mCabeceraOrdenDato.getCliente(), detalleOrden);
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
                                    mCabeceraOrdenDato.setEstado(ORDER_STATUS_INICIAL);
                                    mCabeceraOrdenDato.liberar();
                                    childUpdates.put(nodoCabeceraOrden_2(mCabeceraOrdenDato.getNumeroDeOrden(), ORDER_STATUS_PICKING), null);
                                    childUpdates.put(nodoCabeceraOrden_2(mCabeceraOrdenDato.getNumeroDeOrden(), ORDER_STATUS_INICIAL), mCabeceraOrdenDato.toMap());
                                    childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrdenDato.getNumeroDeOrden()), mCabeceraOrdenDato.toMap());

                                    mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.i(LOG_TAG, "pasarOrdenAInicial updateChildren-onFailure " + e.toString());
                                            liberarRecusosTomados(mCabeceraOrdenDato.getNumeroDeOrden(), "-KX21qOSklZo9gNY7mrC");
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
//                                            mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());
//                                            mCantidadTotal.setText(String.valueOf( mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
//                                            mKeyList.add(mproductKeyDato);
                                            Log.i(LOG_TAG, "pasarOrdenAInicial - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                                        }
                                    });
                                }


                            });
                            allTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    liberarRecusosTomados(mCabeceraOrdenDato.getNumeroDeOrden(), "-KX21qOSklZo9gNY7mrC");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.i(LOG_TAG, "pasarOrdenAInicial Operacion fallo- " + task.getException().toString());
                    liberarRecusosTomados(mCabeceraOrdenDato.getNumeroDeOrden(), "-KX21qOSklZo9gNY7mrC");
                }
            }
        });


//        taskList.add(mCabeceraOrdenTask);
        limpiarTodosLosSemaforosLiberar();


//        readBlockCabeceraOrden(cabeceraOrden.getNumeroDeOrden());

//        readBlockProductosEnOrdenes(productoKey, cabeceraOrden.getNumeroDeOrden());


//        mDatabase.child(ESQUEMA_ORDENES).child(mDetalleCabeceraAnterior.getClienteKey()).runTransaction(new Transaction.Handler() {
//
//
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                CabeceraOrden cabeceraOrden1b = mutableData.getValue(CabeceraOrden.class);
//                if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
//                    Log.d(LOG_TAG, "pasarOrdenAInicial cabeceraOrden1b-null, es un error");
//                    return Transaction.success(mutableData);
//                } else {
//
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    for (MutableData data : mutableData.getChildren()) {
//
//
//                        String productoKey = data.getKey();
//                        Detalle d = data.getValue(Detalle.class);
//                        Double cantidad = d.getCantidadOrden();
//                        Producto producto = d.getProducto();
//                        Log.d(LOG_TAG, "pasarOrdenAInicial  keyProducto: " + productoKey);
//                        Log.d(LOG_TAG, "pasarOrdenAInicial " + d.getProducto().getNombreProducto() + " - " + d.getCantidadOrden());
//
//
//                        Log.d(LOG_TAG, "orden:abmDetalleDeOrden 1c-NOT null");
//                        Detalle detalle = new Detalle();
//                        detalle.ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
////                        mCabeceraOrden.getTotales().ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
////
////                        Map<String, Object> detalleOrdenValues = detalle.toMap();
/////*1c*/
////                        childUpdates.put(NODO_ORDENES + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
/////*4 */
////                        childUpdates.put(NODO_ORDENES_DETALLE + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
////
////                        Log.i("ClienteViewHolder", "saveCustomOrderProductproductoKey" + productoKey);
////                        Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Nombre" + producto.getNombreProducto());
////                        Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Precio" + producto.getPrecio());
////
/////*3*/               Detalle detalleInicial=detalle;
////                        detalleInicial.setCantidadOrden(0.0);
////                        detalleInicial.setMontoItemOrden(0.0);
////                        mDetalleAnterior=detalleInicial;
////                        saveDetalleInicialTotales( cantidad, productoKey); // cantidad es el valor nuevo y detalle tiene el anterior en este caso esta en cero
/////*5*/
////                        saveOrdenProductoXCliente(productoKey, detalle); // tiene el valor final que se graba en la orden
////                        mKeyList.add(productoKey); // Agrega productkey para que no se repita.
////
////                    }
////                    Map<String, Object> cabeceraOrdenValues = mCabeceraOrden.toMap();
////
/////*2 */
////
////                    childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" +ORDER_STATUS_INICIAL+ "/"+ mCabeceraOrden.getNumeroDeOrden(), cabeceraOrdenValues);
////
////                    mDatabase.updateChildren(childUpdates);
////                    mDatabase.keepSynced();
////                    NumberFormat format = NumberFormat.getCurrencyInstance();
////                    mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
////                    // Set value and report transaction success
////                    mutableData.setValue(mCabeceraOrden);
//
//
//                    }
//                }
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });
    }
}
