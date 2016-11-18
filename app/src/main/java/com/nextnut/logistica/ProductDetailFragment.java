package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Producto;
import com.nextnut.logistica.util.Constantes;
import com.nextnut.logistica.util.CurrencyToDouble;
import com.nextnut.logistica.util.DialogAlerta;
import com.nextnut.logistica.util.Imagenes;
import com.nextnut.logistica.util.NumberTextWatcher;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PRODUCT_KEY;
import static com.nextnut.logistica.util.Constantes.NODO_EMPRESA_PRODUCTOS;
import static com.nextnut.logistica.util.Imagenes.dimensiona;
import static com.nextnut.logistica.util.Imagenes.saveImageSelectedReturnPath;
import static com.nextnut.logistica.util.Imagenes.selectImage;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment
//        implements LoaderManager.LoaderCallbacks<Cursor>
{


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


    private static final String LOG_TAG = ProductDetailFragment.class.getSimpleName();
    private static final String DIALOG_FRAGMENT = "Dialog Fragment";

    private long mItem = 0;

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductPriceSpecial;
    private EditText mProductDescription;
    private ImageButton mImageProducto;
    public ProgressView spinner;
    private String mProductKey;

    String mCurrentPhotoPath = null;


    private int mAction;

    CollapsingToolbarLayout appBarLayout;

    private ValueEventListener mProductListener;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private String mUserKey;
    private Empresa mEmpresa;
    private String mEmpresaKey;
    private Perfil mPerfil;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = mStorage.getReferenceFromUrl(Constantes.STORAGE_REFERENCE);
        mPerfil=(Perfil) getArguments().getParcelable(EXTRA_PERFIL);
        mEmpresa= (Empresa) getArguments().getParcelable(EXTRA_EMPRESA);
        mEmpresaKey=  getArguments().getString( EXTRA_EMPRESA_KEY);
        // [END initialize_database_ref]


        // Get post key from intent
        mProductKey = getArguments().getString(EXTRA_PRODUCT_KEY);
        if (mProductKey != null) { // Si exite mProductKey es que estamos modificando un producto.
            // Initialize Database
//            mDatabase = FirebaseDatabase.getInstance().getReference();

//                    .child("empresa").child("producto").push().getKey();

        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = getArguments().getLong(ARG_ITEM_ID);
            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);


            if (appBarLayout != null) {
                if (mAction == PRODUCT_NEW) {
                    appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
                }
            }

        }
        if (getArguments().containsKey(PRODUCT_ACTION)) {
            mAction = getArguments().getInt(PRODUCT_ACTION);
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
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectImage(ProductDetailFragment.this);


//            }
//
//        });
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
                    // ya tenemos los datos que queremos modificar por lo tanto desconectamos el listener!
                    if (mProductListener != null) {
                        mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mProductKey).removeEventListener(mProductListener);
                        Log.i("producto", "onDataChange-removeEventListener ");

                    }
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

            mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).child(mProductKey).addValueEventListener(productListener);
            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
            mProductListener = productListener;


        } else {
            Log.i("producto", "onActivityCreated: mProductKey: Null");

        }

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // cuando sacamos una foto.
                bitmap = (Bitmap) data.getExtras().get(getString(R.string.data));

            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {// cuando leemos un archivo foto.

                mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getContext(), data);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (bitmap != null) {
                // Create a storage reference from our app
                if (mProductKey == null) {
                    mProductKey = mDatabase.child(ESQUEMA_EMPRESA_PRODUCTOS).push().getKey();
                }
                // Crear una referencia a la foto. (directorio Imagenes/mProductoKey
                StorageReference ImagenRef = mStorageRef.child("images/" + mProductKey);
                Log.i("subirFotoReturnUri", "onFailure: -spinner ON 11" +mProductKey );

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datosFoto = baos.toByteArray();
                spinner.setVisibility(View.VISIBLE);
                mImageProducto.setVisibility(View.GONE);
                UploadTask uploadTask = ImagenRef.putBytes(datosFoto);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        spinner.setVisibility(View.GONE);
                                                        mImageProducto.setVisibility(View.VISIBLE);
                                                        Log.i("subirFotoReturnUri", "onFailure: -spinner off " + exception.toString());

                                                    }
                                                }

                ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                               // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                               Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                               Log.i("subirFotoReturnUri", "onSuccess: Spinner Off");
                                               Drawable drawable = dimensiona(getContext(), R.drawable.ic_action_action_redeem);
