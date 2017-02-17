package com.nextnut.logistica.modelos;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 24/11/2016.
 * <p>
 * Esta clase se utiliza para mostrar los listados de productos, con sus cantidades y precios
 * <p>
 * En ella se guardan los datos del producto, y si este cambia no se actualiza esta entrada. Contiene los datos de su creacion. Ej, precio
 */
@IgnoreExtraProperties
public class Detalle {

    private Producto producto;
    private Double cantidadOrden=0.0;
    private Double cantidadPicking=0.0;
    private Double cantidadEntrega=0.0;
    private Double precio=0.0;
    private Double montoItemOrden=0.0;
    private Double montoItemPicking=0.0;
    private Double montoItemEntrega=0.0;
    private Double montoImpuesto=0.0;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public Detalle() {
        // Default constructor required for calls to DataSnapshot.getValue(Totales.class)
    }
    public Detalle copy(){
            Detalle detalleCppy=new Detalle();
            detalleCppy.setProducto(producto);
            detalleCppy.setCantidadOrden(cantidadOrden);
            detalleCppy.setCantidadPicking(cantidadPicking);
            detalleCppy.setCantidadEntrega(cantidadEntrega);
            detalleCppy.setPrecio(precio);
            detalleCppy.setMontoItemOrden(montoItemOrden);
            detalleCppy.setMontoItemPicking(montoItemPicking);
            detalleCppy.setMontoItemEntrega(montoItemEntrega);
            detalleCppy.setMontoImpuesto(montoImpuesto);
            detalleCppy.setMontoImpuesto(montoImpuesto);
            detalleCppy.setSemaforo(semaforo);
                return detalleCppy;
    }

    public Detalle(Double cantidadOrden, Producto producto, Cliente cliente) {
        this.cantidadOrden = cantidadOrden;
        this.producto = producto;
        if (cliente!=null){
            if(cliente.getEspecial()){
                this.precio=producto.getPrecioEspcecial();
            }else {
                this.precio=producto.getPrecio();
            }
            this.montoItemOrden=cantidadOrden*this.precio;
        }
    }

    public Double getCantidadEntrega() {
        return cantidadEntrega;
    }

    public void setCantidadEntrega(Double cantidadEntrega) {
        this.cantidadEntrega = cantidadEntrega;
    }

    public Double getCantidadOrden() {
        return cantidadOrden;
    }

    public void setCantidadOrden(Double cantidadOrden) {
        this.cantidadOrden = cantidadOrden;
    }

    public Double getCantidadPicking() {
        return cantidadPicking;
    }

    public void setCantidadPicking(Double cantidadPicking) {
        this.cantidadPicking = cantidadPicking;
    }

