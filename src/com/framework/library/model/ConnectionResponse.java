package com.framework.library.model;

import java.io.Serializable;

public class ConnectionResponse implements IModel, Serializable{

	private static final long serialVersionUID = 1L;
	
	String url = "";
	Object data = null;
	Object connection = null;
	
	public ConnectionResponse(String url,Object data, Object connection) {
		super();
		this.url = url;
		this.data = data;
		this.connection = connection;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getConnection() {
		return connection;
	}

	public void setConnection(Object connection) {
		this.connection = connection;
	}
}
