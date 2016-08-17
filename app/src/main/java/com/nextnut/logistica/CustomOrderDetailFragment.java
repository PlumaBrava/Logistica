package com.nextnut.logistica;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.nextnut.logistica.Util.BoolIntConverter;
import com.nextnut.logistica.Util.CurrencyToDouble;
import com.nextnut.logistica.Util.ProductSectionActivity;
import com.nextnut.logistica.Util.YesNoDialog;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.OrderDetailCursorAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * A fragment representing a single CustomOrder detail screen.
 * This fragment is either contained in a {@link CustomOrderListFragment}
 * in two-pane mode (on tablets) or a {@link CustomOrderDetailActivity}
 * on handsets.
 */
public class CustomOrderDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    private static final String LOG_TAG = CustomOrderDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private long mItem;
    private long mCustomRef=0;
    private long mIdDetailCustomOrder_for_favorite=0;
    private CheckBox mCheckBox_for_favorite;
    private OrderDetailCursorAdapter.ViewHolder mvh_for_favorite;
    private Cursor c_favorite;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public static final String ARG_ITEM_ID = "item_id";
    public static final String CUSTOM_ORDER_ACTION = "custom_order_action";

    private static final int CUSTOM_LOADER = 0;
    private static final int CUSTOM_ORDER_LOADER = 1;
    private static final int PRODUCTS_LOADER = 2;
    private static final int TOTALES_LOADER = 3;
    private static final int CUSTOM_LOADER_NEW = 4;






    private TextView mCustomId;
//    private Spinner mSpinner;
//    CustomAdapter mSpinnerAdapter;
    private TextView mCustomName;
    private TextView mLastName;
    private TextView mDeliveyAddress;
    private TextView mCity;
    private TextView mCuit;
    private TextView mIva;
    private Double mIvaCalculo;
    public TextView mCantidadTotal;
    public TextView mMontoTotal;
    public TextView mMontoTotalDelivey;

    String mCurrentPhotoPath = null;
    private Integer mIsSpecialCustom;


    private ImageView mImageCustomer;
    private Button mBotonSeleccionCliente;
    private Button mBotonSeleccionProduto;

    private int mAction;


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_IMAGE_GET = 1889;
    private static final int REQUEST_CUSTOMER = 1234;
    private static final int REQUEST_PRODUCT = 12345;

    public static final int CUSTOM_ORDER_NEW = 0;
    public static final int CUSTOM_ORDER_DOUBLE_SCREEN = 1;
    public static final int CUSTOM_ORDER_SAVE = 2;
    public static final int CUSTOM_ORDER_SELECTION = 3;
    public static final int ACTION_CUSTOM_ORDER_DELIVERY= 104;

    public static final int STATUS_ORDER_INICIAL = 0;
    public static final int STATUS_ORDER_PICKING = 1;
    public static final int STATUS_ORDER_DELIVEY = 2;
    public static final int STATUS_ORDER_DELIVED = 3;

    CollapsingToolbarLayout appBarLayout;

    RecyclerView mRecyclerView;
    OrderDetailCursorAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;



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

    Button openButton ;
    Button sendButton ;

    Cursor mCursorTotales;

    public CustomOrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = getArguments().getLong(ARG_ITEM_ID,-1);
            Log.i(LOG_TAG, "ARG_ITEM_ID: "+mItem);
        }
            mAction= getArguments().getInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION,CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                if(mAction==CustomOrderDetailFragment.CUSTOM_ORDER_NEW){
                appBarLayout.setTitle(getResources().getString(R.string.title_New_Order));
                }
            }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (mAction) {
            case CUSTOM_ORDER_NEW:
                getLoaderManager().initLoader(CUSTOM_LOADER_NEW, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
//                getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-CUSTOM_LOADER_NEW");
                break;

            case CUSTOM_ORDER_DOUBLE_SCREEN:
                if (mItem == 0) {
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-default DETAIL_PRODUCT_LOADER");
                    getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                } else {
//                    getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-DETAIL_PRODUCT_LOADER");
                }
                break;
            case ACTION_CUSTOM_ORDER_DELIVERY:
                getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
//                getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_SELECTION");
                break;

            case CUSTOM_ORDER_SELECTION:
                getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
//                getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_SELECTION");
            default:
                break;
        }

//        getLoaderManager().initLoader(NAME_PRODUCT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        Log.e(LOG_TAG, "onPause");
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(LOG_TAG, "onStop");
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customorder_detail, container, false);

        openButton = (Button) rootView.findViewById(R.id.open);
        sendButton = (Button) rootView.findViewById(R.id.send);
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
                try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mBotonSeleccionCliente = (Button) rootView.findViewById(R.id.botonSelecionCliente);
        mBotonSeleccionCliente.setVisibility(View.VISIBLE);

        mBotonSeleccionProduto = (Button) rootView.findViewById(R.id.botonSelecionProdcuto);
        mBotonSeleccionProduto.setVisibility(View.VISIBLE);

        mCustomId = (TextView) rootView.findViewById(R.id.custom_Id);
        mCustomName = (TextView) rootView.findViewById(R.id.custom_name_text);
        mLastName = (TextView) rootView.findViewById(R.id.product_Lastname);
