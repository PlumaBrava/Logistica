package com.abuseret.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 23/11/2016.
 * <p>
 * Totales se utilizara para acumular los totales de las ramas del esquema
 * <p>
 * Acumulara el total de ordenes (de cliente o de Picking)
 * Total de procutos distintos.
 * Montos en ordenes, pagodos y saldos a pagar.
 * <p>
 * Tambien tiene los metodos para actualizar estas variables.
 * <p>
 * Serán llamados para ejecutarlas en un  Transaction.Handler() para administar las concureencias.
 * <p>
 * No lleva ni User ni Fecha porque es para totalizar. Se usará uno por logar del equema que queremos sumar.
 */
@IgnoreExtraProperties
public class Totales implements Parcelable {

    private long cantidadDeOrdenesClientes=0;         //Ademas de contar la canidad de ordenes de Cliente, servira de Indice para Ordenes
    private long cantidadDeOrdenesPicking=0;          //Ademas de contar la canidad de ordenes de Picking, servira de Indice para Ordenes
    private long cantidadDeProductosDiferentes=0;     //Numero de productos
    private Double montoEnOrdenes=0.0;                  // Dinero en Ordenes de Cliente
    private Double montoEnPicking=0.0;                  // Dinero en Ordenes de Picking
    private Double montoEntregado=0.0;                  // Dinero a cobrar al cliente (total)
    private Double montoPagado=0.0;                     // Dinero a pagado por el cliente
    private Double saldo=0.0;                           // Dinero que aun debe el cliente
    private Double montoImpuesto=0.0;                   // Monto de dinero que corresponde a Impuestos. Esta incluido en lo que se cobra al cliente.

    public Totales() {
        // Default constructor required for calls to DataSnapshot.getValue(Totales.class)
    }

