package com.nextnut.logistica;

import android.animation.ObjectAnimator;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
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

import static com.nextnut.logistica.util.MakeCall.makeTheCall;
import static com.nextnut.logistica.widget.LogisticaWidget.upDateWitget;

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
    private static final String M_ITEM = "mItem";


    private CustomsOrdersCursorAdapter mOrdersAdapter;
    private OrderDetailCursorAdapter mCursorAdapterTotalProductos;
    private RecyclerView recyclerViewOrders;
    private RecyclerView recyclerViewTotalProductos;


    private long mItem = 0;
    private long mCustomOrderIdSelected;


    public static final int ORDER_STATUS_INICIAL = 0;
    public static final int ORDER_STATUS_PICKING = 1;
    public static final int ORDER_STATUS_DELIVERED = 2;
    public static final int ORDER_STATUS_DELETED = 3;


    private boolean mTwoPane;

    private static final String LOG_TAG = CustomOrderListFragment.class.getSimpleName();


    public static final float LARGE_SCALE = 1.5f;
    private boolean symmetric = true;
    private boolean small = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mItem = savedInstanceState.getLong(M_ITEM);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.customorder_list_fragment, container, false);


        View emptyViewTotalProducts = rootView.findViewById(R.id.recyclerview_totalproduct_empty);
        recyclerViewTotalProductos = (RecyclerView) rootView.findViewById(R.id.total_products_customOrder);

        recyclerViewTotalProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        mCursorAdapterTotalProductos = new OrderDetailCursorAdapter(getContext(), null,
                emptyViewTotalProducts,
                new OrderDetailCursorAdapter.ProductCursorAdapterOnClickHandler() {
                    @Override
                    public void onClick(long id, OrderDetailCursorAdapter.ViewHolder v) {
                        Intent intent = new Intent(getContext(), ProductosEnOrdenes.class);
                        intent.putExtra(ProductosEnOrdenes.ARG_ITEM_ID, id);
                        startActivity(intent);
                    }

                    @Override
                    public void onFavorite(long id, OrderDetailCursorAdapter.ViewHolder vh) {
                    }

                    @Override
                    public void onProductDismiss(long id) {

                    }
                }
        );

        mCursorAdapterTotalProductos.resetFavoriteVisible();
        recyclerViewTotalProductos.setAdapter(mCursorAdapterTotalProductos);


        View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        recyclerViewOrders = (RecyclerView) rootView.findViewById(R.id.customorder_list);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(recyclerViewOrders.getContext()));


        mOrdersAdapter = new CustomsOrdersCursorAdapter(getContext(), null, emptyView, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, CustomsOrdersCursorAdapter.ViewHolder vh) {
                mItem = id;
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


                AlertDialog.Builder alert;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(getContext());
                }
                alert.setMessage(message);
                alert.create().show();
                alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            }

            @Override
            public void onItemDismissCall(long cursorID) {
                mCustomOrderIdSelected = cursorID;

                AlertDialog.Builder alert;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(getContext());
                }

                alert.setMessage(getString(R.string.doYouWantDelete));
                alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDataChange();

                        dialog.cancel();
                    }
                });
                alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(2);

                        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withRefCustomOrder(mCustomOrderIdSelected));
                        ContentProviderOperation.Builder builder1 = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                        batchOperations.add(builder.build());
                        batchOperations.add(builder1.build());

                        try {

                            getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                    notifyItemRemoved(position);
                        } catch (RemoteException | OperationApplicationException e) {

                        } finally {
                            onDataChange();
                        }
                    }
                });
                alert.create().show(); // btw show() creates and shows it..


            }

            @Override
            public void onItemAceptedCall(long cursorID) {
                mCustomOrderIdSelected = cursorID;

                if (MainActivity.mPickingOrderSelected == 0) {

                    onDialogAlert(getResources().getString(R.string.selectPickingOrderToAssing));
                    onDataChange();
                } else {
                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(mCustomOrderIdSelected));
                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, ORDER_STATUS_PICKING);
                    SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                    String formattedDate = df.format(new Date());
                    builder.withValue(CustomOrdersColumns.DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER, formattedDate);
                    builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, MainActivity.getmPickingOrderSelected());
                    batchOperations.add(builder.build());
                    try {
                        getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                        onDataChange();
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.e(getString(R.string.InformeError), getString(R.string.InformeErrorApplyingBatchInsert), e);

                    }
                }
            }

            @Override
            public void onDataChange() {
                getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, CustomOrderListFragment.this);
                getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
                upDateWitget(getContext());

            }
        }
        );


        recyclerViewOrders.setAdapter(mOrdersAdapter);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mOrdersAdapter, SimpleItemTouchHelperCallback.ORDER_INICIAL);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(M_ITEM, mItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
        getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mOrdersAdapter != null) {
            getLoaderManager().restartLoader(CUSTOM_LOADER_LIST, null, this);
            getLoaderManager().restartLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, CustomOrderListFragment.this);

        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CUSTOM_LOADER_LIST, null, this);
        getLoaderManager().initLoader(CUSTOM_LOADER_TOTAL_PRODUCTOS, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case CUSTOM_LOADER_LIST:
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
/* 9 */             LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER,
/* 10 */            LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." + CustomOrdersDetailColumns.QUANTITY_DELIVER_CUSTOM_ORDER_DETAIL

                };

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
                if (data != null && data.moveToFirst()) {
                    mOrdersAdapter.swapCursor(data);
                    if (mTwoPane) {

                    }
                }


                break;


            case CUSTOM_LOADER_TOTAL_PRODUCTOS:
                if (data != null && data.moveToFirst()) {
                    mCursorAdapterTotalProductos.swapCursor(data);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOrdersAdapter.swapCursor(null);
    }


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


}

