package com.nextnut.logistica;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.CabeceraPicking;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.PrductosxOrden;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.DetalleViewHolder;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_TOTAL_INICIAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PICKING;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PICKING_TOTAL;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ORDER_STATUS_INICIAL;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CustomOrderListFragment extends FragmentBasic {

    public static final String ARG_ITEM_ID = "item_id";
    private static final String M_ITEM = "mItem";

    private FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder> mCabeceraAdapter;
    private FirebaseRecyclerAdapter<Detalle, DetalleViewHolder> mCursorAdapterTotalProductos;
    private RecyclerView recyclerViewOrders;
    private RecyclerView recyclerViewTotalProductos;

    // Total Inicial de Ordenes. 3
    private Task mTotalInicialTask;
    private TaskCompletionSource<DataSnapshot> mTotalInicialCompletionTask;
    private Boolean mLiberarSemaforoTotalInicial = false; // se pone en true cuando tengo que liberar por accion de este proceso
    private Task mLiberarTotalInicialTask;
    private TaskCompletionSource<DataSnapshot> mLiberarTotalInicialCompetionTask;

    // Cabecera Orden 1B
    private Boolean mLiberarSemaforoCabeceraOrden = false;
    private TaskCompletionSource<DataSnapshot> mCabeceraOrdenCompletionTask;
    private Task mCabeceraOrdenTask;
    private Task mLiberarCabeceraOrdenTask;
    private TaskCompletionSource<DataSnapshot> mLiberarcabeceraOrdenCompletionTask;


    // Productos En Ordenes 5
    private Boolean mLiberarSemaforoProductosEnOrdenes = false;
    private TaskCompletionSource<DataSnapshot> mProductosEnOrdenesCompletionTask;
    private Task mProductosEnOrdenesTask;
    private Task mLiberarProductosEnOrdenesTask;
    private TaskCompletionSource<DataSnapshot> mLiberarProductosEnOrdenesCompletionTask;


    // Cabecera Picking 6
    private Boolean mLiberarSemaforoPicking = false;
    private TaskCompletionSource<DataSnapshot> mPickingCompletionTask;
    private Task mPickingTask;
    private Task mLiberarPickingTask;
    private TaskCompletionSource<DataSnapshot> mLiberarPickingCompletionTask;


    // Picking Total 7
    private Boolean mLiberarSemaforoPickingTotal = false;
    private TaskCompletionSource<DataSnapshot> mPickingTotalCompletionTask;
    private Task mPickingTotalTask;
    private Task mLiberarPickingTotalTask;
    private TaskCompletionSource<DataSnapshot> mLiberarPickingTotalCompletionTask;


    private long mItem = 0;


    private boolean mTwoPane;

    private static final String LOG_TAG = CustomOrderListFragment.class.getSimpleName();
    private CabeceraOrden mDetalleCabeceraAnterior;
    private Detalle mDetalleItemAnterior;

    public static final float LARGE_SCALE = 1.5f;
    private boolean symmetric = true;
    private boolean small = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
//            mItem = savedInstanceState.getLong(M_ITEM);

        }

    }

    @Override
    public void savePhoto(Bitmap bitmap) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.customorder_list_fragment, container, false);


        final View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_customOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        Query totalProductos = mDatabase.child(ESQUEMA_ORDENES_TOTAL_INICIAL).child(mEmpresaKey);
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
                                Intent intent = new Intent(getContext(), ProductosEnOrdenes.class);
//                                mCliente=model.getCliente();
                                mProducto = model.getProducto();
                                mProductKey = productKey;
                                putExtraFirebase_Fragment(intent);

//                                intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
//                                intent.putExtra(EXTRA_PRODUCT, model.getProducto());
                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Detalle model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(Detalle model, int position) {

            }
        };
