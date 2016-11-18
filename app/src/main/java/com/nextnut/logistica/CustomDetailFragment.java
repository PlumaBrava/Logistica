package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.CustomTextWatcher;
import com.nextnut.logistica.util.Imagenes;
import com.nextnut.logistica.util.MakeCall;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.Imagenes.saveImageSelectedReturnPath;
import static com.nextnut.logistica.util.Imagenes.savePhotoReturnPath;
import static com.nextnut.logistica.util.Imagenes.selectImage;
import static com.nextnut.logistica.util.MakeCall.getUserName;

/**
 * A fragment representing a single Custom detail screen.
 * This fragment is either contained in a {@link CustomListActivity}
 * in two-pane mode (on tablets) or a {@link CustomDetailActivity}
 * on handsets.
 */
public class CustomDetailFragment extends FragmentBasic  {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    public static final String ARG_ITEM_ID = "item_id";
    public static final String CUSTOM_ACTION = "product_action";
    public static final int CUSTOM_NEW = 0;
    public static final int CUSTOM_DOUBLE_SCREEN = 1;
    public static final int CUSTOM_SAVE = 2;
    public static final int CUSTOM_SELECTION = 3;
    private static final int DETAIL_CUSTOM_LOADER = 0;
    private static final int DEFAULT_DETAIL_CUSTOM_LOADER = 1;


    String mCurrentPhotoPath = null;


    private TextView mCustomId;
    private EditText mCustomName;
    private EditText mLastName;
    private EditText mDeliveyAddress;
    private EditText mCity;
    private Button button;
    private ImageView mImageCustomer;
    private EditText mCuit;
    private EditText mIva;
    private CheckBox mSpecial;


    private int mAction;


    private ValueEventListener mCustomListener;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;


    CollapsingToolbarLayout appBarLayout;


    private long mItem = 0;

    private static final String LOG_TAG = CustomDetailFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mStorage = FirebaseStorage.getInstance();
        Log.d(LOG_TAG, "onCreate-msavedInstanceState:" + savedInstanceState);
        Log.d(LOG_TAG, "onCreate-mStorage:" + mStorage.toString());