    public Totales(long cantidadDeOrdenesClientes, long cantidadDeOrdenesPicking, long cantidadDeProductosDiferentes, Double montoEnOrdenes, Double montoEnPicking, Double montoEntregado, Double montoPagado, Double saldo, Double montoImpuesto) {
        this.cantidadDeOrdenesClientes = cantidadDeOrdenesClientes;
        this.cantidadDeOrdenesPicking = cantidadDeOrdenesPicking;
        this.cantidadDeProductosDiferentes = cantidadDeProductosDiferentes;
        this.montoEnOrdenes = montoEnOrdenes;
        this.montoEnPicking = montoEnPicking;
        this.montoEntregado = montoEntregado;
        this.montoPagado = montoPagado;
        this.saldo = saldo;
        this.montoImpuesto = montoImpuesto;

    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public long getCantidadDeOrdenesClientes() {
        return cantidadDeOrdenesClientes;
    }

    public void setCantidadDeOrdenesClientes(long cantidadDeOrdenesClientes) {
        this.cantidadDeOrdenesClientes = cantidadDeOrdenesClientes;
    }

    public long getCantidadDeOrdenesPicking() {
        return cantidadDeOrdenesPicking;
    }

    public void setCantidadDeOrdenesPicking(long cantidadDeOrdenesPicking) {
        this.cantidadDeOrdenesPicking = cantidadDeOrdenesPicking;
    }

    public long getCantidadDeProductosDiferentes() {
        return cantidadDeProductosDiferentes;
    }

    public void setCantidadDeProductosDiferentes(long cantidadDeProductosDiferentes) {
        this.cantidadDeProductosDiferentes = cantidadDeProductosDiferentes;
    }

    public Double getMontoEnOrdenes() {
        return montoEnOrdenes;
    }

    public void setMontoEnOrdenes(Double montoEnOrdenes) {
        this.montoEnOrdenes = montoEnOrdenes;
    }

    public Double getMontoEnPicking() {
        return montoEnPicking;
    }

    public void setMontoEnPicking(Double montoEnPicking) {
        this.montoEnPicking = montoEnPicking;
    }

    public Double getMontoEntregado() {
        return montoEntregado;
    }

    public void setMontoEntregado(Double montoEntregado) {
        this.montoEntregado = montoEntregado;
    }

    public Double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Double getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(Double montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public void ingresaProductoEnOrden(Double cantidadOrden, Producto producto, Cliente cliente){
        cantidadDeProductosDiferentes=cantidadDeProductosDiferentes+1;
        Log.d("Totales", "this.montoEnOrdenes " +this.montoEnOrdenes);
        Log.d("Totales", "cantidadOrden " +cantidadOrden);
        Log.d("Totales", "cliente.getPerfilDePrecios() " +cliente.getPerfilDePrecios());

        Log.d("Totales", "producto.getPrecioEspcecial() " +producto.getPrecioEspecialPerfil(cliente.getPerfilDePrecios()));
        Log.d("Totales", "producto.getPerfilDePrecio " +producto.getPrecioParaPerfil(cliente.getPerfilDePrecios()));
        if (cliente.getEspecial()) {
            this.montoEnOrdenes = this.montoEnOrdenes+cantidadOrden * producto.getPrecioEspecialPerfil(cliente.getPerfilDePrecios());
        } else {
            this.montoEnOrdenes = this.montoEnOrdenes+ cantidadOrden * producto.getPrecioParaPerfil(cliente.getPerfilDePrecios());
        }
    }

    public void sacarProductoDeOrden(Detalle detalle){
        // detalle tiene los datos del producto que se saca.

        cantidadDeProductosDiferentes=cantidadDeProductosDiferentes-1;
        this.montoEnOrdenes = this.montoEnOrdenes-detalle.getCantidadOrden() * detalle.getPrecio();
    }

    public void modificarCantidadProductoDeOrden(Double cantidadOrdenNueva,Detalle detalle){
//        cantidadDeProductosDiferentes no se modifica. Sigue igual
        // El producto y tipo de cliente no son necesarios porque ya los use para actualizar el precio
        // El producto no cambiará y si lo hace. habria que actualizar lo que corresponda desde el cambio de producto
        // Si cambia el precio, o algo del producto. lo puedo sacar de la orden y cargar el nuevo con el cambio.

        Log.d("Totales", "this.montoEnOrdenes " +this.montoEnOrdenes);
        Log.d("Totales", "cantidadOrdenAnterior " +detalle.getCantidadOrden());
        Log.d("Totales", "cantidadOrdenNueva " +cantidadOrdenNueva);
        this.montoEnOrdenes = this.montoEnOrdenes+cantidadOrdenNueva * detalle.getPrecio()-detalle.getCantidadOrden() * detalle.getPrecio();

    }

    public void modificarCantidadProductoDeEntrega(Double cantidadEntregaNueva,Detalle detalle){
        //
        // El producto y tipo de cliente. Detalle ya tiene todos los datos.
        // Se actualiza el monto Entregado de la orden.
        // Si cambia el precio, o algo del producto. lo puedo sacar de la orden y cargar el nuevo con el cambio.

        Log.d("Totales", "this.montoEneNTREGA " +this.montoEntregado);
        Log.d("Totales", "cantidadOrdenAnteriorEntrega " +detalle.getCantidadEntrega());
        Log.d("Totales", "cantidadEntregaNueva " +cantidadEntregaNueva);
        this.montoEntregado = this.montoEntregado+cantidadEntregaNueva * detalle.getPrecio()-detalle.getCantidadEntrega() * detalle.getPrecio();

    }

    public void setSumaMontoEntregado(CabeceraOrden cabeceraOrden) {
        this.montoEntregado = this.montoEntregado + cabeceraOrden.getTotales().getMontoEntregado();
        if (!cabeceraOrden.getCliente().getEspecial()){
            this.montoImpuesto = this.montoImpuesto + cabeceraOrden.getTotales().getMontoImpuesto();
    }
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cantidadDeOrdenesClientes", cantidadDeOrdenesClientes);
        result.put("cantidadDeOrdenesPicking", cantidadDeOrdenesPicking);
        result.put("cantidadDeProductosDiferentes", cantidadDeProductosDiferentes);
        result.put("montoEnOrdenes", montoEnOrdenes);
        result.put("montoEnPicking", montoEnPicking);
        result.put("montoEntregado", montoEntregado);
        result.put("montoPagado", montoPagado);
        result.put("saldo", saldo);

        return result;
    }

    /*
    *   Cobrar ingrea el cobro realizado.
    *   Si el saldo - monto cobrado < 0, deja el saldo en 0 retorna el lo que resta para compenzar otra cosa.
    *
    * */
    public Double cobrar(Double montoCobrado) {
        if (saldo - montoCobrado >= 0) {
            saldo = saldo - montoCobrado;
            return 0.0;
        } else {
            double cambio = montoCobrado - saldo;
            saldo = 0.0;
            return cambio;
        }
    }

    /*
    *  Ingresar producto en una orden de cliente-
    *
    *  Suma un producto y el monto correspondiente
    *  El impuesto esta incluido en el monto, no se suma.
    * */

    public void ingresaProductoEnOrden(Double monto, Double impueto) {
        cantidadDeProductosDiferentes = cantidadDeProductosDiferentes + 1;
        montoEnOrdenes = montoEnOrdenes + monto;
        montoImpuesto = montoImpuesto + impueto;
        saldo = saldo + monto;
    }

    /*
    *  Modifica producto en una orden de cliente-
    *
    *  Suma un producto y el monto correspondiente
    *  El impuesto esta incluido en el monto, no se suma.
    * */

    public void modificaProductoEnOrden(Double montoAnterio, Double impuetoAnterior, Double montoPosterior, Double impuetoPosterior) {
        // cantidadDeProductosDiferentes = cantidadDeProductosDiferentes; // no se modifica sigue existiendo el producto
        montoEnOrdenes = montoEnOrdenes - montoAnterio + montoPosterior;
        montoImpuesto = montoImpuesto - impuetoAnterior + impuetoPosterior;
        saldo = saldo - montoAnterio + montoPosterior;
    }

    /*
    *  Eliminar producto en una orden de cliente-
    *
    *  Suma un producto y el monto correspondiente
    *  El impuesto esta incluido en el monto, no se suma.
    * */

    public void eliminarProductoEnOrden(Double monto, Double impueto) {
        cantidadDeProductosDiferentes = cantidadDeProductosDiferentes - 1;
        montoEnOrdenes = montoEnOrdenes - monto;
        montoImpuesto = montoImpuesto - impueto;
        saldo = saldo - monto;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(cantidadDeOrdenesClientes);
        parcel.writeLong(cantidadDeOrdenesPicking);
        parcel.writeLong(cantidadDeProductosDiferentes);
        parcel.writeDouble(montoEnOrdenes);
        parcel.writeDouble(montoEnPicking);
        parcel.writeDouble(montoEntregado);
        parcel.writeDouble(montoPagado);
        parcel.writeDouble(saldo);
        parcel.writeDouble(montoImpuesto);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Totales> CREATOR = new Parcelable.Creator<Totales>() {
        public Totales createFromParcel(Parcel in) {
            return new Totales(in);
        }

        public Totales[] newArray(int size) {
            return new Totales[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Totales(Parcel in) {



        cantidadDeOrdenesClientes= in.readLong();
        cantidadDeOrdenesPicking=in.readLong();
        cantidadDeProductosDiferentes=in.readLong();
        montoEnOrdenes=in.readDouble();
        montoEnPicking=in.readDouble();
        montoEntregado= in.readDouble();
        montoPagado=in.readDouble();
        saldo=in.readDouble();
        montoImpuesto=in.readDouble();
    }


}
