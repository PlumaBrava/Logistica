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
import android.support.design.widget.FloatingActionButton;
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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.nextnut.logistica.Util.CurrencyToDouble;
import com.nextnut.logistica.Util.DialogAlerta;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.data.PickingOrdersDetailColumns;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.rest.CustomsOrdersCursorAdapter;
import com.nextnut.logistica.rest.PickingOrdersCursorAdapter;
import com.nextnut.logistica.rest.PickingOrderProductsAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallbackPickingCustomOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nextnut.logistica.Util.MakeCall.makeTheCall;
import static com.nextnut.logistica.Util.SharePickingOrder.sharePickingOrder;
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

    public static final String ARG_ITEM_ID = "item_id";

    private long mCustomOrderIdSelected;

    private long mIDPickingOrderSelected;

    private PickingOrdersCursorAdapter mCursorAdapterPickingOrder;
    private PickingOrderProductsAdapter mCursorAdapterTotalProductos;
    private CustomsOrdersCursorAdapter mCustomsOrdersCursorAdapter;

    private RecyclerView recyclerViewPickingOrder;
    private RecyclerView recyclerViewTotalProductos;
    private RecyclerView recyclerViewCustomOrderInPickingOrder;
    private CardView mPickingOrderTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private TextView mCreationDate;

    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper mItemTouchHelperCustomOrder;
    private PickingOrdersHandler  mPickingOrdersHandler;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save_picking;
    private FloatingActionButton fab_delete;
    private static final int CUSTOM_ORDER_LOADER = 0;
    private static final int PICKING_ORDER_LOADER = 1;
    private static final int PICKING_LOADER_TOTAL_PRODUCTOS = 2;

    public static final int PICKING_STATUS_INICIAL = 0;
    public static final int PICKING_STATUS_DELIVERY = 1;
    public static final int PICKING_STATUS_CERRADA = 2;
    public static final int PICKING_STATUS_DELETED = 3;

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

    private static final String LOG_TAG = PickingListFragment.class.getSimpleName();


    public void setPickingOrdersHandler(PickingOrdersHandler pOH){
        mPickingOrdersHandler= pOH;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mPickingOrdersHandler = (PickingOrdersHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(LOG_TAG, "onHiddenChanged");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(LOG_TAG, "setUserVisibleHint" + isVisibleToUser);
        if(isVisibleToUser){
            getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, this);
            getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
            getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
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
    public void onResume() {
        Log.i(LOG_TAG, "onResume()");
        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, this);
        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, this);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.picking_list_fragment, container, false);

        View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        recyclerViewPickingOrder =(RecyclerView) rootView.findViewById(R.id.pickingOrder_list);
        mPickingOrderTile = (CardView) rootView.findViewById(R.id.pickingNumbertitleID);

        fab_save_picking = (FloatingActionButton) mPickingOrderTile.findViewById(R.id.fab_save_picking);
        fab_save_picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePickingOrder(MainActivity.mPickingOrderSelected);
                mPickingOrdersHandler.onPickingOrderSelected(0);
                mPickingOrderTile.setVisibility(View.GONE);
                recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);
                recyclerViewTotalProductos.setVisibility(View.GONE);
                recyclerViewPickingOrder.setVisibility(View.VISIBLE);

            }
        });


        mPickingOrderTile.setVisibility(View.GONE);
        mTilePickingOrderNumber= (TextView) mPickingOrderTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent=(EditText) mPickingOrderTile.findViewById(R.id.TitlepickingOrderComents);
        mCreationDate=(TextView) mPickingOrderTile.findViewById(R.id.titlePicckinOder_creationdate);

        recyclerViewPickingOrder.setLayoutManager(layoutManager);



        mCursorAdapterPickingOrder = new PickingOrdersCursorAdapter(
                getContext(),
                null,
                emptyView,
                new PickingOrdersCursorAdapter.PinckingOrdersCursorAdapterOnClickHandler() {
                @Override
                    public void onClick(long id, PickingOrdersCursorAdapter.ViewHolder vh) {
                         Log.i(LOG_TAG, "onclicka:" + id);
                    if(mPickingOrdersHandler!=null){

                        mTilePickingComent.setText(vh.mpickingOrderComents.getText());
                        mTilePickingOrderNumber.setText(vh.mPickingOrderNumber.getText());
                        mTilePickingComent.setVisibility(View.VISIBLE);
                        mCreationDate.setText(vh.mCreationDate.getText());
                         mPickingOrdersHandler.onPickingOrderSelected(id);


                        mCursorAdapterPickingOrder.notifyDataSetChanged();
                        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null,PickingListFragment.this);
                        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null,PickingListFragment.this);
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerViewPickingOrder.setVisibility(View.GONE);
                        recyclerViewCustomOrderInPickingOrder.setVisibility(View.VISIBLE);
                        recyclerViewTotalProductos.setVisibility(View.VISIBLE);
                        mPickingOrderTile.setVisibility(View.VISIBLE);

                }}

                    @Override
                    public void onDataChange() {
                        Log.i("ProductListActivity", "CustomsOrdersCursorAdapteronDataChangekHandler");
                        getLoaderManager().restartLoader(PICKING_ORDER_LOADER, null, PickingListFragment.this);
                        getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, PickingListFragment.this);
                        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, PickingListFragment.this);
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        mCursorAdapterPickingOrder.notifyDataSetChanged();
                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemDismissCall(long cursorID) {
                        mIDPickingOrderSelected=cursorID;
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setMessage(getContext().getResources().getString(R.string.detete_picking_order));
                        alert.setNegativeButton(getContext().getResources().getString(R.string.detete_picking_order_cancel),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.i("YesNoDialog:", "setNegativeButton" );
                                onDataChange();
                                dialog.cancel();
                            }
                        });
                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.i("YesNoDialog:", "setPositiveButton picking ");



                                if(exitenCUasignedtoPickingOrder()){
                                    Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder verdadero");



                                    onDialogAlert(getResources().getString(R.string.detete_picking_order_Existen_CustomOrders_Asigned));
                                }else
                                {
                                    Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder falso");

                                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                                builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELETED);
                                batchOperations.add(builder.build());


                                try {

                                    getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                                   onDataChange();


                                } catch (RemoteException | OperationApplicationException e) {
                                    Log.e("TouchHelper:", "Error applying batch insert", e);

                                }finally {
                                    onDataChange();
                                }
                            }

                            }
                        });
                        alert.create().show(); // btw show() creates and shows it..


                    }

                    @Override
                    public void onItemAceptedCall(long cursorID) {
                        mIDPickingOrderSelected=cursorID;
//                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//
//                        alert.setMessage(getContext().getResources().getString(R.string.detete_picking_order));
//                        alert.setNegativeButton(getContext().getResources().getString(R.string.detete_picking_order_cancel),new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                Log.i("YesNoDialog:", "setNegativeButton" );
//                                onDataChange();
//                                dialog.cancel();
//                            }
//                        });
//                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.i("YesNoDialog:", "setPositiveButton picking  ");



                                if(!exitenCUasignedtoPickingOrder()){
                                    Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder verdadero");
                                    onDialogAlert(getResources().getString(R.string.detete_picking_order_NO_Existen_CustomOrders_Asigned));
                                onDataChange();
                                }else {
                                    Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder falso");

                                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(mIDPickingOrderSelected));
                                    builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, PICKING_STATUS_DELIVERY);
                                    batchOperations.add(builder.build());


                                    try {

                                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                                        onDataChange();


                                    } catch (RemoteException | OperationApplicationException e) {
                                        Log.e("TouchHelper:", "Error applying batch insert", e);

                                    } finally {
                                        onDataChange();
                                        upDateWitget (getContext());
                                    }

                                }

                        }
