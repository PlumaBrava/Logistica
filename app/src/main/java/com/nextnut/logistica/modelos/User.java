package com.nextnut.logistica.modelos;

/**
 * Created by perez.juan.jose on 22/10/2016.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    private String username;
    private String email;
    private String status;
    private String photoURL;
    private Boolean activo;
    private Perfil perfil;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email,String photoURL, String status, Boolean activo, Perfil perfil) {
        this.username = username;
        this.email = email;
        this.status = status;
        this.activo = activo;
        this.perfil = perfil;
        this.photoURL=photoURL;
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

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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
        result.put("photoURL",photoURL);
        result.put("status", status);
        result.put("perfil", perfil);
        result.put("activo", activo);

        return result;
    }
    // [END post_to_map]


}
// [END blog_user_class]