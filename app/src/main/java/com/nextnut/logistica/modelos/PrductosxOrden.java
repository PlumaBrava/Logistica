package com.nextnut.logistica.modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 29/11/2016.
 */
@IgnoreExtraProperties
public class PrductosxOrden {

    private Cliente cliente;
    private Detalle detalle;
    private Boolean semaforo=true; // Si el semaforo es true, significa que esta verde. Se puede modificar el contenidos. si es false esta bloquedao para modificacioens/

    public PrductosxOrden() {
    }

    public PrductosxOrden( Cliente cliente, Detalle detalle) {

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

        result.put("cliente", cliente);
        result.put("detalle", detalle);
        result.put("semaforo", semaforo);



        return result;
    }


}
