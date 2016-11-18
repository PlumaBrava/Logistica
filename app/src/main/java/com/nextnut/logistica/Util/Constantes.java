package com.nextnut.logistica.util;

/**
 * Created by perez.juan.jose on 09/11/2016.
 */

public class Constantes {
    public static String STORAGE_REFERENCE = "gs://logistica-144918.appspot.com";


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


    // Constantes para pasar datos entre actividades.
    public static  final String EXTRA_FIREBASE_URL ="FIREBASE_URL";
    public static  final String EXTRA_USER_KEY ="user_key";
    public static  final String EXTRA_EMPRESA_KEY ="empresa_key";
    public static  final String EXTRA_EMPRESA ="empresa";
    public static  final String EXTRA_PERFIL ="perfil";
    public static final String EXTRA_PRODUCT_KEY = "product_key";
    public static final String EXTRA_CLIENTE_KEY = "cliente_key";
    public static final String EXTRA_CLIENTE= "cliente";


    // Activity For Result
    public static final int REQUEST_EMPRESA = 7777; // Se llama para seleccionar la empresa con la cual queremos trabajar en la aplicacion
    public static final int REQUEST_CUSTOMER = 1234;// Se para elegir al cliente en la creacion de una orden
    public static final int UPDATE_CUSTOMER = 1236;// Se para elegir al cliente en la Modificación de una orden
    public static final int REQUEST_PRODUCT = 12345;// Se para elegir productos en la creacion de una orden

}
