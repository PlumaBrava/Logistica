package com.nextnut.logistica.modelos;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 29/11/2016.
 */
@IgnoreExtraProperties
public class ReporteClienteProducto {

    private Cliente cliente;
    private Detalle detalle;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public ReporteClienteProducto() {
    }

    public ReporteClienteProducto(Cliente cliente, Detalle detalle) {

        this.cliente = cliente;
        this.detalle = detalle;
    }




    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Detalle getDetalle() {
        return detalle;
    }

    public void setDetalle(Detalle detalle) {
        this.detalle = detalle;
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
        Log.i("informe", "Producto x Orden map - producto " + detalle.getProducto().getNombreProducto());
        Log.i("informe", "Producto x Orden map -  cantidad " + detalle.getCantidadOrden());
        result.put("cliente", cliente);
        result.put("detalle", detalle.toMap());
        result.put("semaforo", semaforo);
        Log.i("informe", "Producto x Orden map - D1 producto" );
//        Detalle d= (Detalle) result.get("detalle");
//        Log.i("informe", "Producto x Orden map - D producto " + d.getProducto().getNombreProducto());
//        Log.i("informe", "Producto x Orden map - D cantidad " + d.getCantidadUnidadesEnStock());
//        Log.i("informe", "Producto x Orden map - D precio " + d.getPerfilDePrecio());

        return result;
    }


}
