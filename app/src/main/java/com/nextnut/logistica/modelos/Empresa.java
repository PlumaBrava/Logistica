package com.nextnut.logistica.modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 25/10/2016.
 */
// [START blog_user_class]
@IgnoreExtraProperties
public class Empresa {
    public String uid;
    private String nombre;
    private String cuit;
    private String ciudad;
    private String direccion;
    private String codigoPostal;
    private String telefono;

    private String logo;



    public Empresa() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Empresa(String uid,String nombre, String cuit, String ciudad, String direccion, String codigoPostal,String telefono,String logo) {
        this.uid=uid;
        this.nombre = nombre;
        this.cuit = cuit;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.telefono = telefono;
        this.logo = logo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid (String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPosta(String codigoPosta) {
        this.codigoPostal = codigoPosta;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("nombre", nombre);
        result.put("cuit", cuit);
        result.put("ciudad", ciudad);
        result.put("direccion", direccion);
        result.put("codigoPostal", codigoPostal);
        result.put("telefono", telefono);
        result.put("logo", logo);

        return result;
    }
    // [END post_to_map]


}
// [END blog_user_class]

