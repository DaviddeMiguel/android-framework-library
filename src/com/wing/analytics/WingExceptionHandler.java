package com.wing.analytics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 
 * @author dmiguel
 *
 * WingExceptionHandler recoge todos los cierres forzados de la aplicacion evitando que sea el sistema quien lo gestion,
 * el resultado es que en lugar de mostrar un dialogo que anima al usuario a forzar el cierre de la aplicacion redirige a 
 * la pantalla principal de nuestra aplicacion
 * 
 * http://developer.android.com/guide/faq/framework.html
 */
public class WingExceptionHandler implements UncaughtExceptionHandler{

	public static final String EXCEPTION = "EXCEPTION";
	
	private static final String FILE_NAME 		= "FileName = ";
	private static final String CLASS_NAME 		= "ClassName = ";
	private static final String LINE_NUMBER 	= "LineNumber = ";
	private static final String METHOD_NAME 	= "MethodName = ";
	
	private Context context;
	private static Intent intentCustom;
//	private UncaughtExceptionHandler uncaughtExceptionHandler;
	
	/**
	 * Crea una instancia del handler y guarda el contexto
	 * @param context Context de la actividad
	 */
	public WingExceptionHandler(Context context) {
		this.context = context;
//		uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	/**
	 * 
	 * @param thread Hilo del que proviene el cierre de la aplicacion
	 * @param ex Context Excepcion que se ha producido para el cierre de la aplicacion
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {	
		String exceptionMessage = getExceptionMessage(ex);
		if(exceptionMessage != null){
			Log.e("error:", exceptionMessage);
			//Wing.getInstance().onEvent(Wing.EXCEPTION, exceptionMessage);
		}

		//Wing.getInstance().sendPendingEvents();
		//Wing.getInstance().onEndSession(Wing.getInstance().getContext());
		
		PackageManager packageManager = context.getPackageManager(); 

		Intent intent = null;
		
		if(intentCustom == null){
			intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(EXCEPTION, exceptionMessage);
			context.startActivity(intent);
		}else{
			intentCustom.putExtra(EXCEPTION, exceptionMessage);
			context.startActivity(intentCustom);
		}

		Process.killProcess(Process.myPid());
		
//		uncaughtExceptionHandler.uncaughtException(thread, ex);
	}
	
	/**
	 * Crea un mensaje customizado para el reporte a Wing de la excepcion que se ha producido
	 * @param ex
	 * @return
	 */
	private String getExceptionMessage(Throwable ex){
		StringBuilder builder = new StringBuilder();
		if(ex.getMessage() != null){
			builder.append(ex.getMessage());
		}
		
		if(ex.getStackTrace() != null && ex.getStackTrace().length > 0){
			for(StackTraceElement element : ex.getStackTrace()){
				if(element.getFileName() != null){
					builder.append(FILE_NAME);
					builder.append(element.getFileName());
				}
				
				if(element.getClassName() != null){
					builder.append(CLASS_NAME);
					builder.append(element.getClassName());
				}
				
				builder.append(LINE_NUMBER);
				builder.append(element.getLineNumber());
				
				if(element.getMethodName() != null){
					builder.append(METHOD_NAME);
					builder.append(element.getMethodName());
				}
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Este metodo permite elegir a que pantalla debe ir la aplicacion cuando se haya producido una excepcion y la aplicacion
	 * tenga que cerrarse (Evitamos que se cierre y dirigimos al usuario donde nos interesa, ej. Ups, hubo un problema ;) )
	 * Por defecto cuando se produce un error de este tipo la libreria redirige al usuario a la pantalla principal de la aplicacion.
	 * @param intent
	 * @return Devuelve true si se ha guardado correctamente y false si no.
	 */
	public void setActivityForUncaughtExceptionIntent(Intent intent){
		intentCustom = intent;
	}
}