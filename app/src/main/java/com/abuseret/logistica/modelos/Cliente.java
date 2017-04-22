package com.abuseret.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
    private String IndiceNombreApellido;
    private String telefono;
    private String fotoCliente;
    private String direccionDeEntrega;
    private String ciudad;
    private double iva;
    private String cuit;

    public String getIndiceNombreApellido() {
        return IndiceNombreApellido;
    }

    private Boolean especial;
    private String perfilDePrecios;
    private long fechaModificacion;
    private Map<String, String> telefonos= new HashMap<>();
    private String uid;

    public Map<String, String> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(Map<String, String> telefonos) {
        this.telefonos = telefonos;
    }



    public Cliente() {
        // Default constructor required for calls to DataSnapshot.getValue(Cliente.class)
    }

    public Cliente(String uid,String nombre, String apellido, String telefono, String fotoCliente, String direccionDeEntrega, String ciudad, double iva, String cuit, Boolean especial,Map<String, String> telefonos,String perfilDePrecios) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.IndiceNombreApellido=(nombre+apellido).toLowerCase();
        this.telefono = telefono;
        this.fotoCliente = fotoCliente;
        this.direccionDeEntrega = direccionDeEntrega;
        this.ciudad = ciudad;
        this.iva = iva;
        this.cuit = cuit;
        this.especial = especial;
        this.uid = uid;
        this.telefonos =telefonos;
        this.perfilDePrecios=perfilDePrecios;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        result.put("apellido", apellido);
        result.put("IndiceNombreApellido",IndiceNombreApellido);
        result.put("fotoCliente", fotoCliente);
        result.put("direccionDeEntrega", direccionDeEntrega);
        result.put("ciudad", ciudad);
        result.put("iva", iva);
        result.put("cuit", cuit);
        result.put("especial", especial);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("uid", uid);
        result.put("telefonos",telefonos);
        result.put("perfilDePrecios",perfilDePrecios);

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

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
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

    public String getPerfilDePrecios() {
        return perfilDePrecios;
    }

    public void setPerfilDePrecios(String perfilDePrecios) {
        this.perfilDePrecios = perfilDePrecios;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(nombre);
        parcel.writeString(apellido);
        parcel.writeString(IndiceNombreApellido);
        parcel.writeString(telefono);
        parcel.writeString(fotoCliente);
        parcel.writeString(direccionDeEntrega);
        parcel.writeString(ciudad);
        parcel.writeDouble(iva);
        parcel.writeString(cuit);
        parcel.writeByte((byte)(especial?1:0));
        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);
        parcel.writeInt(telefonos.size());
        for(Map.Entry<String,String> entry : telefonos.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
        parcel.writeString(perfilDePrecios);
        Log.d("cliente", "writeString perfilDePrecios "+perfilDePrecios);
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
        IndiceNombreApellido=in.readString();
        telefono =in.readString();
        fotoCliente= in.readString();
        direccionDeEntrega=in.readString();
        ciudad=in.readString();
        iva =in.readDouble();
        cuit=in.readString();
        especial= in.readByte()!=0;
        fechaModificacion=in.readLong();
        uid= in.readString();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            String value = in.readString();
            telefonos.put(key,value);
        }
        perfilDePrecios=in.readString();
        Log.d("cliente", "readString perfilDePrecios "+perfilDePrecios);


    }
}

