package com.nextnut.logistica.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.RemoteException;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nextnut.logistica.CustomOrderListFragment;
import com.nextnut.logistica.MainActivity;
import com.nextnut.logistica.R;
import com.nextnut.logistica.data.CustomOrdersColumns;
import com.nextnut.logistica.data.LogisticaProvider;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperAdapter;
import com.nextnut.logistica.swipe_helper.ItemTouchHelperViewHolder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CustomsOrdersCursorAdapter extends CursorRecyclerViewAdapter<CustomsOrdersCursorAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    Context mContext;
    ViewHolder mVh;
    final private CustomsOrdersCursorAdapterOnClickHandler mClickHandler;


    int mProcesStep;

    public static final int STEP_CUSTOM_ORDER = 0;
    public static final int STEP_PICKING = 1;
    public static  final int STEP_DELIVEY = 2;

//    private View.OnClickListener listener;
    public CustomsOrdersCursorAdapter(Context context, Cursor cursor, View empltyView, CustomsOrdersCursorAdapterOnClickHandler dh,int procesStep

    ){
        super(context, cursor,empltyView);
        mContext = context;
        mClickHandler = dh;
        mProcesStep= procesStep;

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
            mBottonPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickHandler.onMakeACall(mCustomerRefContacto);
                }
            });




        }


        @Override
        public void onItemSelected() {
            Log.i("TouchHelper:", "Adapter onItemSelected(): ");
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {

            Log.i("TouchHelper:", "Adapter onItemClear(): ");
            itemView.setBackgroundColor(0x7f0100a5);
        }




        @Override
        public void onClick(View view) {

            Log.i("onClick", "onClick " + getPosition() + " " + getAdapterPosition());
            Log.i("onClick", "cursorID " + mcursorId);
            Log.i("onClick", "PhotoString " + mphotString);
            mClickHandler.onClick(mcursorId, this);

        }




    }

            @Override
            public ViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.customorder_list_content, parent, false);

//                itemView.setOnClickListener(mContext);
                ViewHolder vh = new ViewHolder(itemView);
                mVh = vh;
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
                    viewHolder.mOrderNumber.setTextColor(Color.GREEN);
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


//                viewHolder.mPickingOrderId=cursor.getInt(cursor.getColumnIndex(CustomColumns.ID_CUSTOM));
//                viewHolder.mOrderNumber.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(CustomOrdersColumns.ID_CUSTOM_ORDER))));
//                viewHolder.mName.setText("name");
//                viewHolder.mLastname.setText("LastName");
//                viewHolder.mTotalPrice.setText(Long.toString(cursor.getLong(cursor.getColumnIndex(CustomOrdersColumns.TOTAL_PRICE_CUSTOM_ORDER))));
//                viewHolder.mDate.setText(cursor.getString(cursor.getColumnIndex(CustomOrdersColumns.CREATION_DATE_CUSTOM_ORDER)));

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

    /**
     * Here is the key method to apply the animation
     */

    // Allows to remember the last item shown on screen
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


    public static interface CustomsOrdersCursorAdapterOnClickHandler {
        void onClick(long id, ViewHolder vh);
        void onMakeACall(String ContactID);
        void onDialogAlert(String message);
        void onItemDismissCall(long cursorID);
        void onItemAceptedCall ( long cursorID);
        void onDataChange();
    }

