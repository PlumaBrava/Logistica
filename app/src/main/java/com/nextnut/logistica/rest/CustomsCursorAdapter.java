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
import com.nextnut.logistica.data.CustomColumns;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;

import static com.nextnut.logistica.util.Imagenes.resize;


public class CustomsCursorAdapter extends CursorRecyclerViewAdapter<CustomsCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;
    ViewHolder mVh;
    final private CustomsCursorAdapterOnClickHandler mClickHandler;

//    private View.OnClickListener listener;
    public CustomsCursorAdapter(Context context, Cursor cursor, View empltyView, CustomsCursorAdapterOnClickHandler dh){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public long mcursorId ;
        public String mphotString;


        public TextView mName;
        public TextView mSurename;
        public ImageView mphotoCustomer;
        public TextView mDeliveryAddress;
        public TextView mCity;


        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mphotoCustomer = (ImageView) view.findViewById(R.id.photocustom_listContent);
            mName = (TextView) view.findViewById(R.id.nameCustom_listContent);
            mSurename = (TextView) view.findViewById(R.id.surenameCustom_listContent);
            mDeliveryAddress = (TextView) view.findViewById(R.id.deliveryAddress_listContent);
            mCity = (TextView) view.findViewById(R.id.cityCustom_listContent);



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
                        .inflate(R.layout.custom_list_content, parent, false);
                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
                return vh;
            }



            @Override
            public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);
                viewHolder.mcursorId=cursor.getInt(cursor.getColumnIndex(CustomColumns.ID_CUSTOM));
                viewHolder.mphotString=cursor.getString(cursor.getColumnIndex(CustomColumns.IMAGEN_CUSTOM));
                Drawable drawable = resize(mContext, R.drawable.ic_action_image_timer_auto);
                Picasso.with(viewHolder.mphotoCustomer.getContext())

                        .load(viewHolder.mphotString)
                        .resize(mContext.getResources().getDimensionPixelSize(R.dimen.product_picture_w),mContext. getResources().getDimensionPixelSize(R.dimen.product_picture_h))
                        .placeholder(drawable)
                        .centerCrop()
                        .into(viewHolder.mphotoCustomer);

                viewHolder.mName.setText(cursor.getString(cursor.getColumnIndex(CustomColumns.NAME_CUSTOM)));
                viewHolder.mSurename.setText(cursor.getString(cursor.getColumnIndex(CustomColumns.LASTNAME_CUSTOM)));
                viewHolder.mDeliveryAddress.setText(cursor.getString(cursor.getColumnIndex(CustomColumns.DELIIVERY_ADDRES_CUSTOM)));
                viewHolder.mCity.setText(cursor.getString(cursor.getColumnIndex(CustomColumns.DELIVERY_CITY_CUSTOM)));


            }



    public interface CustomsCursorAdapterOnClickHandler {
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
