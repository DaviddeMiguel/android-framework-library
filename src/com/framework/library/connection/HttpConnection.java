package com.framework.library.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

import com.framework.library.exception.ConnectionException;
import com.framework.library.json.JSONParser;
import com.framework.library.model.ConnectionResponse;
import com.framework.library.util.Functions;
import com.framework.library.xml.Parser;

public class HttpConnection<IType extends Object> implements Runnable {

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
	private List<IType> trees;
	
	@Deprecated
	private Map<String, String> keys;

	private HttpClient httpClient;
	
	private String connectionId;
	private boolean cancelled;
	
	public boolean imageConnection = false;
	
	private HashMap<String, String> parametersGetMap;
	private HashMap<String, String> parametersPostMap;
	private HashMap<String, Object> parametersFileMap;
	private HashMap<String, String> headersMap;
	
	private HashMap<String, String> cookies;
	
	/**
	 * Constructor principal, recoge el contexto, el tipo de conexion, la url y el handler al que volver�� cuando termine
	 * @param context
	 * @param method
	 * @param url
	 * @param handler
	 */
	public HttpConnection(Context context, int method, String url, Handler handler) {
		this.context = context;
		this.method = method;
		this.url = url != null ? url.replace(" ", "%20") : "";
		this.handler = handler;
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
		
		if(isImageConnection()){
			ConnectionManagerImages.getInstance().didComplete(this);	
		}else{
			ConnectionManager.getInstance().didComplete(this);	
		}	
	}
	
	public void setImageConnection(boolean imageConnection){
		this.imageConnection = imageConnection;
	}
	
