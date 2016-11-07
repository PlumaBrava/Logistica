package com.nextnut.logistica.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;

//import com.nextnut.distribution.ProductsActivity;
//import com.nextnut.distribution.R;

//import com.sam_chordas.android.schematicplanets.R;
//import com.sam_chordas.android.schematicplanets.data.ArchivedPlanetColumns;
//import com.sam_chordas.android.schematicplanets.data.PlanetColumns;
//import com.sam_chordas.android.schematicplanets.data.PlanetProvider;

//import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sam_chordas on 8/12/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */

public class PickingOrderProductsAdapter extends CursorRecyclerViewAdapter<PickingOrderProductsAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    public static final int COLUMN_ID_PRODUCTO = 0;
    public static final int COLUMN_NOMBRE_PRODUCTO = 1;
    public static final int COLUMN_IMAGEN_PRODUCTO = 2;
    public static final int COLUMN_DESCRIPCION_PRODUCTO = 3;
    public static final int COLUMN_QTOTAL_ORDENES = 4;
    public static final int COLUMN_ID_PICKING = 5;
    public static final int COLUMN_QTOTAL_PICKING = 6;
    public static final int COLUMN_QTOTAL_DELIVERY = 7;
    public static final int COLUMN_OORDERS_COUNT = 8;


    Context mContext;
    ViewHolder mVh;
    final private ProductCursorAdapterOnClickHandler mClickHandler;

    public PickingOrderProductsAdapter(Context context, Cursor cursor, View empltyView, ProductCursorAdapterOnClickHandler dh) {
        super(context, cursor, empltyView);
        mContext = context;
        mClickHandler = dh;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        public Long mDetalleOrderId;
        public Long mRefCustomer;
        public Long mRefProduct;
        public Long mRefPickingDetail;
        public String mphotString;
        public ImageView mphotoProducto;
        public TextView mTextViewNombre;
        public TextView mTextViewPrecio;
        public TextView mTextcantidad;
        public TextView mTextcantidadPicking;
        public TextView mTextcantidadDelivey;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
            mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
            mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
            mTextcantidad = (TextView) view.findViewById(R.id.cantidad);
            mTextcantidadPicking = (TextView) view.findViewById(R.id.cantidadPicking);
            mTextcantidadDelivey = (TextView) view.findViewById(R.id.cantidadTOTALDelivery);


        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mDetalleOrderId, this);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picking_product_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        mVh = vh;
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);

        viewHolder.mDetalleOrderId = cursor.getLong(COLUMN_ID_PRODUCTO);
        viewHolder.mphotString = cursor.getString(COLUMN_IMAGEN_PRODUCTO);
        Drawable drawable = dimensiona(mContext, R.drawable.ic_action_action_redeem);
        Picasso.with(viewHolder.mphotoProducto.getContext())

                .load(cursor.getString(COLUMN_IMAGEN_PRODUCTO))
                .resize(viewHolder.mphotoProducto.getMaxWidth(), viewHolder.mphotoProducto.getMaxHeight())
                .placeholder(drawable)
                .centerCrop()
                .into(viewHolder.mphotoProducto);

        viewHolder.mTextViewNombre.setText(cursor.getString(COLUMN_NOMBRE_PRODUCTO));
        viewHolder.mTextcantidad.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_ORDENES)));
        viewHolder.mTextcantidadPicking.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_PICKING)));
        viewHolder.mTextcantidadDelivey.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_DELIVERY)));

        if (Integer.parseInt(viewHolder.mTextcantidadPicking.getText().toString()) >
                Integer.parseInt(String.valueOf(viewHolder.mTextcantidad.getText().toString()))) {
            viewHolder.mTextcantidadPicking.setTextColor(Color.GREEN);

        } else if (Integer.parseInt(viewHolder.mTextcantidadPicking.getText().toString()) ==
                Integer.parseInt(String.valueOf(viewHolder.mTextcantidad.getText().toString()))) {
            viewHolder.mTextcantidadPicking.setTextColor(Color.BLUE);

        } else {
            viewHolder.mTextcantidadPicking.setTextColor(Color.RED);
        }

        viewHolder.mRefProduct = cursor.getLong(COLUMN_ID_PRODUCTO);
        viewHolder.mRefPickingDetail = cursor.getLong(COLUMN_ID_PICKING);
    }


    public interface ProductCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);

    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemAcepted(int position) {
    }


}
