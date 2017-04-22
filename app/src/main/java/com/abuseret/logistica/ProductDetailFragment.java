package com.abuseret.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.abuseret.logistica.modelos.PerfilDePrecio;
import com.abuseret.logistica.modelos.Precio;
import com.abuseret.logistica.modelos.Producto;
import com.abuseret.logistica.util.CurrencyToDouble;
import com.abuseret.logistica.util.NumberTextWatcher;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;
import static com.abuseret.logistica.util.Constantes.IMAGENES_PRODUCTOS;
import static com.abuseret.logistica.util.Constantes.NODO_EMPRESA_PRODUCTOS;
import static com.abuseret.logistica.util.Imagenes.selectImage;

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
    private com.rey.material.widget.Spinner mRubro;
    private com.rey.material.widget.Spinner mTipoUnidad;
    private com.rey.material.widget.Button mBotonModificarPrecio;
    private EditText mCantidadMinima;
    private EditText mCantidadMaxima;
    private EditText mCantidadDefault;
    private EditText mProductDescription;
    private ImageButton mImageProducto;
    public ProgressView spinner;
    public ArrayAdapter<CharSequence> mAdapterTipoUnidad;
    public ArrayAdapter<CharSequence> mAdapterRubro;
    public ArrayAdapter<CharSequence> mAdapterPerfilDePrecios;
    private com.rey.material.widget.Spinner mPerfilDePrecios;
    List<CharSequence> mPerfilDePreciosList = new ArrayList<CharSequence>();
    private RecyclerView mListadePrecios;
    private Map<String, Precio> mPrecios = new HashMap<>();
    private MyAdapterPrecios mAdapterPrecios;


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

        mPerfilDePrecios = (Spinner) rootView.findViewById(R.id.productPerfildePrecios);

        mListadePrecios = (RecyclerView) rootView.findViewById(R.id.listaPrecios);
        // use a linear layout manager
        mListadePrecios.setHasFixedSize(true);
        mListadePrecios.setLayoutManager(new LinearLayoutManager(getContext()));

        refPerfilDePrecios().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    muestraMensajeEnDialogo("Configure el Prefil de Precios");
                } else {
                    for (DataSnapshot perfil : dataSnapshot.getChildren()) {
                        PerfilDePrecio perfilDePrecio = perfil.getValue(PerfilDePrecio.class);
                        mPerfilDePreciosList.add(perfilDePrecio.getPerfilDePrecio());
                    }
                    mAdapterPerfilDePrecios = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, mPerfilDePreciosList);

