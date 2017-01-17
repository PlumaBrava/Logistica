package com.nextnut.logistica.swipe_helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.nextnut.logistica.R;

import static com.nextnut.logistica.R.drawable.ic_action_action_redeem;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_PICKING;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_PICKING;
import static com.nextnut.logistica.util.Constantes.ADAPTER_DETALLE_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_DETALLE_ORDEN;

/**
 * Created by sam_chordas on 8/14/15.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback{
    private final ItemTouchHelperAdapter mAdapter;

    private int mStep;
    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter,int step){
        mAdapter = adapter;
        mStep=step;
    }





    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    private Paint p = new Paint();
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        Bitmap icon=null;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 6;

            if (dX > 0) {
                p.setColor(recyclerView.getResources().getColor(R.color.SwipeRight));

                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);

                switch (mStep) {

                    case ADAPTER_CABECERA_ORDEN:
                    icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_carga);
                    break;
                    case ADAPTER_CABECERA_PICKING:
                    icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_lory);
                    break;
                    case ADAPTER_CABECERA_DELIVEY:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_candado);
                        break;
                    case ADAPTER_CABECERA_ORDEN_EN_PICKING:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_carga);
                        break;

                }

//                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + 1* width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 5 * width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                p.setColor(recyclerView.getResources().getColor(R.color.SwipeLeft));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);

                switch (mStep) {

                    case ADAPTER_DETALLE_ORDEN:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_delete);
                        break;
                    case ADAPTER_CABECERA_ORDEN:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_delete);
                        break;
                    case ADAPTER_CABECERA_PICKING:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(),ic_action_action_redeem);
                        break;
                    case ADAPTER_CABECERA_DELIVEY:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.ic_carga);
                        break;
                    case ADAPTER_CABECERA_ORDEN_EN_PICKING:
                        icon = BitmapFactory.decodeResource(recyclerView.getResources(), ic_action_action_redeem);
                        break;

                }




                RectF icon_dest = new RectF((float) itemView.getRight() - 5* width, (float) itemView.getTop() + width, (float) itemView.getRight() - 1*width, (float) itemView.getBottom() - width);

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
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        switch ( mStep   ){
            case ADAPTER_CABECERA_ORDEN:

            case ADAPTER_CABECERA_PICKING:
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; //Swipe para ambos lados.
                break;

            case ADAPTER_CABECERA_DELIVEY:          // Swipe a la derecha
                swipeFlags =  ItemTouchHelper.END;
                break;
            case ADAPTER_DETALLE_ORDEN:
            case ADAPTER_DETALLE_DELIVEY:
            case ADAPTER_CABECERA_ORDEN_EN_PICKING:
                swipeFlags =  ItemTouchHelper.START; //Swipe a la izquierda
                break;
        }
        return makeMovementFlags(dragFlags, swipeFlags);


    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

       switch (i){
           case ItemTouchHelper.END:
               mAdapter.onItemAcepted(viewHolder.getAdapterPosition());
               break;
           case ItemTouchHelper.START :

               mAdapter.onItemDismiss(viewHolder.getAdapterPosition());

               break;
           default:
       }


    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
//            itemViewHolder.onItemSelected();
//        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
//        itemViewHolder.onItemClear();
    }
}
