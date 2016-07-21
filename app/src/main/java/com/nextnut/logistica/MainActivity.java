package com.nextnut.logistica;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Build;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

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

    public int mCurrentFragmet;
    public static final int CUSTOM_ORDER_FRAGMENT=0;
    public static final int PICKING_FRAGMENT=1;
    public static final int DELIVERY_FRAGMENT=2;

    private static final int REQUEST_CUSTOMER = 1234;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

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

        // Eliminar si no se usa la interfase.

        CustomOrderListFragment.DataChangeNotification a= new CustomOrderListFragment.DataChangeNotification() {
            @Override
            public void onStepModification(int step) {

            }
        }

                ;
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.i(LOG_TAG,"mCurrentFragmet"+mViewPager.getCurrentItem());

                switch (mViewPager.getCurrentItem()) {
                    case CUSTOM_ORDER_FRAGMENT: {
                        Log.i(LOG_TAG,"mCurrentFragmet CUSTOM_ORDER_FRAGMENT "+mViewPager.getCurrentItem());
                        Intent intent = new Intent(getApplicationContext(), CustomSelectionActivity.class);
                        startActivityForResult(intent, REQUEST_CUSTOMER);

//                        Intent intent = new Intent(MainActivity.this, CustomOrderDetailActivity.class);
//                        intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION,CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//                        ActivityOptionsCompat activityOptions =
//                                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
//                        ActivityCompat.startActivity(MainActivity.this, intent, activityOptions.toBundle());

                        break;
                    }
                    case PICKING_FRAGMENT: {
                        break;
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
        Log.i(LOG_TAG, "LLego resyktado ok" );
        if (requestCode == REQUEST_CUSTOMER && resultCode == RESULT_OK) {
            String res = data.getExtras().getString("resultado");
            Log.i(LOG_TAG, "LLego resyktado ok" + res);
            long customRef = data.getExtras().getLong("resultado");



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
            Intent intent = new Intent(getApplicationContext(), CustomOrderDetailActivity.class);
            intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
            Log.i(LOG_TAG, "ARG_ITEM_ID: 1" + customRef);
            Log.i(LOG_TAG, "CUSTOM_ACTION" + CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//            intent.putExtra(CustomDetailFragment.ARG_ITEM_ID, customRef);
//                            fab_new.setVisibility(View.VISIBLE);
//                            fab_save.setVisibility(View.GONE);
//                            fab_delete.setVisibility(View.GONE);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                Log.i("ProductListActivity", "makeSceneTransitionAnimation");
//
////                                Pair<View, String> p1 = Pair.create((View) vh.mphotoCustomer, getString(R.string.custom_icon_transition_imagen));
////                Pair<View, String> p2 = Pair.create((View) vh.mName, getString(R.string.custom_icon_transition_name));
////                                Pair<View, String> p3 = Pair.create((View) vh.mSurename, getString(R.string.custom_icon_transition_surname));
//                ActivityOptionsCompat activityOptions =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
//                startActivity(intent, activityOptions.toBundle());
//
//            } else {
            Log.i("ProductListActivity", "makeSceneTransitionAnimation Normal");
            startActivity(intent);
//            }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Action Sttings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,LoginActivity.class);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            return true;
        }

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
            Intent intent = new Intent(this,CustomListActivity.class);

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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
//                   return new CustomOrderListFragment(new CustomOrderListFragment.DataChangeNotification() {
//                       @Override
//                       public void onStepModification(int step) {
//                           Log.i("Main:", "onStepModification" + step);
//                        View view = mViewPager.getChildAt(1);
//                           if (view != null) {
//                               mViewPager.removeView(view);
//
//                               instantiateItem(mViewPager, 1);
//                           }
//                           mSectionsPagerAdapter.destroyItem(mViewPager,1, new PickingListFragment());
//                           mViewPager.destroyDrawingCache();
//                           mSectionsPagerAdapter.notifyDataSetChanged();

//                           View view = mViewPager.getChildAt(1);
//                           if (view != null) {
//                               mViewPager.removeView(view);
//                               getItem(1);
////                               mSectionsPagerAdapter.notifyDataSetChanged();
//                                                        }
//
////
//                       }
//                   });
                case PICKING_FRAGMENT:
                    return new PickingListFragment();

                default:
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
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
