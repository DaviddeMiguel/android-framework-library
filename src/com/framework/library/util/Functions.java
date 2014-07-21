package com.framework.library.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.ParseException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.flurry.android.FlurryAgent;

public class Functions {

	private static boolean PRODUCCION 				= true;
	public static final boolean PRODUCCION_TRUE 	= true;
	public static final boolean PRODUCCION_FALSE 	= false;
	
	public static final int PREFERENCES_STRING								 = 0;
	public static final int PREFERENCES_INT									 = 1;
	public static final int PREFERENCES_BOOLEAN								 = 2;
	
	public static final String LAST_MODIFIED_HEADER 	= "Last-Modified";
	
	public static final String THREE_DOTS				= "...";
	
	public static final String PERFORM_DIAL_TEL			= "tel:";
	
	public static final String SIMPLE_DATE_FORMAT_PATTERN 		= "yyyy-MM-dd-HH.mm.ss.SSSSSS";
	public static final String SIMPLE_DATE_FORMAT_PATTERN_TEXT 	= "dd-MM-yyyy HH:mm:ss";
	
	public static final String FILE_TYPE_SHARE	= "text/plain";
	public static final String FILE_TYPE_PDF	= "application/pdf";
	public static final String FILE_TYPE_EMAIL	= "message/rfc822";

	
	
	public static final String PARENTHESIS_OPEN		= "(";
	public static final String PARENTHESIS_CLOSES	= ")";
	
	private static final String ERROR = "Error: ";
	
	public static final String UNKNOWN = "unknown";
	public static final int ICE_CREAM_SANDWICH = 14;
	
	/**
	 * Este m��todo decide si la aplicaci��n esta en modo producci��n o no
	 * @param (Functions.PRODUCCION_TRUE || PRODUCCION_FALSE)producci��n boolean para asignar a produccion
	 */
	public static void setProduccion(boolean produccion){
		PRODUCCION = produccion;
	}
	
	/**
	 * Devuelve el valor de la variable que indica si esta en modo producci��n.
	 * Si el valor es true se enviar��n datos a flurry y no habr�� logs,
	 * si el valor es false no se enviaran datos a flurry y habr�� logs.
	 * @return Devuelve el valor que tiene producci��n
	 */
	public static boolean isProduccion(){
		return PRODUCCION;
	}
	
