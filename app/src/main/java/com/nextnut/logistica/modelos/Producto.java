package com.nextnut.logistica.modelos;

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
public class Producto {

    private String nombreProducto;
    private Double precio;
    private Double precioEspcecial;
    private String descripcionProducto;
    private String fotoProducto;
    private long fechaModificacion;
    private String uid;





    public Producto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Producto(String uid,String nombreProducto, Double precio, Double precioEspcecial, String descripcionProducto, String fotoProducto ) {
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.precioEspcecial = precioEspcecial;
        this.descripcionProducto = descripcionProducto;
        this.fotoProducto = fotoProducto;
         this.uid = uid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("nombreProducto", nombreProducto);
        result.put("precio", precio);
        result.put("precioEspcecial", precioEspcecial);
        result.put("descripcionProducto", descripcionProducto);
        result.put("fotoProducto", fotoProducto);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("uid", uid);
        return result;
    }
    // [END post_to_map]


    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecioEspcecial() {
        return precioEspcecial;
    }

    public void setPrecioEspcecial(Double precioEspcecial) {
        this.precioEspcecial = precioEspcecial;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getFotoProducto() {
        return fotoProducto;
    }

    public void setFotoProducto(String fotoProducto) {
        this.fotoProducto = fotoProducto;
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
// [END blog_user_class]

