package com.abuseret.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_COMPENSADA;
import static com.abuseret.logistica.util.Constantes.ORDEN_STATUS_DELIVERED_COMPENSADA_PARCIALMENTE;
import static com.abuseret.logistica.util.Constantes.PAGO_STATUS_COMPENSADO;
import static com.abuseret.logistica.util.Constantes.PAGO_STATUS_COMPENSADO_PARCIALMENTE;

/**
 * Created by perez.juan.jose on 23/11/2016.
 */
@IgnoreExtraProperties
public class Compensacion implements Parcelable {
    private String clienteKey;
    private Cliente cliente;
    private long fechaDeCompensacion;
    private String usuarioCompensador;

    private long numeroDeOrden;

    private Double saldoOrdenAntesDeCompensar;
    private Double saldoOrdenDespuesDeCompensar;
    private int estadoOrdenAntesDeCompensar;
    private int estadoOrdenDespuesDeCompensar;



    private String pagoKey;
    private Double saldoPagoAntesDeCompensar;
    private Double saldoPagoDespuesDeCompensar;
    private int estadoPagoAntesDeCompensar;
    private int estadoPagoDespuesDeCompensar;


    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public Compensacion() {
        // Default constructor required for calls to DataSnapshot.getValue(CabeceraOrden.class)
    }