	/**
	 * devuelve un entero que viene en los extras del intent
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un entero
	 */
	public static int getExtraInt(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getInt(key);
		}else{
			return -1;
		}
	}
	
	/**
	 * devuelve un @long que viene en los extras del intent
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hashmap que viene en el intent
	 * @return devuelve un @long
	 */
	public static long getExtraLong(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getLong(key);
		}else{
			return -1;
		}
	}
	
	public static float getExtraFloat(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getFloat(key);
		}else{
			return -1;
		}
	}
	
	/**
	 * devuelve un string que viene en los extras del intent 
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un string
	 */
	public static String getExtraString(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getString(key);
		}else{
			return "";
		}
	}
	
	/**
	 * devuelve un boolean que viene en los extras del intent 
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un boolean
	 */
	public static boolean getExtraBoolean(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getBoolean(key);
		}else{
			return false;
		}
	}
	
	/**
	 * devuelve un object que viene en los extras del intent 
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un object
	 */
	public static Object getExtraObject(String key, Bundle bundle){
		if(null != bundle){
			return bundle.get(key);
		}else{
			return null;
		}
	}
	
	/**
	 * devuelve un serializable que viene en los extras del intent 
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un serializable
	 */
	public static Serializable getExtraSerializableObject(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getSerializable(key);
		}else{
			return null;
		}
	}
	
	/**
	 * devuelve un Parcelabl que viene en los extras del intent 
	 * @param key clave para recuperar el valor
	 * @param bundle esto es el hasmap que viene en el intent
	 * @return devuelve un serializable
	 */
	public static Parcelable getExtraParcelableObject(String key, Bundle bundle){
		if(null != bundle){
			return bundle.getParcelable(key);
		}else{
			return null;
		}
	}
	
	/**
	 * redondea un numero a los decimales y al modo elegidos
	 * @param numero el numero que hay que redondear
	 * @param newScale el numero de decimales que va a dejar
	 * @param roundingMode es el modo en el que va a redondear (ejemplo: RoundingMode.UP)
	 * @return devuelve un double redondeado a su gusto
	 */
	public static  double roundDecimal(double numero, int newScale , int roundingMode){
		 String val = String.valueOf(numero);

		 BigDecimal big = new BigDecimal(val);

		 big = big.setScale(newScale, roundingMode);

	     return big.doubleValue();
	}
	
	/**
	 * Este metodo proporciona una cadena limitada al n��mero de car��cteres elegido como m��ximo
	 * y a��ade 3 puntos al final si se desea
	 * @param string cadena que se quiere cortar
	 * @param count numero de caracteres que debe tener la cadena de salida como maximo
	 * @param add si este parametro es true se le a��aden 3 puntos al final de la cadena
	 * @return devuelve una cadena con el numero de caracteres maximo elegido
	 */
	public static String substringCustom(String string, int count, boolean add){
		if(string.length() > count){
			if(add == true){
				return string.substring(0, count) + THREE_DOTS;
			}else{
				return string.substring(0, count);
			}
		}else{
			return string;
		}
	}
	
	/**
	 * Este m��todo devuelve la cadena que recibe sin car��cteres especiales, solo con n��meros y 
	 * letras
	 * @param string cadena que se quiere limpiar de caracteres especiales
	 * @return devuelve un string con solo car��cteres y numeros
	 */
	public static String customTrim(String string){
		String result = "";
		
		for(int i=0; i<string.length(); i++){

			if (Character.isLetter(string.charAt(i)) == true || Character.isDigit(string.charAt(i)) == true){
				result = result + string.charAt(i);
			}
		}
		
		return result;
	}
	
	/**
	 * Este m��todo pinta las trazas de la excepci��n 
	 * si esta en producci��n y la excepcion no es nula 
	 * @param e Es la excepci��n obtenida
	 */
	public static void log(Exception e){
		if(PRODUCCION == false && e != null){
			if(e.getStackTrace() != null){
				e.printStackTrace();
			}
			
			if(e.getMessage() != null){
				log(ERROR, e.getMessage());
			}
		}
	}
	
	/**
	 * Escribe en el log un mensaje y tag customizados
	 * @param appName Parametro que indica el tag o nombre de la app que saldr�� en el log
	 * @param log Es el mensaje que se quiere escribir en el log
	 */
	public static void log(String appName, String log){
		if(PRODUCCION == false){
			Log.d(appName, log);	
		}
	}
	
	/**
	 * Este m��todo muestra un toast, siempre reutiliza el mismo objeto toast para que en caso de
	 * que se est�� mostrando no se ponga en cola y elimine el actual y muestre el nuevo
	 * @param context Contexto del toast
	 * @param toast Toast que se quiere mostrar
	 * @param text Mensaje que mostrar�� el toast
	 * @param duration Duraci��n del toast
	 * @return Devuelve un toast para que siempre se reutilize el mismo objeto
	 */
	public static Toast toast(Context context, Toast toast, String text, int duration){
		if(toast != null){
			toast.cancel();
		}
		
		toast = Toast.makeText(context, text, duration);
		toast.show();
		
		return toast;
	}
	
	/**
	 * Guarda un valor deseado en preferencias
	 * @param context Contexto de la actividad
	 * @param preferences Nombre del archivo de preferencias
	 * @param mode Modo de preferencias, compartido o privado
	 * @param type Tipo de dato a guardar en prefenrencias
	 * @param key Clave para guardar y acceder
	 * @param valueString Valor string si corresponde
	 * @param valueInt Valor entero si corresponde
	 * @param valueBoolean Valor boolean si corresponde
	 * @return Devuelve true si se ha guardado con ��xito y false si ha fallado
	 */
	public static boolean savePreferences(Context context,String preferences, int mode, int type, String key, String valueString, int valueInt, boolean valueBoolean){
		try{
			SharedPreferences settings = context.getSharedPreferences(preferences, mode);
			SharedPreferences.Editor editor = settings.edit();
			
			boolean ok = false;
			
		    switch (type) {
	            case PREFERENCES_STRING:  
	            	editor.putString(key, valueString); 
	            	ok = true;
	            	break;
	            case PREFERENCES_INT:  
	            	editor.putInt(key, valueInt);     
	            	ok = true;
	            	break;
	            case PREFERENCES_BOOLEAN:  
	            	editor.putBoolean(key, valueBoolean);     
	            	ok = true;
	            	break;
	        }
		    
		    if(ok == false){
		    	return false;
		    }

			editor.commit();
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Recupera un valor de las preferencias, devuelve un Object al que se deber�� hacer un cast
	 * @param context Contexto de la actividad
	 * @param preferences Nombre del archivo de preferencias
	 * @param mode Modo de preferencias, compartido o privado
	 * @param type Tipo de dato a guardar en prefenrencias
	 * @param key Clave para guardar y acceder
	 * @return Devuelve un Object con el valor que se desea obtener
	 */
	public static Object restorePreferences(Context context, String preferences,int mode, int type, String key){
		Object object = null;
		
		try{
			SharedPreferences settings = context.getSharedPreferences(preferences, mode);
			
		    switch (type) {
	            case PREFERENCES_STRING:  
	            	object = settings.getString(key, "");
	            	break;
	            case PREFERENCES_INT:  
	            	object = settings.getInt(key, -1);
	            	break;
	            case PREFERENCES_BOOLEAN:  
	            	object = settings.getBoolean(key, false);
	            	break;
	        }

		}catch(Exception e){
			return null;
		}
		
		return object;
	}
	
	/**
	 * Muestra el splash el tiempo deseado, despues lo oculta y muestra el contenedor que contiene
	 * la verdadera actividad
	 * @param activity Actividad en la que est��
	 * @param splashId Id del splash view
	 * @param containerId Id del contenedor view principal
	 * @param splashTime Tiempo que permanecer�� el splash
	 */
//	public static void splash(final Activity activity, final int splashId, final int containerId, int splashTime){
//		View view = (View) activity.findViewById(splashId);
//		view.setVisibility(View.VISIBLE);
//		
//		view = (View) activity.findViewById(containerId);
//		view.setVisibility(View.GONE);
//		
//		TimerTask timertask = new TimerTask() {
//			@Override
//			public void run() {
//				handlerSplash.sendMessage(handlerSplash.obtainMessage(0, splashId, containerId, activity));
//			}
//		};
//
//		Timer timer = new Timer();
//		timer.schedule(timertask, splashTime);
//	}
//	
//	/**
//	 * Se oculta el splash y se muestra el contenedor principal
//	 */
//	private static Handler handlerSplash = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			int splashId = msg.arg1;
//			int containerId = msg.arg2;
//			
//			Activity activity = (Activity)msg.obj;
//			
//			View view = (View) activity.findViewById(splashId);
//			view.setVisibility(View.GONE);
//			
//			view = (View) activity.findViewById(containerId);
//			view.setVisibility(View.VISIBLE);
//		}
//	};
	
//	/**
//	 * Lanza el metodo start del flurry (solo se hara si esta en producci��n)
//	 * @param context Contexto de la actividad
//	 * @param flurryId Id del flurry
//	 * @param versionName Nombre de la versi��n que aparecer�� en el flurry
//	 */
//	public static void flurryStart(final Context context, final String flurryId, final String versionName){
//		if(PRODUCCION == true){
//			Thread thread = new Thread(new Runnable() {	
//				@Override
//				public void run() {
//					FlurryAgent.setVersionName(versionName);
//					FlurryAgent.onStartSession(context, flurryId);
//				}
//			});
//			
//			thread.start();
//		}
//	}
//	
//	/**
//	 * Para el flurry (solo se hara si esta en producci��n)
//	 * @param context Contexto de la actividad
//	 */
//	public static void flurryStop(final Context context){
//		if(PRODUCCION == true){
//			Thread thread = new Thread(new Runnable() {	
//				@Override
//				public void run() {
//					FlurryAgent.onEndSession(context);
//				}
//			});
//			
//			thread.start();
//		}
//	}
//	
//	/**
//	 * Manda un evento a flurry (solo se hara si esta en producci��n)
//	 * @param eventName Nombre del evento que se manda a flurry
//	 */
//	public static void flurryOnEvent(final String eventName){
//		if(PRODUCCION == true){
//			Thread thread = new Thread(new Runnable() {	
//				@Override
//				public void run() {
//					FlurryAgent.onEvent(eventName);
//				}
//			});
//			
//			thread.start();
//		}
//	}
//	
	/**
	 * Este m��todo sirve para comprobar la ��ltima modificaci��n de un recurso que haya en el
	 * servidor, esto servir�� para saber si ha cambiado desde la ��ltima vez que se pidi�� o no
	 * y en funci��n de esto se vuelve a pedir o no
	 * @param actualUrl Url del recurso
	 * @return Devuelve un string con la fecha de la ��ltima modificaci��n
	 */
	public static String getLastModified(String url){
		String lastModified = "";
		try{
			HttpHead httpHead = new HttpHead(new URL(url).toURI());
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpClient.execute(httpHead);
			Header header = response.getLastHeader(LAST_MODIFIED_HEADER);
			lastModified = header.getValue();   	
		}catch(Exception e){
			Functions.log(e);
		}
		
		return lastModified;
    }
	
	/**
	 * Este m��todo devuelve la fecha de hoy customizada
	 * @return Devuelve un string con la fecha
	 */
	public static String getActualDateString(){
		String result = "";
		Date date = new Date();
		result = result + String.valueOf(date.getYear());
		result = result + String.valueOf(date.getMonth());
		result = result + String.valueOf(date.getDay());
		result = result + String.valueOf(date.getHours());
		result = result + String.valueOf(date.getMinutes());
		
		return result;
	}
	
	/**
	 * Este m��todo crea un peque��o xml con el tag y el contenido deseado
	 * @param openTag Tag de apertura
	 * @param closeTag Tag que cierra
	 * @param content Contendido del xml
	 * @return
	 */
	public static final String getXmlString(String openTag, String closeTag, String content){
		return openTag + content + closeTag;
	}
	
	/**
	 * Comprueba si internet esta funcionando o no, y devuelve un boolean true o false en 
	 * consecuencia
	 * @param context Contexto de la actividad
	 * @return Devuelve un boolean que indica el estado de la conexi��n
	 */
	public static boolean checkInternetConnection(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    // test for connection
	    if (cm.getActiveNetworkInfo() != null && 
	    	cm.getActiveNetworkInfo().isAvailable() && 
	    	cm.getActiveNetworkInfo().isConnected()) {
	        return true;
	    } else {
	    	Functions.log("", "");
	        return false;
	    }
	}
	
	/**
	 * Comprueba si wifi esta operativo y devuelve un boolean en consecuencia
	 * @param context Contexto de la actividad
	 * @return Devuelve un boolean tras comprobar si el wifi esta activo y conectando o conectado
	 */
	public static boolean checkWifiConnected(Context context){
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	
		if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
		    return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Comprueba si internet m��vil esta operativo y devuelve un boolean en consecuencia
	 * @param context Contexto de la actividad
	 * @return Devuelve un boolean tras comprobar si el internet m��vil esta activo y 
	 * conectando o conectado
	 */
	public static boolean checkMobileConnected(Context context){
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		//mobile
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

		if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
			return true;
		}else{
			return false;
		}
	}
	
    /**
	 * Comprueba si hay conexi��n a internet
	 * @return true si hay conexi��n, false en caso contrario
	 */
	public static boolean isConnected(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Procesa la respuesta del servidor desde un HttpResponse y devuelve la un string con el resultado
	 * @param response Respuesta http
	 * @return String con el valor de la respuesta
	 */
	public static String processResponse(HttpResponse response){
		StringBuilder total = null;
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			total = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null){
				total.append(line);
			}
		}catch(Exception e){
			Functions.log(e);
		}

		if(total != null){
			return total.toString();
		}else{
			return null;
		}	
	}
	
    /**
     * Procesa la respuesta obtenida tras una petici��n HTTP desde un InputStream
     * @param input
     * @return Un String con la respuesta
     */
    public static String processResponse(InputStream input){
    	StringBuilder total = null;
  	  
    	try{
    		BufferedReader br = new BufferedReader(new InputStreamReader(input));
    		total = new StringBuilder();
    		String line;
    		while ((line = br.readLine()) != null){
    			total.append(line);
    		}
    	}catch(Exception e){
    		Functions.log(e);
    	}

    	if(total != null){
    		return total.toString();
    	}else{
    		return null;
    	}
    	
    }
	
	/**
     * Devuelve un bitmap redimensionado
     * @param bitmap Imagen a redimensionar
     * @param newHeight Nueva altura
     * @return Imagen redimensionada
     */
    /* PROBLEMAS DE MEMORIA
     * 
    public static Bitmap resizeBitmap(Bitmap bitmap, int newHeight){   	
    	Bitmap bitmapResized = null;
    	
    	if(bitmap != null){
    		
    		if(bitmap.getHeight() > newHeight){
    	
		    	int width = bitmap.getWidth();
		        int height = bitmap.getHeight();
		       
		        // calculate the scale - in this case = 0.4f
		        float scale = (float) newHeight / height;
		        	       
		        // create a matrix for the manipulation
		        Matrix matrix = new Matrix();
		        
		        // resize the bit map
		        matrix.postScale(scale, scale);
		 
		        // recreate the new Bitmap
		        bitmapResized = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    		}
    		else{
    			return bitmap;
    		}
    	}

        return bitmapResized;
    }  
	*/	
    
    public static Bitmap decodeFileBitmap(File f, int imageMaxSize){
        Bitmap b = null;
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > imageMaxSize || o.outWidth > imageMaxSize) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(imageMaxSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
        }
        return b;
    }
    
    /**
     * 
     * @param context Contexto de la actividad
     * @param number Numero al que se va a llamar
     * @param performDialConstantTel (Functions.PERFORM_DIAL_TEL) Constante que va unida al numero para lanzar la app del tel��fono 
     */
	public static void performDial(Context context, String number, String performDialConstantTel){
		if(number!=null){
			try {
				if(performDialConstantTel == null || performDialConstantTel.equals("") == true){
					performDialConstantTel = PERFORM_DIAL_TEL;
				}
				context.startActivity(new Intent(Intent. ACTION_DIAL, Uri.parse(performDialConstantTel + number)));
			} catch (Exception e) {
				Functions.log(e);
			}
		}
	}
	
	/**
	 * El m��todo devuelve el path de la imagen que se desea
	 * @param activity Actividad actual
	 * @param contentUri Uri de la imagen
	 * @return devuelve un string con el path de la imagen
	 */
	@Deprecated
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		String [] proj      = {MediaStore.Images.Media.DATA};
		Cursor cursor       = activity.managedQuery( contentUri, proj, null, null,null);
	 
		if (cursor == null){
			return null;
		}
	 
		int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	 
		cursor.moveToFirst();
	 
		if(cursor.getString(column_index) != null){
			return cursor.getString(column_index);
		}else{
			return "";
		}
	}
	
	public static String getRealPathFromUri(Activity activity, Uri contentURI) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else { 
            cursor.moveToFirst(); 
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
            
            return cursor.getString(idx); 
        }
    }
	
	/**
	 * Devuelve el directorio si existe y si no lo crea y lo devuelve igualmente
	 * @param context Contexto de la actividad
	 * @param directory Nombre del directorio
	 * @return Devuelve el directorio
	 */
	public static File createDir(Context context, String directory){
		File fileDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
        	fileDir = new File(android.os.Environment.getExternalStorageDirectory(), directory);
        }else{
        	fileDir=context.getCacheDir();
        }
            
        if(!fileDir.exists()){
        	fileDir.mkdirs();	
        }
        
        return fileDir;
	}
	
	/**
	 * 
	 * @param context Contexto de la actividad
	 * @param directory Directorio
	 * @param fileName Nombre del archivo
	 * @return Devuelve un File del archivo
	 */
	public static File getFile(Context context, File directory, String fileName){
		File file = new File(directory, fileName);
		return file;
	}
	
	/**
	 * Escribe un archivo con el nombre, modo y contenido deseados
	 * @param activity Actividad actual
	 * @param fileName Nombre de archivo
	 * @param mode (ejemplo: Activity.MODE_WORLD_READABLE) Modo de escritura y lectura del archivo
	 * @param content Contenido que se escribira en el archivo
	 * @return Devuelve un boolean que indica c��mo fue el proceso
	 */
	public static boolean writeFile(Activity activity, String fileName, int mode, String content){
		boolean ok = false;
		
		try { 			
			// ##### Write a file to the disk #####
			/* We have to use the openFileOutput()-method
			 * the ActivityContext provides, to
			 * protect your file from others and
			 * This is done for security-reasons.
			 * We chose MODE_WORLD_READABLE, because
			 *  we have nothing to hide in our file */             
			FileOutputStream fileOutputStream = activity.openFileOutput(fileName, mode);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream); 
			
			// Write the string to the file
			outputStreamWriter.write(content);
			/* ensure that everything is
			 * really written out and close */
			outputStreamWriter.flush();
			outputStreamWriter.close();
			
			ok = true;
		}catch(IOException e){
			Functions.log(e);
		}catch(Exception e){
			Functions.log(e);
		}
		
		return ok;
	}
	
	/**
	 * Este m��todo lee un archivo y devuelve su contenido
	 * @param activity Actividad actual
	 * @param fileName Nombre del archivo
	 * @return devuelve un string con el contenido del archivo
	 */
	public static String readFile(Activity activity, String fileName){
		String string = "";

		try {
			FileInputStream fileInputStream;
			fileInputStream = activity.openFileInput(fileName);
			string = Functions.processResponse(fileInputStream);
		}catch(FileNotFoundException e){
			Functions.log(e);
		}catch(Exception e){
			Functions.log(e);
		}

		return string;
	}
	
	/**
	 * Elimina el archivo
	 * @param activity Actividad actual
	 * @param fileName Nombre del archivo
	 * @return devuelve un boolean de como ha ido el proceso
	 */
	public static boolean deleteFile(Activity activity, String fileName){
		return activity.deleteFile(fileName);
	}
	
	/**
	 * 
	 * @param dateString String de la fecha
	 * @param pattern (Functions.SIMPLE_DATE_FORMAT_PATTERN) Patr��n para parsear la fecha
	 * @return Devuelve un objeto date
	 */
	public static Date parseDate(String dateString, String pattern){
		Date date = null;
		
		if(dateString != null && dateString.equals("") == false){			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			
			try {
				date = simpleDateFormat.parse(dateString);
			}catch (ParseException e){
				Functions.log(e);
			}catch(Exception e){
				Functions.log(e);
			}
		}
		
		return date;
	}
	
	/**
	 * 
	 * @param dateString String de la fecha
	 * @param patternDate (Functions.SIMPLE_DATE_FORMAT_PATTERN) Patr��n para parsear la fecha
	 * @param patternText (Functions.SIMPLE_DATE_FORMAT_PATTERN_TEXT) Patr��n para dar formato texto a la fecha
	 * @return
	 */
	public static String parseDate(String dateString, String patternDate, String patternText) {
		if(dateString != null && dateString.equals("") == false){			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patternDate);
			SimpleDateFormat simpleDateFormatText = new SimpleDateFormat(patternText);
			
			try {
				Date parsedDate = simpleDateFormat.parse(dateString);
				return simpleDateFormatText.format(parsedDate) ;     
			}catch (ParseException e){
				Functions.log(e);
			}catch(Exception e){
				Functions.log(e);
			}    
		}
		
		return "";
	}
	
	/**
	 * Formatea una fecha pasada por par?metro al formato indicado.
	 * @param date
	 * @param dateFormat
	 * @return fecha formateada.
	 */
	public static String formatDate(Date date, SimpleDateFormat dateFormat){		
		return dateFormat.format(date);		
	}
	
	public static String stringBetweenParentheses(String string){
		return PARENTHESIS_OPEN + string + PARENTHESIS_CLOSES;
	}
	
	/**
	 * M���todo que oculta el teclado
	 * @param context Contexto
	 * @param v Vista que tiene el foco actual. (La que mantiene el teclado abierto)
	 */
	public static void hideKeyboard(Context context, View v){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	/**
	 * Convierte DIP a P��xeles
	 * @param context
	 * @param dip Valor que se pasar��� a p���xeles
	 * @return El valor convertido a p���xeles
	 */
	public static int dipToPixels(Context context, int dip){
		Resources r = context.getResources();
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	}
	
	/**
	 * Escribe un archivo con el nombre, modo y contenido deseados
	 * @param activity Actividad actual
	 * @param fileName Nombre de archivo
	 * @param mode (ejemplo: Activity.MODE_WORLD_READABLE) Modo de escritura y lectura del archivo
	 * @param content Contenido que se escribira en el archivo
	 * @return Devuelve un boolean que indica c��mo fue el proceso
	 */
	public static boolean writeObjInternalMemory(Activity activity, String objName, int mode, Object object){
		boolean ok = false;
		
		try { 			
			// ##### Write a file to the disk #####
			/* We have to use the openFileOutput()-method
			 * the ActivityContext provides, to
			 * protect your file from others and
			 * This is done for security-reasons.
			 * We chose MODE_WORLD_READABLE, because
			 *  we have nothing to hide in our file */             
			FileOutputStream fileOutputStream = activity.openFileOutput(objName, mode);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream); 
			
			// Write the string to the file
			objectOutputStream.writeObject(object);
			/* ensure that everything is
			 * really written out and close */
			objectOutputStream.flush();
			objectOutputStream.close();
			
			ok = true;
		}catch(IOException e){
			Functions.log(e);
		}catch(Exception e){
			Functions.log(e);
		}
		
		return ok;
	}
	
	/**
	 * Este m��todo lee un archivo y devuelve su contenido
	 * @param activity Actividad actual
	 * @param fileName Nombre del archivo
	 * @return devuelve un string con el contenido del archivo o null si no existe.
	 */
	public static Object readObjInternalMemory(Activity activity, String objName){
		Object obj = null;

		try {
			FileInputStream fileInputStream = activity.openFileInput(objName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			
			obj = objectInputStream.readObject();
		}catch(FileNotFoundException e){
			Functions.log(e);
		}catch(Exception e){
			Functions.log(e);
		}

		return obj;
	}
	
	/**
	 * Elimina el archivo
	 * @param activity Actividad actual
	 * @param fileName Nombre del archivo
	 * @return devuelve un boolean de como ha ido el proceso
	 */
	public static boolean deleteObjInternalMemory(Activity activity, String objName){
		return activity.deleteFile(objName);
	}
	
	
	/**
	 * Check if it is an email address
	 */
	public static final Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

	public static boolean isSameDay(Calendar calendar1, Calendar calendar2){
		boolean sameDay = false;
		
		if(calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
		   calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
		   calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
			sameDay = true;
		}
		
		return sameDay;
	}
	
	public static int getMinutesBetween(Calendar calendar1, Calendar calendar2){
		return getMinutes(calendar1.getTimeInMillis()) - getMinutes(calendar2.getTimeInMillis());
	}
	
	/**
	 * int seconds = (int) (milliseconds / 1000) % 60 ;
	 * @param milliseconds
	 * @return
	 */
	public static int getSeconds(long milliseconds){
		return (int) (milliseconds / 1000) % 60 ;
	}
	
	/**
	 * int minutes = (int) ((milliseconds / (1000*60)) % 60);
	 * @param milliseconds
	 * @return
	 */
	public static int getMinutes(long milliseconds){
		return (int) ((milliseconds / (1000*60)) % 60);
	}
	
	/**
	 * int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	 * @param milliseconds
	 * @return
	 */
	public static int getHours(long milliseconds){
		return (int) ((milliseconds / (1000*60*60)) % 24);
	}
	
    /**
     * 
     * @param context Contexto de la actividad
     * @param number Numero al que se va a llamar
     * @param performDialConstantTel (Functions.PERFORM_DIAL_TEL) Constante que va unida al numero para lanzar la app del tel��fono 
     */
	public static void intentPhoneCall(Context context, String number, String performDialConstantTel){
		if(number!=null){
			try {
				if(performDialConstantTel == null || performDialConstantTel.equals("") == true){
					performDialConstantTel = PERFORM_DIAL_TEL;
				}
				context.startActivity(new Intent(Intent. ACTION_DIAL, Uri.parse(performDialConstantTel + number)));
			} catch (Exception e) {
				Functions.log(e);
			}
		}
	}
	
	public static void intentBrowser(Context context, String url){
		try {
			if(url != null && !url.equals("")){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				context.startActivity(intent);
			}
		} catch (Exception e) {
			Functions.log(e);
		}
	}
	
	public static void intentInternetSettings(Context context){
		context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}
	
	public static void intentInternetSettingsResult(Activity activity, int requestCode){
		activity.startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), requestCode);
	}
	
	public static void intentLocationSettings(Context context){
		context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	public static void intentLocationSettingsResult(Activity activity, int requestCode){
		activity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode);
	}
	
	/**
	 * Lanza una aplicaci��n donde pueda verse un archivo .pdf
	 * @param activity Activity actual
	 * @param directory Nombre del directorio
	 * @param fileName Nombre del archivo
	 * @param fileType (Functions.FILE_TYPE_PDF) Tipo de archivo
	 * @return devuelve un boolean con el resultado del proceso
	 */
	public static boolean showPdf(Activity activity, String directory, String fileName, String fileType){
		boolean ok = false;
		
    	File dir = Functions.createDir(activity, directory);

    	File file = Functions.getFile(activity, dir, fileName);
    	if(file.exists() == true){
            try {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, fileType);
                activity.startActivity(intent);
                
                ok = true;
            }catch (ActivityNotFoundException e){
                Functions.log(e);
            }catch(Exception e){
            	Functions.log(e);
            }
    	}
    	
    	return ok;
	}
	
	public static void intentShare(Context context, String dialogTitle, String type, String subject, String message){
		//create the send intent  
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);  
		  
		//set the type  
		//shareIntent.setType("text/plain");  
		intent.setType(type);    
		
		//add a subject  
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);    
		  
		//add the message  
		intent.putExtra(android.content.Intent.EXTRA_TEXT, message);  
		  
		//start the chooser for sharing  
		context.startActivity(Intent.createChooser(intent, dialogTitle));  
	}
	
	public static void intentShare(Context context, String fileType, String[] recipient, String subject, String body, String titleChooser, String noEmailAppInstalledMessage){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(fileType);
		intent.putExtra(Intent.EXTRA_EMAIL  , recipient);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT   , body);
		try {
		    context.startActivity(Intent.createChooser(intent, titleChooser));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(context, noEmailAppInstalledMessage, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Example bundle:
	 * 	intent.putExtra(Intent.EXTRA_EMAIL  , recipient);
	 *	intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	 *	intent.putExtra(Intent.EXTRA_TEXT   , body);
	 *  intent.putExtra(Intent.EXTRA_STREAM   , uri);
	 */
	
	public static void intentShare(Context context, String dialogTitle, String type, Bundle bundle){
		//create the send intent  
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);  
		  
		//set the type  
		//shareIntent.setType("text/plain");  
		intent.setType(type);    
  
		intent.putExtras(bundle);
		  
		//start the chooser for sharing  
		context.startActivity(Intent.createChooser(intent, dialogTitle));  
	}
	
	
	public static void intentShare(Context context, String fileType, Bundle bundle, String titleChooser, String noEmailAppInstalledMessage){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(fileType);
		intent.putExtras(bundle);

		try {
		    context.startActivity(Intent.createChooser(intent, titleChooser));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(context, noEmailAppInstalledMessage, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Default prefix = "sms:"
	 * Default separator = ";"
	 * ExtraBody = "sms_body"
	 * @param context
	 * @param prefix
	 * @param separator
	 * @param recipients
	 * @param extraBody
	 * @param body
	 * @param noAppInstalledAvailableMessage
	 */
	public static void intentShareSMS(Context context, String prefix, String separator, String[] recipients, String extraBody, String body, String noAppInstalledAvailableMessage){
		String defaultPrefix = "sms:";
		String defaultSeparator = "; ";
		String defaultExtraBody = "sms_body";
		
		String constraintSamsung = "samsung";
		
		if(prefix != null && !prefix.equals("")){
			defaultPrefix = prefix;
		}
		
		if(separator != null && !separator.equals("")){
			defaultSeparator = separator;
		}else if(android.os.Build.MANUFACTURER.toLowerCase().contains(constraintSamsung)){
			defaultSeparator = ", ";
		}
		
		if(extraBody != null && !extraBody.equals("")){
			defaultExtraBody = extraBody;
		}
		
		StringBuilder recipient = new StringBuilder();
		recipient.append(defaultPrefix);
		
		for(int i=0; i<recipients.length; i++){
			recipient.append(recipients[i]);
			
			if(i < recipients.length - 1){
				recipient.append(defaultSeparator);
			}
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(recipient.toString()));
		intent.putExtra(defaultExtraBody, body);
		
		try {
		    context.startActivity(intent);
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(context, noAppInstalledAvailableMessage, Toast.LENGTH_SHORT).show();
		} catch(Exception e){
		    Toast.makeText(context, noAppInstalledAvailableMessage, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void intentPlayStore(Context context) {
		try {
		    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getPackageName())));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+context.getPackageName())));
		}
	}
	
	/**
	 * Con este m��todo se manda un email con un array de archivos adjuntos
	 * (Ejemplo: startActivity(Intent.createChooser(emailIntent, "TITULO DIALOGO"));)
	 * @param context Contexto de la actividad
	 * @param action_send tipo de envio, ejemplo (Intent.ACTION_SEND o Intent.ACTION_SEND_MULTIPLE)
	 * @param directories Nombre de los directorios
	 * @param fileNames Nombres de los archivos
	 * @param fileType (Functions.FILE_TYPE_EMAIL) Tipo de archivo
	 * @param email Email de destino
	 * @param cc En copia
	 * @param text Texto para el email
	 * @return Devuelve un intent que deber�� ser lanzado desde la actividad
	 */
	public static Intent intentShare(Context context, String action_send, List<String> directories, List<String> fileNames, String emailTitle, String fileType, String email, String cc, String text){
    	//need to "send multiple" to get more than one attachment
    	Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    	emailIntent.setType(fileType);
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
    	emailIntent.putExtra(android.content.Intent.EXTRA_CC, cc);
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);

    	if(directories != null && fileNames != null){
        	//has to be an ArrayList
        	ArrayList<Uri> uris = new ArrayList<Uri>();
        	
        	for(int i=0; i<fileNames.size(); i++){
        		File dir = createDir(context, directories.get(i));
            	File file = getFile(context, dir, fileNames.get(i));
            	
            	Uri u = Uri.fromFile(file);
            	uris.add(u);
        	}
        	
        	emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    	}
    	
    	return emailIntent;
    }
	
	/**
	 * 
	 * @param context
	 * @param timeInMillis
	 * @param typeTime beginTime o endTime
	 * @return
	 */
	public static void intentCalendar(Context context){
		try{
		    Intent calendarIntent = new Intent() ;
		    calendarIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    
		    if(Build.VERSION.SDK_INT >= 8){
		    	calendarIntent.setClassName("com.google.android.calendar","com.android.calendar.LaunchActivity");
		    }else{
		    	calendarIntent.setClassName("com.android.calendar","com.android.calendar.LaunchActivity");
		    } 
		    
		    context.startActivity(calendarIntent); 
		}catch(Exception e) {
			Functions.log(e);
		}
    }
	
    /**
     * ContactsContract.Contacts.CONTENT_TYPE
     * ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
     * ContactsContract.CommonDataKinds.Email.CONTENT_TYPE
     * 
     * 
     * @param context
     */
    public static void intentPickContacts(Activity activity, int requestCode, String contentType){
    	Intent intent = new Intent (Intent.ACTION_PICK);
    	intent.setType(contentType); 
    	activity.startActivityForResult(intent, requestCode); 
    }
	
	// NO FUNCIONA
