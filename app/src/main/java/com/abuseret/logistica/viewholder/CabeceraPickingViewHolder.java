package com.abuseret.logistica.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abuseret.logistica.R;
import com.abuseret.logistica.modelos.CabeceraPicking;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.abuseret.logistica.util.Constantes.PICKING_STATUS_CERRADA;


public class CabeceraPickingViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public long mPickingOrderId;
    public TextView mPickingOrderNumber;
    public TextView mpickingOrderComents;
    public TextView mCreationDate;
    public TextView mCreationUser;
    public View mView;
    public ImageButton mSharePickingOrder;

    public CabeceraPickingViewHolder(View view) {
        super(view);
        mView=view;
        mContext=view.getContext();
        mPickingOrderNumber = (TextView) view.findViewById(R.id.pickingNumberOrderCard);
        mpickingOrderComents = (TextView) view.findViewById(R.id.pickingOrderComents);
        mCreationDate = (TextView) view.findViewById(R.id.PicckinOder_creationdate);
        mSharePickingOrder = (ImageButton) view.findViewById(R.id.SharePickingorder);
        mSharePickingOrder = (ImageButton) view.findViewById(R.id.SharePickingorder);
        mCreationUser=(TextView) view.findViewById(R.id.pickingOrderUser);

    }

    public void bindToPost(CabeceraPicking cabeceraPicking, View.OnClickListener clickListener) {
        if (cabeceraPicking.getEstado() == PICKING_STATUS_CERRADA) {
//            ((View) mPickingOrderNumber.getParent().getParent()).setBackgroundColor(Color.RED);
            mView.setBackgroundColor(Color.RED);

        } else {
            ((View) (mPickingOrderNumber.getParent().getParent())).setBackgroundColor(
                    mContext.getResources().getColor(R.color.PickingCard_background));

        }
        Log.d("picking1", "cabeceraPicking.getNumeroDePickingOrden() " + cabeceraPicking.getNumeroDePickingOrden());
        Log.d("picking1", "cabeceraPicking.getComentario() " + cabeceraPicking.getComentario());
        Log.d("picking1", "cabeceraPicking.getUsuarioCompensador() " + cabeceraPicking.getUsuarioCreador());
        Log.d("picking1", "cabeceraPicking.getFecha() " + cabeceraPicking.getFechaDeCreacion());
        Log.d("picking1", "cabeceraPicking.getUsuarioCompensador " + cabeceraPicking.getUsuarioCreador());
        mPickingOrderId = cabeceraPicking.getNumeroDePickingOrden();
        mPickingOrderNumber.setText(Long.toString(cabeceraPicking.getNumeroDePickingOrden()));
        mpickingOrderComents.setText(cabeceraPicking.getComentario());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        mCreationDate.setText(sfd.format(new Date(cabeceraPicking.getFechaDeCreacion())) );
        mCreationUser.setText(cabeceraPicking.getUsuarioCreador());

        ((View)mCreationDate.getParent().getParent().getParent()) . setOnClickListener(clickListener);
    }
}
