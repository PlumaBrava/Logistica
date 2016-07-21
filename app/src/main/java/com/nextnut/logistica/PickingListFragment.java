package com.nextnut.logistica;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.rest.CustomsOrdersCursorAdapter;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;

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

    private static final int  CUSTOM_ORDER_LOADER=0;

    private CustomsOrdersCursorAdapter mCursorAdapter;
    private RecyclerView recyclerView;

    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;
    private static final int CURSOR_LOADER_ID = 0;
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(LOG_TAG, "onHiddenChanged");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(LOG_TAG, "setUserVisibleHint" + isVisibleToUser);
        if(isVisibleToUser){
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
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
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
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
        View emptyView = rootView.findViewById(R.id.recyclerview_custom_empty);
        recyclerView =(RecyclerView) rootView.findViewById(R.id.customorder_list);

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
                        }
                ,CustomsOrdersCursorAdapter.STEP_PICKING, new CustomsOrdersCursorAdapter.CustomsOrdersCursorAdapteronDataChangekHandler() {
            @Override
            public void onDataChange() {
                Log.i("ProductListActivity", "CustomsOrdersCursorAdapteronDataChangekHandler");
                getLoaderManager().restartLoader(CUSTOM_ORDER_LOADER, null, PickingListFragment.this);
            }}


        );





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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated");
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
//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");

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
        String proyection[] = {LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER ,
                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
                LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
                LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
                LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM
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
                LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.STATUS_CUSTOM_ORDER +"="+1,
                null,
                null);




    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "onLoadFinished");
        if (data != null && data.moveToFirst()){
//            Log.i(LOG_TAG,"ID:"+ data.getInt(0));
//            Log.i(LOG_TAG,"date:"+ data.getString(1));
//            Log.i(LOG_TAG,"price:"+ data.getLong(2));
//            Log.i(LOG_TAG,"Name:"+ data.getString(3));
//            Log.i(LOG_TAG,"LastName:"+ data.getString(4));
//
//            Log.i(LOG_TAG, "swapCursor");
            mCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG, "swapCursor");
        mCursorAdapter.swapCursor(null);
    }


}