//        mCursorAdapterTotalProductos = new OrderDetailCursorAdapter(getContext(), null,
//                emptyViewTotalProducts,
//                new OrderDetailCursorAdapter.ProductCursorAdapterOnClickHandler() {
//                    @Override
//                    public void onClick(long id, OrderDetailCursorAdapter.ViewHolder v) {
//                        Intent intent = new Intent(getContext(), ProductosEnOrdenes.class);
//                        intent.putExtra(ProductosEnOrdenes.ARG_ITEM_ID, id);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFavorite(long id, OrderDetailCursorAdapter.ViewHolder vh) {
//                    }
//
//                    @Override
//                    public void onProductDismiss(long id) {
//
//                    }
//                }
//        );

//        mCursorAdapterTotalProductos.resetFavoriteVisible();
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);


        final View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        recyclerViewOrders = (RecyclerView) rootView.findViewById(R.id.customorder_list);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(recyclerViewOrders.getContext()));

        Query clientesQuery = getQuery(mDatabase);
        mCabeceraAdapter = new FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder>(CabeceraOrden.class, R.layout.customorder_list_content,
                CabeceraViewHolder.class, clientesQuery) {
            @Override
            protected void populateViewHolder(final CabeceraViewHolder viewHolder, final CabeceraOrden model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                emptyView.setVisibility(View.GONE);
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

            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {


                if (MainActivity.mPickingOrderSelected == 0) {

                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
                } else {

                    // pasar la orden a Picking
                    pasarOrdenAPicking(model);

                    // restar los productos de la cabecera de Ordenes.
                    // restar los productos del detalle Total de Ordenes.
                    // Sumar los productos a la cabecera de Picking.
                    // Sumar los productos al detalle de  Picking.
                    // Modificar el estado de la orden a Picking.


//                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
//                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_PICKING);
//                    SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
//                    String formattedDate = df.format(new Date());
//                    builder.withValue(CustomOrdersColumns.DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER, formattedDate);
//                    builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, MainActivity.getmPickingOrderSelected());
//                    batchOperations.add(builder.build());
//                    try {
//                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                        onDataChange();
//                    } catch (RemoteException | OperationApplicationException e) {
//                        Log.e(getString(R.string.InformeError), getString(R.string.InformeErrorApplyingBatchInsert), e);
//
//                    }
//                }

                }
            }
        };

        recyclerViewOrders.setAdapter(mCabeceraAdapter);


//        mCabeceraAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
//            @Override
//            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {
//                mItem = id;
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//
////                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);
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

//            @Override
//            public void onItemDismissCall(long cursorID) {
//                mCustomOrderIdSelected = cursorID;
//
//                AlertDialog.Builder alert;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    alert = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
//                } else {
//                    alert = new AlertDialog.Builder(getContext());
//                }
//
//                alert.setMessage(getString(R.string.doYouWantDelete));
//                alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        onDataChange();
//
//                        dialog.cancel();
//                    }
//                });
//                alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(2);
//
//                        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withRefCustomOrder(mCustomOrderIdSelected));
//                        ContentProviderOperation.Builder builder1 = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
//                        batchOperations.add(builder.build());
//                        batchOperations.add(builder1.build());
//
//                        try {
//
//                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
////                    notifyItemRemoved(position);
//                        } catch (RemoteException | OperationApplicationException e) {
//
//                        } finally {
//                            onDataChange();
//                        }
//                    }
//                });
//                alert.create().show(); // btw show() creates and shows it..
//
//
//            }
//
//            @Override
//            public void onItemAceptedCall(long cursorID) {
//                mCustomOrderIdSelected = cursorID;
//
//                if (MainActivity.mPickingOrderSelected == 0) {
//
//                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
//                    onDataChange();
//                } else {
//                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
//                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_PICKING);
//                    SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
//                    String formattedDate = df.format(new Date());
//                    builder.withValue(CustomOrdersColumns.DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER, formattedDate);
//                    builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, MainActivity.getmPickingOrderSelected());
//                    batchOperations.add(builder.build());
//                    try {
//                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                        onDataChange();
//                    } catch (RemoteException | OperationApplicationException e) {
//                        Log.e(getString(R.string.InformeError), getString(R.string.InformeErrorApplyingBatchInsert), e);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onDataChange() {
//                getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, CustomOrderListFragment.this);
//                getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
//                upDateWitget(getContext());
//
//            }


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCabeceraAdapter, ADAPTER_CABECERA_ORDEN);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewOrders);


        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            // TOdo: levanar el detalle de ordenes cuando esta la tableta en horizontal
            //loadDustomDetailFragment();

        }


        return rootView;
    }

    private void pasarOrdenAPicking(CabeceraOrden cabeceraOrden) {

        limpiarTodosLosSemaforosLiberar();


        readBlockTotalInicial("-KX22-_c-M1VPBuFsdQI");
        readBlockCabeceraOrden("2");
        Task<Void> allTask;
        allTask = Tasks.whenAll(mTotalInicialTask, mCabeceraOrdenTask);
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DataSnapshot data = (DataSnapshot) mTotalInicialTask.getResult();

                // do something with db data?
                if (data == null) {
                    Log.i(LOG_TAG, "pasarOrdenAPickingl onSuccess Detalle = NuLL- ");
                } else {
                    data.getKey();
                    data.getValue(Detalle.class);
                    Log.i(LOG_TAG, "pasarOrdenAPickingl onSuccess Key =" + data.getKey() + "- nombre Producto" + data.getValue(Detalle.class).getProducto().getNombreProducto());

                }

                DataSnapshot dataCabecera = (DataSnapshot) mCabeceraOrdenTask.getResult();

                // do something with db data?
                if (dataCabecera == null) {
                    Log.i(LOG_TAG, "pasarOrdenAPickingl onSuccess dataCabecera = NuLL- ");
                } else {
                    dataCabecera.getKey();
                    Log.i(LOG_TAG, "pasarOrdenAPickingl onSuccess cabecera =" + dataCabecera.getKey() + "- monto cab" + dataCabecera.getValue(CabeceraOrden.class).getTotales().getMontoEnOrdenes());

                }
            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               liberarRecusosTomados();
            }
        });

        mDetalleCabeceraAnterior = cabeceraOrden;

