package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.Util.CurrencyToDouble;
import com.nextnut.logistica.Util.NumberTextWatcher;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.dummy.DummyContent;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String PRODUCT_ACTION = "product_action";
    public static final int PRODUCT_NEW = 0;
    public static final int PRODUCT_DOUBLE_SCREEN = 1;
    public static final int PRODUCT_SAVE = 2;
    public static final int PRODUCT_SELECTION = 3;
    private static final int DETAIL_PRODUCT_LOADER = 0;
    private static final int DEFAULT_DETAIL_PRODUCT_LOADER = 1;
    private static final int NAME_PRODUCT_LOADER = 2;
    private static final String LOG_TAG = ProductDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private int mItem=0;
    private TextView mProductId;
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductDescription;
    private ImageView mImageProducto;
    private int mAction;
    String mCurrentPhotoPath=null;
    private Button button;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_IMAGE_GET = 1889;
    CollapsingToolbarLayout appBarLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = getArguments().getInt(ARG_ITEM_ID);
            Log.i(LOG_TAG,"ARG_ITEM_ID:"+ mItem);
            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);


            if (appBarLayout != null) {
                if (mAction==PRODUCT_NEW) {
                    appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
                } else {appBarLayout.setTitle("TITLE");}
            }

        }
//        if (getArguments().containsKey(PRODUCT_MODIFICACION)) {
            mAction=getArguments().getInt(PRODUCT_ACTION);
            Log.i(LOG_TAG,"PRODUCT_ACTION"+ mAction);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        mProductId = (TextView) rootView.findViewById(R.id.product_Id_text);
        mProductName = (EditText) rootView.findViewById(R.id.product_name_text);
        Log.i(LOG_TAG,"OnEditorActionListener check10");
        mProductName.addTextChangedListener(new TextWatcher() {
                                                public void afterTextChanged(Editable s) {
                                                    Log.i(LOG_TAG,"OnEditorActionListener afterTextChanged s: "+s.toString());


                                                }

                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                    Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged s:"+s.toString());
                                                    Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged, start: "+start);
                                                    Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged, after:"+after);
        /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/
                                                }

                                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                    Log.i(LOG_TAG,"OnEditorActionListener onTextChanged s:"+s.toString());
                                                    Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, start: "+start);
                                                    Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, before: "+before);
                                                    Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, count:"+count);
                                                    Boolean modifyText=false;
                                                    StringBuilder b = new StringBuilder();

                                                    for(int i =0 ; i < s.length(); i++) {
                                                        if(s.charAt(i)=='\n'){
                                                            Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, Enter detected");
                                                            modifyText=true;
                                                        }
                                                        else {
                                                            b.append(s.charAt(i));
                                                        }

                                                        if (modifyText){
                                                                        // hide keyboard before calling the done action
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

                                                            checkProductName(b.toString());
                                                            mProductName.setText(b.toString());
                                                        }




                                                   }
                                                }
                                            }
            );

