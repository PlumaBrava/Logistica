package com.nextnut.logistica.util;

/**
 * Created by perez.juan.jose on 09/06/2016.
 */
public class CurrencyToDouble {
    String s;
    public CurrencyToDouble(String s) {
    this.s=s;
    }

    public Double convert(){
        if(s != null && !s.isEmpty()){


            String cleanString = s.replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            return parsed/100;
        }
        else  {return null;}

    }





}
