package com.nextnut.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 23/11/2016.
 */
@IgnoreExtraProperties
public class CabeceraPicking implements Parcelable {

    private long fechaDeCreacion;
    private String usuarioCreador;

    private Totales totales;
    private long numeroDePickingOrden;
    private long fechaPicking;
    private String usuarioPicking;

    private long fechaEntrega;
    private String usuarioEntrega;
    private String comentario;
    private int estado;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public CabeceraPicking() {
        // Default constructor required for calls to DataSnapshot.getValue(CabeceraOrden.class)
    }


    public CabeceraPicking( String mUserKey, long numeroOrden,int estado){
        this.usuarioCreador=mUserKey;
        this.numeroDePickingOrden=numeroOrden;
        this.comentario="nueva orden";
        this.estado=estado;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public long getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(long fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public long getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(long fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public long getFechaPicking() {
        return fechaPicking;
    }

    public void setFechaPicking(long fechaPicking) {
        this.fechaPicking = fechaPicking;
    }


    public long getNumeroDePickingOrden() {
        return numeroDePickingOrden;
    }

    public void setNumeroDePickingOrden(long numeroDePickingOrden) {
        this.numeroDePickingOrden = numeroDePickingOrden;
    }

    public Totales getTotales() {
        return totales;
    }

    public void setTotales(Totales totales) {
        this.totales = totales;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public String getUsuarioEntrega() {
        return usuarioEntrega;
    }

    public void setUsuarioEntrega(String usuarioEntrega) {
        this.usuarioEntrega = usuarioEntrega;
    }

    public String getUsuarioPicking() {
        return usuarioPicking;
    }

    public void setUsuarioPicking(String usuarioPicking) {
        this.usuarioPicking = usuarioPicking;
    }

    public Boolean getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Boolean semaforo) {
        this.semaforo = semaforo;
    }

    public Boolean sepuedeModificar() {
        return semaforo;
    }

    public void bloquear() {
        this.semaforo = false;
    }

    public void liberar() {
        this.semaforo = true;
    }


    public void ingresaProductoEnOrden(Double cantidadOrden, String productKey, Producto producto, Boolean clienteEspecial) {
        this.totales.ingresaProductoEnOrden(cantidadOrden,  producto, clienteEspecial);

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fechaDeCreacion", ServerValue.TIMESTAMP);
        result.put("usuarioCreador", usuarioCreador);
        result.put("totales", totales);
        result.put("numeroDePickingOrden", numeroDePickingOrden);
        result.put("fechaPicking", fechaPicking);
        result.put("usuarioPicking", usuarioPicking);
        result.put("fechaEntrega", fechaEntrega);
        result.put("usuarioEntrega", usuarioEntrega);
        result.put("comentario", comentario);
        result.put("estado", estado);
        result.put("semaforo", semaforo);


        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }




    @Override
    public void writeToParcel(Parcel parcel, int i) {




        parcel.writeLong(fechaDeCreacion);
        parcel.writeString(usuarioCreador);


        //Totales
        parcel.writeLong(totales.getCantidadDeOrdenesClientes());
        parcel.writeLong(totales.getCantidadDeOrdenesPicking());
        parcel.writeLong(totales.getCantidadDeProductosDiferentes());
        parcel.writeDouble(totales.getMontoEnOrdenes());
        parcel.writeDouble(totales.getMontoEnPicking());
        parcel.writeDouble(totales.getMontoEntregado());
        parcel.writeDouble(totales.getMontoPagado());
        parcel.writeDouble(totales.getSaldo());
        parcel.writeDouble(totales.getMontoImpuesto());

        parcel.writeLong(numeroDePickingOrden);
        parcel.writeLong(fechaPicking);
        parcel.writeString(usuarioPicking);
        parcel.writeLong(fechaEntrega);
        parcel.writeString(usuarioEntrega);
        parcel.writeString(comentario);
        parcel.writeInt(estado);
        parcel.writeByte((byte)(semaforo?1:0));


    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<CabeceraPicking> CREATOR = new Creator<CabeceraPicking>() {
        public CabeceraPicking createFromParcel(Parcel in) {
            return new CabeceraPicking(in);
        }

        public CabeceraPicking[] newArray(int size) {
            return new CabeceraPicking[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private CabeceraPicking(Parcel in) {


        this.fechaDeCreacion=in.readLong();
        this.usuarioCreador=in.readString();



//Totales
        Totales t = new Totales();

        t.setCantidadDeOrdenesClientes( in.readLong());
        t.setCantidadDeOrdenesPicking(in.readLong());
        t.setCantidadDeProductosDiferentes(in.readLong());
        t.setMontoEnOrdenes(in.readDouble());
        t.setMontoEnPicking(in.readDouble());
        t.setMontoEntregado( in.readDouble());
        t.setMontoPagado(in.readDouble());
        t.setSaldo(in.readDouble());
        t.setMontoImpuesto(in.readDouble());

        totales=t;


        Log.d("orden read", "orden:onComplete: totales " + totales.getCantidadDeOrdenesClientes());

        numeroDePickingOrden=in.readLong();
        fechaPicking=in.readLong();
        usuarioPicking=in.readString();
        fechaEntrega=in.readLong();
        usuarioEntrega=in.readString();
        comentario=in.readString();
        estado=in.readInt();
        semaforo =(in.readByte()!=0);

    }

}