//                        alert.create().show(); // btw show() creates and shows it..




                    @Override
                    public void onDialogAlert(String message) {
                        Log.i("YesNoDialog:", "onDialogAlert ");
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setMessage(message);

                        alert.setPositiveButton(getContext().getResources().getString(R.string.detete_picking_order_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.i("YesNoDialog:", "setPositiveButton picking ");

//                                        DialogAlerta dFragment = DialogAlerta.newInstance(message);
//                        dFragment.show(getFragmentManager(), "Dialog Fragment");
                            }
                        });
                        alert.create().show();
                        mCursorAdapterPickingOrder.notifyDataSetChanged();
                    }

                    @Override
                    public void sharePickingorder(PickingOrdersCursorAdapter.ViewHolder vh) {
                        sharePickingOrder(getContext(),  vh.mPickingOrderNumber.getText().toString(),mTilePickingComent.getText().toString());
                    }
                }
            );





        recyclerViewPickingOrder.setAdapter(mCursorAdapterPickingOrder);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapterPickingOrder);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewPickingOrder);

        View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_pickingOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        mCursorAdapterTotalProductos= new PickingOrderProductsAdapter(getContext(), null,
                emptyViewTotalProducts,
                new PickingOrderProductsAdapter.ProductCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, PickingOrderProductsAdapter.ViewHolder vh) {

                            Log.i(LOG_TAG, "setupRecyclerView" + id);
                            showDialogNumberPicker( vh);

                    }



                                    }
        );

