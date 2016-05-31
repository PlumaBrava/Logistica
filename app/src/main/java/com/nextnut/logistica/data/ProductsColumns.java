package com.nextnut.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class ProductsColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID_PRODUCTO ="_id";

    @DataType(DataType.Type.TEXT)
    public static final String DESCRIPCION_PRODUCTO = "descripcionProducto";

    @DataType(DataType.Type.TEXT)
    public static final String IMAGEN_PRODUCTO = "imagenProducto";

    @DataType(DataType.Type.REAL)
    public static final String PRECIO_PRODUCTO = "precioProducto";

    @DataType(DataType.Type.TEXT)
    public static final String NOMBRE_PRODUCTO = "nombreProducto";
}
