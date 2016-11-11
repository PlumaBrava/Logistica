package com.nextnut.logistica.util;

/**
 * Created by perez.juan.jose on 10/11/2016.
 */

public class KeyMailConverter {
    public KeyMailConverter() {
    }
    public static String getKeyFromEmail(String email) {
        if ( email !=null){
            String a =email.toLowerCase()
                    .replace('.','-')
                    .replace('#','N')
                    .replace('[','P')
                    .replace(']','p')
                    .replace('/','B');
            return a;
        }
        return email;
    }
}
