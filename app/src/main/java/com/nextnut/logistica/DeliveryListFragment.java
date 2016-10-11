package com.nextnut.logistica;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallbackDeleveyCustomOrder;
import com.nextnut.logistica.util.CurrencyToDouble;
import com.nextnut.logistica.util.DialogAlerta;

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
public class DeliveryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ITEM_ID = "item_id";

    private long mCustomOrderIdSelected;

    private long mIDPickingOrderSelected;

    private PickingOrdersCursorAdapter mPickinOrdersAdapter;
    private PickingOrderProductsAdapter mCursorAdapterTotalProductos;
    private CustomsOrdersCursorAdapter mCustomsOrdersCursorAdapter;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewTotalProductos;
    private RecyclerView recyclerViewCustomOrderInDeliveyOrder;
    private CardView mDeliveryOrderTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private TextView mCreationDate;

    //    private PickingOrdersHandler  mPickingOrdersHandler;

    private FloatingActionButton fab_new;
    private FloatingActionButton fab_delete;
    private static final int CUSTOM_ORDER_LOADER = 0;
    private static final int PICKING_ORDER_LOADER = 1;
    private static final int PICKING_LOADER_TOTAL_PRODUCTOS = 2;
    private int mItem = 0;
//    /**
//     * The dummy content this fragment is presenting.
//     */
//    private DummyContent.DummyItem mItem;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = DeliveryListFragment.class.getSimpleName();


