package com.caterpillar.xmlrpc.connection;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

import com.framework.library.connection.ConnectionInterfaces.ConnectionListener;
import com.framework.library.exception.ConnectionException;
import com.framework.library.model.ConnectionResponse;

public abstract class DefaultConnection{
	private static ConnectionHandler connectionHandler;
	private XMLRpcConnection connection;

	private String url;
	private boolean active;
	
	public static ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public static void setConnectionHandler(ConnectionHandler connectionHandler) {
		DefaultConnection.connectionHandler = connectionHandler;
	}

	public XMLRpcConnection getConnection() {
		return connection;
	}

	public void setConnection(XMLRpcConnection connection) {
		this.connection = connection;
	}
	
	public String getConnectionId() {
		return getConnection().getConnectionId();
	}

	public void setConnectionId(String connectionId) {
		getConnection().setConnectionId(connectionId);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getMethod() {
		return connection.getMethod();
	}

	public void setMethod(String method) {
		connection.setMethod(method);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void cancel(){
		getConnection().cancel();
	}
	
	public void addHeader(String key, String value){
		connection.addHeader(key, value);
	}
	
	public void addCookie(String key, String value){
		connection.addCookie(key, value);
	}

	public void addParameter(Object param){
		connection.addParameter(param);
	}
	
	public void setParameters(Object[] parameters){
		connection.setParameters(parameters);
	}
	
	public void request(){
		getConnection().request();
	}
	
	public static class ConnectionHandler extends Handler {
		
		private WeakReference<DefaultConnection> mDefaultConnection;
		private WeakReference<ConnectionListener> mRefListener;
		
		public ConnectionHandler(DefaultConnection defaultConnection, ConnectionListener listener) {
			this.mDefaultConnection = new WeakReference<DefaultConnection>(defaultConnection);;
			this.mRefListener = new WeakReference<ConnectionListener>(listener);
		}
		
		@Override
		public void handleMessage(Message message) {
			DefaultConnection defaultConnection = mDefaultConnection.get();
			ConnectionListener listener = mRefListener.get();
			
			if(listener != null) {
				if(defaultConnection != null){
					switch (message.what) {
					case XMLRpcConnection.DID_START:
						defaultConnection.setActive(true);
						break;

					default:
						defaultConnection.setActive(false);
						break;
					}
				}
				
				switch (message.what) {
				case XMLRpcConnection.DID_START: {		
					listener.onConnectionStart((ConnectionResponse) message.obj);
					break;
				}
				case XMLRpcConnection.DID_SUCCEED: {
					listener.onConnectionComplete((ConnectionResponse) message.obj);
					break;
				}
				case XMLRpcConnection.DID_ERROR: {
					listener.onConnectionError((ConnectionException) message.obj);
					break;
				}
				}
			}
		}
	}
}