//                                               mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getContext(), (Bitmap) data.getExtras().get(getString(R.string.data)));
                                               mCurrentPhotoPath = downloadUrl.toString();

                                               Picasso.with(getContext())
                                                       .load(mCurrentPhotoPath)
//                                                       .placeholder(drawable)
                                                       .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                                                       .into(mImageProducto);
                                               mImageProducto.setVisibility(View.VISIBLE);
                                               spinner.setVisibility(View.GONE);
                                           }
                                       }

                ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        spinner.setProgress((float) progress);
                        Log.i("subirFotoReturnUri", "spinner progress"+progress);

                        System.out.println("Upload is " + progress + "% done");
                    }
                });
            }
        }
    }


    public void deleteProduct() {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mAction == PRODUCT_SELECTION && mItem != 0) {

            getActivity().onBackPressed();
        }

    }

    public void verificationAndsave() {

        if (verification()) {
            fireBaseSaveProducto();

        }
    }


    /// Validations!!!!


    public boolean verification() {

        if (!verifyName(mProductName.getText().toString())) {
            return false;
        } else if (!isthePriceCorrect()) {

            return false;
        }
        return isDescriptionMaxLengthCorrect(mProductDescription.getText().toString());
    }


    public boolean verifyName(String name) {
        if (!isNameMaxLengthCorrect(name)) {
            return false;
        } else return !existeProductName(name);

    }

    public boolean existeProductName(String productName) {

//return true if the product exist or can not be saved because its a modification, else false
        if (productName.equals("")) {
            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametcantbenull));
            // Show DialogFragment
            mProductName.setError(getResources().getString(R.string.product_name_error_cantBeNull));

            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return true;
        }
        String select = "((" + ProductsColumns.NOMBRE_PRODUCTO + " NOTNULL) AND ("
                + ProductsColumns.NOMBRE_PRODUCTO + " =?))";
        String projection[] = {ProductsColumns.NOMBRE_PRODUCTO, ProductsColumns._ID_PRODUCTO};
        String arg[] = {productName};

        Cursor c = getActivity().getContentResolver().query(LogisticaProvider.Products.CONTENT_URI,
                null,
                select,
                arg,
                null);
        if (c == null || c.getCount() == 0) {
            return false;
        } else {
            c.moveToFirst();
            if (mAction == PRODUCT_NEW && c.getCount() != 0) {
                //its a new producto and the product name exist
                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametExist));
                // Show DialogFragment
                dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
                mProductName.setError(getResources().getString(R.string.product_name_error_yaexiste));

                mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return true;
            } else if (mAction == PRODUCT_SELECTION && c.getCount() >= 1 && c.getInt(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)) != mItem) {
                //its a Modification producto and the product name exist in other register
                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametExist));
                mProductName.setError(getResources().getString(R.string.product_name_error_yaexiste));
                // Show DialogFragment
                dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
                mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return true;

            } else {
                mProductName.setTextColor(getResources().getColor(R.color.ValidationOK));
                return false;
            }


        }
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

    public void fireBaseSaveProducto() {
        Log.i("producto", "fireBaseSaveProducto");

        CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
        CurrencyToDouble priceEspecial = new CurrencyToDouble(mProductPriceSpecial.getText().toString());
        Log.i("producto", "onSuccess: nombre " + mProductName.getText().toString());

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

            Producto producto = new Producto(uid,nombreProducto, precio, precioEspcecial, descripcionProducto, fotoProducto );
            Map<String, Object> productoValues = producto.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/empresa/" + key, empresaValues);
            childUpdates.put("/empresa-producto/" + mProductKey, productoValues);
            // Remove post value event listener
            if (mProductListener != null) {
                mDatabase.child(NODO_EMPRESA_PRODUCTOS).child(mEmpresaKey).child(mProductKey).removeEventListener(mProductListener);
                Log.i("producto", "removeEventListener");

            }
            mDatabase.updateChildren(childUpdates);
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("producto", "onStop");

        // Remove post value event listener
        if (mProductListener != null) {
            mDatabase.child("empresa-producto").child(mProductKey).removeEventListener(mProductListener);
        }

    }
}