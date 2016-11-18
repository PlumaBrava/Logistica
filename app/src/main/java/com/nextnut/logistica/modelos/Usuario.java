package com.nextnut.logistica.modelos;

/**
 * Created by perez.juan.jose on 22/10/2016.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class Usuario implements Parcelable {

    private String username;
    private String email;
    private String status;
    private String photoURL;
    private Boolean activo;

    private long fechaModificacion;


    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Usuario(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Usuario(String username, String email, String photoURL, String status, Boolean activo) {
        this.username = username;
        this.email = email;
        this.status = status;
        this.activo = activo;

        this.photoURL = photoURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }


    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("username", username);
        result.put("photoURL", photoURL);
        result.put("status", status);
        result.put("activo", activo);
        result.put("fechaModificacion", ServerValue.TIMESTAMP);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(status);
        parcel.writeString(photoURL);
        parcel.writeByte((byte) (activo ? 1 : 0));
        parcel.writeLong(fechaModificacion);


    }

    // [END post_to_map]
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Usuario(Parcel in) {
        username = in.readString();
        email = in.readString();
        status = in.readString();
        photoURL = in.readString();
        activo = in.readByte() != 0;
        fechaModificacion = in.readLong();
    }
}
// [END blog_user_class]


