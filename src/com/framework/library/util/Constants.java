package com.framework.library.util;

import java.util.regex.Pattern;

public class Constants {
	public static final String FILE_CACHE_DIRECTORY = "Android/data/%s";
	
	public static final int VERSION_SDK_CUPCAKE = 5;
	public static final int VERSION_SDK_DONUT = 6;
	public static final int VERSION_SDK_ECLAIR = 7;
	public static final int VERSION_SDK_FROYO = 8;
	public static final int VERSION_SDK_GINGERBREAD = 9;
	public static final int VERSION_SDK_HONEYCOMB = 11;
	public static final int VERSION_SDK_ICECREAM = 14;
	public static final int VERSION_SDK_JELLYBEAN = 16;
	public static final int VERSION_SDK_JELLYBEAN_MR1 = 17;

	public static final String SETTINGS = "settings";
	
	public static final long TIME_SPLASH = 1500;
	public static final int TIME_BACK_PRESSED_DEFAULT = 2000;
	public static final int TIME_HELP_DEFAULT = 10000;
	
	public static final String PARAM_CONTENT_TYPE_TEXT_PLAIN	= "text/plain";
	public static final String PARAM_CONTENT_TYPE_JSON			= "application/json";
	
	// SimpleDateFormat Templates
	public static final String TIME_FORMAT 						= "hh:mm a";
	public static final String TIME_FORMAT_ES   				= "HH:mm";
	public static final String DATE_FORMAT 						= "EEEE MM/dd/yyyy";
	public static final String SLASHED_DATE_FORMAT 				= "MM/dd/yyyy";
	public static final String SLASHED_DATE_FORMAT_ES 			= "dd/MM/yyyy";
	public static final String SLASHED_DATE_AND_TIME_FORMAT 	= "MM/dd/yyyy hh:mm a";
	public static final String SLASHED_DATE_AND_TIME_FORMAT_ES 	= "dd/MM/yyyy HH:mm";
	public static final String DAY_OF_WEEK						= "EEEE";
	public static final String DATE_FORMAT_SERVER				= "YYYY-MM-DD'T'hh:mm:ss.SSSTZD";
	public static final String DATE_DRUPAL_DATETIME				= "yyyy-MM-dd";
	public static final String DATE_DRUPAL_DATE					= "yyyy-MM-dd'T'hh:mm:ss";
	
	public static final int NUMERIC_NOT_INITIALIZED = -1;
	
	public static final String EXTENSION_TXT = ".txt";
	public static final String EXTENSION_XML = ".xml";
	public static final String EXTENSION_JSON = ".json";
	public static final String EXTENSION_ZIP = ".zip";
	public static final String EXTENSION_PNG = ".png";
	public static final String EXTENSION_JPG = ".jpg";
	public static final String EXTENSION_GIF = ".gif";
	public static final String EXTENSION_MP3 = ".mp3";
	
	public static final String TYPE_JSON = "JSON";
	public static final String TYPE_XML	 = "XML";
	
	/**
	 * Check if it is an email address
	 */
	public static final Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
	
	/**
	 * Check if it is an url
	 */
	public static final Pattern rfc_url_regex = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");

	
	public static enum AppType{
							   /**
							    * Example:
							    * http://www.vogella.com
							    */
							   BROWSER,
							   /**
							    * Example:
							    * (+49)12345789
							    */
							   PHONE_CALL, 
							   /**
							    * Example:
							    * (+49)12345789
							    */
							   PHONE_DIAL,
							   /**
							    * Example:
							    * (+49)12345789
							    */
							   PHONE_VIEW,
							   /**
							    * Example:
							    * (+49)12345789
							    */
							   SMS,
							   /**
							    * Example:
							    * 50.123,7.1434?z=19
							    */
							   MAPS,
							   /**
							    * Example:
							    * 0,0?q=query
							    */
							   MAPS_QUERY,
							   /**
							    * Example:
							    * contacts/people/
							    */
							   CONTACTS,
							   /**
							    * Example:
							    * contacts/people/1
							    */
							   CONTACTS_EDIT,
							   /**
							    * Example:
							    * Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
								* String aEmailList[] = { "user@fakehost.com","user2@fakehost.com" };  
								* String aEmailCCList[] = { "user3@fakehost.com","user4@fakehost.com"};  
								* String aEmailBCCList[] = { "user5@fakehost.com" };  
								* String filePathsList[] = { "mount/sd/download/file.txt","mount/sd/download/file.txt" };
								* emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);  
								* emailIntent.putExtra(android.content.Intent.EXTRA_CC, aEmailCCList);  
								* emailIntent.putExtra(android.content.Intent.EXTRA_BCC, aEmailBCCList);  
								* emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My subject");
								* emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, filePathsList);    
								* emailIntent.setType("plain/text");  
								* emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "My message body.");  
								* startActivity(emailIntent);  
							    */
							   EMAIL,
							   EMAIL_FILE,
							   SHARE,
							   GALLERY,
							   CAMERA, 
							   PICK_IMAGE,
							   VIDEO,
							   CHOOSER_IMAGE}
}
