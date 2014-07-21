package com.framework.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ButtonStates extends Button{
	private boolean invalidate = true;
	
	boolean enableFilterColor = true;
	
	// Green
//	PorterDuffColorFilter filterColorSelected = new PorterDuffColorFilter(0x66a4ca39, PorterDuff.Mode.SRC_ATOP);
	
	PorterDuffColorFilter filterColorSelected = new PorterDuffColorFilter(0x66ffffff, PorterDuff.Mode.SRC_ATOP);
	PorterDuffColorFilter filterColorPressed = new PorterDuffColorFilter(0x33000000, PorterDuff.Mode.SRC_ATOP);
	PorterDuffColorFilter filterColorDisabled = new PorterDuffColorFilter(0x99ffffff, PorterDuff.Mode.SRC_ATOP);
	
	public ButtonStates(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ButtonStates(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtonStates(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(getBackground() != null){
			onDraw(getBackground());
		}else{
			Drawable[] compoundsDrawables = getCompoundDrawables();
			if (compoundsDrawables != null) {
				for (Drawable drawable : compoundsDrawables) {
					if(drawable != null){
						onDraw(drawable);	
					}
				}
			}
		}
	}
	
	private void onDraw(Drawable drawable){
		if(isEnableFilterColor() && invalidate){
			if(!isEnabled()){
				invalidate = false;
				drawable.setColorFilter(filterColorDisabled);
				invalidate();
		    }
		}	
	}
	
	private void dispatchTouchEvent(MotionEvent event, Drawable drawable){
		if(isEnabled() && isEnableFilterColor()){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:				
				applyFilterOrNot(drawable, filterColorPressed, true);
				
				invalidate();
				
				break;
			case MotionEvent.ACTION_UP:
				applyFilterOrNot(drawable, filterColorSelected, isSelected());
				
				invalidate();
				
				break;		
			case MotionEvent.ACTION_MOVE:
				if(event.getX() < 0 || event.getX() > getWidth() ||
				   event.getY() < 0 || event.getY() > getHeight()){
					
					applyFilterOrNot(drawable, filterColorSelected, isSelected());
					
					invalidate();
				}
				
				break;
			
			case MotionEvent.ACTION_CANCEL:
				applyFilterOrNot(drawable, filterColorSelected, isSelected());	
	
				invalidate();
				
				break;
			}
		}	
	}
	 	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {	
		if(getBackground() != null){
			dispatchTouchEvent(event, getBackground());
		}else{
			Drawable[] compoundsDrawables = getCompoundDrawables();
			if (compoundsDrawables != null) {
				for (Drawable drawable : compoundsDrawables) {
					if(drawable != null){
						dispatchTouchEvent(event, drawable);	
					}
				}
			}
		}
		
		return super.dispatchTouchEvent(event);
	}
	
	private void applyFilterOrNot(Drawable drawable, PorterDuffColorFilter filter, boolean apply){
		if(apply){
			drawable.setColorFilter(filter);
		}else{
			drawable.clearColorFilter();
		}
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		
		if(getBackground() != null){
			applyFilterOrNot(getBackground(), filterColorSelected, selected);	
		}else{
			Drawable[] compoundsDrawables = getCompoundDrawables();
			if (compoundsDrawables != null) {
				for (Drawable drawable : compoundsDrawables) {
					if(drawable != null){
						applyFilterOrNot(drawable, filterColorSelected, selected);	
					}
				}
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if(getBackground() != null){
			applyFilterOrNot(getBackground(), filterColorDisabled, enabled);
		}else{
			Drawable[] compoundsDrawables = getCompoundDrawables();
			if (compoundsDrawables != null) {
				for (Drawable drawable : compoundsDrawables) {
					if(drawable != null){
						applyFilterOrNot(drawable, filterColorDisabled, enabled);						
					}
				}
			}
		}
	}

	public PorterDuffColorFilter getFilterColorSelected() {
		return filterColorSelected;
	}

	public void setFilterColorSelected(PorterDuffColorFilter filterColorSelected) {
		this.filterColorSelected = filterColorSelected;
	}

	public PorterDuffColorFilter getFilterColorPressed() {
		return filterColorPressed;
	}

	public void setFilterColorPressed(PorterDuffColorFilter filterColorPressed) {
		this.filterColorPressed = filterColorPressed;
	}

	public PorterDuffColorFilter getFilterColorDisabled() {
		return filterColorDisabled;
	}

	public void setFilterColorDisabled(PorterDuffColorFilter filterColorDisabled) {
		this.filterColorDisabled = filterColorDisabled;
	}

	public boolean isEnableFilterColor() {
		return enableFilterColor;
	}

	public void setEnableFilterColor(boolean enableFilterColor) {
		this.enableFilterColor = enableFilterColor;
	}
}
