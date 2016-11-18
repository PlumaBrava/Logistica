package com.nextnut.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 31/10/2016.
 */
@IgnoreExtraProperties
public class Cliente implements Parcelable {

    private String nombre;
    private String apellido;
    private String telefono;
    private String fotoCliente;
    private String direccionDeEntrega;
    private String ciudad;
    private long iva;
    private String cuit;
    private Boolean especial;
    private long fechaModificacion;
    private String uid;

    public Cliente() {
        // Default constructor required for calls to DataSnapshot.getValue(Cliente.class)
    }

    public Cliente(String uid,String nombre, String apellido, String telefono, String fotoCliente, String direccionDeEntrega, String ciudad, long iva, String cuit, Boolean especial) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.fotoCliente = fotoCliente;
        this.direccionDeEntrega = direccionDeEntrega;
        this.ciudad = ciudad;
        this.iva = iva;
        this.cuit = cuit;
        this.especial = especial;
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
        result.put("cuit", cuit);
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

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String ciut) {
        this.cuit = ciut;
    }

    public Boolean getEspecial() {
        return especial;
    }

    public void setEspecial(Boolean especial) {
        this.especial = especial;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(nombre);
        parcel.writeString(apellido);
        parcel.writeString(telefono);
        parcel.writeString(fotoCliente);
        parcel.writeString(direccionDeEntrega);
        parcel.writeString(ciudad);
        parcel.writeLong(iva);
        parcel.writeString(cuit);
        parcel.writeByte((byte)(especial?1:0));
        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);

    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Cliente> CREATOR = new Parcelable.Creator<Cliente>() {
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Cliente(Parcel in) {
        nombre=in.readString();
        apellido=in.readString();
        telefono =in.readString();
        fotoCliente= in.readString();
        direccionDeEntrega=in.readString();
        ciudad=in.readString();
        iva =in.readLong();
        cuit=in.readString();
        especial= in.readByte()!=0;
        fechaModificacion=in.readLong();
        uid= in.readString();

    }
}

