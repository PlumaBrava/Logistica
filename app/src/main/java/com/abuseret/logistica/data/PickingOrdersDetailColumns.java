package com.abuseret.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class PickingOrdersDetailColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String  ID_PICKING_ORDERS_DETAIL ="_id";

    @DataType(DataType.Type.INTEGER)
    public static final String  REF_PICKING_ORDER_PICKING_ORDERS_DETAIL = "refPickingOrderPickingOrdersDetail";

    @DataType(DataType.Type.INTEGER)
    public static final String  REF_PRODUCT_PICKING_ORDERS_DETAIL = "refProductPickingOrdersDetail";

    @DataType(DataType.Type.TEXT)
    public static final String  PRODUCT_NAME_PICKING_ORDERS_DETAIL = "productNamePickingOrderDetails";

    @DataType(DataType.Type.INTEGER)
    public static final String  QUANTITY_PICKING_ORDERS_DETAIL = "quantityPickingOrderDetails";
}
