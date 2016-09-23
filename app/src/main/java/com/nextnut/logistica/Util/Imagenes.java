package com.nextnut.logistica.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.nextnut.logistica.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by perez.juan.jose on 20/09/2016.
 */

public class Imagenes {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    public static final int REQUEST_IMAGE_GET = 1889;

    public static Drawable resize(Context context, int somedrawable) {

        // Read your drawable from somewhere

        Drawable dr =context.getResources(). getDrawable(somedrawable);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        Drawable d = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap
                ,context.getResources().getDimensionPixelSize(R.dimen.product_cardPhotoheigth)
                ,context.getResources().getDimensionPixelSize(R.dimen.product_cardPhotowidth)
                , true));
// Set your new, scaled drawable "d"

//        Bitmap b = ((BitmapDrawable)image).getBitmap();
//        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
//        return new BitmapDrawable(getResources(), bitmapResized);
        d.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN));
        return d;
    }


    public static void selectImage(final Fragment fragment) {
        final CharSequence[] items = {fragment.getResources().getString(R.string.TakePhoto),
                fragment.getResources().getString(R.string.ChoosefromLibrary),
                fragment.getResources().getString(R.string.Cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setTitle(fragment.getResources().getString(R.string.AddPhoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                Log.e("selectImage", "dialog:" + dialog + " item: " + item);

                if (items[item].equals(fragment.getResources().getString(R.string.TakePhoto))) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                   fragment. startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (items[item].equals(fragment.getResources().getString(R.string.ChoosefromLibrary))) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
                        fragment. startActivityForResult(intent, REQUEST_IMAGE_GET);
                    }


                } else if (items[item].equals(fragment.getResources().getString(R.string.Cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static String savePhotoReturnPath(Context context, Bitmap imagen){
        Bitmap bmp = imagen;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // convert byte array to Bitmap

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                byteArray.length);


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "Product_" + timeStamp;

        File file = new File(context.getFilesDir(), filename);

        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
//                    outputStream.write(string.getBytes());
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return file.getAbsolutePath();
        }

    }

    public static String saveImageSelectedReturnPath (Context context,  Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());


                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

//                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                        byteArray.length);


                String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String filename = "Product_" + timeStamp;

                File file = new File(context.getFilesDir(), filename);

                FileOutputStream outputStream;

                try {
                    outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
//                    outputStream.write(string.getBytes());
                    outputStream.write(byteArray);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return file.getAbsolutePath();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return null;
    }
}
