package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nextnut.logistica.MainActivity;
import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.CabeceraOrden;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_DELIVERED_PARA_COMPENSAR;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_EN_DELIVERY;
import static com.nextnut.logistica.util.MakeCall.makePhoneCallCliente;


public class CabeceraViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public TextView mOrderNumber;
    public TextView mApellidoyNombre;
    //    public TextView mLastname;
    public ImageButton mBottonPhoto;
    public String mCustomerRefContacto;

    public TextView mTotalPrice;
    public TextView mDate;
//    public ImageButton mpasarAPickingSelector;

    public CabeceraViewHolder(View view) {
        super(view);
        mContext = view.getContext();
        mOrderNumber = (TextView) view.findViewById(R.id.numberOrderCard);
        mApellidoyNombre = (TextView) view.findViewById(R.id.ApellidoyNombreOrderCard);
//        mLastname = (TextView) view.findViewById(R.id.latNameOrderCard);
        mTotalPrice = (TextView) view.findViewById(R.id.totalPriceOrderCard);
        mDate = (TextView) view.findViewById(R.id.dateOrderCard);
        mBottonPhoto = (ImageButton) view.findViewById(R.id.phoneClallButton);
//        mpasarAPickingSelector = (ImageButton) view.findViewById(R.id.pasarPickingSelector);
        mBottonPhoto.setBackgroundColor(Color.TRANSPARENT);
    }

    public void bindToPost(final CabeceraOrden cabeceraOrden, View.OnClickListener cabeceraClickListener) {
        if (cabeceraOrden.getEstado() >= ORDEN_STATUS_DELIVERED_PARA_COMPENSAR) {
            ((View) (mOrderNumber.getParent().getParent())).setBackgroundColor(Color.RED);
            mOrderNumber.setTextColor(Color.GREEN);
        } else {
            ((View) (mOrderNumber.getParent().getParent())).setBackgroundColor(
                    mContext.getResources().getColor(R.color.CustomOrderCard_background));

        }


        mOrderNumber.setText(String.valueOf(cabeceraOrden.getNumeroDeOrden()));
        mApellidoyNombre.setText(cabeceraOrden.getCliente().getNombre() + " " + cabeceraOrden.getCliente().getApellido());
        NumberFormat format = NumberFormat.getCurrencyInstance();

        Double montoEntregado = cabeceraOrden.getTotales().getMontoEntregado();
        Double montoEnOrden = cabeceraOrden.getTotales().getMontoEnOrdenes();
        if (!cabeceraOrden.getCliente().getEspecial()) {
            montoEntregado = montoEntregado * (1 + (cabeceraOrden.getCliente().getIva() / 100));
            montoEnOrden = montoEnOrden * (1 + (cabeceraOrden.getCliente().getIva() / 100));
        }

        if (cabeceraOrden.getEstado() >= ORDEN_STATUS_EN_DELIVERY) {
            mTotalPrice.setText(format.format(montoEntregado));

        } else {
            mTotalPrice.setText(format.format(montoEnOrden));
        }

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy");


        mDate.setText(sfd.format(new Date(cabeceraOrden.getFechaDeCreacion())));


        if (cabeceraOrden.getCliente().getTelefonos() != null) {


            ArrayList mData;

            mData = new ArrayList();
            mData.addAll(cabeceraOrden.getCliente().getTelefonos().entrySet());
//            Map.Entry<String, String> item = (Map.Entry) mData.get(0);
//7
//
//            Log.i("call", "cabeceraOrden.getCliente(). item - " +item.getKey()+" -- "+ item.getValue());
//            Log.i("call", "cabeceraOrden.getCliente(). mData.isEmpty() - " +mData.isEmpty());
            Log.i("call", "cabeceraOrden.getCliente(). mData.sie - " + mData.size());
            Log.i("call", "cabeceraOrden.getCliente().getTelefono() - no nulo");
            Log.i("call", "cabeceraOrden.getCliente().getTelefono() - " + cabeceraOrden.getCliente().getTelefonos().toString());
            Log.i("call", "cabeceraOrden.getCliente().getTelefono().isEmpty() - " + cabeceraOrden.getCliente().getTelefonos().isEmpty());
            if (!cabeceraOrden.getCliente().getTelefonos().isEmpty()) {
                mBottonPhoto.setVisibility(View.VISIBLE);
                mBottonPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        makePhoneCallCliente(MainActivity.getMainActivity(), cabeceraOrden.getCliente());
                    }
                });
            } else {
                Log.i("call", "cabeceraOrden.getCliente().getTelefono() -  nulo");

                mBottonPhoto.setVisibility(View.GONE);
            }
        }
        ((View) mApellidoyNombre.getParent().getParent().getParent()).setOnClickListener(cabeceraClickListener);

    }
}
