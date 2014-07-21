package com.framework.library.view;

import android.widget.AbsListView;

/**
 * Created by david on 05/06/14.
 */
public class OnScrollLoadListener implements AbsListView.OnScrollListener {
    int scrollState =  -1;
    int lastVisibleItem = 0;
    boolean isScrollingDown = false;


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem != lastVisibleItem){
            if (firstVisibleItem > lastVisibleItem) {
                isScrollingDown = true;
            }else {
                isScrollingDown = false;
            }

            lastVisibleItem = firstVisibleItem;
        }
    }

    public boolean shouldLoadMore(){
        if(scrollState ==  SCROLL_STATE_TOUCH_SCROLL || scrollState ==  SCROLL_STATE_FLING){
            return true;
        }else{
            return false;
        }
    }

    public boolean isScrollingDown(){
        return isScrollingDown;
    }
}
