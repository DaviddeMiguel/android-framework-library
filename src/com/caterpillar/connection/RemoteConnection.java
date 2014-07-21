package com.caterpillar.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.framework.library.exception.ConnectionException;
import com.framework.library.json.JSONParser;
import com.framework.library.model.ConnectionResponse;

public class RemoteConnection<IType extends Object> implements Runnable {

	// ESTOS SON LOS TAGS QUE DIFERENCIAN LAS ENTRADAS EN EL SWICH DEL HANDLER
	public static final int DID_START 		= 0;
	public static final int DID_ERROR 		= 1;
	public static final int DID_SUCCEED 	= 2;
	public static final int DID_RESPONSE 	= 3;
	public static final int DID_HEADER 		= 4;
	public static final int DID_BITMAP 		= 5;

	// DIFERENTES METODOS DE CONEXION
	public static final int GET 	= 0;
	public static final int POST 	= 1;
	public static final int PUT 	= 2;
	public static final int DELETE 	= 3;

	//TIPOS DE ERRORES EN LA CONEXION
	public static final int STATUS_NO_CONNECTION 				= -1000;
	public static final int STATUS_ERROR 						= -2000;
	public static final int STATUS_CODE_OK 						= 200;
	public static final int STATUS_CODE_OK_CREATED				= 201;
	public static final int STATUS_CODE_OK_ACCEPTED 			= 202;
	public static final int STATUS_CODE_OK_NO_CONTENT 			= 204;
	public static final int STATUS_CODE_SOLICITUD_INCORRECTA 	= 400;
	public static final int STATUS_CODE_NO_AUTORIZADO 			= 401;
	public static final int STATUS_CODE_PROHIBIDO 				= 403;
	public static final int STATUS_CODE_NO_ENCONTRADO 			= 404;
	public static final int STATUS_CODE_ERROR_INTERNO_SERVIDOR 	= 500;
	public static final int STATUS_CODE_SERVICIO_NO_DISPONIBLE 	= 503;

	// DIFERENTES PARAMETROS PARA CREAR EL HTTPCLIENT Y EVITAR PROBLEMAS CON CERTIFICADOS
	public static final int PARAM_HTTP_PORT 					= 80;
	public static final int PARAM_HTTPS_PORT 					= 443;
	public static final String PARAM_HTTP 						= "http";
	public static final String PARAM_HTTPS 						= "https";
	public static final String PARAM_PROTOCOL_EXPECT_CONTINUE 	= "http.protocol.expect-continue";

	// DIFERENTES PARAMETROS PROPIOS QUE PUEDE NECESITAR LA CONEXION
	public static final String PARAM_AUTHORIZATION = "Authorization";
	public static final String PARAM_BASIC = "Basic ";
	public static final String PARAM_TWO_DOTS = ":";
	public static final String PARAM_ANDROID = "Android";
	public static final String PARAM_CONTENT_TYPE = HTTP.CONTENT_TYPE;
	public static final String PARAM_CONTENT_TYPE_JSON = "application/json";
	public static final String PARAM_CONTENT_TYPE_TEXT_XML = "text/xml";
	public static final String PARAM_CONTENT_TYPE_TEXT_JSON = "text/json";
	public static final String PARAM_CONTENT_TYPE_XHTML_XML = "application/xhtml+xml";
	public static final String PARAM_CONTENT_TYPE_RSS_XML = "application/rss+xml";
	public static final String PARAM_CONTENT_TYPE_ATOM_XML = "application/atom+xml";
	public static final String PARAM_CONTENT_TYPE_TEXT_HTML = "text/html";

	public static final String PARAM_ACCEPT_ENCODING 	= "Accept-Encoding";
	public static final String PARAM_GZIP 			 	= "gzip";
	
	public static final String PARAM_ACCEPT_LANGUAGE    = "Accept-Language";
	
	// TIPO DE RESPUESTA QUE SE ESPERA, SIRVE PARA PARSEARLO
	public static enum TypeResult {JSON, XML, BITMAP}
	
	// TIMEOUT PARA LA CONEXION
	private static int TIME_OUT = 15000;
	// ENCODING Y CHARSET PARA LA CONEXION
	private String encoding = HTTP.UTF_8;
	// DEVUELVE EL HTTPRESPONSE
	private boolean response = false;
	// DEVUELVE EL HEADER DE LA CONEXION
	private boolean header = false;
	
	private boolean enableGzip = true;
	
