package com.nextnut.logistica;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.rest.CustomsCursorAdapter;

/**
 * An activity representing a list of Customs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CustomListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomsCursorAdapter mCursorAdapter;

    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton fab_new;
    private FloatingActionButton fab_save;
    private FloatingActionButton fab_delete;
    private static final int CURSOR_LOADER_ID = 0;
    private long mItem = 0;

    private static final String LOG_TAG = CustomListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());



        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        fab_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), CustomDetailActivity.class);
                intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, 0);
                intent.putExtra(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_NEW);
                fab_new.setVisibility(View.VISIBLE);
                fab_save.setVisibility(View.GONE);
                fab_delete.setVisibility(View.GONE);
                startActivity(intent);
            }
        });


        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        fab_save.setVisibility(View.GONE);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDetailFragment customDetailFragment=(CustomDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if(customDetailFragment!=null){
                    customDetailFragment.verificationAndsave();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);
                }else {
                }
            }

        });

        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_delete.setVisibility(View.GONE);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDetailFragment customDetailFragment=(CustomDetailFragment)
                        getSupportFragmentManager().findFragmentById(R.id.custom_detail_container);
                if(customDetailFragment!=null){
                    customDetailFragment.deleteCustomer();
                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);
                }else {
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View emptyView = findViewById(R.id.recyclerview_custom_empty);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.custom_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mCursorAdapter = new CustomsCursorAdapter(this, null, emptyView, new CustomsCursorAdapter.CustomsCursorAdapterOnClickHandler() {
            @Override
            public void onClick(long id, CustomsCursorAdapter.ViewHolder vh) {
                mItem = id; // when rotate the screen the selecction of the second Screen is conserved.
                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, mItem);
                    arguments.putInt(CustomDetailFragment.CUSTOM_ACTION, CustomDetailFragment.CUSTOM_SELECTION);

                    CustomDetailFragment fragment = new CustomDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.custom_detail_container, fragment)
                            .commit();

                    fab_new.setVisibility(View.GONE);
                    fab_save.setVisibility(View.VISIBLE);
                    fab_delete.setVisibility(View.VISIBLE);


                }else {
                    Bundle arguments = new Bundle();

                    arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, mItem);
                    arguments.putInt(CustomDetailFragment.CUSTOM_ACTION, CustomDetailFragment.CUSTOM_SELECTION);
                    Intent intent = new Intent(getApplicationContext(), CustomDetailActivity.class);
                    intent.putExtras(arguments);


                    fab_new.setVisibility(View.VISIBLE);
                    fab_save.setVisibility(View.GONE);
                    fab_delete.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
                        Pair<View, String> p3 = Pair.create((View) vh.mSurename, getString(R.string.custom_icon_transition_surname));
                        ActivityOptionsCompat activityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(CustomListActivity.this,  p2, p3);
                        startActivity(intent, activityOptions.toBundle());

                    } else {
                        startActivity(intent);
                    }
                }
            }
        });


        recyclerView.setAdapter(mCursorAdapter);


        if (findViewById(R.id.custom_detail_container) != null ) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            Bundle arguments = new Bundle();
            arguments.putLong(CustomDetailFragment.ARG_ITEM_ID, mItem);// Fragmet load de last ID.
            arguments.putInt(CustomDetailFragment.CUSTOM_ACTION, CustomDetailFragment.CUSTOM_DOUBLE_SCREEN);
            CustomDetailFragment fragment = new CustomDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.custom_detail_container,fragment)
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onPostCreate(savedInstanceState);
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, LogisticaProvider.Customs.CONTENT_URI,
                null,
                null,
                null,
                CustomColumns.LASTNAME_CUSTOM +" ASC , "+CustomColumns.NAME_CUSTOM+" ASC  ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

}
