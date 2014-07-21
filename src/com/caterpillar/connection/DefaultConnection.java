package com.caterpillar.connection;

import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Handler;
import android.os.Message;

import com.framework.library.connection.ConnectionInterfaces.ConnectionListener;
import com.framework.library.exception.ConnectionException;
import com.framework.library.model.ConnectionResponse;

public abstract class DefaultConnection{
	private static ConnectionHandler connectionHandler;
	@SuppressWarnings("rawtypes")
	private RemoteConnection connection;

	private String url;
	private boolean active;
	
	public static ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public static void setConnectionHandler(ConnectionHandler connectionHandler) {
		DefaultConnection.connectionHandler = connectionHandler;
	}

	@SuppressWarnings("rawtypes")
	public RemoteConnection getConnection() {
		return connection;
	}

	@SuppressWarnings("rawtypes")
	public void setConnection(RemoteConnection connection) {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setEnableGzip(boolean enable) {
		getConnection().setEnableGzip(enable);
	}
	
	public void cancel(){
		getConnection().cancel();
	}

	public void addParameterGet(NameValuePair param){
		connection.addParameterGet(param.getName(), param.getValue());
	}
	
	public void addParametersGet(List<? extends NameValuePair> params){
		for(NameValuePair param : params){
			connection.addParameterGet(param.getName(), param.getValue());
		}
	}
	
	public void addParameterPost(NameValuePair param){
		connection.addParameterPost(param.getName(), param.getValue());
	}
	
	public void addParametersPost(List<? extends NameValuePair> params){
		for(NameValuePair param : params){
			connection.addParameterPost(param.getName(), param.getValue());
		}
	}
	
	public void setData(String data){
		getConnection().setData(data);
	}
	
	public void addParameterGet(String key, String value){
		connection.addParameterGet(key, value);
	}
	
	public void addParameterPost(String key, String value){
		connection.addParameterPost(key, value);
	}
	
	public void addParameterFile(String key, Object value){
		connection.addParameterFile(key, value);
	}
	
	public void addHeader(String key, String value){
		connection.addHeader(key, value);
	}
	
	public void addCookie(String key, String value){
		connection.addCookie(key, value);
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
					case RemoteConnection.DID_START:
						defaultConnection.setActive(true);
						break;

					default:
						defaultConnection.setActive(false);
						break;
					}
				}
				
				switch (message.what) {
				case RemoteConnection.DID_START: {		
					listener.onConnectionStart((ConnectionResponse) message.obj);
					break;
				}
				case RemoteConnection.DID_SUCCEED: {
					listener.onConnectionComplete((ConnectionResponse) message.obj);
					break;
				}
				case RemoteConnection.DID_ERROR: {
					listener.onConnectionError((ConnectionException) message.obj);
					break;
				}
				case RemoteConnection.DID_RESPONSE: {
					listener.onConnectionResponse((ConnectionResponse) message.obj);
				}
				case RemoteConnection.DID_HEADER :{
					listener.onConnectionHeader((ConnectionResponse) message.obj);
				}
				case RemoteConnection.DID_BITMAP :{
					listener.onConnectionBitmap((ConnectionResponse) message.obj);
				}
				}
			}
		}
	}
}
