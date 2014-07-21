package com.wing.analytics;


import android.content.Context;
import android.content.Intent;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 
 * @author dmiguel
 *
 * Esta interfaz es una capa intermedia que se puede utilizar para crear librerias de estadisticas.
 * El origen de su desarrollo es poder crear una capa intermedia y poder reportar estadisticas de diferentes librerias 
 * al mismo tiempo.
 */
public abstract class IAnalytics {
	
	private Context context;
	private boolean enableUncaughtExceptionHandler = false;

	Context getContext() {
		return context;
	}
	
	/**
	 * Guarda el contexto y asigna por defecto el handler para las excepciones no recogidas
	 * @param context
	 */
	void setContext(Context context) {
		this.context = context;
		setUncaughtExceptionHandler(new WingExceptionHandler(context));
	}
	
	public void enableUncaughtExceptionHandler(boolean enable){
		enableUncaughtExceptionHandler = enable;
	}
	
	public boolean isEnableUncaughtExceptionHandler(){
		return enableUncaughtExceptionHandler;
	}
	
	/**
	 * Asigna el handler para las excepciones no recogidas
	 * @param uncaughtExceptionHandler
	 */
	public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler){
		if(isEnableUncaughtExceptionHandler()){
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
	}
	
	/**
	 * Este metodo permite elegir a que pantalla debe ir la aplicacion cuando se haya producido una excepcion y la aplicacion
	 * tenga que cerrarse (Evitamos que se cierre y dirigimos al usuario donde nos interesa, ej. Ups, hubo un problema ;) )
	 * Por defecto cuando se produce un error de este tipo la libreria redirige al usuario a la pantalla principal de la aplicacion.
	 * @param intent Intent que debe ejecutar
	 * @return Devuelve true si se ha guardado correctamente y false si no.
	 */
	public void setUncaughtExceptionHandlerIntent(Intent intent){
		if(isEnableUncaughtExceptionHandler()){
			UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			if(Thread.getDefaultUncaughtExceptionHandler() != null && uncaughtExceptionHandler instanceof WingExceptionHandler){
				((WingExceptionHandler)uncaughtExceptionHandler).setActivityForUncaughtExceptionIntent(intent);
			}	
		}
	}
	
	/**
	 * Este metodo seria llamado si queremos llevar un control estadistico de las instalaciones que tiene la aplicacion
	 * @param context
	 * @param keyId
	 */
	public void installEvent(Context context, String keyId){}
	/**
	 * Este metodo seria llamado si queremos llevar un control estadistico de las desinstalaciones que tiene la aplicacion
	 * @param context
	 * @param keyId
	 */
	public void uninstallEvent(Context context, String keyId){}
	
	/**
	 * Este metodo deberia ser llamado en el "onStart" de cada actividad para el correcto funcionamiento de la libreria
	 * @param context Es el contexto de la actividad
	 * @param keyId
	 */
	public abstract void onStartSession(Context context, String keyId);
	
	/**
	 * Este metodo deberia ser llamado en el "onStart" de cada actividad para el correcto funcionamiento de la libreria
	 * @param context Es el contexto de la actividad
	 * @param keyId
	 * * @param intent lugar donde redirigir en caso de excepcion incontrolada
	 */
	public abstract void onStartSession(Context context, String keyId, Intent intent);

	/**
	 * Este metodo deberia ser llamado en el "onStop" de cada actividad para el correcto funcionamiento de la libreria.
	 * Si la libreria detecta que se ha salido de la aplicacion enviara los eventos pendientes al servidor 
	 * @param context Es el contexto de la actividad
	 */
	public abstract void onEndSession(Context context);
	
	/**
	 * Registra un evento en el servidor
	 * @param eventId Nombre del evento
	 */
	public abstract void onEvent(String eventId);
	
	/**
     * Registra un evento en el servidor con una descripcion
     * @param eventId Nombre del evento
     * * @param description Descripcion del evento
     */
    public abstract void onEvent(String eventId, String description);

    /**
     * Registra un evento en el servidor con una descripcion
     * * @param context
     * @param eventId Nombre del evento
     * * @param description Descripcion del evento
     */
    public abstract void onEvent(Context context, String eventId, String description);
	
	/**
	 * Envia los eventos que estan guardados y aun no han sido enviados.
	 * No es necesario llamar a este metodo porque la libreria lo hace de forma automatica cuando el usuario se sale de la aplicacion
	 */
	public abstract void sendPendingEvents();
}
