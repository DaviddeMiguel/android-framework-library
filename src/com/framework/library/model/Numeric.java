package com.framework.library.model;

public class Numeric {
	private static final String PATTERN_NUMERIC = "-?\\d+(\\.\\d+)?";
	
	public static Boolean isNumeric(String value) {
		return getNumeric(value).matches(PATTERN_NUMERIC);  //match a number with optional '-' and decimal.
	}
	
	public static Double getDouble(String value){
		return Double.valueOf(getNumeric(value));
	}
	
	public static Float getFloat(String value){
		return Float.valueOf(getNumeric(value));
	}
	
	public static Integer getInteger(String value){
		return Integer.valueOf(getNumeric(value));
	}
	
	public static Long getLong(String value){
		return Long.valueOf(getNumeric(value));
	}
	
	public static String getNumeric(String value){
		if(value.contains(",")){
			return value.trim().replace(".", "").replace(",", ".");
		}else{
			return value;
		}
	}
}