//        mDatabase.child(ESQUEMA_ORDENES).child(mDetalleCabeceraAnterior.getClienteKey()).runTransaction(new Transaction.Handler() {
//
//
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                CabeceraOrden cabeceraOrden1b = mutableData.getValue(CabeceraOrden.class);
//                if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
//                    Log.d(LOG_TAG, "pasarOrdenAPicking cabeceraOrden1b-null, es un error");
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
//                        Log.d(LOG_TAG, "pasarOrdenAPicking  keyProducto: " + productoKey);
//                        Log.d(LOG_TAG, "pasarOrdenAPicking " + d.getProducto().getNombreProducto() + " - " + d.getCantidadOrden());
//
//
//                        Log.d(LOG_TAG, "orden:agregarProductoAlaOrden 1c-NOT null");
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


//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getChildrenCount() > 0)// Existen Cabecera
//                {
////   1b -Bloqueo toda la orden y puedo modificar toda sus dependencias.  Pues modifico mientra tengo tomada la orden.
//                    mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).child("cabecera").runTransaction(new Transaction.Handler() {
//                        @Override
//                        public Transaction.Result doTransaction(MutableData mutableData) {
//                            // Bloqueo la orden para modificaciones,
//                            // Actualizo  el esquema y luego lo libero.
//
//
//
//                        }
//
//                        @Override
//                        public void onComplete(DatabaseError databaseError, boolean commited,
//                                               DataSnapshot dataSnapshot) {
//                            // Transaction completed
//                            Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete:  databaseError" + databaseError);
//                            Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: boolean b" + commited);
//                            CabeceraOrden cabecera_orden = dataSnapshot.getValue(CabeceraOrden.class);
//
//                            long numeroOrden = cabecera_orden.getNumeroDeOrden();
//                            Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: ID " + numeroOrden);
//                            Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: Monto Orden " + cabecera_orden.getTotales().getMontoEnOrdenes());
//
//                            mCantidadTotal.setText("Items: " + String.valueOf(cabecera_orden.getTotales().getCantidadDeProductosDiferentes()));
//                            NumberFormat format = NumberFormat.getCurrencyInstance();
//                            mMontoTotal.setText("Monto Orden" + format.format(cabecera_orden.getTotales().getMontoEnOrdenes()));
//                            mMontoTotalDelivey.setText("Monto Entregado" + format.format(cabecera_orden.getTotales().getMontoEntregado()));
//
//                        }
//                    });
//
//                }}}}


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

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putLong(M_ITEM, mItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
//        getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
//        getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
        super.onResume();
    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser && mCabeceraAdapter != null) {
//            getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
//            getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
//
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(CUSTOM_LOADER_LIST, null, this);
//        getLoaderManager().initLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, this);
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
//
//    private void setupRecyclerView(@NonNull RecyclerView recyclerViewOrders) {
//        recyclerViewOrders.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//
//        switch (id) {
//
//            case CUSTOM_LOADER_LIST:
//                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
//                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER
//                };
//
//
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.ShowJoin.CONTENT_URI,
//                        proyection,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + "=" + 0,
//                        null,
//                        null);
//
//
//            case CUSTOM_LOADER_TOTAL_PRODUCTOS:
//
//                String select[] = {
///* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
///* 1 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
///* 2 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER,
///* 3 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
///* 4 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,
///* 5 */             "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
///* 6 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL,
///* 7 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
///* 8 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
///* 9 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
///* 10 */            LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL
//
//                };
//
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.join_Product_Detail_order.CONTENT_URI,
//                        select,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderDetailFragment.STATUS_ORDER_INICIAL,
//                        null,
//                        null);
//
//
//            default:
//
//
//                return null;
//        }
//
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        switch (loader.getId()) {
//
//            case CUSTOM_LOADER_LIST:
//                if (data != null && data.moveToFirst()) {
//                    mCabeceraAdapter.swapCursor(data);
//                    if (mTwoPane) {
//
//                    }
//                }
//
//
//                break;
//
//
//            case CUSTOM_LOADER_TOTAL_PRODUCTOS:
//                if (data != null && data.moveToFirst()) {
//                    mCursorAdapterTotalProductos.swapCursor(data);
//                }
//
//                break;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mCabeceraAdapter.swapCursor(null);
//    }
//

    public void loadDustomDetailFragment() {
        CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();

        Bundle arguments = new Bundle();

//        arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, mItem);
        if (mItem != 0) {
            arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
        } else {
            // go to the last order
            arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
        }
        fragment.setArguments(arguments);

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.customorder_detail_container, fragment)
                .commit();        // go to the last order
        arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
    }

    public void changeSize(View view) {
        Interpolator interpolator = AnimationUtils.loadInterpolator(getContext(), android.R
                .interpolator.fast_out_slow_in);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(recyclerViewTotalProductos, View.SCALE_X, (small ? LARGE_SCALE : 1f));
        scaleX.setInterpolator(interpolator);
        scaleX.setDuration(symmetric ? 600L : 200L);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(recyclerViewTotalProductos, View.SCALE_Y, (small ? 1f : 0.1f));
        scaleY.setInterpolator(interpolator);
        scaleY.setDuration(600L);
        scaleX.start();
        scaleY.start();


        // toggle the state so that we switch between large/small and symmetric/asymmetric
        small = !small;
        if (small) {
            recyclerViewTotalProductos.setVisibility(View.GONE);
            symmetric = !symmetric;
        }

    }


    private void animateViewsIn() {
//        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        int count = recyclerViewOrders.getChildCount();
        float offset = getResources().getDimensionPixelSize(R.dimen.offset_y);
        Interpolator interpolator =
                AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear_out_slow_in);

        // loop over the children setting an increasing translation y but the same animation
        // duration + interpolation
        for (int i = 0; i < count; i++) {
            View view = recyclerViewOrders.getChildAt(i);
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(-offset);
            view.setAlpha(0.85f);
            // then animate back to natural position
            view.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setInterpolator(interpolator)
                    .setDuration(1000L)
                    .start();
            // increase the offset distance for the next view
            offset *= 1.5f;
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(ESQUEMA_ORDENES_CABECERA).child(mEmpresaKey).child(String.valueOf(ORDER_STATUS_INICIAL));
    }


    public void readBlockTotalInicial(String productoKey) {

//        Lee y bloquea el total Inicial para un pruducto de una empresa
//        Si el producto esta bloqueda Retorna error
//

        mTotalInicialCompletionTask = new TaskCompletionSource<>();
        mTotalInicialTask = mTotalInicialCompletionTask.getTask();
        mTotalInicialTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "readBlockTotalInicial onComplete mTotalInicialTask " + task.toString());
                Log.i(LOG_TAG, "readBlockTotalInicial onComplete  task.isSuccessful() " + task.isSuccessful());
                if (task.isSuccessful()) {
                    mLiberarSemaforoTotalInicial = true;
                }

            }
        });
        mTotalInicialTask.addOnFailureListener(new OnFailureListener() {
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
                if (commited) {

                    mTotalInicialCompletionTask.setResult(dataSnapshot);
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
                        Log.i(LOG_TAG, "readBlockTotalInicial onComplete False " + "error nulo");
                        mTotalInicialCompletionTask.setException(new Exception("Error Nulo"));
                    } else {
                        Log.i(LOG_TAG, "readBlockTotalInicial onComplete False " + databaseError.toString());
                        mTotalInicialCompletionTask.setException(new Exception(databaseError.getMessage()));
                    }
                }


            }
        });
    }

    public void liberarTotalInicial(final String productoKey) {


        mLiberarTotalInicialCompetionTask = new TaskCompletionSource<>();
        mLiberarTotalInicialTask = mLiberarTotalInicialCompetionTask.getTask();
        mLiberarTotalInicialTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "liberarTotalInicial onComplete liberarTotalInicial" + task.toString());
                if (task.isSuccessful()) {
                    mLiberarSemaforoTotalInicial = false;
                }

            }
        });
        mTotalInicialTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "liberarTotalInicial onFailure liberarTotalInicial " + e.toString());
