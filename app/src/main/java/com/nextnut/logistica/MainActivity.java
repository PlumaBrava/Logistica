package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.nextnut.logistica.util.ProductSectionActivity;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements PickingListFragment.PickingOrdersHandler {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    public static final int CUSTOM_ORDER_FRAGMENT=0;
    public static final int PICKING_FRAGMENT=1;
    public static final int DELIVERY_FRAGMENT=2;

//    private static final int REQUEST_CUSTOMER = 1234;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static long mPickingOrderSelected=0;
    public FloatingActionButton mFab;
    public static long    getmPickingOrderSelected(){
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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
                Log.i(LOG_TAG,"addOnPageChangeListener-onPageScrollStateChanged"+state);
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(LOG_TAG,"addOnPageChangeListener-onPageScrolled"+position+"-"+positionOffset+"-"+positionOffsetPixels);
            }

            public void onPageSelected(int position) {
                Log.i(LOG_TAG, "addOnPageChangeListener-xxxonPageSelecte" + position);
                switch (position) {
                    case CUSTOM_ORDER_FRAGMENT: {
                        mFab.setVisibility(View.VISIBLE);
                        Log.i(LOG_TAG, "xxxorder" + position);
                        break;
                    }
                    case PICKING_FRAGMENT: {
                        if(mPickingOrderSelected>0){
                            mFab.setVisibility(View.GONE);
                            Log.i(LOG_TAG, "xxxpickinga" + position);
                        }else{
                            mFab.setVisibility(View.VISIBLE);
                            Log.i(LOG_TAG, "xxxpickingb" + position);
                        }
                        break;
                    }
                    case DELIVERY_FRAGMENT: {
                            mFab.setVisibility(View.GONE);
                        Log.i(LOG_TAG, "xxxfragmentr" + position);
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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Log.i(LOG_TAG,"mCurrentFragmet"+mViewPager.getCurrentItem());

                switch (mViewPager.getCurrentItem()) {
                    case CUSTOM_ORDER_FRAGMENT: {
                        Log.i(LOG_TAG,"mCurrentFragmet CUSTOM_ORDER_FRAGMENT "+mViewPager.getCurrentItem());
                        Intent intent = new Intent(getApplicationContext(), CustomSelectionActivity.class);
                        startActivityForResult(intent,CustomOrderDetailFragment. REQUEST_CUSTOMER);

//                        Intent intent = new Intent(MainActivity.this, CustomOrderDetailActivity.class);
//                        intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION,CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
//                        ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());

                        break;
                    }
                    case PICKING_FRAGMENT: {


                        PickingListFragment fragmentpicking = (PickingListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + (R.id.container) + ":" + mViewPager.getCurrentItem());

                        if (fragmentpicking != null) {
                            Log.i(LOG_TAG,"Picking Frament Not Null");
                            fragmentpicking.saveNewPickingOrder();
                            break;
                        }else {
                            Log.i(LOG_TAG, "Picking Frament  Null");
                        }
                    }
                    case DELIVERY_FRAGMENT: {
                        break;
                    }
                    default:
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        Log.i(LOG_TAG, "LLego resultado ok" );
        Log.i(LOG_TAG, "LLego requestCode: "+requestCode );
        Log.i(LOG_TAG, "LLego resultCode: "+resultCode );

        ////////////////// CUSTOMER for a new Order /////////

        if (requestCode == CustomOrderDetailFragment. REQUEST_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle == null){ Log.i(LOG_TAG, "LLego resyktado ok" + "bundleNULL");}
            else {Log.i(LOG_TAG, "LLego resyktado ok" + "bundle ok: " + bundle.toString());

                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.i("resyktado", String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }


            }

            String res = bundle .getString("resultado");
            Log.i(LOG_TAG, "LLego resyktado ok " + res);
            Log.i(LOG_TAG, "LLego resyktado ok int " + bundle .getInt("resultado"));
            long customRef = bundle.getLong("resultado");

            Log.i(LOG_TAG, "LLego resyktado ok long " + customRef);

            if (customRef != 0) {
//            if ( mItem != 0) {
                Log.i(LOG_TAG, "save New");
                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.CustomOrders.CONTENT_URI);
                builder.withValue(CustomOrdersColumns.REF_CUSTOM_CUSTOM_ORDER, customRef);
                SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
                String formattedDate = df.format(new Date());
                Log.i(LOG_TAG, "formattedDate:" + formattedDate);
                builder.withValue(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER, formattedDate);
                builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, CustomOrderDetailFragment.STATUS_ORDER_INICIAL);

                batchOperations.add(builder.build());
            try {

                getApplicationContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }

            }

            CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);




            Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
            intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
            Log.i(LOG_TAG, "ARG_ITEM_ID: 1" + customRef);
            Log.i(LOG_TAG, "CUSTOM_ACTION" + CustomOrderDetailFragment.CUSTOM_ORDER_NEW);

            startActivity(intent);


        }



        ////////////////// UPDATE CUSTOMER /////////
        if (requestCode == CustomOrderDetailFragment. UPDATE_CUSTOMER && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                long customRef = bundle.getLong("resultado");

                Log.i(LOG_TAG, "LLego resyktado ok long " + customRef);

                if (customRef != 0) {

                    CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


                    if (fragmentCustomOrder != null) {
                        Log.i(LOG_TAG, "Picking Frament Not Null");
                        fragmentCustomOrder.upDateCustomer(customRef);


                    } else {
                        Log.i(LOG_TAG, "Picking Frament  Null");
                    }
                }
                }
            }
        ////////////////// Select a new Product /////////


            if (requestCode == CustomOrderDetailFragment.REQUEST_PRODUCT && resultCode == RESULT_OK) {
                String res = data.getExtras().getString("resultado");
                Log.i(LOG_TAG, "REQUEST_PRODUCT " + data.getExtras().getString("ProductoName"));

                Log.i(LOG_TAG, "REQUEST_PRODUCT ProductPrice" + data.getExtras().getString("ProductPrice"));

                CustomOrderDetailFragment fragmentCustomOrder = (CustomOrderDetailFragment) getSupportFragmentManager().findFragmentById(R.id.customorder_detail_container);


                if (fragmentCustomOrder != null) {
                    Log.i(LOG_TAG, "Picking Frament Not Null");
                    fragmentCustomOrder.saveCustomOrderProduct(data.getExtras().getLong(ProductSectionActivity.KEY_RefPRODUCTO),
                            data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_NAME),
                            data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICES_ESPECIAL),
                            data.getExtras().getString(ProductSectionActivity.KEY_PRODUCTO_PRICE)
                    );

                } else {
                    Log.i(LOG_TAG, "Picking Frament  Null");
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

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(MainActivity.this, "Action Sttings", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this,LoginActivity.class);
//            ActivityOptionsCompat activityOptions =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
//            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
//            return true;
//        }

        if (id == R.id.productos) {
            Toast.makeText(MainActivity.this, "Productos", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,ProductListActivity.class);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());

            return true;
        }
        if (id == R.id.customs) {
            Toast.makeText(MainActivity.this, "Customs", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CustomListActivity.class);

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            return true;
        }  if (id == R.id.reportexCliente) {

            Intent intent1 = new Intent(this, ReporteMensualxCliente.class);

            ActivityOptionsCompat activityOptions1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent1, activityOptions1.toBundle());
            return true;

        } if (id == R.id.reportexMes) {

                    Intent intent1 = new Intent(this,ReportexMes.class);

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
        if(mPickingOrderSelected>0){
            mFab.setVisibility(View.GONE);
            Log.i(LOG_TAG, "xxxpickinga" + pickingOrderID);
        }else{
            mFab.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "xxxpickingb" + pickingOrderID);
        }
        Log.i("Main:", "mPickingOrderSelected " +mPickingOrderSelected);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter  {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            Log.i("Main:", "getItem" + position);
            switch (position) {

                case CUSTOM_ORDER_FRAGMENT:
                   return new CustomOrderListFragment();

                case PICKING_FRAGMENT:
                    PickingListFragment a= new PickingListFragment();

                    Log.i("Main:", "Fragmet ID " +a.getId());
                    return a;

                case DELIVERY_FRAGMENT:
                    DeliveryListFragment b= new DeliveryListFragment();

                    Log.i("Main:", "Fragmet ID " +b.getId());
                    return b;

                default:

                return null;
            }
        }

        @Override
        public void startUpdate(ViewGroup container) {
            Log.i("Main:", "startUpdate" );
            super.startUpdate(container);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.i("Main:", "getPageTitle: "+position );
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
}