    public Compensacion( CabeceraOrden cabeceraOrden, Pago pago, String pagoKey, String usuarioCreador) {

        Double saldoOrden=cabeceraOrden.getTotales().getSaldo();
        Double saldoPago=pago.getSaldoAcompensar();

        this.clienteKey=cabeceraOrden.getClienteKey();
        this.cliente=cabeceraOrden.getCliente();
//       fechaDeCompensacion= new date(now());
        this.usuarioCompensador=usuarioCreador;

        this.numeroDeOrden=cabeceraOrden.getNumeroDeOrden();

        this.saldoOrdenAntesDeCompensar=saldoOrden;
        this.estadoOrdenAntesDeCompensar=cabeceraOrden.getEstado();

        this. pagoKey=pagoKey;
        this.saldoPagoAntesDeCompensar=saldoPago;
        this.estadoPagoAntesDeCompensar=pago.getEstado();

        if(saldoOrden>saldoPago){
            this.saldoOrdenDespuesDeCompensar=saldoOrden-saldoPago;
            this.estadoOrdenDespuesDeCompensar=ORDEN_STATUS_DELIVERED_COMPENSADA_PARCIALMENTE;
            this.saldoPagoDespuesDeCompensar=0.0;
            this.estadoPagoDespuesDeCompensar=PAGO_STATUS_COMPENSADO;
        }else if(saldoOrden<saldoPago){
            this.saldoOrdenDespuesDeCompensar=0.0;
            this.estadoOrdenDespuesDeCompensar=ORDEN_STATUS_COMPENSADA;
            this.saldoPagoDespuesDeCompensar=saldoPago-saldoOrden;
            this.estadoPagoDespuesDeCompensar=PAGO_STATUS_COMPENSADO_PARCIALMENTE;
        }else if(saldoOrden==saldoPago){
            this.saldoOrdenDespuesDeCompensar=0.0;
            this.estadoOrdenDespuesDeCompensar=ORDEN_STATUS_COMPENSADA;
            this.saldoPagoDespuesDeCompensar=0.0;
            this.estadoPagoDespuesDeCompensar=PAGO_STATUS_COMPENSADO;
        }




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


    public long getFechaDeCompensacion() {
        return fechaDeCompensacion;
    }

    public void setFechaDeCompensacion(long fechaDeCompensacion) {
        this.fechaDeCompensacion = fechaDeCompensacion;
    }

    public Double getSaldoOrdenAntesDeCompensar() {
        return saldoOrdenAntesDeCompensar;
    }

    public void setSaldoOrdenAntesDeCompensar(Double saldoOrdenAntesDeCompensar) {
        this.saldoOrdenAntesDeCompensar = saldoOrdenAntesDeCompensar;
    }

    public Double getSaldoOrdenDespuesDeCompensar() {
        return saldoOrdenDespuesDeCompensar;
    }

    public void setSaldoOrdenDespuesDeCompensar(Double saldoOrdenDespuesDeCompensar) {
        this.saldoOrdenDespuesDeCompensar = saldoOrdenDespuesDeCompensar;
    }

    public int getEstadoOrdenAntesDeCompensar() {
        return estadoOrdenAntesDeCompensar;
    }

    public void setEstadoOrdenAntesDeCompensar(int estadoOrdenAntesDeCompensar) {
        this.estadoOrdenAntesDeCompensar = estadoOrdenAntesDeCompensar;
    }

    public int getEstadoOrdenDespuesDeCompensar() {
        return estadoOrdenDespuesDeCompensar;
    }

    public void setEstadoOrdenDespuesDeCompensar(int estadoOrdenDespuesDeCompensar) {
        this.estadoOrdenDespuesDeCompensar = estadoOrdenDespuesDeCompensar;
    }

    public Double getSaldoPagoAntesDeCompensar() {
        return saldoPagoAntesDeCompensar;
    }

    public void setSaldoPagoAntesDeCompensar(Double saldoPagoAntesDeCompensar) {
        this.saldoPagoAntesDeCompensar = saldoPagoAntesDeCompensar;
    }

    public Double getSaldoPagoDespuesDeCompensar() {
        return saldoPagoDespuesDeCompensar;
    }

    public void setSaldoPagoDespuesDeCompensar(Double saldoPagoDespuesDeCompensar) {
        this.saldoPagoDespuesDeCompensar = saldoPagoDespuesDeCompensar;
    }

    public int getEstadoPagoAntesDeCompensar() {
        return estadoPagoAntesDeCompensar;
    }

    public void setEstadoPagoAntesDeCompensar(int estadoPagoAntesDeCompensar) {
        this.estadoPagoAntesDeCompensar = estadoPagoAntesDeCompensar;
    }

    public int getEstadoPagoDespuesDeCompensar() {
        return estadoPagoDespuesDeCompensar;
    }

    public void setEstadoPagoDespuesDeCompensar(int estadoPagoDespuesDeCompensar) {
        this.estadoPagoDespuesDeCompensar = estadoPagoDespuesDeCompensar;
    }

    public long getFechaDeCreacion() {
        return fechaDeCompensacion;
    }

    public void setFechaDeCreacion(long fechaDeCreacion) {
        this.fechaDeCompensacion = fechaDeCreacion;
    }



    public long getNumeroDeOrden() {
        return numeroDeOrden;
    }

    public void setNumeroDeOrden(long numeroDeOrden) {
        this.numeroDeOrden = numeroDeOrden;
    }


    public String getUsuarioCompensador() {
        return usuarioCompensador;
    }

    public void setUsuarioCompensador(String usuarioCompensador) {
        this.usuarioCompensador = usuarioCompensador;
    }

    public String getPagoKey() {
        return pagoKey;
    }

    public void setPagoKey(String pagoKey) {
        this.pagoKey = pagoKey;
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
        result.put("usuarioCompensador", usuarioCompensador);
        result.put("numeroDeOrden", numeroDeOrden);
        result.put("saldoOrdenAntesDeCompensar", saldoOrdenAntesDeCompensar);
        result.put("saldoOrdenDespuesDeCompensar", saldoOrdenDespuesDeCompensar);
        result.put("estadoOrdenAntesDeCompensar",estadoOrdenAntesDeCompensar);
        result.put("estadoOrdenDespuesDeCompensar",estadoOrdenDespuesDeCompensar);
        result.put("pagoKey", pagoKey);
        result.put("saldoPagoAntesDeCompensar", saldoPagoAntesDeCompensar);
        result.put("saldoPagoDespuesDeCompensar", saldoPagoDespuesDeCompensar);

        result.put("estadoPagoAntesDeCompensar", estadoPagoAntesDeCompensar);
        result.put("estadoPagoDespuesDeCompensar", estadoPagoDespuesDeCompensar);
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



        parcel.writeLong(fechaDeCompensacion);
        parcel.writeString(usuarioCompensador);
        parcel.writeLong(numeroDeOrden);


        parcel.writeDouble(saldoOrdenAntesDeCompensar);
        parcel.writeDouble(saldoOrdenDespuesDeCompensar);
        parcel.writeInt(estadoOrdenAntesDeCompensar);
        parcel.writeInt(estadoOrdenDespuesDeCompensar);


        parcel.writeString(pagoKey);

        parcel.writeDouble(saldoPagoAntesDeCompensar);
        parcel.writeDouble(saldoPagoDespuesDeCompensar);
        parcel.writeInt(estadoPagoAntesDeCompensar);
        parcel.writeInt(estadoPagoDespuesDeCompensar);


        parcel.writeByte((byte)(semaforo?1:0));


    }



    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Compensacion> CREATOR = new Creator<Compensacion>() {
        public Compensacion createFromParcel(Parcel in) {
            return new Compensacion(in);
        }

        public Compensacion[] newArray(int size) {
            return new Compensacion[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Compensacion(Parcel in) {

        clienteKey= in.readString();
        String s =in.readString();
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

        cliente=c;
        this.fechaDeCompensacion=in.readLong();
        this.usuarioCompensador =in.readString();
        this.numeroDeOrden=in.readLong();


        this.saldoOrdenAntesDeCompensar= in.readDouble();
        this.saldoOrdenDespuesDeCompensar= in.readDouble();
        this.estadoOrdenAntesDeCompensar= in.readInt();
        this.estadoOrdenDespuesDeCompensar= in.readInt();


        this.pagoKey=  in.readString();

        this.saldoPagoAntesDeCompensar= in.readDouble();
        this.saldoPagoDespuesDeCompensar= in.readDouble();
        this.estadoPagoAntesDeCompensar= in.readInt();
        this.estadoPagoDespuesDeCompensar=  in.readInt();

        semaforo =(in.readByte()!=0);

    }

}
