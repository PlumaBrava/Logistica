package com.nextnut.logistica.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by perez.juan.jose on 28/05/2016.
 */
@ContentProvider(authority = LogisticaProvider.AUTHORITY, database = LogisticaDataBase.class)
public class LogisticaProvider {

        public static final String AUTHORITY =
                "com.nextnut.logistica.data.LogisticaProvider";
        static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        interface Path{

            String PRODUCTS = "products";
            //        String PRECIOS = "precios";
            String CUSTOMS = "customs";
//        String PEDIDOS = "pedidos";
//        String DETALLEPEDIDOS = "detallepedidos";
//        String ENTREGAS = "entregas";
//        String PAGOS = "pagos";
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
                    defaultSort = CustomColumns.ID_CLIENTE + " DESC")//OJO CON Desc lo escribi...
            public static final Uri CONTENT_URI = buildUri(Path.CUSTOMS);

            @InexactContentUri(
                    name = "CLIENTES_ID",
                    path = Path.CUSTOMS + "/#",
                    type = "vnd.android.cursor.item/clientes",
                    whereColumn = CustomColumns.ID_CLIENTE,
                    pathSegment = 1)

            public static Uri withId(long id) {
                return buildUri(Path.CUSTOMS, String.valueOf(id));

            }
        }

}
