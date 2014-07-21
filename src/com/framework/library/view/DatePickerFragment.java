package com.framework.library.view;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment{
	
	private Calendar calendar;
	private OnDateSetListener onDateListener;
	
	private Map<String, Object> tagReference;
	
	public DatePickerFragment(){}
	
	public DatePickerFragment(OnDateSetListener onDateListener){
		this(onDateListener, Calendar.getInstance());
	}
	
	public DatePickerFragment(OnDateSetListener onDateListener, Calendar calendar){
		setCalendar(calendar);
		setOnDateListener(onDateListener);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int year = getCalendar().get(Calendar.YEAR);
		int month = getCalendar().get(Calendar.MONTH);
		int day = getCalendar().get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), getOnDateListener(), year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		getCalendar().set(Calendar.YEAR, year);
		getCalendar().set(Calendar.MONTH, month);
		getCalendar().set(Calendar.DAY_OF_MONTH, day);
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public OnDateSetListener getOnDateListener() {
		return onDateListener;
	}

	public void setOnDateListener(OnDateSetListener onDateListener) {
		this.onDateListener = onDateListener;
	}
	
	public Object getTagReference(String key) {
		if (tagReference == null) {
			return null;
		} else {
			return tagReference.get(key);
		}
	}

	public void setTagReference(String key, Object tag) {
		if (tagReference == null) {
			tagReference = new HashMap<String, Object>();
		}

		this.tagReference.put(key, tag);
	}
}