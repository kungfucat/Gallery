package me.kungfucat.gall;

import android.content.Context;
import android.gesture.GestureUtils;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import me.kungfucat.gall.interfaces.OnItemClickListener;

/**
 * Created by harsh on 12/13/17.
 */

public class ImageClickedListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener clickListener;
    private GestureDetector gestureDetector;

    public ImageClickedListener(Context context, OnItemClickListener listener) {
        clickListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView=rv.findChildViewUnder(e.getX(),e.getY());
        if (childView != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onItemClick(childView, rv.getChildPosition(childView));
            return true;
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
