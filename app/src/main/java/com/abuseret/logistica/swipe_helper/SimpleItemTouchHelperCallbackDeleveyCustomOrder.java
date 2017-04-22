package com.abuseret.logistica.swipe_helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.abuseret.logistica.R;

/**
 * Created by sam_chordas on 8/14/15.
 */
public class SimpleItemTouchHelperCallbackDeleveyCustomOrder extends ItemTouchHelper.Callback{
    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallbackDeleveyCustomOrder(ItemTouchHelperAdapter adapter){
        mAdapter = adapter;
    }





    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    private Paint p = new Paint();
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                p.setColor(itemView.getResources().getColor(R.color.SwipeRight));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_candado);
                RectF icon_dest = new RectF((float) itemView.getLeft() + 1* width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 5 * width, (float) itemView.getBottom() - width);

                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                p.setColor(itemView.getResources().getColor(R.color.SwipeLeft));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_carga);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public boolean isLongPressDragEnabled(){
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.END ;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

       switch (i){
           case 32:
               mAdapter.onItemAcepted(viewHolder.getAdapterPosition());
               break;
           case 16:

               mAdapter.onItemDismiss(viewHolder.getAdapterPosition());

               break;
           default:
       }


    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}
