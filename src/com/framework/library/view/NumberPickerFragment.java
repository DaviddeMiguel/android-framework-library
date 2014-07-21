package com.framework.library.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class NumberPickerFragment extends DialogFragment{
	
	private NumberPicker numberPicker;
	private int minValue;
	private int maxValue;
	private String dialogTitle;
	
	private OnClickListener onClickListener;
	private OnValueChangeListener onValueChangeListener;
	
	public NumberPickerFragment(){}
	
	public NumberPickerFragment(OnClickListener onClickListener, int maxValue){
		this(onClickListener, maxValue, 0);
	}
	
	public NumberPickerFragment(OnClickListener onClickListener, int maxValue, int minValue){
		setMinValue(minValue);
		setMaxValue(maxValue);
		setOnClickListener(onClickListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    LinearLayout container = new LinearLayout(getActivity());
	    container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    container.setGravity(Gravity.CENTER);
	    
	    
	    NumberPicker numberPicker = new NumberPicker(getActivity());
	    numberPicker.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    numberPicker.setMaxValue(getMaxValue());
	    numberPicker.setMinValue(getMinValue());
	    numberPicker.setWrapSelectorWheel(false);
	    
	    if(getOnValueChangeListener() != null){
	    	numberPicker.setOnValueChangedListener(getOnValueChangeListener());
	    }
	    
//	    String[] nums = new String[21];
//	    
//	    for(int i=0; i<nums.length; i++)
//	       nums[i] = Integer.toString(i*5);
//	    getNumberPicker().setDisplayedValues(nums);
	    
	    setNumberPicker(numberPicker);

	    if(getDialogTitle() != null && !getDialogTitle().equals("")){
	    	builder.setTitle(getDialogTitle());
	    }
	    
	    container.addView(getNumberPicker());
	    
	    builder.setView(container);
	    builder.setPositiveButton(android.R.string.ok, getOnClickListener());
	    builder.setNegativeButton(android.R.string.cancel, null);
	    return builder.create();
	}

	public NumberPicker getNumberPicker() {
		return numberPicker;
	}

	public void setNumberPicker(NumberPicker numberPicker) {
		this.numberPicker = numberPicker;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public OnValueChangeListener getOnValueChangeListener() {
		return onValueChangeListener;
	}

	public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
		this.onValueChangeListener = onValueChangeListener;
	}
}