package com.nextnut.logistica;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.modelos.Detalle;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.modelos.Totales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_ORDENES;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES;
import static com.nextnut.logistica.util.Constantes.NODO_ORDENES_CABECERA;
import static com.nextnut.logistica.util.Constantes.ORDEN_STATUS_INICIAL;
import static com.nextnut.logistica.util.Constantes.REQUEST_CUSTOMER;
import static com.nextnut.logistica.util.Constantes.REQUEST_EMPRESA;
import static com.nextnut.logistica.util.Constantes.REQUEST_PRODUCT;
import static com.nextnut.logistica.util.Constantes.UPDATE_CUSTOMER;
import static com.nextnut.logistica.util.MakeCall.migrarTelefonosDelContactoAsociado;
import static com.nextnut.logistica.util.Network.isNetworkAvailable;


public class MainActivity extends ActivityBasic implements PickingListFragment.PickingOrdersHandler {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int mViewPagerFabItem;
    private ArrayList<Integer> mSeccionesDisponibles = new ArrayList<Integer>();
    private ArrayList<String> mTitulosSeccionesDisponibles = new ArrayList<String>();

    private InterstitialAd mInterstitial;
    public static final int CUSTOM_ORDER_FRAGMENT = 0;
    public static final int PICKING_FRAGMENT = 1;
    public static final int DELIVERY_FRAGMENT = 2;
    public static final int PAGOS_FRAGMENT = 3;
    public static final int STOCK_FRAGMENT = 4;
    public static final String USER_DISPLAY_NAME = "userDisplayName";
    public static final String USER_ID = "userId";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static long mPickingOrderSelected = 0;
    public FloatingActionButton mFab;
    public FloatingActionButton mFabPasarAPicking;

    private static AppCompatActivity mContext;

    public static AppCompatActivity getMainActivity() {
        return mContext;
    }

    public static long getmPickingOrderSelected() {
        return mPickingOrderSelected;
    }

//    private DatabaseReference mDatabase;
//
//    //    private FirebaseAuth mAuth;
//    private Empresa mEmpresa;
//    private Usuario mUsuario;
//    private String mFirebaseUrl;
//    private String mUserKey;
//    private String mEmpresaKey;
//    private Perfil mPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        mAuth = FirebaseAuth.getInstance();
////        FirebaseUser user = mAuth.getCurrentUser();
//        if (savedInstanceState == null) {
//
//            mFirebaseUrl = getIntent().getStringExtra(EXTRA_FIREBASE_URL);
//            mUserKey = getIntent().getStringExtra(EXTRA_USER_KEY);
//            mUsuario = (Usuario) getIntent().getParcelableExtra(EXTRA_USER);
//            mEmpresaKey = getIntent().getStringExtra(EXTRA_EMPRESA_KEY);
//
//            mEmpresa = (Empresa) getIntent().getParcelableExtra(EXTRA_EMPRESA);
//            mPerfil = (Perfil) getIntent().getParcelableExtra(EXTRA_PERFIL);
//
//            Log.d(LOG_TAG, "mFirebaseUrl:" + mFirebaseUrl);
//
//            Log.d(LOG_TAG, "onAuthStateChanged:mUserKey:" + mUserKey);
//            Log.d(LOG_TAG, "onAuthStateChanged:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
//
//
//            Log.d(LOG_TAG, "mEmpresaKey:" + mEmpresaKey);
//            Log.d(LOG_TAG, "mEmpresa:" + mEmpresa.getNombre());
//
//            Log.d(LOG_TAG, "Perfil:" + mPerfil.getClientes());
//
//        } else {
//            leerVariablesGlobales(savedInstanceState);
//        }
//
////        if (user != null) {
////            // Check auth on Activity start
////            if (mAuth.getCurrentUser() != null) {
////                Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid()+" - "+user.getDisplayName());
////
////            }
////
////        } else {
////            // User is signed out
////            Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
////        }
        getSupportActionBar().setSubtitle(mEmpresa.getNombre() + "-" + mUsuario.getUsername());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.

