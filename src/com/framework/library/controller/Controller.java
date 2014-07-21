package com.framework.library.controller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.framework.library.connection.ConnectionInterfaces.ConnectionListener;
import com.framework.library.exception.BackgroundException;
import com.framework.library.exception.ConnectionException;
import com.framework.library.model.BackgroundResponse;
import com.framework.library.model.ConnectionResponse;
import com.framework.library.util.AppIntent;
import com.framework.library.util.BackgroundInterfaces.BackgroundListener;
import com.framework.library.util.BackgroundInterfaces.BackgroundMode;
import com.framework.library.util.Constants;
import com.framework.library.util.Constants.AppType;
import com.framework.library.util.Functions;
import com.framework.library.util.MediaManager;
import com.framework.library.util.TaskManager;
import com.framework.library.util.TaskManagerSequential;

public class Controller implements BackgroundMode, BackgroundListener, ConnectionListener{

	public static final String METHOD_IS_APPLICATION_VISIBLE_OR_BACKGROUND	= "isApplicationVisibleOrBackground";
	
	Activity activity;
	Context context;
	Fragment fragment;
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	HandlerBackground handler;
	
	boolean finishing = false;
	private boolean appVisible = false;
	
	private boolean backPressedDouble = false;
	
	@SuppressWarnings("unused")
	private Controller(){}
	
	public Controller(Activity activity){
		setActivity(activity);
	}
	
	public Controller(Context context){
		setContext(context);
	}
	
