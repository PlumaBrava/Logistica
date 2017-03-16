package com.nextnut.logistica;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.CabeceraPicking;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.DetalleViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_TOTAL_INICIAL;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_PICKING;
import static com.nextnut.logistica.util.Constantes.PICKING_STATUS_INICIAL;

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


    private long mItem = 0;


    private boolean mTwoPane;

    private static final String LOG_TAG = CustomOrderListFragment.class.getSimpleName();
    private CabeceraOrden mCabeceraOrdenDato;
    private DataSnapshot mProductosEnOrdenDatos;
    ArrayList<Task> taskList = new ArrayList<Task>();
    Task<Void> allTask;

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
        Log.i(LOG_TAG, "adapter:Ref: " + totalProductos.toString());
        Log.i(LOG_TAG, "adapter:mEmpresakey: " + mEmpresaKey);
        Log.i(LOG_TAG, "adapter:mUserKey: " + mUserKey);


        mCursorAdapterTotalProductos = new FirebaseRecyclerAdapter<Detalle, DetalleViewHolder>(Detalle.class, R.layout.order_detail_item,
                DetalleViewHolder.class, totalProductos) {
            @Override
            protected void populateViewHolder(final DetalleViewHolder viewHolder, final Detalle model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                emptyViewTotalProducts.setVisibility(View.GONE);
                viewHolder.mfavorito.setVisibility(View.GONE);
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
//                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDEN_STATUS_PICKING);
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
//                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDEN_STATUS_PICKING);
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

    private void pasarOrdenAPicking(final CabeceraOrden cabeceraOrden) {

        if(hayTareaEnProceso()){
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
                                Log.i(LOG_TAG, "pasarOrdenAPickingl onDetalleOrden Key- " + snapshot.getKey());
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
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl Completo el bloqueo- ");

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    Map<String, Object> totalInicialDetalleValues = null;
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
                                            Log.i(LOG_TAG, "pasarOrdenAPickingl TotalInicial Detalle = NuLL- ");
                                        } else {
                                            detalleOrdenTotalInicial.modificarCantidadEnTotalInicial(detalleOrdenAux, detalleOrden);
                                            if(detalleOrdenTotalInicial.getCantidadOrden()==0){totalInicialDetalleValues=null;}
                                            else {
                                                detalleOrdenTotalInicial.liberar();
                                                totalInicialDetalleValues = detalleOrdenTotalInicial.toMap();
                                            }
                                            /*3 */
                                            childUpdates.put(nodoTotalInicial_3(productKey), totalInicialDetalleValues);
                                        }



                                        // Actualizacion de totales en Picking (7)


                                        Detalle detallePickingTotal = ((DataSnapshot) mPickingTotalTask.get(i).getResult()).getValue(Detalle.class);


                                            if (detallePickingTotal == null) {
                                                // di no existe esta estructura, se crea una en cero.
                                                Log.i(LOG_TAG, "pasarOrdenAPickingl TotalPicking= NULL" );
                                                detallePickingTotal = new Detalle(0.0, detalleOrdenTotalInicial.getProducto(), null);
                                                detallePickingTotal.modificarCantidadEnTotalInicial(detalleOrden, detalleOrdenAux);
                                            } else {
                                                Log.i(LOG_TAG, "pasarOrdenAPickingl TotalPicking=" + ((DataSnapshot) mTotalInicialTask.get(i).getResult()).getKey() + "- nombre Producto" + ((DataSnapshot) mTotalInicialTask.get(i).getResult()).getValue(Detalle.class).getProducto().getNombreProducto());
                                                detallePickingTotal.modificarCantidadEnTotalInicial(detalleOrden, detalleOrdenAux);
                                            }

                                            /*7 */
                                        detallePickingTotal.liberar();
                                        childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_INICIAL,MainActivity.getmPickingOrderSelected(), productKey),detallePickingTotal.toMap());


                                            /*5*/
                                            childUpdates.put(nodoProductosXOrdenInicial_5(productKey, mCabeceraOrdenDato.getNumeroDeOrden()), null);

                                            /*4 */ //No se modifica, queda igual
//                                            childUpdates.put(nodoDetalleOrden_4(mCabeceraOrdenDato.getNumeroDeOrden(), productKey), null);

                                    i++;
                                    }

                                    // Actualizacion de totales en Picking (6)
                                    CabeceraPicking cabeceraPicking = ((DataSnapshot) mPickingTask.getResult()).getValue(CabeceraPicking.class);
                                    cabeceraPicking.liberar();
                                    childUpdates.put(nodoPicking_6(PICKING_STATUS_INICIAL,((DataSnapshot) mPickingTask.getResult()).getKey()),cabeceraPicking.toMap());


                                /*2 */
                                    mCabeceraOrdenDato.setEstado(ORDEN_STATUS_PICKING);
                                    mCabeceraOrdenDato.liberar();
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl MainActivity.getmPickingOrderSelected() "+ MainActivity.getmPickingOrderSelected());
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl mCabeceraOrdenDato "+ mCabeceraOrdenDato.getNumeroDePickingOrden());
                                    mCabeceraOrdenDato.setNumeroDePickingOrden(MainActivity.getmPickingOrderSelected());
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl mCabeceraOrdenDato "+ mCabeceraOrdenDato.getNumeroDePickingOrden());
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl getNumeroDeOrden() "+ mCabeceraOrdenDato.getNumeroDeOrden());
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl getNumeroDePickingOrden() "+ mCabeceraOrdenDato.getNumeroDePickingOrden());

                                    childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_INICIAL,mCabeceraOrdenDato.getNumeroDeOrden()), null);
                                    childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_PICKING,mCabeceraOrdenDato.getNumeroDeOrden(),MainActivity.getmPickingOrderSelected() ),mCabeceraOrdenDato.toMap());
                                    childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrdenDato.getNumeroDeOrden()),mCabeceraOrdenDato.toMap());

                                    mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.i(LOG_TAG, "pasarOrdenAPickingl updateChildren-onFailure "+e.toString());
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
                                            Log.i(LOG_TAG, "pasarOrdenAPickingl - OnCompleteListener task.isSuccessful():"+task.isSuccessful());

                                        }
                                    });
                                }


                            });
                            allTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    liberarRecusosTomados();
                                    liberarArrayTaskConBloqueos();
                                    onDialogAlert(getResources().getString( R.string.ERROR_NO_SE_PUDO_BLOQUEAR));

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.i(LOG_TAG, "pasarOrdenAPickingl Operacion fallo- " + task.getException().toString());
                    liberarRecusosTomados();
                    liberarArrayTaskConBloqueos();
                    onDialogAlert(getResources().getString( R.string.ERROR_NO_SE_PUDO_BLOQUEAR));
                }
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
        return databaseReference.child(ESQUEMA_ORDENES_CABECERA).child(mEmpresaKey).child(String.valueOf(ORDEN_STATUS_INICIAL));
    }


}

