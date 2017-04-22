package com.abuseret.logistica.modelos;

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
public class Almacen implements Parcelable{
    private String nombre;
    private String ciudad;
    private String direccion;
    private String reponsable;
    private String tipodeAlmacen;
    private String telefono;
    private long fechaModificacion;
    private String uid;
    private String almacenKey;

    private String logo;



    public Almacen() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Almacen(String uid, String nombre, String responsable, String ciudad, String direccion, String tipoAlmacen, String telefono, String logo, String almacenKey) {
        this.uid=uid;
        this.nombre = nombre;
        this.reponsable = responsable;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.tipodeAlmacen = tipoAlmacen;
        this.telefono = telefono;
        this.logo = logo;
        this.almacenKey = almacenKey;
    }

    public String getAlmacenKey() {
        return almacenKey;
    }

    public void setAlmacenKey(String almacenKey) {
        this.almacenKey = almacenKey;
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

    public String getReponsable() {
        return reponsable;
    }

    public void setReponsable(String reponsable) {
        this.reponsable = reponsable;
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

    public String getTipodeAlmacen() {
        return tipodeAlmacen;
    }

    public void setCodigoPosta(String codigoPosta) {
        this.tipodeAlmacen = codigoPosta;
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
        result.put("reponsable", reponsable);
        result.put("ciudad", ciudad);
        result.put("direccion", direccion);
        result.put("tipodeAlmacen", tipodeAlmacen);
        result.put("telefono", telefono);
        result.put("logo", logo);
        result.put("uid", uid);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("almacenKey", almacenKey);
        return result;
    }
    // [END post_to_map]


    public void setTipodeAlmacen(String tipodeAlmacen) {
        this.tipodeAlmacen = tipodeAlmacen;
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
        parcel.writeString(reponsable);
        parcel.writeString(ciudad);
        parcel.writeString(direccion);
        parcel.writeString(tipodeAlmacen);
        parcel.writeString(telefono);
        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);
        parcel.writeString(logo);
        parcel.writeString(almacenKey);

    }
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Almacen> CREATOR = new Creator<Almacen>() {
        public Almacen createFromParcel(Parcel in) {
            return new Almacen(in);
        }

        public Almacen[] newArray(int size) {
            return new Almacen[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Almacen(Parcel in) {
        nombre=in.readString();
        reponsable =in.readString();
        ciudad=in.readString();
        direccion=in.readString();
        tipodeAlmacen = in.readString();
        telefono= in.readString();
        fechaModificacion= in.readLong();
        uid= in.readString();
        logo =in.readString();
        almacenKey =in.readString();

    }
}
// [END blog_user_class]