     */
        completarSeccionesTitulos();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                switch (position) {
                    case CUSTOM_ORDER_FRAGMENT: {
                        mFab.setVisibility(View.VISIBLE);
                        mFabPasarAPicking.setVisibility(View.VISIBLE);
                        break;
                    }
                    case PICKING_FRAGMENT: {
                        if (mPickingOrderSelected > 0) {
                            mFab.setVisibility(View.GONE);
                        } else {
                            mFab.setVisibility(View.VISIBLE);
                        }
                        mFabPasarAPicking.setVisibility(View.GONE);
                        break;
                    }
                    case DELIVERY_FRAGMENT: {
                        mFab.setVisibility(View.GONE);
                        mFabPasarAPicking.setVisibility(View.GONE);
                        break;
                    }
                    case PAGOS_FRAGMENT: {
                        mFab.setVisibility(View.VISIBLE);
                        mFabPasarAPicking.setVisibility(View.GONE);
                        break;
                    }
                    case STOCK_FRAGMENT: {
                        mFab.setVisibility(View.VISIBLE);
                        mFabPasarAPicking.setVisibility(View.GONE);
                        break;
                    }

                    default:
                }
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//hace que los tabs se puedan escrolear.MODE.FIX entran todos en la pantalla, los apiña
        tabLayout.setupWithViewPager(mViewPager);


        mFabPasarAPicking = (FloatingActionButton) findViewById(R.id.fab_pasarAPicking);
        mFabPasarAPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("main", "FabPasarAPicking click");
                CustomOrderListFragment fragment = (CustomOrderListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + (R.id.container) + ":" + mViewPager.getCurrentItem());

