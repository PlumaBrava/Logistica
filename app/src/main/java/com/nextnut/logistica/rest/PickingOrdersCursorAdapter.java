package com.nextnut.logistica.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nextnut.logistica.MainActivity;
import com.nextnut.logistica.PickingListFragment;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.PickingOrdersColumns;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;

//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.nextnut.logistica.CustomOrderListFragment;


public class PickingOrdersCursorAdapter extends CursorRecyclerViewAdapter<PickingOrdersCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;
    ViewHolder mVh;
    final private PinckingOrdersCursorAdapterOnClickHandler mClickHandler;
//    final private PickingOrdersCursorAdapteronDataChangekHandler mOnDataChangeHandler;

    int mProcesStep;
    private boolean mpickongOrderSelected=false;

//    public static final int STEP_CUSTOM_ORDER = 0;
//    public static final int STEP_PICKING = 1;
//    public static  final int STEP_DELIVEY = 2;

//    private View.OnClickListener listener;
    public PickingOrdersCursorAdapter(Context context, Cursor cursor, View empltyView, PinckingOrdersCursorAdapterOnClickHandler dh

    ){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;


    }


    public class ViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder, View.OnClickListener {
        public long mPickingOrderId;
        public TextView mPickingOrderNumber;
        public TextView mpickingOrderComents;
        public TextView mCreationDate;
        public ImageButton mSharePickingOrder;

        public Context holderContex = mContext;

        //        public CircleImageView mImageview;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            mPickingOrderNumber = (TextView) view.findViewById(R.id.pickingNumberOrderCard);
            mpickingOrderComents = (TextView) view.findViewById(R.id.pickingOrderComents);
            mCreationDate = (TextView) view.findViewById(R.id.PicckinOder_creationdate);
            mSharePickingOrder =(ImageButton)view.findViewById(R.id.SharePickingorder);

            mSharePickingOrder.setBackgroundColor(Color.TRANSPARENT);

        }


        @Override
        public void onItemSelected() {
            Log.i("TouchHelper:", "Adapter onItemSelected(): ");
//            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {

            Log.i("TouchHelper:", "Adapter onItemClear(): ");
//            itemView.setBackgroundColor(0x7f0100a5);
        }




        @Override
        public void onClick(View view) {

            Log.i("onClick", "onClick " + getPosition() + " " + getAdapterPosition());
            Log.i("onClick", "cursorID " + mPickingOrderId);

            mpickongOrderSelected=  mpickongOrderSelected ? false : true;
             mClickHandler.onClick(mPickingOrderId, this);

        }




    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.picking_orders_list_content, parent, false);


                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
                return vh;
            }


            @Override
            public void onBindViewHolder (final ViewHolder viewHolder, Cursor cursor){
                DatabaseUtils.dumpCursor(cursor);


                if(cursor.getInt(cursor.getColumnIndex(PickingOrdersColumns.STATUS_PICKING_ORDERS))==
                        PickingListFragment.PICKING_STATUS_CERRADA){
                    Log.i("PickingCursor:", "PICKING_STATUS_CERRADA: ");
                    ((View )(viewHolder.mPickingOrderNumber.getParent().getParent())).setBackgroundColor(Color.RED);

                } else {
                    Log.i("PickingCursor:", "PICKING_STATUS_NO CERRADA: ");
                    ((View )(viewHolder.mPickingOrderNumber.getParent().getParent())).setBackgroundColor(
                            mContext.getResources().getColor(R.color.PickingCard_background));

                }

                viewHolder.mPickingOrderId =cursor.getLong(cursor.getColumnIndex(PickingOrdersColumns.ID_PICKING_ORDERS));
                viewHolder.mPickingOrderNumber.setText(Long.toString(cursor.getLong(cursor.getColumnIndex(PickingOrdersColumns.ID_PICKING_ORDERS))));
                viewHolder.mpickingOrderComents.setText(cursor.getString(cursor.getColumnIndex(PickingOrdersColumns.COMMENTS_PICKING_ORDERS)));
                viewHolder.mCreationDate.setText(cursor.getString(cursor.getColumnIndex(PickingOrdersColumns.CREATION_DATE_PICKING_ORDERS)));
                Log.i("PickingCursor:", "getmPickingOrderSelected: "+MainActivity.getmPickingOrderSelected());
                Log.i("PickingCursor:", "D_PICKING_ORDERS: "+cursor.getLong(cursor.getColumnIndex(PickingOrdersColumns.ID_PICKING_ORDERS)));
//                if(mpickongOrderSelected && MainActivity.getmPickingOrderSelected()==cursor.getLong(cursor.getColumnIndex(PickingOrdersColumns.ID_PICKING_ORDERS))){
//                    viewHolder.itemView.setBackgroundColor(Color.RED);
//
//                }else {
////                    viewHolder.itemView.setBackgroundColor(Color.MAGENTA);
////                    viewHolder.itemView.setVisibility(mpickongOrderSelected?View.GONE:View.VISIBLE);
//                }

                viewHolder.mSharePickingOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("adapter", "sharePickingorder"+ viewHolder.mPickingOrderNumber.getText().toString());
                        mClickHandler.sharePickingorder(viewHolder);
                    }
                });
                }


    private int lastPosition = -1;
    private long cursorId;
    private void setAnimation(View viewToAnimate)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
//        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
        }
    }


    public static interface PinckingOrdersCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
        void onDataChange();
        void onItemDismissCall(long cursorID);
        void onItemAceptedCall ( long cursorID);
        void onDialogAlert(String message);
        void sharePickingorder(ViewHolder vh);
    }

//    public static interface PickingOrdersCursorAdapteronDataChangekHandler {
//
//    }





            @Override
            public void onItemDismiss ( int position) {

                mClickHandler.onItemDismissCall(getItemId(position));
                Log.i("TouchHelper:", "Adapter onItemDismiss picking CUSTOM ORDER " + position);

            }

    @Override
    public void onItemAcepted(int position) {

        mClickHandler.onItemAceptedCall(getItemId(position));


        }


}