//        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
        mImageCustomer = (ImageView) rootView.findViewById(R.id.custom_imagen);
        mDeliveyAddress = (TextView) rootView.findViewById(R.id.custom_delivery_address);
        mCity = (TextView) rootView.findViewById(R.id.custom_city);
        mCuit = (TextView) rootView.findViewById(R.id.CUIT);
        mIva = (TextView) rootView.findViewById(R.id.IVA);


        mCantidadTotal=(TextView) rootView.findViewById(R.id.cantidadTotal);;
        mMontoTotal=(TextView) rootView.findViewById(R.id.montoToal);;
        mMontoTotalDelivey=(TextView) rootView.findViewById(R.id.montoToalDelivery);;


        mBotonSeleccionCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CustomSelectionActivity.class);
                startActivityForResult(intent, REQUEST_CUSTOMER);

//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    DialogoSeleccionCliente dialogo = new DialogoSeleccionCliente();
//                    dialogo.monOptionSelected

//                    dialogo.show(fragmentManager, "tagAlerta");


            }

        });



//        if (mAction==CustomOrderDetailFragment.CUSTOM_ORDER_NEW){
//                mBotonSeleccionProduto.setVisibility(View.GONE);}
//        else {  mBotonSeleccionProduto.setVisibility(View.VISIBLE);}


        mBotonSeleccionProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ProductSectionActivity.class);
                intent.putExtra("ITEM",mItem);
                startActivityForResult(intent, REQUEST_PRODUCT);

            }

        });

        View emptyView = rootView.findViewById(R.id.recyclerview_product_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.product_list_customOrder);
