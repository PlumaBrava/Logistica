package com.nextnut.logistica;

import android.animation.ObjectAnimator;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.nextnut.logistica.Util.DialogAlerta;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.CustomsOrdersCursorAdapter;
import com.nextnut.logistica.rest.OrderDetailCursorAdapter;

import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CustomOrderListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";
    private static final int CUSTOM_LOADER_LIST = 0;


    private static final int CUSTOM_LOADER_TOTAL_PRODUCTOS = 1;

    final private int  MY_PERMISSIONS_REQUEST_CALL_PHONE =123;
    final private int  MY_PERMISSIONS_REQUEST_READ_CONTACT =124;

    private static final int CUSTOM_ORDER_LOADER = 0;

    private CustomsOrdersCursorAdapter mCursorAdapter;
    private OrderDetailCursorAdapter mCursorAdapterTotalProductos;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTotalProductos;

    private ItemTouchHelper mItemTouchHelper;

    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;

    private int mItem = 0;
    private long mCustomOrderIdSelected;


    public static final int ORDER_STATUS_INICIAL = 0;
    public static final int ORDER_STATUS_PICKING = 1;
    public static final int ORDER_STATUS_DELIVERED = 2;
    public static final int ORDER_STATUS_DELETED = 3;

//    /**
//     * The dummy content this fragment is presenting.
//     */
//    private DummyContent.DummyItem mItem;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = CustomOrderListFragment.class.getSimpleName();


    public static final float LARGE_SCALE = 1.5f;
    private boolean symmetric = true;
    private boolean small = true;

    private DataChangeNotification mDataChangeNotification;

    public CustomOrderListFragment() {

    }
    public CustomOrderListFragment(DataChangeNotification dataChangeNotification) {
        this.mDataChangeNotification= dataChangeNotification;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_customorder_list, container, false);

//        setContentView(R.layout.activity_customorder_list);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        // Show the Up button in the action bar.
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_customOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        mCursorAdapterTotalProductos = new OrderDetailCursorAdapter(getContext(), null,
                emptyViewTotalProducts,
                new OrderDetailCursorAdapter.ProductCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, OrderDetailCursorAdapter.ViewHolder v) {
                Log.i(LOG_TAG, "Productos:" + id);
//                changeSize(recyclerViewTotalProductos);
                recyclerViewTotalProductos.setVisibility(View.GONE);
            }

                    @Override
                    public void onFavorite(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                        Log.i(LOG_TAG, " onFavorite:" + id);
                    }

                    @Override
                    public void onProductDismiss(long id) {

                    }
                }
        );

        mCursorAdapterTotalProductos.resetFavoriteVisible();
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);


        View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.customorder_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));


        mCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {
                Log.i(LOG_TAG, "onclicka:" + id);
//
                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);
                    Log.i(LOG_TAG, "ARG_ITEM_ID2 :" + id);
                    arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);

                    CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();
                    fragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.customorder_detail_container, fragment)
                            .commit();

//                            fab_new.setVisibility(View.GONE);
//                            fab_save.setVisibility(View.VISIBLE);
//                            fab_delete.setVisibility(View.VISIBLE);

                } else {
                    Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                    intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
                    Log.i(LOG_TAG, "ARG_ITEM_ID: 1" + id);
                    Log.i(LOG_TAG, "CUSTOM_ACTION" + CustomDetailFragment.CUSTOM_SELECTION);
                    intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, id);
//                            fab_new.setVisibility(View.VISIBLE);
//                            fab_save.setVisibility(View.GONE);
//                            fab_delete.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.i("ProductListActivity", "makeSceneTransitionAnimation");

//                                Pair<View, String> p1 = Pair.create((View) vh.mphotoCustomer, getString(R.string.custom_icon_transition_imagen));
                        Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
//                                Pair<View, String> p3 = Pair.create((View) vh.mSurename, getString(R.string.custom_icon_transition_surname));
                        ActivityOptionsCompat activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
                        startActivity(intent, activityOptions.toBundle());

                    } else {
                        Log.i("ProductListActivity", "makeSceneTransitionAnimation Normal");
                        startActivity(intent);
                    }

                }


            }

            @Override
            public void onMakeACall(String ContactID) {
                Log.i("ProductListActivity", "Make a Call "+ ContactID );
                makeTheCall(ContactID);
            }

            @Override
            public void onDialogAlert(String message) {
                Log.i("onDialogAlert:", "onDialogAlert " +message);
//                DialogAlerta dFragment = DialogAlerta.newInstance("test"+message);
//
//                dFragment.show(getFragmentManager(), "Dialog Fragment");


                AlertDialog.Builder alert ;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert  = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    alert  = new AlertDialog.Builder(getContext());
                }

//                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setMessage(message);
                alert.create().show();
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            }

            @Override
            public void onItemDismissCall(long cursorID) {
                mCustomOrderIdSelected =cursorID;

                AlertDialog.Builder alert ;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert  = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    alert  = new AlertDialog.Builder(getContext());
                }

