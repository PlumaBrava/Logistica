package com.nextnut.logistica;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.PrductosxOrden;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.DetalleViewHolder;

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

import static com.nextnut.logistica.util.Constantes.ADAPTER_DETALLE_ORDEN;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_FAVORITOS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES_DETALLE;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_KEYLIST;
import static com.nextnut.logistica.util.Constantes.EXTRA_NRO_PICKIG;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_DETALLE;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_EN_DELIVERING;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.REQUEST_PRODUCT;
import static com.nextnut.logistica.util.Constantes.UPDATE_CUSTOMER;
import static com.nextnut.logistica.util.Constantes.detalleOrdenRef_1C;
import static com.nextnut.logistica.util.Constantes.detalleOrdenRef_4;

/**
 * A fragment representing a single CustomOrder detail screen.
 * This fragment is either contained in a {@link CustomOrderListFragment}
 * in two-pane mode (on tablets) or a {@link CustomOrderDetailActivity}
 * on handsets.
 */
public class CustomOrderDetailFragment extends FragmentBasic {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    private static final String LOG_TAG = CustomOrderDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
    private long mItem;
    private long mCustomRef = 0;
    private long mIdDetailCustomOrder_for_favorite = 0;
    private CheckBox mCheckBox_for_favorite;
    private Cursor c_favorite;

    private Detalle mDetalleAnterior;
    private Detalle mDetalleDato;//Tiene los datos a ser cargados en una operacion
    private Double mCantidadDato;//Tiene los datos a ser cargados en una operacion
    private String mproductKeyDato;
    private DataSnapshot mDataSanpshotFavoritosDato;//Tiene los datos a ser cargados en una operacion
    private DataSnapshot mDataSanpshotPrinting;//para imprimir
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public static final String ARG_ITEM_ID = "item_id";
    public static final String CUSTOM_ORDER_ACTION = "custom_order_action";


    private TextView mOrderNumber;
    private TextView mCustomName;
    private TextView mLastName;
    private TextView mDeliveyAddress;
    private TextView mCity;
    private TextView mCuit;
    private TextView mIva;
    private Double mIvaCalculo;
    String mCurrentPhotoPath = null;
    private CheckBox mIsSpecialCustom;
    private ArrayList<String> mKeyList;

    public TextView mCantidadTotal;
    public TextView mMontoTotal;
    public TextView mMontoTotalDelivey;

    public CabeceraOrden mCabeceraOrden;
    public Long mNroPicking;

    ArrayList<Task> taskList = new ArrayList<Task>();
    Task<Void> allTask;
    private Button mBotonSeleccionCliente;
    private Button mBotonSeleccionProduto;

    private int mAction;


    // This paramenter is use the define the acction we need to do.
    // mAction recibe the information from bundle
    public static final int CUSTOM_ORDER_NEW = 0;  // A new order was created.
    //    public static final int CUSTOM_ORDER_DOUBLE_SCREEN = 1;
    public static final int CUSTOM_ORDER_SAVE = 2;

    public static final int CUSTOM_ORDER_SELECTION = 3; // A order was selected.
    public static final int ACTION_CUSTOM_ORDER_DELIVERY = 104;

    public static final int STATUS_ORDER_INICIAL = 0;
    public static final int STATUS_ORDER_PICKING = 1;
    public static final int STATUS_ORDER_DELIVEY = 2;
    public static final int STATUS_ORDER_DELIVED = 3;

    CollapsingToolbarLayout appBarLayout;

    private View mRootView;
    RecyclerView mDetalleRecyclerView;
    //    OrderDetailCursorAdapter mDetalleAdapter;
    private FirebaseRecyclerAdapter<Detalle, DetalleViewHolder> mDetalleAdapter;

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

    Button openButton;
    Button sendButton;

    Cursor mCursorTotales;

    public CustomOrderDetailFragment() {
        Log.i(LOG_TAG, "CustomOrderDetailFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = getArguments().getLong(ARG_ITEM_ID, -1);
        }
        mAction = getArguments().getInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);

        Activity activity = this.getActivity();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        mCabeceraOrden = getArguments().getParcelable(EXTRA_CABECERA_ORDEN);
        mNroPicking = getArguments().getLong(EXTRA_NRO_PICKIG);
        Log.d(LOG_TAG, "orden:onComplete: mcabeceraOrden " + mCabeceraOrden.getClienteKey());
        Log.d(LOG_TAG, "orden:onComplete: mcabeceraOrden " + mCabeceraOrden.getNumeroDeOrden());
        Log.d(LOG_TAG, "orden:onComplete: mcabeceraOrden " + mCabeceraOrden.getCliente().getNombre());

    }

    @Override
    public void savePhoto(Bitmap bitmap) {

    }


    @Override
    public void onResume() {
//        switch (mAction) {
//            case CUSTOM_ORDER_NEW: // Go to the last order
//                getLoaderManager().restartLoader(CUSTOM_LOADER_NEW, null, this);
//                break;
//
//            case CUSTOM_ORDER_SELECTION: // Go to the mItem order.
//                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
//                getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
//                break;
//            case ACTION_CUSTOM_ORDER_DELIVERY: // Process Delivery state
//                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
//                getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
//                break;
//            default:
//                break;
//        }

        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.customorder_detail, container, false);

        openButton = (Button) mRootView.findViewById(R.id.open);
        sendButton = (Button) mRootView.findViewById(R.id.send);
        openButton.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        // open bluetooth connection
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // send data typed by the user to be printed
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // me fijo los productos cargados. si hay, los envio a imprimir.
                refDetalleOrden_4_ListaXOrden(mCabeceraOrden.getNumeroDeOrden())

                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(LOG_TAG, "onClick Print Cantidad de getChildrenCount: " + dataSnapshot.getChildrenCount());
                                Log.d(LOG_TAG, "onClick Print Cantidad de getRef(): " + dataSnapshot.getRef());
                                Log.d(LOG_TAG, "onClick Print Cantidad de getKey(): " + dataSnapshot.getKey());
                                Log.d(LOG_TAG, "onClick Print Cantidad de exists(): " + dataSnapshot.exists());
                                Log.d(LOG_TAG, "onClick Print Cantidad de hasChildren(): " + dataSnapshot.hasChildren());

                                if (dataSnapshot.exists()) {
                                    mDataSanpshotPrinting = dataSnapshot;
                                    try {
                                        sendData();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(LOG_TAG, "onClick onCancell Error: " + databaseError.toString());

                            }
                        });


            }
        });

        mOrderNumber = (TextView) mRootView.findViewById(R.id.orderNumber);
        mBotonSeleccionCliente = (Button) mRootView.findViewById(R.id.botonSelecionCliente);
        mBotonSeleccionCliente.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);

        mBotonSeleccionProduto = (Button) mRootView.findViewById(R.id.botonSelecionProdcuto);
        mBotonSeleccionProduto.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);

        mCustomName = (TextView) mRootView.findViewById(R.id.custom_name_text);
        mLastName = (TextView) mRootView.findViewById(R.id.product_Lastname);
        final ImageView mImageCustomer = (ImageView) mRootView.findViewById(R.id.custom_imagen);
        mDeliveyAddress = (TextView) mRootView.findViewById(R.id.custom_delivery_address);
        mCity = (TextView) mRootView.findViewById(R.id.custom_city);
        mCuit = (TextView) mRootView.findViewById(R.id.CUIT);
        mIva = (TextView) mRootView.findViewById(R.id.IVA);

        mIsSpecialCustom = (CheckBox) mRootView.findViewById(R.id.custom_special);
        mCantidadTotal = (TextView) mRootView.findViewById(R.id.cantidadTotal);
        mMontoTotal = (TextView) mRootView.findViewById(R.id.montoToal);
        mMontoTotalDelivey = (TextView) mRootView.findViewById(R.id.montoToalDelivery);


        mBotonSeleccionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CustomSelectionActivity.class);
                putExtraFirebase_Fragment(intent);
                getActivity().startActivityForResult(intent, UPDATE_CUSTOMER);
            }

        });


        mBotonSeleccionProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ProductSectionActivity.class);
                putExtraFirebase_Fragment(intent);

                intent.putExtra(EXTRA_KEYLIST, mKeyList);
                Log.d(LOG_TAG, "mKeyList: " + mKeyList.toString());
                intent.putExtra("ITEM", mItem);
                getActivity().startActivityForResult(intent, REQUEST_PRODUCT);

            }

        });
        mBotonSeleccionProduto.setVisibility(View.GONE);

        final View emptyView = mRootView.findViewById(R.id.recyclerview_product_empty);
        mDetalleRecyclerView = (RecyclerView) mRootView.findViewById(R.id.product_list_customOrder);

        Query productosAsignados = getQuery(mDatabase);
        mKeyList = new ArrayList<String>();
        // me fijo si hay productos cargados. si No hay busco en favoritos y los agrego. Luego muestro el boton para agregar productos.
        refDetalleOrden_4_ListaXOrden(mCabeceraOrden.getNumeroDeOrden())