//        mProductName.setOnKeyListener(new View.OnKeyListener() {
//
////
////            // hide keyboard before calling the done action
////            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
////                    Context.INPUT_METHOD_SERVICE);
////            View view = getActivity().getCurrentFocus();
////            if (view != null) {
////                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
////            }
//
//
//            @Override
//            public boolean onKey(View v, int actionId,
//                                          KeyEvent event) {
//                Log.i(LOG_TAG,"OnEditorActionListener TextView v"+v.toString());
//                Log.i(LOG_TAG,"OnEditorActionListener actionId: "+actionId);
//                Log.i(LOG_TAG,"OnEditorActionListener event"+event.toString());
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_DONE || event != null && (event.getAction() == KeyEvent.ACTION_DOWN &&
//                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    Log.i(LOG_TAG,"OnEditorActionListener KEYCODE_ENTER");
//                        return true;
//                    }
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Log.i(LOG_TAG,"OnEditorActionListener KEYCODE_ENTER");
//                    handled = true;
//                }
//                return handled;
//            }
//        });


        mProductPrice = (EditText) rootView.findViewById(R.id.product_price);

        mProductPrice.addTextChangedListener(new NumberTextWatcher(mProductPrice));
        mProductDescription = (EditText) rootView.findViewById(R.id.product_description);
        Log.i(LOG_TAG,"checkProductName, Enter chck2");
        Log.i(LOG_TAG,"checkProductName, Enter detected");
        mProductDescription.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            Log.i(LOG_TAG,"OnEditorActionListener afterTextChanged s: "+s.toString());
             }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                           Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged s:"+s.toString());
                                                           Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged, start: "+start);
                                                           Log.i(LOG_TAG,"OnEditorActionListener beforeTextChanged, after:"+after);
        /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(LOG_TAG,"OnEditorActionListener onTextChanged s:"+s.toString());
                Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, start: "+start);
                Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, before: "+before);
                Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, count:"+count);
                Boolean modifyText=false;
                StringBuilder b = new StringBuilder();
                Log.i(LOG_TAG,"checkProductName, onTextChanged");
                    for(int i =0 ; i < s.length(); i++) {
                    Log.i(LOG_TAG,"jj, "+i);
                       if(s.charAt(i)=='\n'){
                         modifyText=true;
                         Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, Enter detected");
                         Log.i(LOG_TAG,"jj, Enter detected");
                       }
                       else {
                        b.append(s.charAt(i));
                        }
                    }
                    if (modifyText){
                    Log.i(LOG_TAG,"OnEditorActionListener onTextChanged, Enter Modify");
                     // hide keyboard before calling the done action
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                          View view = getActivity().getCurrentFocus();
                          if (view != null) {
                          inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                          }
                          checkProductName(b.toString());
                          mProductDescription.setText(b.toString());
                    }
            }
        });
        mImageProducto = ((ImageView) rootView.findViewById(R.id.product_imagen));
        button = ((Button) rootView.findViewById(R.id.product_imagen_button));
        // Show the dummy content as text in a TextView.


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage();


                }

            });

            if(mCurrentPhotoPath==null) {
                Log.i("prdDetFrament","mCurrentPhotoPath=null");
                mImageProducto.setImageResource(R.drawable.art_clear);
            }else {
                Log.i("prdDetFrament","mCurrentPhotoPath!=null");
            }
        if (mAction==PRODUCT_NEW) {
            if (appBarLayout != null) {
                appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
            }
            mProductId.setText(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
        }



        return rootView;
    }

    public boolean checkProductName (String productName){

        Log.e(LOG_TAG, "jj");

        String select = "((" + ProductsColumns.NOMBRE_PRODUCTO + " NOTNULL) AND ("
                + ProductsColumns.NOMBRE_PRODUCTO+ " =?))";


        String projection []= {ProductsColumns.NOMBRE_PRODUCTO, "sum("+ ProductsColumns.PRECIO_PRODUCTO
                + " * " + ProductsColumns.PRECIO_PRODUCTO
                +")"};
        String arg[]={productName};

        Cursor c=getActivity().getContentResolver().query(LogisticaProvider.Products.CONTENT_URI,
                null,
                select,
                arg,
                null);

        Log.e(LOG_TAG, "jj Cursor:"+ c.getCount());

        return true;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (mAction) {
            case  PRODUCT_DOUBLE_SCREEN:
                if( mItem == 0) {
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-default DETAIL_PRODUCT_LOADER");
                getLoaderManager().initLoader(DEFAULT_DETAIL_PRODUCT_LOADER, null, this);
                }
                else {
                getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_DOUBLE_SCREEN-DETAIL_PRODUCT_LOADER");
                }
            break;
            case PRODUCT_SELECTION:
                getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
                Log.e(LOG_TAG, "onActivityCreated-PRODUCT_SELECTION");
                break;
            default:
                break;
        }

//        getLoaderManager().initLoader(NAME_PRODUCT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "Product_"+timeStamp;
        File file = new File(getContext().getFilesDir(), filename);

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return file;
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

                if (resultCode == Activity.RESULT_OK) {

                    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {


                        Bitmap bmp = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        // convert byte array to Bitmap

                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                                byteArray.length);


                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String filename = "Product_" + timeStamp;

                        File file = new File(getContext().getFilesDir(), filename);

                        FileOutputStream outputStream;

                        try {
                            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
//                    outputStream.write(string.getBytes());
                            outputStream.write(byteArray);
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mCurrentPhotoPath = "file:" + file.getAbsolutePath();

                        Picasso.with(getContext())
                                .load(mCurrentPhotoPath)
                                .placeholder(R.drawable.art_clear)
                                .into(mImageProducto);

//                mProductId.setText(mCurrentPhotoPath);


                    } else if (requestCode == REQUEST_IMAGE_GET) {
                        Bitmap bm = null;
                        if (data != null) {
                            try {
                                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                                mCurrentPhotoPath = String.valueOf(data.getData());
                                Log.i("prdDetFrament", "mCurrentPhotoPath:" + mCurrentPhotoPath);

                                Picasso.with(getContext())
                                        .load(data.getData())
                                        .into(mImageProducto);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }

        }
    }

    public void deleteProduct() {

        Log.i(LOG_TAG,"deleteProduct");
        Log.i(LOG_TAG,"mAction"+mAction);
        Log.i(LOG_TAG," mItem"+ mItem);
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mAction==PRODUCT_SELECTION && mItem!=0) {

            Log.i(LOG_TAG,"entro");
//            ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete();
//            batchOperations.add(builder.build());
            getActivity().getContentResolver().delete(
                    LogisticaProvider.Products.withId(mItem), null, null);
            getActivity().onBackPressed();
        }

    }

    public void verificationAndsave() {

    Log.i(LOG_TAG,"save");
        if(verification()) {
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
            if (mAction==PRODUCT_NEW && mItem==0) {


                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Products.CONTENT_URI);
                builder.withValue(ProductsColumns.NOMBRE_PRODUCTO, mProductName.getText().toString());
                builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, mProductDescription.getText().toString());
                builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, mCurrentPhotoPath);
                CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_PRODUCTO,price.convert());
                batchOperations.add(builder.build());
            } else