        mStorageRef = mStorage.getReferenceFromUrl(Constantes.STORAGE_REFERENCE);
        Log.d(LOG_TAG, "onCreate-mStorageRef:" + mStorageRef.toString());

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = getArguments().getLong(ARG_ITEM_ID);
            AppCompatActivity activity = (AppCompatActivity) this.getContext();
             appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                if (mItem==0){
                    appBarLayout.setTitle(getResources().getString(R.string.custom_new)+ mItem);
                }else
                    appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text)+" "+ mItem);
            }
        }
        if (getArguments().containsKey(CUSTOM_ACTION)) {
            mAction = getArguments().getInt(CUSTOM_ACTION);
        }
        //            getIntent().getStringExtra(EXTRA_USER_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_detail, container, false);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mImageCustomer = (ImageView) rootView.findViewById(R.id.custom_imagen);
        mImageCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(CustomDetailFragment.this);
            }
        });
        mDeliveyAddress = (EditText) rootView.findViewById(R.id.custom_delivery_address);
        mCity = (EditText) rootView.findViewById(R.id.custom_city);

        mCustomName.addTextChangedListener(new TextWatcher() {
                                               public void afterTextChanged(Editable s) {
                                               }

                                               public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                               }

                                               public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                   Boolean modifyText = false;
                                                   StringBuilder b = new StringBuilder();
                                                   for (int i = 0; i < s.length(); i++) {
                                                       if (s.charAt(i) == '\n') {
                                                           modifyText = true;
                                                       } else {
                                                           b.append(s.charAt(i));
                                                       }
                                                       if (modifyText) {
                                                           // hide keyboard before calling the done action
                                                           InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                                                                   Context.INPUT_METHOD_SERVICE);
                                                           View view = getActivity().getCurrentFocus();
                                                           if (view != null) {
                                                               inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                                           }
//                                                           verifyName(b.toString());
                                                           mCustomName.setText(b.toString());
                                                       }
                                                   }
                                               }
                                           }
        );
        mLastName.addTextChangedListener(new CustomTextWatcher(mLastName));
        mDeliveyAddress.addTextChangedListener(new CustomTextWatcher( mDeliveyAddress));
        mCity.addTextChangedListener(new CustomTextWatcher(mCity));


        mCuit = (EditText) rootView.findViewById(R.id.custom_cuit);
        mIva = (EditText) rootView.findViewById(R.id.custom_iva);
        mSpecial = (CheckBox) rootView.findViewById(R.id.custom_special);
        return rootView;
    }

    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    String number;
    String mIdContact;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                // Check which request it is that we're responding to
                if (requestCode == PICK_CONTACT_REQUEST) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
               ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContext().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int column3 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
               number = cursor.getString(column1);
                String name = cursor.getString(column2);
                mIdContact = cursor.getString(column3);
                if (mCustomId!=null){
                    button.setBackgroundColor(Color.GREEN);

                }

                button.setText(name);
            }
                if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getContext(),(Bitmap) data.getExtras().get(getString(R.string.data)));

                    Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
                    Picasso.with(getContext())
                            .load(mCurrentPhotoPath)
                            .placeholder(drawable)
                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                            .into(mImageCustomer);

                } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {
                    mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getContext(), data);
                    mImageCustomer.setBackgroundColor(Color.TRANSPARENT);
                    Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
                    Picasso.with(getContext())
                            .load(mCurrentPhotoPath)
                            .placeholder(drawable)
                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                            .into(mImageCustomer);
                }

            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MakeCall.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (mCustomId!=null){
                        button.setBackgroundColor(Color.GREEN);
                    }


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }



                }
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {




            if (mClienteKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
                Log.i("producto", "onActivityCreated-mClienteKey: " + mClienteKey);
                Log.i("producto", "onActivityCreated:- mEmpresaKey;  " + mEmpresaKey);
                // Add value event listener to show the data.
                // [START post_value_event_listener]
                ValueEventListener customListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("producto", "onDataChange: ");
                        Log.i("producto", "onDataChange: count "+dataSnapshot.getChildrenCount());
                        // Get Post object and use the values to update the UI
                        Cliente cliente = (Cliente) dataSnapshot.getValue(Cliente.class);
                        // [START_EXCLUDE]
                        Log.i("producto", "onDataChange:cliente.getNombre() "+cliente.getNombre());

                        mCustomName.setText(cliente.getNombre());
                        mLastName.setText(cliente.getApellido());
                        mDeliveyAddress.setText(cliente.getDireccionDeEntrega());
                        mCity.setText(cliente.getCiudad());
                        mCurrentPhotoPath=cliente.getFotoCliente();
                        // todo: reemplazar por un listado de telefonos.
                        mIdContact=null;// aqui deber ir la referencia al Id Android de contacto.

                        mCuit.setText(cliente.getCuit());
                        mIva.setText(Long.toString(cliente.getIva()));
                        mSpecial.setChecked(cliente.getEspecial());


                        if (mIdContact != null){
                            button.setBackgroundColor(Color.GREEN);
                            button.setText(getUserName(getContext() ,mIdContact));
                        }

                        Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
                        Picasso.with(getContext())
                                .load(mCurrentPhotoPath)
                                .placeholder(drawable)
                                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                .into(mImageCustomer);



                        if (appBarLayout != null) {
                            {
                                appBarLayout.setTitle(cliente.getNombre()+" "+cliente.getApellido());
                            }
                        }
                        // ya tenemos los datos que queremos modificar por lo tanto desconectamos el listener!
//                        if ( mCustomListener != null) {
//                            mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).child(mClienteKey).removeEventListener(mCustomListener);
//                            Log.i("producto", "onDataChange-removeEventListener ");
//
//                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(getContext(), "Failed to load Products.",
                                Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]
                    }
                };
                Log.i(LOG_TAG ," mDatabase: "+ mDatabase);
                mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).child(mClienteKey).addListenerForSingleValueEvent(customListener);

                // [END post_value_event_listener]

                // Keep copy of post listener so we can remove it when app stops
//                mCustomListener = customListener;


            } else {
                Log.i("producto", "onActivityCreated: mProductKey: Null");

            }

            super.onActivityCreated(savedInstanceState);