//        mDatabase.child(ESQUEMA_ORDENES_DETALLE).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "favorito Cantidad de getChildrenCount: " + dataSnapshot.getChildrenCount());
                        Log.d(LOG_TAG, "favorito Cantidad de getRef(): " + dataSnapshot.getRef());
                        Log.d(LOG_TAG, "favorito Cantidad de getKey(): " + dataSnapshot.getKey());
                        Log.d(LOG_TAG, "favorito Cantidad de exists(): " + dataSnapshot.exists());
                        Log.d(LOG_TAG, "favorito Cantidad de hasChildren(): " + dataSnapshot.hasChildren());

                        if (!dataSnapshot.exists()) {
                            Log.d(LOG_TAG, "favorito Sin Productos Asignados ");
                            refListaFavoritosXCliente(mCabeceraOrden.getClienteKey())
//                            mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(mCabeceraOrden.getClienteKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                //Existen favoritos para agregar.
                                                mDataSanpshotFavoritosDato = dataSnapshot;
                                                addFavorite();
                                            } else {
                                                // no existen favoritos para agregar
                                                mBotonSeleccionProduto.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);
                                                mKeyList.add(null);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            mBotonSeleccionProduto.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);

                                        }
                                    });


                        } else {

                            // ya existen productos asignados a esta orden. Leo las claves para sacarlas del listado propuesto.
                            Log.d(LOG_TAG, "favorito Con Productos Asignados. ");
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                mKeyList.add(messageSnapshot.getKey());
                                Log.d(LOG_TAG, "mKeyList added" + messageSnapshot.getKey());
                                mDatabase.child("copy").setValue(dataSnapshot.getValue());
                                mBotonSeleccionProduto.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "favorito error " + databaseError.toString());

                    }
                });
        mDetalleAdapter = new FirebaseRecyclerAdapter<Detalle, DetalleViewHolder>(Detalle.class, R.layout.order_detail_item,
                DetalleViewHolder.class, productosAsignados) {
            @Override
            protected void populateViewHolder(final DetalleViewHolder viewHolder, final Detalle model, final int position) {
                final DatabaseReference detalleRef = getRef(position);
                if (mCabeceraOrden.getEstado() >= ORDEN_STATUS_EN_DELIVERING) {
                    Log.i(LOG_TAG, "adapter:detalleRef:ORDEN_STATUS_EN_DELIVERING " + mCabeceraOrden.getEstado());
                    viewHolder.setDeliveryState();
                    viewHolder.mTextViewPrecioDelivery.setVisibility(View.VISIBLE);
                    viewHolder.mTextcantidadDelivery.setVisibility(View.VISIBLE);
                    viewHolder.mTextToalDelivery.setVisibility(View.VISIBLE);
                    viewHolder.mfavorito.setVisibility(View.GONE);
                } else {
                    Log.i(LOG_TAG, "adapter:detalleRef:NO ORDEN_STATUS_EN_DELIVERING " + mCabeceraOrden.getEstado());
                }

                emptyView.setVisibility(View.GONE);
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
                                                                onFavorite(viewHolder, model, productKey);

                                                            }
                                                        }
                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View view) {
                                Log.d(LOG_TAG, "adapter:onClick model: " + model.getProducto().getNombreProducto());
                                mDetalleAnterior = model;
                                showDialogNumberPicker(productKey);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Detalle model, int position) {
                // TODO: ACTUALIZAR TOTALES !!!
                mDetalleAnterior = model;
                abmDetalleDeOrden(0.0, getRef(position).getKey(), model);
//                borrarProductoDeOrden(getRef(position).getKey(), model);
                Log.d(LOG_TAG, " onItemDismissHolder: " + model.getProducto().getNombreProducto() + " pos: " + position);
                Log.d(LOG_TAG, " onItemDismissHolder: " + " key: " + getRef(position).getKey());

                mDetalleRecyclerView.setEnabled(false);
            }

            @Override
            protected void onItemAcceptedHolder(Detalle model, int position) {
                Log.d(LOG_TAG, "onItemAcceptedHolder: " + model.getProducto().getNombreProducto() + " pos: " + position);
            }
        };


        if (mAction == CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY) {
//            mDetalleAdapter.setDeliveryState();
        }

        assert mDetalleRecyclerView != null;
        setupRecyclerView(mDetalleRecyclerView);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mDetalleAdapter, ADAPTER_DETALLE_ORDEN);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mDetalleRecyclerView);

        return mRootView;
    }

    public void onFavorite(final DetalleViewHolder viewHolder, final Detalle model, final String productKey) {
        Log.d(LOG_TAG, "favorito viewHolder nombre: " + viewHolder.mTextViewNombre);
        Log.d(LOG_TAG, "favorito model-Producto: " + model.getProducto().getNombreProducto());
        Log.d(LOG_TAG, "favorito  productkey: " + productKey);
        Log.d(LOG_TAG, "favorito  mEmpresaKey: " + mEmpresaKey);
        Log.d(LOG_TAG, "favorito  mClienteKey: " + mCabeceraOrden.getClienteKey());
        refProductoFavoritoDeCliente(mCabeceraOrden.getClienteKey(), (productKey)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0)// Eviste el favorito, preguntar si se quiere modificar
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(getString(R.string.favoriteAlreadyExisist));
                    alert.setMessage(getString(R.string.doYouWantToChange));
                    alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {

                            viewHolder.mfavorito.setChecked(!viewHolder.mfavorito.isChecked());
                            dialog.cancel();
                        }
                    });
                    alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (viewHolder.mfavorito.isChecked()) {
                                mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(mCabeceraOrden.getClienteKey()).child(productKey).setValue(model);
                            } else {
                                mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(mCabeceraOrden.getClienteKey()).child(productKey).removeValue();

                            }
                        }
                    });
                    alert.create().show();

                } else {

                    mDatabase.child(ESQUEMA_FAVORITOS).child(mEmpresaKey).child(mCabeceraOrden.getClienteKey()).child(productKey).setValue(model);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addFavorite() {

        Log.d(LOG_TAG, "favorito  mEmpresaKey: " + mEmpresaKey);
        Log.d(LOG_TAG, "favorito  mClienteKey: " + mCabeceraOrden.getClienteKey());

        if (hayTareaEnProceso()) {
            return;
        }

        final ArrayList<String> keyListBkp = mKeyList;
        Log.d(LOG_TAG, "favorito  keyListBkp: " + keyListBkp.toString());
        readBlockCabeceraOrden(mCabeceraOrden.getNumeroDeOrden());
        mCabeceraOrdenTask.get(0).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    CabeceraOrden cabeceraOrden1b = ((DataSnapshot) mCabeceraOrdenTask.get(0).getResult()).getValue(CabeceraOrden.class);


                    if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
                        Log.d(LOG_TAG, "orden:abmDetalleDeOrden cabeceraOrden1b-null, es un error");

                    } else {

                        Map<String, Object> childUpdates = new HashMap<>();

                        int i = 0;
                        for (DataSnapshot data : mDataSanpshotFavoritosDato.getChildren()) {

                            Log.i(LOG_TAG, "pasarOrdenAPickingl onDetalleOrden Key- " + data.getKey());
                            String productKey = data.getKey();
                            readBlockTotalInicial(productKey);
                            taskList.add(mTotalInicialTask.get(i)); /*5*/
                            i++;
                        }

                        allTask = Tasks.whenAll(taskList.toArray(new Task[taskList.size()]));
                        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(final Void aVoid) {
                                Log.i(LOG_TAG, "pasarOrdenAPickingl Completo el bloqueo- ");

                                Map<String, Object> childUpdates = new HashMap<>();

                                int i = 0;

                                for (DataSnapshot data : mDataSanpshotFavoritosDato.getChildren()) {
                                    Log.i(LOG_TAG, "pasarOrdenAPickingl onDetalleOrden Key- " + data.getKey());
                                    String productKey = data.getKey();
                                    mKeyList.add(productKey);

                                    Detalle d = data.getValue(Detalle.class);
                                    Double cantidad =(Double) d.getCantidadOrden();
                                    Producto producto = d.getProducto();
                                    Log.d(LOG_TAG, "favorito  keyProducto: " + productKey);
                                    Log.d(LOG_TAG, "favorito  d : " + d.getProducto().getNombreProducto() + " - " + d.getCantidadOrden());

                                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden 1c-NOT null");
                                    Detalle detalle = new Detalle();
                                    detalle.ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
                                    mCabeceraOrden.getTotales().ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());

                                    Map<String, Object> detalleOrdenValues = detalle.toMap();
/*1c*/
                                    childUpdates.put(detalleOrdenRef_1C(mEmpresaKey, mCabeceraOrden.getNumeroDeOrden(), productKey), detalleOrdenValues);

/*4 */
                                    childUpdates.put(detalleOrdenRef_4(mEmpresaKey, mCabeceraOrden.getNumeroDeOrden(), productKey), detalleOrdenValues);

                                    Log.i("ClienteViewHolder", "saveCustomOrderProductproductoKey" + productKey);
                                    Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Nombre" + producto.getNombreProducto());
                                    Log.i("ClienteViewHolder", "saveCustomOrderProductproducto Precio" + producto.getPrecio());
                                    Log.i("informe", "det producto" + detalle.getProducto().getNombreProducto());
                                    Log.i("informe", "det cantidad" + detalle.getCantidadOrden());
/*5 */
                                    PrductosxOrden detallexOrden = new PrductosxOrden(mCliente, detalle);
                                     Map<String, Object> prductosxOrdenValues =  detallexOrden.toMap();
                                    Log.i("informe", "PrductosxOrden producto" + detallexOrden.getDetalle().getProducto().getNombreProducto());
                                    Log.i("informe", "PrductosxOrden cantidad" + detallexOrden.getDetalle().getCantidadOrden());
                                    childUpdates.put(nodoProductosXOrdenInicial_5(productKey, mCabeceraOrden.getNumeroDeOrden()), prductosxOrdenValues);



                                    // Actualizacion de Totales en Ordenes (3)
/*3*/
                                    Detalle detalleInicial = detalle;
                                    detalleInicial.setCantidadOrden(0.0);
                                    detalleInicial.setMontoItemOrden(0.0);
                                    Map<String, Object> totalInicialDetalleValues = null;

                                    Detalle detalleOrdenTotalInicial = ((DataSnapshot) mTotalInicialTask.get(i).getResult()).getValue(Detalle.class);


                                    if (detalleOrdenTotalInicial == null) {
                                        detalleOrdenTotalInicial = new Detalle(0.0, detalleInicial.getProducto(), null);
                                        detalleOrdenTotalInicial.modificarCantidadTotalDeOrden(cantidad, detalleInicial);
                                        detalleOrdenTotalInicial.liberar();
                                        totalInicialDetalleValues = detalleOrdenTotalInicial.toMap();
                                        Log.d("detalle1", "saveDetalleInicialTotales NULL -detalleAnteriorvar) " + detalleInicial.getCantidadOrden());
                                        Log.d("detalle1", "saveDetalleInicialTotales NULL -cantidadNUeva) " + cantidad);
                                        Log.i(LOG_TAG, "orden:SaveDetalleInicialTotales detalle NuLL- ");
                                    } else {
                                        Log.d("detalle1", "saveDetalleInicialTotales Not NULL -detalleAnteriorvar) " + detalleInicial.getCantidadOrden());
                                        Log.d("detalle1", "saveDetalleInicialTotales Not NULL -cantidadNUeva) " + detalleInicial);
                                        // update de totales con cantidades anteriores.
                                        detalleOrdenTotalInicial.modificarCantidadTotalDeOrden(cantidad, detalleInicial);
                                        detalleOrdenTotalInicial.liberar();
                                        if (detalleOrdenTotalInicial.getCantidadOrden() == 0) {
                                            totalInicialDetalleValues = null;
                                        } else {
                                            totalInicialDetalleValues = detalleOrdenTotalInicial.toMap();
                                        }

                                    }


                                            /*3 */
                                    childUpdates.put(nodoTotalInicial_3(productKey), totalInicialDetalleValues);


//                                        saveDetalleInicialTotales(cantidad, productKey); // cantidad es el valor nuevo y detalle tiene el anterior en este caso esta en cero




                                    mKeyList.add(productKey); // Agrega productkey para que no se repita.
                                    i++;
                                }

/*2 */
                                mCabeceraOrden.liberar();
                                Map<String, Object> cabeceraOrdenValues = mCabeceraOrden.toMap();


                                childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" + ORDEN_STATUS_INICIAL + "/" + mCabeceraOrden.getNumeroDeOrden(), cabeceraOrdenValues);
/*1b*/
                                childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);

                                mDatabase.updateChildren(childUpdates)

                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) { //No puedo escribir...
                                                liberarArrayTaskCasoExitoso();
                                                mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
                                                NumberFormat format = NumberFormat.getCurrencyInstance();
                                                mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
                                                mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
                                                mBotonSeleccionProduto.setVisibility(mCabeceraOrden.getEstado() == ORDEN_STATUS_INICIAL ? View.VISIBLE : View.GONE);
//                                            mKeyList.add(mproductKeyDato);
                                                Log.i(LOG_TAG, "pasarOrdenAPickingl - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                                            }
                                        }).addOnFailureListener(new OnFailureListener() { //No puedo escribir...
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mKeyList.clear();
                                        mKeyList = keyListBkp;
                                        Log.i(LOG_TAG, "pasarOrdenAPickingl updateChildren-onFailure " + mKeyList);
                                        liberarRecusosTomados();
                                        liberarArrayTaskConBloqueos();
                                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));

                                    }
                                });


                            }

                        }).addOnFailureListener(new OnFailureListener() { // No puedo bloquear todas los productos
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                liberarRecusosTomados();
                                liberarArrayTaskConBloqueos();
                                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + "Producto Inicial");
                            }
                        });


                    }
                }
            }
        })// cierro la lectura de la cabecera de orden
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        liberarRecusosTomados();
                        liberarArrayTaskConBloqueos();
                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR) + " Orden");

                    }
                })
        ;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("orden read", "mcabeceraOrden.getNumeroDeOrden() " + mCabeceraOrden.getNumeroDeOrden());

        mOrderNumber.setText(String.valueOf(mCabeceraOrden.getNumeroDeOrden()));
        mCustomName.setText(mCliente.getNombre());
        mLastName.setText(mCliente.getApellido());
        mDeliveyAddress.setText(mCliente.getDireccionDeEntrega());
        mCity.setText(mCliente.getCiudad());
        mCuit.setText(mCliente.getCuit());
        mIva.setText(String.format("%.1f", mCliente.getIva()) + " %");
        mIvaCalculo = mCliente.getIva();
        mCurrentPhotoPath = null;
        mIsSpecialCustom.setChecked(mCliente.getEspecial());
        mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
        mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
    }


    public void showDialogNumberPicker(final String productKey) {

        {
            Log.i(LOG_TAG, "pasarOrdenEntrega TIMESTAMe1P4- " + System.currentTimeMillis());

//            SimpleDateFormat aamm = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            SimpleDateFormat aamm = new SimpleDateFormat("yyyy-MM");
            aamm.format(new Date(System.currentTimeMillis()));
            Log.i(LOG_TAG, "pasarOrdenEntrega TIMESTAMP- " + aamm.format(new Date(System.currentTimeMillis())));
//            Log.i(LOG_TAG, "pasarOrdenEntrega TIMESTAMP- " + aamm);


            final Dialog d = new Dialog(getContext());
            d.setTitle(getString(R.string.quantityPicker));
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            Log.d("Picker", "Default: " + mDetalleAnterior.getProducto().getCantidadDefault());
            Log.d("Picker", "Man: " + mDetalleAnterior.getProducto().getCantidadMaxima());
            Log.d("Picker", "Mix: " + mDetalleAnterior.getProducto().getCantidadMinima());
            Log.d("Picker", "ordenCantidad: " + mDetalleAnterior.getCantidadOrden());

            np.setMaxValue(mDetalleAnterior.getProducto().getCantidadMaxima());
            np.setMinValue(mDetalleAnterior.getProducto().getCantidadMinima());
            np.setWrapSelectorWheel(true);

            if (mCabeceraOrden.getEstado() == ORDEN_STATUS_EN_DELIVERING) {
                np.setValue(mDetalleAnterior.getCantidadOrden().intValue());
                openButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.VISIBLE);
            } else {

                if (mDetalleAnterior.getCantidadOrden() == null) {
                    np.setValue(mDetalleAnterior.getProducto().getCantidadDefault());
                } else {
                    np.setValue(mDetalleAnterior.getCantidadOrden().intValue());
                }
            }
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    if (mCabeceraOrden.getEstado() == ORDEN_STATUS_EN_DELIVERING) {
                        abmDetalleDeOrdenDelivery((double) np.getValue(), productKey, mDetalleAnterior);
//                        ca();
//                        
//                        vh.mUnidadesSolicitadas.setText(String.valueOf(np.getValue()));
//                        CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecioDelivery.getText().toString());
//                        double total = np.getValue() * price.convert();
//                        NumberFormat format = NumberFormat.getCurrencyInstance();
//                        vh.mKgSolicitados.setText(format.format(total));
//                        saveCantidad(vh.mDetalleOrderId, Integer.valueOf(np.getValue()));
//
//                        d.dismiss();

                    } else {
                        Log.d("detalle1", "showDialogNumberPicker-detalle) " + mDetalleAnterior.getCantidadOrden());
                        Log.d("detalle1", "showDialogNumberPicker-np.getValue() " + np.getValue());
                        abmDetalleDeOrden((double) np.getValue(), productKey, mDetalleAnterior);
//                        modificarCantidadDeProductoEnOrden(np.getValue(), productKey);
//                        vh.mUnidadesEnStock.setText(String.valueOf(np.getValue()));
//                        CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
//                        double total = np.getValue() * price.convert();
//                        NumberFormat format = NumberFormat.getCurrencyInstance();
//                        vh.mKgEnStock.setText(format.format(total));
//                        saveCantidad(vh.mDetalleOrderId, Integer.valueOf(np.getValue()));


                    }

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


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {


        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

        recyclerView.setAdapter(mDetalleAdapter);
    }


    public void saveTotalPrice(double totalPrice) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);

        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mItem));
        builder.withValue(CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER, totalPrice);


        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
        }


    }


    public void reportTotalesXProductoy() {

        String select[] = {
                "strftime('%Y-%m', " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER + " ) ",
                LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
                LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
                "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " ) as Qdeliver ",
                "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " ) as Qorder ",
        };


        String where =
                LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " = " + mCustomRef + " and " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL + " = 1 ";
        try {
            Cursor c = getActivity().getContentResolver().query(LogisticaProvider.reporte.CONTENT_URI,
                    select,
                    null,
                    null,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                String mes = c.getString(0) + "-";
                String cliente = c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM)) + " " + c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM));
                String producto = null;
                do {
                    if (!mes.equals(c.getString(0) + "-")) {
                        mes = c.getString(0) + "-";
                    }
                    if (!cliente.equals(
                            c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM)) + " " + c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM))
                    )) {
                        cliente = c.getString(c.getColumnIndex(CustomColumns.NAME_CUSTOM)) + " " + c.getString(c.getColumnIndex(CustomColumns.LASTNAME_CUSTOM));
                    }
                    producto = c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO));


                } while (c.moveToNext());


            } else {
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
        }
    }

    public void deleteCustomOrder() {
        // Custom orders are not deleted
    }

    public void upDateCustomer(long customerReference) {
//        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//        if (customerReference != 0) {
//            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mItem));
//            builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customerReference);
//            SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
//            String formattedDate = df.format(new Date());
//            builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
//            builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, STATUS_ORDER_INICIAL);
//            builder.withValue(CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER, 0);
//            batchOperations.add(builder.build());
//        }
//
//        try {
//            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//        } catch (RemoteException | OperationApplicationException e) {
//        } finally {
////            getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
//        }


    }

    // abmDetalleDeOrden
    // si Detalle viene con cantidad =0 se entiende que es un producto Nuevo.
    // Si cantidad es cero se entiende que se saca de la orden
    public void abmDetalleDeOrden(final Double cantidad, String productoKey, Detalle detalle) {


        Log.i(LOG_TAG, "abmDetalleDeOrden cantidad " + cantidad + " productokey " + productoKey + " Producto " + detalle.getProducto().getNombreProducto());
        if (hayTareaEnProceso()) {
            return;
        }

        mCantidadDato = cantidad; // Es la nueva cantidad que queremos tener
        mproductKeyDato = productoKey;
        mDetalleDato = detalle; // tiene los valores del detalle que se quiere modificar


        readBlockCabeceraOrden(mCabeceraOrden.getNumeroDeOrden());
        readBlockTotalInicial(productoKey);
        readBlockProductosEnOrdenes(productoKey, mCabeceraOrden.getNumeroDeOrden());
        Log.i(LOG_TAG, "mCabeceraOrdenTask.get(0) " + mCabeceraOrdenTask.get(0));
        Log.i(LOG_TAG, "mTotalInicialTask.get(0) " + mTotalInicialTask.get(0));
        Log.i(LOG_TAG, "mProductosEnOrdenesTask " + mProductosEnOrdenesTask);


        Task<Void> allTask;
        allTask = Tasks.whenAll(mCabeceraOrdenTask.get(0), mTotalInicialTask.get(0), mProductosEnOrdenesTask);
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                CabeceraOrden cabeceraOrden;
                Detalle totalInicialDetalle;
                PrductosxOrden prductosxOrden;
                Detalle nuevoDetalleOrden = mDetalleDato.copy();

                DataSnapshot dataCabecera = (DataSnapshot) mCabeceraOrdenTask.get(0).getResult();

                // do something with db data?
                if (dataCabecera.exists()) {
                    dataCabecera.getKey();
                    cabeceraOrden = dataCabecera.getValue(CabeceraOrden.class);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask =" + dataCabecera.getKey() + "- monto cab" + dataCabecera.getValue(CabeceraOrden.class).getTotales().getMontoEnOrdenes());

                } else {
                    //Debe existir, de lo contrario es un error.
                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask = NuLL- ");
                    return;
                }


                DataSnapshot dataDetalleTotalInicial = (DataSnapshot) mTotalInicialTask.get(0).getResult();

                // do something with db data?
                if (dataDetalleTotalInicial.exists()) {
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask key =" + dataDetalleTotalInicial.getKey());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetalleTotalInicial.hasChildren());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.hasChildren() =" + dataDetalleTotalInicial.getChildrenCount());
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask data.exists() =" + dataDetalleTotalInicial.exists());

                    dataDetalleTotalInicial.getKey();
                    totalInicialDetalle = dataDetalleTotalInicial.getValue(Detalle.class);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask =" + dataDetalleTotalInicial.getKey() + "- nombre Producto" + dataDetalleTotalInicial.getValue(Detalle.class).getProducto().getNombreProducto()
                            + "- cantidad Orden" + dataDetalleTotalInicial.getValue(Detalle.class).getCantidadOrden());

                } else {
                    // si no existe el producto en el listado se crea.
                    totalInicialDetalle = new Detalle(0.0, mDetalleDato.getProducto(), null);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mTotalInicialTask = NuLL- ");

                }


