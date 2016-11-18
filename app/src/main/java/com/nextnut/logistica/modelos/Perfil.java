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
public class Perfil implements Parcelable {

    public static int NUMERO_DE_VARIABLES =10;
    private Boolean usuarios;
    private Boolean productos;
    private Boolean clientes;
    private Boolean reportes;
    private Boolean ordenes;
    private Boolean preparar;
    private Boolean entregar;
    private Boolean pagos;
    private Boolean stock;
    private long fechaModificacion;
    private String uid;

    public Perfil() {

    }


    public void setPerfilAdministrador() {
        this.usuarios = true;
        this.productos = true;
        this.clientes = true;
        this.reportes = true;
        this.ordenes = true;
        this.preparar = true;
        this.entregar = true;
        this.pagos = true;
        this.stock = true;

    }


    public Perfil(String uid, Boolean usuarios, Boolean productos, Boolean clientes, Boolean reportes, Boolean ordenes, Boolean preparar, Boolean entregar, Boolean pagos, Boolean stock) {
        this.usuarios = usuarios;
        this.productos = productos;
        this.clientes = clientes;
        this.reportes = reportes;
        this.ordenes = ordenes;
        this.preparar = preparar;
        this.entregar = entregar;
        this.pagos = pagos;
        this.stock = stock;
        this.uid = uid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("usuarios", usuarios);
        result.put("productos", productos);
        result.put("clientes", clientes);
        result.put("reportes", reportes);
        result.put("ordenes", ordenes);
        result.put("preparar", preparar);
        result.put("entregar", entregar);
        result.put("pagos", pagos);
        result.put("stock", stock);
        result.put("uid", uid);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);

        return result;
    }
    // [END post_to_map]


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

    public Boolean getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Boolean usuarios) {
        this.usuarios = usuarios;
    }

    public Boolean getProductos() {
        return productos;
    }

    public void setProductos(Boolean productos) {
        this.productos = productos;
    }

    public Boolean getClientes() {
        return clientes;
    }

    public void setClientes(Boolean clientes) {
        this.clientes = clientes;
    }

    public Boolean getReportes() {
        return reportes;
    }

    public void setReportes(Boolean reportes) {
        this.reportes = reportes;
    }

    public Boolean getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(Boolean ordenes) {
        this.ordenes = ordenes;
    }

    public Boolean getPreparar() {
        return preparar;
    }

    public void setPreparar(Boolean preparar) {
        this.preparar = preparar;
    }

    public Boolean getEntregar() {
        return entregar;
    }

    public void setEntregar(Boolean entregar) {
        this.entregar = entregar;
    }

    public Boolean getPagos() {
        return pagos;
    }

    public void setPagos(Boolean pagos) {
        this.pagos = pagos;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (usuarios ? 1 : 0));
        parcel.writeByte((byte) (productos ? 1 : 0));
        parcel.writeByte((byte) (clientes ? 1 : 0));
        parcel.writeByte((byte) (reportes ? 1 : 0));
        parcel.writeByte((byte) (ordenes ? 1 : 0));
        parcel.writeByte((byte) (preparar ? 1 : 0));
        parcel.writeByte((byte) (entregar ? 1 : 0));
        parcel.writeByte((byte) (pagos ? 1 : 0));
        parcel.writeByte((byte) (stock ? 1 : 0));
        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);

    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Perfil> CREATOR = new Parcelable.Creator<Perfil>() {
        public Perfil createFromParcel(Parcel in) {
            return new Perfil(in);
        }

        public Perfil[] newArray(int size) {
            return new Perfil[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Perfil(Parcel in) {

        usuarios = in.readByte() != 0;     //myBoolean == true if byte != 0;
        productos = in.readByte() != 0;
        clientes = in.readByte() != 0;
        reportes = in.readByte() != 0;
        ordenes = in.readByte() != 0;
        preparar = in.readByte() != 0;
        entregar = in.readByte() != 0;
        pagos = in.readByte() != 0;
        stock = in.readByte() != 0;
        fechaModificacion = in.readLong();
        uid = in.readString();


    }
}

