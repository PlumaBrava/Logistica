package com.nextnut.logistica;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;

public class CustomSelectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Adapter mAdapter;
    public static String RESULTADO = "resultado";
    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = CustomSelectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View recyclerView = findViewById(R.id.content_custom_selection);
        mAdapter = new Adapter(null);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        String orderBy = CustomColumns.DELIVERY_CITY_CUSTOM + " ASC, " + CustomColumns.NAME_CUSTOM + " ASC, " + CustomColumns.LASTNAME_CUSTOM + " ASC ";
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
        if (data != null) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spiner_custom_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {


            if (mCursor == null) {
            } else {

                mCursor.moveToPosition(position);

                holder.customCity.setText(mCursor.getString(mCursor.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
                holder.custonName.setText(mCursor.getString(mCursor.getColumnIndex(CustomColumns.NAME_CUSTOM)) + " " +
                        mCursor.getString(mCursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));

                Drawable drawable = dimensiona(CustomSelectionActivity.this, R.drawable.ic_action_action_redeem);
                Picasso.with(CustomSelectionActivity.this)

                        .load(mCursor.getString(mCursor.getColumnIndex(CustomColumns.IMAGEN_CUSTOM)))
                        .resize(holder.photoCliente.getMaxWidth(), holder.photoCliente.getMaxHeight())
                        .placeholder(drawable)
                        .centerCrop()
                        .into(holder.photoCliente);


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.putExtra(RESULTADO, getItemId(holder.getAdapterPosition()));
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

            public final ImageView photoCliente;
            public final TextView customCity;
            public final TextView custonName;


            public ViewHolder(View view) {

                super(view);
                mView = view;
                photoCliente = (ImageView) view.findViewById(R.id.photoCliente);
                customCity = (TextView) view.findViewById(R.id.customCity);
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