    public Double getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(Double montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public Double getMontoItemEntrega() {
        return montoItemEntrega;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setMontoItemEntrega(Double montoItemEntrega) {
        this.montoItemEntrega = montoItemEntrega;
    }


    public Double getMontoItemOrden() {
        return montoItemOrden;
    }


    public void setMontoItemOrden(Double montoItemOrden) {
        this.montoItemOrden = montoItemOrden;
    }

    public Double getMontoItemPicking() {
        return montoItemPicking;
    }


    public void setMontoItemPicking(Double montoItemPicking) {
        this.montoItemPicking = montoItemPicking;
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


    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    // Metodos para modificar en detalle en Como Items.

    public void ingresaProductoEnOrden(Double cantidadOrden,  Producto producto, Boolean clienteEspecial) {
        this.cantidadOrden = cantidadOrden;
        Log.d("detalle2", "antes IngresaProductoenOrden-this.precio) " + this.precio);
        this.producto = producto;
        if (clienteEspecial) {
            setPrecio( producto.getPrecioEspcecial());
            Log.d("detalle2", "despues-especial IngresaProductoenOrden-this.precio) " + this.precio);
            this.montoItemOrden = cantidadOrden * producto.getPrecioEspcecial();
        } else {
            setPrecio(producto.getPrecio());
            Log.d("detalle2", "despues -comun- IngresaProductoenOrden-this.precio) " + this.precio);
            this.montoItemOrden = cantidadOrden * producto.getPrecio();
        }

    }
    public void modificarCantidadEnTotalInicial(Detalle detalleOrdenNuevo,  Detalle detalleOrdenAnterior) {
        Log.d("EnTotalInicial", "this.montoItemOrden) " + this.montoItemOrden);
        Log.d("EnTotalInicial", "detalleOrdenNuevo.getMontoItemOrden() " + detalleOrdenNuevo.getMontoItemOrden());
        Log.d("EnTotalInicial", "detalleOrdenAnterior.getMontoItemOrden() " + detalleOrdenAnterior.getMontoItemOrden());
        this.cantidadOrden = this.cantidadOrden+detalleOrdenNuevo.getCantidadOrden()-detalleOrdenAnterior.getCantidadOrden();
        this.montoItemOrden = this.montoItemOrden+detalleOrdenNuevo.getMontoItemOrden()-detalleOrdenAnterior.getMontoItemOrden();

    }

    public void modificarCantidadEnTotalDelivey(Detalle detalleOrdenNuevo,  Detalle detalleOrdenAnterior) {
        Log.d("EnTotalInicial", "this.montoItemOrden) " + this.montoItemOrden);
        Log.d("EnTotalInicial", "detalleOrdenNuevo.getMontoItemOrden() " + detalleOrdenNuevo.getMontoItemOrden());
        Log.d("EnTotalInicial", "detalleOrdenAnterior.getMontoItemOrden() " + detalleOrdenAnterior.getMontoItemOrden());
        this.cantidadEntrega = this.cantidadEntrega+detalleOrdenNuevo.getCantidadEntrega()-detalleOrdenAnterior.getCantidadEntrega();
        this.montoItemEntrega = this.montoItemEntrega+detalleOrdenNuevo.getMontoItemEntrega()-detalleOrdenAnterior.getMontoItemEntrega();

    }


    public void modificarCantidadProductoDeOrden(Double cantidadOrdenNueva){
        this.cantidadOrden = cantidadOrdenNueva;
        this.montoItemOrden = cantidadOrdenNueva * this.precio;

    }

    public void modificarCantidadProductoDeEntrega(Double cantidadOrdenNueva){
        this.cantidadEntrega = cantidadOrdenNueva;
        this.montoItemEntrega = cantidadOrdenNueva * this.precio;

    }

    // Se usa en 7-Al pasar al orden al Picking
    public void ingresaProductoEnPicking(Double cantidad,  Producto producto) {
        // En picking el monto se suma de la orden y en la cabecera del picking en funcion de las ordenes.
        this.cantidadPicking = cantidad;
//       this.montoItemPicking = this.montoItemPicking+monto;

    }

//    Se usa en 7 onClick
    public void modificarCantidadProductoDePicking(Double cantidadNueva){
        this.cantidadPicking = cantidadNueva;
// El monto en picking no se suma. no se sabe exactamente cual es el precio.
//        this.montoItemPicking = cantidadNueva * this.precio;
    }


    public void ingresaProductoEnEntrega(Double cantidad,  Producto producto, Boolean clienteEspecial) {
        this.cantidadEntrega = cantidad;
        Log.d("detalle2", "antes IngresaProductoenOrden-this.precio) " + this.precio);
        this.producto = producto;
        if (clienteEspecial) {
            setPrecio( producto.getPrecioEspcecial());
            Log.d("detalle2", "despues-especial IngresaProductoenOrden-this.precio) " + this.precio);
            this.montoItemEntrega = cantidad * producto.getPrecioEspcecial();
        } else {
            setPrecio(producto.getPrecio());
            Log.d("detalle2", "despues -comun- IngresaProductoenOrden-this.precio) " + this.precio);
            this.montoItemEntrega = cantidad * producto.getPrecio();
        }

    }




    // Metodos para modificar en detalle en Cabeceras

    public void modificarCantidadTotalDeOrden(Double cantidadOrdenNueva, Detalle detalleAnterior){
        //El detalle que ingresa tiene los datos de la posición de una orden.
        // El detalle que estamos modificando tiene los datos Totaliazos por producto.
        // detalle tienen los datos anteriores y precio y MontoTotal del Item (precio x Canidad).
//        Cantidad nueva es la nueva cantidad
        Log.i("detalle1", "this.cantidadOrden "+this.cantidadOrden);
        Log.i("detalle1", "cantidadAnterior "+detalleAnterior.getCantidadOrden());
        Log.i("detalle1", "cantidadMonto "+detalleAnterior.getMontoItemOrden());
        Log.i("detalle1", "cantidadOrdenNueva "+cantidadOrdenNueva);
        this.cantidadOrden = this.cantidadOrden +cantidadOrdenNueva-detalleAnterior.getCantidadOrden();
        this.montoItemOrden = montoItemOrden+cantidadOrdenNueva*detalleAnterior.getPrecio()-detalleAnterior.getMontoItemOrden();
    }

    public void modificarCantidadTotalDePicking(Double cantidadOrdenNueva, Detalle detalleAnterior){
        //  El detalle que ingresa tiene los datos del item anterior
        //  El detalle que estamos modificando tiene los datos Totaliazos por producto.
        //  detalle tienen los datos anteriores y precio y MontoTotal del Item (precio x Canidad).
        //  Cantidad nueva es la nueva cantidad

        this.cantidadPicking = this.cantidadPicking +cantidadOrdenNueva-detalleAnterior.getCantidadPicking();
        this.montoItemPicking = montoItemPicking+cantidadOrdenNueva*detalleAnterior.getPrecio()-detalleAnterior.getMontoItemPicking();
    }
    public void modificarCantidadTotalDeEntrega(Double cantidadOrdenNueva, Detalle detalleAnterior){
        //  El detalle que ingresa tiene los datos del item anterior
        //  El detalle que estamos modificando tiene los datos Totaliazos por producto.
        //  detalle tienen los datos anteriores y precio y MontoTotal del Item (precio x Canidad).
        //  Cantidad nueva es la nueva cantidad

        this.cantidadEntrega = this.cantidadEntrega +cantidadOrdenNueva-detalleAnterior.getCantidadEntrega();
        this.montoItemEntrega = montoItemEntrega+cantidadOrdenNueva*detalleAnterior.getPrecio()-detalleAnterior.getMontoItemEntrega();
    }



    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("producto", producto);
        result.put("cantidadOrden", cantidadOrden);
        result.put("cantidadPicking", cantidadPicking);
        result.put("cantidadEntrega", cantidadEntrega);
        result.put("precio", precio);
        result.put("montoItemOrden", montoItemOrden);
        result.put("montoItemPicking", montoItemPicking);
        result.put("montoItemEntrega", montoItemEntrega);
        result.put("montoImpuesto", montoImpuesto);
        result.put("semaforo", semaforo);

        return result;
    }
    class DetalleTask {
        String key;
        int Producto;

    }
}
