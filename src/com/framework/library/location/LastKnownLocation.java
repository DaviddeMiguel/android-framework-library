package com.framework.library.location;

import java.io.Serializable;

import android.location.Location;

import com.framework.library.model.IModel;

public class LastKnownLocation implements IModel, Serializable{

	private static final long serialVersionUID = 1L;
	
	private float accuracy;
	private double altitude;
	private float bearing;
	private double latitude;
	private double longitude;
	private String provider;
	private float speed;
	private long time;
	
	public LastKnownLocation(){}
	
	public LastKnownLocation(Location location){
		setAccuracy(location.getAccuracy());
		setAltitude(location.getAltitude());
		setBearing(location.getBearing());
		setLatitude(location.getLatitude());
		setLongitude(location.getLongitude());
		setProvider(location.getProvider());
		setSpeed(location.getSpeed());
		setTime(location.getTime());
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void setId(long id) {}
	
	public Location getLocation(){
		Location location = new Location("");

		location.setAccuracy(getAccuracy());
		location.setAltitude(getAltitude());
		location.setBearing(getBearing());
		location.setLatitude(getLatitude());
		location.setLongitude(getLongitude());
		location.setProvider(getProvider());
		location.setSpeed(getSpeed());
		location.setTime(getTime());
		
		return location;
	}
}
