package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.util.CurrencyToDouble;
import com.nextnut.logistica.util.DialogAlerta;
import com.nextnut.logistica.util.NumberTextWatcher;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.IMAGENES_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Imagenes.selectImage;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends FragmentBasic
//        implements LoaderManager.LoaderCallbacks<Cursor>
{


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */


    public static final String PRODUCT_ACTION = "product_action";
    public static final int PRODUCT_NEW = 0;
    public static final int PRODUCT_DOUBLE_SCREEN = 1;
    public static final int PRODUCT_SAVE = 2;
    public static final int PRODUCT_SELECTION = 3;
    private static final int DETAIL_PRODUCT_LOADER = 0;
    private static final int DEFAULT_DETAIL_PRODUCT_LOADER = 1;


    private static final String LOG_TAG = ProductDetailFragment.class.getSimpleName();
    private static final String DIALOG_FRAGMENT = "Dialog Fragment";


    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductPriceSpecial;
    private EditText mProductDescription;
    private ImageButton mImageProducto;
    public ProgressView spinner;




//    String mCurrentPhotoPath = null;


    private int mAction;

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


        Activity activity = this.getActivity();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

        if (getArguments().containsKey(PRODUCT_ACTION)) {
            mAction = getArguments().getInt(PRODUCT_ACTION);
        }
        if (appBarLayout != null) {
            if (mAction == PRODUCT_NEW) {
                appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail, container, false);


        mProductName = (EditText) rootView.findViewById(R.id.product_name_text);
        mProductName.addTextChangedListener(new TextWatcher() {
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
                                                            verifyName(b.toString());

                                                            mProductName.setText(b.toString());
                                                        }
                                                    }
                                                }
                                            }
        );


        mProductPrice = (EditText) rootView.findViewById(R.id.product_price);
        mProductPrice.addTextChangedListener(new NumberTextWatcher(mProductPrice));
        mProductPriceSpecial = (EditText) rootView.findViewById(R.id.product_pricespecial);
        mProductPriceSpecial.addTextChangedListener(new NumberTextWatcher(mProductPriceSpecial));
        mProductDescription = (EditText) rootView.findViewById(R.id.product_description);
        mProductDescription.addTextChangedListener(new TextWatcher() {
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
                }
                if (modifyText) {
                    // hide keyboard before calling the done action
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    isDescriptionMaxLengthCorrect(b.toString());
                    mProductDescription.setText(b.toString());
                }
            }
        });
        mImageProducto = ((ImageButton) rootView.findViewById(R.id.product_imagen));
        if (mCurrentPhotoPath == null) {
            mImageProducto.setBackgroundColor(Color.BLUE);
        } else {
            mImageProducto.setBackgroundColor(Color.TRANSPARENT);
        }


        Picasso.with(getActivity())
                .load(mCurrentPhotoPath)
                .placeholder(R.drawable.ic_action_action_redeem)
                .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                .into(mImageProducto);

