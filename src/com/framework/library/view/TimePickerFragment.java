package com.framework.library.view;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
	
	private Calendar calendar;
	private OnTimeSetListener onTimeSetListener;
	
	public TimePickerFragment(){}
	
	public TimePickerFragment(OnTimeSetListener onTimeSetListener){
		this(onTimeSetListener, Calendar.getInstance());
	}
	
	public TimePickerFragment(OnTimeSetListener onTimeSetListener, Calendar calendar){
		setCalendar(calendar);
		setOnTimeSetListener(onTimeSetListener);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = getCalendar().get(Calendar.HOUR_OF_DAY);
		int minute = getCalendar().get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), getOnTimeSetListener(), hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		getCalendar().set(Calendar.HOUR_OF_DAY, hourOfDay);
		getCalendar().set(Calendar.MINUTE, minute);
	}
	
	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public OnTimeSetListener getOnTimeSetListener() {
		return onTimeSetListener;
	}

	public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener) {
		this.onTimeSetListener = onTimeSetListener;
	}
}