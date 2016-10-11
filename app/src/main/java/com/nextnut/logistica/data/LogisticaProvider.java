package com.nextnut.logistica.data;

import android.net.Uri;

import com.nextnut.logistica.BuildConfig;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
@ContentProvider(authority = LogisticaProvider.AUTHORITY, database = LogisticaDataBase.class)
public class LogisticaProvider {

        public static final String AUTHORITY =
                BuildConfig.APPLICATION_ID +
                ".data.LogisticaProvider";
        static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        interface Path{

            String PRODUCTS = "products";
            String CUSTOMS = "customs";
            String CUSTOM_ORDERS = "custom_orders";
            String CUSTOM_ORDERS_DETAIL = "custom_orders_detail";
            String CUSTOM_ORDERS_DETAIL_REF_CUSTOMORDER = "custom_orders_detail_ref_customOrder";
            String PICKING_ORDERS = "picking_orders";
            String PICKING_ORDERS_DETAIL = "picking_orders_detail";
            String JOINORDERCUSTOMER = "JOINORDERCUSTOMER";
            String JOINPRODUCTCUSTOMORDER = "joinProuct_CustomOrder";
            String JOINPRODUCTDETAILORDER = "join_Product_Detail_order";
            String JOINCUSTOMORDERDETAILPRODUCTOCUSTOMER = "join_customorderDetail_Product_Customer";
            String JOINCUSTOMORDERDETAILPRODUCTOCUSTOMERPICKING = "join_customorderDetail_Product_Customer_picking";
            String REPORTE = "reporte";
            String REPORTExMES = "reportexMes";
            String WIDGET = "witgets";

        }

        private static Uri buildUri(String ... paths){
            Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
            for (String path : paths){
                builder.appendPath(path);
            }
            return builder.build();
        }




        //  Tabla de Productos

        @TableEndpoint(table = LogisticaDataBase.PRODUCTS) public static class Products {
            @ContentUri(
                    path = Path.PRODUCTS,
                    type = "vnd.android.cursor.dir/products",
                    defaultSort = ProductsColumns._ID_PRODUCTO + " DESC")//OJO CON Desc lo escribi...
            public static final Uri CONTENT_URI = buildUri(Path.PRODUCTS);

            @InexactContentUri(
                    name = "PRODUCTOS_ID",
                    path = Path.PRODUCTS + "/#",
                    type = "vnd.android.cursor.item/productos",
                    whereColumn = ProductsColumns._ID_PRODUCTO,
                    pathSegment = 1)

            public static Uri withId(long id) {
                return buildUri(Path.PRODUCTS, String.valueOf(id));

            }
        }

        //  Tabla de Clientes

    @TableEndpoint(table = LogisticaDataBase.CUSTOMS) public static class Customs {
        @ContentUri(
                path = Path.CUSTOMS,
                type = "vnd.android.cursor.dir/clientes",
                defaultSort = CustomColumns.ID_CUSTOM + " DESC")//OJO CON Desc lo escribi...
        public static final Uri CONTENT_URI = buildUri(Path.CUSTOMS);

