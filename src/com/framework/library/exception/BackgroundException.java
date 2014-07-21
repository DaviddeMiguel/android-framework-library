package com.framework.library.exception;

import com.framework.library.model.IModel;
import com.framework.library.util.BackgroundInterfaces.BackgroundListener;

public class BackgroundException implements IModel{

	String methodName = "";
	Exception exception = null;
	BackgroundListener listener;
	String methodCallbackError = "";
	Object objectCallback;
	
	public BackgroundException(String methodName, Exception exception, BackgroundListener listener) {
		super();
		this.methodName = methodName;
		this.exception = exception;
		this.listener = listener;
	}
	
	public BackgroundException(String methodName, Exception exception, String methodCallbackError, Object objectCallback) {
		super();
		this.methodName = methodName;
		this.exception = exception;
		this.methodCallbackError = methodCallbackError;
		this.objectCallback = objectCallback;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	public BackgroundListener getListener() {
		return listener;
	}

	public void setListener(BackgroundListener listener) {
		this.listener = listener;
	}
	
	public String getMethodCallbackError() {
		return methodCallbackError;
	}

	public void setMethodCallbackError(String methodCallbackError) {
		this.methodCallbackError = methodCallbackError;
	}
	
	public Object getObjectCallback() {
		return objectCallback;
	}

	public void setObjectCallback(Object objectCallback) {
		this.objectCallback = objectCallback;
	}
}
