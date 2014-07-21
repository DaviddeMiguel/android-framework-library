package com.framework.library.controller;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.framework.library.location.LastKnownLocation;
import com.framework.library.location.LocationListener;
import com.framework.library.location.LocationsManager;

public class LocationController extends Controller implements LocationListener{
	LocationsManager locationsManager;
	HandlerLocation handler;
	boolean locating;
	
	public LocationController(Activity activity){
		super(activity);
	}
	
	public LocationController(Context context) {
		super(context);
	}
	
	public LocationController(Fragment fragment) {
		super(fragment);
	}

	public LocationsManager getLocationsManager() {
		if(locationsManager == null){
			locationsManager = new LocationsManager(getContext());
			handler = new HandlerLocation(this);
		}
		
		return locationsManager;
	}

	public void setLocationsManager(LocationsManager locationsManager) {
		this.locationsManager = locationsManager;
	}
	
	public boolean isLocating() {
		return locating;
	}

	public void setLocating(boolean locating) {
		this.locating = locating;
	}

	public void startLocationManager(){
		setLocating(true);
		getLocationsManager().start(getContext(), handler);
	}
	
	public void stopLocationManager(){
		setLocating(false);
		getLocationsManager().stop();
	}
	
	public boolean isLastKnownLocation(int maxLocationTimeLimitMillis){
		LastKnownLocation lastKnownLocation = getLocationsManager().getLastKnownLocation(getContext());
		
		if(lastKnownLocation != null){
			Calendar calendar = Calendar.getInstance();
			
			if(calendar.getTimeInMillis() - lastKnownLocation.getTime() < maxLocationTimeLimitMillis){
				return true;
			}
		}
		
		return false;
	}
	
	@Override 
	public void onLocationChanged(Location location) {}

	@Override
	public void onLocationStatusChanged(int provider, int status, Bundle extras) {}

	@Override
	public void onLocationProviderEnabled(String provider) {}

	@Override
	public void onLocationProviderDisabled(String provider) {}
	
    /**
     * Handler que recibe la localización cuando esta es obtenida
     */
	static class HandlerLocation extends Handler{
		private final WeakReference<LocationController> mController;
		
		HandlerLocation(LocationController controller) {
			mController = new WeakReference<LocationController>(controller);
	    }
		
		@Override
		public void handleMessage(Message message){
			LocationController controller = mController.get();
			if (controller != null) {
				switch (message.what) {
				case LocationsManager.LOCATION_CHANGED:
					Location location = (Location) message.obj;
					controller.onLocationChanged(location);

					break;
				case LocationsManager.LOCATION_STATUS_CHANGED:
					controller.onLocationStatusChanged(message.arg1, message.arg2, (Bundle) message.obj);
					
					break;
				case LocationsManager.LOCATION_PROVIDER_ENABLED:
					controller.onLocationProviderEnabled((String) message.obj);
					
					break;
				case LocationsManager.LOCATION_PROVIDER_DISABLED:
					controller.onLocationProviderDisabled((String) message.obj);
					
					break;
				default:
					break;
				}
			}
		}
	}
}
