package com.nextnut.logistica.modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 31/10/2016.
 */
@IgnoreExtraProperties
public class UsuarioPerfil {

    private Usuario usuario;
    private Perfil perfil;

    private long fechaModificacion;
    private String uid;

    public UsuarioPerfil() {

    }

    public UsuarioPerfil(String uid, Usuario usuario, Perfil perfil) {
        this.usuario = usuario;
        this.perfil = perfil;
        this.uid = uid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usuario", usuario);
        result.put("perfil", perfil);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("uid", uid);

        return result;
    }
    // [END post_to_map]


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(long fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

