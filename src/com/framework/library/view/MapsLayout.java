package com.framework.library.view;

import com.framework.library.view.MapsLayout.StateChangedListener.State;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class MapsLayout extends ViewGroup {
	
	public interface StateChangedListener{
		public static enum State {OPEN, CLOSED, MIDDLE}
		public void onStateChanged(State state);
	}

	private final ViewDragHelper mDragHelper;

	private View mHeaderView;
    private View mDescView;

	private float mInitialMotionX;
	private float mInitialMotionY;

	private int mDragRange;
    private int mTop;
	private float mDragOffset;
	
	private boolean insideFragmentFirst;
	
	private float maxTopPosition = 0f;
	private float maxBottomPosition = 1.0f;

	private StateChangedListener stateChangedListener;
	
	private boolean shouldInterceptTouchEvent = true;

    public MapsLayout(Context context) {
		this(context, null);
	}

	public MapsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MapsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
	}

    public boolean isInsideFragmentFirst() {
		return insideFragmentFirst;
	}

	public void setInsideFragment(boolean insideFragmentFirst) {
		this.insideFragmentFirst = insideFragmentFirst;
	}

	@Override
    protected void onFinishInflate() {
        mHeaderView = findViewById(android.R.id.tabhost);
        mDescView = findViewById(android.R.id.tabcontent);
    }

    public void maximize(float slideOffset) {    	
        smoothSlideTo(slideOffset);
    }

    public void minimize(float slideOffset) {    	
        smoothSlideTo(slideOffset);
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y;
                
        if(insideFragmentFirst){
        	y = (int) (topBound + slideOffset * (mDragRange - mHeaderView.getHeight()));
        	insideFragmentFirst = false;
        }else{
        	y = (int) (topBound + slideOffset * mDragRange);
        }
        
        if (mDragHelper.smoothSlideViewTo(mHeaderView, mHeaderView.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
            return child == mHeaderView;
		}

        @Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			mTop = top;

			mDragOffset = (float) top / mDragRange;

//            mHeaderView.setPivotX(mHeaderView.getWidth());
//            mHeaderView.setPivotY(mHeaderView.getHeight());
//            mHeaderView.setScaleX(1 - mDragOffset / 2);
//            mHeaderView.setScaleY(1 - mDragOffset / 2);

//            mDescView.setAlpha(1 - mDragOffset);

            requestLayout();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int top = getPaddingTop();
			if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
				top += mDragRange;
				
				if(stateChangedListener != null){
					stateChangedListener.onStateChanged(State.CLOSED);
				}
			}else{
				if(stateChangedListener != null){
					stateChangedListener.onStateChanged(State.OPEN);
				}
			}
			mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
			invalidate();
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			return mDragRange;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			final int topBound = getPaddingTop();
			final int bottomBound = getHeight() - mHeaderView.getHeight() - mHeaderView.getPaddingBottom();

			final int newTop = Math.min(Math.max(top, topBound), bottomBound);
			return newTop;
		}
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(shouldInterceptTouchEvent){
			final int action = MotionEventCompat.getActionMasked(ev);

			if (( action != MotionEvent.ACTION_DOWN)) {
				mDragHelper.cancel();
				return super.onInterceptTouchEvent(ev);
			}

			if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
				mDragHelper.cancel();
				return false;
			}

			final float x = ev.getX();
			final float y = ev.getY();
			boolean interceptTap = false;

			switch (action) {
				case MotionEvent.ACTION_DOWN: {
					mInitialMotionX = x;
					mInitialMotionY = y;
	                interceptTap = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
					break;
				}

				case MotionEvent.ACTION_MOVE: {
					final float adx = Math.abs(x - mInitialMotionX);
					final float ady = Math.abs(y - mInitialMotionY);
					final int slop = mDragHelper.getTouchSlop();
	                /*useless*/
					if (ady > slop && adx > ady) {
						mDragHelper.cancel();
						return false;
					}
				}
			}

			return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;	
		}else{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mDragHelper.processTouchEvent(ev);

		final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mHeaderView, (int) x, (int) y);
        switch (action & MotionEventCompat.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				mInitialMotionX = x;
				mInitialMotionY = y;
				break;
			}

			case MotionEvent.ACTION_UP: {
				final float dx = x - mInitialMotionX;
				final float dy = y - mInitialMotionY;
				final int slop = mDragHelper.getTouchSlop();
				if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
					if (mDragOffset == 0) {
						smoothSlideTo(maxBottomPosition);
						
						if(stateChangedListener != null){
							stateChangedListener.onStateChanged(State.CLOSED);
						}
					} else if (mDragOffset != maxTopPosition) {
						smoothSlideTo(maxTopPosition);
						
						if(stateChangedListener != null){
							stateChangedListener.onStateChanged(State.MIDDLE);
						}
					}else{
						smoothSlideTo(0f);
						
						if(stateChangedListener != null){
							stateChangedListener.onStateChanged(State.OPEN);
						}
					}
				}
				
				break;
			}
		}

		return isHeaderViewUnder && isViewHit(mHeaderView, (int) x, (int) y) || isViewHit(mDescView, (int) x, (int) y);
	}

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState2(maxWidth, widthMeasureSpec, 0),
        		resolveSizeAndState2(maxHeight, heightMeasureSpec, 0));
    }

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mDragRange = getHeight() - mHeaderView.getHeight();

        mHeaderView.layout(
                0,
                mTop,
                r,
                mTop + mHeaderView.getMeasuredHeight());

        mDescView.layout(
                0,
                mTop + mHeaderView.getMeasuredHeight(),
                r,
                mTop  + b);
	}
    
    private int resolveSizeAndState2(int size, int measureSpec, int childMeasuredState) {
    	int result = size;
    	int specMode = MeasureSpec.getMode(measureSpec);
    	int specSize =  MeasureSpec.getSize(measureSpec);
    	switch (specMode) {
    	case MeasureSpec.UNSPECIFIED:
    	    result = size;
    	    break;
    	case MeasureSpec.AT_MOST:
    	    if (specSize < size) {
    	        result = specSize | MEASURED_STATE_TOO_SMALL;
    	    } else {
    	        result = size;
    	    }
    	    break;
    	case MeasureSpec.EXACTLY:
    	    result = specSize;
    	    break;
    	}
    	return result | (childMeasuredState&MEASURED_STATE_MASK);
    }

	public float getMaxTopPosition() {
		return maxTopPosition;
	}

	public void setMaxTopPosition(float maxTopPosition) {
		this.maxTopPosition = maxTopPosition;
	}

	public float getMaxBottomPosition() {
		return maxBottomPosition;
	}

	public void setMaxBottomPosition(float maxBottomPosition) {
		this.maxBottomPosition = maxBottomPosition;
	}

	public StateChangedListener getStateChangedListener() {
		return stateChangedListener;
	}

	public void setStateChangedListener(StateChangedListener stateChangedListener) {
		this.stateChangedListener = stateChangedListener;
	}

	public boolean isShouldInterceptTouchEvent() {
		return shouldInterceptTouchEvent;
	}

	public void setShouldInterceptTouchEvent(boolean shouldInterceptTouchEvent) {
		this.shouldInterceptTouchEvent = shouldInterceptTouchEvent;
	}
}
