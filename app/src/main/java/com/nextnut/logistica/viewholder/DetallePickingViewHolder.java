package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class DetallePickingViewHolder extends RecyclerView.ViewHolder  implements ItemTouchHelperAdapter {
    Context mContext;




    public String mphotString;
    public ImageView mphotoProducto;
    public TextView mTextViewNombre;


    public TextView mTextcantidadEnOrden;
    public TextView mTextcantidadEnPicking;


    public DetallePickingViewHolder(View view) {
        super(view);
        mContext=view.getContext();
        mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
        mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);

        mTextcantidadEnOrden = (TextView) view.findViewById(R.id.cantidadEnOrdenes);
        mTextcantidadEnPicking = (TextView) view.findViewById(R.id.cantidadPicking);

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
//        NumberFormat format = NumberFormat.getCurrencyInstance();
//        mTextViewPrecio.setText(format.format(detalle.getPrecio()));
        mTextcantidadEnOrden.setText(String.valueOf(detalle.getCantidadOrden()));
        mTextcantidadEnPicking.setText(String.valueOf(detalle.getCantidadPicking()));
        Log.d("detallePicking", "antes saveDetalle-detalle.getPrecio()) " + detalle.getPrecio());
        Log.d("detallePicking", "Cantidad Orden " + detalle.getCantidadOrden());
        Log.d("detallePicking", "Cantidad Picking " + detalle.getCantidadPicking());




        ((View)mTextcantidadEnPicking.getParent().getParent().getParent()).setOnClickListener(detalleClickListener);

    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemAcepted(int position) {

    }
}
