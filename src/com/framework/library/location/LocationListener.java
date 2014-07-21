package com.framework.library.location;

import android.location.Location;
import android.os.Bundle;

public interface LocationListener {
	//location
	/**
	 * Este metodo es llamado cuando la localizacion ha cambiado (se recomienda parar el LocationsManager si ya no se necesita actualizar
	 * la posicion o hacerlo cuando corresponda.
	 */
	public void onLocationChanged(Location location);
	/**
	 * Este metodo avisa cuando algo ha cambiado en un provider, te devuelve el estado (fuera de servicio ...) y un extra que contiene
	 * datos detallados (numero de satelites por ejemplo).
	 */
	public void onLocationStatusChanged(int provider, int status, Bundle extras);
	/**
	 * Avisa cuando un provider ha sido habilitado especificando cual ha sido.
	 * @param provider Puede ser LocationsManager.GPS_PROVIDER o LocationsManager.NETWORK_PROVIDER
	 */
	public void onLocationProviderEnabled(String provider);
	/**
	 * Avisa cuando un provider ha sido deshabilitado especificando cual ha sido.
	 * @param provider
	 */
	public void onLocationProviderDisabled(String provider);
}
