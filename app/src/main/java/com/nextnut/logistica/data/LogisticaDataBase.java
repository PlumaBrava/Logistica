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
    //    @Table(PreciosColumns.class)public static final String PRECIOS = "precios";
    @Table(CustomColumns.class)public static final String CUSTOMS = "customs";
//    @Table(PedidosColumns.class)public static final String PEDIDOS = "pedidos";
//    @Table(DetallePedidosColumns.class)public static final String DETALLEPEDIDOS = "detallepedidos";
//    @Table(EntregasColumns.class)public static final String ENTREGAS = "entregas";
//    @Table(PagosColumns.class)public static final String PAGOS = "pagos";
}
