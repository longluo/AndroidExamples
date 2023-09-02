package com.longluo.volume;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RatingBar;

public class WmtRatingBar extends RatingBar {

	private OnRatingBarChanging mOnRatingBarChanging;

	public WmtRatingBar(Context context) {
		super(context);
	}

	public WmtRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WmtRatingBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (mOnRatingBarChanging != null)
				mOnRatingBarChanging.onRatingChanging(this.getRating());
			break;
		}
		return super.onTouchEvent(event);

	}

	public void setOnRatingBarChange(OnRatingBarChanging changing) {
		mOnRatingBarChanging = changing;
	}

	public interface OnRatingBarChanging {
		void onRatingChanging(float f);
	}

}
