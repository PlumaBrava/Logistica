package com.nextnut.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.util.ProductSectionActivity;

import java.util.ArrayList;

import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.nextnut.logistica.util.Constantes.EXTRA_CLIENTE_KEY;
import static com.nextnut.logistica.util.Constantes.REQUEST_CUSTOMER;
import static com.nextnut.logistica.util.Constantes.REQUEST_EMPRESA;
import static com.nextnut.logistica.util.Constantes.REQUEST_PRODUCT;
import static com.nextnut.logistica.util.Constantes.UPDATE_CUSTOMER;


public class MainActivity extends ActivityBasic implements PickingListFragment.PickingOrdersHandler {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArrayList<Integer> mSeccionesDisponibles = new ArrayList<Integer>();
    private ArrayList<String> mTitulosSeccionesDisponibles = new ArrayList<String>();

    private InterstitialAd mInterstitial;
    public static final int CUSTOM_ORDER_FRAGMENT = 0;
    public static final int PICKING_FRAGMENT = 1;
    public static final int DELIVERY_FRAGMENT = 2;
    public static final String USER_DISPLAY_NAME = "userDisplayName";
    public static final String USER_ID = "userId";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static long mPickingOrderSelected = 0;
    public FloatingActionButton mFab;

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
                        break;
                    }
                    case PICKING_FRAGMENT: {
                        if (mPickingOrderSelected > 0) {
                            mFab.setVisibility(View.GONE);
                        } else {
                            mFab.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case DELIVERY_FRAGMENT: {
                        mFab.setVisibility(View.GONE);
                        break;
                    }
                    default:
                }
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//hace que los tabs se puedan escrolear.MODE.FIX entran todos en la pantalla, los apiña
        tabLayout.setupWithViewPager(mViewPager);

        mFab = (FloatingActionButton) findViewById(R.id.fab);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getBoolean(R.bool.is_app_free)) {
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

            mClienteKey =bundle.getString(EXTRA_CLIENTE_KEY);
            Cliente cliente=bundle.getParcelable(EXTRA_CLIENTE);
            Log.e(LOG_TAG, "mClienteKey: " +mClienteKey);
            Log.e(LOG_TAG, "cliente-apellido: " +cliente.getNombre());
            Log.e(LOG_TAG, "cliente-Nombre: " +cliente.getApellido());


//            long customRef = bundle.getLong(CustomSelectionActivity.RESULTADO);

            if (mClienteKey != null) {

                Log.e(LOG_TAG, "mClienteKey: NO NULO " );

//                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrders.CONTENT_URI);
//                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customRef);
//                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
//                String formattedDate = df.format(new Date());
//                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
//                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);
//
//                batchOperations.add(builder.build());
//                try {
//
//                    getApplicationContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                } catch (RemoteException | OperationApplicationException e) {
//                    Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
//                }

            }

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
            Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
            putExtraFirebase(intent);

            intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

            startActivity(intent);


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
                fragmentCustomOrder.saveCustomOrderProduct(data.getExtras().getLong(ProductSectionActivity.KEY_RefPRODUCTO),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_NAME),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICES_ESPECIAL),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICE)
                );

            }


        }

        // Cambio en a empresa seleccionada para trabajar.
        if (requestCode == REQUEST_EMPRESA && resultCode == RESULT_OK) {
                        Log.d(LOG_TAG, "Cambio enmpres LLego OnResult:" );

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
            Intent intent =new Intent(this, UsuarioListActivity.class);
            putExtraFirebase(intent);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set the picking order in the main activity
    // to assign custom orders and manage the fab visibility in the picking segment
    @Override
    public void onPickingOrderSelected(long pickingOrderID) {
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
                    return new CustomOrderListFragment();

                case PICKING_FRAGMENT:
                    PickingListFragment a = new PickingListFragment();
                    return a;

                case DELIVERY_FRAGMENT:
                    DeliveryListFragment b = new DeliveryListFragment();
                    return b;

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
        Log.d(LOG_TAG, getString(R.string.AddonFailedToLoad)+ mErrorReason);

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

        switch (mViewPager.getCurrentItem()) {
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
            mSeccionesDisponibles.add(DELIVERY_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_pagos));
        }
        if (mPerfil.getStock()) {
            mSeccionesDisponibles.add(DELIVERY_FRAGMENT);
            mTitulosSeccionesDisponibles.add(getResources().getString(R.string.title_stock));
        }

    }

}
