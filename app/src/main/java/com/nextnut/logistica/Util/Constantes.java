package com.nextnut.logistica.util;

/**
 * Created by perez.juan.jose on 09/11/2016.
 */

public class Constantes {
    public static String STORAGE_REFERENCE = "gs://logistica-144918.appspot.com";
    public static String IMAGENES_PRODUCTOS = "/imagenes/productos";
    public static String IMAGENES_CLIENTES = "/imagenes/clientes";
    public static String IMAGENES_ALMACENES = "/imagenes/almacenes";
    public static String IMAGENES_PAGOS = "/imagenes/pagos";


    //usuarios
    public static String ESQUEMA_USERS = "users";

    // Lleva la empresa elegida para trabajar por cada usuario-La vigente
    public static String ESQUEMA_USER_EMPRESA = "user-empresa";
    public static String NODO_USER_EMPRESA = "/" + ESQUEMA_USER_EMPRESA + "/";

    // Lleva el perfil asignado por cada usuario-El vigente
    public static String ESQUEMA_USER_PERFIL = "user-perfil";
    public static String NODO_USER_PERFIL = "/" + ESQUEMA_USER_PERFIL + "/";

    // Listado por mail (sin puntos ni signos raros -KeyfromMail
    // Datos de la empresa que los propuso y el perfil.
    // Se muesta en el listado de empresas disponibles para elegir.
    // Al seleccionar una se copian los datos al USER_EMPRESA y USER_PERFIL
    public static String ESQUEMA_USER_PROPUETO_EMPRESA = "user-propuesto-empresa";
    public static String NODO_USER_PROPUETO_EMPRESA = "/" + ESQUEMA_USER_PROPUETO_EMPRESA + "/";


    // Listado de Empresas Creadas
    public static String ESQUEMA_EMPRESA = "empresa";
    public static String NODO_EMPRESA = "/" + ESQUEMA_EMPRESA + "/";

    // Listado de Usuarios por empresa- lleva los usuarios de cada empresa
    public static String ESQUEMA_EMPRESA_USERS = "empresa-users";
    public static String NODO_EMPRESA_USERS = "/" + ESQUEMA_EMPRESA_USERS + "/";

    // Listado de productos de una empresa.
    public static String ESQUEMA_EMPRESA_PRODUCTOS = "empresa-productos";
    public static String NODO_EMPRESA_PRODUCTOS ="/" + ESQUEMA_EMPRESA_PRODUCTOS+ "/";


    // Listado de CLIENTES de una empresa.
    public static String ESQUEMA_EMPRESA_CLIENTES = "empresa-clientes";
    public static String NODO_EMPRESA_CLIENTES ="/" + ESQUEMA_EMPRESA_CLIENTES+ "/";

    // Listado de Favoritos
    public static String ESQUEMA_FAVORITOS = "favoritos";
    public static String NODO_FAVORITOS ="/" + ESQUEMA_FAVORITOS+ "/";

    // Listado Completo de Ordenes de una empresa
    // --  1  ---

    public static String ESQUEMA_ORDENES = "ordenes";
    public static String NODO_ORDENES ="/" + ESQUEMA_ORDENES+ "/";



    // TOTALES de una orderen de una empresa en Estado Inicial (uno por orden)
    // llave: (empresaKey)(Nro de Orden)
    // (ADMNISTRA CLASES CabeceraOrden)  Adaptador:CabeceraAdapter
    // --  2  ---

    public static String ESQUEMA_ORDENES_CABECERA = "ordenes_cabecera";
    public static String NODO_ORDENES_CABECERA="/" + ESQUEMA_ORDENES_CABECERA+ "/";


    // Listado de detalles TOTALES en Estado inicial de uma empresa.
    // Tiene las sumatoria para cada producto de las ordenes en estado inicial
    // llave: (empresaKey)(productKey)
    // Tiene el detalle de productos solicitados en cada Orden
    // (ADMNISTRA CLASES Detalle)  Adaptador:Detalles Adapter

