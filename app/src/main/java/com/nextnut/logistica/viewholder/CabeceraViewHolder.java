package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.CabeceraOrden;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nextnut.logistica.util.Constantes.ESTADO_ORDEN_ENTREGADA;


public class CabeceraViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public TextView mOrderNumber;
    public TextView mName;
    public TextView mLastname;
    public ImageButton mBottonPhoto;
    public String mCustomerRefContacto;

    public TextView mTotalPrice;
    public TextView mDate;

    public CabeceraViewHolder(View view) {
        super(view);
        mContext=view.getContext();
        mOrderNumber = (TextView) view.findViewById(R.id.numberOrderCard);
        mName = (TextView) view.findViewById(R.id.nameOrderCard);
        mLastname = (TextView) view.findViewById(R.id.latNameOrderCard);
        mTotalPrice = (TextView) view.findViewById(R.id.totalPriceOrderCard);
        mDate = (TextView) view.findViewById(R.id.dateOrderCard);
        mBottonPhoto = (ImageButton) view.findViewById(R.id.phoneClallButton);
        mBottonPhoto.setBackgroundColor(Color.TRANSPARENT);
//        mBottonPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mClickHandler.onMakeACall(mCustomerRefContacto);
//            }
//        });
    }

    public void bindToPost(CabeceraOrden cabeceraOrden, View.OnClickListener cabeceraClickListener) {
        if(cabeceraOrden.getEstado()== ESTADO_ORDEN_ENTREGADA){
            ((View )(mOrderNumber.getParent().getParent())).setBackgroundColor(Color.RED);
            mOrderNumber.setTextColor(Color.GREEN);
        }else {
            ((View )(mOrderNumber.getParent().getParent())).setBackgroundColor(
                    mContext.getResources().getColor(R.color.CustomOrderCard_background));

        }


//        mcursorId=cursor.getLong(0);
        mOrderNumber.setText(String.valueOf(cabeceraOrden.getNumeroDeOrden()));
        mName.setText(cabeceraOrden.getCliente().getNombre());
        mLastname.setText(cabeceraOrden.getCliente().getApellido());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTotalPrice.setText(format.format(cabeceraOrden.getTotales().getMontoEnOrdenes()));
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


        mDate.setText(sfd.format(new Date(cabeceraOrden.getFechaDeCreacion())) );
//        mCustomerRefContacto=cursor.getString(5);
        mBottonPhoto.setVisibility(cabeceraOrden.getCliente().getTelefono()!=null?View.VISIBLE:View.GONE);
        ((View)mDate.getParent()) . setOnClickListener(cabeceraClickListener);

    }
}
