package com.nextnut.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class CustomColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String  ID_CUSTOM ="_id";

    @DataType(DataType.Type.TEXT)
    public static final String  NAME_CUSTOM = "nameCustom";

    @DataType(DataType.Type.TEXT)
    public static final String LASTNAME_CUSTOM = "lastNameCustom";

    @DataType(DataType.Type.TEXT)
    public static final String DELIIVERY_ADDRES_CUSTOM = "deliveryAddressCustom";

    @DataType(DataType.Type.TEXT)
    public static final String DELIVERY_CITY_CUSTOM = "deliveryCitysCustom";

    @DataType(DataType.Type.TEXT)
    public static final String IMAGEN_CUSTOM = "ImagenCustome";

    @DataType(DataType.Type.TEXT)
    public static final String REFERENCE_CUSTOM = "ContactReferenceCustom"; // referencia a la tabla de contactos android

    @DataType(DataType.Type.INTEGER)
    public static final String SPECIAL_CUSTOM  = "SpecialCustom";

//    @DataType(DataType.Type.TEXT)
//    public static final String RAZON_SOCIAL_CUSTOM ="razonSocialCliente";
//
//
//    @DataType(DataType.Type.TEXT)
//    public static final String CALLE_CUSTOM = "calleCliente";
//
//    @DataType(DataType.Type.TEXT)
//    public static final String NUMERO_CUSTOM = "numeroCliente";
//
//    @DataType(DataType.Type.TEXT)
//    public static final String CIUDAD_CUSTOM = "ciudadCliente";
//
//    @DataType(DataType.Type.TEXT)
//    public static final String PROVINCIA_CUSTOM= "provinciaCliente";
//
    @DataType(DataType.Type.TEXT)
    public static final String CUIT_CUSTOM= "cuitCliente";

    @DataType(DataType.Type.REAL)
    public static final String IVA_CUSTOM= "ivaCliente";
}
