package com.nextnut.logistica.rest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.ProductDetailActivity;
import com.nextnut.logistica.R;
import com.nextnut.logistica.Util.BoolIntConverter;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaDataBase;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;
import com.nextnut.logistica.Util.CurrencyToDouble;

import java.text.NumberFormat;

//import com.nextnut.distribution.ProductsActivity;
//import com.nextnut.distribution.R;

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

public class OrderDetailCursorAdapter extends CursorRecyclerViewAdapter<OrderDetailCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;
    ViewHolder mVh;
    Boolean mfavoriteVisibe = true;
    final private ProductCursorAdapterOnClickHandler mClickHandler;

//    private View.OnClickListener listener;
    public OrderDetailCursorAdapter(Context context, Cursor cursor, View empltyView, ProductCursorAdapterOnClickHandler dh){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;
//        ((View)empltyView.getParent()).getId();
        Log.i("OrderDetailCursorAda:", "Constructor");

        Log.i("OrderDetailCursorAda:", "((View)empltyView.getParent()).getId()" + ((View)empltyView.getParent()).getId());
        Log.i("OrderDetailCursorAda:", "R.layout.activity_customorder_list" + R.layout.activity_customorder_list);
    }


    public void setFavoriteVisible(){
        this.mfavoriteVisibe=true;
    }

    public void resetFavoriteVisible(){
        this.mfavoriteVisibe=false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public Long mDetalleOrderId ;
        public Long mRefCustomer ;
        public Long mRefProduct ;
        public String mphotString;
        public ImageView mphotoProducto;
        public TextView mTextViewNombre;
        public TextView mTextViewPrecio;
        public TextView mTextcantidad;
        public TextView mTextToal;
        public TextView mTextViewDescition;
        public CheckBox mfavorito;



        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            Log.i("OrderDetailCursorAda:", "ViewHolder");
            mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
            mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
            mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
            mTextViewDescition = (TextView) view.findViewById(R.id.descriptionProducto);
            mTextcantidad = (TextView) view.findViewById(R.id.cantidad);
            mTextToal = (TextView) view.findViewById(R.id.total);
            mfavorito = (CheckBox) view.findViewById(R.id.favorito);
           mfavorito.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    mClickHandler.onFavorite(mDetalleOrderId, ViewHolder.this);

                    Log.i("OrderDetailCursorAda:", "mfavorito "+mfavorito.isChecked());

                }
            });
            mfavorito.setVisibility(mfavoriteVisibe ? View.VISIBLE : View.GONE);

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
            Log.i("onClick", "cursorID " + mDetalleOrderId);
            Log.i("onClick", "PhotoString " + mphotString);
            mClickHandler.onClick(mDetalleOrderId, this);
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
                        .inflate(R.layout.order_detail_item, parent, false);
                Log.i("OrderDetailCursorAda:", "onCreateViewHolder ");
//                itemView.setOnClickListener(mContext);
                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
                return vh;
            }


//    public void setOnClickListener(View.OnClickListener listener) {
//        this.listener = listener;
//    }




            @Override
            public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);

//
//                 /* 0 */       String proyection1[] = {LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.ID_CUSTOM_ORDER_DETAIL ,
//       /* 1 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_PRODUCT_CUSTOM_ORDER_DETAIL ,
//       /* 2 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.REF_CUSTOM_ORDER_CUSTOM_ORDER_DETAIL ,
//       /* 3 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.FAVORITE_CUSTOM_ORDER_DETAIL ,
//       /* 4 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRICE_CUSTOM_ORDER_DETAIL ,
//       /* 5 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.QUANTITY_CUSTOM_ORDER_DETAIL ,
//       /* 6 */                LogisticaDataBase.CUSTOM_ORDERS_DETAIL+"."+ CustomOrdersDetailColumns.PRODUCT_NAME_CUSTOM_ORDER_DETAIL ,
//       /* 7 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.IMAGEN_PRODUCTO ,
//       /* 8 */                LogisticaDataBase.PRODUCTS+"."+ ProductsColumns.DESCRIPCION_PRODUCTO ,

                Log.i("OrderDetailCursorAda:", "onBindViewHolder");

                viewHolder.mDetalleOrderId=cursor.getLong(0);
                viewHolder.mphotString=cursor.getString(7);
                Picasso.with(viewHolder.mphotoProducto.getContext())

                        .load(cursor.getString(cursor.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)))
                        .resize(96, 96)
                        .placeholder(R.drawable.art_clear)
                        .centerCrop()
                        .into(viewHolder.mphotoProducto);

                viewHolder.mTextViewNombre.setText(cursor.getString(6));
                NumberFormat format = NumberFormat.getCurrencyInstance();

                viewHolder.mTextViewPrecio.setText(format.format(cursor.getDouble(4)));
                viewHolder.mTextcantidad.setText(Double.toString(cursor.getDouble(5)));
                viewHolder.mTextToal.setText(format.format(
                        cursor.getDouble(4)
                        * cursor.getDouble(5)
                        ));
                viewHolder.mTextViewDescition.setText(cursor.getString(8));
                viewHolder.mfavorito.setChecked(new BoolIntConverter().intToBool(cursor.getInt(3)));

                viewHolder.mRefProduct =cursor.getLong(1) ;
                viewHolder.mRefCustomer =cursor.getLong(9) ;
                 }



    public static interface ProductCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
        void onFavorite(long id, ViewHolder vh);
    }

            @Override
            public void onItemDismiss ( int position){
                Log.i("TouchHelper:", "Adapter onItemDismiss ");
//                long cursorId = getItemId(position);
//                Cursor c = getCursor();
//                ContentValues cv = new ContentValues();
//                cv.put(ProductsColumns._ID_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
//                cv.put(ProductsColumns.DESCRIPCION_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
//                cv.put(ProductsColumns.IMAGEN_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
//                cv.put(ProductsColumns.PRECIO_PRODUCTO, c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
//
//                Intent intent = new Intent(mContext, ProductDetailActivity.class);
//                intent.putExtra("PRODUCT_MODIFICACION", true);
//                intent.putExtra("_ID_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns._ID_PRODUCTO)));
//                intent.putExtra("DESCRIPCION_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));
//                intent.putExtra("IMAGEN_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)));
//                intent.putExtra("PRECIO_PRODUCTO", c.getString(c.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO)));
//
//                mContext.startActivity(intent);

//        mContext.getContentResolver().delete(PlanetProvider.Planets.withId(cursorId),
//                null, null);
//        mContext.getContentResolver().insert(PlanetProvider.ArchivedPlanets.withId(cursorId),
//                cv);
//                notifyDataSetChanged();
//        notifyItemRemoved(position);
            }

    @Override
    public void onItemAcepted(int position) {
        Log.i("TouchHelper:", "onItemAcepted ");
    }


}