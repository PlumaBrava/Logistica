package com.nextnut.logistica.rest;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.nextnut.logistica.R;
import com.nextnut.logistica.Util.BoolIntConverter;
import com.nextnut.logistica.Util.CurrencyToDouble;
import com.nextnut.logistica.data.CustomOrdersDetailColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.data.ProductsColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.nextnut.logistica.Util.Imagenes.resize;

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

public class PickingOrderProductsAdapter extends CursorRecyclerViewAdapter<PickingOrderProductsAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    public static final int COLUMN_ID_PRODUCTO =0;
    public static final int COLUMN_NOMBRE_PRODUCTO =1;
    public static final int COLUMN_IMAGEN_PRODUCTO =2;
    public static final int COLUMN_DESCRIPCION_PRODUCTO =3;
    public static final int COLUMN_QTOTAL_ORDENES =4;
    public static final int COLUMN_ID_PICKING =5;
    public static final int COLUMN_QTOTAL_PICKING =6;
    public static final int COLUMN_QTOTAL_DELIVERY =7;
    public static final int COLUMN_OORDERS_COUNT =8;


    Context mContext;
    ViewHolder mVh;
//    Boolean mfavoriteVisibe = true;
    final private ProductCursorAdapterOnClickHandler mClickHandler;

//    private View.OnClickListener listener;
    public PickingOrderProductsAdapter(Context context, Cursor cursor, View empltyView, ProductCursorAdapterOnClickHandler dh){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;
//        ((View)empltyView.getParent()).getId();
        Log.i("OrderDetailCursorAda:", "Constructor");

        Log.i("OrderDetailCursorAda:", "((View)empltyView.getParent()).getId()" + ((View)empltyView.getParent()).getId());
        Log.i("OrderDetailCursorAda:", "R.layout.activity_customorder_list" + R.layout.activity_customorder_list);
    }




    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public Long mDetalleOrderId ;
        public Long mRefCustomer ;
        public Long mRefProduct ;
        public Long mRefPickingDetail ;
        public String mphotString;
        public ImageView mphotoProducto;
        public TextView mTextViewNombre;
        public TextView mTextViewPrecio;
        public TextView mTextcantidad;
        public TextView mTextcantidadPicking;
        public TextView mTextcantidadDelivey;
//        public TextView mTextViewDescition;
//        public CheckBox mfavorito;



        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            Log.i("OrderDetailCursorAda:", "ViewHolder");
            mphotoProducto = (ImageView) view.findViewById(R.id.photoProducto);
            mTextViewNombre = (TextView) view.findViewById(R.id.nombreProducto);
            mTextViewPrecio = (TextView) view.findViewById(R.id.precioProducto);
//            mTextViewDescition = (TextView) view.findViewById(R.id.descriptionProducto);
            mTextcantidad = (TextView) view.findViewById(R.id.cantidad);
           mTextcantidadPicking = (TextView) view.findViewById(R.id.cantidadPicking);
            mTextcantidadDelivey = (TextView) view.findViewById(R.id.cantidadTOTALDelivery);


        }

        @Override
        public void onItemSelected() {
            Log.i("TouchHelper:", "Adapter onItemSelected(): ");
//            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {

            Log.i("TouchHelper:", "Adapter onItemClear(): ");
//            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View view) {

            Log.i("onClick", "onClick " + getPosition() + " " + getAdapterPosition());
            Log.i("onClick", "cursorID " + mDetalleOrderId);
            Log.i("onClick", "PhotoString " + mphotString);
            mClickHandler.onClick(mDetalleOrderId, this);

        }
    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.picking_product_item, parent, false);
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



                Log.i("OrderDetailCursorAda:", "onBindViewHolder");

                viewHolder.mDetalleOrderId=cursor.getLong(COLUMN_ID_PRODUCTO);
                viewHolder.mphotString=cursor.getString(COLUMN_IMAGEN_PRODUCTO);
                Drawable drawable = resize(mContext, R.drawable.ic_action_action_redeem);
                Picasso.with(viewHolder.mphotoProducto.getContext())

                        .load(cursor.getString(COLUMN_IMAGEN_PRODUCTO))
                        .resize(viewHolder.mphotoProducto.getMaxWidth(),viewHolder. mphotoProducto.getMaxHeight())
                        .placeholder(drawable)
                        .centerCrop()
                        .into(viewHolder.mphotoProducto);

                viewHolder.mTextViewNombre.setText(cursor.getString(COLUMN_NOMBRE_PRODUCTO));
//                NumberFormat format = NumberFormat.getCurrencyInstance();
//               Log.i("OrderDetailCursorAda:", "mRefPickingDetail prod: "+cursor.getString(6)+ "cant: "+Double.toString(cursor.getDouble(5))+
//                       "cantPincking: "+Double.toString(cursor.getDouble(11))+ " pickinID : "+cursor.getLong(10));

//                viewHolder.mTextViewPrecio.setText(format.format(cursor.getDouble(4)));
                viewHolder.mTextcantidad.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_ORDENES)));
//                viewHolder.mTextcantidadPicking.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_PICKING)/cursor.getInt(COLUMN_OORDERS_COUNT)));
                viewHolder.mTextcantidadPicking.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_PICKING)));

                  viewHolder.mTextcantidadDelivey.setText(Integer.toString(cursor.getInt(COLUMN_QTOTAL_DELIVERY)));

                if (Integer.parseInt(viewHolder.mTextcantidadPicking.getText().toString()) >
                        Integer.parseInt(  String.valueOf(viewHolder.mTextcantidad.getText().toString()))){
                    viewHolder.mTextcantidadPicking.setTextColor(Color.GREEN);

                }
                    else if (Integer.parseInt(viewHolder.mTextcantidadPicking.getText().toString()) ==
                        Integer.parseInt(  String.valueOf(viewHolder.mTextcantidad.getText().toString()))){
                    viewHolder.mTextcantidadPicking.setTextColor(Color.BLUE);

                }
                        else {
                    viewHolder.mTextcantidadPicking.setTextColor(Color.RED);
                }




//                viewHolder.mTextToal.setText(format.format(
//                        cursor.getDouble(4)
//                        * cursor.getDouble(5)
//                        ));
//                viewHolder.mTextViewDescition.setText(cursor.getString(8));

//                viewHolder.mfavorito.setChecked(new BoolIntConverter().intToBool(cursor.getInt(3)));
//                cursor.getLong(10)== null ? Log.i("OrderDetailCursorAda:", "mRefPickingDetail NUL"):Log.i("OrderDetailCursorAda:", "mRefPickingDetail"+cursor.getLong(10));
                viewHolder.mRefProduct =cursor.getLong(COLUMN_ID_PRODUCTO) ;
//                viewHolder.mRefCustomer =cursor.getLong(9) ;
                viewHolder.mRefPickingDetail =cursor.getLong(COLUMN_ID_PICKING) ;
                 }



    public static interface ProductCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);

    }

            @Override
            public void onItemDismiss ( int position){
                Log.i("TouchHelper:", "Adapter onItemDismiss ");

            }

    @Override
    public void onItemAcepted(int position) {
        Log.i("TouchHelper:", "onItemAcepted ");
    }




}
