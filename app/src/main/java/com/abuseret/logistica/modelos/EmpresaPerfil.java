package com.abuseret.logistica.modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 25/10/2016.
 */
// [START blog_user_class]
@IgnoreExtraProperties
public class EmpresaPerfil {
    private Empresa empresa;
    private Perfil perfil;
    private long fechaModificacion;
    private String uid;


    public EmpresaPerfil() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public EmpresaPerfil(String uid, Empresa empresa, Perfil perfil) {
        this.uid=uid;
        this.empresa = empresa;
        this.perfil = perfil;
    }

    public String getUid() {
        return uid;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("empresa", empresa);
        result.put("perfil", perfil);
        result.put("uid", uid);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);

        return result;
    }
    // [END post_to_map]


    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

    public void setUid(String uid) {
        this.uid = uid;
    }
}
// [END blog_user_class]

