package com.nextnut.logistica.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
@Database(version = LogisticaDataBase.VERSION)
public class LogisticaDataBase {
    public static final int VERSION = 6;

    private LogisticaDataBase() {}

    @Table(ProductsColumns.class) public static final String PRODUCTS = "products";
    @Table(CustomColumns.class)public static final String CUSTOMS = "customs";
    @Table(CustomOrdersColumns.class)public static final String CUSTOM_ORDERS = "custom_orders";

    @Table(CustomOrdersDetailColumns.class)public static final String CUSTOM_ORDERS_DETAIL = "custom_orders_detail";

    @Table(PickingOrdersColumns.class)public static final String PICKING_ORDERS = "picking_orders";

    @Table(PickingOrdersDetailColumns.class)public static final String PICKING_ORDERS_DETAIL = "picking_orders_detail";


    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        Log.i("UPDATE", "oldVersion:"+oldVersion);
        if (oldVersion < 6) {

            db.beginTransaction();
            try {
                db.execSQL("ALTER TABLE " + CUSTOM_ORDERS
                        + " ADD COLUMN " + CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER + " " + DataType.Type.REAL + " DEFAULT 0");
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.i("UPDATE", "oldVersion:"+e.toString());

            } finally {
                db.endTransaction();
            }
//            db.execSQL("DROP TABLE IF EXISTS shows"); Borra una tabla
//            db.execSQL("CREATE TABLE "); Crea una tabla
//            db.execSQL("ALTER TABLE IF EXISTS shows"); Modifica una tabla/
//            db.execSQL("RENAME TABLE old_name TO new_name;"); Cambia el nombre/
//            CREATE TABLE employees (
//                    id            INTEGER       PRIMARY KEY,
//                    first_name    VARCHAR(50)   not null,
//                    last_name     VARCHAR(75)   not null,
//                    fname         VARCHAR(50)   not null,
//                    dateofbirth   DATE          not null
//            );

        }

        if (oldVersion < 7) {

            db.beginTransaction();
            try {
                db.execSQL("ALTER TABLE " + CUSTOM_ORDERS
                        + " ADD COLUMN " + CustomOrdersColumns.SALDO_A_PAGAR_PRICE_CUSTOM_ORDER + " " + DataType.Type.REAL + " DEFAULT 0");
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.i("UPDATE", "oldVersion:"+e.toString());

            } finally {
                db.endTransaction();
            }


        }


    }
}


