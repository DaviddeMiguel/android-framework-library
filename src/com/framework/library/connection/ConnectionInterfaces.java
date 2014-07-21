package com.framework.library.connection;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import com.framework.library.exception.ConnectionException;
import com.framework.library.model.ConnectionResponse;

public class ConnectionInterfaces {
	public interface ConnectionExecutor {
		public void request();
		public void addParams(List<? extends NameValuePair> params);
		public void setEntity(HttpEntity entity);
	}

	public interface ConnectionListener {
		/**
		 * El metodo es llamado cuando ha terminado la conexion 
		 * @param response (Tipo: ConnectionResponse) contiene la url de la conexion y el objeto de la respuesta, puede ser un objeto parseado o un string si
		 * no habia o no se podia parsear. Ademas contiene el objeto HttpConnection que se ha ejecutado
		 */
		public void onConnectionComplete(ConnectionResponse response);
		/**
		 * @param response (Tipo: ConnectionResponse) contiene la url de la conexion. Ademas contiene el objeto HttpConnection que se ha ejecutado
		 */
		public void onConnectionStart(ConnectionResponse response);
		
		/**
		 * * El metodo es llamado cuando se ha producido un error en la conexion, se el que sea
		 * @param e (Tipo: ConnectionException) Contiene la url, la excepcion si se ha producido, el objeto parseado si se ha podido parsear, el contenido de la respuesta en una 
		 * cadena de texto si se ha podido recuperar y el objeto HttpConnection que ejecuto la llamada
		 */
		public void onConnectionError(ConnectionException exception);
		/**
		 * El metodo es llamado cuando ha terminado la conexion 
		 * @param response (Tipo: ConnectionResponse) contiene la url de la conexion y el array de HttpResponse que ha devuelto la conexion
		 */
		public void onConnectionResponse(ConnectionResponse response);
		/**
		 * El metodo es llamado cuando ha terminado la conexion 
		 * @param response (Tipo: ConnectionResponse) contiene la url de la conexion y el array de Header que ha devuelto la conexion
		 */
		public void onConnectionHeader(ConnectionResponse response);
		
		/**
		 * Este metodo es llamado cuando ha terminado la conexion
		 * @param response (Tipo: ConnectionResponse) contiene la url de la conexion y el bitmap que ha devuelto la conexion
		 */
		public void onConnectionBitmap(ConnectionResponse response);
		/*
		 public void onConnectionComplete(Object data);
		public void onConnectionStart();
		public void onConnectionError(ConnectionException e);
		public void onConnectionResponse(HttpResponse response);
		public void onConnectionHeader(Header []headers);
		 */
	}
}