//            if      (mAction==PRODUCT_SAVE )
            {


                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.Products.withId(mItem));
                builder.withValue(ProductsColumns.NOMBRE_PRODUCTO, mProductName.getText().toString());
                builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, mProductDescription.getText().toString());
                builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, mCurrentPhotoPath);
                CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_PRODUCTO,price.convert());
                batchOperations.add(builder.build());

            }

            try {
//
//            getApplicationContext().getContentResolver().insert(
//                    DistributionProvider.Productos.withId(_id), cv);
                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }
            getActivity().onBackPressed();
        }
    }

    public boolean verification(){

        return true;
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                Log.e(LOG_TAG, "dialog:"+dialog+" item: "+ item);
//                boolean result=getContext().
//                        Utility.checkPermission(this);
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (items[item].equals("Choose from Library")) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);}

//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);//
//                    startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);


//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader" +id);
        switch (id) {
            case DETAIL_PRODUCT_LOADER:
                Log.e(LOG_TAG, "onCreateLoader-DETAIL_PRODUCT_LOADER");

                    // Now create and return a CursorLoader that will take care of
                    // creating a Cursor for the data being displayed.
                    return new CursorLoader(
                            getActivity(),
                            LogisticaProvider.Products.withId(mItem),
                            null,
                            null,
                            null,
                            null);



            case DEFAULT_DETAIL_PRODUCT_LOADER:
                Log.e(LOG_TAG, "onCreateLoader-DETAIL_PRODUCT_LOADER");
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.Products.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            case NAME_PRODUCT_LOADER:
                Log.e(LOG_TAG, "onCreateLoader-NAME_PRODUCT_LOADER");

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.

                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                String select = "((" + ProductsColumns.NOMBRE_PRODUCTO + " NOTNULL) AND ("
                        + ProductsColumns.NOMBRE_PRODUCTO+ " ='mate'))";
//                AND ("
//                        + Contacts.DISPLAY_NAME + " != '' ))";

                String proyection []= {ProductsColumns.NOMBRE_PRODUCTO, "sum("+ ProductsColumns.PRECIO_PRODUCTO
                        + " * " + ProductsColumns.PRECIO_PRODUCTO
                        +")"};
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.Products.CONTENT_URI,//uri
                        proyection,// PROYECTION
                        select,//sELECTION (WHERE)
                        null,// ARGUMENTS, STRING
                        null); // ORDEN


            default:
                Log.e(LOG_TAG, "onCreateLoader-Default");
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "onLoadFinished");
        if (data != null && data.moveToFirst()) {
        switch (loader.getId()) {
            case DETAIL_PRODUCT_LOADER:
                if (data != null && data.moveToFirst()) {
                    Log.e(LOG_TAG, "DETAIL_PRODUCT_LOADER data != null && data.moveToFirst() cantidad" + data.getCount());
                }
                break;
            case DEFAULT_DETAIL_PRODUCT_LOADER:
                if (data != null && data.moveToFirst()) {
                    Log.e(LOG_TAG, "DEFAULT_DETAIL_PRODUCT_LOADERdata != null && data.moveToFirst() cantidad" + data.getCount());
                }
                break;

            case NAME_PRODUCT_LOADER:
                if (data != null && data.moveToFirst()) {
                    Log.e(LOG_TAG, "NAME_PRODUCT_LOADERdata != null && data.moveToFirst() cantidad de mate" + data.getCount());
                    Log.e(LOG_TAG, "NAME_PRODUCT_LOADERdata != null && data.moveToFirst() Producto" + data.getString(data.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));

                    Log.e(LOG_TAG, "NAME_PRODUCT_LOADERdata != null && data.moveToFirst() suma" +Double.toString(data.getDouble(1)));
                }
        }
        mProductId.setText(Integer.toString(data.getInt(data.getColumnIndex(ProductsColumns._ID_PRODUCTO))));
        mProductName.setText(data.getString(data.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
        NumberFormat format = NumberFormat.getCurrencyInstance();

        mProductPrice.setText(format.format(data.getDouble(data.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO))));

        mProductDescription.setText(data.getString(data.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));

        mCurrentPhotoPath = data.getString(data.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO));
        Picasso.with(getContext())
                .load(mCurrentPhotoPath)
                .into(mImageProducto);

        if (appBarLayout != null) {
            {
                appBarLayout.setTitle(data.getString(data.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
            }
        }
    }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG, "onLoaderReset");

    }
}
