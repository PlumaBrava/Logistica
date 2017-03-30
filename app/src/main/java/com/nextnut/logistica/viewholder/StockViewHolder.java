package com.nextnut.logistica.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Stock;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class StockViewHolder extends RecyclerView.ViewHolder  implements ItemTouchHelperAdapter {
    Context mContext;

    public String mphotString;
    public ImageView mphotoProducto;
    public TextView mNombreProducto;

    public TextView mUnidadesEnStock;
    public TextView mKgEnStock;

    public TextView mUnidadesSolicitadas;
    public TextView mKgSolicitados;



    public StockViewHolder(View view) {
        super(view);
        mContext=view.getContext();
        mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
        mNombreProducto = (TextView) view.findViewById(R.id.nombreProducto);
        mUnidadesEnStock = (TextView) view.findViewById(R.id.unidadesEnStock);
        mKgEnStock = (TextView) view.findViewById(R.id.kgEnStock);
        mUnidadesSolicitadas = (TextView) view.findViewById(R.id.unidadesSolicitadas);
        mKgSolicitados = (TextView) view.findViewById(R.id.kgSoliciados);

    }

    public void bindToPost(Stock stock, View.OnClickListener detalleClickListener) {

        mphotString=stock.getProducto().getFotoProducto();
        Drawable drawable = dimensiona(mContext, R.drawable.ic_action_action_redeem);
        Picasso.with(mphotoProducto.getContext())

                .load(mphotString)
                .resize(mphotoProducto.getMaxWidth(),mphotoProducto.getMaxHeight())
                .placeholder(drawable)
                .centerCrop()
                .into(mphotoProducto);

        mNombreProducto.setText(stock.getProducto().getNombreProducto());
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mUnidadesEnStock.setText(String.valueOf(stock.getCantidadUnidadesEnStock()));
        mKgEnStock.setText(format.format(stock.getKilosEnStock()));

        mUnidadesSolicitadas.setText(Double.toString( stock.getCantidadUnidadesSolicitadas()));
        mKgSolicitados.setText(format.format(stock.getKilosSolicitados()));



        ((View) mUnidadesEnStock.getParent().getParent().getParent()).setOnClickListener(detalleClickListener);

    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemAcepted(int position) {

    }
}
