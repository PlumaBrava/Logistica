package com.nextnut.logistica.modelos;

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
public class PerfilDePrecio implements Parcelable {



    private  String perfilDePrecio;


    public PerfilDePrecio() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public PerfilDePrecio(String perfilDePrecio) {
        this.perfilDePrecio = perfilDePrecio;
    }

    public String getPerfilDePrecio() {
        return perfilDePrecio;
    }

    public void setPerfilDePrecio(String perfilDePrecio) {
        this.perfilDePrecio = perfilDePrecio;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("perfilDePrecio", perfilDePrecio);
        return result;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(perfilDePrecio);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<PerfilDePrecio> CREATOR = new Creator<PerfilDePrecio>() {
        public PerfilDePrecio createFromParcel(Parcel in) {
            return new PerfilDePrecio(in);
        }

        public PerfilDePrecio[] newArray(int size) {
            return new PerfilDePrecio[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private PerfilDePrecio(Parcel in) {
        perfilDePrecio = in.readString();

    }


}
// [END blog_user_class]

