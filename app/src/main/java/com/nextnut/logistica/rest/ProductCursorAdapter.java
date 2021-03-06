package com.nextnut.logistica.rest;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextnut.logistica.ProductDetailActivity;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.nextnut.logistica.util.Imagenes.resize;


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
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mcursorId, this);

        }
    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_item, parent, false);

                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
                return vh;
            }




            @Override
            public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);
                viewHolder.mcursorId=cursor.getLong(cursor.getColumnIndex(ProductsColumns._ID_PRODUCTO));
                viewHolder.mphotString=cursor.getString(cursor.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO));
                Drawable drawable = resize(mContext, R.drawable.ic_action_action_redeem);
                Picasso.with(viewHolder.mphotoProducto.getContext())
                        .load(cursor.getString(cursor.getColumnIndex(ProductsColumns.IMAGEN_PRODUCTO)))
                        .placeholder( drawable)
                        .resize(mContext.getResources().getDimensionPixelSize(R.dimen.product_cardPhotowidth), mContext.getResources().getDimensionPixelSize(R.dimen.product_cardPhotoheigth))
                        .centerCrop()
                        .into(viewHolder.mphotoProducto);

                viewHolder.mTextViewNombre.setText(cursor.getString(cursor.getColumnIndex(ProductsColumns.NOMBRE_PRODUCTO)));
                NumberFormat format = NumberFormat.getCurrencyInstance();

                viewHolder.mTextViewPrecio.setText(format.format(cursor.getDouble(cursor.getColumnIndex(ProductsColumns.PRECIO_PRODUCTO))));
                viewHolder.mTextViewPrecioEspecial.setText(format.format(cursor.getDouble(cursor.getColumnIndex(ProductsColumns.PRECIO_SPECIAL_PRODUCTO))));
                viewHolder.mTextViewDescition.setText(cursor.getString(cursor.getColumnIndex(ProductsColumns.DESCRIPCION_PRODUCTO)));


            }



    public interface ProductCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
    }

            @Override
            public void onItemDismiss ( int position){
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

                notifyDataSetChanged();
            }

    @Override
    public void onItemAcepted(int position) {

    }


}