// Specify the layout to use when the list of choices appears
                    mAdapterPerfilDePrecios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                    mPerfilDePrecios.setAdapter(mAdapterPerfilDePrecios);
                    mPerfilDePrecios.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(Spinner parent, View view, int position, long id) {
                            Log.i("Producto", "parent " + parent.toString());
                            Log.i("Producto", "view: " + view.toString());
                            Log.i("Producto", "position: " + position);
                            Log.i("Producto", "id: " + id);
                            Log.i("Producto", "parent.getSelectedItem(): " + parent.getSelectedItem());
                            parent.getSelectedItem();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProductPrice = (EditText) rootView.findViewById(R.id.product_price);
        mProductPrice.addTextChangedListener(new NumberTextWatcher(mProductPrice));
        mProductPriceSpecial = (EditText) rootView.findViewById(R.id.product_pricespecial);
        mProductPriceSpecial.addTextChangedListener(new NumberTextWatcher(mProductPriceSpecial));
        mBotonModificarPrecio = (Button) rootView.findViewById(R.id.botonModificarPrecio);
        mBotonModificarPrecio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Double price = new CurrencyToDouble(mProductPrice.getText().toString()).convert();
                Double priceEspecial = new CurrencyToDouble(mProductPriceSpecial.getText().toString()).convert();
                Boolean error=false;
                if (mProductPrice.getText().toString().equals("") || price == 0.0) {
                    mProductPrice.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                    mProductPrice.setTextColor(getResources().getColor(R.color.ValidationERROR));
                    mProductPrice.requestFocus();
                    error=true;

                } else if (mProductPriceSpecial.getText().toString().equals("") || priceEspecial == 0.0) {
                    mProductPriceSpecial.setError(getResources().getString(R.string.price_error_mustBegraterthan0));
                    mProductPriceSpecial.setTextColor(getResources().getColor(R.color.ValidationERROR));
                    mProductPriceSpecial.requestFocus();
                    error=true;
                }
                if(error){
                    muestraMensajeEnDialogo(getResources().getString(R.string.priceError));
                    return;
                }

                Double[] t = new Double[2];


                t[0] = price;
                t[1] = priceEspecial;
                Log.i("Producto", "(String) mPerfilDePrecios.getSelectedItem(): " + (String) mPerfilDePrecios.getSelectedItem());

                mPrecios.put((String) mPerfilDePrecios.getSelectedItem(), new Precio(t[0], t[1]));
                mProductPrice.setText("");
                mProductPriceSpecial.setText("");
                mAdapterPrecios.swap(mPrecios);
            }
        });
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

        mRubro = (Spinner) rootView.findViewById(R.id.productRubro);
        mAdapterRubro = ArrayAdapter.createFromResource(getContext(),
                R.array.productRubro_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapterRubro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mRubro.setAdapter(mAdapterRubro);
        mRubro.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                Log.i("producto", "parent: " + parent.toString());
                Log.i("producto", "view: " + view.toString());
                Log.i("producto", "position: " + position);
                Log.i("producto", "id: " + id);
                Log.i("producto", "parent.getSelectedItem(): " + parent.getSelectedItem());
                parent.getSelectedItem();
            }
        });


        mTipoUnidad = (Spinner) rootView.findViewById(R.id.product_TipoUnidad);
        mAdapterTipoUnidad = ArrayAdapter.createFromResource(getContext(),
                R.array.productTipoUnidad_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapterTipoUnidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mTipoUnidad.setAdapter(mAdapterTipoUnidad);
        mTipoUnidad.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {

            }
        });


        mCantidadMinima = (EditText) rootView.findViewById(R.id.productMinCantidad);
        mCantidadMaxima = (EditText) rootView.findViewById(R.id.productMaxCantidad);
        mCantidadDefault = (EditText) rootView.findViewById(R.id.productCantidadDefault);


        spinner = (ProgressView) rootView.findViewById(R.id.progressBarProducto);
        spinner.setVisibility(View.GONE);


        mAdapterPrecios = new MyAdapterPrecios(mPrecios);
        mListadePrecios.setAdapter(mAdapterPrecios);

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

                    mPrecios = producto.getPrecios();
