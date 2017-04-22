package com.abuseret.logistica;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.abuseret.logistica.modelos.Almacen;
import com.abuseret.logistica.util.CustomTextWatcher;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_ALMACENES;
import static com.abuseret.logistica.util.Constantes.EXTRA_ALMACEN_KEY;
import static com.abuseret.logistica.util.Constantes.IMAGENES_ALMACENES;
import static com.abuseret.logistica.util.Imagenes.dimensiona;
import static com.abuseret.logistica.util.Imagenes.selectImage;

/**
 * A fragment representing a single Custom detail screen.
 * This fragment is either contained in a {@link CustomListActivity}
 * in two-pane mode (on tablets) or a {@link CustomDetailActivity}
 * on handsets.
 */
public class AlmacenesDetailFragment extends FragmentBasic  {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */



    private EditText mNombreAlmacen;
    private EditText mCiudadAlmacen;
    private ImageView mImageAlmacen;
    private EditText mDireccion;
//    private EditText mCity;
//    private Button button;

    private EditText mResponsable;
    private EditText mTipoAlmacen;
    //    private CheckBox mSpecial;
    public ProgressView spinner;

    private EditText mTelefono;

    private String mAlmacenKey;


    CollapsingToolbarLayout appBarLayout;


    private static final String LOG_TAG = AlmacenesDetailFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlmacenesDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlmacenKey=getArguments().getString(EXTRA_ALMACEN_KEY);

        Log.d(LOG_TAG, "onCreate-mStorageRef:" + mStorageRef.toString());

        AppCompatActivity activity = (AppCompatActivity) this.getContext();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            if (this.mAlmacenKey ==null){
                appBarLayout.setTitle(getResources().getString(R.string.almacen_nuevo));
            }else
                appBarLayout.setTitle(getResources().getString(R.string.almacen_titulo)+" " );
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.almacen_detail, container, false);
        mNombreAlmacen = (EditText) rootView.findViewById(R.id.almacen_nombre);
        mDireccion = (EditText) rootView.findViewById(R.id.almacen_direccion);
