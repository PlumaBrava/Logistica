package com.nextnut.logistica.util;

/**
 * Created by perez.juan.jose on 09/11/2016.
 */

public class Constantes {
    public static String STORAGE_REFERENCE = "gs://logistica-144918.appspot.com";
    public static String IMAGENES_PRODUCTOS = "/imagenes/productos";
    public static String IMAGENES_CLIENTES = "/imagenes/clientes";


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
    public static String NODO_ESQUEMA_FAVORITOS ="/" + ESQUEMA_FAVORITOS+ "/";

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
    public static String NODO_PRODUCTOS_X_ORDEN_INICIAL ="/" + ESQUEMA_PRODUCTOS_EN_ORDENES_INICIAL+ "/";

    // Listado de Picking
    // --  6  ---

    public static String ESQUEMA_PICKING = "picking";
    public static String NODO_PICKING ="/" + ESQUEMA_PICKING+ "/";


// Listado de Picking Total x Producto
    // --  7  ---

    public static String ESQUEMA_PICKING_TOTAL = "picking_total";
    public static String NODO_PICKING_TOTAL ="/" + ESQUEMA_PICKING_TOTAL+ "/";



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


    // Activity For Result
    public static final int REQUEST_EMPRESA = 7777; // Se llama para seleccionar la empresa con la cual queremos trabajar en la aplicacion
    public static final int REQUEST_CUSTOMER = 1234;// Se para elegir al cliente en la creacion de una orden
    public static final int UPDATE_CUSTOMER = 1236;// Se para elegir al cliente en la Modificación de una orden
    public static final int REQUEST_PRODUCT = 12345;// Se para elegir productos en la creacion de una orden


    // Estado de Ordenes
    public static final int ESTADO_ORDEN_INICIAL = 0;       // Estado de Inicial de una orden
    public static final int ESTADO_ORDEN_EN_PICKING = 1;    // Estado En Picking de una orden
    public static final int ESTADO_ORDEN_EN_ENTREGA = 2;    // Estado  EN ENTREGA de una orden
    public static final int ESTADO_ORDEN_ENTREGADA = 3;     // Estado ENTREGADO de una orden
    public static final int ESTADO_ORDEN_PAGO_PARCIAL = 4;  // Estado REGISTRA PAGO PARCIAL de una orden
    public static final int ESTADO_ORDEN_COMPENSADA = 5;    // Estado TOTALMENTE PAGADA
    public static final int ESTADO_ORDEN_ACTUALIZANDO_DATOS = 6;    // Estado actualizando datos. Se pasa a este estado para actualizar todo el esquema, al finalizar se retorna al estado que corresponda.

    public static final int ORDER_STATUS_INICIAL = 0;
    public static final int ORDER_STATUS_PICKING = 1;
    public static final int ORDER_STATUS_DELIVERED = 2;
    public static final int ORDER_STATUS_DELETED = 3;

    // Estado de De Picking
    public static final int PICKING_STATUS_INICIAL = 0;
    public static final int PICKING_STATUS_DELIVERY = 1;
    public static final int PICKING_STATUS_CERRADA = 2;
    public static final int PICKING_STATUS_DELETED = 3;

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

}
