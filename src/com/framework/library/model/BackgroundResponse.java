package com.framework.library.model;

import java.io.Serializable;

import com.framework.library.util.BackgroundInterfaces.BackgroundListener;

public class BackgroundResponse implements IModel, Serializable{

	private static final long serialVersionUID = 1L;
	
	String methodName = "";
	Object data = null;
	BackgroundListener listener;
	String methodCallback = "";
	Class<?> methodParam = null;
	Object objectCallback;
	
	public BackgroundResponse(String methodName, Object data, BackgroundListener listener) {
		super();
		this.methodName = methodName;
		this.data = data;
		this.listener = listener;
	}
	
	public BackgroundResponse(String methodName, Object data, String methodCallback, Class<?> methodParam, Object objectCallback) {
		super();
		this.methodName = methodName;
		this.data = data;
		this.methodCallback = methodCallback;
		this.methodParam = methodParam;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public BackgroundListener getListener() {
		return listener;
	}

	public void setListener(BackgroundListener listener) {
		this.listener = listener;
	}

	public String getMethodCallback() {
		return methodCallback;
	}

	public void setMethodCallback(String methodCallback) {
		this.methodCallback = methodCallback;
	}
	
	public Class<?> getMethodParam() {
		return methodParam;
	}

	public void setMethodParam(Class<?> methodParam) {
		this.methodParam = methodParam;
	}

	public Object getObjectCallback() {
		return objectCallback;
	}

	public void setObjectCallback(Object objectCallback) {
		this.objectCallback = objectCallback;
	}
}
