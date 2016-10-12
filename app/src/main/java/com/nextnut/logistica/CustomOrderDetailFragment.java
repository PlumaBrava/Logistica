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
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.OrderDetailCursorAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.util.BoolIntConverter;
import com.nextnut.logistica.util.CurrencyToDouble;
import com.nextnut.logistica.util.ProductSectionActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private long mItem;
    private long mCustomRef = 0;
    private long mIdDetailCustomOrder_for_favorite = 0;
    private CheckBox mCheckBox_for_favorite;
    private Cursor c_favorite;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public static final String ARG_ITEM_ID = "item_id";
    public static final String CUSTOM_ORDER_ACTION = "custom_order_action";

    private static final int CUSTOM_LOADER = 0;
    // This loader look for the customer of an Specific Custom Order
    // mItem has the number of the order and it is send by bundle
    private static final int CUSTOM_ORDER_LOADER = 1;
    private static final int PRODUCTS_LOADER = 2;
    private static final int TOTALES_LOADER = 3;
    // This loader looks for the custom information of the las Custom Order
    // This order was created just before this call this loader
    private static final int CUSTOM_LOADER_NEW = 4;


    //    private TextView mCustomId;
//    private Spinner mSpinner;
//    CustomAdapter mSpinnerAdapter;
    private TextView mOrderNumber;
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
    private CheckBox mIsSpecialCustom;


    private Button mBotonSeleccionCliente;

    private int mAction;

    public static final int REQUEST_CUSTOMER = 1234;
    public static final int UPDATE_CUSTOMER = 1236;
    public static final int REQUEST_PRODUCT = 12345;

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
    RecyclerView mRecyclerView;
    OrderDetailCursorAdapter mAdapter;


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


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        switch (mAction) {
            case CUSTOM_ORDER_NEW: // Go to the last order
                getLoaderManager().initLoader(CUSTOM_LOADER_NEW, null, this);
                break;

            case CUSTOM_ORDER_SELECTION: // Go to the mItem order.
                getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
                break;
            case ACTION_CUSTOM_ORDER_DELIVERY: // Process Delivery state
                getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
//                openButton.setVisibility(View.VISIBLE);
//                sendButton.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        switch (mAction) {
            case CUSTOM_ORDER_NEW: // Go to the last order
                getLoaderManager().restartLoader(CUSTOM_LOADER_NEW, null, this);
                break;

            case CUSTOM_ORDER_SELECTION: // Go to the mItem order.
                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
                break;
            case ACTION_CUSTOM_ORDER_DELIVERY: // Process Delivery state
                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
                break;
            default:
                break;
        }

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




        mOrderNumber = (TextView) mRootView.findViewById(R.id.orderNumber);
        mBotonSeleccionCliente = (Button) mRootView.findViewById(R.id.botonSelecionCliente);
        mBotonSeleccionCliente.setVisibility(View.VISIBLE);

        Button mBotonSeleccionProduto = (Button) mRootView.findViewById(R.id.botonSelecionProdcuto);
        mBotonSeleccionProduto.setVisibility(View.VISIBLE);

        mCustomName = (TextView) mRootView.findViewById(R.id.custom_name_text);
        mLastName = (TextView) mRootView.findViewById(R.id.product_Lastname);
        ImageView mImageCustomer = (ImageView) mRootView.findViewById(R.id.custom_imagen);
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
                getActivity().startActivityForResult(intent, UPDATE_CUSTOMER);
            }

        });


        mBotonSeleccionProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ProductSectionActivity.class);
                intent.putExtra("ITEM",mItem);
                getActivity().startActivityForResult(intent, REQUEST_PRODUCT);

            }

        });

        View emptyView = mRootView.findViewById(R.id.recyclerview_product_empty);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.product_list_customOrder);

        mAdapter = new OrderDetailCursorAdapter(getContext(), null, emptyView, new OrderDetailCursorAdapter.ProductCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                showDialogNumberPicker(vh);
            }

            @Override
            public void onFavorite(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                String where =
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " = " + vh.mRefCustomer + " and " +
                                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL + " = " + vh.mRefProduct + " and " +
                                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL + " = 1 ";

                c_favorite = getActivity().getContentResolver().query(LogisticaProvider.join_customorderDetail_Product_Customer.CONTENT_URI,
                        null,
                        where,
                        null,
                        null, null);


                if (c_favorite.getCount() >= 1) {
                    mIdDetailCustomOrder_for_favorite = vh.mDetalleOrderId;
                    mCheckBox_for_favorite = vh.mfavorito;
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(getString(R.string.favoriteAlreadyExisist));
                    alert.setMessage(getString(R.string.doYouWantToChange));
                    alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {

                            mCheckBox_for_favorite.setChecked(!mCheckBox_for_favorite.isChecked());
                            dialog.cancel();
                        }
                    });
                    alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {


                            if (c_favorite != null && c_favorite.getCount() > 0) {
                                c_favorite.moveToFirst();
                                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(c_favorite.getCount());

                                do {
                                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrdersDetail.withId(c_favorite.getLong(0)));


                                    builder.withValue(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, false);
                                    batchOperations.add(builder.build());

                                } while (c_favorite.moveToNext());
                                try {

                                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);

                                } catch (RemoteException | OperationApplicationException e) {

                                }


                            }


                            ContentValues upDateValues = new ContentValues();
                            upDateValues.put(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, new BoolIntConverter().boolToInt(mCheckBox_for_favorite.isChecked()));
                            getContext().getContentResolver().update(LogisticaProvider.CustomOrdersDetail.withId(mIdDetailCustomOrder_for_favorite),
                                    upDateValues, null, null);

                        }
                    });
                    alert.create().show();


                } else {


                    ContentValues upDateValues = new ContentValues();
                    upDateValues.put(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, new BoolIntConverter().boolToInt(vh.mfavorito.isChecked()));
                    getContext().getContentResolver().update(LogisticaProvider.CustomOrdersDetail.withId(vh.mDetalleOrderId),
                            upDateValues, null, null);
                }
            }

            @Override
            public void onProductDismiss(long id) {


                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);


                ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withId(id));

                try {

                    batchOperations.add(builder.build());
                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);


                } catch (RemoteException | OperationApplicationException e) {

                } finally {
                    getLoaderManager().restartLoader(PRODUCTS_LOADER, null, CustomOrderDetailFragment.this);
                    getLoaderManager().restartLoader(TOTALES_LOADER, null, CustomOrderDetailFragment.this);
                    mAdapter.notifyDataSetChanged();
                }


            }

        });
        if (mAction == CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY) {
            mAdapter.setDeliveryState();
        }

        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, SimpleItemTouchHelperCallback.ORDER_INICIAL);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        return mRootView;
    }

    public void showDialogNumberPicker(final OrderDetailCursorAdapter.ViewHolder vh) {

        {

            final Dialog d = new Dialog(getContext());
            d.setTitle(getString(R.string.quantityPicker));
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(getResources().getInteger(R.integer.quantityPickerMax));
            np.setValue(Integer.valueOf(vh.mTextcantidad.getText().toString()));
            np.setMinValue(getResources().getInteger(R.integer.quantityPickerMin));
            np.setWrapSelectorWheel(true);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAction == CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY) {
                        vh.mTextcantidadDelivery.setText(String.valueOf(np.getValue()));
                        CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecioDelivery.getText().toString());
                        double total = np.getValue() * price.convert();
                        NumberFormat format = NumberFormat.getCurrencyInstance();
                        vh.mTextToalDelivery.setText(format.format(total));
                        saveCantidad(vh.mDetalleOrderId, Integer.valueOf(np.getValue()));

                        d.dismiss();

                    } else {
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

        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {


            case CUSTOM_ORDER_LOADER:


    /*0*/
                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
    /*1*/                    LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
    /*2*/                    LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
    /*3*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
    /*4*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
    /*5*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.DELIIVERY_ADDRES_CUSTOM,
    /*6*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.DELIVERY_CITY_CUSTOM,
    /*7*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.IMAGEN_CUSTOM,
    /*8*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.SPECIAL_CUSTOM,
    /*9*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.CUIT_CUSTOM,
    /*10*/                    LogisticaDataBase.CUSTOMS + "." + CustomColumns.IVA_CUSTOM
                };


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.ShowJoin.CONTENT_URI,
                        proyection,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER + "=" + mItem,
                        null,
                        null);

            case CUSTOM_LOADER_NEW:


