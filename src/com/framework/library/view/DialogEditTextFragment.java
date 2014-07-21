package com.framework.library.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class DialogEditTextFragment extends DialogFragment{
	
	public interface DialogEditTextChange{
		public void accept(String text);
		public void cancel();
	}
	
	private EditTextInput editText;
	private String dialogTitle;
	
	private DialogEditTextChange listener;
	
	public DialogEditTextFragment(){}
	
	public DialogEditTextFragment(DialogEditTextChange listener){
		this.listener = listener;
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

	    if(getDialogTitle() != null && !getDialogTitle().equals("")){
	    	builder.setTitle(getDialogTitle());
	    }
	    
	    editText = new EditTextInput(getActivity());
	    editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    container.addView(editText);
	    
	    builder.setView(container);
	    builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.accept(editText.getText().toString());
			}
		});
	    builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.cancel();
			}
		});
	    return builder.create();
	}

	public EditTextInput getEditText() {
		return editText;
	}

	public void setEditText(EditTextInput editText) {
		this.editText = editText;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public DialogEditTextChange getListener() {
		return listener;
	}

	public void setListener(DialogEditTextChange listener) {
		this.listener = listener;
	}
}