package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.util.CurrencyToDouble;
import com.nextnut.logistica.util.DialogAlerta;
import com.nextnut.logistica.util.Imagenes;
import com.nextnut.logistica.util.NumberTextWatcher;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.nextnut.logistica.util.Imagenes.resize;
import static com.nextnut.logistica.util.Imagenes.saveImageSelectedReturnPath;
import static com.nextnut.logistica.util.Imagenes.savePhotoReturnPath;
import static com.nextnut.logistica.util.Imagenes.selectImage;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
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
    private ImageView mImageProducto;

    String mCurrentPhotoPath = null;


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
        mImageProducto = ((ImageView) rootView.findViewById(R.id.product_imagen));
        Button button = ((Button) rootView.findViewById(R.id.product_imagen_button));
        // Show the dummy content as text in a TextView.


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(ProductDetailFragment.this);


            }

        });

        if (mAction == PRODUCT_NEW) {
            if (appBarLayout != null) {
                appBarLayout.setTitle(getResources().getString(R.string.pruductDetailBar_NEW_PRODUCT));
            }

        }


        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (mAction) {
            case PRODUCT_DOUBLE_SCREEN:
                if (mItem == 0) {
                    getLoaderManager().initLoader(DEFAULT_DETAIL_PRODUCT_LOADER, null, this);
                } else {
                    getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
                }
                break;
            case PRODUCT_SELECTION:
                getLoaderManager().initLoader(DETAIL_PRODUCT_LOADER, null, this);
                break;
            default:
                break;
        }

        super.onActivityCreated(savedInstanceState);
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Drawable drawable = resize(getContext(), R.drawable.ic_action_action_redeem);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Imagenes.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                mCurrentPhotoPath = getString(R.string.file) + savePhotoReturnPath(getContext(),(Bitmap) data.getExtras().get(getString(R.string.data)));
                Picasso.with(getContext())
                        .load(mCurrentPhotoPath)
                        .placeholder(drawable)
                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .into(mImageProducto);

            } else if (requestCode == Imagenes.REQUEST_IMAGE_GET) {


                mCurrentPhotoPath = getString(R.string.file) + saveImageSelectedReturnPath(getContext(),data);


                mImageProducto.setBackgroundColor(Color.TRANSPARENT);
                Picasso.with(getContext())
                        .load(mCurrentPhotoPath)
                        .placeholder(drawable)
                        .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .into(mImageProducto);

            }

        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_PRODUCT_LOADER:

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
                return new CursorLoader(
                        getActivity(),
                        LogisticaProvider.Products.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case DETAIL_PRODUCT_LOADER:
                    if (data != null && data.moveToFirst()) {
                    }
                    break;
                case DEFAULT_DETAIL_PRODUCT_LOADER:
                    if (data != null && data.moveToFirst()) {
                    }
                    break;


            }

            mProductName.setText(data.getString(data.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
            NumberFormat format = NumberFormat.getCurrencyInstance();

            mProductPrice.setText(format.format(data.getDouble(data.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO))));
            mProductPriceSpecial.setText(format.format(data.getDouble(data.getColumnIndex(ProductsColumns.PRECIO_SPECIAL_PRODUCTO))));

            mProductDescription.setText(data.getString(data.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));

            mCurrentPhotoPath = data.getString(data.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO));
            if (mCurrentPhotoPath==null){
                mImageProducto.setBackgroundColor(Color.BLUE);
            } else {
                mImageProducto.setBackgroundColor(Color.TRANSPARENT);
            }
            Picasso.with(getContext())
                    .load(mCurrentPhotoPath)
                    .placeholder(R.drawable.ic_action_action_redeem)
                    .resize(getResources().getDimensionPixelSize(R.dimen.product_picture_w), getResources().getDimensionPixelSize(R.dimen.product_picture_h))
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
    }


    public void deleteProduct() {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
        if (mAction == PRODUCT_SELECTION && mItem != 0) {

            getActivity().onBackPressed();
        }

    }

    public void verificationAndsave() {

        if (verification()) {
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
            if (mAction == PRODUCT_NEW && mItem == 0) {
                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Products.CONTENT_URI);
                builder.withValue(ProductsColumns.NOMBRE_PRODUCTO, mProductName.getText().toString());
                builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, mProductDescription.getText().toString());
                builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, mCurrentPhotoPath);
                CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_PRODUCTO, price.convert());
                CurrencyToDouble price1 = new CurrencyToDouble(mProductPriceSpecial.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_SPECIAL_PRODUCTO, price1.convert());
                batchOperations.add(builder.build());
            } else   {
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.Products.withId(mItem));
                builder.withValue(ProductsColumns.NOMBRE_PRODUCTO, mProductName.getText().toString());
                builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, mProductDescription.getText().toString());
                builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, mCurrentPhotoPath);
                CurrencyToDouble price = new CurrencyToDouble(mProductPrice.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_PRODUCTO, price.convert());
                CurrencyToDouble price1 = new CurrencyToDouble(mProductPriceSpecial.getText().toString());
                builder.withValue(ProductsColumns.PRECIO_SPECIAL_PRODUCTO, price1.convert());
                batchOperations.add(builder.build());

            }

            try {
                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
            }
            getActivity().onBackPressed();
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
                mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return true;
            } else if (mAction == PRODUCT_SELECTION && c.getCount() >= 1 && c.getInt(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)) != mItem) {
                //its a Modification producto and the product name exist in other register
                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproducNametExist));
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
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return false;
        }

        if (desc.length() > getResources().getInteger(R.integer.productoNameMaxLength)) {

            DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.theproductdescToLong));
            // Show DialogFragment
            dFragment.show(getFragmentManager(), DIALOG_FRAGMENT);
            mProductDescription.setTextColor(getResources().getColor(R.color.ValidationERROR));
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
            mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
            return false;

        } else {
            price = new CurrencyToDouble(mProductPrice.getText().toString()).convert();

            if (price == null) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else if (price <= 0) {

                DialogAlerta dFragment = DialogAlerta.newInstance(getResources().getString(R.string.priceError));
                // Show DialogFragment
                mProductName.setTextColor(getResources().getColor(R.color.ValidationERROR));
                return false;
            } else {
                mProductName.setTextColor(getResources().getColor(R.color.ValidationOK));
                return true;
            }
        }
    }

}