	private TypeResult typeResult = TypeResult.JSON;

	// PARAMETROS PARA LA CONEXION
	private Context context;
	private String url;
	private int method;
	private Handler handler;
	private String data;

	private IType tree;
		
	private String connectionId;
	private boolean cancelled;
		
	private HashMap<String, String> parametersGetMap;
	private HashMap<String, String> parametersPostMap;
	private HashMap<String, Object> parametersFileMap;
	private HashMap<String, String> headersMap;
	
	private HashMap<String, String> cookies;
	
	private HttpURLConnection urlConnection;
	
	/**
	 * Constructor principal, recoge el contexto, el tipo de conexion, la url y el handler al que volver�� cuando termine
	 * @param context
	 * @param method
	 * @param url
	 * @param handler
	 */
	public RemoteConnection(Context context, int method, String url, Handler handler) {
		this.context = context;
		this.method = method;
		this.url = url != null ? url.replace(" ", "%20") : "";
		this.handler = handler;
		
		disableConnectionReuseIfNecessary();
	}
	
	public HttpURLConnection getConnection(){
		return urlConnection;
	}
	
	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
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
	
	public void cancel(){
		setCancelled(true);
		
		ConnectionManager.getInstance().didComplete(this);
	}

	/**
	 * Devuelve el encoding de la llamada, por defecto es UTF-8
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Asigna manualmente el encoding de la llamada
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Devuelve el tipo de objeto que va a devolver cuando termine la conexion
	 * @return
	 */
	public IType getTree() {
		return tree;
	}

	/**
	 * Asigna el tipo de objeto que devolvera la conexion, habria que asignar tambien las keys para poder parsearlo (setKeys())
	 * @param tree
	 */
	public void setTree(IType tree) {
		this.tree = tree;
	}

	/**
	 * Para saber si tiene que devolver las cabeceras al handler
	 * @return
	 */
	public boolean isHeader() {
		return header;
	}

	/**
	 * Set true si se quieren recibir las cabeceras en el handler
	 * @param header
	 */
	public void setHeader(boolean header) {
		this.header = header;
	}
	
	public boolean isEnableGzip() {
		return enableGzip;
	}

	public void setEnableGzip(boolean enableGzip) {
		this.enableGzip = enableGzip;
	}

	/**
	 * si se quiere recuperar la respuesta http se le asignara true y en el
	 * swich con DID_RESPONSE se podra recuperar
	 * 
	 * @param value valor booleano que sirve para saber si se quiere recuperar la respuesta http o no
	 */
	public void setResponse(boolean value) {
		this.response = value;
	}

	/**
	 * Devuelve el valor de response, este valor indica si se va a recuperar la
	 * respuesta http o no en el swich con DID_RESPONSE
	 * @return
	 */
	public boolean getResponse() {
		return this.response;
	}

	public TypeResult getTypeResult() {
		return typeResult;
	}

	/**
	 * Puede ser xml o json segun la respuesta que se espere del servidor, por defecto es JSON
	 * @param typeResult
	 */
	public void setTypeResult(TypeResult typeResult) {
		this.typeResult = typeResult;
	}

	/**
	 * Se asigna el tiempo maximo que se desea como timeout
	 * 
	 * @param timeOut Tiempo maximo para el timeout
	 */
	public void setTimeOut(int timeOut) {
		TIME_OUT = timeOut;
	}

