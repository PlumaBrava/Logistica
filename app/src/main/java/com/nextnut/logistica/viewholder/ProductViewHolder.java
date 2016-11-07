package com.nextnut.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Producto;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class ProductViewHolder extends RecyclerView.ViewHolder {

    private TextView nombreProducto;
    private TextView precio;
    private TextView precioEspecial;
    private TextView descripcion;
    private TextView codigoPostalView;
    private TextView telefonoView;
    private String logo;
    public ImageView fotoProducto;

    public ProductViewHolder(View itemView) {
        super(itemView);
        nombreProducto = (TextView) itemView.findViewById(R.id.nombreProducto);
        precio = (TextView) itemView.findViewById(R.id.precioProducto);
        precioEspecial = (TextView) itemView.findViewById(R.id.precioProductoSpecial);
        descripcion = (TextView) itemView.findViewById(R.id.descriptionProducto);

        fotoProducto = (ImageView) itemView.findViewById(R.id.photoProducto);
    }

    public void bindToPost(Producto producto, View.OnClickListener starClickListener) {
        Log.i("ProductView", "bindToPost-nombre: " + producto.getNombreProducto());
        Log.i("ProductView", "bindToPost-precio: " + producto.getPrecio());
        Log.i("ProductView", "bindToPost-precioEspecial: " + producto.getPrecioEspcecial());
        Log.i("ProductView", "bindToPost-fotoProducto: " + producto.getFotoProducto());
        Log.i("ProductView", "bindToPost-descripcion: " + producto.getDescripcionProducto());
        nombreProducto.setText(producto.getNombreProducto());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        precio.setText(format.format(producto.getPrecio()));
        precioEspecial.setText(format.format(producto.getPrecioEspcecial()));
        descripcion.setText(producto.getDescripcionProducto());

        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(itemView.getContext())

                .load(producto.getFotoProducto())
                .resize(200,200)
                .placeholder(drawable)
                .centerCrop()
                .into(fotoProducto);


        nombreProducto.setOnClickListener(starClickListener);
    }
}