//        Button button = ((Button) rootView.findViewById(R.id.product_imagen_button));
        // Show the dummy content as text in a TextView.

        mImageProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(ProductDetailFragment.this);


            }

        });



        spinner = (ProgressView) rootView.findViewById(R.id.progressBarProducto);
        spinner.setVisibility(View.GONE);




        if (mAction == PRODUCT_NEW) {
            if (appBarLayout != null) {
                appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
            }

        }


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        if (mProductKey != null) {//Si mProductKey existe leo los datos de Firebase y los muestro.
            Log.i("producto", "onActivityCreated: " + mProductKey);
            // Add value event listener to show the data.
            // [START post_value_event_listener]
            ValueEventListener productListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("producto", "onDataChange: ");
                    // Get Post object and use the values to update the UI
                    Producto producto = dataSnapshot.getValue(Producto.class);
                    // [START_EXCLUDE]
                    mProductName.setText(producto.getNombreProducto());
                    NumberFormat format = NumberFormat.getCurrencyInstance();

                    mProductPrice.setText(format.format(producto.getPrecio()));
                    mProductPriceSpecial.setText(format.format(producto.getPrecioEspcecial()));

                    mProductDescription.setText(producto.getDescripcionProducto());

                    mCurrentPhotoPath = producto.getFotoProducto();
                    if (mCurrentPhotoPath == null) {
                        mImageProducto.setBackgroundColor(Color.BLUE);

                    } else {
                        mImageProducto.setBackgroundColor(Color.TRANSPARENT);
                    }

                    Log.i("producto", "onDataChange-mCurrentPhotoPath: " + mCurrentPhotoPath);

                    Picasso.with(getActivity())
                            .load(mCurrentPhotoPath)
                            .placeholder(R.drawable.ic_action_action_redeem)
                            .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                            .into(mImageProducto);

                    if (appBarLayout != null) {
                        {
                            appBarLayout.setTitle(producto.getNombreProducto());
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

            mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).child(mProductKey).addListenerForSingleValueEvent(productListener);
            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
//            mProductListener = productListener;


        } else {
            Log.i("producto", "onActivityCreated: mProductKey: Null");

        }

        super.onActivityCreated(savedInstanceState);
    }


    public void deleteProduct() {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//        if (mAction == PRODUCT_SELECTION && mItem != 0) {
//
//            getActivity().onBackPressed();
//        }

    }

    public void verificationAndsave() {

        if (verificationFromulario()) {
            existeProductName(mProductName.getText().toString());// Desde aqui se llama a SaveFirebase

        }
    }


    /// Validations!!!!


    public boolean verificationFromulario() {
        boolean verification = true;
        if (!verifyName(mProductName.getText().toString())) {
            verification = false;
        }
        if (!isthePriceCorrect()) {
            verification = false;
        }
        if (!istheSpecialPriceCorrect()) {
            verification = false;
        }
        if (!isDescriptionMaxLengthCorrect(mProductDescription.getText().toString())) {
            verification = false;

        }
        return verification;
    }


    public boolean verifyName(String name) {
        if (!isNameMaxLengthCorrect(name)) {
            return false;
        } else {return true;}

    }

    public void existeProductName(String productName) {

//return true if the product exist or can not be saved because its a modification, else false
        if (productName.equals("")) {
            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametcantbenull));
            // Show DialogFragment
            mProductName.setError(getResources().getString(R.string.product_name_error_cantBeNull));

            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return ;
        }


        // Verifica que si existe ese usuario en NewUser
        Query myNewUsers= mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).orderByChild("nombreProducto");
        myNewUsers
                .startAt(mProductName.getText().toString())
                .endAt(mProductName.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "mProductName count"+ dataSnapshot.getChildrenCount());
                        Log.d(LOG_TAG, "mProductName-KEY1: "+ dataSnapshot.getKey());
                        if (dataSnapshot.getChildrenCount()==0){ fireBaseSaveProducto();}//El producto no exisite en el listado

                        else if(dataSnapshot.getChildrenCount()>0){//El producto existe

                            Log.d(LOG_TAG, "mProductName- count"+ dataSnapshot.getChildrenCount());
                            Log.d(LOG_TAG, "mProductName-KEY2: "+ dataSnapshot.getKey());
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                Log.d(LOG_TAG, "mmProductName-KEY3:"+ messageSnapshot.getKey());
                                if (messageSnapshot.getKey().equals(mProductKey)){
                                    Log.d(LOG_TAG, "mmProductName-Es una modificacio de ProductKey:"+ mProductKey);
                                    fireBaseSaveProducto();
                                    return;
                                }

                            }

                            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.product_name_error_yaexiste));
                            // Show DialogFragment
                            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                            mProductName.setError(getResources().getString(R.string.product_name_error_yaexiste));
                            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "Producto Cancelled "+ databaseError.toString());

                    }
                });




    }

    public boolean isNameMaxLengthCorrect(String name) {
        if (name.length() > getResources().getInteger(R.integer.productoNameMaxLength)) {

            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproductNameToLong));
            // Show DialogFragment
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductName.setError(getResources().getString(R.string.product_name_error_tooLong));
            return false;
        } else {
            mProductName.setTextColor(getResources().getColor(R.color.ValidationOK));
            return true;
        }
    }

    public boolean isDescriptionMaxLengthCorrect(String desc) {
        if (desc.equals("")) {
            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproductdesccantbenull));
            // Show DialogFragment
            mProductDescription.setError(getResources().getString(R.string.product_name_error_cantBeNull));
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return false;
        }

        if (desc.length() > getResources().getInteger(R.integer.productoNameMaxLength)) {

            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproductdescToLong));
            // Show DialogFragment
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductDescription.setError(getResources().getString(R.string.product_name_error_tooLong));
            return false;
        } else {
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationOK));
            return true;
        }
    }


    public boolean isthePriceCorrect() {
        Double price = null;
        if (mProductPrice.getText().toString() == null) {

            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
            // Show DialogFragment
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductPrice.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductPrice.setError(getResources().getString(R.string.price_error_cantBeNull));
            return false;

        } else {
            price = new CurrencyToDouble(mProductPrice.getText().toString()).convert();

            if (price == null) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductPrice.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                mProductPrice.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else if (price <= 0) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductPrice.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                mProductPrice.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else {

                mProductPrice.setTextColor(getResources().getColor(R.color.ValidationOK));
                return true;
            }
        }
    }

    public boolean istheSpecialPriceCorrect() {
        Double specialPrice = null;
        if (mProductPriceSpecial.getText().toString() == null) {

            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
            // Show DialogFragment
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductPriceSpecial.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductPriceSpecial.setError(getResources().getString(R.string.price_error_cantBeNull));
            return false;

        } else {
            specialPrice = new CurrencyToDouble(mProductPriceSpecial.getText().toString()).convert();

            if (specialPrice == null) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductPriceSpecial.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                mProductPriceSpecial.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else if (specialPrice <= 0) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductPriceSpecial.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                mProductPriceSpecial.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else {

                mProductPriceSpecial.setTextColor(getResources().getColor(R.color.ValidationOK));
                return true;
            }
        }
    }

    public void fireBaseSaveProducto() {
        Log.i("producto", "fireBaseSaveProducto");

        CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
        CurrencyToDouble priceEspecial = new CurrencyToDouble(mProductPriceSpecial.getText().toString());
        Log.i("producto", "onSuccess: nombre " + mProductName.getText().toString());
        Log.i("producto", "mCurrentPhotoPath " + mCurrentPhotoPath);

        writeNewProducto(mProductName.getText().toString(),
                price.convert(),
                priceEspecial.convert(),
                mProductDescription.getText().toString(),
                mCurrentPhotoPath,
                mUserKey
        );
    }


    // [START basic_write]
    private void writeNewProducto(String nombreProducto, Double precio, Double precioEspcecial, String descripcionProducto, String fotoProducto, String uid) {
        if (true) {//validar formulario
            Log.i("producto", "writeNewProducto: nombre " + nombreProducto);
            Log.i("producto", "writeNewProducto: precio " + precio);
            Log.i("producto", "writeNewProducto: precio " + precio);
            Log.i("producto", "writeNewProducto: precioEspcecial " + precioEspcecial);
            Log.i("producto", "writeNewProducto: fotoProducto " + fotoProducto);
            Log.i("producto", "writeNewProducto: uid " + uid);
            Log.i("producto", "writeNewProducto: mProductKey " + mProductKey);

            Producto producto = new Producto(uid, nombreProducto, precio, precioEspcecial, descripcionProducto, fotoProducto);
            Map<String, Object> productoValues = producto.toMap();
            Map<String, Object> childUpdates = new HashMap<>();


            if (mProductKey == null) { // Si exite mProductKey es que estamos modificando un producto.
                mProductKey = mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).push().getKey();
            }
            childUpdates.put(NODO_EMPRESA_PRODUCTOS + mEmpresaKey + "/" + mProductKey, productoValues);

            mDatabase.updateChildren(childUpdates);
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("producto", "onStop");


    }

    @Override
    public void savePhoto(Bitmap bitmap) {
        if (mProductKey == null) {
            mProductKey = mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).push().getKey();
        }
        StorageReference ImagenRef = mStorageRef.child(IMAGENES_PRODUCTOS).child(mEmpresaKey).child(mProductKey);
        uploadImagen(bitmap, ImagenRef, mImageProducto, spinner);
        Log.i("subirFotoReturnUri", "onSuccess: photoPath[0]" + mCurrentPhotoPath);


    }

    ;

}