package com.abuseret.logistica.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.abuseret.logistica.R;
import com.abuseret.logistica.modelos.Pago;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PagosViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public View view;
    public TextView mTipoDePago;
    public TextView mMonto;
    public TextView mFechaDePago;

    public PagosViewHolder(View view) {
        super(view);
        this.view = view;
        mContext = view.getContext();

        mTipoDePago = (TextView) view.findViewById(R.id.tipoDePago_PagoVH);
        mMonto = (TextView) view.findViewById(R.id.mont_Pago_PagoVH);
        mFechaDePago = (TextView) view.findViewById(R.id.fecha_Pago_PagoVH);


    }

    public void bindToPost(Pago pago, View.OnClickListener pagoClickListener) {

        mContext.getResources().getColor(R.color.PagosCard_background);
        mTipoDePago.setText(pago.getTipoDePago());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mMonto.setText(format.format(pago.getMonto()));
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        mFechaDePago.setText(sfd.format(new Date(pago.getFechaDePago())));

        view.setOnClickListener(pagoClickListener);

    }
}