//        mCursorAdapterTotalProductos.resetFavoriteVisible();
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);
        recyclerViewTotalProductos.setVisibility(View.GONE);

//////////////




        recyclerViewCustomOrderInPickingOrder = (RecyclerView) rootView.findViewById(R.id.customOrderInpickingOrder_list);

        recyclerViewCustomOrderInPickingOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        mCustomsOrdersCursorAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
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
                makeTheCall(getActivity(),ContactID);
            }

            @Override
            public void onDialogAlert(String message) {

            }

            @Override
            public void onItemDismissCall(long cursorID) {
                mCustomOrderIdSelected=cursorID;
                Log.i("TouchHelper:", "Adapter onItemDismiss PICKIG --" );

                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderListFragment.ORDER_STATUS_INICIAL);
                builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, null);
                batchOperations.add(builder.build());


                try {
                    getContext(). getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);



                } catch (RemoteException | OperationApplicationException e) {
                    Log.e("TouchHelper:", "Error applying batch insert", e);

                }finally {
                    onDataChange();
                }

            }

            @Override
            public void onItemAceptedCall(long cursorID) {
                mCustomOrderIdSelected=cursorID;
                Log.i("TouchHelper:", "Adapter onItemDismiss PICKIG --" );

                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderListFragment.ORDER_STATUS_DELETED);
                builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, null);
                batchOperations.add(builder.build());


                try {
                    getContext(). getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);



                } catch (RemoteException | OperationApplicationException e) {
                    Log.e("TouchHelper:", "Error applying batch insert", e);

                }finally {
                    onDataChange();
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
                upDateWitget (getContext());
            }
        }

        );

        recyclerViewCustomOrderInPickingOrder.setAdapter(mCustomsOrdersCursorAdapter);

        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallbackPickingCustomOrder(mCustomsOrdersCursorAdapter);
        mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderInPickingOrder);
        recyclerViewCustomOrderInPickingOrder.setVisibility(View.GONE);

        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
    }

    public boolean exitenCUasignedtoPickingOrder(){
        Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder mIDPickingOrderSelected "+mIDPickingOrderSelected);


        String select = "((" + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER+ " NOTNULL) AND ("
                + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " =?))";

//
//        String projection []= {ProductsColumns.NOMBRE_PRODUCTO, "sum("+ ProductsColumns.PRECIO_PRODUCTO
//                + " * " + ProductsColumns.PRECIO_PRODUCTO
//                +")"};
        String projection[] = {CustomOrdersColumns.ID_CUSTOM_ORDER};
        String arg[] = {String.valueOf(mIDPickingOrderSelected)};


        Cursor c = getActivity().getContentResolver().query(LogisticaProvider.CustomOrders.CONTENT_URI,
                null,
                select,
                arg,
                null);
        if (c == null || c.getCount() == 0) {
            Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder true ");
            return false;
        }

            else {
            Log.i("YesNoDialog:", "exitenCUasignedtoPickingOrder falso ");
            return true;
        }
    }

    public static interface PickingOrdersHandler {
        void onPickingOrderSelected(long pickingOrderID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated");
        getLoaderManager().initLoader(PICKING_ORDER_LOADER, null, this);
        getLoaderManager().initLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
        getLoaderManager().initLoader(CUSTOM_ORDER_LOADER, null, this);

    super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onViewStateRestored");
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
//
//    private void setupRecyclerView(@NonNull RecyclerView recyclerViewPickingOrder) {
//        recyclerViewPickingOrder.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");

        switch(id){

            case CUSTOM_ORDER_LOADER :

        String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER ,
                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
                LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
                LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
                LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM,
                LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM,
                LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.STATUS_CUSTOM_ORDER
        };


        return new CursorLoader(
                getActivity(),
                LogisticaProvider.ShowJoin.CONTENT_URI,
                proyection,
                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.STATUS_CUSTOM_ORDER +"="+1
                        + " and " + LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected(),
                null,
                null);

            case PICKING_ORDER_LOADER:


                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.PickingOrders.CONTENT_URI,
                        null,
                        LogisticaDataBase.PICKING_ORDERS+"."+ PickingOrdersColumns.STATUS_PICKING_ORDERS + " = " +PICKING_STATUS_INICIAL,
                        null,
                        null);

            case PICKING_LOADER_TOTAL_PRODUCTOS:
//
//                String select[] = {
///* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
///* 1 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER,
///* 2 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER,
///* 3 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER,
///* 4 */             LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL,
///* 5 */             "sum( " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL + " )",
///* 6 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
///* 7 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
///* 8 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
///* 9 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
///* 10 */           "sum( " + LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL+ " )",
///* 11 */            "sum( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL+ " )",
///* 12 */            "sum( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL+ " )"};


                String select[] = {
/* 0 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
/* 1 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.NOMBRE_PRODUCTO,
/* 2 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.IMAGEN_PRODUCTO,
/* 3 */             LogisticaDataBase.PRODUCTS + "." + ProductsColumns.DESCRIPCION_PRODUCTO,
/* 4 */            "sum ( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL +" )",
///* 5 */           "sum( " + LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL+ " )",
/* 5 */            LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
///* 6 */            "sum( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL+ " )",
/* 6 */           "max ( "+ LogisticaDataBase.PICKING_ORDERS_DETAIL+"."+ PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL +" )",
/* 7 */           "sum ( "+  LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL +" )",
/* 8 */            "max ( "+ LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL+" )"};


                String where =
