package com.abuseret.logistica.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
public class CustomOrdersColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String  ID_CUSTOM_ORDER ="_id";

    @DataType(DataType.Type.TEXT)
    public static final String CREATION_DATE_CUSTOM_ORDER = "creationDateCustomOrder";

    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String  REF_CUSTOM_CUSTOM_ORDER = "refCustomer";

    @DataType(DataType.Type.INTEGER)
    public static final String  REF_PICKING_ORDER_CUSTOM_ORDER = "refPickingOrder";

    @DataType(DataType.Type.TEXT)
    public static final String DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER = "dateOfPickingAsignationCustomOrder";

    @DataType(DataType.Type.TEXT)
    public static final String DATE_OF_DELIVEY_CUSTOM_ORDER = "dateOfDeliveyCustomOrder";

    @DataType(DataType.Type.REAL)
    public static final String TOTAL_PRICE_CUSTOM_ORDER = "totalPriceCustomOrder";

    @DataType(DataType.Type.INTEGER)
    public static final String STATUS_CUSTOM_ORDER = "statusCustomOrder";

    @DataType(DataType.Type.REAL)
    public static final String SALDO_A_PAGAR_PRICE_CUSTOM_ORDER = "saldoAPagarCustomOrder";

}
