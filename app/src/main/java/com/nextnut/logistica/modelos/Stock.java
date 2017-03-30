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
public class Stock {

    private Producto producto;
    private Double cantidadUnidadesEnStock =0.0;
    private Double cantidadUnidadesSolicitadas =0.0;
    private Double cantidadEntrega=0.0;
    private Double precio=0.0;
    private Double kilosEnStock =0.0;
    private Double kilosSolicitados =0.0;
    private Double montoItemEntrega=0.0;
    private Double montoImpuesto=0.0;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public Stock() {
        // Default constructor required for calls to DataSnapshot.getValue(Totales.class)
    }
    public Stock copy(){
            Stock detalleCppy=new Stock();
            detalleCppy.setProducto(producto);
            detalleCppy.setCantidadUnidadesEnStock(cantidadUnidadesEnStock);
            detalleCppy.setCantidadUnidadesSolicitadas(cantidadUnidadesSolicitadas);
            detalleCppy.setCantidadEntrega(cantidadEntrega);
            detalleCppy.setPrecio(precio);
            detalleCppy.setKilosEnStock(kilosEnStock);
            detalleCppy.setKilosSolicitados(kilosSolicitados);
            detalleCppy.setMontoItemEntrega(montoItemEntrega);
            detalleCppy.setMontoImpuesto(montoImpuesto);
            detalleCppy.setMontoImpuesto(montoImpuesto);
            detalleCppy.setSemaforo(semaforo);
                return detalleCppy;
    }



    public Double getCantidadEntrega() {
        return cantidadEntrega;
    }

    public void setCantidadEntrega(Double cantidadEntrega) {
        this.cantidadEntrega = cantidadEntrega;
    }

    public Double getCantidadUnidadesEnStock() {
        return cantidadUnidadesEnStock;
    }

    public void setCantidadUnidadesEnStock(Double cantidadUnidadesEnStock) {
        this.cantidadUnidadesEnStock = cantidadUnidadesEnStock;
    }

    public Double getCantidadUnidadesSolicitadas() {
        return cantidadUnidadesSolicitadas;
    }