//                DataSnapshot dataProductosEnOrdenes = (DataSnapshot) mProductosEnOrdenesTask.getResult();
//
//
//                if (dataProductosEnOrdenes.exists()) {
//                    dataProductosEnOrdenes.getKey();
//                    prductosxOrden = dataProductosEnOrdenes.getValue(PrductosxOrden.class);
////                    data.getValue(Detalle.class);
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask key =" + dataProductosEnOrdenes.getKey());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.hasChildren() =" + dataProductosEnOrdenes.hasChildren());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.hasChildren() =" + dataProductosEnOrdenes.getChildrenCount());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask data.exists() =" + dataProductosEnOrdenes.exists());
//                    Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask NombreProd =" + dataProductosEnOrdenes.getValue(PrductosxOrden.class).getDetalle().getProducto().getNombreProducto());
//
//
//                } else {
                prductosxOrden = new PrductosxOrden(mCabeceraOrden.getCliente(), new Detalle(0.0, mDetalleDato.getProducto(), null));
                Log.i(LOG_TAG, "abmDetalleDeOrden mProductosEnOrdenesTask = NuLL- ");
//                }

                // Todo: procesar los datos
                // liberar los semaforos para grabar
                cabeceraOrden.liberar();
                nuevoDetalleOrden.liberar();
                totalInicialDetalle.liberar();
                prductosxOrden.liberar();
                if (mDetalleDato.getCantidadOrden() == 0 && mCantidadDato != 0.0)//ingresa un nuevo producto
                {
                    Log.i(LOG_TAG, "abmDetalleDeOrden ingresa un nuevo producto ");
                    //Actualizo cabecera de Orden 1B ( Ingresa producto en orden suma 1 a la cantidad de Items y ajusta el monto total
                    // Se ajusta Totales
                    cabeceraOrden.ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());

                    //Actualizo productos por Orden 5. Es necesario bloquear este esquema? o se puede planchar directamente???
                    prductosxOrden.getDetalle().ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());

                    //Actualizo Detalle de Orden para 1C y 4
                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);

                    //Actualizo Total Inicial (3).
                    totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);

                    mKeyList.add(mproductKeyDato);
                } else if (mDetalleDato.getCantidadOrden() > 0 && mCantidadDato > 0)// Se modifica una cantidad
                {
                    Log.i(LOG_TAG, "abmDetalleDeOrden Se modifica una cantidad ");

                    //Actualizo cabecera de Orden 1B ( Ingresa producto en orden suma 1 a la cantidad de Items y ajusta el monto total
                    // Se ajusta Totales
                    cabeceraOrden.modificarCantidadProductoEnOrden(mCantidadDato, mDetalleDato);

                    //Actualizo productos por Orden 5. Es necesario bloquear este esquema? o se puede planchar directamente???
                    //Idem al anterior, se plantacha
                    prductosxOrden.getDetalle().ingresaProductoEnOrden(mCantidadDato, mDetalleDato.getProducto(), mCabeceraOrden.getCliente().getEspecial());

                    //Actualizo Detalle de Orden para 1C y 4
                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);

                    //Actualizo Total Inicial (3).
                    totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);
                } else if (mCantidadDato == 0.0)// se borra un producto
                {
                    Log.i(LOG_TAG, "abmDetalleDeOrden se borra un producto ");
                    mKeyList.remove(mproductKeyDato);
                    // modifico el detalle nuevo para impactar totalInicialDetalle.
                    nuevoDetalleOrden.modificarCantidadProductoDeOrden(mCantidadDato);
                    // Actualizo Cabecera
                    cabeceraOrden.getTotales().sacarProductoDeOrden(mDetalleDato);
                    //Actualizo Total Inicial (3).
                    if (mDetalleDato.getCantidadOrden().equals(totalInicialDetalle.getCantidadOrden()))
                        totalInicialDetalle = null;
                    else {
                        totalInicialDetalle.modificarCantidadEnTotalInicial(nuevoDetalleOrden, mDetalleDato);
                    }
                    //Actualizo Detalle de Orden para 1C y 4
                    nuevoDetalleOrden = null;
                    //Actualizo productos por Orden 5
                    prductosxOrden = null;
                    Log.i(LOG_TAG, "abmDetalleDeOrden mKeyList " + mKeyList.toString());

                    Log.i(LOG_TAG, "abmDetalleDeOrden mproductKeyDato " + mproductKeyDato);
                    Log.i(LOG_TAG, "abmDetalleDeOrden mKeyList " + mKeyList.toString());

                }

                // Todo: Escribir en la Firebase simultaneament.

                mCabeceraOrden.getTotales().setCantidadDeProductosDiferentes(cabeceraOrden.getTotales().getCantidadDeProductosDiferentes());
                mCabeceraOrden.getTotales().setMontoEnOrdenes(cabeceraOrden.getTotales().getMontoEnOrdenes());


                Map<String, Object> cabeceraOrdenValues = null;
                Map<String, Object> detalleOrdenValues = null;
                Map<String, Object> totalInicialDetalleValues = null;
                Map<String, Object> prductosxOrdenValues = null;

                if (cabeceraOrden != null) {
                    cabeceraOrdenValues = cabeceraOrden.toMap();
                }
                if (nuevoDetalleOrden != null) {
                    detalleOrdenValues = nuevoDetalleOrden.toMap();
                }
                if (totalInicialDetalle != null) {
                    totalInicialDetalleValues = totalInicialDetalle.toMap();
                }
                if (prductosxOrden != null) {
                    prductosxOrdenValues = prductosxOrden.toMap();
                }

                Map<String, Object> childUpdates = new HashMap<>();


                Log.i(LOG_TAG, "abmDetalleDeOrden refCabeceraOrden_1B " + refCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()).toString());
                Log.i(LOG_TAG, "abmDetalleDeOrden refCabeceraOrden_2 " + refCabeceraOrden_2(ORDEN_STATUS_INICIAL, mCabeceraOrden.getNumeroDeOrden()).toString());
                Log.i(LOG_TAG, "abmDetalleDeOrden refDetalleOrden_1C " + refDetalleOrden_1C(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato).toString());
                Log.i(LOG_TAG, "abmDetalleDeOrden refDetalleOrden_4 " + refDetalleOrden_4(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato).toString());
                Log.i(LOG_TAG, "abmDetalleDeOrden refProductosXOrdenInicial_5 " + refProductosXOrdenInicial_5(mproductKeyDato, mCabeceraOrden.getNumeroDeOrden()).toString());
                Log.i(LOG_TAG, "abmDetalleDeOrden refTotalInicial_3 " + refTotalInicial_3(mproductKeyDato).toString());



