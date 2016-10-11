package com.nextnut.logistica.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nextnut.logistica.CustomOrderListFragment;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;

import java.text.NumberFormat;


public class CustomsOrdersCursorAdapter extends CursorRecyclerViewAdapter<CustomsOrdersCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;

    final private CustomsOrdersCursorAdapterOnClickHandler mClickHandler;



    public static final int STEP_CUSTOM_ORDER = 0;
//    public static final int STEP_PICKING = 1;
//    public static  final int STEP_DELIVEY = 2;

    public CustomsOrdersCursorAdapter(Context context, Cursor cursor, View empltyView, CustomsOrdersCursorAdapterOnClickHandler dh

    ){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;


    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public long mcursorId ;
        public String mphotString;

        public TextView mOrderNumber;
        public TextView mName;
        public TextView mLastname;
        public ImageButton mBottonPhoto;
        public String mCustomerRefContacto;

        public TextView mTotalPrice;
        public TextView mDate;


        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mOrderNumber = (TextView) view.findViewById(R.id.numberOrderCard);
            mName = (TextView) view.findViewById(R.id.nameOrderCard);
            mLastname = (TextView) view.findViewById(R.id.latNameOrderCard);
            mTotalPrice = (TextView) view.findViewById(R.id.totalPriceOrderCard);
            mDate = (TextView) view.findViewById(R.id.dateOrderCard);
            mBottonPhoto = (ImageButton) view.findViewById(R.id.phoneClallButton);
            mBottonPhoto.setBackgroundColor(Color.TRANSPARENT);
            mBottonPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickHandler.onMakeACall(mCustomerRefContacto);
                }
            });
        }


        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }




        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mcursorId, this);

        }




    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.customorder_list_content, parent, false);

                ViewHolder vh = new ViewHolder(itemView);

                return vh;
            }

//          0  LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.ID_CUSTOM_ORDER ,
//          1  LogisticaDataBase.CUSTOM_ORDERS+"."+ CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER ,
//          2  LogisticaDataBase.CUSTOM_ORDERS+"."+CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER,
//          3  LogisticaDataBase.CUSTOMS+"."+ CustomColumns.NAME_CUSTOM,
//          4  LogisticaDataBase.CUSTOMS+"."+ CustomColumns.LASTNAME_CUSTOM
//          5  LogisticaDataBase.CUSTOMS + "." + CustomColumns.REFERENCE_CUSTOM

            @Override
            public void onBindViewHolder (ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);



                if(cursor.getInt(cursor.getColumnIndex(CustomOrdersColumns.STATUS_CUSTOM_ORDER))==
                        CustomOrderListFragment.ORDER_STATUS_DELIVERED){
                    ((View )(viewHolder.mOrderNumber.getParent().getParent())).setBackgroundColor(Color.RED);
                    viewHolder.mOrderNumber.setTextColor(Color.GREEN);
                }else {
                    ((View )(viewHolder.mOrderNumber.getParent().getParent())).setBackgroundColor(
                            mContext.getResources().getColor(R.color.CustomOrderCard_background));

                }


                viewHolder.mcursorId=cursor.getLong(0);
                viewHolder.mOrderNumber.setText(Long.toString(cursor.getLong(0)));
                viewHolder.mName.setText(cursor.getString(3));
                viewHolder.mLastname.setText(cursor.getString(4));
                NumberFormat format = NumberFormat.getCurrencyInstance();
                viewHolder.mTotalPrice.setText(format.format(cursor.getDouble(2)));
                viewHolder.mDate.setText(cursor.getString(1));
                viewHolder.mCustomerRefContacto=cursor.getString(5);
                viewHolder.mBottonPhoto.setVisibility(viewHolder.mCustomerRefContacto!=null?View.VISIBLE:View.GONE);


            }




    /**
     * Here is the key method to apply the animation
     */

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    private long cursorId;
    private void setAnimation(View viewToAnimate)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
        }
    }


    public interface CustomsOrdersCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
        void onMakeACall(String ContactID);
        void onDialogAlert(String message);
        void onItemDismissCall(long cursorID);
        void onItemAceptedCall ( long cursorID);
        void onDataChange();
    }






            @Override
            public void onItemDismiss ( int position) {



                        mClickHandler.onItemDismissCall(getItemId(position));




            }

    @Override
    public void onItemAcepted(int position) {
                mClickHandler.onItemAceptedCall(getItemId(position));


        }


}