    public void setCantidadUnidadesSolicitadas(Double cantidadUnidadesSolicitadas) {
        this.cantidadUnidadesSolicitadas = cantidadUnidadesSolicitadas;
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


    public Double getKilosEnStock() {
        return kilosEnStock;
    }


    public void setKilosEnStock(Double kilosEnStock) {
        this.kilosEnStock = kilosEnStock;
    }

    public Double getKilosSolicitados() {
        return kilosSolicitados;
    }


    public void setKilosSolicitados(Double kilosSolicitados) {
        this.kilosSolicitados = kilosSolicitados;
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
//        this.cantidadUnidadesEnStock = cantidadUnidadesEnStock;
        setCantidadUnidadesEnStock(cantidadOrden);
        Log.d("detalle2", "antes IngresaProductoenOrden-this.precio) " + this.precio);
//        this.producto = producto;
        setProducto(producto);
        if (clienteEspecial) {
            setPrecio( producto.getPrecioEspcecial());
            Log.d("detalle2", "despues-especial IngresaProductoenOrden-this.precio) " + this.precio);
//            this.kilosEnStock = cantidadUnidadesEnStock * producto.getPrecioEspcecial();
            setKilosEnStock(cantidadOrden * producto.getPrecioEspcecial());
        } else {
            setPrecio(producto.getPrecio());
            Log.d("detalle2", "despues -comun- IngresaProductoenOrden-this.precio) " + this.precio);
//            this.kilosEnStock = cantidadUnidadesEnStock * producto.getPrecio();
            setKilosEnStock(cantidadOrden * producto.getPrecio());

        }

    }
    public void modificarCantidadEnTotalInicial(Stock detalleOrdenNuevo, Stock detalleOrdenAnterior) {
        Log.d("EnTotalInicial", "this.kilosEnStock) " + this.kilosEnStock);
        Log.d("EnTotalInicial", "detalleOrdenNuevo.getKilosEnStock() " + detalleOrdenNuevo.getKilosEnStock());
        Log.d("EnTotalInicial", "detalleOrdenAnterior.getKilosEnStock() " + detalleOrdenAnterior.getKilosEnStock());
        this.cantidadUnidadesEnStock = this.cantidadUnidadesEnStock +detalleOrdenNuevo.getCantidadUnidadesEnStock()-detalleOrdenAnterior.getCantidadUnidadesEnStock();
        this.kilosEnStock = this.kilosEnStock +detalleOrdenNuevo.getKilosEnStock()-detalleOrdenAnterior.getKilosEnStock();

    }

    public void modificarCantidadEnTotalDelivey(Stock detalleOrdenNuevo, Stock detalleOrdenAnterior) {
        Log.d("EnTotalDelivey", "this.kilosEnStock) " + this.kilosEnStock);
        Log.d("EnTotalDelivey", "detalleOrdenNuevo.getCantidadEntrega()" + detalleOrdenNuevo.getCantidadEntrega());
        Log.d("EnTotalDelivey", "detalleOrdenNuevo.getMontoItemEntrega()" + detalleOrdenNuevo.getMontoItemEntrega());
        Log.d("EnTotalDelivey", "detalleOrdenAnterior.getKilosEnStock() " + detalleOrdenAnterior.getKilosEnStock());
        Log.d("EnTotalDelivey", "detalleOrdenAnterior.getMontoItemEntrega() " + detalleOrdenAnterior.getMontoItemEntrega());
        this.cantidadEntrega = this.cantidadEntrega+detalleOrdenNuevo.getCantidadEntrega()-detalleOrdenAnterior.getCantidadEntrega();
        this.montoItemEntrega = this.montoItemEntrega+detalleOrdenNuevo.getMontoItemEntrega()-detalleOrdenAnterior.getMontoItemEntrega();

    }


    public void modificarCantidadProductoDeOrden(Double cantidadOrdenNueva){
        this.cantidadUnidadesEnStock = cantidadOrdenNueva;
        this.kilosEnStock = cantidadOrdenNueva * this.precio;

    }

    public void modificarCantidadProductoDeEntrega(Double cantidadOrdenNueva){
        this.cantidadEntrega = cantidadOrdenNueva;
        this.montoItemEntrega = cantidadOrdenNueva * this.precio;

    }

    // Se usa en 7-Al pasar al orden al Picking
    public void ingresaProductoEnPicking(Double cantidad,  Producto producto) {
        // En picking el monto se suma de la orden y en la cabecera del picking en funcion de las ordenes.
        this.cantidadUnidadesSolicitadas = cantidad;
//       this.kilosSolicitados = this.kilosSolicitados+monto;

    }

//    Se usa en 7 onClick
    public void modificarCantidadProductoDePicking(Double cantidadNueva){
        this.cantidadUnidadesSolicitadas = cantidadNueva;
// El monto en picking no se suma. no se sabe exactamente cual es el precio.
//        this.kilosSolicitados = cantidadNueva * this.precio;
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

    public void modificarCantidadTotalDeOrden(Double cantidadOrdenNueva, Stock detalleAnterior){
        //El detalle que ingresa tiene los datos de la posición de una orden.
        // El detalle que estamos modificando tiene los datos Totaliazos por producto.
        // detalle tienen los datos anteriores y precio y MontoTotal del Item (precio x Canidad).
//        Cantidad nueva es la nueva cantidad
        Log.i("detalle1", "this.cantidadUnidadesEnStock "+this.cantidadUnidadesEnStock);
        Log.i("detalle1", "cantidadAnterior "+detalleAnterior.getCantidadUnidadesEnStock());
        Log.i("detalle1", "cantidadMonto "+detalleAnterior.getKilosEnStock());
        Log.i("detalle1", "cantidadOrdenNueva "+cantidadOrdenNueva);
        this.cantidadUnidadesEnStock = this.cantidadUnidadesEnStock +cantidadOrdenNueva-detalleAnterior.getCantidadUnidadesEnStock();
        this.kilosEnStock = kilosEnStock +cantidadOrdenNueva*detalleAnterior.getPrecio()-detalleAnterior.getKilosEnStock();
    }

    public void modificarCantidadTotalDePicking(Double cantidadOrdenNueva, Stock detalleAnterior){
        //  El detalle que ingresa tiene los datos del item anterior
        //  El detalle que estamos modificando tiene los datos Totaliazos por producto.
        //  detalle tienen los datos anteriores y precio y MontoTotal del Item (precio x Canidad).
        //  Cantidad nueva es la nueva cantidad

        this.cantidadUnidadesSolicitadas = this.cantidadUnidadesSolicitadas +cantidadOrdenNueva-detalleAnterior.getCantidadUnidadesSolicitadas();
        this.kilosSolicitados = kilosSolicitados +cantidadOrdenNueva*detalleAnterior.getPrecio()-detalleAnterior.getKilosSolicitados();
    }
    public void modificarCantidadTotalDeEntrega(Double cantidadOrdenNueva, Stock detalleAnterior){
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
        Log.i("informe", "detalle map - producto" + getProducto().getNombreProducto());
        Log.i("informe", "detalle map -  cantidad" + cantidadUnidadesEnStock);
        result.put("producto", producto);
        result.put("cantidadUnidadesEnStock", cantidadUnidadesEnStock);
        result.put("cantidadUnidadesSolicitadas", cantidadUnidadesSolicitadas);
        result.put("cantidadEntrega", cantidadEntrega);
        result.put("precio", precio);
        result.put("kilosEnStock", kilosEnStock);
        result.put("kilosSolicitados", kilosSolicitados);
        result.put("montoItemEntrega", montoItemEntrega);
        result.put("montoImpuesto", montoImpuesto);
        result.put("semaforo", semaforo);

        return result;
    }



}
