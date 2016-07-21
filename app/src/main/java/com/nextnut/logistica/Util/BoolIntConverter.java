package com.nextnut.logistica.Util;

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
        if (b==0) {return false;}
        else  return true;
    }
}