        @InexactContentUri(
                name = "CLIENTES_ID",
                path = Path.CUSTOMS + "/#",
                type = "vnd.android.cursor.item/clientes",
                whereColumn = CustomColumns.ID_CUSTOM,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.CUSTOMS, String.valueOf(id));

        }
    }


    //  Tabla de Custom Orders

    @TableEndpoint(table = LogisticaDataBase.CUSTOM_ORDERS) public static class CustomOrders {
        @ContentUri(
                path = Path.CUSTOM_ORDERS,
                type = "vnd.android.cursor.dir/custom_orders",
                defaultSort = CustomOrdersColumns.ID_CUSTOM_ORDER + " DESC")//OJO CON Desc lo escribi...
        public static final Uri CONTENT_URI = buildUri(Path.CUSTOM_ORDERS);


        @InexactContentUri(
                name = "CUSTOM_ORDERS_ID",
                path = Path.CUSTOM_ORDERS + "/#",
                type = "vnd.android.cursor.item/custom_orders",
                whereColumn = CustomOrdersColumns.ID_CUSTOM_ORDER,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.CUSTOM_ORDERS, String.valueOf(id));

        }
    }


    //  Tabla de CUSTOM_ORDERS_DETAIL

    @TableEndpoint(table = LogisticaDataBase.CUSTOM_ORDERS_DETAIL) public static class CustomOrdersDetail {
        @ContentUri(
                path = Path.CUSTOM_ORDERS_DETAIL,
                type = "vnd.android.cursor.dir/customordersdetail",
                defaultSort = CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL + " DESC")//OJO CON Desc lo escribi...
        public static final Uri CONTENT_URI = buildUri(Path.CUSTOM_ORDERS_DETAIL);

        @InexactContentUri(
                name = "CUSTOM_ORDERS_DETAIL_ID",
                path = Path.CUSTOM_ORDERS_DETAIL + "/#",
                type = "vnd.android.cursor.item/customordersdetail",
                whereColumn = CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.CUSTOM_ORDERS_DETAIL, String.valueOf(id));


        }

        @InexactContentUri(
                name = "CUSTOM_ORDERS_DETAIL_REF_CUSTOMORDER",
                path = Path.CUSTOM_ORDERS_DETAIL_REF_CUSTOMORDER+ "/#",
                type = "vnd.android.cursor.item/customordersdetail",
                whereColumn = CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL,
                pathSegment = 1)

        public static Uri withRefCustomOrder(long id) {
            return buildUri(Path.CUSTOM_ORDERS_DETAIL_REF_CUSTOMORDER, String.valueOf(id));


        }

    }


    //  Tabla de PICKING ORDERS

    @TableEndpoint(table = LogisticaDataBase.PICKING_ORDERS) public static class PickingOrders {
        @ContentUri(
                path = Path.PICKING_ORDERS,
                type = "vnd.android.cursor.dir/pickingorders",
                defaultSort = PickingOrdersColumns.ID_PICKING_ORDERS + " DESC")//OJO CON Desc lo escribi...
        public static final Uri CONTENT_URI = buildUri(Path.PICKING_ORDERS);

        @InexactContentUri(
                name = "PICKING_ORDERS_ID",
                path = Path.PICKING_ORDERS+ "/#",
                type = "vnd.android.cursor.item/pickingorders",
                whereColumn = PickingOrdersColumns.ID_PICKING_ORDERS ,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.PICKING_ORDERS, String.valueOf(id));

        }
    }

     //  Tabla de PICKING ORDERS DETAIL


    @TableEndpoint(table = LogisticaDataBase.PICKING_ORDERS_DETAIL) public static class PickingOrdersDetail {
        @ContentUri(
                path = Path.PICKING_ORDERS_DETAIL,
                type = "vnd.android.cursor.dir/pickingordersdetail",
                defaultSort = PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL + " DESC")//OJO CON Desc lo escribi...
        public static final Uri CONTENT_URI = buildUri(Path.PICKING_ORDERS_DETAIL);

        @InexactContentUri(
                name = "PICKING_ORDERS_DETAIL_ID",
                path = Path.PICKING_ORDERS_DETAIL + "/#",
                type = "vnd.android.cursor.item/pickingordersdetail",
                whereColumn = PickingOrdersDetailColumns.ID_PICKING_ORDERS_DETAIL,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.PICKING_ORDERS_DETAIL, String.valueOf(id));

        }
    }




    @TableEndpoint(table = LogisticaDataBase.CUSTOM_ORDERS)
    public static class ShowJoin {
        //define "virtual" columns
        public static final String JOIN_CUSTOMORDER = "number_customOrder";
        public static final String JOIN_DATE = "date_customOrder";
        public static final String JOIN_PRICE_TOTAL = "pricetotalcustomOrder";
        public static final String JOIN_NAME = "namecustomOrder";
        public static final String JOIN_LASTNAME = "lastnamecustomOrder";

        public static String[] SHOW_JOIN_PROJECTION = new String[]{JOIN_CUSTOMORDER, JOIN_DATE, JOIN_PRICE_TOTAL , JOIN_NAME,JOIN_LASTNAME};

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>(1);
            map.put(JOIN_NAME, LogisticaDataBase.CUSTOMS + "." + CustomColumns.NAME_CUSTOM);
            map.put(JOIN_LASTNAME, LogisticaDataBase.CUSTOMS + "." + CustomColumns.LASTNAME_CUSTOM);
            map.put(JOIN_CUSTOMORDER, LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.ID_CUSTOM_ORDER);
            map.put(JOIN_DATE, LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER);
            map.put(JOIN_PRICE_TOTAL, LogisticaDataBase.CUSTOM_ORDERS + "." + CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER);

            return map;
        }