//                TOdo: ver que hacer cuando falla el relase. cuanto se insiste;
//                liberarTotalInicial(productoKey);
            }
        });

        mDatabase.child(ESQUEMA_ORDENES_TOTAL_INICIAL).child(mEmpresaKey).child(productoKey).runTransaction(new Transaction.Handler() {
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
                // Transaction completed
                Log.d(LOG_TAG, "liberarTotalInicial: boolean b" + commited);
                if (commited) {
//                    mLiberarSemaforoTotalInicial=false;
                    mLiberarTotalInicialCompetionTask.setResult(dataSnapshot);
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
                        mLiberarTotalInicialCompetionTask.setException(new Exception("Error Nulo"));
                        Log.i(LOG_TAG, "liberarTotalInicial onComplete False " + "error nulo");
                    } else {
                        mLiberarTotalInicialCompetionTask.setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "liberarTotalInicial onComplete False " + databaseError.toString());
                    }
                }


            }
        });
    }

    public void readBlockCabeceraOrden(String numeroOrden) {

//        Lee y bloquea el total Inicial para un pruducto de una empresa
//        Si el producto esta bloqueda Retorna error
//


        mCabeceraOrdenCompletionTask = new TaskCompletionSource<>();
        mCabeceraOrdenTask = mCabeceraOrdenCompletionTask.getTask();
        mCabeceraOrdenTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete mCabeceraOrdenTask " + task.toString());
                Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete is succesfull " + task.isSuccessful());
                if (task.isSuccessful()) {
                    mLiberarSemaforoCabeceraOrden = true;
                }
            }
        });
        mCabeceraOrdenTask.addOnFailureListener(new OnFailureListener() {
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
                        Log.i(LOG_TAG, "readBlockCabeceraOrden No se puede Modificar ");
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
                Log.i(LOG_TAG, "readBlockCabeceraOrden Inicial: boolean b" + commited);

                if (commited) {

                    mCabeceraOrdenCompletionTask.setResult(dataSnapshot);
                    Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete True ");
                    CabeceraOrden cabeceraOrden = dataSnapshot.getValue(CabeceraOrden.class);
                    if (cabeceraOrden == null) {
                        Log.d(LOG_TAG, "readAndBlockCabeceraOrden Inicial cabecera NULL");
                    } else {
                        Log.d(LOG_TAG, "readBlockCabeceraOrden Inicial semaforo " + cabeceraOrden.getSemaforo());
                    }
                } else {


                    if (databaseError == null) {
                        mCabeceraOrdenCompletionTask.setException(new Exception("error nulo"));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete False " + "error nulo");
                    } else {
                        mCabeceraOrdenCompletionTask.setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete False " + databaseError.toString());
                    }

                }


            }
        });
    }

    public void liberarCabeceraOrden(String numeroOrden) {

        mLiberarcabeceraOrdenCompletionTask = new TaskCompletionSource<>();
        mLiberarCabeceraOrdenTask = mLiberarcabeceraOrdenCompletionTask.getTask();
        mLiberarCabeceraOrdenTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.i(LOG_TAG, "liberarCabeceraOrden onComplete liberarTotalInicial" + task.toString());
            if(task.isSuccessful()){    mLiberarSemaforoCabeceraOrden = false;}

            }
        });
        mTotalInicialTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "liberarCabeceraOrden onFailure liberarTotalInicial " + e.toString());
