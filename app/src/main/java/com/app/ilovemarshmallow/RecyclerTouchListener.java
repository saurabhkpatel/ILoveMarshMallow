package com.app.ilovemarshmallow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.app.ilovemarshmallow.utils.Utils;

/**
 * RecyclerTouchListener.java - This class used for touch events of recycler view.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGestureDetector;
    private final ClickListener mClickListener;
    private final Context mContext;

    public RecyclerTouchListener(final Context context, final RecyclerView recyclerView,
                                 final ClickListener clickListener) {
        mContext = context;
        this.mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                        if (Utils.isConnectingToInternet(mContext)) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null && clickListener != null) {
                                clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        if (Utils.isConnectingToInternet(mContext)) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e)) {
                mClickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
