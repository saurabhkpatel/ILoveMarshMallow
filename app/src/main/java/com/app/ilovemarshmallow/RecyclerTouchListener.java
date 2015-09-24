package com.app.ilovemarshmallow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * RecyclerTouchListener.java - This class used for touch events of recycler view.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

	private GestureDetector gestureDetector;
	public ClickListener clickListener;

	public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
			final ClickListener clickListener) {
		this.clickListener = clickListener;
		gestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
						if (child != null && clickListener != null) {
							clickListener.onLongClick(child, recyclerView.getChildPosition(child));
						}
					}
				});
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

		View child = rv.findChildViewUnder(e.getX(), e.getY());
		if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
			clickListener.onClick(child, rv.getChildPosition(child));
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
