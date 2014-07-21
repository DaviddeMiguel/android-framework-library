package com.framework.library.view;

import java.util.HashMap;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.framework.library.util.StringValidator;

public class EditTextInput extends EditText {

	private boolean empty;
	private int inputType;
	private int minCharactersLenght;

	public static enum InputType {
		NONE(0), EMAIL(1), URL(2), NUMBER(3);
		private final int value;

		private InputType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	
	private static final HashMap<String, Integer> input_type = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;

		{
			put("none", 0);
			put("email", 1);
			put("url", 2);
			put("number", 3);
		}
	};

	private ColorStateList textColor;
	private ColorStateList textColorHint;
	private Object drawable;
	
	public EditTextInput(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public EditTextInput(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public EditTextInput(Context context) {
		super(context);
		init(null);
	}
	
	public void init(AttributeSet attrs){
	    if(attrs != null){
			try {
				empty = attrs.getAttributeBooleanValue(null, "empty", false);
				String inputType = attrs.getAttributeValue(null, "input_type");
				if(inputType != null){
					this.inputType = input_type.get(inputType);
				}else{
					this.inputType = -1;
				}
				
				minCharactersLenght = attrs.getAttributeIntValue(null, "minCharactersLenght", -1);
			} finally {}
	    }else{
			empty = false;
			inputType = -1;
			minCharactersLenght = -1;
	    }
	    
		textColor = getTextColors();
		textColorHint = getHintTextColors();
		drawable = getBackground();
	}

	public boolean validate() {
		boolean ok = true;
		if (empty) {
			if (getText().toString() == null || getText().toString().equals("")) {
				ok = false;
			}
		}
		
		if(!TextUtils.isEmpty(getText().toString())){
			if(ok){
				if(minCharactersLenght > 0){
					if(minCharactersLenght > this.getText().toString().length()){
						ok = false;
					}
				}
			}

			if(ok){
				if (inputType != -1) {
					InputType type = InputType.values()[inputType];
					StringValidator validator;
					
					switch (type) {
					case NONE:
						ok = true;
						break;
					case EMAIL:
						validator = new StringValidator(StringValidator.TYPE_EMAIL);
						ok = validator.validate(getText().toString());
						break;
					case URL:
						validator = new StringValidator(StringValidator.TYPE_URL);
						ok = validator.validate(getText().toString());
						break;
					case NUMBER:
						validator = new StringValidator(StringValidator.TYPE_NUMERIC);
						ok = validator.validate(getText().toString());
						break;
					default:
						break;
					}
				}	
			}
		}
		
		if(!ok){
			setState(0xffffffff, 0xC0ffffff, 0xffff0000);
		}else{
			setState(textColor, textColorHint, drawable);
		}

		return ok;
	}
	
	public void setState(int textColor, int textColorHint, Object background){
		setTextColor(textColor);
		setHintTextColor(textColorHint);
		setStateBackground(background);
	}
	
	public void setState(ColorStateList textColor, ColorStateList textColorHint, Object background){
		setTextColor(textColor);
		setHintTextColor(textColorHint);
		setStateBackground(background);
	}
	
	private void setStateBackground (Object background){
		if(background instanceof Integer){
			setBackgroundColor((Integer)background);
		}else if(background instanceof StateListDrawable){
			if(Build.VERSION.SDK_INT >= com.framework.library.util.Constants.VERSION_SDK_JELLYBEAN){
				setBackground((StateListDrawable)background);
			}else{
				setBackgroundDrawable((StateListDrawable)background);
			}	
		}else if (background instanceof AnimationDrawable){
			if(Build.VERSION.SDK_INT >= com.framework.library.util.Constants.VERSION_SDK_JELLYBEAN){
				setBackground((AnimationDrawable)background);
			}else{
				setBackgroundDrawable((AnimationDrawable)background);
			}	
		}else if (background instanceof Drawable){
			if(Build.VERSION.SDK_INT >= com.framework.library.util.Constants.VERSION_SDK_JELLYBEAN){
				setBackground((Drawable)background);
			}else{
				setBackgroundDrawable((Drawable)background);
			}	
		}
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}
