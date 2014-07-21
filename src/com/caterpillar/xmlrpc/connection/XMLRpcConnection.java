package com.caterpillar.xmlrpc.connection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

import com.caterpillar.xmlrpc.core.XmlRpcClient;
import com.caterpillar.xmlrpc.core.XmlRpcStruct;
import com.framework.library.exception.ConnectionException;
import com.framework.library.model.ConnectionResponse;

public class XMLRpcConnection implements Runnable {

	// ESTOS SON LOS TAGS QUE DIFERENCIAN LAS ENTRADAS EN EL SWICH DEL HANDLER
	public static final int DID_START 		= 0;
	public static final int DID_ERROR 		= 1;
	public static final int DID_SUCCEED 	= 2;
	
	//TIPOS DE ERRORES EN LA CONEXION
	public static final int STATUS_NO_CONNECTION 				= -1000;
	public static final int STATUS_ERROR 						= -2000;

	private Context context;
	private String url;
	private String method;
	private Handler handler;
	
	private Object[] parameters;
		
	private String connectionId;
	private boolean cancelled;
	
	private Map<String, String> cookies;
	private HashMap<String, String> headers;
	
	/**
	 * Constructor principal, recoge el contexto, el tipo de conexion, la url y el handler al que volver�� cuando termine
	 * @param context
	 * @param method
	 * @param url
	 * @param handler
	 */
	public XMLRpcConnection(Context context, String method, String url, Handler handler) {
		this.context = context;
		this.method = method;
		this.url = url != null ? url.replace(" ", "%20") : "";
		this.handler = handler;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(Object param){
		if(parameters == null){
			parameters = new Object[1];
		}else{
			parameters = Arrays.copyOf(parameters, parameters.length + 1);
		}
		
		parameters[parameters.length - 1] = param;
	}
	
	public void addCookie(String key, String value){
		if(cookies == null){
			cookies = new HashMap<String, String>();
		}
		
		cookies.put(key, value);
	}
	
	public void addHeader(String key, String value){
		if(headers == null){
			headers = new HashMap<String, String>();
		}
		
		headers.put(key, value);
	}


	public void cancel(){
		setCancelled(true);
		
		ConnectionManager.getInstance().didComplete(this);
	}

	/**
	 * Lanza el a��adir a cola y ejecutar una llamada sin parametros
	 */
	private void create() {
		ConnectionManager.getInstance().push(this);	
	}

	/**
	 * Este es el metodo principal de esta clase, su funci��n es meter la request
	 * en la cola de llamadas.
	 */
	public void request() {
		create();
	}

	/**
	 * Realiza la request correspondiente seg��n el m��todo elegido, tambien a��ade
	 * a la llamada los parametros que se le hayan pasado en la llamada
	 */
	public void run() {
		if(!isCancelled()){
			if (hasInternetConnection(context) == true) {
				sendMessage(handler, XMLRpcConnection.DID_START, 0, new ConnectionResponse(url, null, this));
				
				try {
					XmlRpcClient xmlrpc = new XmlRpcClient(url, false);
					xmlrpc.setRequestProperties(headers);
					xmlrpc.setRequestCookies(cookies);
					XmlRpcStruct res = (XmlRpcStruct) xmlrpc.invoke(method, parameters);
					
					sendMessage(handler, XMLRpcConnection.DID_SUCCEED, 0, new ConnectionResponse(url, res, this));
				} catch (Exception e) {
					sendMessage(handler, XMLRpcConnection.DID_ERROR, XMLRpcConnection.STATUS_ERROR, new ConnectionException(url, e, method, null, this));
				}
			} else {
				sendMessage(handler, XMLRpcConnection.DID_ERROR, XMLRpcConnection.STATUS_NO_CONNECTION, new ConnectionException(url, null, method, null, this));
			}

			ConnectionManager.getInstance().didComplete(this);	
		}
	}
	
	public void sendMessage(Handler handler, int messageType, int statusCode, Object value) {
		if(!isCancelled()){
			handler.sendMessage(Message.obtain(handler, messageType, statusCode, 0, value));	
		}
	}

	public static boolean hasInternetConnection(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    // test for connection
	    if (cm.getActiveNetworkInfo() != null && 
	    	cm.getActiveNetworkInfo().isAvailable() && 
	    	cm.getActiveNetworkInfo().isConnected()) {
	        return true;
	    } else {
	        return false;
	    }
	}
}
