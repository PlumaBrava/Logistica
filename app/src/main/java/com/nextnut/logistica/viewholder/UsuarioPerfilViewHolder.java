package com.nextnut.logistica.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.modelos.Usuario;
import com.nextnut.logistica.modelos.UsuarioPerfil;
import com.rey.material.widget.Switch;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.dimensiona;


public class UsuarioPerfilViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "UsuarioListActivity";
    private TextView mEmail;
    private TextView mNombre;
    private TextView mStatus;
     private ImageView mFotoUsuario;
    private Switch mActivoSwitch;

    public UsuarioPerfilViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "UsuarioPerfilViewHolder");
        mEmail = (TextView) itemView.findViewById(R.id.emailUsuarioListContent);
        mNombre = (TextView) itemView.findViewById(R.id.usernameUsuarioListContent);
        mStatus = (TextView) itemView.findViewById(R.id.statusUsuarioListContent);
        mActivoSwitch = (Switch) itemView.findViewById(R.id.activoSwitchUsuarioListContent);
        mFotoUsuario = (ImageView) itemView.findViewById(R.id.fotoUsuarioUsuarioListContent);

    }

    public void bindToPost(UsuarioPerfil usuarioPerfil, View.OnClickListener usuarioPerfilClickListener) {
        Usuario usuario = usuarioPerfil.getUsuario();
        Log.d(TAG, "bindToPost-nombre: " + usuario.getUsername());
        Log.d(TAG, "bindToPost-email: " + usuario.getEmail());
        Log.d(TAG, "bindToPost-status: " + usuario.getStatus());
        Log.d(TAG, "bindToPost-activo: " + usuario.getActivo());
        Log.d(TAG, "bindToPost-foto: " + usuario.getPhotoURL());
        mEmail.setText(usuario.getEmail());
        mNombre.setText(usuario.getUsername());
        mStatus.setText(usuario.getStatus());
//        mActivoSwitch.setEnabled(true);
        mActivoSwitch.setChecked(usuario.getActivo());
//        mActivoSwitch.setEnabled(usuario.getActivo());



        Drawable drawable = dimensiona(itemView.getContext(), R.drawable.ic_action_account_circle_40);
        Picasso.with(itemView.getContext())

                .load(usuario.getPhotoURL())
                .resize(200,200)
                .placeholder(drawable)
                .centerCrop()
                .into(mFotoUsuario);


        ((View)mEmail.getParent()).setOnClickListener(usuarioPerfilClickListener);
    }
}