//    public static interface CustomsOrdersCursorAdapteronDataChangekHandler {
//
//    }





            @Override
            public void onItemDismiss ( int position) {

                switch (mProcesStep) {
                    case STEP_CUSTOM_ORDER: {

                        Log.i("TouchHelper:", "Adapter onItemDismiss CUSTOM ORDER " + position);
                        mClickHandler.onItemDismissCall(getItemId(position));


//                      cursorId = getItemId(position);
//                        AlertDialog.Builder alert = new AlertDialog.Builder((Activity)mContext);
//
//                        alert.setMessage("Do you want to delete?");
//                        alert.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                Log.i("YesNoDialog:", "setNegativeButton" );
//                                mClickHandler.onDataChange();
//
//                                dialog.cancel();
//                            }
//                        });
//                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                Log.i("YesNoDialog:", "setPositiveButton " );
//
//                                ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(2);
//
//                                ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrdersDetail.withRefCustomOrder(cursorId));
//                                ContentProviderOperation.Builder builder1 = ContentProviderOperation.newDelete(LogisticaProvider.CustomOrders.withId(cursorId));
//                                batchOperations.add(builder.build());
//                                batchOperations.add(builder1.build());
//
//                                try {
//
//                                    mContext.getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                                    notifyDataSetChanged();
//                                    mClickHandler.onDataChange();
//
//
////                    notifyItemRemoved(position);
//                                } catch (RemoteException | OperationApplicationException e) {
//                                    Log.e("TouchHelper:", "Error applying batch insert", e);
//
//                                }
//                            }
//                        });
//                        alert.create().show(); // btw show() creates and shows it..
//
                    }
                        break;
                    case STEP_PICKING :
                        Log.i("TouchHelper:", "Adapter onItemDismiss PICKIG --" + position);
                        mClickHandler.onItemDismissCall(getItemId(position));
//                        Log.i("TouchHelper:", "Adapter onItemDismiss PICKIG --" + position);
//                        cursorId = getItemId(position);
//                        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(cursorId));
//                        builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, 0);
//                        batchOperations.add(builder.build());
//
//
//                        try {
//
//                            mContext.getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                            mClickHandler.onDataChange();
//
//
//                        } catch (RemoteException | OperationApplicationException e) {
//                            Log.e("TouchHelper:", "Error applying batch insert", e);
//
//                        }

                        break;
                    case STEP_DELIVEY :
                        break;
                    default:
                        break;
                }


            }

    @Override
    public void onItemAcepted(int position) {


        switch(mProcesStep) {
            case STEP_CUSTOM_ORDER : {

                Log.i("TouchHelper:", "Adapter onItemAcepted" + position);
                mClickHandler.onItemAceptedCall(getItemId(position));

//                if (MainActivity.mPickingOrderSelected==0){
//                    mClickHandler.onDialogAlert(mContext.getString(R.string.selectPickingOrderToAssing));
//
//                }
//                else {
//                    Log.i("TouchHelper:", "Adapter onItemAcepted " + position + " : " + mContext.getApplicationContext().toString());
//                    long cursorId = getItemId(position);
//                    Log.e("TouchHelper:", "position: " + position);
//                    Log.e("TouchHelper:", "getItemCount(): " + getItemCount());
//
//
//                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(1);
//                    ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(LogisticaProvider.CustomOrders.withId(cursorId));
//                    builder.withValue(CustomOrdersColumns.STATUS_CUSTOM_ORDER, 1);
//                    SimpleDateFormat df = new SimpleDateFormat(mContext.getString(R.string.dateFormat));
//                    String formattedDate = df.format(new Date());
//                    builder.withValue(CustomOrdersColumns.DATE_OF_PICKING_ASIGNATION_CUSTOM_ORDER, formattedDate);
//                    builder.withValue(CustomOrdersColumns.REF_PICKING_ORDER_CUSTOM_ORDER, MainActivity.getmPickingOrderSelected());
//                    batchOperations.add(builder.build());
//                    try {
//
//                        mContext.getContentResolver().applyBatch(LogisticaProvider.AUTHORITY, batchOperations);
//                        Log.e("TouchHelper:", "position: " + position);
//                        Log.e("TouchHelper:", "getItemCount(): " + getItemCount());
//                        mClickHandler.onDataChange();
//
//
////                notifyItemRemoved(position);
////                    notifyItemRangeChanged(position, getItemCount());
//                    } catch (RemoteException | OperationApplicationException e) {
//                        Log.e("TouchHelper:", "Error applying batch insert", e);
//
//                    }
//                }
            }
                break;

                case STEP_PICKING :
                    break;
                case STEP_DELIVEY :
                    break;

                default:
                    break;
        }

        }


}