//        switch (mAction) {
//            case CUSTOM_NEW:{
//                Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
//                Picasso.with(getContext())
//                        .load(mCurrentPhotoPath)
//                        .placeholder(drawable)
//                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                        .into(mImageCustomer);
//                break;
//            }
//
//            case CUSTOM_DOUBLE_SCREEN:
//                if (mItem == 0) {
//                    getLoaderManager().initLoader(DEFAULT_DETAIL_CUSTOM_LOADER, null, this);
//                } else {
//                    getLoaderManager().initLoader(DETAIL_CUSTOM_LOADER, null, this);
//                }
//                break;
//            case CUSTOM_SELECTION:
//                getLoaderManager().initLoader(DETAIL_CUSTOM_LOADER, null, this);
//                break;
//            default:
//                break;
//        }

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (appBarLayout != null) {
            if (mItem==0){
                appBarLayout.setTitle(getResources().getString(R.string.custom_new)+ mItem);
            }else
                appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text)+" "+ mItem);
        }
    }

    public void verificationAndsave() {

        if (verification()) {
            fireBaseSaveCliente();
//            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//            if (mAction == CUSTOM_NEW && mItem == 0) {
//
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Customs.CONTENT_URI);
//                builder.withValue(CustomColumns.NAME_CUSTOM, mCustomName.getText().toString());
//                builder.withValue(CustomColumns.LASTNAME_CUSTOM, mLastName.getText().toString());
//                builder.withValue(CustomColumns.DELIIVERY_ADDRES_CUSTOM, mDeliveyAddress.getText().toString());
//                builder.withValue(CustomColumns.DELIVERY_CITY_CUSTOM, mCity.getText().toString());
//                builder.withValue(CustomColumns.IMAGEN_CUSTOM, mCurrentPhotoPath);
//                builder.withValue(CustomColumns.REFERENCE_CUSTOM, mIdContact);
//                builder.withValue(CustomColumns.CUIT_CUSTOM, mCuit.getText().toString());
//                builder.withValue(CustomColumns.IVA_CUSTOM, mIva.getText().toString());
//                builder.withValue(CustomColumns.SPECIAL_CUSTOM,mSpecial.isChecked()?1:0);
//
//                batchOperations.add(builder.build());
//            } else
//            {
//                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.Customs.withId(mItem));
//                builder.withValue(CustomColumns.NAME_CUSTOM, mCustomName.getText().toString());
//                builder.withValue(CustomColumns.LASTNAME_CUSTOM, mLastName.getText().toString());
//                builder.withValue(CustomColumns.DELIIVERY_ADDRES_CUSTOM, mDeliveyAddress.getText().toString());
//                builder.withValue(CustomColumns.DELIVERY_CITY_CUSTOM, mCity.getText().toString());
//                builder.withValue(CustomColumns.IMAGEN_CUSTOM, mCurrentPhotoPath);
//                builder.withValue(CustomColumns.REFERENCE_CUSTOM, mIdContact);
//                builder.withValue(CustomColumns.CUIT_CUSTOM, mCuit.getText().toString());
//                builder.withValue(CustomColumns.IVA_CUSTOM, mIva.getText().toString());
//                builder.withValue(CustomColumns.SPECIAL_CUSTOM,mSpecial.isChecked()?1:0);
//                batchOperations.add(builder.build());
//
//            }
//
//            try {
//                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//            } catch (RemoteException | OperationApplicationException e) {
//            }
            getActivity().onBackPressed();
        }
    // The data is not valid

    }

    public void fireBaseSaveCliente() {
        Log.i("producto", "fireBaseSaveProducto");


        writeNewCliente(mUserKey,
                mCustomName.getText().toString(),
                mLastName.getText().toString(),
                mIdContact,//todo ver este tema para pasar lo Nros de telefonos
                mCurrentPhotoPath,
                mDeliveyAddress.getText().toString(),
                mCity.getText().toString(),
               21,// Long.getLong(mIva.getText().toString()),
                mCuit.getText().toString(),
                mSpecial.isChecked()
        );
    }

    // [START basic_write]
    private void writeNewCliente(String uid,
            String nombre,
            String apellido,
            String telefono,
             String fotoCliente,
             String direccionDeEntrega,
             String ciudad,
             long iva,
            String cuit,
            Boolean especial){
        if (true) {//validar formulario
            Log.i("producto", "writeNewClient: nombre " + nombre);
            Log.i("producto", "writeNewClient: apellido " + apellido);
            Log.i("producto", "writeNewClient: telefono " + telefono);
            Log.i("producto", "writeNewClient: fotoCliente " + fotoCliente);
            Log.i("producto", "writeNewClient: direccionDeEntrega " + direccionDeEntrega);
            Log.i("producto", "writeNewClient: ciudad " + ciudad);
            Log.i("producto", "writeNewClient: IVA " + iva);
            Log.i("producto", "writeNewClient: cUIT " + cuit);
            Log.i("producto", "writeNewClient: especial " + especial);


            Cliente cliente = new Cliente(uid,
            nombre,
            apellido,
            telefono,
            fotoCliente,
            direccionDeEntrega,
            ciudad,
            iva,
            cuit,
            especial);

            if (mClienteKey == null) {
                mClienteKey = mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).push().getKey();
            }

//                    uid,nombreProducto, precio, precioEspcecial, descripcionProducto, fotoProducto );
            Map<String, Object> productoValues =  cliente.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/empresa/" + key, empresaValues);
            childUpdates.put(NODO_EMPRESA_CLIENTES + mEmpresaKey +"/"+ mClienteKey, productoValues);
            // Remove post value event listener
            if (mCustomListener != null) {
                mDatabase.child(NODO_EMPRESA_CLIENTES).child(mEmpresaKey).child(mClienteKey).removeEventListener(mCustomListener);
                Log.i("producto", "removeEventListener");
            }
            mDatabase.updateChildren(childUpdates);
            getActivity().onBackPressed();
        }
    }

    public void deleteCustomer() {

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mAction == CUSTOM_SELECTION && mItem != 0) {
            getActivity().getContentResolver().delete(
                    LogisticaProvider.Customs.withId(mItem), null, null);
            getActivity().onBackPressed();
        }

    }

    public Boolean verification(){
        Boolean isvalid =true;
        if( mCustomName.getText().toString().equals(null))
        {
            isvalid =false;
            mCustomName.setBackgroundColor(Color.RED);
        } else {
            mCustomName.setBackgroundColor(Color.TRANSPARENT);
        }


        if( mLastName.getText().toString().equals(""))
        {
            isvalid =false;
            mLastName.setBackgroundColor(Color.RED);
        } else {
            mLastName.setBackgroundColor(Color.TRANSPARENT);
        }
        if( mDeliveyAddress.getText().toString().equals(""))
        {
            isvalid =false;
            mDeliveyAddress.setBackgroundColor(Color.RED);
        } else {
            mDeliveyAddress.setBackgroundColor(Color.TRANSPARENT);
        }
        if( mCity.getText().toString().equals(""))
        {
            isvalid =false;
            mCity.setBackgroundColor(Color.RED);
        } else {
            mCity.setBackgroundColor(Color.TRANSPARENT);
        }


        if( mCuit.getText().toString().equals(""))
        {
            isvalid =false;
            mCuit.setBackgroundColor(Color.RED);
        } else {
            mCuit.setBackgroundColor(Color.TRANSPARENT);
        }
       if( mIva.getText().toString().equals(""))
       {
           isvalid =false;
           mIva.setBackgroundColor(Color.RED);
       } else {
           mIva.setBackgroundColor(Color.TRANSPARENT);
       }

//        mSpecial doesn´t need to be verified;

        return isvalid;
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        switch (id) {
//            case DETAIL_CUSTOM_LOADER:
//
//                // Now create and return a CursorLoader that will take care of
//                // creating a Cursor for the data being displayed.
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.Customs.withId(mItem),
//                        null,
//                        null,
//                        null,
//                        null);
//
//
//            case DEFAULT_DETAIL_CUSTOM_LOADER:
//                return new CursorLoader(
//                        getActivity(),
//                        LogisticaProvider.Customs.CONTENT_URI,
//                        null,
//                        null,
//                        null,
//                        null);
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (data != null && data.moveToFirst()) {
//            switch (loader.getId()) {
//                case DETAIL_CUSTOM_LOADER:
//                    if (data != null && data.moveToFirst()) {
//                    }
//                    break;
//                case DEFAULT_DETAIL_CUSTOM_LOADER:
//                    if (data != null && data.moveToFirst()) {
//                    }
//                    break;
//
//
//            }
//            mCustomName.setText(data.getString(data.getColumnIndex(CustomColumns.NAME_CUSTOM)));
//            mLastName.setText(data.getString(data.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
//            mDeliveyAddress.setText(data.getString(data.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)));
//            mCity.setText(data.getString(data.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));
//            mCurrentPhotoPath=data.getString(data.getColumnIndex(CustomColumns.IMAGEN_CUSTOM));
//            mIdContact=data.getString(data.getColumnIndex(CustomColumns.REFERENCE_CUSTOM));
//            mCuit.setText(data.getString(data.getColumnIndex(CustomColumns.CUIT_CUSTOM)));
//            mIva.setText(data.getString(data.getColumnIndex(CustomColumns.IVA_CUSTOM)));
//            mSpecial.setChecked(data.getInt(data.getColumnIndex(CustomColumns.SPECIAL_CUSTOM)) > 0);
//
//
//            if (mIdContact != null){
//            button.setBackgroundColor(Color.GREEN);
//                button.setText(getUserName(getContext() ,mIdContact));
//            }
//
//            Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
//            Picasso.with(getContext())
//                    .load(mCurrentPhotoPath)
//                    .placeholder(drawable)
//                    .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                    .into(mImageCustomer);
//
//
//
//
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
}