//                TOdo: ver que hacer cuando falla el relase. cuanto se insiste;
//                liberarTotalInicial(productoKey);
            }
        });


//        *1-B*/
        mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(numeroOrden).child("cabecera").runTransaction(new Transaction.Handler() {
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

                if (commited) {

                    mLiberarcabeceraOrdenCompletionTask.setResult(dataSnapshot);
                    Log.i(LOG_TAG, "liberarCabeceraOrden onComplete True ");
                    CabeceraOrden cabeceraOrden = dataSnapshot.getValue(CabeceraOrden.class);
                    if (cabeceraOrden == null) {
                        Log.d(LOG_TAG, "liberarCabeceraOrden cabecera NULL");
                    } else {
                        Log.d(LOG_TAG, "liberarCabeceraOrden semaforo" + String.valueOf(cabeceraOrden.getSemaforo()));
                    }
                } else {
//                    mCabeceraOrdenCompletionTask.setException(new Exception("Bloqueado"));


                    if (databaseError == null) {
                        mLiberarTotalInicialCompetionTask.setException(new Exception("Error Nulo"));
                        Log.i(LOG_TAG, "liberarCabeceraOrden onComplete False " + "error nulo");
                    } else {
                        mLiberarTotalInicialCompetionTask.setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "liberarCabeceraOrden onComplete False " + databaseError.toString());
                    }

                }


            }
        });
    }


    public void readBlockProductosEnOrdenes(String productKey, String numeroDeOrden) {

//        Lee y bloquea prodcutos en Ordenes 5


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
                } else {
                    if (databaseError == null) {
                        mProductosEnOrdenesCompletionTask.setException(new Exception("error nulo"));
                    } else {
                        mProductosEnOrdenesCompletionTask.setException(new Exception(databaseError.toString()));
                        Log.i(LOG_TAG, "readBlockCabeceraOrden onComplete False " + databaseError.toString());
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
                        mLiberarProductosEnOrdenesCompletionTask.setException(new Exception("Error Nulo"));
                    } else {
                        mLiberarProductosEnOrdenesCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }
public void limpiarTodosLosSemaforosLiberar(){
        mLiberarSemaforoTotalInicial=false;
        mLiberarSemaforoCabeceraOrden=false;
        mLiberarSemaforoProductosEnOrdenes=false;
        mLiberarSemaforoPicking=false;
        mLiberarSemaforoPickingTotal=false;
    }


    public void readBlockPicking(String estado, String numeroPicking) {

//        Lee y bloquea  Ordenes ce Picking 6


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

/*6*/
        mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(estado).child(numeroPicking).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CabeceraPicking cabeceraPicking = mutableData.getValue(CabeceraPicking.class);
                if (cabeceraPicking == null) {
                } else {
                    if (cabeceraPicking.sepuedeModificar()) {
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
                    mPickingCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        mPickingCompletionTask.setException(new Exception("error nulo"));
                    } else {
                        mPickingCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarPicking(String estado, String numeroPicking) {

        mLiberarPickingCompletionTask = new TaskCompletionSource<>();
        mLiberarPickingTask = mLiberarPickingCompletionTask.getTask();
        //noinspection unchecked
        mLiberarPickingTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoPicking = false;
                }
            }
        });
        mLiberarPickingTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

/*6*/
        mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(estado).child(numeroPicking).runTransaction(new Transaction.Handler() {
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
                        mLiberarPickingCompletionTask.setException(new Exception("Error Nulo"));
                    } else {
                        mLiberarPickingCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void readBlockPickingTotal(String estado, String numeroPicking,String productKey) {

//        Lee y bloquea  Ordenes ce Picking 6


        mPickingTotalCompletionTask = new TaskCompletionSource<>();
        mPickingTotalTask = mPickingTotalCompletionTask.getTask();
        mPickingTotalTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoPickingTotal = true;
                }
            }
        });

