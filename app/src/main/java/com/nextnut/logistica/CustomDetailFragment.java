package com.nextnut.logistica;

import android.app.Activity;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.nextnut.logistica.modelos.Cliente;
import com.nextnut.logistica.util.CustomTextWatcher;
import com.nextnut.logistica.util.MakeCall;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Constantes.IMAGENES_CLIENTES;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_CLIENTES;
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.Imagenes.selectImage;
import static com.nextnut.logistica.util.MakeCall.getUserName;
import static com.nextnut.logistica.util.MakeCall.migrarTelefonosDelContactoAsociado;

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



    private TextView mCustomId;
    private EditText mCustomName;
    private EditText mLastName;
    private EditText mDeliveyAddress;
    private EditText mCity;
    private Button buttonContactoAgendado;
    private ImageView mImageCustomer;
    private EditText mCuit;
    private EditText mIva;
    private CheckBox mSpecial;
    public ProgressView spinner;
    private EditText mTipoTelefono;
    private EditText mTelefono;
    private com.rey.material.widget.Button mBotonAgregarTelefono;
    private com.rey.material.widget.Spinner mPerfilDePrecios;
    public ArrayAdapter<CharSequence> mAdapterPerfilDePrecios;
    private RecyclerView mListadeTelefonos;
    private Map<String, String> mTelefonos = new HashMap<>();
    private MyAdapter mAdapterTelefonos;


    CollapsingToolbarLayout appBarLayout;


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

        Log.d(LOG_TAG, "onCreate-mStorageRef:" + mStorageRef.toString());

        AppCompatActivity activity = (AppCompatActivity) this.getContext();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            if (mClienteKey ==null){
                appBarLayout.setTitle(getResources().getString(R.string.custom_new));
            }else
                appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text)+" " );
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_detail, container, false);
        mCustomName = (EditText) rootView.findViewById(R.id.custom_name_text);
        mLastName = (EditText) rootView.findViewById(R.id.product_Lastname);
        mPerfilDePrecios = (Spinner) rootView.findViewById(R.id.customPerfildePrecios);



        mAdapterPerfilDePrecios = ArrayAdapter.createFromResource(getContext(),
                R.array.perfiPrecios_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapterPerfilDePrecios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mPerfilDePrecios.setAdapter(mAdapterPerfilDePrecios);
        mPerfilDePrecios.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                Log.i("Custom", "parent: " + parent.toString());
                Log.i("Custom", "view: " + view.toString());
                Log.i("Custom", "position: " + position);
                Log.i("Custom", "id: " + id);
                Log.i("Custom", "parent.getSelectedItem(): " + parent.getSelectedItem());
                parent.getSelectedItem();
            }
        });




        buttonContactoAgendado = (Button) rootView.findViewById(R.id.contactoAgendado_button);
        buttonContactoAgendado.setOnClickListener(new View.OnClickListener() {
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
        spinner = (ProgressView) rootView.findViewById(R.id.progressBarCliente);
        spinner.setVisibility(View.GONE);
        if (mCurrentPhotoPath == null) {
            mImageCustomer.setBackgroundColor(Color.BLUE);
        } else {
            mImageCustomer.setBackgroundColor(Color.TRANSPARENT);
        }


        Picasso.with(getActivity())
                .load(mCurrentPhotoPath)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .into(mImageCustomer);

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
        mCuit.addTextChangedListener(new TextWatcher() {
                                        int length_before = 0;
                                         @Override
                                         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                             length_before = charSequence.length();
                                         }

                                         @Override
                                         public void onTextChanged(CharSequence s, int in, int i1, int i2) {
                                             Boolean modifyText =false;
                                             Log.d(LOG_TAG, "onTextChanged:" + s.toString()+"-"+in+"-"+i1+"-"+i2);
                                             StringBuilder b = new StringBuilder();
                                             for (int i = 0; i < s.length(); i++) {
                                                 if (s.charAt(i) == '\n') {
                                                     Log.d(LOG_TAG, "onTextChanged:" + s.charAt(i));
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
                                                     mCuit.setText(b.toString());
                                                 }

                                         }}

                                         @Override
                                         public void afterTextChanged(Editable s) {
                                             if (length_before < s.length()) {
                                                 if (s.length() == 2 || s.length() == 11)
                                                     s.append("-");
                                                 if (s.length() > 2) {
                                                     if (Character.isDigit(s.charAt(2)))
                                                         s.insert(2, "-");
                                                 }
                                                 if (s.length() > 11) {
                                                     if (Character.isDigit(s.charAt(11)))
                                                         s.insert(11, "-");
                                                 }
                                             }
                                         }
                                     });


                mIva = (EditText) rootView.findViewById(R.id.custom_iva);
        mSpecial = (CheckBox) rootView.findViewById(R.id.custom_special);

        mTipoTelefono=(EditText) rootView.findViewById(R.id.tipoTelefonoCliente);
        mTelefono=(EditText) rootView.findViewById(R.id.telefonoCliente);
        mBotonAgregarTelefono=(com.rey.material.widget.Button) rootView.findViewById(R.id.botonAgregarTelefono);
        mBotonAgregarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTelefonos.put(mTipoTelefono.getText().toString(),mTelefono.getText().toString());
                mTipoTelefono.setText("");
                mTelefono.setText("");
                mAdapterTelefonos.swap(mTelefonos);
            }
        });
        mListadeTelefonos=(RecyclerView) rootView.findViewById(R.id.listaTelefonos);
        // use a linear layout manager
        mListadeTelefonos.setHasFixedSize(true);
        mListadeTelefonos.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapterTelefonos = new MyAdapter(mTelefonos);



        mListadeTelefonos.setAdapter(mAdapterTelefonos);

        Log.i("TelefonosArrayAdapter", "mTelefonos .size() on create" + mTelefonos.entrySet().size());
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

                    mTelefonos.putAll(migrarTelefonosDelContactoAsociado(getActivity(),mIdContact));
                    mTipoTelefono.setText("");
                    mTelefono.setText("");
                    mAdapterTelefonos.swap(mTelefonos);
                buttonContactoAgendado.setText(name);
            }
