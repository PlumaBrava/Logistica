package com.nextnut.logistica;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
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



    String mCurrentPhotoPath = null;


    private TextView mCustomId;
//    private Spinner mSpinner;
//    CustomAdapter mSpinnerAdapter;
    private EditText mCustomName;
    private EditText mLastName;
    private EditText mDeliveyAddress;
    private EditText mCity;
    public TextView mCantidadTotal;
    public TextView mMontoTotal;

    private Button button;
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

    public static final int STATUS_ORDER_INICIAL = 0;
    public static final int STATUS_ORDER_PICKING = 1;
    public static final int STATUS_ORDER_DELIVEY = 2;
    public static final int STATUS_ORDER_DELIVED = 3;

    CollapsingToolbarLayout appBarLayout;

    RecyclerView mRecyclerView;
    OrderDetailCursorAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

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
                getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-CUSTOM_LOADER_NEW");
                break;

            case CUSTOM_ORDER_DOUBLE_SCREEN:
                if (mItem == 0) {
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-default DETAIL_PRODUCT_LOADER");
                    getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                } else {
                    getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                    Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-DETAIL_PRODUCT_LOADER");
                }
                break;
            case CUSTOM_ORDER_SELECTION:
                getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);
                getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
                getLoaderManager().initLoader(TOTALES_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_SELECTION");
                break;
            default:
                break;
        }

//        getLoaderManager().initLoader(NAME_PRODUCT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.customorder_detail, container, false);

        mBotonSeleccionCliente = (Button) rootView.findViewById(R.id.botonSelecionCliente);
        mBotonSeleccionCliente.setVisibility(View.VISIBLE);

        mBotonSeleccionProduto = (Button) rootView.findViewById(R.id.botonSelecionProdcuto);
        mBotonSeleccionProduto.setVisibility(View.VISIBLE);

        mCustomId = (TextView) rootView.findViewById(R.id.custom_Id);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
        mImageCustomer = (ImageView) rootView.findViewById(R.id.custom_imagen);
        mDeliveyAddress = (EditText) rootView.findViewById(R.id.custom_delivery_address);
        mCity = (EditText) rootView.findViewById(R.id.custom_city);

        mCantidadTotal=(TextView) rootView.findViewById(R.id.cantidadTotal);;
        mMontoTotal=(TextView) rootView.findViewById(R.id.montoToal);;


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

//               if (c_favorite!=null && c_favorite.getCount()>0 ) {
//                   c_favorite.moveToFirst();
//                   do {
//
//
//                       Log.i(LOG_TAG, "onf - ID: " + c_favorite.getLong(0));
//                       Log.i(LOG_TAG, "onf - REF_CUSTOM: " + c_favorite.getLong(1));
//                       Log.i(LOG_TAG, "onf - REF_PRODUCT: " + c_favorite.getString(2));
//                       Log.i(LOG_TAG, "onf - PRODUCT_NAME: " + c_favorite.getString(3));
//                       Log.i(LOG_TAG, "onf - QUANTITY: " + c_favorite.getString(4));
//                       Log.i(LOG_TAG, "onf - PRICE_CUSTOM: " + c_favorite.getDouble(5));
//                       Log.i(LOG_TAG, "onf - FAVORITE_CUSTOM: " + c_favorite.getLong(6));
//
//
//                   } while (c_favorite.moveToNext());
//               }


                 if (c_favorite.getCount()>=1){
                     Log.i(LOG_TAG, "ya existe onFavorite" + id+ "cantidad: "+c_favorite.getCount()+ "refCustomer: "+vh.mRefCustomer+ " refProducto: "+vh.mRefProduct);
//                     YesNoDialog.newInstance ("Ya existe favorito","Lo cambia?");
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

//                                     ContentValues upDateValues1 = new ContentValues();
//                                     upDateValues1.put(CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL, new BoolIntConverter().boolToInt(false));
//
//
//
//
//                             String where =
//                                     LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL+" = " + c_favorite.getLong(1)+ " and " +
//                                             LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL +" = " +mvh_for_favorite.mRefProduct + " and " +
//                                             LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL +" =1 ";
//                             getContext().getContentResolver().update(LogisticaProvider.CustomOrdersDetail.CONTENT_URI,
//                                     upDateValues1, where, null);
//

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

        });

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
            np.setMaxValue(100);
            np.setMinValue(0);
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
                    vh.mTextcantidad.setText(String.valueOf(np.getValue()));
                    CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
                    double total= np.getValue()*price.convert();
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    vh.mTextToal.setText(format.format(total));
                    saveCantidad(vh.mDetalleOrderId, String.valueOf(np.getValue()));
                    d.dismiss();
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
            saveCustomOrderProduct(data.getExtras().getLong("refProducto"),
                    data.getExtras().getString("ProductoName"),
                    data.getExtras().getString("ProductPrice")                                );

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


                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER ,
                        LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
                        LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIIVERY_ADDRES_CUSTOM,
                        LogisticaDataBase.CUSTOMS+"."+ CustomColumns.DELIVERY_CITY_CUSTOM,
                        LogisticaDataBase.CUSTOMS+"."+ CustomColumns.IMAGEN_CUSTOM
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
/* 8 */                      LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER
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

       /* 1 */    "count("  +     LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL+" )"
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


                    Picasso.with(getContext())
                            .load(mCurrentPhotoPath)
                            .into(mImageCustomer);
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
                    if (appBarLayout != null) {

//                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + data.getString(0));
                        appBarLayout.setTitle(getResources().getString(R.string.title_Order_Number) + mItem);

                    }
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
                            getLoaderManager().restartLoader(PRODUCTS_LOADER, null, this);
                            getLoaderManager().restartLoader(TOTALES_LOADER, null, this);
                            mAdapter.notifyDataSetChanged();
                        }


                    }
                }
                break;

            case PRODUCTS_LOADER:
                if (data != null && data.moveToFirst()) {


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
                    mMontoTotal.setText("Monto Total:"+ format.format(data.getDouble(0)));
                    do {

                        Log.i(LOG_TAG, "TOTALES_LOADER - cantidad"+ Integer.toString(data.getInt(1)) );
                        Log.i(LOG_TAG, "TOTALES_LOADER - monto total" +format.format(data.getDouble(0)) );
                        saveTotalPrice(data.getDouble(0));

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

    public void saveCantidad(long id, String cantidad){

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
}