//                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setMessage("Do you want to delete?");
                alert.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.i("YesNoDialog:", "setNegativeButton" );
                        onDataChange();

                        dialog.cancel();
                    }
                });
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.i("YesNoDialog:", "setPositiveButton " );

                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(2);

                        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withRefCustomOrder(mCustomOrderIdSelected));
                        ContentProviderOperation.Builder builder1 = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                        batchOperations.add(builder.build());
                        batchOperations.add(builder1.build());

                        try {

                           getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);




//                    notifyItemRemoved(position);
                        } catch (RemoteException | OperationApplicationException e) {
                            Log.e("TouchHelper:", "Error applying batch insert", e);

                        }finally {
                            onDataChange();
                        }
                    }
                });
                alert.create().show(); // btw show() creates and shows it..




            }

            @Override
            public void onItemAceptedCall(long cursorID) {
                mCustomOrderIdSelected=cursorID;

                if (MainActivity.mPickingOrderSelected==0){

                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
                    onDataChange();
                }
                else {


                    Log.e("TouchHelper:", "mCustomOrderIdSelecte " +mCustomOrderIdSelected);



                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_PICKING );
                    SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                    String formattedDate = df.format(new Date());
                    builder.withValue(CustomOrdersColumns.DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER, formattedDate);
                    builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, MainActivity.getmPickingOrderSelected());
                    batchOperations.add(builder.build());
                    try {

                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                          onDataChange();


//
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.e("TouchHelper:", "Error applying batch insert", e);

                    }
                }
            }

            @Override
            public void onDataChange() {
                Log.i("ProductListActivity", "CustomsOrdersCursorAdapteronDataChangekHandler");
                getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, CustomOrderListFragment.this);
                getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
            }
        }
        ,CustomsOrdersCursorAdapter.STEP_CUSTOM_ORDER);


        recyclerView.setAdapter(mCursorAdapter);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume()");
        getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
        getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(LOG_TAG, "setUserVisibleHint" + isVisibleToUser);
        if(isVisibleToUser && mCursorAdapter!=null){
            getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
            getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated");
        getLoaderManager().initLoader(CUSTOM_LOADER_LIST, null, this);
        getLoaderManager().initLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, this);

        super.onActivityCreated(savedInstanceState);
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
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case CUSTOM_LOADER_LIST:
                Log.i(LOG_TAG, "CUSTOM_LOADER_LIST CreateLoader");

//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        String select = "((" + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " NOTNULL) AND ("
//                + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " ='2'))";
////                AND ("
////                        + Contacts.DISPLAY_NAME + " != '' ))";
//
                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM,
                        LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.STATUS_CUSTOM_ORDER
                };