                if (fragment != null) {
                    Log.i("main", "FabPasarAPicking no Nulo");
                    fragment.pasarAPicking();
                } else {
                    Log.i("main", "FabPasarAPicking  Nulo");

                }
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("main", "Fab click");
                if (getResources().getBoolean(R.bool.is_app_free) && isNetworkAvailable(getApplicationContext())) {
                    showInsterstitial();
                } else {
                    fabActions();
                }
            }
        });


        if (findViewById(R.id.adView) != null && this.getResources().getBoolean(R.bool.is_app_free)) {
            // Banner advertising
            AdView mAddView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(getString(R.string.device1))
                    .addTestDevice(getString(R.string.device2))
                    .build();
            mAddView.loadAd(adRequest);

//             interstitial advertising
            mInterstitial = newInterstitialAd();
            loadInterstitial();

        }

    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {


        ////////////////// CUSTOMER for a new Order /////////

        if (requestCode == REQUEST_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            mClienteKey = bundle.getString(EXTRA_CLIENTE_KEY);
            mCliente = bundle.getParcelable(EXTRA_CLIENTE);
            Log.e(LOG_TAG, "mClienteKey: " + mClienteKey);
            Log.e(LOG_TAG, "cliente-apellido: " + mCliente.getNombre());
            Log.e(LOG_TAG, "cliente-Nombre: " + mCliente.getApellido());


//            long customRef = bundle.getLong(CustomSelectionActivity.RESULTADO);

            if (mClienteKey != null) {

                if (mViewPagerFabItem == CUSTOM_ORDER_FRAGMENT) {

                    Log.e(LOG_TAG, "mClienteKey: NO NULO ");

//1a.-
                    mDatabase.child(ESQUEMA_ORDENES).child(mEmpresaKey).child("cabecera").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Totales cabecera_ordenes = mutableData.getValue(Totales.class);
                            if (cabecera_ordenes == null) {
                                Log.d(LOG_TAG, "orden: cabecera Null");
                                cabecera_ordenes = new Totales();
                                cabecera_ordenes.setCantidadDeOrdenesClientes(1);
                            } else {
                                Log.d(LOG_TAG, "orden: cabecera ID" + cabecera_ordenes.getCantidadDeOrdenesClientes());
                                cabecera_ordenes.setCantidadDeOrdenesClientes(cabecera_ordenes.getCantidadDeOrdenesClientes() + 1);                            // Unstar the post and remove self from stars
                            }

                            // Set value and report transaction success
                            mutableData.setValue(cabecera_ordenes);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean commited,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                            Log.d(LOG_TAG, "orden:onComplete:  databaseError" + databaseError);
                            Log.d(LOG_TAG, "orden:onComplete: boolean b" + commited);
                            Totales ordenes1a = dataSnapshot.getValue(Totales.class);
                            long numeroOrden = ordenes1a.getCantidadDeOrdenesClientes();
                            Log.d(LOG_TAG, "orden:onComplete: ID " + numeroOrden);

                            if (commited) {
                                // preparo la cabecera de orden del cliente

                                //Totales en cero
                                Totales totales = new Totales(0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                                CabeceraOrden cabeceraOrden = new CabeceraOrden(mClienteKey, mCliente, ORDEN_STATUS_INICIAL, totales, mUserKey, numeroOrden);
                                cabeceraOrden.setUsuarioCreador(mUsuario.getUsername());
                                Map<String, Object> cabeceraOrdenValues = cabeceraOrden.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
//1b
                                childUpdates.put(NODO_ORDENES + mEmpresaKey + "/" + numeroOrden + "/cabecera", cabeceraOrdenValues);
//2
                                childUpdates.put(NODO_ORDENES_CABECERA + mEmpresaKey + "/" + ORDEN_STATUS_INICIAL + "/" + numeroOrden, cabeceraOrdenValues);
                                mDatabase.updateChildren(childUpdates);

                                Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
                                putExtraFirebase(intent);
                                intent.putExtra(EXTRA_CABECERA_ORDEN, cabeceraOrden);
                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

                                startActivity(intent);
                            }

                        }
                    });

                } else if (mViewPagerFabItem == PAGOS_FRAGMENT) {

                    Intent intent = new Intent(getApplicationContext(), PagosActivity.class);
                    putExtraFirebase(intent);
                    startActivity(intent);
                }

            }


        }


        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode == UPDATE_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                long customRef = bundle.getLong(CustomSelectionActivity.RESULTADO);

                if (customRef != 0) {

                    CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


                    if (fragmentCustomOrder != null) {
                        fragmentCustomOrder.upDateCustomer(customRef);


                    }
                }
            }
        }
        ////////////////// Select a new Product /////////


        if (requestCode == REQUEST_PRODUCT && resultCode == RESULT_OK) {

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


            if (fragmentCustomOrder != null) {
                Producto p = (Producto) data.getExtras().getParcelable(EXTRA_PRODUCT);
                Detalle detalle = new Detalle(0.0, p, mCliente);
                fragmentCustomOrder.abmDetalleDeOrden(p.getCantidadDefault() * 1.0, data.getExtras().getString(EXTRA_PRODUCT_KEY), detalle

                );

            }


        }

        // Cambio en a empresa seleccionada para trabajar.
        if (requestCode == REQUEST_EMPRESA && resultCode == RESULT_OK) {
            Log.d(LOG_TAG, "Cambio enmpres LLego OnResult:");

            Bundle bundle = data.getExtras();
            leerVariablesGlobales(bundle);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu.findItem(R.id.reportexCliente).getSubMenu().setGroupEnabled(R.id.grupo_reportes,false);
//        menu.findItem(R.id.grupo_configuracion).setEnabled(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.setGroupEnabled(R.id.productos_, mPerfil.getProductos());
        menu.setGroupVisible(R.id.productos_, mPerfil.getProductos());
        menu.setGroupEnabled(R.id.customs_, mPerfil.getClientes());
        menu.setGroupVisible(R.id.customs_, mPerfil.getClientes());
        menu.setGroupEnabled(R.id.almacenes_, mPerfil.getClientes());
        menu.setGroupVisible(R.id.almacenes_, mPerfil.getClientes());
        menu.setGroupEnabled(R.id.action_usuarios_, mPerfil.getUsuarios());

        menu.setGroupEnabled(R.id.grupo_reportes, mPerfil.getReportes());
//        menu.clear();


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.productos) {

            Toast.makeText(MainActivity.this, getString(R.string.productToast), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProductListActivity.class);
            putExtraFirebase(intent);
//            intent.putExtra(EXTRA_EMPRESA, mEmpresa);
//            intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
//            intent.putExtra(EXTRA_PERFIL, mPerfil);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());

            return true;
        }
        if (id == R.id.customs) {
            Toast.makeText(MainActivity.this, getString(R.string.CustomToast), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CustomListActivity.class);
            putExtraFirebase(intent);
//                   intent.putExtra(EXTRA_EMPRESA, mEmpresa);
//            intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
//            intent.putExtra(EXTRA_PERFIL, mPerfil);
            startActivity(intent);
//            ActivityOptionsCompat activityOptions =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
//            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            return true;
        }

        if (id == R.id.almacenes) {
            Intent intent = new Intent(this, AlmacenesListActivity.class);
            putExtraFirebase(intent);
            startActivity(intent);
            return true;
        }

        if (id == R.id.reportexCliente) {

            Intent intent1 = new Intent(this, ReporteMensualxCliente.class);
            putExtraFirebase(intent1);
            ActivityOptionsCompat activityOptions1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent1, activityOptions1.toBundle());
            return true;

        }
        if (id == R.id.reportexMes) {

            Intent intent1 = new Intent(this, ReportexMes.class);
            putExtraFirebase(intent1);
            ActivityOptionsCompat activityOptions1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent1, activityOptions1.toBundle());
            return true;
        }
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (id == R.id.action_empresa) {
            Intent intent = new Intent(this, EmpresasActivity.class);
            putExtraFirebase(intent);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_empresaList) {
            Intent intent = new Intent(this, EmpresasListActivity.class);
            putExtraFirebase(intent);
//            intent.putExtra(EXTRA_EMPRESA, mEmpresa);
//            intent.putExtra(EXTRA_FIREBASE_URL, mFirebaseUrl);
//            intent.putExtra(EXTRA_USER, mUsuario);
//            intent.putExtra(EXTRA_PERFIL, mPerfil);
            startActivityForResult(intent, REQUEST_EMPRESA);
            return true;
        }


        if (id == R.id.action_usuarios) {
            Intent intent = new Intent(this, UsuarioListActivity.class);
            putExtraFirebase(intent);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_migrarClientes) {
            migracionClientes();
            migracionProductos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set the picking order in the main activity
    // to assign custom orders and manage the fab visibility in the picking segment
    @Override
    public void onPickingOrderSelected(long pickingOrderID) {
        Log.i(LOG_TAG, "onPickingOrderSelected: " + pickingOrderID);
        mPickingOrderSelected = pickingOrderID;
        if (mPickingOrderSelected > 0) {
            mFab.setVisibility(View.GONE);
        } else {
            mFab.setVisibility(View.VISIBLE);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (mSeccionesDisponibles.get(position)) {

                case CUSTOM_ORDER_FRAGMENT:
                    CustomOrderListFragment a = new CustomOrderListFragment();
                    a.setArguments(putBundleFirebase());
                    return a;

                case PICKING_FRAGMENT:
                    PickingListFragment b = new PickingListFragment();
                    b.setArguments(putBundleFirebase());
                    return b;

                case DELIVERY_FRAGMENT:
                    DeliveryListFragment c = new DeliveryListFragment();
                    c.setArguments(putBundleFirebase());
                    return c;
                case PAGOS_FRAGMENT:
                    SaldosListFragment d = new SaldosListFragment();
                    d.setArguments(putBundleFirebase());
                    return d;

                case STOCK_FRAGMENT:
                    StockListFragment f = new StockListFragment();
                    f.setArguments(putBundleFirebase());
                    return f;

                default:

                    return null;
            }
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return 3;
            return mSeccionesDisponibles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (position <= mTitulosSeccionesDisponibles.size()) {
                return mTitulosSeccionesDisponibles.get(position);
            } else {
                return null;

            }
//            switch (position) {
//                case 0:
//                    return getResources().getString(R.string.title_custom_orders);
//                case 1:
//                    return getResources().getString(R.string.title_picking);
//                case 2:
//                    return getResources().getString(R.string.title_delivery);
//            }
//            return null;
        }


    }

    public void loadInterstitial() {


        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.device1))
                .addTestDevice(getString(R.string.device2))
                .build();
        mInterstitial.loadAd(adRequest);
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {


            @Override
            public void onAdLeftApplication() {

            }

            @Override
            public void onAdOpened() {

            }


            @Override
            public void onAdLoaded() {
                mFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // When Failed, prepear a new add and call tellJoke.
                String mErrorReason = "";
                switch (errorCode) {

                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        mErrorReason = getString(R.string.AddInternalError);
                        break;

                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        mErrorReason = getString(R.string.AddInvalidReques);
                        break;

                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        mErrorReason = getString(R.string.AddNetworkError);
                        break;

                    case AdRequest.ERROR_CODE_NO_FILL:
                        mErrorReason = getString(R.string.AddNoFill);
                        break;
                }
                Toast.makeText(getApplicationContext(),
                        String.format(getString(R.string.AddonFailedToLoad), mErrorReason),
                        Toast.LENGTH_SHORT).show();

                super.onAdFailedToLoad(errorCode);
                Log.d(LOG_TAG, getString(R.string.AddonFailedToLoad) + mErrorReason);

// Se muestra el erro pero no se reintenta mostrar el aviso nuevamente.
//                mInterstitial = newInterstitialAd();
//                loadInterstitial();
            }


            @Override
            public void onAdClosed() {
                mInterstitial = newInterstitialAd();
                loadInterstitial();
                fabActions();
            }
        });
        return interstitialAd;
    }

    public void showInsterstitial() {
        if (mInterstitial != null && mInterstitial.isLoaded()) {

            mInterstitial.show();

        }


    }


    public void fabActions() {
        mViewPagerFabItem = mViewPager.getCurrentItem();
        switch (mViewPagerFabItem) {
            case CUSTOM_ORDER_FRAGMENT: {
                Intent intent = new Intent(getApplicationContext(), CustomSelectionActivity.class);
                putExtraFirebase(intent);
                startActivityForResult(intent, REQUEST_CUSTOMER);

                break;
            }
            case PICKING_FRAGMENT: {


                PickingListFragment fragmentpicking = (PickingListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + (R.id.container) + ":" + mViewPager.getCurrentItem());

                if (fragmentpicking != null) {
                    fragmentpicking.saveNewPickingOrder();
                    break;
                }

            }
            case DELIVERY_FRAGMENT: {
                break;
            }

            case PAGOS_FRAGMENT: {

                Intent intent = new Intent(getApplicationContext(), CustomSelectionActivity.class);
                putExtraFirebase(intent);
                startActivityForResult(intent, REQUEST_CUSTOMER);


                break;
            }

            case STOCK_FRAGMENT: {

                Intent intent = new Intent(getApplicationContext(), MovimientosStockActivity.class);
                putExtraFirebase(intent);
                startActivity(intent);


                break;
            }


            default:
        }
    }

