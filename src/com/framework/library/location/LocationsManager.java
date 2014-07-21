package com.framework.library.location;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.framework.library.memory.ObjectCache;

public class LocationsManager {
	
	public final static String ADDRESS_DEFAULT = "(%1$f,%2$f)";
	public static final String LAST_KNOWN_LOCATION = "last_known_location";
	
	public static final int LOCATION_CHANGED = 0;
	public static final int LOCATION_STATUS_CHANGED = 1;
	public static final int LOCATION_PROVIDER_ENABLED = 2;
	public static final int LOCATION_PROVIDER_DISABLED = 3;
	public static final int LOCATION_TIME_OUT = 4;
	public static final int LOCATION_REQUEST_START = 5;
	public static final int ALL_LOCATION_PROVIDERS_DISABLED = 6;
	
	public static final int GPS_PROVIDER 		= 0;
	public static final int NETWORK_PROVIDER 	= 1;
		
	LocationManager locationManager;
	LocationListener locationListener;
	static LocationHandler mHandler;
	Context mContext;
	
	String bestProvider;
	
	private boolean callBackEnabled = false;
	private List<LocationsListener> mListners;
	
	boolean criteriaAltitude = false;
	int criteriaAccuracy = Criteria.ACCURACY_COARSE;
	boolean criteriaBearing = false;
	boolean criteriaCost = false;
	int criteriaPower = Criteria.NO_REQUIREMENT;
	boolean criteriaSpeed = false;
	
	long minTimeLocation = 1000;
	float minDistanceLocation = 10;
	
	public LocationsManager(Context context){
		this(context, null);
	};
	
	/**
	 * @param context Contexto de la actividad
	 * @param listener Listener en el que se notificarán los eventos de la localización.
	 */
	public LocationsManager(Context context, LocationsListener listener) {
		mContext = context;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mListners = new ArrayList<LocationsManager.LocationsListener>();
		mHandler = new LocationHandler(this);
		
		mListners.add(listener);
	}
	
	public void addListener(LocationsListener listener) {
		mListners.add(listener);
	}
	
	public void removeListener(LocationsListener listener) {
		mListners.remove(listener);
	}
	
	public void start(final Context context, final Handler handler) {
		start(context, handler, 0);
	}
	
