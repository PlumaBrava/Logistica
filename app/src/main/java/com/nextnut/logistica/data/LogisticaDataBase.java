package com.nextnut.logistica.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
@Database(version = LogisticaDataBase.VERSION)
public class LogisticaDataBase {
    public static final int VERSION = 1;

    private LogisticaDataBase() {}

    @Table(ProductsColumns.class) public static final String PRODUCTS = "products";
    @Table(CustomColumns.class)public static final String CUSTOMS = "customs";
    @Table(CustomOrdersColumns.class)public static final String CUSTOM_ORDERS = "custom_orders";

    @Table(CustomOrdersDetailColumns.class)public static final String CUSTOM_ORDERS_DETAIL = "custom_orders_detail";

    @Table(PickingOrdersColumns.class)public static final String PICKING_ORDERS = "picking_orders";

    @Table(PickingOrdersDetailColumns.class)public static final String PICKING_ORDERS_DETAIL = "picking_orders_detail";

}