//                    mProductPrice.setText(format.format(producto.getPerfilDePrecio()));
//                    mProductPriceSpecial.setText(format.format(producto.getPrecioEspcecial()));

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

                    int spinnerPositionRubro = mAdapterRubro.getPosition(producto.getRubro());
                    mRubro.setSelection(spinnerPositionRubro);

                    int spinnerPositionTipoUnidad = mAdapterTipoUnidad.getPosition(producto.getTipoUnidad());
                    mTipoUnidad.setSelection(spinnerPositionTipoUnidad);

                    mCantidadMinima.setText(String.valueOf(producto.getCantidadMinima()));
                    mCantidadMaxima.setText(String.valueOf(producto.getCantidadMaxima()));
                    mCantidadDefault.setText(String.valueOf(producto.getCantidadDefault()));

                    mAdapterPrecios.swap(producto.getPrecios());

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
            mProductName.setError(getResources().getString(R.string.theproductNameToLong));
            mProductName.requestFocus();
            verification = false;
        }
        if (!isthePriceCorrect()) {
            verification = false;
        }
        if (!isDescriptionMaxLengthCorrect(mProductDescription.getText().toString())) {
            verification = false;

        }

        int c_min = 0;
        int c_def = 0;
        int c_max = 0;
        try {
            c_def = Integer.parseInt(String.valueOf(mCantidadDefault.getText().toString()));
        } catch (Exception e) {
            mCantidadDefault.setError(getResources().getString(R.string.error_CantidadDefault));
            mCantidadDefault.requestFocus();
            return false;
        }

        try {
            c_min = Integer.parseInt(String.valueOf(mCantidadMinima.getText().toString()));

        } catch (Exception e) {
            mCantidadMinima.setError(getResources().getString(R.string.error_CantidadMinima));
            mCantidadMinima.requestFocus();
            return false;
        }
        try {
            c_max = Integer.parseInt(String.valueOf(mCantidadMaxima.getText().toString()));
        } catch (Exception e) {
            mCantidadMaxima.setError(
                    String.format(
                            getResources().getString(R.string.error_CantidadMaxima), getResources().getInteger(R.integer.quantityMax)));
            mCantidadMaxima.requestFocus();
            return false;
        }

        if (c_min < getResources().getInteger(R.integer.quantityMin)) {
            mCantidadMinima.setError(getResources().getString(R.string.error_CantidadMinima));
            mCantidadMinima.requestFocus();
            verification = false;
        }

        if (c_def < c_min || c_def > c_max) {
            mCantidadDefault.setError(getResources().getString(R.string.error_CantidadDefault));
            mCantidadDefault.requestFocus();
            verification = false;
        }
        if (c_max > getResources().getInteger(R.integer.quantityMax)) {
            mCantidadMaxima.setError(getResources().getString(R.string.error_CantidadMaxima) + getResources().getInteger(R.integer.quantityMax));
            mCantidadMaxima.requestFocus();
            verification = false;
        }

        return verification;
    }


    public boolean verifyName(String name) {
        if (!isNameMaxLengthCorrect(name)) {
            return false;
        } else {
            return true;
        }

    }



    public void existeProductName(String productName) {

//return true if the product exist or can not be saved because its a modification, else false
        if (productName.equals("")) {
            muestraMensajeEnDialogo(getResources().getString(R.string.theproducNametcantbenull));
            // Show DialogFragment
            mProductName.setError(getResources().getString(R.string.product_name_error_cantBeNull));

            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return;
        }


        // Verifica que si existe ese usuario en NewUser
        Query myNewUsers = mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mEmpresaKey).orderByChild("nombreProducto");
        myNewUsers
                .startAt(mProductName.getText().toString())
                .endAt(mProductName.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "mProductName count" + dataSnapshot.getChildrenCount());
                        Log.d(LOG_TAG, "mProductName-KEY1: " + dataSnapshot.getKey());
                        if (dataSnapshot.getChildrenCount() == 0) {
                            fireBaseSaveProducto();
                        }//El producto no exisite en el listado

                        else if (dataSnapshot.getChildrenCount() > 0) {//El producto existe

                            Log.d(LOG_TAG, "mProductName- count" + dataSnapshot.getChildrenCount());
                            Log.d(LOG_TAG, "mProductName-KEY2: " + dataSnapshot.getKey());
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                Log.d(LOG_TAG, "mmProductName-KEY3:" + messageSnapshot.getKey());
                                if (messageSnapshot.getKey().equals(mProductKey)) {
                                    Log.d(LOG_TAG, "mmProductName-Es una modificacio de ProductKey:" + mProductKey);
                                    fireBaseSaveProducto();
                                    return;
                                }

                            }

                            muestraMensajeEnDialogo(getResources().getString(R.string.product_name_error_yaexiste));
                            // Show DialogFragment
                            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                            mProductName.setError(getResources().getString(R.string.product_name_error_yaexiste));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "Producto Cancelled " + databaseError.toString());

                    }
                });


    }

    public boolean isNameMaxLengthCorrect(String name) {
        if (name.length() > getResources().getInteger(R.integer.productoNameMaxLength)) {

            muestraMensajeEnDialogo(getResources().getString(R.string.theproductNameToLong));
            // Show DialogFragment
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
            muestraMensajeEnDialogo(getResources().getString(R.string.theproductdesccantbenull));
            // Show DialogFragment
            mProductDescription.setError(getResources().getString(R.string.product_name_error_cantBeNull));
            mProductDescription.requestFocus();
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return false;
        }

        if (desc.length() > getResources().getInteger(R.integer.productoNameMaxLength)) {

            muestraMensajeEnDialogo(getResources().getString(R.string.theproductdescToLong));
            // Show DialogFragment
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductDescription.setError(getResources().getString(R.string.product_name_error_tooLong));
            mProductDescription.requestFocus();
            return false;
        } else {
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationOK));
            return true;
        }
    }


    public boolean isthePriceCorrect() {
        Double price = null;
        if (mPrecios.size() == 0) {

            muestraMensajeEnDialogo(getResources().getString(R.string.priceErrorIngresePrecios));
            // Show DialogFragment
            mProductPrice.setTextColor(getResources().getColor(R.color.ValidationERROR));
            mProductPrice.setError(getResources().getString(R.string.price_error_cantBeNull));
            mProductPrice.requestFocus();

            return false;

        } else {

            return true;
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
                mUserKey,
                mRubro.getSelectedItem().toString(),
                mTipoUnidad.getSelectedItem().toString(),
//                mRubro.getText().toString(),
//                mTipoUnidad.getText().toString(),
                Integer.parseInt(mCantidadMinima.getText().toString()),
                Integer.parseInt(mCantidadMaxima.getText().toString()),
                Integer.parseInt(mCantidadDefault.getText().toString())
        );
    }


    // [START basic_write]
    private void writeNewProducto(String nombreProducto, Double precio, Double precioEspcecial, String descripcionProducto, String fotoProducto, String uid,
                                  String rubro,
                                  String tipoUnidad,
                                  int cantidadMinima,
                                  int cantidadMaxima,
                                  int cantidadDefault

    ) {
        if (true) {//validar formulario
            Log.i("producto", "writeNewProducto: nombre " + nombreProducto);
            Log.i("producto", "writeNewProducto: precio " + precio);
            Log.i("producto", "writeNewProducto: precio " + precio);
            Log.i("producto", "writeNewProducto: precioEspcecial " + precioEspcecial);
            Log.i("producto", "writeNewProducto: fotoProducto " + fotoProducto);
            Log.i("producto", "writeNewProducto: uid " + uid);
            Log.i("producto", "writeNewProducto: mProductKey " + mProductKey);

            Producto producto = new Producto(uid, nombreProducto, mPrecios, descripcionProducto, fotoProducto,
                    rubro,
                    tipoUnidad,
                    cantidadMinima,
                    cantidadMaxima,
                    cantidadDefault);

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

    public class MyAdapterPrecios extends RecyclerView.Adapter<MyAdapterPrecios.ViewHolder> {


        private ArrayList mData;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View mView;
            public TextView mProductoPerfil;
            public TextView mProductoprecio;
            public TextView mProductoPrecioEspecial;

            public ViewHolder(View v) {
                super(v);
                mView = v;
                mProductoPerfil = (TextView) v.findViewById(R.id.productoPrecioPerfil);
                mProductoprecio = (TextView) v.findViewById(R.id.productoprecioPrecio);
                mProductoPrecioEspecial = (TextView) v.findViewById(R.id.productoprecioPrecioEspecial);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapterPrecios(Map<String, Precio> map) {
            Log.i("preciosdapter", "MyAdapterPrecios map" + map.toString());
            TextView mProductoPerfil;
            TextView mProductoprecio;
            TextView mProductoPrecioEspecial;
            mData = new ArrayList();
            mData.addAll(map.entrySet());


        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("preciosdapter", "onCreateViewHolder " + viewType);

            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_precio_item, parent, false);
            // set the view's size, margins, paddings and layout parameters


            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Map.Entry<String, Precio> item = (Map.Entry) mData.get(position);
            final NumberFormat format = NumberFormat.getCurrencyInstance();
            final Precio t = (Precio) item.getValue();
            holder.mProductoPerfil.setText(item.getKey());
            holder.mProductoprecio.setText(format.format(t.getPrecio()));
            holder.mProductoPrecioEspecial.setText(format.format(t.getPrecioEspecial()));
            Log.i("preciosdapter", "getItem(position)" + position + " -+ " + item.getValue().toString());
//            holder.mTextView.setText(item.getKey()+" : "+format.format(t[0])+" - "+format.format(t[1]));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("preciosdapter", "gonClick- + " + item.getValue().toString());
                    int spinnerPerfil = mAdapterPerfilDePrecios.getPosition(item.getKey());
                    Log.i("preciosdapter", "item.getKey()- + " + item.getKey());
                    Log.i("preciosdapter", "spinnerPerfil- + " + spinnerPerfil);
                    mPerfilDePrecios.setSelection(spinnerPerfil);
                    Log.i("preciosdapter", "t[0].toString()- + " + t.getPrecio());
                    Log.i("preciosdapter", "t[1].toString()- + " + t.getPrecioEspecial());

                    mProductPrice.setText(format.format(t.getPrecio()));
                    mProductPriceSpecial.setText(format.format(t.getPrecioEspecial()));
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            Log.i("preciosdapter", "getItemCount()- + " + mData.size());
            return mData.size();
        }

        public void swap(Map<String, Precio> map) {
            Log.i("preciosdapter", "swap  map- + " + map.toString());
            mData.clear();
            mData.addAll(map.entrySet());
            notifyDataSetChanged();
        }
    }

}