package com.nextnut.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Almacen;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class AlmacenesViewHolder extends RecyclerView.ViewHolder {

    private String mphotString;
    private TextView mNombreAlmacen;
    private TextView mCiudadAlmacen;
    private ImageView mFotoAlmacen;
    private TextView mDireccionAlmacen;
    private TextView mTipoAlmacen;
    private View mItemViem;

    public AlmacenesViewHolder(View itemView) {
        super(itemView);
        mItemViem=itemView;
        Log.i("AlmacenViewHolder", "Constructor ViewHolder");
        mFotoAlmacen = (ImageView) itemView.findViewById(R.id.fotoalmacen_listContent);
        mNombreAlmacen = (TextView) itemView.findViewById(R.id.nombreAlamacen_listContent);
        mCiudadAlmacen = (TextView) itemView.findViewById(R.id.ciudadAlmacen_listContent);
        mDireccionAlmacen = (TextView) itemView.findViewById(R.id.direccionAlmacen_listContent);
        mTipoAlmacen = (TextView) itemView.findViewById(R.id.tipoAlmacen_listContent);
    }

    public void bindToPost(Almacen almacen, View.OnClickListener clickListener) {
        mCiudadAlmacen.setText(almacen.getCiudad());
        mNombreAlmacen.setText(almacen.getNombre());
        mphotString=almacen.getLogo();
        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(mFotoAlmacen.getContext())

                .load(mphotString)
                .resize(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.product_picture_w),itemView.getContext(). getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .placeholder(drawable)
                .centerCrop()
                .into(mFotoAlmacen);
        mDireccionAlmacen.setText(almacen.getDireccion());
        mTipoAlmacen.setText(almacen.getTipodeAlmacen() );

        ((View) mNombreAlmacen.getParent().getParent()).setOnClickListener(clickListener);
//        mItemViem.setOnClickListener(clickListener);
    }
}