	public void start(final Context context, final Handler handler, long timeOut){
		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				if(callBackEnabled == true){
					saveLocation(context, location);
					
					// Called when a new location is found by the location provider.
					handler.sendMessage(handler.obtainMessage(LOCATION_CHANGED, location));	
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				if(callBackEnabled == true){
					if(provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER) == true){
						handler.sendMessage(handler.obtainMessage(LOCATION_STATUS_CHANGED, GPS_PROVIDER, status, extras));
					}else if(provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER) == true){
						handler.sendMessage(handler.obtainMessage(LOCATION_STATUS_CHANGED, NETWORK_PROVIDER, status, extras));
					}		
				}
			}
			
			public void onProviderEnabled(String provider) {
				if(callBackEnabled == true){
					handler.sendMessage(handler.obtainMessage(LOCATION_PROVIDER_ENABLED, provider));	
				}
			}
			
			public void onProviderDisabled(String provider) {
				if(callBackEnabled == true){
					handler.sendMessage(handler.obtainMessage(LOCATION_PROVIDER_DISABLED, provider));	
				}
			}
		};

		// Register the listener with the Location Manager to receive location updates		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeLocation, minDistanceLocation, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeLocation, minDistanceLocation, locationListener);	
        
        Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(criteriaAltitude);
		criteria.setAccuracy(criteriaAccuracy);
		criteria.setBearingRequired(criteriaBearing);
		criteria.setCostAllowed(criteriaCost);
		criteria.setPowerRequirement(criteriaPower);
		criteria.setSpeedRequired(criteriaSpeed);
				
		bestProvider = locationManager.getBestProvider(criteria, true);
		
		callBackEnabled = true;
		
		if (timeOut > 0) {
			handler.sendEmptyMessageDelayed(LOCATION_TIME_OUT, timeOut);
		}
		
	}
	
	/**
	 * Por favor, sea consciente que para utilizar este métode debe de haber utilizado 
	 * el constructor no deprecado. {@link LocationsManager#LocationsManager(Context, LocationsListener) LocationsManager(Context, LocationsListener) }
	 * @param timeOut tiempo máximo (en milisegundos) que se quiere esperar una localización. 
	 * Pasado ese tiempo se devolverá la lacalización anterior o null en caso de
	 * que no se conozca ninguna ubicación del usuario.
	 * <p>Para esperar de forma indefinida pasar como parámetro el valor 0.
	 */
	public void start(long timeOut) {
		
		mHandler.sendMessage(mHandler.obtainMessage(LOCATION_REQUEST_START));	
		
		// Define a listener that responds to location updates
		if(locationListener == null) {
			locationListener = new LocationListener() {
				
				public void onLocationChanged(Location location) {
					if(callBackEnabled == true){
						saveLocation(mContext, location);
						
						// Called when a new location is found by the location provider.
						mHandler.sendMessage(mHandler.obtainMessage(LOCATION_CHANGED, location));	
					}
				}
	
				public void onStatusChanged(String provider, int status, Bundle extras) {
					if(callBackEnabled == true){
						if(provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER) == true){
							mHandler.sendMessage(mHandler.obtainMessage(LOCATION_STATUS_CHANGED, GPS_PROVIDER, status, extras));
						}else if(provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER) == true){
							mHandler.sendMessage(mHandler.obtainMessage(LOCATION_STATUS_CHANGED, NETWORK_PROVIDER, status, extras));
						}		
					}
				}
				
				public void onProviderEnabled(String provider) {
					if(callBackEnabled == true){
						mHandler.sendMessage(mHandler.obtainMessage(LOCATION_PROVIDER_ENABLED, provider));	
					}
				}
				
				public void onProviderDisabled(String provider) {
					if(callBackEnabled == true){
						mHandler.sendMessage(mHandler.obtainMessage(LOCATION_PROVIDER_DISABLED, provider));	
					}
				}
			};
		}
		
		if(!isAnyProvidersEnabled()) {
			mHandler.sendMessage(mHandler.obtainMessage(ALL_LOCATION_PROVIDERS_DISABLED));	
		} else {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeLocation, minDistanceLocation, locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeLocation, minDistanceLocation, locationListener);
		}
        
        Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(criteriaAltitude);
		criteria.setAccuracy(criteriaAccuracy);
		criteria.setBearingRequired(criteriaBearing);
		criteria.setCostAllowed(criteriaCost);
		criteria.setPowerRequirement(criteriaPower);
		criteria.setSpeedRequired(criteriaSpeed);
				
		bestProvider = locationManager.getBestProvider(criteria, true);
		
		callBackEnabled = true;
		
		if (timeOut > 0) {
			mHandler.sendEmptyMessageDelayed(LOCATION_TIME_OUT, timeOut);
		}
		
	}
	
	public void stop(Handler handler) {
		handler.removeMessages(LOCATION_TIME_OUT);
		locationManager.removeUpdates(locationListener);
		callBackEnabled = false;
	}
	
	/**
	 * Remove location listener
	 */
	public void stop() {
		if(locationListener != null) locationManager.removeUpdates(locationListener);
		callBackEnabled = false;
		locationListener = null;
	}
	
	protected void removeLocationListener(){
		locationManager.removeUpdates(locationListener);
		callBackEnabled = false;
	}
	
	/**
	 * Guarda una localización como la ultima, por defecto cada vez que se recupera una con esta clase se guarda y sobreescribe la que haya.
	 * @param context
	 * @param location
	 * @return
	 */
	public boolean saveLocation(Context context, Location location){		
		if(context != null){
			LastKnownLocation lastKnownLocation = new LastKnownLocation(location);
			return ObjectCache.getInstance().saveObject(context, LAST_KNOWN_LOCATION, lastKnownLocation);
		}else{
			return false;
		}
	}
	
	/**
	 * Devuelve la ultima localizacion que se haya obtenido mediante LocationsManager
	 * @param context
	 * @return
	 */
	@Deprecated
	public LastKnownLocation getLastKnownLocation(Context context){
		return (LastKnownLocation) ObjectCache.getInstance().getObject(context, LAST_KNOWN_LOCATION);
	}
	
	/**
	 * Devuelve la ultima localizacion que se haya obtenido mediante LocationsManager
	 * @param context
	 * @return
	 */
	public LastKnownLocation getLastKnownLocation(){
		return (LastKnownLocation) ObjectCache.getInstance().getObject(mContext, LAST_KNOWN_LOCATION);
	}
	
	/**
	 * 
	 * @param provider LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER
	 * @return Location Last known location
	 */
	public Location getLastKnownLocation(Context context, String provider){
		try{			
			if(provider != null){
				return locationManager.getLastKnownLocation(provider);
			}else{
				return null;
			}
		}catch(Exception e){
			return null;
		}
	}
	
	public static double fromDegreesToRadians(double n) {
		try{
			return n * Math.PI / 180;
		}catch(Exception e){
			return 0;
		}
	}
	
	public static int fromDegreesToE6(double n){
		try{
			return (int)(n * 1e6);
		}catch(Exception e){
			return 0;
		}
	}
	
	public static double fromE6ToDegrees(int n){
		try{
			return n  / 1e6;
		}catch(Exception e){
			return 0;
		}
	}
	
	public static double fromE6ToRadians(int n){
		try{
			return fromDegreesToRadians(n  / 1e6);
		}catch(Exception e){
			return 0;
		}
	}
	
	public static double fromRadiansToDegrees(double n) {
		try{
			return n / Math.PI * 180;
		}catch(Exception e){
			return 0;
		}
	}
	
	public static double fromRadiansToE6(double n) {
		return fromDegreesToE6(fromRadiansToDegrees(n));
	}
	
	public boolean isGpsProviderActive(){
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public boolean isNetworkProviderActive(){
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public boolean isAnyProvidersEnabled(){
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(gps == true || network == true){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isProvidersEnabled(){
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(gps == true && network == true){
			return true;
		}else{
			return false;
		}
	}
	
	public void settings(Context context){
		context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public boolean isCallBackEnabled() {
		return callBackEnabled;
	}

	public void setCallBackEnabled(boolean callBackEnabled) {
		this.callBackEnabled = callBackEnabled;
	}

	public long getMinTimeLocation() {
		return minTimeLocation;
	}

	public void setMinTimeLocation(long minTimeLocation) {
		this.minTimeLocation = minTimeLocation;
	}

	public float getMinDistanceLocation() {
		return minDistanceLocation;
	}

	public void setMinDistanceLocation(float minDistanceLocation) {
		this.minDistanceLocation = minDistanceLocation;
	}

	public boolean isCriteriaAltitude() {
		return criteriaAltitude;
	}

	/**
	 * Indicates whether the provider must provide altitude information. Not all fixes are guaranteed to 
 	 * contain such information.
	 * @param criteriaAltitude
	 */
	public void setCriteriaAltitude(boolean criteriaAltitude) {
		this.criteriaAltitude = criteriaAltitude;
	}

	public int getCriteriaAccuracy() {
		return criteriaAccuracy;
	}

	/**
	 * Indicates the desired accuracy for latitude and longitude. Accuracy may be ACCURACY_FINE if desired 
 	 * location is fine, else it can be ACCURACY_COARSE. More accurate location may consume more power 
 	 * and may take longer.
	 * @param criteriaAccuracy Criteria.ACCURACY_FINE if desired location is fine, else it can be Criteria.ACCURACY_COARSE (Default)
	 */
	public void setCriteriaAccuracy(int criteriaAccuracy) {
		this.criteriaAccuracy = criteriaAccuracy;
	}

	public boolean isCriteriaBearing() {
		return criteriaBearing;
	}

	/**
	 * Indicates whether the provider must provide bearing information. Not all fixes are guaranteed to 
 	 * contain such information.
	 * @param criteriaBearing
	 */
	public void setCriteriaBearing(boolean criteriaBearing) {
		this.criteriaBearing = criteriaBearing;
	}

	public boolean isCriteriaCost() {
		return criteriaCost;
	}

	/**
	 * Indicates whether the provider is allowed to incur monetary cost.
	 * @param criteriaCost
	 */
	public void setCriteriaCost(boolean criteriaCost) {
		this.criteriaCost = criteriaCost;
	}

	public int getCriteriaPower() {
		return criteriaPower;
	}
	
	/**
	 * Indicates the desired maximum power level. The level parameter must be one of NO_REQUIREMENT, 
 	 * POWER_LOW, POWER_MEDIUM, or POWER_HIGH.
	 * @param criteriaPower
	 */
	public void setCriteriaPower(int criteriaPower) {
		this.criteriaPower = criteriaPower;
	}

	public boolean isCriteriaSpeed() {
		return criteriaSpeed;
	}

	/**
	 * Indicates whether the provider must provide speed information. Not all fixes are guaranteed to contain 
 	 * such information.
	 * @param criteriaSpeed
	 */
	public void setCriteriaSpeed(boolean criteriaSpeed) {
		this.criteriaSpeed = criteriaSpeed;
	}
	
//	public static String getStringFormatGeoPoint(GeoPoint point){
//		double latitud = LocationsManager.fromE6ToDegrees(point.getLatitudeE6());
//		double longitud = LocationsManager.fromE6ToDegrees(point.getLongitudeE6());
//		
//		return String.format(ADDRESS_DEFAULT, latitud, longitud);
//	}
	
	public static String getStringFormatLocation(Location location){
		return String.format(ADDRESS_DEFAULT, location.getLatitude(), location.getLongitude());
	}
	
	public interface LocationsListener {
		
		/**
		 * Se notifica cuando empieza la solicitud de ubicación.
		 */
		public void onLocationRequestStart();
		
		/**
		 * Se llama cuando cambia la ubicación del usuario.
		 * @param location Localización actual.
		 */
		public void onLocationChange(Location location);
		
		/**
		 * Se llama cuando cambia de estado un proveedor de localización.
		 * @param provider GPS o NETWORK.
		 * @param status estado que ha producido la llamada: {@link android.location.LocationProvider#OUT_OF_SERVICE OUT_OF_SERVICE},  
		 * {@link android.location.LocationProvider#AVAILABLE AVAILABLE}, {@link android.location.LocationProvider#TEMPORARILY_UNAVAILABLE TEMPORARILY_UNAVAILABLE}
		 * @param extras conjunto clave-valor del número de satélites.
		 */
		public void onLocationStatusChange(int provider, int status, Bundle extras);
		
		/**
		 * Se llama cuando ha pasado el tiempo especificado como timeOut y no se ha recuperado una ubicación.
		 * @param lastLocation Última ubicación conocida del usuario.
		 */
		public void onLocationTimeOut(LastKnownLocation lastLocation);
		
		/**
		 * Se llama cuando se habilita un proveedor de ubicación.
		 * @param provider GPS o NETWORK.
		 */
		public void onProviderEnabled(String provider);
		
		/**
		 * Se llama cuando está deshabilitado un proveedor de ubicvación.
		 * @param provider provider GPS o NETWORK.
		 */
		public void onProviderDisabled(String provider);
		
		/**
		 * Se llama cuando está deshabilitado un proveedor de ubicvación.
		 * @param provider provider GPS o NETWORK.
		 */
		public void onAllProvidersDisabled();
	}
	
	static class LocationHandler extends Handler {
		private final WeakReference<LocationsManager> mRef;
		
		public LocationHandler(LocationsManager manager) {
			mRef = new WeakReference<LocationsManager>(manager);
	    }
		
		@Override
		public void handleMessage(Message message){
			LocationsManager manager =  mRef.get();
			
			if(manager == null || !manager.callBackEnabled) return;
			for(LocationsListener listener : manager.mListners) {
				
				if (listener != null) {
					
					switch (message.what) {
					
					case LocationsManager.LOCATION_CHANGED:
						listener.onLocationChange((Location) message.obj);
						removeMessages(LocationsManager.LOCATION_TIME_OUT);
						break;
					case LocationsManager.LOCATION_STATUS_CHANGED:
						listener.onLocationStatusChange(message.arg1, message.arg2, (Bundle) message.obj);
						break;
					case LocationsManager.LOCATION_PROVIDER_ENABLED:
						listener.onProviderEnabled((String) message.obj);
						break;
					case LocationsManager.LOCATION_PROVIDER_DISABLED:
						listener.onProviderDisabled((String) message.obj);
						break;
					case LocationsManager.LOCATION_TIME_OUT:
							listener.onLocationTimeOut(manager.getLastKnownLocation());
						break;
					case LocationsManager.LOCATION_REQUEST_START:
						listener.onLocationRequestStart();
						break;
					case LocationsManager.ALL_LOCATION_PROVIDERS_DISABLED:
						listener.onAllProvidersDisabled();
						break;
					default:
						break;
					}
				}
			}
		}
	}
}