//        button = (Button) rootView.findViewById(R.id.custom_imagen_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
//                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
//                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        mImageAlmacen = (ImageView) rootView.findViewById(R.id.almacen_foto);
        mImageAlmacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(AlmacenesDetailFragment.this);
            }
        });
        spinner = (ProgressView) rootView.findViewById(R.id.progressBarAlmacen);
        spinner.setVisibility(View.GONE);
        if (mCurrentPhotoPath == null) {
            mImageAlmacen.setBackgroundColor(Color.BLUE);
        } else {
            mImageAlmacen.setBackgroundColor(Color.TRANSPARENT);
        }


        Picasso.with(getActivity())
                .load(mCurrentPhotoPath)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .into(mImageAlmacen);

        mCiudadAlmacen = (EditText) rootView.findViewById(R.id.almacen_ciudad);
        mCiudadAlmacen.addTextChangedListener(new CustomTextWatcher(mCiudadAlmacen));


        mResponsable = (EditText) rootView.findViewById(R.id.almacen_responsable);
        mResponsable.addTextChangedListener(new CustomTextWatcher( mResponsable));


        mTelefono=(EditText) rootView.findViewById(R.id.almacen_telefono);
        mTipoAlmacen=(EditText) rootView.findViewById(R.id.almacen_tipo);

        return rootView;
    }


    @Override
    public void savePhoto(Bitmap bitmap){
        if (mAlmacenKey == null) {
            mAlmacenKey = mDatabase.child(ESQUEMA_ALMACENES).child(mEmpresaKey).push().getKey();
        }
        StorageReference ImagenRef = mStorageRef.child(IMAGENES_ALMACENES).child(mEmpresaKey).child( mAlmacenKey);
         uploadImagen(bitmap,ImagenRef, mImageAlmacen,spinner);
        Log.i("subirFotoReturnUri", "onSuccess: mCurrentPhotoPath"+ mCurrentPhotoPath);

    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MakeCall.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    if (mCustomId!=null){
//                        button.setBackgroundColor(Color.GREEN);
//                    }
//
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//
//
//                }
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {




            if (mAlmacenKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
                Log.i("producto", "onActivityCreated-mClienteKey: " + this.mAlmacenKey);
                Log.i("producto", "onActivityCreated:- mEmpresaKey;  " + mEmpresaKey);
                // Add value event listener to show the data.
                // [START post_value_event_listener]
                ValueEventListener almacenListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("producto", "onDataChange: ");
                        Log.i("producto", "onDataChange: count "+dataSnapshot.getChildrenCount());
                        // Get Post object and use the values to update the UI
                        Almacen almacen = (Almacen) dataSnapshot.getValue(Almacen.class);
                        // [START_EXCLUDE]
                        Log.i("producto", "onDataChange:cliente.getNombre() "+almacen.getNombre());

                        mNombreAlmacen.setText(almacen.getNombre());
                        mDireccion.setText(almacen.getDireccion());
                        mCiudadAlmacen.setText(almacen.getCiudad());
                        mCurrentPhotoPath =almacen.getLogo();

                        mResponsable.setText(almacen.getReponsable());
                        mTipoAlmacen.setText(almacen.getTipodeAlmacen());
                        mTelefono.setText(almacen.getTelefono());
//                        mAdapterTelefonos.notifyAll();

//                        if (mIdContact != null){
//                            button.setBackgroundColor(Color.GREEN);
//                            button.setText(getUserName(getContext() ,mIdContact));
//                        }

                        Drawable drawable = dimensiona(getContext(), R.drawable.com_facebook_profile_picture_blank_square);
                        Picasso.with(getContext())
                                .load(mCurrentPhotoPath)
                                .placeholder(drawable)
                                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                .into(mImageAlmacen);



                        if (appBarLayout != null) {
                            {
                                appBarLayout.setTitle(almacen.getNombre());
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
                mDatabase.child(ESQUEMA_ALMACENES).child(mEmpresaKey).child(mAlmacenKey).addListenerForSingleValueEvent(almacenListener);



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
        Log.i("almacenes", "verificationAndsave");

        if (verification()) {
            Log.i("almacenes", "paso la verificacion");

            fireBaseSaveAlmacen();

        }
    // The data is not valid

    }

    public void fireBaseSaveAlmacen() {

        Log.i("almacenes", "paso la verificacion");

        if (true) {//validar formulario

            if (mAlmacenKey == null) {
                Log.i("almacenes", "genera una key");

                mAlmacenKey = mDatabase.child(ESQUEMA_ALMACENES).child(mEmpresaKey).push().getKey();
            }

            Almacen almacen= new Almacen(mUserKey,
            mNombreAlmacen.getText().toString(),
            mResponsable.getText().toString(),
            mCiudadAlmacen.getText().toString(),
            mDireccion.getText().toString(),
            mTipoAlmacen.getText().toString(),
            mTelefono.getText().toString(),
            mCurrentPhotoPath,mAlmacenKey
           );



            Map<String, Object> almacenValues =  almacen.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(nodoAlmacen(mAlmacenKey), almacenValues);
//            childUpdates.put(NODO_ALMACENES + mEmpresaKey +"/"+ mClienteKey, almacenValues);
            Log.i("almacenes", "graba y retonra");

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


        if( mNombreAlmacen.getText().toString().equals(null))
        {
            Log.i("almacenes", "verificacion  mNombreAlmacen nulo");

            isvalid =false;
            mNombreAlmacen.setError(getResources().getString(R.string.Required));
            mNombreAlmacen.setBackgroundColor(Color.RED);
        } else {
            mNombreAlmacen.setBackgroundColor(Color.TRANSPARENT);
        }


        if( mDireccion.getText().toString().equals(""))
        {
            Log.i("almacenes", "verificacion  mDireccion nulo");

            isvalid =false;
            mDireccion.setBackgroundColor(Color.RED);
            mDireccion.setError(getResources().getString(R.string.Required));
        } else {
            mDireccion.setBackgroundColor(Color.TRANSPARENT);
        }
        if( mCiudadAlmacen.getText().toString().equals(""))
        {
            Log.i("almacenes", "verificacion  mCiudad nulo");

            isvalid =false;
            mCiudadAlmacen.setBackgroundColor(Color.RED);
            mCiudadAlmacen.setError(getResources().getString(R.string.Required));
        } else {
            mCiudadAlmacen.setBackgroundColor(Color.TRANSPARENT);
        }
//        if( mCity.getText().toString().equals(""))
//        {
//            isvalid =false;
//            mCity.setBackgroundColor(Color.RED);
//        } else {
//            mCity.setBackgroundColor(Color.TRANSPARENT);
//        }


        if( mResponsable.getText().toString().equals(""))
        {
            Log.i("almacenes", "verificacion  mResponsable nulo");

            isvalid =false;
            mResponsable.setBackgroundColor(Color.RED);
            mResponsable.setError(getResources().getString(R.string.Required));
        } else {
            mResponsable.setBackgroundColor(Color.TRANSPARENT);
        }
       if( mTipoAlmacen.getText().toString().equals(""))
       {
           Log.i("almacenes", "verificacion  mTipoAlmacen nulo");

           isvalid =false;
           mTipoAlmacen.setBackgroundColor(Color.RED);
           mTipoAlmacen.setError(getResources().getString(R.string.Required));
       } else {
           mTipoAlmacen.setBackgroundColor(Color.TRANSPARENT);
       }

//        mSpecial doesn´t need to be verified;

        return isvalid;
    }



    }
