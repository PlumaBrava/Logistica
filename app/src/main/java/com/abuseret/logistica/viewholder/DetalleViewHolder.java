package com.abuseret.logistica.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.abuseret.logistica.R;
import com.abuseret.logistica.modelos.Detalle;
import com.abuseret.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.abuseret.logistica.util.Imagenes.dimensiona;


public class DetalleViewHolder extends RecyclerView.ViewHolder  implements ItemTouchHelperAdapter {
    Context mContext;
    Boolean mfavoriteVisibe = true;
    Boolean mDeliveryState = false;

    public String mphotString;
    public ImageView mphotoProducto;
    public TextView mTextViewNombre;

    public TextView mTextViewPrecio;
    public TextView mTextViewPrecioTitulo;
    public TextView mTextcantidad;
    public TextView mTextToal;

    public TextView mTextViewPrecioDelivery;
    public TextView mTextcantidadDelivery;
    public TextView mTextToalDelivery;

    public CheckBox mfavorito;

    public void setDeliveryState() {this.mDeliveryState=true;}
    public void resetDeliveryState() {this.mDeliveryState=false;}

    public DetalleViewHolder(View view) {
        super(view);
        mContext=view.getContext();
        mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
        mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
        mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
        mTextViewPrecioTitulo = (TextView) view.findViewById(R.id.precioTitulo);
        mTextcantidad = (TextView) view.findViewById(R.id.cantidad);
        mTextToal = (TextView) view.findViewById(R.id.total);
        mTextViewPrecioDelivery = (TextView) view.findViewById(R.id.precioProductoDelivery);
        mTextcantidadDelivery = (TextView) view.findViewById(R.id.cantidadDelivery);
        mTextToalDelivery = (TextView) view.findViewById(R.id.totalDelivery);
        mfavorito = (CheckBox) view.findViewById(R.id.favorito);
        mfavorito.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                mClickHandler.onFavorite(mDetalleOrderId, OrderDetailCursorAdapter.ViewHolder.this);

            }
        });


        mfavorito.setVisibility(mfavoriteVisibe ? View.VISIBLE : View.GONE);
        if(mDeliveryState){
            mTextViewPrecioDelivery.setVisibility(View.VISIBLE);
            mTextcantidadDelivery.setVisibility(View.VISIBLE);
            mTextToalDelivery.setVisibility(View.VISIBLE);
            mfavorito.setVisibility(View.GONE);
        } else {
            mTextViewPrecioDelivery.setVisibility(View.GONE);
            mTextcantidadDelivery.setVisibility(View.GONE);
            mTextToalDelivery.setVisibility(View.GONE);

        }
    }

    public void bindToPost(Detalle detalle, View.OnClickListener detalleClickListener) {

//        mDetalleOrderId=cursor.getLong(0);
        mphotString=detalle.getProducto().getFotoProducto();
        Drawable drawable = dimensiona(mContext, R.drawable.ic_action_action_redeem);
        Picasso.with(mphotoProducto.getContext())

                .load(mphotString)
                .resize(mphotoProducto.getMaxWidth(),mphotoProducto.getMaxHeight())
                .placeholder(drawable)
                .centerCrop()
                .into(mphotoProducto);

        mTextViewNombre.setText(detalle.getProducto().getNombreProducto());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTextViewPrecio.setText(format.format(detalle.getPrecio()));
        mTextcantidad.setText(String.valueOf(detalle.getCantidadOrden()));
        mTextToal.setText(format.format(detalle.getMontoItemOrden()));
        Log.d("detalle2", "Delivery " + mDeliveryState);
        Log.d("detalle2", "Cantidad Picking " + detalle.getCantidadPicking());
        Log.d("detalle2", "antes saveDetalle-detalle.getKilosEnStock()) " + detalle.getMontoItemOrden());

        if(detalle.getCantidadPicking() > -1) {
           mTextViewPrecioDelivery.setText(format.format(detalle.getPrecio()));
           mTextcantidadDelivery.setText(Double.toString( detalle.getCantidadEntrega()));
           mTextToalDelivery.setText(format.format(detalle.getMontoItemEntrega()));

        }


        ((View) mTextcantidad.getParent().getParent().getParent()).setOnClickListener(detalleClickListener);

    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemAcepted(int position) {

    }
}
