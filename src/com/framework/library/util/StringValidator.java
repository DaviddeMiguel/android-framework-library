package com.framework.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Patterns;
 
public class StringValidator {
 
	public static final int TYPE_EMAIL = 0;
	public static final int TYPE_URL = 1;
	public static final int TYPE_NUMERIC = 2;
	
	private Pattern pattern;
	private Matcher matcher;
 
	private static final String PATTERN_EMAIL = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static final String PATTERN_URL = 
			"^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

	private static final String PATTERN_NUMERIC = "-?\\d+(\\.\\d+)?";

 
	public StringValidator(final int type) {
		switch (type) {
		case TYPE_EMAIL:
			pattern = Patterns.EMAIL_ADDRESS;
			break;

		case TYPE_URL:
			pattern = Patterns.WEB_URL;
			break;
		case TYPE_NUMERIC:
			pattern = Pattern.compile(PATTERN_NUMERIC);
			break;
		}
		
	}
 
	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public boolean validate(final String hex) {
 
		matcher = pattern.matcher(hex);
		return matcher.matches();
 
	}
}