//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save the user's current state
//
//
//        Log.d(LOG_TAG, "onSaveInstanceState-mFirebaseUrl:" + mFirebaseUrl);
//
//        Log.d(LOG_TAG, "onSaveInstanceState-onAuthStateChanged:mUserKey:" + mUserKey);
//        Log.d(LOG_TAG, "onSaveInstanceState-onAuthStateChanged:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
//
//
//        Log.d(LOG_TAG, "onSaveInstanceState-mEmpresaKey:" + mEmpresaKey);
//        Log.d(LOG_TAG, "onSaveInstanceState-mEmpresa:" + mEmpresa.getNombre());
//
//        Log.d(LOG_TAG, "onSaveInstanceState-Perfil:" + mPerfil.getClientes());
//
//
//        savedInstanceState.putString(EXTRA_FIREBASE_URL, mFirebaseUrl);
//        savedInstanceState.putString(EXTRA_USER_KEY, mUserKey);
//        savedInstanceState.putParcelable(EXTRA_USER, mUsuario);
//        savedInstanceState.putString(EXTRA_EMPRESA_KEY, mEmpresaKey);
//        savedInstanceState.putParcelable(EXTRA_EMPRESA, mEmpresa);
//        savedInstanceState.putParcelable(EXTRA_PERFIL, mPerfil);
//
//
//        // Always call the superclass so it can save the view hierarchy state
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore state members from saved instance
//
//
//        mFirebaseUrl = savedInstanceState.getString(EXTRA_FIREBASE_URL);
//        mUserKey = savedInstanceState.getString(EXTRA_USER_KEY);
//        mUsuario = savedInstanceState.getParcelable(EXTRA_USER);
//        mEmpresaKey = savedInstanceState.getString(EXTRA_EMPRESA_KEY);
//        mEmpresa = savedInstanceState.getParcelable(EXTRA_EMPRESA);
//        mPerfil = savedInstanceState.getParcelable(EXTRA_PERFIL);
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore state members from saved instance
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-mFirebaseUrl:" + mFirebaseUrl);
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUserKey:" + mUserKey);
//        Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
//
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresaKey:" + mEmpresaKey);
//        Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresa:" + mEmpresa.getNombre());
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-Perfil:" + mPerfil.getClientes());
//    }
//
//    private void leerVariablesGlobales(Bundle savedInstanceState) {
//        mFirebaseUrl = savedInstanceState.getString(EXTRA_FIREBASE_URL);
//        mUserKey = savedInstanceState.getString(EXTRA_USER_KEY);
//        mUsuario = savedInstanceState.getParcelable(EXTRA_USER);
//        mEmpresaKey = savedInstanceState.getString(EXTRA_EMPRESA_KEY);
//        mEmpresa = savedInstanceState.getParcelable(EXTRA_EMPRESA);
//        mPerfil = savedInstanceState.getParcelable(EXTRA_PERFIL);
//        // Always call the superclass so it can restore the view hierarchy
//        super.onRestoreInstanceState(savedInstanceState);
//
//        // Restore state members from saved instance
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-mFirebaseUrl:" + mFirebaseUrl);
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUserKey:" + mUserKey);
//        Log.d(LOG_TAG, "onRestoreInstanceState-onAuthStateChanged:mUsuario:" + mUsuario.getUsername() + " - " + mUsuario.getEmail());
//
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresaKey:" + mEmpresaKey);
//        Log.d(LOG_TAG, "onRestoreInstanceState-mEmpresa:" + mEmpresa.getNombre());
//
//        Log.d(LOG_TAG, "onRestoreInstanceState-Perfil:" + mPerfil.getClientes());
//    }

    private void completarSeccionesTitulos() {


        if (mPerfil.getOrdenes()) {
            mSeccionesDisponibles.add(CUSTOM_ORDER_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_custom_orders));
        }
        if (mPerfil.getPreparar()) {
            mSeccionesDisponibles.add(PICKING_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_picking));
        }

        if (mPerfil.getEntregar()) {
            mSeccionesDisponibles.add(DELIVERY_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_delivery));
        }

        if (mPerfil.getPagos()) {
            mSeccionesDisponibles.add(PAGOS_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_pagos));
        }
        if (mPerfil.getStock()) {
            mSeccionesDisponibles.add(STOCK_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_stock));
        }

    }

    public void migracionClientes() {
        Log.i("migracionCliente", "inicio");
        try {
            Cursor data = getContentResolver().query(LogisticaProvider.Customs.CONTENT_URI,
                    null,
                    null,
                    null,
                    null,
                    null);
            Log.i("migracionCliente", "cursor");

            if (data != null && data.getCount() > 0) {
                data.moveToFirst();
                Log.i("migracionCliente", "cursor >0");


                do {

                    writeNewCliente(
                            data.getString(data.getColumnIndex(CustomColumns.ID_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)),
                            data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)),
                            Double.parseDouble(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM))),
                            data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)),
                            data.getInt(data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM)) > 0,
                            migrarTelefonosDelContactoAsociado(getMainActivity() ,data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM)))
                    );
                } while (data.moveToNext());


            } else {
                Log.i("migracionCliente", "cursor <=0");

            }

        } catch (Exception e) {
        }
    }


    // [START basic_write]
    private void writeNewCliente(String Id,
                                 String nombre,
                                 String apellido,
                                 String telefono,
                                 String fotoCliente,
                                 String direccionDeEntrega,
                                 String ciudad,
                                 Double iva,
                                 String cuit,
                                 Boolean especial,
                                 Map<String, String> telefonos
    ) {
        if (true) {//validar formulario
            Log.i("migracionCliente", "writeNewClient: nombre " + nombre);
            Log.i("migracionCliente", "writeNewClient: apellido " + apellido);
            Log.i("migracionCliente", "writeNewClient: telefono " + telefono);
            Log.i("migracionCliente", "writeNewClient: fotoCliente " + fotoCliente);
            Log.i("migracionCliente", "writeNewClient: direccionDeEntrega " + direccionDeEntrega);
            Log.i("migracionCliente", "writeNewClient: ciudad " + ciudad);
            Log.i("migracionCliente", "writeNewClient: IVA " + iva);
            Log.i("migracionCliente", "writeNewClient: cUIT " + cuit);
            Log.i("migracionCliente", "writeNewClient: especial " + especial);


            Cliente cliente = new Cliente("Migracion",
                    nombre,
                    apellido,
                    telefono,
                    fotoCliente,
                    direccionDeEntrega,
                    ciudad,
                    iva,
                    cuit,
                    especial, telefonos);

            if (mClienteKey == null) {
                mClienteKey = mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).push().getKey();
            }

            Map<String, Object> clienteValues = cliente.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(NODO_EMPRESA_CLIENTES + mEmpresaKey + "/" + Id, clienteValues);

            mDatabase.updateChildren(childUpdates);
        }
    }


    public void migracionProductos() {
        Log.i("migracionProductos", "inicio");
        try {
            Cursor data = getContentResolver().query(LogisticaProvider.Products.CONTENT_URI,
                    null,
                    null,
                    null,
                    null,
                    null);
            Log.i("migracionProductos", "cursor");

            if (data != null && data.getCount() > 0) {
                data.moveToFirst();
                Log.i("migracionCliente", "cursor >0");


                do {

                    writeNewProducto(
                            data.getString(data.getColumnIndex(ProductsColumns._ID_PRODUCTO)),
                            data.getString(data.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)),
                            data.getDouble(data.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)),
                            data.getDouble(data.getColumnIndex(ProductsColumns.PRECIO_SPECIAL_PRODUCTO)),
                            data.getString(data.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)),
                            "mCurrentPhotoPath",
                            mUserKey,
                            "Rubro 1",
                            "Unidad",
                            1,
                            1,
                            5000
                    );

                } while (data.moveToNext());


            } else {
                Log.i("migracionProductos", "cursor <=0");

            }

        } catch (Exception e) {
        }
    }


    // [START basic_write]
    private void writeNewProducto(String Id,String nombreProducto, Double precio, Double precioEspcecial, String descripcionProducto, String fotoProducto, String uid,
                                  String rubro,
                                  String tipoUnidad,
                                  int cantidadMinima,
                                  int cantidadMaxima,
                                  int cantidadDefault

    ) {
        if (true) {//validar formulario
            Log.i("migracionProductos", "writeNewProducto: nombre " + nombreProducto);
            Log.i("migracionProductos", "writeNewProducto: precio " + precio);
            Log.i("migracionProductos", "writeNewProducto: precio " + precio);
            Log.i("migracionProductos", "writeNewProducto: precioEspcecial " + precioEspcecial);
            Log.i("migracionProductos", "writeNewProducto: fotoProducto " + fotoProducto);
            Log.i("migracionProductos", "writeNewProducto: uid " + uid);
            Log.i("migracionProductos", "writeNewProducto: mProductKey " + mProductKey);

            Producto producto = new Producto(uid, nombreProducto, precio, precioEspcecial, descripcionProducto, fotoProducto,
                    rubro,
                    tipoUnidad,
                    cantidadMinima,
                    cantidadMaxima,
                    cantidadDefault);

            Map<String, Object> productoValues = producto.toMap();
            Map<String, Object> childUpdates = new HashMap<>();



            childUpdates.put(NODO_EMPRESA_PRODUCTOS + mEmpresaKey + "/" + Id, productoValues);

            mDatabase.updateChildren(childUpdates);

        }
    }


}
