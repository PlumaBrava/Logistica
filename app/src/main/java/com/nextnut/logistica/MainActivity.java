package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
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
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.util.ProductSectionActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements PickingListFragment.PickingOrdersHandler {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        tabLayout.setupWithViewPager(mViewPager);

        mFab = (FloatingActionButton) findViewById(R.id.fab);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getBoolean(R.bool.is_app_free)) {
                    showInsterstitial();
                } else {
                    switch (mViewPager.getCurrentItem()) {
                        case CUSTOM_ORDER_FRAGMENT: {
                            Intent intent = new Intent(getApplicationContext(), CustomSelectionActivity.class);
                            startActivityForResult(intent, CustomOrderDetailFragment.REQUEST_CUSTOMER);
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

        if (requestCode == CustomOrderDetailFragment.REQUEST_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            long customRef = bundle.getLong(CustomSelectionActivity.RESULTADO);

            if (customRef != 0) {
                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrders.CONTENT_URI);
                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customRef);
                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                String formattedDate = df.format(new Date());
                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

                batchOperations.add(builder.build());
                try {

                    getApplicationContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(LOG_TAG, getString(R.string.InformeErrorApplyingBatchInsert), e);
                }

            }

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);
            Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
            intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

            startActivity(intent);


        }


        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode == CustomOrderDetailFragment.UPDATE_CUSTOMER && resultCode == RESULT_OK) {
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


        if (requestCode == CustomOrderDetailFragment.REQUEST_PRODUCT && resultCode == RESULT_OK) {

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


            if (fragmentCustomOrder != null) {
                fragmentCustomOrder.saveCustomOrderProduct(data.getExtras().getLong(ProductSectionActivity.KEY_RefPRODUCTO),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_NAME),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICES_ESPECIAL),
                        data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICE)
                );

            }


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());

            return true;
        }
        if (id == R.id.customs) {
            Toast.makeText(MainActivity.this, getString(R.string.productToast), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CustomListActivity.class);

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            return true;
        }
        if (id == R.id.reportexCliente) {

            Intent intent1 = new Intent(this, ReporteMensualxCliente.class);

            ActivityOptionsCompat activityOptions1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent1, activityOptions1.toBundle());
            return true;

        }
        if (id == R.id.reportexMes) {

            Intent intent1 = new Intent(this, ReportexMes.class);

            ActivityOptionsCompat activityOptions1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent1, activityOptions1.toBundle());
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
            switch (position) {

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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_custom_orders);
                case 1:
                    return getResources().getString(R.string.title_picking);
                case 2:
                    return getResources().getString(R.string.title_delivery);
            }
            return null;
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


                mInterstitial = newInterstitialAd();
                loadInterstitial();
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
                startActivityForResult(intent, CustomOrderDetailFragment.REQUEST_CUSTOMER);
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
}
