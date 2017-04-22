package com.abuseret.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abuseret.logistica.R;

import com.abuseret.logistica.modelos.Empresa;
import com.squareup.picasso.Picasso;

import static com.abuseret.logistica.util.Imagenes.dimensiona;


public class EmpresaViewHolder extends RecyclerView.ViewHolder {

    private TextView nombreView;
    private TextView cuitView;
    private TextView ciudadView;
    private TextView direccionView;
    private TextView codigoPostalView;
    private TextView telefonoView;
    private String logo;
    public ImageView logoView;

    public EmpresaViewHolder(View itemView) {
        super(itemView);
        Log.i("EmpresasView", "EmpresaViewHolder");
        nombreView = (TextView) itemView.findViewById(R.id.itemempresa_nombre);
        cuitView = (TextView) itemView.findViewById(R.id.itemempresa_cuit);
        ciudadView = (TextView) itemView.findViewById(R.id.itemempresa_ciudad);
        direccionView = (TextView) itemView.findViewById(R.id.itemempresa_direccion);
        codigoPostalView = (TextView) itemView.findViewById(R.id.itemempresa_codigoPostal);
        telefonoView = (TextView) itemView.findViewById(R.id.itemempresa_telefono);
        logoView = (ImageView) itemView.findViewById(R.id.itemempresa_logo);
    }

    public void bindToPost(Empresa empresa, View.OnClickListener starClickListener) {
        Log.i("EmpresasView", "bindToPost-nombre: " + empresa.getNombre());
        nombreView.setText(empresa.getNombre());
        cuitView.setText(empresa.getCuit());
        ciudadView.setText(empresa.getCiudad());
        direccionView.setText(empresa.getDireccion());
        codigoPostalView.setText(empresa.getCodigoPostal());
        telefonoView.setText(empresa.getTelefono());
        logo=empresa.getLogo();
        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(itemView.getContext())

                .load(empresa.getLogo())
                .resize(200,200)
                .placeholder(drawable)
                .centerCrop()
                .into(logoView);


        nombreView.setOnClickListener(starClickListener);
    }
}