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
public class Pago implements Parcelable {
    private String clienteKey;
    private Cliente cliente;
    private long fechaDePago;
    private String usuarioCreador;
    private Double monto;
    private Double saldoAcompensar;



    private String tipoDePago;
    private String chequeBanco;
    private String chequeNumero;
    private String chequeEmisor;
    private String chequeFotoPath;
    private long chequeFecha;
    private int estado;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public String getChequeBanco() {
        return chequeBanco;
    }

    public void setChequeBanco(String chequeBanco) {
        this.chequeBanco = chequeBanco;
    }

    public String getChequeEmisor() {
        return chequeEmisor;
    }

    public void setChequeEmisor(String chequeEmisor) {
        this.chequeEmisor = chequeEmisor;
    }

    public long getChequeFecha() {
        return chequeFecha;
    }

    public void setChequeFecha(long chequeFecha) {
        this.chequeFecha = chequeFecha;
    }

    public String getChequeFotoPath() {
        return chequeFotoPath;
    }

    public void setChequeFotoPath(String chequeFotoPath) {
        this.chequeFotoPath = chequeFotoPath;
    }

    public String getChequeNumero() {
        return chequeNumero;
    }

    public void setChequeNumero(String chequeNumero) {
        this.chequeNumero = chequeNumero;
    }

    public static Creator<Pago> getCREATOR() {
        return CREATOR;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipoDePago() {
        return tipoDePago;
    }

    public void setTipoDePago(String tipoDePago) {
        this.tipoDePago = tipoDePago;
    }

    public String getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(String usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Double getSaldoAcompensar() {
        return saldoAcompensar;
    }

    public void setSaldoAcompensar(Double saldoAcompensar) {
        this.saldoAcompensar = saldoAcompensar;
    }

    public Pago() {
        // Default constructor required for calls to DataSnapshot.getValue(CabeceraOrden.class)
    }

    public Pago( String clienteKey,Cliente cliente, String tipoDePago, Double monto, String chequeBanco, long fechaDeCheque, String chequeEmisor, String chequeFotoPath, String chequeNumero,  String usuarioCreador) {
        this.chequeBanco = chequeBanco;
        this.chequeEmisor = chequeEmisor;
        this.chequeFotoPath = chequeFotoPath;
        this.chequeNumero = chequeNumero;
        this.cliente = cliente;
        this.clienteKey = clienteKey;
        this.chequeFecha = fechaDeCheque;
        this.monto = monto;
        this.usuarioCreador = usuarioCreador;
        this.tipoDePago = tipoDePago;
        this.saldoAcompensar=monto;

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

    public long getFechaDePago() {
        return fechaDePago;
    }

    public void setFechaDePago(long fechaDePago) {
        this.fechaDePago = fechaDePago;
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
        result.put("fechaDePago", ServerValue.TIMESTAMP);
        result.put("usuarioCreador", usuarioCreador);
        result.put("monto", monto);
        result.put("tipoDePago",tipoDePago);
        result.put("chequeBanco",chequeBanco);
        result.put("chequeNumero",chequeNumero);
        result.put("chequeEmisor",chequeEmisor);
        result.put("chequeFotoPath",chequeFotoPath);
        result.put("chequeFecha",chequeFecha);
        result.put("estado", estado);
        result.put("semaforo", semaforo);
        result.put("saldoAcompensar", saldoAcompensar);

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


        Log.d("Pago", "orden:onComplete: getCliente().getNombre() " + cliente.getNombre());
        parcel.writeLong(fechaDePago);
        parcel.writeString(usuarioCreador);
        parcel.writeDouble(monto);
        parcel.writeDouble(saldoAcompensar);
        parcel.writeString(tipoDePago);
        parcel.writeString(chequeBanco);
        parcel.writeString(chequeNumero);
        parcel.writeString(chequeEmisor);
        parcel.writeString(chequeFotoPath);
        parcel.writeLong(chequeFecha);
        parcel.writeInt(estado);
        parcel.writeByte((byte)(semaforo?1:0));


    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Pago> CREATOR = new Creator<Pago>() {
        public Pago createFromParcel(Parcel in) {
            return new Pago(in);
        }

        public Pago[] newArray(int size) {
            return new Pago[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Pago(Parcel in) {

        clienteKey= in.readString();
        String s =in.readString();
        Log.d("pago read", "orden:onComplete: getNombre() " + s);
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
        Log.d("pago read", "orden:onComplete: getNombre() " + cliente.getNombre());
        this.fechaDePago =in.readLong();
        this.usuarioCreador=in.readString();
        this.monto=in.readDouble();
        this.saldoAcompensar=in.readDouble();
        this.tipoDePago= in.readString();
        this.chequeBanco= in.readString();
        this.chequeNumero= in.readString();
        this.chequeEmisor= in.readString();
        this.chequeFotoPath= in.readString();
        this.chequeFecha= in.readLong();
        this.estado=in.readInt();
        this.semaforo =(in.readByte()!=0);

    }

}
