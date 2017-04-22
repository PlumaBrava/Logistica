package com.abuseret.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class PickingOrdersColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String  ID_PICKING_ORDERS ="_id";

    @DataType(DataType.Type.INTEGER)
    public static final String  CREATION_DATE_PICKING_ORDERS = "creatonDatePickingOrders";

    @DataType(DataType.Type.TEXT)
    public static final String  COMMENTS_PICKING_ORDERS = "commentsPickingOrders";

    @DataType(DataType.Type.INTEGER)
    public static final String  STATUS_PICKING_ORDERS = "statusPickingOrders";
}
