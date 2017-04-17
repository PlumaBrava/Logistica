package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.PerfilDePrecio;


public class PerfilDePreciosViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    public View view;
    public TextView mPerfildePrecios;


    public PerfilDePreciosViewHolder(View view) {
        super(view);
        this.view=view;
        mContext=view.getContext();

        mPerfildePrecios = (TextView) view.findViewById(R.id.perfilDePreciosVH);

    }

    public void bindToPost(PerfilDePrecio perfilDePrecios, View.OnClickListener perfilDePreciosClickListener) {

        Log.d("SettingsLogistica", "obindToPos" );


        mPerfildePrecios.setText(perfilDePrecios.getPerfilDePrecio());

        view. setOnClickListener(perfilDePreciosClickListener);

    }
}