//	
//	public static void intentCalendarEventInfo(Context context, long timeInMillis, String typeTime){
//		try{
//		    Intent calendarIntent = new Intent() ;
//		    calendarIntent.putExtra(typeTime, timeInMillis);
//		    calendarIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		    
//		    //calendarIntent.setData(data);
//		    if(Build.VERSION.SDK_INT >= 8){
//		    	calendarIntent.setClassName("com.google.android.calendar","com.android.calendar.EventInfoActivity");
//		    	calendarIntent.setData(Uri.parse("content://com.android.calendar/calendars"));
//		    }else{
//		    	calendarIntent.setClassName("com.android.calendar","com.android.calendar.EventInfoActivity");
//		    	calendarIntent.setData(Uri.parse("content://calendar/calendars"));
//		    } 
//		    
//		    context.startActivity(calendarIntent); 
//		}catch(Exception e) {
//			Functions.log(e);
//		}
//    }

    /**
     * Comprueba si la aplicacion estaba visible o vuelve de background
     */
	public static boolean isApplicationRunning(Context context, String packageName) {
		boolean visible = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcInfo = activityManager.getRunningAppProcesses();
		for (int i = 0; i < runningProcInfo.size(); i++) {
			if (runningProcInfo.get(i).processName.equals(packageName)) {
				if (runningProcInfo.get(i).importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					visible = true;
				}else{
					visible = false;
				}
			}
		}
		
		return visible;
	}
	
    /**
     * Comprueba si la aplicacion estaba visible o vuelve de background
     */
	public static boolean isApplicationRunning(Context context) {
		boolean visible = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcInfo = activityManager.getRunningAppProcesses();
		for (int i = 0; i < runningProcInfo.size(); i++) {
			if (runningProcInfo.get(i).processName.equals(context.getPackageName())) {
				if (runningProcInfo.get(i).importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					visible = true;
				}else{
					visible = false;
				}
			}
		}
		
		return visible;
	}
	
	/**
	 * No esta comprobado que funcione
	 * @param context
	 * @param serviceClassName
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceClassName){
	    final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

	    for (RunningServiceInfo runningServiceInfo : services) {
	        if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
	            return true;
	        }
	    }
	    return false;
	 }
	
	public static String getKeystoreSignature(Context context, String packageName){
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
	
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String keystoreSignature = new String(Base64.encode(md.digest(), 0));
				//String something = new String(Base64.encodeBytes(md.digest()));
				return keystoreSignature;
			} 
		}catch (NameNotFoundException e) {
			log(e);
		}catch (NoSuchAlgorithmException e) {
			log(e);
		}catch (Exception e){
			log(e);
		}
		
		return null;
	}
	
	public static String getIMEI(Context context){
		String result = "";
		  
		if(Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.FROYO){  // Mayor de API level 8
			result = android.os.Build.SERIAL;
		}
		
		if(TextUtils.isEmpty(result) || result.equalsIgnoreCase(UNKNOWN)){
			result = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
	
		// NECESITA ESTE PERMISO: <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
/*
		if(TextUtils.isEmpty(result) || result.equalsIgnoreCase(UNKNOWN)){
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			result = wifiInfo.getMacAddress();
		}
*/
		
		return result;
	}
	
	public static void hideKeyBoard(Context context, EditText editText){
		InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	/**
	 * Pone el texto al TextView siempre que no sea @null ni est�� 
	 * vac��o de ser as��, hace el TextView invisible.
	 * @param textView
	 * @param text
	 */
	public static void setText(TextView textView, String text){
		if(text != null && !text.equals("")){
			textView.setText(Html.fromHtml(text));
			textView.setVisibility(View.VISIBLE);
		}else{
			textView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Asigna recursivamente una fuente a todos los hijos del viewgroup proporcionado
	 * @param mContainer
	 * @param mFont
	 */
	public static final void setFontApp(ViewGroup mContainer, Typeface mFont){
	    if (mContainer == null || mFont == null) return;

	    final int mCount = mContainer.getChildCount();

	    // Loop through all of the children.
	    for (int i = 0; i < mCount; ++i){
	    	
	        final View mChild = mContainer.getChildAt(i);
	        if (mChild instanceof TextView){
	            // Set the font if it is a TextView.
	            ((TextView) mChild).setTypeface(mFont);
	        }else if (mChild instanceof ViewGroup){
	            // Recursively attempt another ViewGroup.
	            setFontApp((ViewGroup) mChild, mFont);
	        }
	    }
	}
	
	/**
	 * Asigna recursivamente una fuente a todos los hijos del view proporcionado
	 * @param mContainer
	 * @param mFont
	 */
	public static final void setFontApp(View mContainer, Typeface mFont){
	    if (mContainer == null || mFont == null) return;

	    if(mContainer instanceof ViewGroup){
		    final int mCount = ((ViewGroup)mContainer).getChildCount();

		    // Loop through all of the children.
		    for (int i = 0; i < mCount; ++i){
		    	
		        final View mChild = ((ViewGroup)mContainer).getChildAt(i);
		        if (mChild instanceof TextView){
		            // Set the font if it is a TextView.
		            ((TextView) mChild).setTypeface(mFont);
		        }else if (mChild instanceof ViewGroup){
		            // Recursively attempt another ViewGroup.
		            setFontApp((ViewGroup) mChild, mFont);
		        }
		    }
	    }
	}
	
	/**
	 * Asigna una fuente a la vista proporcionada
	 * @param view
	 * @param mFont
	 */
	public static final void setFont(View view, Typeface mFont){
	    if (view == null || mFont == null) return;
	    
	    ((TextView) view).setTypeface(mFont);
	}
	
	/**
	 * Carga la direcci��n pasada por par��metro en el navegador
	 * @param context
	 * @param url
	 */
	public static void loadURL(Context context, String url){		
		String mUrl = url;
		
		if(!mUrl.startsWith("http://") && !mUrl.startsWith("https://")){
			mUrl = "http://" + mUrl;
		}
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
		context.startActivity(browserIntent);
	}
	
	/**
	 * Vibra el telefono el tiempo pasado como parametro.
	 * Para que funcione tiene que tener permisos de vibracion
	 * @param context
	 * @param milliseconds
	 */
	public static void vibrate(Context context, long milliseconds){
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); 
		vibrator.vibrate(milliseconds);
	}
	
	/**
	 * Vibra el telefono siguiendo un patron pasado como parametro. 
	 * En el array interpreta que el primer valor es lo que tiene que esperar antes de vibrar y el segundo el tiempo que tiene que
	 * estar vibrando.
	 * Para que funcione tiene que tener permisos de vibracion
	 * @param context
	 * @param pattern
	 * @param repeat Este parametro es el index dentro del array donde quiere que se vuelva a repetir (-1 si no queremos repeticion)
	 */
	public static void vibrate(Context context, long[] pattern, int repeat){
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); 
		vibrator.vibrate(pattern, repeat);
	}
	
	public static void vibrateCancel(Context context){
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); 
		vibrator.cancel();
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
	
	public static boolean match(Pattern pattern, String string){
		Matcher matcher = pattern.matcher(string);//replace with string to compare
		if(matcher.find()) {
			return true;
		}else{
			return false;
		}
	}
	
	public static DisplayMetrics getScreenSize(Context context){
	    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    DisplayMetrics displaymetrics = new DisplayMetrics();
	    windowManager.getDefaultDisplay().getMetrics(displaymetrics);
	    
	    return displaymetrics;
	}
	
	public static void setAdapter(AbsListView list, ListAdapter adapter){
		if(list instanceof ListView){
			((ListView) list).setAdapter(adapter);
		}else if(list instanceof GridView){
			((GridView) list).setAdapter(adapter);
		}
	}
	
	
//	public static String hash_hmac(String type, String value, String key){
//		try {
//			javax.crypto.Mac mac = javax.crypto.Mac.getInstance(type);
//			javax.crypto.spec.SecretKeySpec secret = new javax.crypto.spec.SecretKeySpec(key.getBytes(), type);
//			mac.init(secret);
//			byte[] digest = mac.doFinal(value.getBytes());
//			StringBuilder sb = new StringBuilder(digest.length*2);
//			String s;
//			for (byte b : digest){
//				s = Integer.toHexString(intval(b));
//				if(s.length() == 1) sb.append('0');
//				sb.append(s);
//			}
//			return sb.toString();
//		} catch (Exception e) {
//			android.util.Log.v("TAG","Exception ["+e.getMessage()+"]", e);
//		}
//		return "";
//	}
//	
//	public void hashmac(String type, String value, String key){
//        try {
//            Mac mac = Mac.getInstance("HmacSHA1");
//            SecretKeySpec secret = new SecretKeySpec(key.getBytes("UTF-8"), mac.getAlgorithm());
//            mac.init(secret);
//            byte[] digest = mac.doFinal(value.getBytes());
//
//            String enc = new String(digest);
//
//            // Base 64 Encode the results
//            String retVal = Base64.encodeToString(enc.getBytes(), Base64.NO_WRAP);
//            Log.v("hashmac", "String: " + value);
//            Log.v("hashmac", "key: " + key);
//            Log.v("hashmac", "result: " + retVal);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//	}
	
	
}