//        mAdapter = new Adapter(null,emptyView);
        mAdapter = new OrderDetailCursorAdapter(getContext(), null, emptyView, new OrderDetailCursorAdapter.ProductCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                Log.i(LOG_TAG, "setupRecyclerView" + id);
            showDialogNumberPicker( vh);
            }

            @Override
            public void onFavorite(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                Log.i(LOG_TAG, "onFavorite55 " + id);
                mvh_for_favorite=vh;
                String where =
                        LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER +" = " +vh.mRefCustomer + " and " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL +" = " +vh.mRefProduct + " and " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL +" = 1 ";

                c_favorite = getActivity().getContentResolver().query( LogisticaProvider.join_customorderDetail_Product_Customer.CONTENT_URI,
                        null,
                        where,
                        null,
                        null,null);


                 if (c_favorite.getCount()>=1){
                     Log.i(LOG_TAG, "ya existe onFavorite" + id+ "cantidad: "+c_favorite.getCount()+ "refCustomer: "+vh.mRefCustomer+ " refProducto: "+vh.mRefProduct);
;
                     mIdDetailCustomOrder_for_favorite=vh.mDetalleOrderId;
                     mCheckBox_for_favorite=vh.mfavorito;
                     AlertDialog.Builder alert = new AlertDialog.Builder((Activity)getContext());
                     alert.setTitle("Ya existe Favorito");
                     alert.setMessage("Quiere cambiar de Favorito");
                     alert.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int whichButton) {
                             Log.i("YesNoDialog:", "setNegativeButton" );
                             mCheckBox_for_favorite.setChecked(mCheckBox_for_favorite.isChecked()?false:true);
                             dialog.cancel();
                         }
                     });
                     alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int whichButton) {
                             Log.i("YesNoDialog:", "setPositiveButton " );

                             if (c_favorite!=null && c_favorite.getCount()>0 ) {
                                 c_favorite.moveToFirst();
                                 ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(c_favorite.getCount());

                                 do {
                                     ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(c_favorite.getLong(0)));


                                     builder.withValue(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, false);
                                     batchOperations.add(builder.build());

                                     Log.i(LOG_TAG, "onf - ID: " + c_favorite.getLong(0));
                                     Log.i(LOG_TAG, "onf - REF_CUSTOM: " + c_favorite.getLong(1));
                                     Log.i(LOG_TAG, "onf - REF_PRODUCT: " + c_favorite.getString(2));
                                     Log.i(LOG_TAG, "onf - PRODUCT_NAME: " + c_favorite.getString(3));
                                     Log.i(LOG_TAG, "onf - QUANTITY: " + c_favorite.getString(4));
                                     Log.i(LOG_TAG, "onf - PRICE_CUSTOM: " + c_favorite.getDouble(5));
                                     Log.i(LOG_TAG, "onf - FAVORITE_CUSTOM: " + c_favorite.getLong(6));
                                 } while (c_favorite.moveToNext());
                             try {

                                 getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);

                             } catch (RemoteException | OperationApplicationException e) {
                                 Log.e(LOG_TAG, "Error applying batch insert", e);
                             }


                             }


                             ContentValues upDateValues = new ContentValues();
                             upDateValues.put(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, new BoolIntConverter().boolToInt(mCheckBox_for_favorite.isChecked()));
                             getContext().getContentResolver().update(LogisticaProvider.CustomOrdersDetail.withId(mIdDetailCustomOrder_for_favorite),
                                     upDateValues, null, null);

                         }
                     });
                     alert.create().show();




                 }else{

                     Log.i(LOG_TAG, "noo existe onFavorite" + id+ "cantidad: "+c_favorite.getCount()+ "refCustomer: "+vh.mRefCustomer+ " refProducto: "+vh.mRefProduct);
                    ContentValues upDateValues = new ContentValues();
                    upDateValues.put(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, new BoolIntConverter().boolToInt(vh.mfavorito.isChecked()));
                    getContext().getContentResolver().update(LogisticaProvider.CustomOrdersDetail.withId(vh.mDetalleOrderId),
                    upDateValues, null, null);
                 }
            }

            @Override
            public void onProductDismiss(long id) {
                Log.i(LOG_TAG, "onProductDismiss, ID:"+id);



                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);



                    ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withId(id));

                try {

                    batchOperations.add(builder.build());
                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);



                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(LOG_TAG, "Error applying batch insert", e);
                }finally {
                    getLoaderManager().restartLoader(PRODUCTS_LOADER, null, CustomOrderDetailFragment.this);
                    getLoaderManager().restartLoader(TOTALES_LOADER, null, CustomOrderDetailFragment.this);
                    mAdapter.notifyDataSetChanged();
                }





            }

        });
        if(mAction==CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY){
            mAdapter.setDeliveryState();
        }

        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        return rootView;
    }

    public void showDialogNumberPicker(final OrderDetailCursorAdapter.ViewHolder vh){

        {

            final Dialog d = new Dialog(getContext());
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(5000);
            np.setValue(Integer.valueOf(vh.mTextcantidad.getText().toString()));
            np.setMinValue(1);
            np.setWrapSelectorWheel(true);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Log.i("value is",""+newVal);
                }
            });
            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if(mAction==CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY){
                        vh.mTextcantidadDelivery.setText(String.valueOf(np.getValue()));
                        CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecioDelivery.getText().toString());
                        double total = np.getValue() * price.convert();
                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        vh.mTextToalDelivery.setText(format.format(total));
                        saveCantidad(vh.mDetalleOrderId, Integer.valueOf(np.getValue()));
                        d.dismiss();

                    }else {
                        vh.mTextcantidad.setText(String.valueOf(np.getValue()));
                        CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
                        double total = np.getValue() * price.convert();
                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        vh.mTextToal.setText(format.format(total));
                        saveCantidad(vh.mDetalleOrderId, Integer.valueOf(np.getValue()));
                        d.dismiss();
                    }
                }
            });
            b2.setOnClickListener(new  View.OnClickListener()
            {
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

        recyclerView.setAdapter(mAdapter);
    }

    public static final int RESULT_OK           = -1;
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        Log.i(LOG_TAG, "LLego resyktado ok" );
        if (requestCode == REQUEST_CUSTOMER && resultCode == RESULT_OK) {
            String res = data.getExtras().getString("resultado");
            Log.i(LOG_TAG, "LLego resyktado ok"+ res );
            mCustomRef=data.getExtras().getLong("resultado");
            getLoaderManager().restartLoader(CUSTOM_LOADER, null, this);
        }
        if (requestCode == REQUEST_PRODUCT && resultCode == RESULT_OK) {
            String res = data.getExtras().getString("resultado");
            Log.i(LOG_TAG, "REQUEST_PRODUCT "+ data.getExtras().getString("ProductoName") );
//            data.getExtras().getLong("resultado");
            Log.i(LOG_TAG, "REQUEST_PRODUCT ProductPrice"+  data.getExtras().getString("ProductPrice") );
            saveCustomOrderProduct(data.getExtras().getLong(ProductSectionActivity.KEY_RefPRODUCTO),
                    data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_NAME),
                        mIsSpecialCustom>0? data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICES_ESPECIAL): data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICE)

            );

        }

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      switch(id) {

            case CUSTOM_LOADER:

            return new CursorLoader(

                    getActivity(),
                    LogisticaProvider.Customs.withId(mCustomRef),
                    null,
                    null,
                    null,
                    null);

            case CUSTOM_ORDER_LOADER:


    /*0*/            String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER ,
    /*1*/                    LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
    /*2*/                    LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
    /*3*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
    /*4*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM,
    /*5*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIIVERY_ADDRES_CUSTOM,
    /*6*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIVERY_CITY_CUSTOM,
    /*7*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.IMAGEN_CUSTOM,
    /*8*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.SPECIAL_CUSTOM,
    /*9*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.CUIT_CUSTOM,
    /*10*/                    LogisticaDataBase.CUSTOMS+"."+ CustomColumns.IVA_CUSTOM
                };



                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.ShowJoin.CONTENT_URI,
                        proyection,
                        LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER +"="+mItem,
                        null,
                        null);

          case CUSTOM_LOADER_NEW:


