package com.nextnut.logistica;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.Util.NumberTextWatcher;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.dummy.DummyContent;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private String mItem;
    private TextView mProductId;
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductDescription;
    private ImageView mImageProducto;
    String mCurrentPhotoPath;
    private Button button;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;


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
            mItem = getArguments().getString(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("TITLE");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        mProductId = (TextView) rootView.findViewById(R.id.product_Id_text);
        mProductName = (EditText) rootView.findViewById(R.id.product_name_text);
        mProductPrice = (EditText) rootView.findViewById(R.id.product_price);
//        mProductPrice.addTextChangedListener(new NumberTextWatcher(mProductPrice, "#,###.##"));
        mProductDescription = (EditText) rootView.findViewById(R.id.product_description);
        mImageProducto = ((ImageView) rootView.findViewById(R.id.product_imagen));
        button = ((Button) rootView.findViewById(R.id.product_imagen_button));
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            mProductName.setText("name");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
//                        // Create the File where the photo should go
//                        File photoFile = null;
//                        try {
//                            photoFile = createImageFile();
//                        } catch (IOException ex) {
//                            // Error occurred while creating the File
////                            ...
//                        }
//
//
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                Uri.fromFile(photoFile));
                    startActivityForResult(intent,
                            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                }
//                }
            });


            mProductId.setText("ID:28");
//            ((TextView) rootView.findViewById(R.id.product_detail)).setText(mItem);
        }

        return rootView;
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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
//
//                 mImageProducto.setImageBitmap(bitmap);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String filename = "Product_"+timeStamp;
//                String filename = "myfile";
//                String string = "Hello world!";
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
                        .into(mImageProducto);

//                mProductId.setText(mCurrentPhotoPath);


            }
        }
    }

    public void verificationAndsave() {

    Log.i("prdDetFrament","save");
        if(verification()) {
            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);

            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(LogisticaProvider.Products.CONTENT_URI);
            builder.withValue(ProductsColumns.NOMBRE_PRODUCTO, mProductName.getText().toString());
            builder.withValue(ProductsColumns.DESCRIPCION_PRODUCTO, mProductDescription.getText().toString());
            builder.withValue(ProductsColumns.IMAGEN_PRODUCTO, mCurrentPhotoPath);
            builder.withValue(ProductsColumns.PRECIO_PRODUCTO, Double.parseDouble(mProductPrice.getText().toString()));
            batchOperations.add(builder.build());


            try {
//
//            getApplicationContext().getContentResolver().insert(
//                    DistributionProvider.Productos.withId(_id), cv);
                getContext().getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            }

        }
    }

    public boolean verification(){

        return true;
    }
}