/*1B*/
                childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);
/*2 */
                childUpdates.put(nodoCabeceraOrden_2(ORDEN_STATUS_INICIAL, mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);
/*1c*/
                childUpdates.put(nodoDetalleOrden_1C(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
/*4 */
                childUpdates.put(nodoDetalleOrden_4(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
/*5 */
                childUpdates.put(nodoProductosXOrdenInicial_5(mproductKeyDato, mCabeceraOrden.getNumeroDeOrden()), prductosxOrdenValues);
/*3 */
                childUpdates.put(nodoTotalInicial_3(mproductKeyDato), totalInicialDetalleValues);


                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren-onFailure " + e.toString());
                        liberarRecusosTomados();
                        liberarArrayTaskConBloqueos();
                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
                        mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));

                        liberarArrayTaskCasoExitoso();

                        Log.i(LOG_TAG, "abmDetalleDeOrden updateChildren - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                    }
                });


            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG, "abmDetalleDeOrden addOnFailureListener= allTask" + e.toString());
                muestraMensajeEnDialogo("No se pudo bloquear");
                liberarRecusosTomados();
                liberarArrayTaskConBloqueos();
                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR));
            }
        });


    }


    // abmDetalleDeOrden
    // cantidad tiene el nuevo valor de cantidad, detalle: los datos de la orden (1-C)
    // Se modificaran 1-C con la nueva cantidad y la cabecera de la orden ajustando cantidades y Totales en $.
    // Se actualizaron los datos en 4 (detalles de Ordenes) y 2 (Cabecera de Ordenes)
    // Se Actualiza 7. Cantidad entregada del producto para esta entrega.
    public void abmDetalleDeOrdenDelivery(final Double cantidad, final String productoKey, Detalle detalle) {
        if (hayTareaEnProceso()) {
            return;
        }
        mCantidadDato = cantidad; // Es la nueva cantidad que queremos tener
        mproductKeyDato = productoKey;
        mDetalleDato = detalle; // tiene los valores del detalle que se quiere modificar

        Log.i(LOG_TAG, "abmDetalleDeOrden Nro Pickign " + mNroPicking);
        Log.i(LOG_TAG, "abmDetalleDeOrden cantidad " + cantidad + " productokey " + productoKey + " Producto " + detalle.getProducto().getNombreProducto());

//        // leo y bloqueo Cabecera de Orden 1B
//        readBlockCabeceraOrden(mCabeceraOrden.getNumeroDeOrden());
//
//        // leo y bloqueo Detalle
//        readBlockPickingTotal(PICKING_STATUS_DELIVERY, mNroPicking, productoKey);/*7*/
//
//        Task<Void> allTask;
//        allTask = Tasks.whenAll(mCabeceraOrdenTask.get(0), mPickingTotalTask.get(0));
//        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                CabeceraOrden cabeceraOrden;
//                Detalle totalInicialDetalle;
//                Detalle nuevoDetalleOrden = mDetalleDato.copy();
//
//                DataSnapshot dataCabecera = (DataSnapshot) mCabeceraOrdenTask.get(0).getResult();
//
//                // do something with db data?
//                if (dataCabecera.exists()) {
//                    dataCabecera.getKey();
//                    cabeceraOrden = dataCabecera.getValue(CabeceraOrden.class);
//                    Log.i(LOG_TAG, "aabmDetalleDeEntrega mProductosEnOrdenesTask =" + dataCabecera.getKey() + "- monto cab" + dataCabecera.getValue(CabeceraOrden.class).getTotales().getMontoEnOrdenes());
//
//                } else {
//                    //Debe existir, de lo contrario es un error.
//                    Log.i(LOG_TAG, "abmDetalleDeEntrega mProductosEnOrdenesTask = NuLL- ");
//                    return;
//                }
//
//                // Actualizacion de totales en Picking (7)
//
//                Detalle detallePickingTotal = ((DataSnapshot) mPickingTotalTask.get(0).getResult()).getValue(Detalle.class);
//
//                if (detallePickingTotal == null) {
//                    // si es nulo se trataria de un error puesto que existe en la orden y deberia estar sumado en el total
//                    Log.i(LOG_TAG, "abmDetalleDeEntrega etallePickingTotal  Detalle = NuLL- ");
//                } else {
////                    detallePickingTotal.modificarCantidadEnTotalInicial(detalleOrdenAux, detalleOrden);
////                    if (detallePickingTotal.getCantidadUnidadesEnStock() == 0) {
////                        pickingTotalValues = null;
////                    } else {
////                        detallePickingTotal.liberar();
////                        pickingTotalValues = detallePickingTotal.toMap();
////                    }
//
//                }

                mCabeceraOrden.getTotales().modificarCantidadProductoDeEntrega(mCantidadDato, mDetalleDato);
                mDetalleAnterior.modificarCantidadProductoDeEntrega(mCantidadDato);
//                mCabeceraOrden.setTotales(cabeceraOrden.getTotales());
//                detallePickingTotal.modificarCantidadEnTotalDelivey(nuevoDetalleOrden, mDetalleDato);

//
//                cabeceraOrden.liberar();
//                nuevoDetalleOrden.liberar();
//                detallePickingTotal.liberar();


                Map<String, Object> cabeceraOrdenValues = null;
                Map<String, Object> detalleOrdenValues = null;
//                Map<String, Object> detallePickingTotalValues = null;

                if (mCabeceraOrden != null) {
                    cabeceraOrdenValues = mCabeceraOrden.toMap();
                }
                if (mDetalleAnterior != null) {
                    detalleOrdenValues = mDetalleAnterior.toMap();
                }

//                if (detallePickingTotal != null) {
//                    detallePickingTotalValues = detallePickingTotal.toMap();
//                }

                Map<String, Object> childUpdates = new HashMap<>();



/*1B*/
                childUpdates.put(nodoCabeceraOrden_1B(mCabeceraOrden.getNumeroDeOrden()), cabeceraOrdenValues);
/*2 */
                childUpdates.put(nodoCabeceraOrden_2Status(ORDEN_STATUS_EN_DELIVERING, mCabeceraOrden.getNumeroDeOrden(), mCabeceraOrden.getNumeroDePickingOrden()), cabeceraOrdenValues);
/*1c*/
                childUpdates.put(nodoDetalleOrden_1C(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
/*4 */
                childUpdates.put(nodoDetalleOrden_4(mCabeceraOrden.getNumeroDeOrden(), mproductKeyDato), detalleOrdenValues);
/*7*/
//                childUpdates.put(nodoPickingTotal_7(PICKING_STATUS_DELIVERY, mNroPicking, productoKey), detallePickingTotalValues);


        mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
        mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
                mDatabase.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i(LOG_TAG, "abmDetalleDeEntrega updateChildren-onFailure " + e.toString());
//                        liberarRecusosTomados();
//                        liberarArrayTaskConBloqueos();
//                        muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_ESCRIBIR));
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



//                        liberarArrayTaskCasoExitoso();
                        Log.i(LOG_TAG, "abmDetalleDeEntrega updateChildren - OnCompleteListener task.isSuccessful():" + task.isSuccessful());

                    }
                });
