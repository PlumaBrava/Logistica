package com.nextnut.logistica.rest;

/**
 * Created by perez.juan.jose on 29/05/2016.
 */
public class Products {
    public String detalle;
    public String imagen;
    public double precio;

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Products(String detalle, String imagen, Double precio) {
        this.detalle = detalle;
        this.imagen = imagen;
        this.precio=precio;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getImagen() {
        return imagen;
    }
}