//                        LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.STATUS_CUSTOM_ORDER + " = " + CustomOrderDetailFragment.STATUS_ORDER_PICKING
//                        + " and " +
                                LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER + " = " + MainActivity.getmPickingOrderSelected()
//                                    +" and ( " +
//                                LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL + " = " + MainActivity.getmPickingOrderSelected() +
//                               " or " +  LogisticaDataBase.PICKING_ORDERS_DETAIL + "." + PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL + " is null )"
                        ;

//                String groupBy[]={LogisticaDataBase.PRODUCTS + "." + ProductsColumns._ID_PRODUCTO,
//                        LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER
//
//                };

                Log.i(LOG_TAG, "onCreateLoader");
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
        Log.i(LOG_TAG, "onLoadFinished");
        switch (loader.getId()) {

            case CUSTOM_ORDER_LOADER:
            if (data != null && data.moveToFirst()) {
//            Log.i(LOG_TAG,"ID:"+ data.getInt(0));
//            Log.i(LOG_TAG,"date:"+ data.getString(1));
//            Log.i(LOG_TAG,"price:"+ data.getLong(2));
//            Log.i(LOG_TAG,"Name:"+ data.getString(3));
//            Log.i(LOG_TAG,"LastName:"+ data.getString(4));
//
//            Log.i(LOG_TAG, "swapCursor");
                mCustomsOrdersCursorAdapter.swapCursor(data);

            }
                break;
            case PICKING_ORDER_LOADER:
                Log.i(LOG_TAG, "PICKING_ORDER_LOADER count: "+data.getCount());
                if (data != null && data.moveToFirst()) {
                mCursorAdapterPickingOrder.swapCursor(data);}
                break;


            case PICKING_LOADER_TOTAL_PRODUCTOS:
            Log.i(LOG_TAG, "c.count PICKING_ORDER_LOADER count: "+data.getCount());

                if (data != null && data.moveToFirst()) {
//                    Log.i(LOG_TAG, "c.count PICKING_DETAIL_id " + data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PICKING));
                    do {
                        Log.i(LOG_TAG, "c.count ref Product " + data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PRODUCTO));
                        Log.i(LOG_TAG, "c.count name:" + data.getString(PickingOrderProductsAdapter.COLUMN_NOMBRE_PRODUCTO));


                        Log.i(LOG_TAG, "c.count ref det : " + data.getDouble(PickingOrderProductsAdapter.COLUMN_OORDERS_COUNT));
                        Log.i(LOG_TAG, "c.count quantity : " + data.getDouble(PickingOrderProductsAdapter.COLUMN_QTOTAL_ORDENES));
                        Log.i(LOG_TAG, "c.count quantity delivey: " + data.getDouble(PickingOrderProductsAdapter.COLUMN_QTOTAL_DELIVERY));

                        Log.i(LOG_TAG, "c.count PICKING_DETAIL_id " + data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PICKING));
                        Log.i(LOG_TAG, "c.count quantity picking: " + data.getDouble(PickingOrderProductsAdapter.COLUMN_QTOTAL_PICKING));
                        Log.i(LOG_TAG, "c.count ------------------------------- ");


                        if (data.getDouble(PickingOrderProductsAdapter.COLUMN_ID_PICKING) > 0) {
                            Log.i(LOG_TAG, "c.count ref Updat");
//                            builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrdersDetail.withId(data.getLong(PickingOrderProductsAdapter.COLUMN_ID_PICKING)));

                        } else {

                            Log.i(LOG_TAG, "c.count ref NEW");



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
                            Log.e(LOG_TAG, "updatePickingOrder Error applying batch insert", e);
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
        Log.i(LOG_TAG, "swapCursor");
        mCursorAdapterPickingOrder.swapCursor(null);
        mCursorAdapterTotalProductos.swapCursor(null);
        mCustomsOrdersCursorAdapter.swapCursor(null);
    }


    public void saveNewPickingOrder() {
        Log.i(LOG_TAG, "save New");
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrders.CONTENT_URI);
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        String formattedDate = df.format(new Date());
        builder.withValue(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS, formattedDate);
        Log.i(LOG_TAG, "formattedDate:" + formattedDate);
//        builder.withValue(PickingOrdersColumns.COMMENTS_PICKING_ORDERS, "New Order");
        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    public void updatePickingOrder(long idPickingOrder) {
        Log.i(LOG_TAG, "updatePickingOrder: "+idPickingOrder);
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrders.withId(idPickingOrder));
        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        String formattedDate = df.format(new Date());
        builder.withValue(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS, formattedDate);
        Log.i(LOG_TAG, "formattedDate:" + formattedDate);

        builder.withValue(PickingOrdersColumns.COMMENTS_PICKING_ORDERS, mTilePickingComent.getText().toString());

        builder.withValue(PickingOrdersColumns.STATUS_PICKING_ORDERS, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

        batchOperations.add(builder.build());
        try {

            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "updatePickingOrder Error applying batch insert", e);
        }
    }

    public void showDialogNumberPicker(final PickingOrderProductsAdapter.ViewHolder vh){

        {

            final Dialog d = new Dialog(getContext());
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.dialog_number_picker);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(5000);
            np.setMinValue(1);
            np.setValue(Integer.parseInt(vh.mTextcantidadPicking.getText().toString()));
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

                    vh.mTextcantidadPicking.setText(String.valueOf(np.getValue()));
//                    CurrencyToDouble price = new CurrencyToDouble(vh.mTextViewPrecio.getText().toString());
                    saveCantidadPicking(vh, String.valueOf(np.getValue()));

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

    public void saveCantidadPicking(PickingOrderProductsAdapter.ViewHolder vh, String cantidad)
    {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (vh.mRefPickingDetail != 0) {

            Log.i(LOG_TAG, "c.count: mRefPickingDetail ! null: " + vh.mRefPickingDetail );
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.PickingOrdersDetail.withId(vh.mRefPickingDetail));
            builder.withValue(PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL, cantidad);

            batchOperations.add(builder.build());
        } else {

            Log.i(LOG_TAG, " c.count: mRefPickingDetail null");
            double q = Double.valueOf(cantidad);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.PickingOrdersDetail.CONTENT_URI);

            builder.withValue(PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL, MainActivity.getmPickingOrderSelected());
            builder.withValue(PickingOrdersDetailColumns.REF_PRODUCT_PICKING_ORDERS_DETAIL, vh.mRefProduct);
            builder.withValue(PickingOrdersDetailColumns.PRODUCT_NAME_PICKING_ORDERS_DETAIL, vh.mTextViewNombre.getText().toString());
            builder.withValue(PickingOrdersDetailColumns.QUANTITY_PICKING_ORDERS_DETAIL, cantidad);

            Log.i(LOG_TAG, " c.count: SaveProductoPickingOrderSelected(" +MainActivity.getmPickingOrderSelected());
            Log.i(LOG_TAG, " c.count: SaveProducto" +vh.mRefProduct);
            Log.i(LOG_TAG, " c.count: SaveName " +vh.mTextViewNombre.getText().toString());
            Log.i(LOG_TAG, " c.count: SaveCantidad " + cantidad);




            batchOperations.add(builder.build());
        }


        try {

        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);



        getLoaderManager().restartLoader(PICKING_LOADER_TOTAL_PRODUCTOS, null, this);
    } catch (RemoteException | OperationApplicationException e) {
        Log.e(LOG_TAG, "mRefPickingDetail c.count", e);
    }


    }
}