    // --  3  ---

    public static String ESQUEMA_ORDENES_TOTAL_INICIAL = "ordenes_total_inicial";
    public static String NODO_ORDENES_TOTAL_INICIAL="/" + ESQUEMA_ORDENES_TOTAL_INICIAL+ "/";



    // Listado de detalles para cada orden de una empresa
    // llave: (empresaKey)(Nro de Orden)(prodctKey)
    // Tiene el detalle de productos solicitados en cada Orden
    // (ADMNISTRA CLASES Detalle)  Adaptador:Detalles Adapter

    // --  4  ---

    public static String ESQUEMA_ORDENES_DETALLE = "ordenes_detalle";
    public static String NODO_ORDENES_DETALLE="/" + ESQUEMA_ORDENES_DETALLE+ "/";

    // Listado productos de en estado inicial totales de candantidades de cada producto en por orden
    // --  5  ---
    public static String ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL = "productos_en_ordenes_inicial";
    public static String NODO_PRODUCTOS_EN_ORDENES_INICIAL ="/" + ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL+ "/";

    // Listado de Picking
    // --  6  ---

    public static String ESQUEMA_PICKING = "picking";
    public static String NODO_PICKING ="/" + ESQUEMA_PICKING+ "/";


// Listado de Picking Total x Producto
    // --  7  ---

    public static String ESQUEMA_PICKING_TOTAL = "picking_total";
    public static String NODO_PICKING_TOTAL ="/" + ESQUEMA_PICKING_TOTAL+ "/";

// Reporte de ventas x Producto
// --  8  ---

    public static String ESQUEMA_REPORTE_VENTAS_PRODUCTO = "reporte_ventas_producto";
    public static String NODO_REPORTE_VENTAS_PRODUCTO ="/" + ESQUEMA_REPORTE_VENTAS_PRODUCTO+ "/";

    // Reporte de ventas x Cliente
// --  9  ---

    public static String ESQUEMA_REPORTE_VENTAS_CLIENTE = "reporte_ventas_cliente";
    public static String NODO_REPORTE_VENTAS_CLIENTE ="/" + ESQUEMA_REPORTE_VENTAS_CLIENTE+ "/";


    // Saldo Total por cliente
// --  10  ---

    public static String ESQUEMA_SALDO_TOTAL = "saldo_total";
    public static String NODO_SALDO_TOTAL ="/" + ESQUEMA_SALDO_TOTAL+ "/";


    // Pagos
// --  11  ---

    public static String ESQUEMA_PAGOS = "pagos";
    public static String NODO_PAGOS ="/" + ESQUEMA_PAGOS+ "/";


    // HISTORIAL DE Saldos
// --  12  ---

    public static String ESQUEMA_SALDOS_HISTORIAL = "saldos_historial";
    public static String NODO_SALDOS_HISTORIAL ="/" + ESQUEMA_SALDOS_HISTORIAL+ "/";



    // Almacenes
// --  14  ---

    public static String ESQUEMA_ALMACENES = "almacenes";
    public static String NODO_ALMACENES ="/" + ESQUEMA_ALMACENES+ "/";



    // Constantes para pasar datos entre actividades.
    public static  final String EXTRA_FIREBASE_URL ="FIREBASE_URL";
    public static  final String EXTRA_USER_KEY ="user_key";
    public static  final String EXTRA_EMPRESA_KEY ="empresa_key";
    public static  final String EXTRA_EMPRESA ="empresa";
    public static  final String EXTRA_PERFIL ="perfil";
    public static final String EXTRA_PRODUCT_KEY = "product_key";
    public static final String EXTRA_PRODUCT= "product";
    public static final String EXTRA_CLIENTE_KEY = "cliente_key";
    public static final String EXTRA_CLIENTE= "cliente";
    public static final String EXTRA_CABECERA_ORDEN = "cabeceraOrden";
    public static final String EXTRA_KEYLIST = "keyList"; //listado de key de una orden
    public static final String EXTRA_NRO_PICKIG = "NroPicking"; //listado de key de una orden
    public static final String EXTRA_ALMACEN_KEY = "almacen_key"; //key de un almacen


