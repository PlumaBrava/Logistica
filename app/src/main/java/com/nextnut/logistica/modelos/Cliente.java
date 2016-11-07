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
public class Cliente {

    private String nombre;
    private String apellido;
    private String telefono;
    private String fotoCliente;
    private String direccionDeEntrega;
    private String ciudad;
    private long iva;
    private String ciut;
    private Boolean especial;

    private String fechaModificacion;
    private String uid;

    public Cliente() {

    }

    public Cliente(String nombre, String apellido, String telefono, String fotoCliente, String direccionDeEntrega, String ciudad, long iva, String ciut, Boolean especial, String fechaModificacion, String uid) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.fotoCliente = fotoCliente;
        this.direccionDeEntrega = direccionDeEntrega;
        this.ciudad = ciudad;
        this.iva = iva;
        this.ciut = ciut;
        this.especial = especial;
        this.fechaModificacion = fechaModificacion;
        this.uid = uid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("apellido", apellido);
        result.put("fotoCliente", fotoCliente);
        result.put("direccionDeEntrega", direccionDeEntrega);
        result.put("ciudad", ciudad);
        result.put("iva", iva);
        result.put("ciut", ciut);
        result.put("especial", especial);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("uid", uid);

        return result;
    }
    // [END post_to_map]


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFotoCliente() {
        return fotoCliente;
    }

    public void setFotoCliente(String fotoCliente) {
        this.fotoCliente = fotoCliente;
    }

    public String getDireccionDeEntrega() {
        return direccionDeEntrega;
    }

    public void setDireccionDeEntrega(String direccionDeEntrega) {
        this.direccionDeEntrega = direccionDeEntrega;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public long getIva() {
        return iva;
    }

    public void setIva(long iva) {
        this.iva = iva;
    }

    public String getCiut() {
        return ciut;
    }

    public void setCiut(String ciut) {
        this.ciut = ciut;
    }

    public Boolean getEspecial() {
        return especial;
    }

    public void setEspecial(Boolean especial) {
        this.especial = especial;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

