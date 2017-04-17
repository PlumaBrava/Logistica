package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.CabeceraOrden;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SaldosViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public View view;
    public TextView mName;
    public TextView mLastname;


    public TextView mSaldo;
    public TextView mCantidadOrdenesSinCompensar;

    public SaldosViewHolder(View view) {
        super(view);
        this.view=view;
        mContext=view.getContext();

        mName = (TextView) view.findViewById(R.id.nameOrderCard);
        mLastname = (TextView) view.findViewById(R.id.latNameOrderCard);
        mSaldo = (TextView) view.findViewById(R.id.Saldo);
        mCantidadOrdenesSinCompensar = (TextView) view.findViewById(R.id.cantidadOrdenesSinCompensar);


    }

    public void bindToPost(CabeceraOrden cabeceraOrden, View.OnClickListener cabeceraClickListener) {


        mName.setText(cabeceraOrden.getCliente().getNombre());
        mLastname.setText(cabeceraOrden.getCliente().getApellido());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mSaldo.setText(format.format(cabeceraOrden.getTotales().getSaldo()));
//        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy");


        mCantidadOrdenesSinCompensar.setText(sfd.format(new Date(cabeceraOrden.getFechaDeCreacion())) );

        view. setOnClickListener(cabeceraClickListener);
//        ((View) mFechaDePago.getParent()) . setOnClickListener(cabeceraClickListener);

    }
}
