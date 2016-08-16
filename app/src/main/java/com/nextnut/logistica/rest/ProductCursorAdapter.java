package com.nextnut.logistica.rest;

import android.animation.Animator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.nextnut.distribution.ProductsActivity;
//import com.nextnut.distribution.R;
import com.nextnut.logistica.ProductDetailActivity;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.swipe_helper.*;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

//import com.sam_chordas.android.schematicplanets.R;
//import com.sam_chordas.android.schematicplanets.data.ArchivedPlanetColumns;
//import com.sam_chordas.android.schematicplanets.data.PlanetColumns;
//import com.sam_chordas.android.schematicplanets.data.PlanetProvider;

//import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sam_chordas on 8/12/15.
 * Credit to skyfishjy gist:
 *    https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */

public class ProductCursorAdapter extends CursorRecyclerViewAdapter<ProductCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;
    ViewHolder mVh;
    final private ProductCursorAdapterOnClickHandler mClickHandler;

//    private View.OnClickListener listener;
    public ProductCursorAdapter(Context context, Cursor cursor,View empltyView,ProductCursorAdapterOnClickHandler dh){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public Long mcursorId ;
        public String mphotString;
        public ImageView mphotoProducto;
        public TextView mTextViewNombre;
        public TextView mTextViewPrecio;
        public TextView mTextViewPrecioEspecial;
        public TextView mTextViewDescition;


        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
            mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
            mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
            mTextViewPrecioEspecial = (TextView) view.findViewById(R.id.precioProductoSpecial);
            mTextViewDescition = (TextView) view.findViewById(R.id.descriptionProducto);


//            mImageview = (CircleImageView) view.findViewById(R.id.planet_image);
        }


        @Override
        public void onItemSelected() {
            Log.i("TouchHelper:", "Adapter onItemSelected(): ");
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {

            Log.i("TouchHelper:", "Adapter onItemClear(): ");
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View view) {

            Log.i("onClick", "onClick " + getPosition() + " " + getAdapterPosition());
            Log.i("onClick", "cursorID " + mcursorId);
            Log.i("onClick", "PhotoString " + mphotString);
            mClickHandler.onClick(mcursorId, this);
//            if(listener != null)
//                listener.onClick(view);
//            boolean isRed = isRed = !true;
//            final int radius = (int) Math.hypot(view.getWidth() / 2, view.getHeight() / 2);
//            view.setBackgroundColor(Color.RED);
//            if (isRed) {
//                view.setBackgroundColor(Color.GRAY);
//
//            } else {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    Animator anim = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, 0, radius);
//                    view.setBackgroundColor(Color.GREEN);
//                    anim.start();
//                }
//            }
        }
    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_item, parent, false);

//                itemView.setOnClickListener(mContext);
                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
                return vh;
            }


//    public void setOnClickListener(View.OnClickListener listener) {
//        this.listener = listener;
//    }


//        Log.i("TouchHelper:","Adapter onItemDismiss ");
////        long cursorId = getItemId(mVh.getChildPosition(view));
//        Cursor c = getCursor();
//        ContentValues cv = new ContentValues();
//        cv.put(ProductsColumns._ID_PRODUCTO,c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
//        cv.put(ProductsColumns.DESCRIPCION_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
//        cv.put(ProductsColumns.IMAGEN_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
//        cv.put(ProductsColumns.PRECIO_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
//
//        Intent intent=new Intent(mContext,ProductDetailActivity.class);
//        intent.putExtra("PRODUCT_MODIFICACION",true);
//        intent.putExtra("_ID_PRODUCTO",c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
//        intent.putExtra("DESCRIPCION_PRODUCTO",c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
//        intent.putExtra("IMAGEN_PRODUCTO",c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
//        intent.putExtra("PRECIO_PRODUCTO",c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
//
//        mContext.startActivity(intent);

//        mContext.getContentResolver().delete(PlanetProvider.Planets.withId(cursorId),
//                null, null);
//        mContext.getContentResolver().insert(PlanetProvider.ArchivedPlanets.withId(cursorId),
//                cv);

            @Override
            public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);
                viewHolder.mcursorId=cursor.getLong(cursor.getColumnIndex(ProductsColumns._ID_PRODUCTO));
                viewHolder.mphotString=cursor.getString(cursor.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO));
                Picasso.with(viewHolder.mphotoProducto.getContext())

                        .load(cursor.getString(cursor.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)))
                        .resize(96, 96)
                        .placeholder(R.drawable.art_clear)
                        .centerCrop()
                        .into(viewHolder.mphotoProducto);

                viewHolder.mTextViewNombre.setText(cursor.getString(cursor.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
                NumberFormat format = NumberFormat.getCurrencyInstance();

                viewHolder.mTextViewPrecio.setText(format.format(cursor.getDouble(cursor.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO))));
                viewHolder.mTextViewPrecioEspecial.setText(format.format(cursor.getDouble(cursor.getColumnIndex(ProductsColumns.PRECIO_SPECIAL_PRODUCTO))));
                viewHolder.mTextViewDescition.setText(cursor.getString(cursor.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));


            }

//        viewHolder.mImageview.setImageResource(cursor.getInt(cursor.getColumnIndex(
//                                PlanetColumns.IMAGE_RESOURCE)));


//    @Override
//    public void onClick(View v) {
////        int adapterPosition = getAdapterPosition();
//        mClickHandler.onClick( this);
////        mCursor.moveToPosition(adapterPosition);
//    }
//    public static interface ForecastAdapterOnClickHandler {
//        void onClick( ProductCursorAdapter vh);
//    }


    public static interface ProductCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
    }

            @Override
            public void onItemDismiss ( int position){
                Log.i("TouchHelper:", "Adapter onItemDismiss ");
                long cursorId = getItemId(position);
                Cursor c = getCursor();
                ContentValues cv = new ContentValues();
                cv.put(ProductsColumns._ID_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
                cv.put(ProductsColumns.DESCRIPCION_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
                cv.put(ProductsColumns.IMAGEN_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
                cv.put(ProductsColumns.PRECIO_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));

                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_MODIFICACION", true);
                intent.putExtra("_ID_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
                intent.putExtra("DESCRIPCION_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
                intent.putExtra("IMAGEN_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
                intent.putExtra("PRECIO_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));

                mContext.startActivity(intent);

//        mContext.getContentResolver().delete(PlanetProvider.Planets.withId(cursorId),
//                null, null);
//        mContext.getContentResolver().insert(PlanetProvider.ArchivedPlanets.withId(cursorId),
//                cv);
                notifyDataSetChanged();
//        notifyItemRemoved(position);
            }

    @Override
    public void onItemAcepted(int position) {

    }


}
