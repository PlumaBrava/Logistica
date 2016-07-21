package com.nextnut.logistica.swipe_helper;

/**
 * Created by sam_chordas on 8/14/15.
 */
public interface ItemTouchHelperAdapter {

    //void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
    void onItemAcepted(int position);
}