/*7*/
        mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(estado).child(numeroPicking).child(productKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Detalle detalle = mutableData.getValue(Detalle.class);
                if (detalle == null) {
                } else {
                    if (detalle.sepuedeModificar()) {
                        detalle.bloquear();
                    } else {
                        return Transaction.abort();
                    }

                }
                mutableData.setValue(detalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                if (commited) {
                    mPickingTotalCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        mPickingTotalCompletionTask.setException(new Exception("error nulo"));
                    } else {
                        mPickingTotalCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarPickingTotal(String estado, String numeroPicking, String productKey) {

        mLiberarPickingTotalCompletionTask = new TaskCompletionSource<>();
        mLiberarPickingTotalTask = mLiberarPickingTotalCompletionTask.getTask();
        //noinspection unchecked
        mLiberarPickingTotalTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mLiberarSemaforoPicking = false;
                }
            }
        });
        mLiberarPickingTotalTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

/*7*/
        mDatabase.child(ESQUEMA_PICKING_TOTAL).child(mEmpresaKey).child(estado).child(numeroPicking).child(productKey).runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Detalle cetalle = mutableData.getValue(Detalle.class);
                if (cetalle == null) {
                } else {
                    if (!cetalle.sepuedeModificar()) {
                        cetalle.liberar();
                    } else {
                        return Transaction.abort();
                    }
                }
                mutableData.setValue(cetalle);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                if (commited) {
                    mLiberarPickingTotalCompletionTask.setResult(dataSnapshot);
                } else {
                    if (databaseError == null) {
                        mLiberarPickingTotalCompletionTask.setException(new Exception("Error Nulo"));
                    } else {
                        mLiberarPickingTotalCompletionTask.setException(new Exception(databaseError.toString()));
                    }
                }
            }
        });
    }

    public void liberarRecusosTomados(){
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoTotalInicial" + mLiberarSemaforoTotalInicial);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoCabeceraOrden" + mLiberarSemaforoCabeceraOrden);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoProductosEnOrdenes" + mLiberarSemaforoProductosEnOrdenes);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoPicking" + mLiberarSemaforoPicking);
        Log.i(LOG_TAG, "liberarRecusosTomados-mLiberarSemaforoPickingTotal" + mLiberarSemaforoPickingTotal);

        if (mLiberarSemaforoTotalInicial) {
            liberarTotalInicial("-KX22-_c-M1VPBuFsdQI");
        }
        if (mLiberarSemaforoCabeceraOrden) {
            liberarCabeceraOrden("2");
        }
        if (mLiberarSemaforoProductosEnOrdenes) {
            liberarProductosEnOrdenes("2","s");
        }
        if ( mLiberarSemaforoPicking) {
            liberarPicking("2","s");
        }
        if (mLiberarSemaforoPickingTotal) {
            liberarPickingTotal("2","s","");
        }


    }

}

