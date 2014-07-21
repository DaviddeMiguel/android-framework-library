package com.framework.library.exception;

import com.caterpillar.connection.RemoteConnection;
import com.framework.library.connection.HttpConnection;
import com.framework.library.model.IModel;

public class ConnectionException implements IModel{

	String url = "";
	Exception exception = null;
	Object tree;
	String content;
	Object connection;
	
	@SuppressWarnings("rawtypes")
	public ConnectionException(String url, Exception exception, Object tree, String content, Object connection) {
		this.url = url;
		this.exception = exception;
		this.tree = tree;
		this.content = content;
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

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Object getTree() {
		return tree;
	}

	public void setTree(Object tree) {
		this.tree = tree;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getConnection() {
		return connection;
	}

	public void setConnection(Object connection) {
		this.connection = connection;
	}
}