	public Controller(Fragment fragment){
		setFragment(fragment);
		setActivity(fragment.getActivity());
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		setContext(activity);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public SharedPreferences getSettings(){
		if(settings == null){
			settings = context.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
		}
		
		return settings;
	}
	
	public SharedPreferences.Editor getEditor(){
		if(editor == null){
			if(settings == null){
				settings = context.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
			}
			
			editor = settings.edit();
		}
		
		return editor;
	}
	
	@Override
	public void onConnectionStart(ConnectionResponse response) {}
	@Override
	public void onConnectionComplete(ConnectionResponse response) {}
	@Override
	public void onConnectionError(ConnectionException e) {}
	@Override
	public void onConnectionResponse(ConnectionResponse response) {}
	@Override
	public void onConnectionHeader(ConnectionResponse response) {}
	@Override
	public void onConnectionBitmap(ConnectionResponse response) {}

	/**
	 * Al terminar la ejecucion en segundo plano avisa al listener de que ha terminado, tambien recibe los datos en caso de que sea
	 * una funcion.
	 */
	@Override
	public void onBackgroundComplete(BackgroundResponse response) {}

	/**
	 * Recoge la excepcion que ha habido al ejecutar el metodo en segundo plano.
	 */
	@Override
	public void onBackgroundError(BackgroundException exception) {}

	/**
	 * Este metodo hara que la instruccion elegida se ejecute en background, si la instruccion elegida es una funcion necesitara el listener
	 * para poder devolver el resultado obtenido, en caso de ser un void solo lo ejecutara si no tengamos que esperar respuesta.
	 * @param listener [BackgroundListener] Este listener recibira los datos al acabar el proceso.
	 * @param methodName Nombre del metodo que se desea ejecutar en segundo plano.
	 * @param object Instancia del objeto que contiene el metodo (Normalmente sera un controller).
	 * @param params Array de parametros que recibe el metodo.
	 * @param classes Array de clases de los parametros que recibe el metodo.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void runInBackground(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes){
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(listener != null){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, listener)));
					}	
				}finally{
					TaskManager.getInstance().didComplete(this);
					if(!error && listener != null){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, listener)));
					}
				}
			}
		};
		
		TaskManager.getInstance().push(runnable);
	}
	
	@SuppressWarnings("rawtypes")
	public void runInBackground(final String methodName, final Object object, final Object[] params, final Class[] classes, final String methodCallback, final String methodCallbackError){
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				Class<?> returnType = null;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					returnType = method.getReturnType();
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(methodCallbackError != null && !methodCallbackError.equals("")){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, methodCallbackError, object)));
					}
				}finally{
					TaskManager.getInstance().didComplete(this);
					if(!error && methodCallback != null && !methodCallback.equals("")){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, methodCallback, returnType, object)));
					}
				}
			}
		};
		
		TaskManager.getInstance().push(runnable);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void runInBackgroundSequential(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes) {
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(listener != null){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, listener)));
					}	
				}finally{
					TaskManagerSequential.getInstance().didComplete(this);
					if(!error && listener != null){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, listener)));
					}
				}
			}
		};
		
		TaskManagerSequential.getInstance().push(runnable);
	}
	
	/**
	 * 		runInBackgroundSequential(
				  "viewed", 
				  controller, 
				  new Object[]{controller.getAnnounce().getId()}, 
				  new Class[]{long.class}, 
				  "viewedAdded", 
				  "viewedError");
	 * @param methodName
	 * @param object
	 * @param params
	 * @param classes
	 * @param methodCallback
	 * @param methodCallbackError
	 */
	@SuppressWarnings("rawtypes")
	public void runInBackgroundSequential(final String methodName, final Object object, final Object[] params, final Class[] classes, final String methodCallback, final String methodCallbackError){
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				Class<?> returnType = null;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					returnType = method.getReturnType();
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(methodCallbackError != null && !methodCallbackError.equals("")){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, methodCallbackError, object)));
					}	
				}finally{
					TaskManagerSequential.getInstance().didComplete(this);
					if(!error && methodCallback != null && !methodCallback.equals("")){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, methodCallback, returnType, object)));
					}
				}
			}
		};
		
		TaskManagerSequential.getInstance().push(runnable);
	}


	@Override
	@SuppressWarnings("rawtypes")
	public void runInBackgroundSequential(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes, final int priority) {
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(listener != null){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, listener)));
					}	
				}finally{
					TaskManagerSequential.getInstance().didComplete(this);
					if(!error && listener != null){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, listener)));
					}
				}
			}
		};
		
		TaskManagerSequential.getInstance().push(runnable, priority);
	}
	
	@SuppressWarnings("rawtypes")
	public void runInBackgroundSequential(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes, final int priority, final String methodCallback, final String methodCallbackError){
		if(handler == null){
			handler = new HandlerBackground(this);
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object result = null;
				Method method = null;
				boolean error = false;
				Class<?> returnType = null;
				
				try {
					method = object.getClass().getMethod(methodName, classes);
					
					returnType = method.getReturnType();
					result = method.invoke(object, params);
				} catch (Exception e){
					error = true;
					if(methodCallbackError != null && !methodCallbackError.equals("")){
						handler.sendMessage(handler.obtainMessage(1, new BackgroundException(methodName, e, methodCallbackError, object)));
					}	
				}finally{
					TaskManagerSequential.getInstance().didComplete(this);
					if(!error && methodCallback != null && !methodCallback.equals("")){
						handler.sendMessage(handler.obtainMessage(0, new BackgroundResponse(methodName, result, methodCallback, returnType, object)));
					}
				}
			}
		};
		
		TaskManagerSequential.getInstance().push(runnable, priority);
	}
	
	/**
     * Handler que devuelve al hilo principal cuando termina un proceso en background
     */
	static class HandlerBackground extends Handler{
		private final WeakReference<Controller> mController;
		
		HandlerBackground(Controller controller) {
			mController = new WeakReference<Controller>(controller);
	    }

		@Override
		public void handleMessage(Message message){
			Controller controller = mController.get();
			if (controller != null) {
				switch (message.what) {
				case 0:
					BackgroundResponse response = (BackgroundResponse) message.obj;
					if(response.getListener() != null){
						response.getListener().onBackgroundComplete(response);
					}else{
						Method method = null;
						
						try {
							if(response.getMethodParam() != null){
								method = response.getObjectCallback().getClass().getMethod(response.getMethodCallback(), new Class[]{response.getMethodParam()});
							}else{
								method = response.getObjectCallback().getClass().getMethod(response.getMethodCallback(), new Class[]{});
							}
							
							method.invoke(response.getObjectCallback(), response.getData());
						} catch (Exception e){
							Functions.log(e);
						}
					}
					
					break;

				case 1:
					BackgroundException exception = (BackgroundException) message.obj;
					if(exception.getListener() != null){
						exception.getListener().onBackgroundError(exception);
					}else{
						Method method = null;
						
						try {
							if(exception.getException() != null){
								method = exception.getObjectCallback().getClass().getMethod(exception.getMethodCallbackError(), new Class[]{Exception.class});
							}else{
								method = exception.getObjectCallback().getClass().getMethod(exception.getMethodCallbackError(), new Class[]{});
							}
							
							method.invoke(exception.getObjectCallback(), exception.getException());
						} catch (Exception e){
							Functions.log(e);
						}
					}
					
					break;
				}
			}
		}
	}
	
	/**
	 * Comprueba si la aplicacion estaba visible o vuelve de background.
	 * La respuesta llega al metodo onBackgroundComplete o onBackgroundError con el metodo METHOD_IS_APPLICATION_VISIBLE_OR_BACKGROUND.
	 * @param dialog
	 * @param context
	 */
	public void isApplicationVisible(){
		runInBackground(this, METHOD_IS_APPLICATION_VISIBLE_OR_BACKGROUND, this, null, null);
	}
	
	/**
	 * Este metodo comprueba si la aplicacion estaba visible o viene del background.
	 * (Recomendado utilizar isApplicationVisible() y esperar la respuesta del background.
	 * @return
	 */
	public boolean isApplicationVisibleOrBackground(){
		setAppVisible(Functions.isApplicationRunning(context));
		return isAppVisible();
	}
	
	public boolean isFinishing() {
		return finishing;
	}

	public void setFinishing(boolean finishing) {
		this.finishing = finishing;
	}
	
	public static int getVersionSDK(){
		return Build.VERSION.SDK_INT;
	}
	
	public static String getVersionSDKString(){
		return String.valueOf(Build.VERSION.SDK_INT);
	}

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
//            throw new RuntimeException("Could not get package name: " + e);
        }
        
        return 0;
    }
	
	public static boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		return list.size() > 0;
	} 
	
	public static Intent getIntentIfAvailable(Context context, String packageName){
		Intent intent = null;
		PackageManager manager = context.getPackageManager();
		try {
			intent = manager.getLaunchIntentForPackage(packageName);
		    if (intent == null){
//		        throw new PackageManager.NameNotFoundException();
		    }else{
		    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
		    }	
		} catch (Exception e) {}
		
		return intent;
	}
	
	public Intent openApp(AppType type, Bundle bundle){
		Intent intent = null;
		Intent intentResult = null;
		int intentResultCode = -1;
		
		String uri;
		switch (type) {
		case BROWSER:
			uri = makeUri(new String[]{"http://", "https://"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			//Uri.parse("http://www.vogella.com")
					
			break;
	    case PHONE_CALL:
	    	uri = makeUri(new String[]{"tel:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
			//Uri.parse("tel:(+49)12345789")
			
			break;
	    case PHONE_DIAL:
	    	uri = makeUri(new String[]{"tel:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
			//Uri.parse("tel:(+49)12345789")
			
			break;
	    case PHONE_VIEW:
	    	uri = makeUri(new String[]{"tel:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			//Uri.parse("tel:(+49)12345789")
			
			break;
	    case SMS:
	    	uri = makeUri(new String[]{"sms:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.putExtra(AppIntent.EXTRA_SMS_BODY, bundle.getString(AppIntent.EXTRA_SMS_BODY)); 
			//Uri.parse("tel:(+49)12345789")
			
			break;
	    case MAPS:
	    	uri = makeUri(new String[]{"geo:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	    	//Uri.parse("geo:50.123,7.1434?z=19")

			break;
	    case MAPS_QUERY:
	    	uri = makeUri(new String[]{"geo:"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	    	//Uri.parse("geo:0,0?q=query")

			break;
	    case CONTACTS:
	    	uri = makeUri(new String[]{"content://"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	    	//Uri.parse("content://contacts/people/")

			break;
	    case CONTACTS_EDIT:
	    	uri = makeUri(new String[]{"content://"}, bundle.getString(AppIntent.EXTRA_URI));
			intent = new Intent(Intent.ACTION_EDIT, Uri.parse(uri));
	    	//Uri.parse("content://contacts/people/1")

	      break;
	    case EMAIL:
			intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, bundle.getStringArray(AppIntent.EXTRA_EMAIL));
			intent.putExtra(Intent.EXTRA_CC, bundle.getStringArray(AppIntent.EXTRA_CC));
			intent.putExtra(Intent.EXTRA_BCC, bundle.getStringArray(AppIntent.EXTRA_BCC));  
			intent.putExtra(Intent.EXTRA_SUBJECT, bundle.getString(AppIntent.EXTRA_SUBJECT));  
			intent.setType(bundle.getString(AppIntent.EXTRA_TYPE));  
			intent.putExtra(android.content.Intent.EXTRA_TEXT, bundle.getString(AppIntent.EXTRA_TEXT));

	      break;
	    case EMAIL_FILE:
	    	intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	    	intent.putExtra(Intent.EXTRA_EMAIL, bundle.getStringArray(AppIntent.EXTRA_EMAIL));
			intent.putExtra(Intent.EXTRA_CC, bundle.getStringArray(AppIntent.EXTRA_CC));
			intent.putExtra(Intent.EXTRA_BCC, bundle.getStringArray(AppIntent.EXTRA_BCC));  
			intent.putExtra(Intent.EXTRA_SUBJECT, bundle.getString(AppIntent.EXTRA_SUBJECT));  
			intent.setType(bundle.getString(AppIntent.EXTRA_TYPE));  
			intent.putExtra(android.content.Intent.EXTRA_TEXT, bundle.getString(AppIntent.EXTRA_TEXT));
			
			ArrayList<Uri> uris = new ArrayList<Uri>();
			String[] paths = bundle.getStringArray(AppIntent.EXTRA_STREAM);
        	if(paths != null){
        		for(String path: paths){
        			uris.add(Uri.parse(path));
        		}
        		
        		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        	}

        	break;	
		    case SHARE:
				intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(AppIntent.EXTRA_SMS_BODY, bundle.getString(AppIntent.EXTRA_SMS_BODY));
				intent.putExtra(Intent.EXTRA_EMAIL, bundle.getString(AppIntent.EXTRA_EMAIL));  
				intent.putExtra(Intent.EXTRA_CC, bundle.getString(AppIntent.EXTRA_CC));  
				intent.putExtra(Intent.EXTRA_BCC, bundle.getString(AppIntent.EXTRA_BCC));  
				intent.putExtra(Intent.EXTRA_SUBJECT, bundle.getString(AppIntent.EXTRA_SUBJECT));  
				intent.setType(bundle.getString(AppIntent.EXTRA_TYPE));  
				intent.putExtra(android.content.Intent.EXTRA_TEXT, bundle.getString(AppIntent.EXTRA_TEXT));
				
			break;
		    case GALLERY:
		    	intentResult = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		    	intentResultCode = AppIntent.RESULT_GALLERY;
		    	break;
		    	
		    case PICK_IMAGE:
		    	intentResult = new Intent(Intent.ACTION_GET_CONTENT);
		    	intentResult.setType("image/*");
		    	intentResultCode = AppIntent.RESULT_PICK_IMAGE;
		    	break;
		    
		    case VIDEO:
		    	intentResult = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

		        Uri fileVideoUri = MediaManager.getOutputMediaFileUri(MediaManager.MEDIA_TYPE_VIDEO);  // create a file to save the video
		        intentResult.putExtra(MediaStore.EXTRA_OUTPUT, fileVideoUri);  // set the image file name
		        intentResult.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		        intentResult.putExtra(AppIntent.EXTRA_URI, fileVideoUri);
		        
		        intentResultCode = AppIntent.RESULT_VIDEO;
		        break;
		    case CAMERA:
		    	intentResult = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    	
		        Uri fileImageUri = MediaManager.getOutputMediaFileUri(MediaManager.MEDIA_TYPE_IMAGE);  // create a file to save the video
		        intentResult.putExtra(MediaStore.EXTRA_OUTPUT, fileImageUri);
		        intentResult.putExtra(AppIntent.EXTRA_URI, fileImageUri);
		        
		        intentResultCode = AppIntent.RESULT_CAMERA;
		        break;  
		        
		    case CHOOSER_IMAGE:
		    	List<Intent> intents = new ArrayList<Intent>();
		    	
		    	Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        Uri fileImageCameraUri = MediaManager.getOutputMediaFileUri(MediaManager.MEDIA_TYPE_IMAGE);  // create a file to save the video
		        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileImageCameraUri);
		        
		        intents.add(intentCamera);
		        
		        Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		        intentResult = Intent.createChooser(intentGallery, "Select Source");
		    	intentResult.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[]{}));
		    	intentResult.putExtra(AppIntent.EXTRA_URI, fileImageCameraUri);
		    	
		    	intentResultCode = AppIntent.RESULT_PICK_IMAGE;
		        break;  
	    }

        try {
        	if (intent != null) {
        		if(fragment != null){
        			fragment.startActivity(intent);
        		}else{
        			context.startActivity(intent);
        		}
        			
        		return intent;
        	}else if(intentResult != null && getActivity() != null){
        		if(fragment != null){
        			fragment.startActivityForResult(intentResult , intentResultCode);
        		}else{
        			getActivity().startActivityForResult(intentResult , intentResultCode);
        		}
        		return intentResult;
        	}
        	
        	
        	
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, bundle != null && bundle.getString(AppIntent.EXTRA_FEATURE_MESSAGE) != null ? bundle.getString(AppIntent.EXTRA_FEATURE_MESSAGE) : "Feature not available", Toast.LENGTH_SHORT).show();
        }
	    
	    return null;
	}
	
	public String makeUri(String[] pattern, String content){
		if(pattern != null && pattern.length > 0){
			boolean found = false;
			for(int i=0; i<pattern.length; i++){
				if (content.startsWith(pattern[i])){
					found = true;
					break;
				}
			}
			
			if(!found){
				return pattern[0] + content;
			}
		}
		
		return content;
	}
	
	public boolean isAppVisible() {
		return appVisible;
	}

	public void setAppVisible(boolean appVisible) {
		this.appVisible = appVisible;
	}
	
	public boolean isDebug(){
		return (getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}
	
	public OnClickListener internetSettingClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Functions.intentInternetSettings(context);
		}
	};
	
	public OnClickListener locationSettingClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Functions.intentLocationSettings(context);
		}
	};
	
	public OnClickListener cancelClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			getActivity().finish();
		}
	};
	
	public void onBackPressed(long delayMillis){
        backPressedDouble = true;
        
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            	backPressedDouble = false;   

            }
        }, delayMillis);
	}

	public boolean isBackPressedDouble() {
		return backPressedDouble;
	}

	public void setBackPressedDouble(boolean backPressedDouble) {
		this.backPressedDouble = backPressedDouble;
	}
}
