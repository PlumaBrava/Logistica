package com.abuseret.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 25/10/2016.
 */
// [START blog_user_class]
@IgnoreExtraProperties
public class Producto implements Parcelable {

    private String nombreProducto;

    private Map<String, Precio> precios = new HashMap<>();
    //    private Double precio;
//    private Double precioEspcecial;
    private String descripcionProducto;


    private String fotoProducto;
    private long fechaModificacion;

    private String rubro;
    private String tipoUnidad;
    private int cantidadMinima;
    private int cantidadMaxima;
    private int cantidadDefault;

    private String uid;


    public Producto() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Producto(String uid, String nombreProducto, Map<String, Precio> precios, String descripcionProducto, String fotoProducto,
                    String rubro,
                    String tipoUnidad,
                    int cantidadMinima,
                    int cantidadMaxima,
                    int cantidadDefault
    ) {
        this.nombreProducto = nombreProducto;
        this.precios = precios;

        this.descripcionProducto = descripcionProducto;
        this.fotoProducto = fotoProducto;

        this.rubro = rubro;
        this.tipoUnidad = tipoUnidad;
        this.cantidadMinima = cantidadMinima;
        this.cantidadMaxima = cantidadMaxima;
        this.cantidadDefault = cantidadDefault;


        this.uid = uid;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("nombreProducto", nombreProducto);
        result.put("precios", precios);
//        result.put("precioEspcecial", precioEspcecial);
        result.put("descripcionProducto", descripcionProducto);
        result.put("fotoProducto", fotoProducto);

        result.put("rubro", rubro);
        result.put("tipoUnidad", tipoUnidad);
        result.put("cantidadMinima", cantidadMinima);
        result.put("cantidadMaxima", cantidadMaxima);
        result.put("cantidadDefault", cantidadDefault);

        result.put("fechaModificacion", ServerValue.TIMESTAMP);
        result.put("uid", uid);
        return result;
    }
    // [END post_to_map]

    // [START post_to_map]
    @Exclude
    public Map<String, Object> preciosToMap(Map<String, Double[]> precios) {
        HashMap<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Double[]> item : precios.entrySet()) {
//            result.put(item.getKey(), item.getValue());

            Double[] t = item.getValue();
            result.put(item.getKey(), itemToMap(t));


        }
        return result;
    }

    @Exclude
    public Map<String, Object> itemToMap(Double[] precio) {

        HashMap<String, Object> result1 = new HashMap<>();

        result1.put("precio", precio[0]);
        result1.put("precioEspecial", precio[1]);
        return result1;
    }

    public Map<String, Precio> getPrecios() {
        return precios;
    }

    public void setPrecios(Map<String, Precio> precios) {
        this.precios = precios;
    }

    public int getCantidadDefault() {
        return cantidadDefault;
    }

    public void setCantidadDefault(int cantidadDefault) {
        this.cantidadDefault = cantidadDefault;
    }

    public int getCantidadMaxima() {
        return cantidadMaxima;
    }

    public void setCantidadMaxima(int cantidadMaxima) {
        this.cantidadMaxima = cantidadMaxima;
    }

    public int getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public String getTipoUnidad() {
        return tipoUnidad;
    }

    public void setTipoUnidad(String tipoUnidad) {
        this.tipoUnidad = tipoUnidad;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

//    public Double getPerfilDePrecio() {
//        return precio;
//    }
//
//    public void setPerfilDePrecio(Double precio) {
//        this.precio = precio;
//    }
//
//    public Double getPrecioEspcecial() {
//        return precioEspcecial;
//    }
//
//    public void setPrecioEspcecial(Double precioEspcecial) {
//        this.precioEspcecial = precioEspcecial;
//    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getFotoProducto() {
        return fotoProducto;
    }

    public void setFotoProducto(String fotoProducto) {
        this.fotoProducto = fotoProducto;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(long fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombreProducto);
        parcel.writeString(descripcionProducto);
        parcel.writeString(fotoProducto);
        parcel.writeLong(precios.size());


        for (Map.Entry<String, Precio> item : precios.entrySet()) {

            parcel.writeString(item.getKey());
            Precio t = item.getValue();
            parcel.writeDouble(t.getPrecio());
            parcel.writeDouble(t.getPrecioEspecial());

        }
        ;


        parcel.writeString(rubro);
        parcel.writeString(tipoUnidad);
        parcel.writeInt(cantidadMinima);
        parcel.writeInt(cantidadMaxima);
        parcel.writeInt(cantidadDefault);


        parcel.writeLong(fechaModificacion);
        parcel.writeString(uid);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Producto> CREATOR = new Parcelable.Creator<Producto>() {
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Producto(Parcel in) {
        nombreProducto = in.readString();
        descripcionProducto = in.readString();
        fotoProducto = in.readString();


        long tamano = in.readLong();

        for (int a = 0; a < tamano; a++) {
            String key = in.readString();
            Double[] t = new Double[2];
            t[0] = in.readDouble();
            t[1] = in.readDouble();
            precios.put(key, new Precio(t[0], t[1]));
        }

        rubro = in.readString();
        tipoUnidad = in.readString();
        cantidadMinima = in.readInt();
        cantidadMaxima = in.readInt();
        cantidadDefault = in.readInt();


        fechaModificacion = in.readLong();
        uid = in.readString();

    }

    public Double getPrecioParaPerfil(String perfil) {
        Log.i("getPrecioParaPerfil", "perfil: "+perfil);
        for (Map.Entry<String, Precio> item : precios.entrySet()) {
            Log.i("getPrecioParaPerfil", "item.getKey(): "+item.getKey());

            if (item.getKey().equals(perfil)) {
                Precio t = item.getValue();
                Log.i("getPrecioParaPerfil", "iguales:t.getPerfilDePrecio() "+t.getPrecio());

                return t.getPrecio();
            }


        }
        return 0.0;
    }

    public Double getPrecioEspecialPerfil(String perfil) {
        Log.i("getPrecioParaPerfilEs", "perfil: "+perfil);
        for (Map.Entry<String, Precio> item : precios.entrySet()) {
            Log.i("getPrecioParaPerfilEs", "item.getKey(): "+item.getKey());
            if (item.getKey().equals(perfil)) {
                Precio t = item.getValue();
                Log.i("getPrecioParaPerfilEs", "iguales:t.getPerfilDePrecio() "+t.getPrecio());

                return t.getPrecioEspecial();
            }


        }
        return 0.0;
    }
}
// [END blog_user_class]

