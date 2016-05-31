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
    public static final String  ID_CLIENTE ="_idCliente";

    @DataType(DataType.Type.TEXT)
    public static final String  NOMBRE_CLIENTE = "nombreCliente";

    @DataType(DataType.Type.TEXT)
    public static final String APELLIDO_CLIENTE = "apellidoCliente";

    @DataType(DataType.Type.TEXT)
    public static final String RAZON_SOCIAL_CLIENTE ="razonSocialCliente";


    @DataType(DataType.Type.TEXT)
    public static final String CALLE_CLIENTE = "calleCliente";

    @DataType(DataType.Type.TEXT)
    public static final String NUMERO_CLIENTE = "numeroCliente";

    @DataType(DataType.Type.TEXT)
    public static final String CIUDAD_CLIENTE = "ciudadCliente";

    @DataType(DataType.Type.TEXT)
    public static final String PROVINCIA_CLIENTE= "provinciaCliente";

    @DataType(DataType.Type.TEXT)
    public static final String CUIT_CLIENTE= "cuitCliente";

    @DataType(DataType.Type.INTEGER)
    public static final String IVA_CLIENTE= "ivaCliente";
}