    // Activity For Result
    public static final int REQUEST_EMPRESA = 7777; // Se llama para seleccionar la empresa con la cual queremos trabajar en la aplicacion
    public static final int REQUEST_CUSTOMER = 1234;// Se para elegir al cliente en la creacion de una orden
    public static final int UPDATE_CUSTOMER = 1236;// Se para elegir al cliente en la Modificación de una orden
    public static final int REQUEST_PRODUCT = 12345;// Se para elegir productos en la creacion de una orden


    // Estado de Ordenes

    public static final int ORDEN_STATUS_INICIAL = 0;                   // Estado de Inicial de una orden
    public static final int ORDEN_STATUS_PICKING = 1;                   // Estado En Picking de una orden
    public static final int ORDEN_STATUS_EN_DELIVERING = 2;             // Estado  EN ENTREGA de una orden
    public static final int ORDEN_STATUS_DELIVERED_PARA_COMPENSAR = 3;  // Estado Orden entregada.
    public static final int ORDEN_STATUS_DELIVERED_COMPENSADA_PARCIALMENTE = 4;  // Estado entregada, pagada parcialmente
    public static final int ORDEN_STATUS_COMPENSADA = 5;            // Estado TOTALMENTE PAGAD
    public static final int ORDEN_STATUS_DELETED = 99;                   // Orden Borrada-

    // Estado de De Picking
    public static final int PICKING_STATUS_INICIAL = 0;         //Picking Borrado
    public static final int PICKING_STATUS_DELIVERY = 1;        //Picking Pasado a Entregar.
    public static final int PICKING_STATUS_EN_ENTREGA = 2;      //Picking Entrega Seleccionada.
    public static final int PICKING_STATUS_CERRADA = 3;         //Picking Borrado
    public static final int PICKING_STATUS_DELETED = 4;         //Picking Borrado


    // Estado de De UN PAGO
    public static final int PAGO_STATUS_INICIAL_SIN_COMPENSAR = 0;
    public static final int PAGO_STATUS_COMPENSADO_PARCIALMENTE = 1;
    public static final int PAGO_STATUS_COMPENSADO= 2;
    public static final int PAGO_STATUS_XXX = 3;


    // Adaptadores.

    public static final int   ADAPTER_TOTALES_ORDEN=0;
    public static final int   ADAPTER_CABECERA_ORDEN=1;
    public static final int   ADAPTER_DETALLE_ORDEN=2;

    public static final int   ADAPTER_TOTALES_PICKING=4;
    public static final int   ADAPTER_CABECERA_PICKING=5;
    public static final int   ADAPTER_DETALLE_PICKING=6;

    public static final int   ADAPTER_TOTALES_DELIVEY=7;
    public static final int   ADAPTER_CABECERA_DELIVEY=8;
    public static final int   ADAPTER_DETALLE_DELIVEY=9;
    public static final int   ADAPTER_CABECERA_ORDEN_EN_PICKING=10;
    public static final int   ADAPTER_CABECERA_ORDEN_EN_DELIVEY=11;

    public static  String detalleOrdenRef_1C(String mEmpresaKey,long numeroDeOrdena,String productoKey){
        String numeroDeOrden =String.valueOf(numeroDeOrdena);
        return NODO_ORDENES + mEmpresaKey + "/" + numeroDeOrden + "/" + productoKey;
    }
    public static  String detalleOrdenRef_4(String mEmpresaKey,long numeroDeOrdena,String productoKey){
        String numeroDeOrden =String.valueOf(numeroDeOrdena);
        return NODO_ORDENES_DETALLE + mEmpresaKey + "/" + numeroDeOrden + "/" + productoKey;
    }
}