//
//        String from[] = {LogisticaProvider.CustomOrders.CONTENT_URI.toString() + " as CO" ,
//                LogisticaProvider.Customs.CONTENT_URI.toString() + " as CUSTOM"
//
//        };
//        return new CursorLoader(
//                getActivity(),
//                LogisticaProvider.CustomOrders.CONTENT_URI2,//uri
//                proyection,// PROYECTION (select)
//                select,//sELECTION (WHERE)
//                null,// ARGUMENTS, STRING
//                null); // ORDEN


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.ShowJoin.CONTENT_URI,
                        proyection,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + "=" + 0,
                        null,
                        null);



            case CUSTOM_LOADER_TOTAL_PRODUCTOS:

                String select[] = {
/* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
/* 1 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
/* 2 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER,
/* 3 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
/* 4 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,
/* 5 */             "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
/* 6 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL,
/* 7 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
/* 8 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
/* 9 */             LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
/* 10 */            LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL

                };


                Log.i(LOG_TAG, "onCreateLoader");
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.join_Product_Detail_order.CONTENT_URI,
                        select,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderDetailFragment.STATUS_ORDER_INICIAL,
                        null,
                        null);


            default:


                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {

            case CUSTOM_LOADER_LIST:
                Log.i(LOG_TAG, "onLoadFinished");
                if (data != null && data.moveToFirst()) {
//                    Log.i(LOG_TAG, "ID:" + data.getInt(0));
//                    Log.i(LOG_TAG, "date:" + data.getString(1));
//                    Log.i(LOG_TAG, "price:" + data.getLong(2));
//                    Log.i(LOG_TAG, "Name:" + data.getString(3));
//                    Log.i(LOG_TAG, "LastName:" + data.getString(4));

                    Log.i(LOG_TAG, "swapCursor");
                    mCursorAdapter.swapCursor(data);
//                    animateViewsIn();
                }
                break;



            case CUSTOM_LOADER_TOTAL_PRODUCTOS:
                if (data != null && data.moveToFirst()) {
                    mCursorAdapterTotalProductos.swapCursor(data);
                    Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - data.getCount: " + data.getCount());

//                    do {
//
//
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - producto: "+ data.getString(0) );
//                        Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - nombre: " + data.getString(6));
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - imagen:"+ data.getString(2) );
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - descrition"+ data.getString(3) );
//                        Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - cantidad" + data.getString(5));
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - precio"+ data.getString(5) );
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - favorito"+ data.getString(6) );
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - ID_CUSTOM_ORDER"+ data.getString(7) );
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - Status"+ data.getString(8) );
////                Log.i(LOG_TAG, "CUSTOM_LOADER_TOTAL_PRODUCTOS - FechaCreacion"+ data.getString(9) );
//                    } while (data.moveToNext());
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "swapCursor");
        mCursorAdapter.swapCursor(null);
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
        int count = recyclerView.getChildCount();
        float offset = getResources().getDimensionPixelSize(R.dimen.offset_y);
        Interpolator interpolator =
                AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear_out_slow_in);

        // loop over the children setting an increasing translation y but the same animation
        // duration + interpolation
        for (int i = 0; i < count; i++) {
            View view = recyclerView.getChildAt(i);
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

    public interface DataChangeNotification {

        void onStepModification(int step);

    }
    public void makeTheCall (String ContactID ){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "phone-Number: "+"PERMISSION No GRANTED");


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.CALL_PHONE)) {
                Log.e(LOG_TAG, "phone-Number: "+"Notificar el pedido");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                Log.e(LOG_TAG, "phone-Number: "+"pace el pedido luego de verificar si debe");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                Log.e(LOG_TAG, "phone-Number: "+"pace el pedido");
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        else {
            Log.e(LOG_TAG, "phone-Number: "+"llama directo, esta autorizado");
            makePhoneCall(ContactID);
//                Intent intent = new Intent(Intent.ACTION_CALL,
//                        Uri.parse("tel:"+number));
//                startActivity(intent);
            // Do something with the phone number...
        }


    }

    public void makePhoneCall(String id ) {
        Log.e(LOG_TAG, "phone-Number: "+"makePhoneCal : "+id);
        Cursor cursor = null;
        String phoneNumber = "";
        int type = 0;
        String phoneType = "";
        List<String> allNumbers = new ArrayList<String>();
        int phoneIdx = 0;
        int displayNameKeyIdx = 0;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "phone-Number: " + "READ_CONTACTS No GRANTED");


//                            // Should we show an explanation?
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                                    android.Manifest.permission.READ_CONTACTS)) {
//                                Log.e(LOG_TAG, "phone-Number: " + "Notificar el pedido READ_CONTACTS");
//                                // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACT);
            Log.e(LOG_TAG, "phone-Number: " + "Hace el pedido luego de verificar si debe READ_CONTACTS");
        } else {

            // No explanation needed, we can request the permission.


            try {


                cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[] { id }, null);
                phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                displayNameKeyIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                Log.e(LOG_TAG, "phone-Number: " + "curor count : " + cursor.getCount());
                Log.e(LOG_TAG, "phone-Number: " + "curor toString : " + cursor.toString());
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {
                        phoneNumber = cursor.getString(phoneIdx);
                        type = cursor.getInt(displayNameKeyIdx);
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                phoneType="HOME";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                phoneType="MOVILE";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                phoneType="WORK";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                            phoneType="FAX WORK";
                            break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                            phoneType="MAIN";
                            break;
                            default: phoneType="Otro";
                        }
                        Log.e(LOG_TAG, "phone-Number: " + "multiple : " + phoneType+" : "+phoneNumber);
                        allNumbers.add(phoneType+" : "+phoneNumber);
                        cursor.moveToNext();

                    }
                } else {
                    //no results actions
                }


            } catch (Exception e) {
                Log.e(LOG_TAG, "phone-Number: " + "Exception" + e.toString());
                //error actions
            } finally {
                Log.e(LOG_TAG, "phone-Number: " + "finally");
                if (cursor != null) {
                    cursor.close();
                }

                final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose a number");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedNumber = items[item].toString();
                        selectedNumber = selectedNumber.replace("-", "");
                       String selectedNumber1[]= selectedNumber.split(":");
                        Log.e(LOG_TAG, "phone-Number: " + "mULTIPLE llamando. selectedNumber1.length: " + selectedNumber1.length);
                        Log.e(LOG_TAG, "phone-Number: " + "mULTIPLE llamando a: " + selectedNumber1[1]);
                        Intent intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + selectedNumber1[1]));
                        startActivity(intent);

                    }
                });
                AlertDialog alert = builder.create();
                if (allNumbers.size() > 1) {
                    alert.show();
                } else {
                    String selectedNumber = phoneNumber.toString();
                    selectedNumber = selectedNumber.replace("-", "");
                    Log.e(LOG_TAG, "phone-Number: " + "sIMPLE llamando a: " + selectedNumber);
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + selectedNumber));
                    startActivity(intent);
                }

                if (phoneNumber.length() == 0) {
                    //no numbers found actions
                }
            }
        }


    }
}