/* 0 */              String proyection1[] = {LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER ,
/* 1 */                      LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
/* 2 */                      LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
/* 3 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
/* 4 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM,
/* 5 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIIVERY_ADDRES_CUSTOM,
/* 6 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIVERY_CITY_CUSTOM,
/* 7 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.IMAGEN_CUSTOM,
/* 8 */                      LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER,
/* 9 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.SPECIAL_CUSTOM,
/* 10 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.CUIT_CUSTOM,
/* 11 */                      LogisticaDataBase.CUSTOMS+"."+ CustomColumns.IVA_CUSTOM,
              };



              return new CursorLoader(
                      getActivity(),
                      LogisticaProvider.ShowJoin.CONTENT_URI,
                      proyection1,
                      null,
                      null,
                      null);


          case PRODUCTS_LOADER:

              String proyection2[] = {
       /* 0 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL ,
       /* 1 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL ,
       /* 2 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL ,
       /* 3 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL ,
       /* 4 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL ,
       /* 5 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL ,
       /* 6 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL ,
       /* 7 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.IMAGEN_PRODUCTO ,
       /* 8 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.DESCRIPCION_PRODUCTO,
       /* 9 */                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
       /* 10 */               LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL ,


              };

              return new CursorLoader(
                      getActivity(),
//                      LogisticaProvider.joinCustomOrder_Product.CONTENT_URI,
                      LogisticaProvider.join_Product_Detail_order.CONTENT_URI,
                      proyection2,
                      LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +"="+mItem,
                      null,
                      null);

          case TOTALES_LOADER:

                    String proyection3[] = {
        /* 0 */   "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " * "+
                           LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL + " ) ",

       /* 1 */    "count("  +     LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL+" )"  ,
      /* 2 */   "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " * "+
                            LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL + " ) "

                    };

              return new CursorLoader(

                      getActivity(),
                      LogisticaProvider.CustomOrdersDetail.CONTENT_URI,
                      proyection3,
                      LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +"="+mItem,
                      null,
                      null);

            default:
                return null;

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch(loader.getId()) {

            case CUSTOM_LOADER:

                if (data != null && data.moveToFirst()) {

                    mBotonSeleccionCliente.setVisibility(View.VISIBLE);
                    mCustomName.setText(data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)));
                    mLastName.setText(data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
                    mDeliveyAddress.setText(data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)));
                    mCity.setText(data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
                    mCurrentPhotoPath = data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM));
                    mIsSpecialCustom=data.getInt(data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM));
                    Log.i(LOG_TAG, "CUSTOM_LOADER- mIsSpecialCustom: " + mIsSpecialCustom);
                    Picasso.with(getContext())
                            .load(mCurrentPhotoPath)
                            .into(mImageCustomer);
                    mCuit.setText(data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)));
                    mIva.setText(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM)));
                    mIvaCalculo=data.getDouble(data.getColumnIndex(CustomColumns.IVA_CUSTOM));

                }

                break;
            case CUSTOM_ORDER_LOADER:
                if (data != null && data.moveToFirst()) {


                    mBotonSeleccionCliente.setVisibility(View.VISIBLE);
                    mCustomRef=data.getLong(0);
                    mCustomName.setText(data.getString(3));
                    mLastName.setText(data.getString(4));
                    mDeliveyAddress.setText(data.getString(5));
                    mCity.setText(data.getString(6));
                    mCurrentPhotoPath = data.getString(7);
                    mIsSpecialCustom=data.getInt(8);
                    Log.i(LOG_TAG, "CUSTOM_ORDER_LOADER- mIsSpecialCustom: " + mIsSpecialCustom);
                    if (appBarLayout != null) {

//                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + data.getString(0));
                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + mItem);

                    }
                    mCuit.setText(data.getString(9));
                    mIva.setText(data.getString(10));
                    mIvaCalculo=data.getDouble(data.getColumnIndex(CustomColumns.IVA_CUSTOM));
                    getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
                }
                break;

            case CUSTOM_LOADER_NEW:
                if (data != null && data.moveToLast()) {

                    mItem=data.getLong(8);
                    mBotonSeleccionCliente.setVisibility(View.VISIBLE);
                    mCustomRef = data.getLong(0);
                    mCustomName.setText(data.getString(3));
                    mLastName.setText(data.getString(4));
                    mDeliveyAddress.setText(data.getString(5));
                    mCity.setText(data.getString(6));
                    mCurrentPhotoPath = data.getString(7);
                    mIsSpecialCustom=data.getInt(9);
                    Log.i(LOG_TAG, "CUSTOM_LOADER_NEW- mIsSpecialCustom: " + mIsSpecialCustom);

                    mCuit.setText(data.getString(10));
                    mIva.setText(data.getString(11));
                    mIvaCalculo=data.getDouble(data.getColumnIndex(CustomColumns.IVA_CUSTOM));

                    if (appBarLayout != null) {

                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + data.getLong(8));
//                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + mItem);

                    }

                    String where =
                            LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " = " + mCustomRef + " and " +
                                    LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL + " = 1 ";

                    Cursor c = getActivity().getContentResolver().query(LogisticaProvider.join_customorderDetail_Product_Customer.CONTENT_URI,
                            null,
                            where,
                            null,
                            null, null);


                    if (c != null && c.getCount() > 0) {
                        Log.i(LOG_TAG, "onf - count: " + c.getCount());
                        c.moveToFirst();
                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(c.getCount());

                        do {

                            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrdersDetail.CONTENT_URI);
                            builder.withValue(CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL, data.getLong(8));

//                            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrdersDetail.withRefCustomOrder(data.getLong(8)));
                            builder.withValue(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL, c.getLong(c.getColumnIndex(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL)));
                            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, c.getLong(c.getColumnIndex(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL)));
                            builder.withValue(CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL, c.getDouble(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
                            builder.withValue(CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL, c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
                            batchOperations.add(builder.build());

//                            Log.i(LOG_TAG, "onf - ID: " + c.getLong(0));
//                            Log.i(LOG_TAG, "onf - REF_CUSTOM: " + c.getLong(1));
                            Log.i(LOG_TAG, "onf - REF_PRODUCT: " + c.getLong(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
                            Log.i(LOG_TAG, "onf - PRODUCT_NAME: " + c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
                            Log.i(LOG_TAG, "onf - QUANTITY: " + c.getLong(c.getColumnIndex(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL)));
//                            Log.i(LOG_TAG, "onf - PRICE_CUSTOM: " + c.getDouble(5));
//                            Log.i(LOG_TAG, "onf - FAVORITE_CUSTOM: " + c.getLong(6));
                        } while (c.moveToNext());
                        try {


                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);



                        } catch (RemoteException | OperationApplicationException e) {
                            Log.e(LOG_TAG, "Error applying batch insert", e);
                        }finally {
//                            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);

                            mAdapter.notifyDataSetChanged();
                        }

                        getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
                    }
                }
                break;

            case PRODUCTS_LOADER:
                if (data != null && data.moveToFirst()) {

                    mCursorTotales=data;
//                    do {
//
//                        Log.i(LOG_TAG, "PRODUCTS_LOADER - nombre"+ data.getString(6) );
//                        Log.i(LOG_TAG, "PRODUCTS_LOADER - precio"+ data.getString(4) );
//                        Log.i(LOG_TAG, "PRODUCTS_LOADER - descrition"+ data.getString(8) );
//                    }while (data.moveToNext());
                }
                mAdapter.swapCursor(data);
                break;

            case TOTALES_LOADER:
                if (data != null && data.moveToFirst()) {
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    mCantidadTotal.setText("cantidad: "+Integer.toString(data.getInt(1)));
                    Log.i(LOG_TAG, "TOTALES_LOADER - IVAstring"+ mIva.getText().toString());

                    Log.i(LOG_TAG, "TOTALES_LOADER - mIvaCalculo"+ mIvaCalculo);
                    Log.i(LOG_TAG, "TOTALES_LOADER - mIsSpecialCustom"+ mIsSpecialCustom);
                    if(mIsSpecialCustom>0){
                        mIvaCalculo=0.0;
                    }
                    Log.i(LOG_TAG, "TOTALES_LOADER - mIvaCalculo"+ (1+mIvaCalculo/100));
                    mMontoTotal.setText("Monto Total:"+ format.format(data.getDouble(0))+"-"+
                            format.format(data.getDouble(0)*(1+mIvaCalculo/100)));
                    Log.i(LOG_TAG, "TOTALES_LOADER - IVA"+ mIva.getText().toString() );
                    Log.i(LOG_TAG, "TOTALES_LOADER - IVA%"+ (Integer.parseInt(mIva.getText().toString()))/100);


                    mMontoTotalDelivey.setText("Monto Total Delivery:"+ format.format(data.getDouble(2))+"-"+
                            format.format(data.getDouble(2)*(1+mIvaCalculo/100)));
                    do {

                        Log.i(LOG_TAG, "TOTALES_LOADER - cantidad"+ Integer.toString(data.getInt(1)) );
                        Log.i(LOG_TAG, "TOTALES_LOADER - monto total" +format.format(data.getDouble(0)) );
                        saveTotalPrice(data.getDouble(0)*(1+mIvaCalculo/100));
                        if (mAction==CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY){
                            saveTotalPrice(data.getDouble(2)*(1+mIvaCalculo/100));
                        }

                    }while (data.moveToNext());



                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
//        mSpinnerAdapter.swapCursor(null);

    }

    public void saveTotalPrice(double totalPrice){
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);

        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mItem));
        builder.withValue(CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER, totalPrice);

        Log.i(LOG_TAG, "saveTotalPrice:"+totalPrice);

        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }


    }



    public void deleteCustomOrder() {
        Log.i(LOG_TAG, "delete");
    }

    public void SaveCustomOrder() {

        Log.i(LOG_TAG, "save");
        if (true) {
//            if (verification()) {
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
            if (mAction == CUSTOM_ORDER_NEW && mCustomRef != 0) {
//            if ( mItem != 0) {
                Log.i(LOG_TAG, "save New");

                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrders.CONTENT_URI);
                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, mCustomRef);
                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                String formattedDate = df.format(new Date());
                Log.i(LOG_TAG, "formattedDate:"+formattedDate);
                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, STATUS_ORDER_INICIAL);
                builder.withValue(CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER, 0);

                batchOperations.add(builder.build());
            } else

            {

                Log.i(LOG_TAG, "save Modification");
                Log.i(LOG_TAG, "mItem"+mItem);
                Log.i(LOG_TAG, "mCustomRef"+mCustomRef);


                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mItem));
                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, mCustomRef);
                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                String formattedDate = df.format(new Date());
                Log.i(LOG_TAG, "formattedDate:"+formattedDate);
                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, STATUS_ORDER_INICIAL);
                builder.withValue(CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER, 0);

                batchOperations.add(builder.build());




            }

            try {

                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }
            getActivity().onBackPressed();
        }
    }

    public  void saveCustomOrderProduct(long refProduct,String productName,String price) {
        Log.i(LOG_TAG, "saveCustomOrderProduct");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mCustomRef != 0) {
//            if ( mItem != 0) {
            Log.i(LOG_TAG, "saveCustomOrderProduct--mCustomRef != 0");
            CurrencyToDouble price1 = new CurrencyToDouble(price);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrdersDetail.CONTENT_URI);
            builder.withValue(CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL, mItem);
            builder.withValue(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL,refProduct);
            builder.withValue(CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL, productName);
            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, 0);
            builder.withValue(CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,price1.convert());
            batchOperations.add(builder.build());
        } try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    public void saveCantidad(long id, int cantidad){

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {

            Log.i(LOG_TAG, "save saveCantidad");
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
           if(mAction==CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY){
               builder.withValue(CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL, cantidad);
           }else {

               builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, cantidad);
           }
            batchOperations.add(builder.build());
        } try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            getLoaderManager().initLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }


    public void saveCantidadDelivey(long id, int cantidad){

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {

            Log.i(LOG_TAG, "save saveCantidad");
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, cantidad);

            batchOperations.add(builder.build());
        } try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            getLoaderManager().initLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }

    public void saveFavorito(long id, boolean favorito){

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (id != 0) {

            Log.i(LOG_TAG, "save saveCantidad");

            int myInt = (favorito) ? 1 : 0;
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(id));
            builder.withValue(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, myInt);

            batchOperations.add(builder.build());
        } try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }

    }


    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
