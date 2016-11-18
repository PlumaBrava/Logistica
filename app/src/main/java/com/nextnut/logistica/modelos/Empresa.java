package com.nextnut.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Empresa implements Parcelable{
    public static int NUMERO_DE_VARIABLES =9;
    private String nombre;
    private String cuit;
    private String ciudad;
    private String direccion;
    private String codigoPostal;
    private String telefono;
    private long fechaModificacion;
    private String uid;

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

        result.put("nombre", nombre);
        result.put("cuit", cuit);
        result.put("ciudad", ciudad);
        result.put("direccion", direccion);
        result.put("codigoPostal", codigoPostal);
        result.put("telefono", telefono);
        result.put("logo", logo);
        result.put("uid", uid);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        return result;
    }
    // [END post_to_map]


    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(long fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(cuit);
        parcel.writeString(ciudad);
        parcel.writeString(direccion);
        parcel.writeString(codigoPostal);
        parcel.writeString(telefono);
        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);
        parcel.writeString(logo);

    }
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Empresa> CREATOR = new Parcelable.Creator<Empresa>() {
        public Empresa createFromParcel(Parcel in) {
            return new Empresa(in);
        }

        public Empresa[] newArray(int size) {
            return new Empresa[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Empresa(Parcel in) {
        nombre=in.readString();
        cuit=in.readString();
        ciudad=in.readString();
        direccion=in.readString();
        codigoPostal= in.readString();
        telefono= in.readString();
        fechaModificacion= in.readLong();
        uid= in.readString();
        logo =in.readString();

    }
}
// [END blog_user_class]

