package com.abuseret.logistica.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abuseret.logistica.R;
import com.abuseret.logistica.modelos.Detalle;
import com.abuseret.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.squareup.picasso.Picasso;

import static com.abuseret.logistica.util.Imagenes.dimensiona;


public class DetalleDeliveryTotalProdutctosViewHolder extends RecyclerView.ViewHolder  implements ItemTouchHelperAdapter {
    Context mContext;




    public String mphotString;
    public ImageView mphotoProducto;
    public TextView mTextViewNombre;


    public TextView mTextcantidadEnOrden;
    public TextView mTextcantidadEnPicking;
    public TextView mTextcantidadEnDelivey;


    public DetalleDeliveryTotalProdutctosViewHolder(View view) {
        super(view);
        mContext=view.getContext();
        mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
        mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);

        mTextcantidadEnOrden = (TextView) view.findViewById(R.id.cantidadEnOrdenes);
        mTextcantidadEnPicking = (TextView) view.findViewById(R.id.cantidadPicking);
        mTextcantidadEnDelivey = (TextView) view.findViewById(R.id.cantidadDelivery);

    }

    public void bindToPost(Detalle detalle, View.OnClickListener detalleClickListener) {

        Log.d("detalleDeliveryTotal", "Cantidad Orden " + detalle.getCantidadOrden());
        Log.d("detalleDeliveryTotal", "Cantidad Picking " + detalle.getCantidadPicking());
        Log.d("detalleDeliveryTotal", "Cantidad Delivey " + detalle.getCantidadEntrega());
        mphotString=detalle.getProducto().getFotoProducto();
        Drawable drawable = dimensiona(mContext, R.drawable.ic_action_action_redeem);
        Picasso.with(mphotoProducto.getContext())

                .load(mphotString)
                .resize(mphotoProducto.getMaxWidth(),mphotoProducto.getMaxHeight())
                .placeholder(drawable)
                .centerCrop()
                .into(mphotoProducto);

        mTextViewNombre.setText(detalle.getProducto().getNombreProducto());
        mTextcantidadEnOrden.setText(String.valueOf(detalle.getCantidadOrden()));
        mTextcantidadEnPicking.setText(String.valueOf(detalle.getCantidadPicking()));
        Log.d("detalleDeliveryTotal", "Cantidad Orden " + detalle.getCantidadOrden());
        Log.d("detalleDeliveryTotal", "Cantidad Picking " + detalle.getCantidadPicking());
        Log.d("detalleDeliveryTotal", "Cantidad Delivey " + detalle.getCantidadEntrega());

        mTextcantidadEnDelivey.setText(String.valueOf(detalle.getCantidadEntrega()));


        ((View)mTextcantidadEnPicking.getParent()).setOnClickListener(detalleClickListener);

    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemAcepted(int position) {

    }
}