//                myLabel.setText("No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.i("zebra22","size:" + pairedDevices.size());
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    Log.i("zebra22","name:" + device.getName());
                    Log.i("zebra22","getAddress():" + device.getAddress());
                    Log.i("zebra22","describeContents():" + device.describeContents());
                    Log.i("zebra22","BondState():" + device.getBondState());

                    if (device.getName().equals("XXXXJ154501680")) {

                        mmDevice = device;
                        sendButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                }
            }

//            myLabel.setText("Bluetooth device found.");

        }catch(Exception e){
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
            Log.i("zebra22","openBT() :");
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
            Log.i("zebra22","beginListenForDat :");
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
                                Log.i("zebra22","beginListenForDat a:bytesAvailable > 0: "+ bytesAvailable);
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    Log.i("zebra22","beginListenForDat b-: "+ (char) (b & 0xFF));
                                    if (b == delimiter) {
                                        Log.i("zebra22","beginListenForDat b: "+ "enter");
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        Log.i("zebra22","data received1x :");
                                        Log.i("zebra22", data);
//                                        Log.i("zebra22","data received1 :"+ data);
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                                Log.i("zebra22","data received2 :"+ data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
//                            Log.i("zebra22","beginListenForDat :bytesAvailable <= 0");

                        } catch (IOException ex) {
                            Log.i("zebra22","beginListenForDat :IOException ex");
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            Log.i("zebra22","beginListenForDat :IOException ex function");
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
            String msg=null;
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
            msg="^XA";
            mmOutputStream.write(msg.getBytes());
//            msg="^LT000";
//            mmOutputStream.write(msg.getBytes());

//            msg="^HH"; //return configuration Label
//            mmOutputStream.write(msg.getBytes());


//            msg="~WC"; //print configuration Label
//            mmOutputStream.write(msg.getBytes());


            //Label Legth  in dots 8dots/mm. (203 dpi)

            msg="^LL600";
            mmOutputStream.write(msg.getBytes());


            // FO x,y- x: margen derecho, y: distancia al origen.

            //  ADN:alto de letra,ancho de letra (letra horizontal- normal)
            //  ADR:alto de letra,ancho de letra (letra vertical-Mira al Margen )
            //  ADI:alto de letra,ancho de letra (letra horizontal - Invertida)
            //  ADB:alto de letra,ancho de letra (letra vertical-Mira al centro de la etiqueta)

            int h=50;
            int i =30;

            msg="^FO5,"+h+"^ADN,36,20^FD"+"Orden Nro: " +mItem+"^FS";
            mmOutputStream.write(msg.getBytes());

            SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            String formattedDate = df.format(new Date());

            msg="^FO310,"+h+"^ADN,24,10^FD"+"feecha:"+formattedDate+"^FS";
            mmOutputStream.write(msg.getBytes());

            h=h+i+i;
            msg="^FO5,"+h+"^ADN,36,20^FD"+mCustomName.getText().toString() +" "+mLastName.getText().toString()+"^FS";
            mmOutputStream.write(msg.getBytes());

            h=h+i+i;
            msg="^FO5,"+h+"^ADN,24,10^FD"+mDeliveyAddress.getText().toString()+"^FS";
            mmOutputStream.write(msg.getBytes());

            h=h+i-5;
            msg="^FO5,"+h+"^ADN,24,10^FD"+mCity.getText().toString()+"^FS";
            mmOutputStream.write(msg.getBytes());

            h=h+i;

            msg="^FO5,"+(h+i)+"^ADN,24,10^FD"+"Producto"+"^FS";
            mmOutputStream.write(msg.getBytes());

            msg="^FO190,"+(h+i)+"^ADN,24,10^FD"+"Cantidad"+"^FS";
            mmOutputStream.write(msg.getBytes());

            msg="^FO310,"+(h+i)+"^ADN,24,10^FD"+"Precio"+"^FS";
            mmOutputStream.write(msg.getBytes());

            msg="^FO430,"+(h+i)+"^ADN,24,10^FD"+"Total"+"^FS";
            mmOutputStream.write(msg.getBytes());

           h=h+i;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            Double totalOrden =0.0;

            if (mCursorTotales != null && mCursorTotales.moveToFirst()) {
                do {

//
//                String proyection2[] = {
//       /* 0 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL ,
//       /* 1 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL ,
//       /* 2 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL ,
//       /* 3 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL ,
//       /* 4 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL ,
//       /* 5 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL ,
//       /* 6 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL ,
//       /* 7 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.IMAGEN_PRODUCTO ,
//       /* 8 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.DESCRIPCION_PRODUCTO,
//       /* 9 */                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
//       /* 10 */               LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL ,


                    String name = mCursorTotales.getString(6);
                    int cantidad = mCursorTotales.getInt(10);
                    Double precio = mCursorTotales.getDouble(4);
                    Double total = precio * cantidad;
                    totalOrden=totalOrden+total;



                    msg = "^FO5," + (h + i) + "^ADN,24,10^FD" + name + "^FS";
                    mmOutputStream.write(msg.getBytes());

                    msg = "^FO190," + (h + i) + "^ADN,24,10^FD" + cantidad + "^FS";
                    mmOutputStream.write(msg.getBytes());

                    msg = "^FO310," + (h + i) + "^ADN,24,10^FD" +format.format( precio) + "^FS";
                    mmOutputStream.write(msg.getBytes());

                    msg = "^FO430," + (h + i) + "^ADN,24,10^FD" +format.format( total) + "^FS";
                    mmOutputStream.write(msg.getBytes());


                    h = h + i;
                } while (mCursorTotales.moveToNext());
            }

            h = h + i*3;
            msg = "^FO5," + h  + "^ADN,36,20^FD" + "Total: " + "^FS";
            mmOutputStream.write(msg.getBytes());


            msg = "^FO290," + h + "^ADN,36,20^FD" + format.format(totalOrden) + "^FS";
            mmOutputStream.write(msg.getBytes());


            msg = "^FO310," + h + "^ADN,36,20^FD" +"iva: "+ format.format(totalOrden*mIvaCalculo/100) + "^FS";
            mmOutputStream.write(msg.getBytes());


            msg = "^FO430," + h + "^ADN,36,20^FD" + "= "+format.format(totalOrden*(1+mIvaCalculo/100)) + "^FS";
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

            msg="^XZ";
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

}