	/**
	 * Devuelve el timeout que tiene asignado actualmente
	 * 
	 * @return devuelve un entero
	 */
	public int getTimeOut() {
		return TIME_OUT;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public void addHeader(String key, String value){
		if(headersMap == null){
			headersMap = new HashMap<String, String>();
		}
		
		headersMap.put(key, value);
	}
	
	public void addParameterGet(String key, String value){
		if(parametersGetMap == null){
			parametersGetMap = new HashMap<String, String>();
		}
		
		parametersGetMap.put(key, value);
	}
	
	public void addParameterPost(String key, String value){
		if(parametersPostMap == null){
			parametersPostMap = new HashMap<String, String>();
		}
		
		parametersPostMap.put(key, value);
	}
	
	/**
	 * Añade un String como valor si es un solo archivo o un List de strings en caso de ser un array de archivos 
	 * @param key
	 * @param value
	 */
	public void addParameterFile(String key, Object value){
		if(parametersFileMap == null){
			parametersFileMap = new HashMap<String, Object>();
		}
		
		parametersFileMap.put(key, value);
	}
	
	public void addCookie(String key, String value){
		if(cookies == null){
			cookies = new HashMap<String, String>();
		}
		
		cookies.put(key, value);
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
				sendMessage(handler, RemoteConnection.DID_START, 0, new ConnectionResponse(url, null, this));
				urlConnection = null;
				
				try {
					URL urlCon = new URL(addGetParams());
					urlConnection = (HttpURLConnection) urlCon.openConnection();
					
					urlConnection.setReadTimeout(getTimeOut());
					urlConnection.setConnectTimeout(getTimeOut());
					urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					
					addHeaders(urlConnection);
					addCookies(urlConnection);
					
					if(method == POST){
						addPostParams(urlConnection);
					}
					
					processResponse();
				} catch (Exception e) {
					sendMessage(handler, RemoteConnection.DID_ERROR, RemoteConnection.STATUS_ERROR, new ConnectionException(url, e, tree, null, this));
				}finally{
					if(urlConnection != null){
						urlConnection.disconnect();
					}
				}
			} else {
				sendMessage(handler, RemoteConnection.DID_ERROR, RemoteConnection.STATUS_NO_CONNECTION, new ConnectionException(url, null, tree, null, this));
			}

			ConnectionManager.getInstance().didComplete(this);	
		}
	}
	
	public void addHeaders(HttpURLConnection urlConnection){
		if(headersMap != null && headersMap.size() > 0){
			for (Map.Entry<String, String> entry : headersMap.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void addCookies(HttpURLConnection urlConnection){
		if(cookies != null && cookies.size() > 0){
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
			
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				HttpCookie cookie = new HttpCookie(entry.getKey(), String.valueOf(entry.getValue()));
				cookie.setDomain(getDomainName(url));
				
				try {
					cookieManager.getCookieStore().add(new URI(url), cookie);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			if(cookieManager.getCookieStore().getCookies().size() > 0){
				urlConnection.setRequestProperty("Cookie", TextUtils.join(",",  cookieManager.getCookieStore().getCookies()));   
			}
		}
	}
	
	private String addGetParams() throws Exception{
		if(parametersGetMap != null && parametersGetMap.size() > 0){							
			StringBuilder builder = new StringBuilder(url);
			
			if(!url.contains("?")){
				builder.append( "?" );
			}
			
			int index = 0;
			int lenght = parametersGetMap.size();
			
			for (Map.Entry<String, String> entry : parametersGetMap.entrySet()) {
				builder.append(entry.getKey());
				builder.append("=");
				builder.append(URLEncoder.encode(entry.getValue(), encoding));
				
				if(index < lenght - 1){
					builder.append("&");
				}
				
				index++;
			}
			
			return builder.toString();
		}else{
			return url;
		}
	}

	private void addPostParams(HttpURLConnection urlConnection) throws Exception{
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setChunkedStreamingMode(1024);
		
		DataOutputStream outputStream;
		
		if (parametersFileMap != null && parametersFileMap.size() > 0) {
	        String boundary =  "--DMI123456789123456789";
			urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			
			outputStream = new DataOutputStream(urlConnection.getOutputStream());
			addPostParamsMultipart(outputStream, boundary);
		}else{
			outputStream = new DataOutputStream(urlConnection.getOutputStream());
			addPostParamsStrings(outputStream);
		}

        outputStream.flush();
        outputStream.close();
	}
	
	private void addPostParamsMultipart(DataOutputStream outputStream, String boundary) throws Exception{
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        
		// Add strings
		if(parametersPostMap != null && parametersPostMap.size() > 0){
			for (Map.Entry<String, String> entry : parametersPostMap.entrySet()) {				
				addMultipartString(outputStream, entry.getKey(), entry.getValue(), boundary);
			}
		}
		
		// Add files
		for (Map.Entry<String, Object> entry : parametersFileMap.entrySet()) {
			outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
			
			if(entry.getValue() instanceof String){
				addMultipartFile(outputStream, 0, entry.getKey(), entry.getValue(), boundary);
				
				outputStream.writeBytes(lineEnd);
			}else if(entry.getValue() instanceof List){
				List<String> list = (List<String>) entry.getValue();
				for(int i=0; i<list.size(); i++){
					addMultipartFile(outputStream, i, entry.getKey(), list.get(i), boundary);
					
					outputStream.writeBytes(lineEnd + twoHyphens + boundary + twoHyphens + lineEnd);
				}
			}							
		}
		
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	}
	
	private void addMultipartString(DataOutputStream outputStream, String key, Object value, String boundary) throws Exception{
        String twoHyphens = "--";
        String lineEnd = "\r\n";
        
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; " + lineEnd);
        outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd + lineEnd);
        outputStream.writeBytes(String.valueOf(value));        
        outputStream.writeBytes(lineEnd);
	}
	
	private void addMultipartFile(DataOutputStream outputStream, int i, String key, Object value, String boundary) throws Exception{
        String lineEnd = "\r\n";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        File file = new File(String.valueOf(value));
        if(file.exists()){
        	String data = "Content-Disposition: form-data; name=\"" + key + "[" + i + "]\"; filename=\"" + value.hashCode() +".jpg\"" + lineEnd;
        	data = data + "Content-Type: application/octet-stream" + lineEnd + lineEnd;
            outputStream.writeBytes(data);
            
            FileInputStream fileInputStream = new FileInputStream(file);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            
            while(bytesRead > 0) {
            	outputStream.write(buffer, 0, bufferSize);
            	bytesAvailable = fileInputStream.available();
            	bufferSize = Math.min(bytesAvailable, maxBufferSize);
            	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            
            fileInputStream.close();	
        }
	}
	
	private void addPostParamsStrings(DataOutputStream outputStream) throws Exception{
		String outString = "";
		if (parametersPostMap != null && parametersPostMap.size() > 0) {
			outString = getQuery();
		}else if (data != null) {
			outString = data;
		}
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		writer.write(outString);
		writer.flush();
		writer.close();
	}
	
	private String getQuery() throws UnsupportedEncodingException{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (Map.Entry<String, String> entry : parametersPostMap.entrySet()) {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}

	private void processResponse() throws IllegalStateException, IOException {
		if(!isCancelled()){
			int statusCode = 0;
			try {
				if (this.response == true) {
					statusCode = urlConnection.getResponseCode();
					sendMessage(handler, RemoteConnection.DID_RESPONSE, statusCode, new ConnectionResponse(url, response, this));
				}else{
					statusCode = urlConnection.getResponseCode();
					
					InputStream inputStream = null;
					try{
						if (statusCode != STATUS_CODE_OK && statusCode != STATUS_CODE_OK_ACCEPTED && statusCode != STATUS_CODE_OK_CREATED) {
							inputStream = new BufferedInputStream(urlConnection.getErrorStream());
						}else{
							inputStream = new BufferedInputStream(urlConnection.getInputStream());
						}
					}catch(Exception e){}
					
					String value = "";

					if (inputStream != null) {
						value = readContent(inputStream);
					}

					switch (statusCode) {
					case STATUS_CODE_OK:
					case STATUS_CODE_OK_ACCEPTED:
					case STATUS_CODE_OK_NO_CONTENT:
					case STATUS_CODE_OK_CREATED:
						switch (typeResult) {
						case JSON:
							if (inputStream != null && tree != null) {
			    	    		JSONParser<IType> jsonParser = new JSONParser<IType>();
			    	    		tree = jsonParser.parse(value, tree);

								sendMessage(handler, RemoteConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, tree, this));
							}else{
								sendMessage(handler, RemoteConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, value, this));
							}
							break;
						default:
							sendMessage(handler, RemoteConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, value, this));
							
							break;
						}

						break;

					default:
						sendMessage(handler, RemoteConnection.DID_ERROR, statusCode, new ConnectionException(url, null, tree, value, this));

						break;
					}
				}
			} catch (Exception e) {
				sendMessage(handler, RemoteConnection.DID_ERROR, statusCode, new ConnectionException(url, e, tree, null, this));
			}	
		}
	}
	
	public void sendMessage(Handler handler, int messageType, int statusCode, Object value) {
		if(!isCancelled()){
			handler.sendMessage(Message.obtain(handler, messageType, statusCode, 0, value));	
		}
	}

	public static String readContent(InputStream input) {
		StringBuilder total = null;

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			total = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {}

		if (total != null) {
			return total.toString();
		} else {
			return null;
		}
	}
	
	public static String getDomainName(String url){
		try{
		    URI uri = new URI(url);
		    String domain = uri.getHost();
		    return domain.startsWith("www.") ? domain.substring(4) : domain;
		}catch(URISyntaxException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
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
	
	private void disableConnectionReuseIfNecessary() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
}
