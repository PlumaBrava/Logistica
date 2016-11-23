package com.nextnut.logistica;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.CustomsOrdersCursorAdapter;
import com.nextnut.logistica.rest.PickingOrderProductsAdapter;
import com.nextnut.logistica.rest.PickingOrdersCursorAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallbackPickingCustomOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nextnut.logistica.util.MakeCall.makeTheCall;
import static com.nextnut.logistica.util.SharePickingOrder.sharePickingOrder;
import static com.nextnut.logistica.widget.LogisticaWidget.upDateWitget;

/**
 * An activity representing a list of CustomOrders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomOrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PickingListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private long mCustomOrderIdSelected;

    private long mIDPickingOrderSelected;

    private PickingOrdersCursorAdapter mCursorAdapterPickingOrder;
    private PickingOrderProductsAdapter mCursorAdapterTotalProductos;
    private CustomsOrdersCursorAdapter mCustomsOrdersCursorAdapter;

    private RecyclerView recyclerViewPickingOrder;
    private CardView mPickingOrderTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private TextView mCreationDate;

    private LinearLayout mLinearProductos;
    private LinearLayout mLinearOrders;

    private PickingOrdersHandler mPickingOrdersHandler;

    private static final int CUSTOM_ORDER_LOADER = 0;
    private static final int PICKING_ORDER_LOADER = 1;
    private static final int PICKING_LOADER_TOTAL_PRODUCTOS = 2;

    public static final int PICKING_STATUS_INICIAL = 0;
    public static final int PICKING_STATUS_DELIVERY = 1;
    public static final int PICKING_STATUS_CERRADA = 2;
    public static final int PICKING_STATUS_DELETED = 3;

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
    public void onResume() {
        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, this);
        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.picking_list_fragment, container, false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerViewPickingOrder = (RecyclerView) rootView.findViewById(R.id.pickingOrder_list);
        mPickingOrderTile = (CardView) rootView.findViewById(R.id.pickingNumbertitleID);
        mLinearProductos =(LinearLayout)rootView.findViewById(R.id.linearProductos);
        mLinearOrders =(LinearLayout)rootView.findViewById(R.id.linearOrders);

        ImageButton fab_save_picking = (ImageButton) mPickingOrderTile.findViewById(R.id.save_picking_Button);
        fab_save_picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePickingOrder(MainActivity.mPickingOrderSelected);
                mPickingOrdersHandler.onPickingOrderSelected(0);
                mPickingOrderTile.setVisibility(View.GONE);
                mLinearProductos.setVisibility(View.GONE);
                mLinearOrders.setVisibility(View.GONE);
//                recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
//                recyclerViewTotalProductos.setVisibility(View.GONE);
                recyclerViewPickingOrder.setVisibility(View.VISIBLE);

            }
        });


        mPickingOrderTile.setVisibility(View.GONE);
        mTilePickingOrderNumber = (TextView) mPickingOrderTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent = (EditText) mPickingOrderTile.findViewById(R.id.TitlepickingOrderComents);
        mCreationDate = (TextView) mPickingOrderTile.findViewById(R.id.titlePicckinOder_creationdate);
        recyclerViewPickingOrder.setLayoutManager(layoutManager);

        View emptyViewPickingOrders = rootView.findViewById(R.id.recyclerview_pickingOrders_empty);
        mCursorAdapterPickingOrder = new PickingOrdersCursorAdapter(
                getContext(),
                null,
                emptyViewPickingOrders,
                new PickingOrdersCursorAdapter.PinckingOrdersCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, PickingOrdersCursorAdapter.ViewHolder vh) {
                        if (mPickingOrdersHandler != null) {

                            mTilePickingComent.setText(vh.mpickingOrderComents.getText());
                            mTilePickingOrderNumber.setText(vh.mPickingOrderNumber.getText());
                            mTilePickingComent.setVisibility(View.VISIBLE);
                            mCreationDate.setText(vh.mCreationDate.getText());
                            mPickingOrdersHandler.onPickingOrderSelected(id);


                            mCursorAdapterPickingOrder.notifyDataSetChanged();
                            getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, PickingListFragment.this);
                            getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, PickingListFragment.this);
                            mCursorAdapterTotalProductos.notifyDataSetChanged();
                            recyclerViewPickingOrder.setVisibility(View.GONE);

                            mLinearOrders.setVisibility(View.VISIBLE);
                            mLinearProductos.setVisibility(View.VISIBLE);

//                            recyclerViewCustomOrderInPickingOrder.setVisibility(View.VISIBLE);
//                            recyclerViewTotalProductos.setVisibility(View.VISIBLE);
                            mPickingOrderTile.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onDataChange() {
                        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, PickingListFragment.this);
                        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, PickingListFragment.this);
                        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, PickingListFragment.this);
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        mCursorAdapterPickingOrder.notifyDataSetChanged();
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemDismissCall(long cursorID) {
                        mIDPickingOrderSelected = cursorID;
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setMessage(getContext().getResources().getString(R.string.detete_picking_order));
                        alert.setNegativeButton(getContext().getResources().getString(R.string.detete_picking_order_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {

                                onDataChange();
                                dialog.cancel();
                            }
                        });
                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {


                                if (exitenCUasignedtoPickingOrder()) {


                                    onDialogAlert(getResources().getString(R.string.detete_picking_order_Existen_CustomOrders_Asigned));
                                } else {


                                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                                    builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELETED);
                                    batchOperations.add(builder.build());


                                    try {

                                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                                        onDataChange();


                                    } catch (RemoteException | OperationApplicationException e) {


                                    } finally {
                                        onDataChange();
                                    }
                                }

                            }
                        });
                        alert.create().show(); // btw show() creates and shows it..


                    }

                    @Override
                    public void onItemAceptedCall(long cursorID) {
                        mIDPickingOrderSelected = cursorID;


                        if (!exitenCUasignedtoPickingOrder()) {

                            onDialogAlert(getResources().getString(R.string.detete_picking_order_NO_Existen_CustomOrders_Asigned));
                            onDataChange();
                        } else {


                            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                            builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELIVERY);
                            batchOperations.add(builder.build());


                            try {

                                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                                onDataChange();


                            } catch (RemoteException | OperationApplicationException e) {


                            } finally {
                                onDataChange();
                                upDateWitget(getContext());
                            }

                        }

                    }

                    @Override
                    public void onDialogAlert(String message) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setMessage(message);

                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {


                            }
                        });
                        alert.create().show();
                        mCursorAdapterPickingOrder.notifyDataSetChanged();
                    }

                    @Override
                    public void sharePickingorder(PickingOrdersCursorAdapter.ViewHolder vh) {
                        sharePickingOrder(getContext(), vh.mPickingOrderNumber.getText().toString(), mTilePickingComent.getText().toString());
                    }
                }
        );


        recyclerViewPickingOrder.setAdapter(mCursorAdapterPickingOrder);



        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapterPickingOrder, SimpleItemTouchHelperCallback.PICKING);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewPickingOrder);


        View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
//        emptyViewTotalProducts.setVisibility(View.GONE);
        RecyclerView recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);
        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        mCursorAdapterTotalProductos = new PickingOrderProductsAdapter(getContext(), null,
                emptyViewTotalProducts,
                new PickingOrderProductsAdapter.ProductCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, PickingOrderProductsAdapter.ViewHolder vh) {

                        showDialogNumberPicker(vh);

                    }


                }
        );


        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);
//        recyclerViewTotalProductos.setVisibility(View.GONE);

        RecyclerView recyclerViewCustomOrderInPickingOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);

        recyclerViewCustomOrderInPickingOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        View emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_custom_empty);
//        emptyViewCustomOrder.setVisibility(View.GONE);
        mCustomsOrdersCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyViewCustomOrder, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {


                if (mTwoPane) {
                    Bundle arguments = new Bundle();

//                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);

                    arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);

                    CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();
                    fragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.customorder_detail_container, fragment)
                            .commit();


                } else {
                    Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                    intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
//                    intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, id);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                        Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
                        ActivityOptionsCompat activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
                        startActivity(intent, activityOptions.toBundle());

                    } else {

                        startActivity(intent);
                    }

                }


            }

            @Override
            public void onMakeACall(String ContactID) {
                makeTheCall(getActivity(), ContactID);
            }

            @Override
            public void onDialogAlert(String message) {

            }

            @Override
            public void onItemDismissCall(long cursorID) {
                mCustomOrderIdSelected = cursorID;

                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderListFragment.ORDER_STATUS_INICIAL);
                builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, null);
                batchOperations.add(builder.build());


                try {
                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);


                } catch (RemoteException | OperationApplicationException e) {


                } finally {
                    onDataChange();
                }

            }

            @Override
            public void onItemAceptedCall(long cursorID) {
                mCustomOrderIdSelected = cursorID;

            }

            @Override
            public void onDataChange() {
                getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, PickingListFragment.this);
                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, PickingListFragment.this);
                getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, PickingListFragment.this);
                mCursorAdapterTotalProductos.notifyDataSetChanged();
                mCursorAdapterPickingOrder.notifyDataSetChanged();
                mCursorAdapterTotalProductos.notifyDataSetChanged();
                upDateWitget(getContext());
            }
        }

        );



        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallbackPickingCustomOrder(mCustomsOrdersCursorAdapter);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInPickingOrder);

        recyclerViewCustomOrderInPickingOrder.setAdapter(mCustomsOrdersCursorAdapter);
//        recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
//        emptyViewCustomOrder.setVisibility(View.GONE);

        mLinearOrders.setVisibility(View.GONE);
        mLinearProductos.setVisibility(View.GONE);

        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
    }

    public boolean exitenCUasignedtoPickingOrder() {


        String select = "((" + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " NOTNULL) AND ("
                + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " =?))";


        String projection[] = {CustomOrdersColumns.ID_CUSTOM_ORDER};
        String arg[] = {String.valueOf(mIDPickingOrderSelected)};


        Cursor c = getActivity().getContentResolver().query(LogisticaProvider.CustomOrders.CONTENT_URI,
                null,
                select,
                arg,
                null);
        return !(c == null || c.getCount() == 0);
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

        getLoaderManager().initLoader(PICKING_ORDER_LOADER, null, this);
        getLoaderManager().initLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        switch (id) {

            case CUSTOM_ORDER_LOADER:

                String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM,
                        LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER
                };


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.ShowJoin.CONTENT_URI,
                        proyection,
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + "=" + 1
                                + " and " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected(),
                        null,
                        null);

            case PICKING_ORDER_LOADER:


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.PickingOrders.CONTENT_URI,
                        null,
                        LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PICKING_STATUS_INICIAL,
                        null,
                        null);

            case PICKING_LOADER_TOTAL_PRODUCTOS:


                String select[] = {
/* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
/* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
/* 2 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
/* 3 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
/* 4 */            "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
/* 5 */            LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
/* 6 */           "max ( " + LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL + " )",
/* 7 */           "sum ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL + " )",
/* 8 */            "max ( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL + " )"};


                String where =
                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected();


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.join_customorderDetail_Product_Customer_picking.CONTENT_URI,
                        select,
                        where,
                        null,
                        null);

            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {

            case CUSTOM_ORDER_LOADER: {

                mCustomsOrdersCursorAdapter.swapCursor(data);

            }
            break;
            case PICKING_ORDER_LOADER:

            {
                mCursorAdapterPickingOrder.swapCursor(data);
            }
            break;


            case PICKING_LOADER_TOTAL_PRODUCTOS:


                if (data != null && data.moveToFirst()) {

                    do {


                        if (data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PICKING) > 0) {


                        } else {
                            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                            ContentProviderOperation.Builder builder;
                            builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrdersDetail.CONTENT_URI);
                            builder.withValue(PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL, MainActivity.getmPickingOrderSelected());
                            builder.withValue(PickingOrdersDetailColumns.REF_PRODUCT_PICKING_ORDERS_DETAIL, data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PRODUCTO));
                            builder.withValue(PickingOrdersDetailColumns.PRODUCT_NAME_PICKING_ORDERS_DETAIL, data.getString(PickingOrderProductsAdapter.COLUMN_NOMBRE_PRODUCTO));
                            builder.withValue(PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL, data.getInt(PickingOrderProductsAdapter.COLUMN_QTOTAL_ORDENES));
                            batchOperations.add(builder.build());

                            try {

                                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                            } catch (RemoteException | OperationApplicationException e) {

                            } finally {
                                mCursorAdapterTotalProductos.notifyDataSetChanged();
                                getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
                            }
                        }

                    } while (data.moveToNext());
                }
                if (data != null && data.moveToFirst()) {
                    mCursorAdapterTotalProductos.swapCursor(data);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapterPickingOrder.swapCursor(null);
        mCursorAdapterTotalProductos.swapCursor(null);
        mCustomsOrdersCursorAdapter.swapCursor(null);
    }


    public void saveNewPickingOrder() {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrders.CONTENT_URI);
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        String formattedDate = df.format(new Date());
        builder.withValue(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS, formattedDate);


        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

        }
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

    public void showDialogNumberPicker(final PickingOrderProductsAdapter.ViewHolder vh) {

        {

            final Dialog d = new Dialog(getContext());
            d.setTitle(getResources().getString(R.string.NumberPicker));
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(getResources().getInteger(R.integer.MaxPickerNumber));
            np.setMinValue(getResources().getInteger(R.integer.MinPickerNumber));
            np.setValue(Integer.parseInt(vh.mTextcantidadPicking.getText().toString()));
            np.setWrapSelectorWheel(true);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    vh.mTextcantidadPicking.setText(String.valueOf(np.getValue()));
                    saveCantidadPicking(vh, String.valueOf(np.getValue()));

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


            getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, getString(R.string.InformeError), e);
        }


    }
}