	public boolean isImageConnection(){
		return imageConnection;
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
	 * 
	 * @return
	 */
	public List<IType> getTrees() {
		return trees;
	}

	/**
	 * Asigna el tipo de ArrayList de objeto que devolvera la conexion.
	 * @param trees
	 */
	public void setTrees(List<IType> trees) {
		this.trees = trees;
	}

	/**
	 * Devuelve las keys que se utilizaran para parsear el objeto que devuelve la conexion
	 * @return
	 */
	@Deprecated
	public Map<String, String> getKeys() {
		return keys;
	}

	/**
	 * Asigna las keys para parsear el objeto que devolvera la conexion
	 * @param keys
	 */
	@Deprecated
	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
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
	
	public void removeHeader(String key){
		if(headersMap != null){
			headersMap.remove(key);
		}
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
		if(isImageConnection()){
			ConnectionManagerImages.getInstance().push(this);
		}else{
			ConnectionManager.getInstance().push(this);
		}	
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
				sendMessage(handler, HttpConnection.DID_START, 0, new ConnectionResponse(url, null, this));
				httpClient = getClient();
				
				try {
					switch (method) {
					case GET:
						HttpGet httpGet = new HttpGet();
						addHeaders(httpGet);
						addGetParams(httpGet);

						processResponse(httpClient.execute(httpGet, getHttpContext()));
						break;

					case POST:
						HttpPost httpPost = new HttpPost();
						addHeaders(httpPost);
						addGetParams(httpPost);
						addPostParams(httpPost);

						processResponse(httpClient.execute(httpPost, getHttpContext()));
						break;

					case PUT:
						HttpPut httpPut = new HttpPut();
						addHeaders(httpPut);
						addGetParams(httpPut);
						addPostParams(httpPut);

						processResponse(httpClient.execute(httpPut, getHttpContext()));
						break;

					case DELETE:
						HttpDelete httpDelete = new HttpDelete();
						addHeaders(httpDelete);
						addGetParams(httpDelete);

						processResponse(httpClient.execute(httpDelete, getHttpContext()));
						break;
					}
				} catch (Exception e) {
					sendMessage(handler, HttpConnection.DID_ERROR, HttpConnection.STATUS_ERROR, new ConnectionException(url, e, tree, null, this));
				}
			} else {
				sendMessage(handler, HttpConnection.DID_ERROR, HttpConnection.STATUS_NO_CONNECTION, new ConnectionException(url, null, tree, null, this));
			}

			if(isImageConnection()){
				ConnectionManagerImages.getInstance().didComplete(this);	
			}else{
				ConnectionManager.getInstance().didComplete(this);	
			}	
		}
	}
	
	private HttpContext getHttpContext(){
		if(cookies != null){
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());

			return localContext;
		}else{
			return null;
		}
	}
	
	private CookieStore getCookieStore(){
		if(cookies != null){
			CookieStore cookieStore = new BasicCookieStore();
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), String.valueOf(entry.getValue()));
				cookie.setDomain(getDomainName(url));
				cookieStore.addCookie(cookie);
			}
			
			return cookieStore;
		}else{
			return null;
		}
	}
	
	private void addHeaders(HttpUriRequest request){
		if(headersMap != null && headersMap.size() > 0){
			for (Map.Entry<String, String> entry : headersMap.entrySet()) {
				request.setHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
	
		if(isEnableGzip()){
			request.addHeader(PARAM_ACCEPT_ENCODING, PARAM_GZIP);
		}
	}
	
	private void addGetParams(HttpRequestBase request) throws Exception{
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
			
			request.setURI(URI.create(builder.toString()));
		}else{
			request.setURI(URI.create(url));
		}
	}
	
	private void addMultipartFile(MultipartEntity entity, String key, String value){
		File file = new File(value);
		FileBody fileBody = new FileBody(file);
		
		entity.addPart(key, fileBody);
	}
	
	@SuppressWarnings("unchecked")
	private void addPostParams(HttpEntityEnclosingRequestBase request) throws Exception{
		if(parametersFileMap != null && parametersFileMap.size() > 0){
			MultipartEntity entity = new MultipartEntity();

			for (Map.Entry<String, Object> entry : parametersFileMap.entrySet()) {
				if(entry.getValue() instanceof String){
					addMultipartFile(entity, entry.getKey(), String.valueOf(entry.getValue()));
				}else if(entry.getValue() instanceof List){
					List<String> list = (List<String>) entry.getValue();
					for(int i=0; i<list.size(); i++){
						addMultipartFile(entity, entry.getKey() + "[" + i + "]", list.get(i));
					}
				}							
			}
			
			if(parametersPostMap != null && parametersPostMap.size() > 0){
				for (Map.Entry<String, String> entry : parametersPostMap.entrySet()) {
					entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}
			
			request.setEntity(entity);
		}else if (parametersPostMap != null && parametersPostMap.size() > 0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>(parametersPostMap.size());
			
			for (Map.Entry<String, String> entry : parametersPostMap.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));								
			}
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, encoding);
			
			request.setEntity(entity);
		}else if (data != null) {
			StringEntity entity = new StringEntity(data, encoding);
			
			request.setEntity(entity);
		}
	}

	/**
	 * Este m��todo procesa la respuesta http y devuelve mediante un handler si
	 * el resultado de la llamada ha sido satisfactorio o no, adem��s mediante
	 * message.obj se recupera el contenido y mediante message.arg1 se recupera
	 * el statusCode de la llamada
	 * 
	 * @param response
	 *            Es la respuesta de la request
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void processResponse(HttpResponse response) throws IllegalStateException, IOException {
		if(!isCancelled()){
			int statusCode = 0;
			try {
				if (this.response == true) {
					statusCode = response.getStatusLine().getStatusCode();
					sendMessage(handler, HttpConnection.DID_RESPONSE, statusCode, new ConnectionResponse(url, response, this));
				}else{
					if (this.header == true) {
						sendMessage(handler, HttpConnection.DID_HEADER, statusCode, new ConnectionResponse(url, response.getAllHeaders(), this));
					}

					statusCode = response.getStatusLine().getStatusCode();

					switch (statusCode) {
					case STATUS_CODE_OK:
					case STATUS_CODE_OK_ACCEPTED:
					case STATUS_CODE_OK_NO_CONTENT:
					case STATUS_CODE_OK_CREATED:
						switch (typeResult) {
						case XML:
							if (response.getEntity() != null && tree != null) {
								Parser parser = new Parser(keys);
								
								InputStream instream = getEntityContent(response);

								tree = (IType) parser.parseData(instream, tree);

								sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, tree, this));
							} else {
								String value = "";
								InputStream instream = getEntityContent(response);

								if (instream != null) {
									value = readContent(instream);
								}

								sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, value, this));
							}
							
							break;

						case JSON:
							if (response.getEntity() != null && tree != null) {
								String value = "";
								InputStream instream = getEntityContent(response);

								if (instream != null) {
									value = readContent(instream);
								}
								
			    	    		JSONParser<IType> jsonParser = new JSONParser<IType>();
			    	    		tree = jsonParser.parse(value, tree);

								sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, tree, this));
							}else if(response.getEntity() != null && trees != null){								
								String value = "";
								InputStream instream = getEntityContent(response);

								if (instream != null) {
									value = readContent(instream);
								}
								
			    	    		JSONParser<IType> jsonParser = new JSONParser<IType>();
			    	    		trees = jsonParser.parse(value);

								sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, trees, this));
							}else{
								String value = "";
								InputStream instream = getEntityContent(response);

								if (instream != null) {
									value = readContent(instream);
								}

								sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, value, this));
							}
							break;
							
						case BITMAP:
							if (response.getEntity() != null && response.getEntity().getContent() != null){
								sendMessage(handler, HttpConnection.DID_BITMAP, statusCode, new ConnectionResponse(url, BitmapFactory.decodeStream(response.getEntity().getContent()), this));
							}else{
								sendMessage(handler, HttpConnection.DID_BITMAP, statusCode, new ConnectionResponse(url, null, this));
							}
							
							break;
						default:
							String value = "";
							InputStream instream = getEntityContent(response);

							if (instream != null) {
								value = readContent(instream);
							}
							
							sendMessage(handler, HttpConnection.DID_SUCCEED, statusCode, new ConnectionResponse(url, value, this));
							
							break;
						}

						break;

					default:
						String value = "";
						InputStream instream = getEntityContent(response);

						if (instream != null) {
							value = readContent(instream);
						}

						sendMessage(handler, HttpConnection.DID_ERROR, statusCode, new ConnectionException(url, null, tree, value, this));

						break;
					}
				}
			} catch (Exception e) {
				sendMessage(handler, HttpConnection.DID_ERROR, statusCode, new ConnectionException(url, e, tree, null, this));
			}	
		}
	}
	
	public InputStream getEntityContent(HttpResponse response){
		InputStream instream = null;
		
		try{
			if(response.getEntity() != null){
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				    instream = new GZIPInputStream(response.getEntity().getContent());
				}else{
					instream = response.getEntity().getContent();
				}
			}
		}catch(Exception e){
			Functions.log(e);
		}
		
		return instream;
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

	/**
	 * Este m��todo crea un HttpClient que ha sido registrado para que tenga una
	 * conexi��n segura y no falle ni para llamadas http ni para llamadas https
	 * 
	 * @return Devuelve un HttpClient
	 */
	public DefaultHttpClient getClient() {
		DefaultHttpClient ret = null;

		// sets up parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, encoding);
		params.setBooleanParameter(PARAM_PROTOCOL_EXPECT_CONTINUE, false);
		
		HttpConnectionParams.setConnectionTimeout(params, HttpConnection.TIME_OUT);
		HttpConnectionParams.setSoTimeout(params, HttpConnection.TIME_OUT);

		// registers schemes for both http and https
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme(PARAM_HTTP, PlainSocketFactory.getSocketFactory(), PARAM_HTTP_PORT));
		registry.register(new Scheme(PARAM_HTTPS, new EasySSLSocketFactory(), PARAM_HTTPS_PORT));
		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
		ret = new DefaultHttpClient(manager, params);
		ret.setCookieStore(getCookieStore());
		return ret;
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
	
	/**
	 * Comprueba si internet esta funcionando o no, y devuelve un boolean true o false en 
	 * consecuencia
	 * @param context Contexto de la actividad
	 * @return Devuelve un boolean que indica el estado de la conexi��n
	 */
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
	
	
	/*
	 Gson gson = new Gson();

Type collectionType = new TypeToken<List<ContactDetail>>(){}.getType();

List<ContactDetail> details = gson.fromJson(json1, collectionType);

	 */
}
