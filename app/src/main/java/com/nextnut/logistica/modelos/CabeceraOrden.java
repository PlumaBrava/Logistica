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
public class CabeceraOrden  implements Parcelable {
    private String clienteKey;
    private Cliente cliente;
    private long fechaDeCreacion;
    private String usuarioCreador;
    private long numeroDeOrden;
    private Totales totales;
    private long numeroDePickingOrden;
    private long fechaPicking;
    private String usuarioPicking;
    private long fechaEntrega;
    private String usuarioEntrega;
    private int estado;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public CabeceraOrden() {
        // Default constructor required for calls to DataSnapshot.getValue(CabeceraOrden.class)
    }

    public CabeceraOrden(String clienteKey,Cliente cliente,  int estado, Totales totales, String usuarioCreador, long numeroDeOrden) {
        this.cliente = cliente;
        this.clienteKey = clienteKey;
        this.estado = estado;
        this.totales = totales;
        this.usuarioCreador = usuarioCreador;
        this.numeroDeOrden = numeroDeOrden;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getClienteKey() {
        return clienteKey;
    }

    public void setClienteKey(String clienteKey) {
        this.clienteKey = clienteKey;
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

    public long getNumeroDeOrden() {
        return numeroDeOrden;
    }

    public void setNumeroDeOrden(long numeroDeOrden) {
        this.numeroDeOrden = numeroDeOrden;
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

    public void ingresaProductoEnOrden(Double cantidadOrden,  Producto producto, Cliente cliente) {
        this.totales.ingresaProductoEnOrden(cantidadOrden,  producto, cliente);

    }

    public void modificarCantidadProductoEnOrden(Double cantidadOrden,  Detalle detalleAnterior) {
        this.totales.modificarCantidadProductoDeOrden(cantidadOrden,  detalleAnterior);

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


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("clienteKey", clienteKey);
        result.put("cliente", cliente);
        result.put("fechaDeCreacion", ServerValue.TIMESTAMP);
        result.put("usuarioCreador", usuarioCreador);
        result.put("numeroDeOrden", numeroDeOrden);
        result.put("totales", totales);
        result.put("numeroDePickingOrden", numeroDePickingOrden);
        result.put("fechaPicking", fechaPicking);
        result.put("usuarioPicking", usuarioPicking);
        result.put("fechaEntrega", fechaEntrega);
        result.put("fechaEntrega", fechaEntrega);
        result.put("usuarioEntrega", usuarioEntrega);
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


        parcel.writeString(clienteKey);

        //Clientes
        parcel.writeString(cliente.getNombre());
        parcel.writeString(cliente.getApellido());
        parcel.writeString(cliente.getTelefono());
        parcel.writeString(cliente.getFotoCliente());
        parcel.writeString(cliente.getDireccionDeEntrega());
        parcel.writeString(cliente.getCiudad());
        parcel.writeDouble(cliente.getIva());
        parcel.writeString(cliente.getCuit());
        parcel.writeByte((byte)(cliente.getEspecial()?1:0));
        parcel.writeLong(cliente.getFechaModificacion());
        parcel.writeString(cliente.getUid());
        parcel.writeInt(cliente.getTelefonos().size());
        for(Map.Entry<String,String> entry : cliente.getTelefonos().entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
        parcel.writeString(cliente.getPerfilDePrecios());
        Log.d("cliente", "writeString perfilDePrecios "+cliente.getPerfilDePrecios());

        Log.d("orden write cliente", "orden:onComplete: getCliente().getNombre() " + cliente.getNombre());
        parcel.writeLong(fechaDeCreacion);
        parcel.writeString(usuarioCreador);
        parcel.writeLong(numeroDeOrden);

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
        parcel.writeInt(estado);
        parcel.writeByte((byte)(semaforo?1:0));


    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<CabeceraOrden> CREATOR = new Parcelable.Creator<CabeceraOrden>() {
        public CabeceraOrden createFromParcel(Parcel in) {
            return new CabeceraOrden(in);
        }

        public CabeceraOrden[] newArray(int size) {
            return new CabeceraOrden[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private CabeceraOrden(Parcel in) {

        clienteKey= in.readString();
        String s =in.readString();
        Log.d("orden read", "orden:onComplete: getNombre() " + s);
        Cliente c = new Cliente();
       c.setNombre(s);
        c.setApellido(in.readString());
        c.setTelefono(in.readString());
        c.setFotoCliente(in.readString());
        c.setDireccionDeEntrega(in.readString());
        c.setCiudad(in.readString());
        c.setIva(in.readDouble());
        c.setCuit(in.readString());
        c.setEspecial(in.readByte()!=0);
        c.setFechaModificacion(in.readLong());
        c.setUid(in.readString());
        int size = in.readInt();
         Map<String, String> a= new HashMap<>();;
        for(int i = 0; i < size; i++){
            String key = in.readString();
            String value = in.readString();
            a.put(key,value);
        }
        c.setTelefonos(a);
        c.setPerfilDePrecios(in.readString());
        Log.d("cliente", "readString perfilDePrecios "+c.getPerfilDePrecios());

        cliente=c;
        Log.d("orden read", "orden:onComplete: getNombre() " + cliente.getNombre());
        this.fechaDeCreacion=in.readLong();
        this.usuarioCreador=in.readString();
        this.numeroDeOrden=in.readLong();

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
        estado=in.readInt();
        semaforo =(in.readByte()!=0);

    }

}
