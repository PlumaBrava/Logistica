package com.abuseret.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abuseret.logistica.R;
import com.abuseret.logistica.modelos.Producto;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.abuseret.logistica.util.Imagenes.dimensiona;


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
//        Log.i("ProductView", "bindToPost-precio: " + producto.getPerfilDePrecio());
//        Log.i("ProductView", "bindToPost-precioEspecial: " + producto.getPrecioEspcecial());
        Log.i("ProductView", "bindToPost-fotoProducto: " + producto.getFotoProducto());
        Log.i("ProductView", "bindToPost-descripcion: " + producto.getDescripcionProducto());
        nombreProducto.setText(producto.getNombreProducto());
        NumberFormat format = NumberFormat.getCurrencyInstance();
//        precio.setText(format.format(producto.getPerfilDePrecio()));
//        precioEspecial.setText(format.format(producto.getPrecioEspcecial()));
        descripcion.setText(producto.getDescripcionProducto());

        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_action_redeem);
        Picasso.with(itemView.getContext())

                .load(producto.getFotoProducto())
                .resize(fotoProducto.getResources().getDimensionPixelSize(R.dimen.product_picture_w), fotoProducto.getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .placeholder(drawable)
                .centerCrop()
                .into(fotoProducto);


//        nombreProducto.setOnClickListener(starClickListener);
    }
}