/* 0 */
                String proyection1[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
/* 1 */                      LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
/* 2 */                      LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
/* 3 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
/* 4 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
/* 5 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.DELIIVERY_ADDRES_CUSTOM,
/* 6 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.DELIVERY_CITY_CUSTOM,
/* 7 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.IMAGEN_CUSTOM,
/* 8 */                      LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
/* 9 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.SPECIAL_CUSTOM,
/* 10 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.CUIT_CUSTOM,
/* 11 */                      LogisticaDataBase.CUSTOMS + "." + CustomColumns.IVA_CUSTOM,
                };


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.ShowJoin.CONTENT_URI,
                        proyection1,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + "=" + CustomOrderDetailFragment.STATUS_ORDER_INICIAL,
                        null,
                        null);


            case PRODUCTS_LOADER:

                String proyection2[] = {
       /* 0 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL,
       /* 1 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL,
       /* 2 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL,
       /* 3 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL,
       /* 4 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,
       /* 5 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL,
       /* 6 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL,
       /* 7 */                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
       /* 8 */                LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
       /* 9 */                LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
       /* 10 */               LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL,


                };

                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.join_Product_Detail_order.CONTENT_URI,
                        proyection2,
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL + "=" + mItem,
                        null,
                        null);

            case TOTALES_LOADER:

                String proyection3[] = {
        /* 0 */   "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " * " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL + " ) ",

       /* 1 */    "count(" + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL + " )",
      /* 2 */   "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " * " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL + " ) "

                };

                return new CursorLoader(

                        getActivity(),
                        LogisticaProvider.CustomOrdersDetail.CONTENT_URI,
                        proyection3,
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL + "=" + mItem,
                        null,
                        null);

            default:
                return null;

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CUSTOM_LOADER_NEW:
                if (data != null && data.moveToLast()) {

                    mItem = data.getLong(8);
                    mOrderNumber.setText(Long.toString(mItem));
                    mBotonSeleccionCliente.setVisibility(View.VISIBLE);
                    mCustomRef = data.getLong(0);
                    mCustomName.setText(data.getString(3));
                    mLastName.setText(data.getString(4));
                    mDeliveyAddress.setText(data.getString(5));
                    mCity.setText(data.getString(6));
                    mCurrentPhotoPath = data.getString(7);
                    mIsSpecialCustom.setChecked(data.getInt(9) > 0);
                    mCuit.setText(data.getString(10));
                    mIva.setText(data.getString(11));
                    mIvaCalculo = data.getDouble(data.getColumnIndex(CustomColumns.IVA_CUSTOM));

                    if (appBarLayout != null) {

                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + data.getLong(8));

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
                        c.moveToFirst();
                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(c.getCount());

                        do {

                            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrdersDetail.CONTENT_URI);
                            builder.withValue(CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL, data.getLong(8));
                            builder.withValue(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL, c.getLong(c.getColumnIndex(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL)));
                            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, c.getLong(c.getColumnIndex(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL)));
                            builder.withValue(CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL, c.getDouble(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
                            builder.withValue(CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL, c.getString(c.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
                            batchOperations.add(builder.build());
                        } while (c.moveToNext());
                        try {
                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                        } catch (RemoteException | OperationApplicationException e) {
                        } finally {
                            getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
                            mAdapter.notifyDataSetChanged();
                        }

                        getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                    }
                } else {
                    mRootView.setVisibility(View.GONE);
                }
                break;

            case CUSTOM_ORDER_LOADER:
                if (data != null && data.moveToFirst()) {
                    mOrderNumber.setText(Long.toString(mItem));
                    mBotonSeleccionCliente.setVisibility(View.VISIBLE);
                    mCustomRef = data.getLong(0);
                    mCustomName.setText(data.getString(3));
                    mLastName.setText(data.getString(4));
                    mDeliveyAddress.setText(data.getString(5));
                    mCity.setText(data.getString(6));
                    mCurrentPhotoPath = data.getString(7);
                    mIsSpecialCustom.setChecked(data.getInt(8) > 0);
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + mItem);
                    }
                    mCuit.setText(data.getString(9));
                    mIva.setText(data.getString(10));
                    mIvaCalculo = data.getDouble(data.getColumnIndex(CustomColumns.IVA_CUSTOM));
                    getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
                }
                break;

            case PRODUCTS_LOADER:
                if (data != null && data.moveToFirst()) {

                    mCursorTotales = data;
                }
                mAdapter.swapCursor(data);

                break;

            case TOTALES_LOADER:
                if (data != null && data.moveToFirst()) {
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    mCantidadTotal.setText(getResources().getString(R.string.TotalCantidad) + Integer.toString(data.getInt(1)));
                    if (mIsSpecialCustom.isChecked()) {
                        mIvaCalculo = 0.0;
                    }
                    mMontoTotal.setText(getResources().getString(R.string.MontoTotal) + format.format(data.getDouble(0)) + "-" +
                            format.format(data.getDouble(0) * (1 + mIvaCalculo / 100)));


                    mMontoTotalDelivey.setText(getResources().getString(R.string.MontoTotalDelivey) + format.format(data.getDouble(2)) + "-" +
                            format.format(data.getDouble(2) * (1 + mIvaCalculo / 100)));
                    do {
                        saveTotalPrice(data.getDouble(0) * (1 + mIvaCalculo / 100));
                        if (mAction == CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY) {
                            saveTotalPrice(data.getDouble(2) * (1 + mIvaCalculo / 100));
                        }

                    } while (data.moveToNext());


                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

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

    public void updateDateFormat() {
        try {
            Cursor c = getActivity().getContentResolver().query(LogisticaProvider.CustomOrders.CONTENT_URI,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();


                do {

                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(c.getLong(0)));
                    builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, "2016-08-15");
                    batchOperations.add(builder.build());
                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                } while (c.moveToNext());


            } else {
            }

        } catch (Exception e) {
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
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (customerReference != 0) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mItem));
            builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customerReference);
            SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            String formattedDate = df.format(new Date());
            builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
            builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, STATUS_ORDER_INICIAL);
            builder.withValue(CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER, 0);
            batchOperations.add(builder.build());
        }

        try {
            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
        } finally {
            getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        }


    }

    public void saveCustomOrderProduct(long refProduct, String productName, String priceSpecial, String price) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mCustomRef != 0) {
//            if ( mItem != 0) {
            CurrencyToDouble price1 = new CurrencyToDouble(mIsSpecialCustom.isChecked() ? priceSpecial : price);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrdersDetail.CONTENT_URI);
            builder.withValue(CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL, mItem);
            builder.withValue(CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL, refProduct);
            builder.withValue(CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL, productName);
            builder.withValue(CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL, 0);
            builder.withValue(CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL, price1.convert());
            batchOperations.add(builder.build());
        }
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
            mAdapter.notifyDataSetChanged();

        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
        }
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
            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
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
            getLoaderManager().initLoader(TOTALES_LOADER, null, this);
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
            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
        } catch (RemoteException | OperationApplicationException e) {
        }

    }




}
