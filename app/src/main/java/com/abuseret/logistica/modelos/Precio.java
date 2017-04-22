package com.abuseret.logistica.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 25/10/2016.
 */
// [START blog_user_class]
@IgnoreExtraProperties
public class Precio implements Parcelable {



    private  Double precio;
    private  Double precioEspecial;


    public Precio() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Precio(Double precio, Double precioEspecial) {
        this.precio = precio;
        this.precioEspecial = precioEspecial;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecioEspecial() {
        return precioEspecial;
    }

    public void setPrecioEspecial(Double precioEspecial) {
        this.precioEspecial = precioEspecial;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("precio", precio);
        result.put("precioEspecial", precioEspecial);
        return result;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(precio);
        parcel.writeDouble(precioEspecial);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Precio> CREATOR = new Creator<Precio>() {
        public Precio createFromParcel(Parcel in) {
            return new Precio(in);
        }

        public Precio[] newArray(int size) {
            return new Precio[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Precio(Parcel in) {
        precio = in.readDouble();
        precioEspecial = in.readDouble();

    }


}
// [END blog_user_class]

