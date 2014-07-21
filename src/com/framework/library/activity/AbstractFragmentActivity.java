package com.framework.library.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.framework.library.connection.HttpConnection;
import com.framework.library.controller.Controller;
import com.framework.library.util.Constants;
import com.framework.library.util.Functions;

public class AbstractFragmentActivity extends FragmentActivity{
	
	protected Activity activity;
	protected Context context;
	protected Toast toast;
	protected ProgressDialog progress;
	protected SharedPreferences settings;
	protected SharedPreferences.Editor editor;
	
	private Controller controller;

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			activity = this;
			context = this;
		}catch(Exception e){
			Functions.log(e);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(getController() != null){
			getController().setFinishing(true);
		}
	}
	
	protected SharedPreferences getSettings(){
		if(settings == null){
			settings = context.getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE);
		}
		
		return settings;
	}
	
	public SharedPreferences.Editor getEditor(){
		if(editor == null){
			if(settings == null){
				settings = context.getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE);
			}
			
			editor = settings.edit();
		}
		
		return editor;
	}
	
	public ProgressDialog createProgressDialog(String title, String message, boolean cancelable){
		progress = new ProgressDialog(context);
		progress.setTitle(title);
		progress.setMessage(message);
		progress.setCancelable(cancelable);
		
		return progress;
	}
	
	protected ProgressDialog getProgressDialog(){
		return progress;
	}
	
	public void gestionarErrorConexion(int error){	
		try{
			if(error == HttpConnection.STATUS_NO_CONNECTION){
				//MyAlertDialog.alertaMensajeTwoEvents(context, context.getString(R.string.application_name), context.getString(R.string.ERROR_CONECTION_NO_INTERNET), context.getString(R.string.ALERT_ACCEPT), context.getString(R.string.ALERT_CANCEL), internetSettingClickListener, cancelClickListener, MyAlertDialog.ALERTA).show();
			}else if(error == HttpConnection.STATUS_CODE_SERVICIO_NO_DISPONIBLE){
				//toast = Functions.toast(context, toast, context.getString(R.string.ERROR_CONECTION_SERVICE_UNAVAILABLE), Toast.LENGTH_SHORT);
			}else if(error == HttpConnection.STATUS_CODE_NO_AUTORIZADO || error == HttpConnection.STATUS_CODE_PROHIBIDO) {
				//toast = Functions.toast(context, toast, context.getString(R.string.ERROR_CONECTION_AUTHORITATION), Toast.LENGTH_SHORT);
			}else{
				//toast = Functions.toast(context, toast, context.getString(R.string.ERROR_CONECTION_DEFAULT), Toast.LENGTH_SHORT);
			}
		}catch(Exception e){
			Functions.log(e);
		}
	}
	
	public void buttonBack(View view){
		finish();
	}
	
	public void showProgressDialog(){
		try{
			if(progress != null && !progress.isShowing()) {
				progress.show();
			}
		}catch(Exception e){
			Functions.log(e);
		}
	}
	
	public void dismissProgressDialog(){
		try{
			if(progress != null && progress.isShowing()) {
				progress.dismiss();
			}
		}catch(Exception e){
			Functions.log(e);
		}
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
			finish();
		}
	};
}