//    public void setPickingOrdersHandler(PickingOrdersHandler pOH){
//        mPickingOrdersHandler= pOH;
//    }

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
    public void onResume() {
        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, this);
        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);

        View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.pickingOrder_list);
        mDeliveryOrderTile = (CardView) rootView.findViewById(R.id.deliveryOrderNumbertitleID);
        mDeliveryOrderTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDPickingOrderSelected = 0;
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

        recyclerView.setLayoutManager(layoutManager);


        mPickinOrdersAdapter = new PickingOrdersCursorAdapter(
                getContext(),
                null,
                emptyView,
                new PickingOrdersCursorAdapter.PinckingOrdersCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, PickingOrdersCursorAdapter.ViewHolder vh) {
                        mTilePickingComent.setText(vh.mpickingOrderComents.getText());
                        mTilePickingOrderNumber.setText(vh.mPickingOrderNumber.getText());
                        mTilePickingComent.setVisibility(View.VISIBLE);
                        mCreationDate.setText(vh.mCreationDate.getText());
                        mIDPickingOrderSelected = id;
                        mPickinOrdersAdapter.notifyDataSetChanged();
                        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, DeliveryListFragment.this);
                        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, DeliveryListFragment.this);
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.VISIBLE);
                        recyclerViewTotalProductos.setVisibility(View.VISIBLE);
                        mDeliveryOrderTile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDataChange() {
                        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, DeliveryListFragment.this);
                        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, DeliveryListFragment.this);
                        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, DeliveryListFragment.this);
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        mPickinOrdersAdapter.notifyDataSetChanged();
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemDismissCall(long cursorID) {
                        mIDPickingOrderSelected = cursorID;
                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PickingListFragment.PICKING_STATUS_INICIAL);
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

                    @Override
                    public void onItemAceptedCall(long cursorID) {

                        mIDPickingOrderSelected = cursorID;
                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PickingListFragment.PICKING_STATUS_CERRADA);
                        batchOperations.add(builder.build());


                        try {

                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                            onDataChange();


                        } catch (RemoteException | OperationApplicationException e) {

                        } finally {
                            onDataChange();
                        }


                    }

                    @Override
                    public void onDialogAlert(String message) {
                        DialogAlerta dFragment = DialogAlerta.newInstance(message);
                        dFragment.show(getFragmentManager(), "Dialog Fragment");
                    }

                    @Override
                    public void sharePickingorder(PickingOrdersCursorAdapter.ViewHolder vh) {
                        sharePickingOrder(getContext(), vh.mPickingOrderNumber.getText().toString(), mTilePickingComent.getText().toString());
                    }
                }
        );


        recyclerView.setAdapter(mPickinOrdersAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPickinOrdersAdapter, SimpleItemTouchHelperCallback.DELIVERY);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        FloatingActionButton fab_save = (FloatingActionButton) mDeliveryOrderTile.findViewById(R.id.fab_save_picking);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDPickingOrderSelected = 0;
                mDeliveryOrderTile.setVisibility(View.GONE);
                recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);
                recyclerViewTotalProductos.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        });

        View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));


        mCursorAdapterTotalProductos = new PickingOrderProductsAdapter(getContext(), null,
                emptyViewTotalProducts,
                new PickingOrderProductsAdapter.ProductCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, PickingOrderProductsAdapter.ViewHolder vh) {

                    }
                }
        );


        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);
        recyclerViewTotalProductos.setVisibility(View.GONE);


        recyclerViewCustomOrderInDeliveyOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);

        recyclerViewCustomOrderInDeliveyOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        mCustomsOrdersCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {

                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, id);
                    arguments.putInt(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_SELECTION);
                    CustomOrderDetailFragment fragment = new CustomOrderDetailFragment();
                    fragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.customorder_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                    intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.ACTION_CUSTOM_ORDER_DELIVERY);
                    intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, id);


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


            }

            @Override
            public void onItemAceptedCall(long cursorID) {
                mCustomOrderIdSelected = cursorID;
                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderListFragment.ORDER_STATUS_DELIVERED);
                batchOperations.add(builder.build());
                try {
                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                } catch (RemoteException | OperationApplicationException e) {

                } finally {
                    onDataChange();
                }


            }

            @Override
            public void onDataChange() {
                getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, DeliveryListFragment.this);
                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, DeliveryListFragment.this);
                getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, DeliveryListFragment.this);
                mCursorAdapterTotalProductos.notifyDataSetChanged();
                mPickinOrdersAdapter.notifyDataSetChanged();
                mCursorAdapterTotalProductos.notifyDataSetChanged();
            }
        }

        );

        recyclerViewCustomOrderInDeliveyOrder.setAdapter(mCustomsOrdersCursorAdapter);

        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallbackDeleveyCustomOrder(mCustomsOrdersCursorAdapter);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInDeliveyOrder);
        recyclerViewCustomOrderInDeliveyOrder.setVisibility(View.GONE);

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
                        " ( " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderListFragment.ORDER_STATUS_PICKING
                                + " or " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderListFragment.ORDER_STATUS_DELIVERED + " ) "
                                + " and " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + mIDPickingOrderSelected,
                        null,
                        null);

            case PICKING_ORDER_LOADER:


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.PickingOrders.CONTENT_URI,
                        null,
                        LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PickingListFragment.PICKING_STATUS_DELIVERY +
                                " OR " +
                                LogisticaDataBase.PICKING_ORDERS + "." + PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " + PickingListFragment.PICKING_STATUS_CERRADA,
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

                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + mIDPickingOrderSelected;


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

            case CUSTOM_ORDER_LOADER:
                if (data != null && data.moveToFirst()) {
                    mCustomsOrdersCursorAdapter.swapCursor(data);

                }
                break;
            case PICKING_ORDER_LOADER:
                if (data != null && data.moveToFirst()) {
                    mPickinOrdersAdapter.swapCursor(data);
                }
                break;


            case PICKING_LOADER_TOTAL_PRODUCTOS:
                if (data != null && data.moveToFirst()) {
                    mCursorAdapterTotalProductos.swapCursor(data);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPickinOrdersAdapter.swapCursor(null);
        mCursorAdapterTotalProductos.swapCursor(null);
        mCustomsOrdersCursorAdapter.swapCursor(null);

    }


    public void saveNewPickingOrder() {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrders.CONTENT_URI);
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        String formattedDate = df.format(new Date());
        builder.withValue(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS, formattedDate);
        builder.withValue(PickingOrdersColumns.COMMENTS_PICKING_ORDERS, getString(R.string.NewOrder));
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
            d.setTitle(getString(R.string.NumberPicker));
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(getResources().getInteger(R.integer.MaxPickerNumber));
            np.setMinValue(getResources().getInteger(R.integer.MinPickerNumber));
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
                    CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
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
}
