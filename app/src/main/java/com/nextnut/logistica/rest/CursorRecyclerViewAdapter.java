package com.nextnut.logistica.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
extends RecyclerView.Adapter<VH>
{
    private static final String LOG_TAG = CursorRecyclerViewAdapter.class.getSimpleName();
    private static final String ID_Adpater = "_id";
    private Context mContext;
    private Cursor mCursor;
    private boolean mDataIsValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
    private View mEmptyView;

    public CursorRecyclerViewAdapter(Context context, Cursor cursor, View emptyView){
        mContext = context;
        mCursor = cursor;
        mDataIsValid = cursor != null;
        mRowIdColumn = mDataIsValid ? mCursor.getColumnIndex(ID_Adpater) : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        mEmptyView=emptyView;

        if (mDataIsValid){
            mCursor.registerDataSetObserver(mDataSetObserver);
        }

    }

    public Cursor getCursor(){
        return mCursor;
    }

    @Override
    public int getItemCount(){
        if(mDataIsValid && mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position){
        if(mDataIsValid && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }




    @Override
    public void setHasStableIds(boolean hasStableIds){
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position){
        if(!mDataIsValid){
            throw new IllegalStateException("This should only be called when Cursor is valid");
        }
        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Could not move cursor to position " + position);
        }

        onBindViewHolder(viewHolder, mCursor);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {

            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if(mCursor != null){
            if(mDataSetObserver != null){
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow(ID_Adpater);
            mDataIsValid = true;
            notifyDataSetChanged();

            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }else{
            mRowIdColumn = -1;
            mDataIsValid = false;
            notifyDataSetChanged();
            mEmptyView.setVisibility(View.VISIBLE );

        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged(){
            super.onChanged();
            mDataIsValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated(){
            super.onInvalidated();
            mDataIsValid = false;
            notifyDataSetChanged();
        }
    }
}
