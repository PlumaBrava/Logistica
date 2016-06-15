package com.nextnut.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class CustomOrdersDetailColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String  ID_CUSTOM_ORDER_DETAIL ="_id";

    @DataType(DataType.Type.INTEGER)
    public static final String  REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL = "refCustomOrderCustomOrderDetail";

    @DataType(DataType.Type.INTEGER)
    public static final String REF_PRODUCT_CUSTOM_ORDER_DETAIL = "refProductCustomOrderDetail";

    @DataType(DataType.Type.TEXT)
    public static final String PRODUCT_NAME_CUSTOM_ORDER_DETAIL = "productNameCustomOrderDetail";

    @DataType(DataType.Type.INTEGER)
    public static final String QUANTITY_CUSTOM_ORDER_DETAIL = "quantityCustomOrderDetail";

    @DataType(DataType.Type.REAL)
    public static final String PRICE_CUSTOM_ORDER_DETAIL = "priceCustomOrderDetail";

    @DataType(DataType.Type.INTEGER)
    public static final String FAVORITE_CUSTOM_ORDER_DETAIL = "favoriteCustomOrderDetail";


}
