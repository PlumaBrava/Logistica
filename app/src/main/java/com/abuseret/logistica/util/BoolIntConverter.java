package com.abuseret.logistica.util;

/**
 * Created by perez.juan.jose on 20/07/2016.
 */
public class BoolIntConverter {
    public BoolIntConverter() {
    }
    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public boolean intToBool(int b) {
        return b != 0;
    }
}