//

//            }
//        });
//        allTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i(LOG_TAG, "abmDetalleDeOrden addOnFailureListener= allTask" + e.toString());
//                liberarRecusosTomados();
//                liberarArrayTaskConBloqueos();
//                muestraMensajeEnDialogo(getResources().getString(R.string.ERROR_NO_SE_PUDO_BLOQUEAR));
//            }
//        });


    }

    public void modificarCantidadDeProductoEnOrden(final int cantidadNueva, final String productoKey) {

        Log.d("detalle1", "modificarCantidadDeProductoEnOrden-cantidadNueva) " + cantidadNueva);
        Log.d("detalle1", "modificarCantidadDeProductoEnOrden -detalleanterior) " + mDetalleAnterior.getCantidadOrden());
//1b -Bloqueo toda la orden y puedo modificar toda sus dependencias.  Pues modifico mientra tengo tomada la orden.
        mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).child("cabecera").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                CabeceraOrden cabeceraOrden1b = mutableData.getValue(CabeceraOrden.class);
                if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden cabeceraOrden1b-null, es un error");
                    return Transaction.success(mutableData);
                } else {
                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden 1c-NOT null");
                    Log.d("detalle1", "antes saveDetalle-detalleAnterior) " + mDetalleAnterior.getCantidadOrden());
                    Log.d("detalle1", "antes saveDetalle-cantidadNUeva) " + cantidadNueva);
/*3*/               //Se copia para evitar el traslado que produce pasar final

                    saveDetalleInicialTotales(cantidadNueva * 1.0, productoKey);// en este caso se pasa detalle anterior que tiene los valore de cantidades antes de la modificacion


//                    detalle.ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
                    mCabeceraOrden.getTotales().modificarCantidadProductoDeOrden(1.0 * cantidadNueva,
                            mDetalleAnterior);
                    Detalle detalle = new Detalle();
                    detalle = mDetalleAnterior.copy();
                    detalle.modificarCantidadProductoDeOrden(cantidadNueva * 1.0);
//                    mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());

/*5*/
                    saveOrdenProductoXCliente(productoKey, detalle);

                    Map<String, Object> cabeceraOrdenValues = mCabeceraOrden.toMap();
                    Map<String, Object> detalleOrdenValues = detalle.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
/*1c*/
                    childUpdates.put(NODO_ORDENES + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
/*4 */
                    childUpdates.put(NODO_ORDENES_DETALLE + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, detalleOrdenValues);
/*2 */
                    childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" + ORDEN_STATUS_INICIAL + "/" + mCabeceraOrden.getNumeroDeOrden(), cabeceraOrdenValues);

                    mDatabase.updateChildren(childUpdates);

                    Log.i("ClienteViewHolder", "saveCustomOrderProductproductoKey" + productoKey);


                }


                // Set value and report transaction success
                mutableData.setValue(mCabeceraOrden);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete:  databaseError" + databaseError);
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: boolean b" + commited);
                CabeceraOrden cabecera_orden = dataSnapshot.getValue(CabeceraOrden.class);

                long numeroOrden = cabecera_orden.getNumeroDeOrden();
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: ID " + numeroOrden);
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: Monto Orden " + cabecera_orden.getTotales().getMontoEnOrdenes());

                mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
                NumberFormat format = NumberFormat.getCurrencyInstance();
                mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
                mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
                mDetalleAdapter.notifyDataSetChanged();
            }

        });
    }

    public void borrarProductoDeOrden(final String productoKey, final Detalle detalle) {

//        final int cantidadNueva=0;
        Log.d("detalle1", "modificarCantidadDeProductoEnOrden-detalle) " + detalle.getCantidadOrden());
        Log.d("detalle1", "modificarCantidadDeProductoEnOrden -detalleanterior) " + detalle.getCantidadOrden());
//1b -Bloqueo toda la orden y puedo modificar toda sus dependencias.  Pues modifico mientra tengo tomada la orden.
        mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).child("cabecera").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                CabeceraOrden cabeceraOrden1b = mutableData.getValue(CabeceraOrden.class);
                if (cabeceraOrden1b == null) { // seria un error puesto que la cabecera se genera al conseguir el Nuevo Nro.
                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden cabeceraOrden1b-null, es un error");
                    return Transaction.success(mutableData);
                } else {
                    Log.d(LOG_TAG, "orden:abmDetalleDeOrden 1c-NOT null");
                    Log.d("detalle1", "antes saveDetalle-detalleAnterior) " + detalle.getCantidadOrden());
//                    Log.d("detalle1", "antes saveDetalle-cantidadNUeva) " + cantidadNueva);
/*3*/
                    saveDetalleInicialTotales(0 * 1.0, productoKey);// en este caso se pasa detalle anterior que tiene los valore de cantidades antes de la modificacion
/*5*/
//                    saveOrdenProductoXCliente(productoKey, detalle);
                    // borrar de este esquema el dato para esta orden.
                    mDatabase.child(ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL).child(mEmpresaKey).child(productoKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).removeValue();

//                    detalle.ingresaProductoEnOrden(cantidad, producto, mCliente.getEspecial());
                    mCabeceraOrden.getTotales().sacarProductoDeOrden(detalle);
//                    detalle.modificarCantidadProductoDeOrden(cantidadNueva * 1.0);
//                    mMontoTotal.setText(mCabeceraOrden.getTotales().getMontoEnOrdenes().toString());

                    Map<String, Object> cabeceraOrdenValues = mCabeceraOrden.toMap();
//                    Map<String, Object> detalleOrdenValues = detalle.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
/*1c*/
                    childUpdates.put(NODO_ORDENES + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, null);
/*4 */
                    childUpdates.put(NODO_ORDENES_DETALLE + mEmpresaKey + "/" + mCabeceraOrden.getNumeroDeOrden() + "/" + productoKey, null);
/*2 */
                    childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" + ORDEN_STATUS_INICIAL + "/" + mCabeceraOrden.getNumeroDeOrden(), cabeceraOrdenValues);

                    mDatabase.updateChildren(childUpdates);

                    Log.i("ClienteViewHolder", "saveCustomOrderProductproductoKey" + productoKey);

//                    mKeyList.remove(productoKey);


                }


                // Set value and report transaction success
                mutableData.setValue(mCabeceraOrden);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed

                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete:  databaseError" + databaseError);
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: boolean b" + commited);
                CabeceraOrden cabecera_orden = dataSnapshot.getValue(CabeceraOrden.class);

                long numeroOrden = cabecera_orden.getNumeroDeOrden();
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: ID " + numeroOrden);
                Log.d(LOG_TAG, "orden:saveCustomOrderProductonComplete: Monto Orden " + cabecera_orden.getTotales().getMontoEnOrdenes());

                mCantidadTotal.setText("Items: " + String.valueOf(mCabeceraOrden.getTotales().getCantidadDeProductosDiferentes()));
                NumberFormat format = NumberFormat.getCurrencyInstance();
                mMontoTotal.setText("Monto Orden" + format.format(mCabeceraOrden.getTotales().getMontoEnOrdenes()));
                mMontoTotalDelivey.setText("Monto Entregado" + format.format(mCabeceraOrden.getTotales().getMontoEntregado()));
                mDetalleAdapter.notifyDataSetChanged();
            }
        });
    }

    public void saveDetalleInicialTotales(final Double cantidadNueva, final String productoKey) {

//        final Double cantidadAnterior = detalle.getCantidadUnidadesEnStock();
//        final Producto producto=detalle.getProducto();
//
//        Log.d("detalle1", "saveDetalleInicialTotales-detalleAnterior) " + cantidadAnterior);
//        Log.d("detalle1", "saveDetalleInicialTotales-cantidadNUeva) " + cantidadNueva);
/*3*/
        refTotalInicial_3(productoKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                Detalle detalle3 = mutableData.getValue(Detalle.class);

                if (detalle3 == null) {
                    detalle3 = new Detalle(0.0, mDetalleAnterior.getProducto(), null);
                    detalle3.modificarCantidadTotalDeOrden(cantidadNueva, mDetalleAnterior);
                    Log.d("detalle1", "saveDetalleInicialTotales NULL -detalleAnteriorvar) " + mDetalleAnterior.getCantidadOrden());
                    Log.d("detalle1", "saveDetalleInicialTotales NULL -cantidadNUeva) " + cantidadNueva);
                    Log.i(LOG_TAG, "orden:SaveDetalleInicialTotales detalle NuLL- ");
                } else {
                    Log.d("detalle1", "saveDetalleInicialTotales Not NULL -detalleAnteriorvar) " + mDetalleAnterior.getCantidadOrden());
                    Log.d("detalle1", "saveDetalleInicialTotales Not NULL -cantidadNUeva) " + cantidadNueva);
                    // update de totales con cantidades anteriores.
                    detalle3.modificarCantidadTotalDeOrden(cantidadNueva, mDetalleAnterior);
                    if (detalle3.getCantidadOrden() == 0) {
                        detalle3 = null;
                    }

                }

                // Set value and report transaction success
                mutableData.setValue(detalle3);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "orden:SaveDetalleInicialTotales:onComplete:  databaseError" + databaseError);
                Log.d(LOG_TAG, "orden:SaveDetalleInicialTotales: boolean b" + commited);
                Detalle detalle = dataSnapshot.getValue(Detalle.class);
                Log.d(LOG_TAG, "orden:SaveDetalleInicialTotales:onComplete: detalle.getCantidadUnidadesEnStock() " + detalle.getCantidadOrden());

            }
        });
    }

    public void saveOrdenProductoXCliente(final String productoKey, final Detalle detalle) {


        mDatabase.child(ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL).child(mEmpresaKey).child(productoKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden())).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Bloqueo la orden para modificaciones,
                // Actualizo  el esquema y luego lo libero.

                PrductosxOrden detallexOrden = mutableData.getValue(PrductosxOrden.class);

                if (detallexOrden == null) {
                    detallexOrden = new PrductosxOrden(mCliente, detalle);
                    Log.i(LOG_TAG, "orden:SaveDetalleInicialTotales detalle NuLL- ");
                } else {
                    // update de totales con cantidades anteriores.
                    detallexOrden.setCliente(mCliente);
                    detallexOrden.setDetalle(detalle);
                    Log.i(LOG_TAG, "orden:SaveDetalleInicialTotales detalle Not Null ");
                }

                // Set value and report transaction success
                mutableData.setValue(detallexOrden);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "orden:SaveDetallexOrden:onComplete:  databaseError" + databaseError);
                Log.d(LOG_TAG, "orden:SaveDetallexOrden: boolean b" + commited);
                PrductosxOrden detalle = dataSnapshot.getValue(PrductosxOrden.class);
                Log.d(LOG_TAG, "orden:SaveDetallexOrden:onComplete: detalle.getCantidadUnidadesEnStock() " + detalle.getDetalle().getCantidadOrden());

            }
        });
    }

    public void saveCantidad(long id, int cantidad) {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {

            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
            if (mAction == CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY) {
                builder.withValue(CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL, cantidad);
            } else {

                builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, cantidad);
            }
            batchOperations.add(builder.build());
        }
        try {
            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
//            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
        }

    }


    public void saveCantidadDelivey(long id, int cantidad) {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, cantidad);

            batchOperations.add(builder.build());
        }
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//            getLoaderManager().initLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
        }

    }

    public void saveFavorito(long id, boolean favorito) {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {
            int myInt = (favorito) ? 1 : 0;
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
            builder.withValue(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, myInt);
            batchOperations.add(builder.build());
        }
        try {
            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
        }

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
                        sendButton.setBackgroundColor(Color.BLUE);
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

//            msg = "^LL600";
            msg = "^LL700";
            mmOutputStream.write(msg.getBytes());


            // FO x,y- x: margen derecho, y: distancia al origen.

            //  ADN:alto de letra,ancho de letra (letra horizontal- normal)
            //  ADR:alto de letra,ancho de letra (letra vertical-Mira al Margen )
            //  ADI:alto de letra,ancho de letra (letra horizontal - Invertida)
            //  ADB:alto de letra,ancho de letra (letra vertical-Mira al centro de la etiqueta)

            int h = 50;
            int i = 30;


//            SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(new Date());


            msg = "^FO310," + h + "^ADN,24,10^FD" + "feecha:" + formattedDate + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i + i;

            msg = "^FO5," + h + "^ADN,36,20^FD" + "Orden Nro: " + mCabeceraOrden.getNumeroDeOrden() + "^FS";
            mmOutputStream.write(msg.getBytes());



            h = h + i + i;
            msg = "^FO5," + h + "^ADN,36,20^FD" + mCabeceraOrden.getCliente().getNombre() + " " + mCabeceraOrden.getCliente().getApellido() + "^FS";
            mmOutputStream.write(msg.getBytes());


            h = h + i + i;
            msg = "^FO5," + h + "^ADN,24,15^FD" + mCabeceraOrden.getCliente().getCuit() + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i + i;
            msg = "^FO5," + h + "^ADN,24,10^FD" + mCabeceraOrden.getCliente().getDireccionDeEntrega() + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i - 5;
            msg = "^FO5," + h + "^ADN,24,10^FD" + mCabeceraOrden.getCliente().getCiudad() + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i;

            msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + "Producto" + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + "Cantidad" + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + "Precio" + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO430," + (h + i) + "^ADN,24,10^FD" + "Total" + "^FS";
            mmOutputStream.write(msg.getBytes());

            h = h + i;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            Double totalOrden = 0.0;


            for (DataSnapshot data : mDataSanpshotPrinting.getChildren()) {

                Log.i(LOG_TAG, "printing product Key- " + data.getKey());
                String productKey = data.getKey();
                Detalle detalleProducto = data.getValue(Detalle.class);


                String name = detalleProducto.getProducto().getNombreProducto();
                Double cantidad = detalleProducto.getCantidadEntrega();
                Double precio = detalleProducto.getPrecio();
                ;
                Double total = precio * cantidad;
                totalOrden = totalOrden + total;


                msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + name + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + cantidad + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO310," + (h + i) + "^ADN,24,10^FD" + format.format(precio) + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO430," + (h + i) + "^ADN,24,10^FD" + format.format(total) + "^FS";
                mmOutputStream.write(msg.getBytes());


                h = h + i;
            }


            h = h + i * 3;

            if (mIvaCalculo > 0) {
                msg = "^FO5," + h + "^ADN,36,20^FD" + "SubTotal: " + "^FS";
                mmOutputStream.write(msg.getBytes());


                msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(totalOrden) + "^FS";
                mmOutputStream.write(msg.getBytes());

                h = h + i + i;

                msg = "^FO5," + h + "^ADN,36,20^FD" + "Iva: " + "^FS";
                mmOutputStream.write(msg.getBytes());

                msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(totalOrden * mIvaCalculo / 100) + "^FS";
                mmOutputStream.write(msg.getBytes());

                h = h + i + i;

            }
//            h = h - i;
            msg = "^FO5," + h + "^ADN,36,20^FD" + "Total: " + "^FS";
            mmOutputStream.write(msg.getBytes());

            msg = "^FO310," + h + "^ADN,36,20^FD" + format.format(totalOrden * (1 + mIvaCalculo / 100)) + "^FS";
            mmOutputStream.write(msg.getBytes());
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
//            myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Log.d(LOG_TAG, "favorito getQuery Cantidad de mEmpresaKey: " + mEmpresaKey);
        Log.d(LOG_TAG, "favorito getQuerymCabeceraOrden.getNumeroDeOrden(): " + mCabeceraOrden.getNumeroDeOrden());
        return databaseReference.child(ESQUEMA_ORDENES_DETALLE).child(mEmpresaKey).child(String.valueOf(mCabeceraOrden.getNumeroDeOrden()));
    }


}
