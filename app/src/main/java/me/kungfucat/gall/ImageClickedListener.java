package me.kungfucat.gall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.kungfucat.gall.interfaces.OnItemClickListener;

/**
 * Created by harsh on 12/13/17.
 */

public class ImageClickedListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener clickListener;
    private GestureDetector gestureDetector;

    public ImageClickedListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        clickListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && clickListener != null) {
                    clickListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
