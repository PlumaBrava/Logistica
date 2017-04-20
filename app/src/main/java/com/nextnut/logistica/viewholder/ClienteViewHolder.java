package com.nextnut.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Cliente;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class ClienteViewHolder extends RecyclerView.ViewHolder {

    private long mcursorId ;
    private String mphotString;
    private String mCustomKey;


    private TextView mName;
    private TextView mSurename;
    private ImageView mphotoCustomer;
    private TextView mDeliveryAddress;
    private TextView mCity;
    private CheckBox  mSpecial;
    private TextView mPerfilDePrecios;

    public ClienteViewHolder(View itemView) {
        super(itemView);
        Log.i("ClienteViewHolder", "Cliente");
        mphotoCustomer = (ImageView) itemView.findViewById(R.id.photocustom_listContent);
        mName = (TextView) itemView.findViewById(R.id.nameCustom_listContent);
        mSurename = (TextView) itemView.findViewById(R.id.surenameCustom_listContent);
        mDeliveryAddress = (TextView) itemView.findViewById(R.id.deliveryAddress_listContent);
        mCity = (TextView) itemView.findViewById(R.id.cityCustom_listContent);
        mPerfilDePrecios = (TextView) itemView.findViewById(R.id.perfilDePrecios_listContent);
        mSpecial = (CheckBox) itemView.findViewById(R.id.custom_special);
    }


    public void bindToPost(Cliente cliente, View.OnClickListener starClickListener) {
        Log.i("ClienteViewHolder", "bindToPost-apellido: " + cliente.getApellido());
        mSurename.setText(cliente.getApellido());
        Log.i("ClienteViewHolder", "bindToPost-nombre: " + cliente.getNombre());
        mName.setText(cliente.getNombre());
        mphotString=cliente.getFotoCliente();
        mSpecial.setChecked(cliente.getEspecial());
        mPerfilDePrecios.setText(cliente.getPerfilDePrecios()==null?"Definir":cliente.getPerfilDePrecios());
        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_image_timer_auto);
        Picasso.with(mphotoCustomer.getContext())

                .load(mphotString)
                .resize(itemView.getContext().getResources().getDimensionPixelSize(R.dimen.product_picture_w),itemView.getContext(). getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .placeholder(drawable)
                .centerCrop()
                .into(mphotoCustomer);
        Log.i("ClienteViewHolder", "cliente.getDireccionDeEntrega: "+cliente.getDireccionDeEntrega());
        mDeliveryAddress.setText(cliente.getDireccionDeEntrega());
        Log.i("ClienteViewHolder", "cliente.getCiudad(): "+cliente.getCiudad());
        mCity.setText(cliente.getCiudad());

        mName.setOnClickListener(starClickListener);
    }
}
