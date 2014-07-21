package com.framework.library.util;

import android.content.Intent;

public class AppIntent extends Intent{
	public static final String EXTRA_URI = "extra_uri";
	public static final String EXTRA_SMS_BODY = "sms_body";
	public static final String EXTRA_TYPE = "extra_type";
	
	public static final String EXTRA_TYPE_TEXT = "text/plain";
	public static final String EXTRA_TYPE_PDF = "application/pdf";
	public static final String EXTRA_TYPE_EMAIL = "message/rfc822";

	public static final String EXTRA_FEATURE_MESSAGE = "extra_feature_message";

	public static final String RESULT_CODE = "RESULT_CODE";
	public static final String REQUEST_CODE = "REQUEST_CODE";
	
	public static final int RESULT_GALLERY = 1000;
	public static final int RESULT_CAMERA = 1001;
	public static final int RESULT_PICK_IMAGE = 1002;
	public static final int RESULT_VIDEO = 1003;
	
	public static final String INTENT_GALLERY = "com.butterfly.library.INTENT_GALLERY";
	public static final String INTENT_CAMERA = "com.butterfly.library.INTENT_CAMERA";
	public static final String INTENT_PICK_IMAGE = "com.butterfly.library.INTENT_PICK_IMAGE";

}