@ContentUri(

                path = Path.JOINORDERCUSTOMER ,

                type = "vnd.android.cursor.item/join_custom_order",

                join = "JOIN " + LogisticaDataBase.CUSTOMS+ " ON " + LogisticaDataBase.CUSTOM_ORDERS+ "." + CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER + " = " + LogisticaDataBase.CUSTOMS + "." +  CustomColumns.ID_CUSTOM)


public static final Uri CONTENT_URI = buildUri(Path.JOINORDERCUSTOMER );
    }

    @TableEndpoint(table = LogisticaDataBase.CUSTOM_ORDERS_DETAIL)
    public static class joinCustomOrder_Product {


        @ContentUri(

                path = Path.JOINPRODUCTCUSTOMORDER ,

                type = "vnd.android.cursor.item/joinProuct_CustomOrder",
                join = "JOIN " + LogisticaDataBase.PRODUCTS+ " ON " + LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO+ " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL)


        public static final Uri CONTENT_URI = buildUri(Path.JOINPRODUCTCUSTOMORDER );
    }


    @TableEndpoint(table =  LogisticaDataBase.CUSTOM_ORDERS_DETAIL )
    public static class join_Product_Detail_order {



        @ContentUri(

                path = Path.JOINPRODUCTDETAILORDER ,

                type = "vnd.android.cursor.item/join_Product_Detail_order",


                join = "JOIN " + LogisticaDataBase.PRODUCTS + " ON " + LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO+ " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL +

                " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "
                + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER,

                groupBy = LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO

        )


        public static final Uri CONTENT_URI = buildUri(Path.JOINPRODUCTDETAILORDER );
    }

    @TableEndpoint(table =  LogisticaDataBase.CUSTOM_ORDERS_DETAIL )
    public static class join_customorderDetail_Product_Customer {



        @ContentUri(

                path = Path.JOINCUSTOMORDERDETAILPRODUCTOCUSTOMER ,

                type = "vnd.android.cursor.item/join_customorderDetail_Product_Customer",


                join = "JOIN " + LogisticaDataBase.PRODUCTS + " ON " + LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO+ " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL +

                        " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "
                        + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER



        )


        public static final Uri CONTENT_URI = buildUri(Path.JOINCUSTOMORDERDETAILPRODUCTOCUSTOMER );
    }

    @TableEndpoint(table =  LogisticaDataBase.PRODUCTS )
    public static class join_customorderDetail_Product_Customer_picking {



        @ContentUri(

                path = Path.JOINCUSTOMORDERDETAILPRODUCTOCUSTOMERPICKING ,

                type = "vnd.android.cursor.item/join_customorderDetail_Product_Customer_picking",


                join = "  JOIN " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + " ON " + LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO + " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL

                        +

                        " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "
                        + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER

                        +  " left JOIN " + LogisticaDataBase.PICKING_ORDERS_DETAIL

                        + " ON " + " ( "
                        +

                LogisticaDataBase.PICKING_ORDERS_DETAIL + "." +  PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL +" = "
                + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER

                        +" AND "

                + LogisticaDataBase.PICKING_ORDERS_DETAIL  + "." +PickingOrdersDetailColumns.REF_PRODUCT_PICKING_ORDERS_DETAIL+" = "
               +  LogisticaDataBase.PRODUCTS+ "." + ProductsColumns._ID_PRODUCTO

 + " ) "

                   ,

                groupBy = LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO

        )


        public static final Uri CONTENT_URI = buildUri(Path.JOINCUSTOMORDERDETAILPRODUCTOCUSTOMERPICKING );
    }

    @TableEndpoint(table =  LogisticaDataBase.CUSTOM_ORDERS_DETAIL )
    public static class reporte {



        @ContentUri(

                path = Path.REPORTE ,

                type = "vnd.android.cursor.item/reporte",


                join = "  JOIN " + LogisticaDataBase.PRODUCTS + " ON " +
                        LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO + " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL

                        +

                        " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                        LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER+" = "+
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL

                        +  " JOIN " + LogisticaDataBase.CUSTOMS   + " ON " +
                        LogisticaDataBase.CUSTOMS + "." +CustomColumns.ID_CUSTOM+" = "+
                        LogisticaDataBase.CUSTOM_ORDERS + "." +  CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER



                ,

                groupBy = "1,2,3,4 "

        )


        public static final Uri CONTENT_URI = buildUri(Path.REPORTE );
    }

    @TableEndpoint(table =  LogisticaDataBase.CUSTOM_ORDERS_DETAIL )
    public static class reportexMes {



        @ContentUri(

                path = Path.REPORTExMES ,

                type = "vnd.android.cursor.item/reporte",


                join = "  JOIN " + LogisticaDataBase.PRODUCTS + " ON " +
                        LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO + " = " +
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL

                        +

                        " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                        LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER+" = "+
                        LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL

                        +  " JOIN " + LogisticaDataBase.CUSTOMS   + " ON " +
                        LogisticaDataBase.CUSTOMS + "." +CustomColumns.ID_CUSTOM+" = "+
                        LogisticaDataBase.CUSTOM_ORDERS + "." +  CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER



                ,

                groupBy = "1, 2 "

        )


        public static final Uri CONTENT_URI = buildUri(Path.REPORTExMES );
    }
    @TableEndpoint(table =  LogisticaDataBase.PRODUCTS )
    public static class join_witget {
    @ContentUri(

            path = Path.WIDGET ,

            type = "vnd.android.cursor.item/join_widget",


            join = "  JOIN " + LogisticaDataBase.CUSTOM_ORDERS_DETAIL + " ON " + LogisticaDataBase.PRODUCTS+ "." +ProductsColumns._ID_PRODUCTO + " = " +
                    LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL

                    +

                    " JOIN " + LogisticaDataBase.CUSTOM_ORDERS  + " ON " +
                    LogisticaDataBase.CUSTOM_ORDERS_DETAIL + "." +  CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL +" = "
                    + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.ID_CUSTOM_ORDER

                    +  " left JOIN " + LogisticaDataBase.PICKING_ORDERS_DETAIL

                    + " ON " + " ( "
                    +

                    LogisticaDataBase.PICKING_ORDERS_DETAIL + "." +  PickingOrdersDetailColumns.REF_PICKING_ORDER_PICKING_ORDERS_DETAIL +" = "
                    + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER

                    +" AND "

                    + LogisticaDataBase.PICKING_ORDERS_DETAIL  + "." +PickingOrdersDetailColumns.REF_PRODUCT_PICKING_ORDERS_DETAIL+" = "
                    +  LogisticaDataBase.PRODUCTS+ "." + ProductsColumns._ID_PRODUCTO
                    + " ) "

                    +  " left JOIN " + LogisticaDataBase.PICKING_ORDERS

                    + " ON " + " ( "
                    +

                    LogisticaDataBase.PICKING_ORDERS+ "." +  PickingOrdersColumns.ID_PICKING_ORDERS+" = "
                    + LogisticaDataBase.CUSTOM_ORDERS + "." +CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER



                    + " ) "

            ,

            groupBy = "1,2"

    )


    public static final Uri CONTENT_URI = buildUri(Path.WIDGET );
}

}
