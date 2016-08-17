package com.nextnut.logistica;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.rest.CustomAdapter;

public class CustomSelectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Adapter mAdapter;
    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = CustomSelectionActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent();
                intent.putExtra("resultado","valor");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View recyclerView = findViewById(R.id.content_custom_selection);
        mAdapter = new Adapter(null);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }




    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        Log.i(LOG_TAG,"setupRecyclerView");
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG,"onPostCreate");
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"onCreateLoader");


        String orderBy =CustomColumns.DELIVERY_CITY_CUSTOM  +" ASC, "+ CustomColumns.NAME_CUSTOM  +" ASC, "+ CustomColumns.LASTNAME_CUSTOM +" ASC ";
        return new CursorLoader(
                this,
                LogisticaProvider.Customs.CONTENT_URI,
                null,
                null,
                null,
                orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null) {
            Log.i(LOG_TAG,"onLoadFinished");
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(LOG_TAG,"onLoaderReset");
        mAdapter.swapCursor(null);

    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Cursor mCursor;
        private DataSetObserver mDataSetObserver;
        private boolean mDataIsValid;
        private int mRowIdColumn;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }


        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mCursor.getColumnIndex(CustomColumns.ID_CUSTOM));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(LOG_TAG,"onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spiner_custom_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {


            if (mCursor == null) {
                Log.i(LOG_TAG, "cursor Nulo");
            } else{

                mCursor.moveToPosition(position);


                String text1 = mCursor.getString(mCursor.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM))+" | "+
                        mCursor.getString(mCursor.getColumnIndex(CustomColumns.NAME_CUSTOM))+ " "+
                        mCursor.getString(mCursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM));

                holder.custonName.setText(text1);

                Log.i("LOG_TAG", "ID: " + Long.toString(getItemId(position)));

           holder.custonName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i(LOG_TAG,"nClickListener"+", valor id:"+getItemId(holder.getAdapterPosition()));
                        Intent intent = new Intent();
                        intent.putExtra("resultado",getItemId(holder.getAdapterPosition()));
//                        intent.putExtra("resultado",2);
                        setResult(RESULT_OK, intent);
                        finish();



                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            int cantidad = 0;
            if (mCursor != null) {
                cantidad = mCursor.getCount();
            }
            return cantidad;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;

            public final TextView custonName;


            public ViewHolder(View view) {

                super(view);
                Log.i(LOG_TAG,"ViewHolder(");
                mView = view;
                custonName = (TextView) view.findViewById(R.id.customNameSpinner);

            }

        }

        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return null;
            }
            final Cursor oldCursor = mCursor;
            if (oldCursor != null && mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
            mCursor = newCursor;
            if (mCursor != null) {
                if (mDataSetObserver != null) {
                    mCursor.registerDataSetObserver(mDataSetObserver);
                }
                mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
                mDataIsValid = true;
                notifyDataSetChanged();
            } else {
                mRowIdColumn = -1;
                mDataIsValid = false;
                notifyDataSetChanged();
            }
            return oldCursor;
        }
    }


}
