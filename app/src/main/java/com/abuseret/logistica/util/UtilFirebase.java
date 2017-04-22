package com.abuseret.logistica.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by perez.juan.jose on 17/11/2016.
 */

public class UtilFirebase {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