//                if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//                    mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getContext(),(Bitmap) data.getExtras().get(getString(R.string.data)));
//
//                    Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
//                    Picasso.with(getContext())
//                            .load(mCurrentPhotoPath)
//                            .placeholder(drawable)
//                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                            .into(mImageCustomer);
//
//                } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {
//                    mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getContext(), data);
//                    mImageCustomer.setBackgroundColor(Color.TRANSPARENT);
//                    Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_image_timer_auto);
//                    Picasso.with(getContext())
//                            .load(mCurrentPhotoPath)
//                            .placeholder(drawable)
//                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
//                            .into(mImageCustomer);
//                }
//
            }
    }

    @Override
    public void savePhoto(Bitmap bitmap){
        if (mClienteKey == null) {
            mClienteKey = mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).push().getKey();
        }
        StorageReference ImagenRef = mStorageRef.child(IMAGENES_CLIENTES).child(mEmpresaKey).child(mClienteKey);
         uploadImagen(bitmap,ImagenRef,mImageCustomer,spinner);
        Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath"+ mCurrentPhotoPath);

    };


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
                        buttonContactoAgendado.setBackgroundColor(Color.GREEN);
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




            if (mClienteKey != null) {//Si mClienteKey existe leo los datos de Firebase y los muestro.
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
                        mCurrentPhotoPath =cliente.getFotoCliente();
                        // todo: reemplazar por un listado de mTelefonos.
                        mIdContact=null;// aqui deber ir la referencia al Id Android de contacto.

                        mCuit.setText(cliente.getCuit());
                        mIva.setText(Double.toString(cliente.getIva()));
                        mSpecial.setChecked(cliente.getEspecial());
                        Log.i("TelefonosArrayAdapter", "mTelefonos .size()antes" + mTelefonos.entrySet().size());

                        mTelefonos =cliente.getTelefonos();
                        Log.i("TelefonosArrayAdapter", "mTelefonos .size()" + mTelefonos.entrySet().size());
//                        mTelefonos.clear();
                        for(Map.Entry<String,String> entry : cliente.getTelefonos().entrySet()) {
                            mTelefonos.put(entry.getKey(),entry.getValue());
                            Log.i("TelefonosArrayAdapter", "mTelefonos .key" + entry.getKey());
                            Log.i("TelefonosArrayAdapter", "mTelefonos .value" + entry.getValue());
                        }
                        mAdapterTelefonos.swap(mTelefonos);
//                        mListadeTelefonos.invalidate();
//                        mAdapterTelefonos.notifyDataSetChanged();
//                        mAdapterTelefonos.notifyAll();

                        int spinnerPositionPerfil = mAdapterPerfilDePrecios.getPosition(cliente.getPerfilDePrecios());

                        mPerfilDePrecios.setSelection(spinnerPositionPerfil);

                        if (mIdContact != null){
                            buttonContactoAgendado.setBackgroundColor(Color.GREEN);
                            buttonContactoAgendado.setText(getUserName(getContext() ,mIdContact));
                        }

                        Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
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



            } else {
                Log.i("producto", "onActivityCreated: mProductKey: Null");

            }

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (appBarLayout != null) {
//            if (mItem==0){
//                appBarLayout.setTitle(getResources().getString(R.string.custom_new)+ mItem);
//            }else
//                appBarLayout.setTitle(getResources().getString(R.string.custom_Id_text)+" "+ mItem);
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
//            getActivity().onBackPressed();
        }
    // The data is not valid

    }

    public void fireBaseSaveCliente() {
        Log.i("producto", "fireBaseSaveProducto");



        writeNewCliente(mUserKey,
                mCustomName.getText().toString(),
                mLastName.getText().toString(),
                mIdContact,//todo ver este tema para pasar lo Nros de mTelefonos
                mCurrentPhotoPath,
                mDeliveyAddress.getText().toString(),
                mCity.getText().toString(),
                Double.parseDouble(mIva.getText().toString()),
                mCuit.getText().toString(),
                mSpecial.isChecked(),
                mTelefonos,
                mPerfilDePrecios.getSelectedItem().toString());
    }



    // [START basic_write]
    private void writeNewCliente(String uid,
            String nombre,
            String apellido,
            String telefono,
             String fotoCliente,
             String direccionDeEntrega,
             String ciudad,
             Double iva,
            String cuit,
            Boolean especial,Map<String, String> telefonos,
                                 String perfilDePrecios
                                 ){
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
            especial,telefonos,perfilDePrecios);

            if (mClienteKey == null) {
                mClienteKey = mDatabase.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).push().getKey();
            }

            Map<String, Object> clienteValues =  cliente.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(NODO_EMPRESA_CLIENTES + mEmpresaKey +"/"+ mClienteKey, clienteValues);

            mDatabase.updateChildren(childUpdates);
            getActivity().onBackPressed();
        }
    }

    public void deleteCustomer() {

        //TODO: Evaluar el delete del customer.

//        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//        if (mAction == CUSTOM_SELECTION && mItem != 0) {
//            getActivity().getContentResolver().delete(
//                    LogisticaProvider.Customs.withId(mItem), null, null);
//            getActivity().onBackPressed();
//        }

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
       if( mIva.getText().toString().equals("")) //todo: validar que el valor sea mayor que cero y menor a 100%
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
//            buttonContactoAgendado.setBackgroundColor(Color.GREEN);
//                buttonContactoAgendado.setText(getUserName(getContext() ,mIdContact));
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

    private class TelefonosArrayAdapter extends BaseAdapter {
        private final ArrayList mData;

        public TelefonosArrayAdapter(Map<String, String> map) {
            Log.i("TelefonosArrayAdapter", "constructor " + map.entrySet().size());

            mData = new ArrayList();
            mData.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            Log.i("TelefonosArrayAdapter", "getCount() " +mData.size());

            return mData.size();
        }

        @Override
        public Map.Entry<String, String> getItem(int position) {
            return (Map.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            Log.i("TelefonosArrayAdapter", "getItemId " +position);

            // TODO implement you own logic with ID
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                result = convertView;
            }

            final Map.Entry<String, String> item = getItem(position);
            Log.i("TelefonosArrayAdapter", "getItem(position)" +position+" -+ "+ item.getValue());

            // TODO replace findViewById by ViewHolder
            ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey()+" : "+item.getValue());
//            ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TelefonosArrayAdapter", "gonClick- + "+ item.getValue());
                    mTipoTelefono.setText(item.getKey());
                    mTelefono.setText(item.getValue());
                }
            });
            return result;
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        private  ArrayList mData;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public  class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Map<String, String> map) {

            mData = new ArrayList();
            mData.addAll(map.entrySet());

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            // set the view's size, margins, paddings and layout parameters


            ViewHolder vh = new ViewHolder((TextView) v);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Map.Entry<String, String> item = (Map.Entry) mData.get(position);
            Log.i("TelefonosArrayAdapter", "getItem(position)" +position+" -+ "+ item.getValue());
            holder.mTextView.setText(item.getKey()+" : "+item.getValue());
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TelefonosArrayAdapter", "gonClick- + "+ item.getValue());
                    mTipoTelefono.setText(item.getKey());
                    mTelefono.setText(item.getValue());
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
          return mData.size();
        }

        public void swap(Map<String, String> map){
            mData.clear();
            mData.addAll(map.entrySet());
            notifyDataSetChanged();
        }
    }

    }
